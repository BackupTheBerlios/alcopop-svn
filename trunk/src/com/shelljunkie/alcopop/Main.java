/**
 Copyright (c) 2005,2006 Juergen Becker
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 1. Redistributions of source code must retain the above copyright notice,
 this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright 
 notice, this list of conditions and the following disclaimer in
 the documentation and/or other materials provided with the distribution.

 3. The names of the authors may not be used to endorse or promote products
 derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JCRAFT,
 INC. OR ANY CONTRIBUTORS TO THIS SOFTWARE BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.shelljunkie.alcopop;

import com.shelljunkie.alcopop.jmx.RemoteManagementUtility;
import com.shelljunkie.alcopop.pipeline.Pipeline;
import com.shelljunkie.alcopop.pipeline.PipelinedProcessingCore;
import com.shelljunkie.alcopop.pipeline.descriptor.IPipelineDescriptorProvider;
import com.shelljunkie.alcopop.pipeline.descriptor.PipelineBuilder;
import com.shelljunkie.alcopop.pipeline.descriptor.XMLPipelineDescriptorReader;
import com.shelljunkie.alcopop.ui.console.ConsoleMainWindow;
import com.shelljunkie.alcopop.ui.editor.EditorMainWindow;

/**
 * @author Juergen Becker
 */
public class Main {

	public static void usage() {
		System.out.println( Configuration.NAME_SHORT + " - " + Configuration.NAME_LONG + " " + Configuration.VERSION + " " + Configuration.COPYRIGHT );
		System.out.println( "usage: java -jar " + Configuration.NAME_SHORT + ".jar <options> <launch options> <xml pipeline descriptor>" );
		System.out.println( "options:" );
		System.out.println( "\t-j|J\t enable jmx support" );
		System.out.println( "\t-l|L\t enable logging to console ( in addition to the file logs)" );
		System.out.println( "launch options:" );
		System.out.println( "\t-p|P\t start the pipeline processing (default)" );
		System.out.println( "\t-e|E\t start the editor" );
		System.out.println( "\t-c|C\t start the console" );
	}

	private static CommandlineOptions scanParameters( String args[] ) {
		if ( args.length < 1 ) {
			usage();
			System.exit( 0 );
		}
		CommandlineOptions opts = new CommandlineOptions();
		for ( String arg : args ) {
			if ( arg.startsWith( "-" ) ) {
				if ( arg.equalsIgnoreCase( "-J" ) ) {
					opts.setJmxEnabled( true );
				} else if ( arg.equalsIgnoreCase( "-E" ) ) {
					opts.setStartEditor( true );
				} else if ( arg.equalsIgnoreCase( "-C" ) ) {
					opts.setStartConsole( true );
				} else if ( arg.equalsIgnoreCase( "-L" ) ) {
					opts.setConsoleLoggingEnabled( true );
				}
			} else {
				if ( opts.getXmlPipelineDescriptor() == null ) {
					opts.setXmlPipelineDescriptor( arg );
				}
			}
		}
		return opts;
	}

	public static void main( String[] args ) {
		CommandlineOptions opts = scanParameters( args );
		Configuration.getInstance().setJmxEnabled( opts.isJmxEnabled() );
		Configuration.getInstance().setConsoleLoggingEnabled( opts.isConsoleLoggingEnabled() );
		if ( opts.isStartEditor() ) {
			EditorMainWindow win = new EditorMainWindow();
			win.show();
		} else if ( opts.isStartConsole() ) {
			ConsoleMainWindow win = new ConsoleMainWindow();
			win.show();
		} else {
			if ( opts.getXmlPipelineDescriptor() == null ) {
				usage();
				System.exit( 0 );
			}
			RemoteManagementUtility jcm = RemoteManagementUtility.getInstance();
			IPipelineDescriptorProvider pdp = new XMLPipelineDescriptorReader( opts.getXmlPipelineDescriptor() );
			Pipeline pipeline = new PipelineBuilder().build( pdp );
			PipelinedProcessingCore core = new PipelinedProcessingCore( pipeline, pdp.getDescriptor() );
			if ( opts.isJmxEnabled() ) {
				jcm.register( core );
			}
			core.start();
		}
	}

	private static final class CommandlineOptions {
		private boolean jmxEnabled = true;
		private boolean startEditor = false;
		private boolean startConsole = false;
		private boolean consoleLoggingEnabled = false;

		private String xmlPipelineDescriptor;

		public boolean isJmxEnabled() {
			return jmxEnabled;
		}

		public void setJmxEnabled( boolean jmxEnabled ) {
			this.jmxEnabled = jmxEnabled;
		}

		public boolean isStartEditor() {
			return startEditor;
		}

		public void setStartEditor( boolean startConfigurator ) {
			startEditor = startConfigurator;
		}

		public void setXmlPipelineDescriptor( String filename ) {
			xmlPipelineDescriptor = filename;
		}

		public String getXmlPipelineDescriptor() {
			return xmlPipelineDescriptor;
		}

		public boolean isStartConsole() {
			return startConsole;
		}

		public void setStartConsole( boolean startRunner ) {
			startConsole = startRunner;
		}

		public boolean isConsoleLoggingEnabled() {
			return consoleLoggingEnabled;
		}

		public void setConsoleLoggingEnabled( boolean consoleLogging ) {
			this.consoleLoggingEnabled = consoleLogging;
		}
	}

}
