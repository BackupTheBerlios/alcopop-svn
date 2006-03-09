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
package com.shelljunkie.alcopop.logging;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.shelljunkie.alcopop.Configuration;

/**
 * Simple Logger
 * 
 * @author Juergen Becker
 */
class DefaultLogger implements ILogger {
	private Logger logger;

	/**
	 * named logger
	 * 
	 * @param name
	 */
	DefaultLogger( String name ) {
		logger = Logger.getLogger( name );
		try {
			for ( Handler handler : logger.getHandlers() ) {
				logger.removeHandler( handler );
			}
			Formatter formatter = new MyFormatter();
			FileHandler fh = new FileHandler( name + ".%u.log", true );
			fh.setFormatter( formatter );
			logger.addHandler( fh );
			if ( Configuration.getInstance().isConsoleLoggingEnabled() ) {
				ConsoleHandler ch = new ConsoleHandler();
				ch.setFormatter( formatter );
				logger.addHandler( ch );
			}
			logger.setUseParentHandlers( false );
		} catch ( SecurityException excep ) {
			excep.printStackTrace( System.err );
		} catch ( IOException excep ) {
			excep.printStackTrace( System.err );
		}
	}

	public void info( String msg ) {
		logger.log( Level.INFO, msg );
	}

	public void info( String msg, Object param ) {
		logger.log( Level.INFO, msg, param );
	}

	public void info( String msg, Object param1, Object param2 ) {
		logger.log( Level.INFO, msg, new Object[] { param1, param2 } );
	}

	public void info( String msg, Object[] params ) {
		logger.log( Level.INFO, msg, params );
	}

	public void info( String msg, Throwable throwable ) {
		logger.log( Level.INFO, msg, throwable );
	}

	public void warning( String msg ) {
		logger.log( Level.WARNING, msg );
	}

	public void warning( String msg, Object param ) {
		logger.log( Level.WARNING, msg, param );
	}

	public void warning( String msg, Object param1, Object param2 ) {
		logger.log( Level.WARNING, msg, new Object[] { param1, param2 } );
	}

	public void warning( String msg, Object[] params ) {
		logger.log( Level.WARNING, msg, params );
	}

	public void warning( String msg, Throwable throwable ) {
		logger.log( Level.WARNING, msg, throwable );
	}

	public void error( String msg ) {
		logger.log( Level.SEVERE, msg );
	}

	public void error( String msg, Object param ) {
		logger.log( Level.SEVERE, msg, param );
	}

	public void error( String msg, Object param1, Object param2 ) {
		logger.log( Level.SEVERE, msg, new Object[] { param1, param2 } );
	}

	public void error( String msg, Object[] params ) {
		logger.log( Level.SEVERE, msg, params );
	}

	public void error( String msg, Throwable throwable ) {
		logger.log( Level.SEVERE, msg, throwable );
	}

	private final class MyFormatter extends Formatter {
		@Override
		public String format( LogRecord record ) {
			String trace;
			if ( record.getThrown() != null ) {
				Throwable tr = record.getThrown();
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter( sw );
				tr.printStackTrace( pw );
				trace = "\n" + sw.getBuffer().toString();
			} else {
				trace = "";
			}
			return record.getMillis() + " " + record.getLoggerName() + " " + record.getLevel().getName() + ": " + record.getMessage() + trace + "\n";
		}
	}

}
