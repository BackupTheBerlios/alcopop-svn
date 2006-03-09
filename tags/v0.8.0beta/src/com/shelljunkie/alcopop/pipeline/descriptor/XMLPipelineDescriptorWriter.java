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
package com.shelljunkie.alcopop.pipeline.descriptor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import com.shelljunkie.alcopop.logging.LoggerManager;

/**
 * @author Juergen Becker
 */
public class XMLPipelineDescriptorWriter {
	private static final String XML_HEADER = "<?xml version=\"1.0\"?>";
	private static final String XML_NAMESPACE = "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"../com.shelljunkie.pep/pep.xsd\"";

	private PrintWriter output;

	public XMLPipelineDescriptorWriter( File file ) {
		try {
			output = new PrintWriter( file );
		} catch ( FileNotFoundException excep ) {
			LoggerManager.getInstance().getDefaultLogger().error( "xml file not found", excep );
		}
	}

	public XMLPipelineDescriptorWriter( String filename ) {
		this( new File( filename ) );
	}

	public boolean write( PipelineDescriptor descriptor ) {
		writeXMLHeader();
		writePipelineStartTag( descriptor );
		writePipelineElementTags( descriptor.getSourceDescriptors() );
		writePipelineElementTags( descriptor.getFilterDescriptors() );
		writePipelineElementTags( descriptor.getSinkDescriptors() );
		writePipelinePipeElementTags( descriptor.getPipeDescriptors() );
		writePipelineEndTag( descriptor );
		output.close();
		return true;
	}

	protected void writeXMLHeader() {
		output.println( XML_HEADER );
	}

	protected void writePipelineElementTags( Iterable<PipelineElementDescriptor> descriptors ) {
		for ( PipelineElementDescriptor ped : descriptors ) {
			writePipelineElementStartTag( ped );
			for ( String name : ped.getConfigurationPropertyNames() ) {
				writePipelineElementPropertyTag( name, ped.getConfigurationPropertyValue( name ) );
			}
			writePipelineElementEndTag( ped );
		}
	}

	protected void writePipelineStartTag( PipelineDescriptor descriptor ) {
		output.println( "<" + XMLPipelineDescriptorReader.TAG_PIPELINE + " " + XMLPipelineDescriptorReader.ATTR_NAME + "=\"" + descriptor.getName() + "\" "
			+ XML_NAMESPACE + ">" );
	}

	protected void writePipelineEndTag( PipelineDescriptor descriptor ) {
		output.println( "</" + XMLPipelineDescriptorReader.TAG_PIPELINE + ">" );
	}

	protected void writePipelineElementStartTag( PipelineElementDescriptor descriptor ) {
		output.print( "\t<" + getTagName( descriptor ) );
		output.print( " " + XMLPipelineDescriptorReader.ATTR_ID + "=\"" + descriptor.getID() + "\"" );
		output.print( " " + XMLPipelineDescriptorReader.ATTR_NAME + "=\"" + descriptor.getName() + "\"" );
		output.println( " " + XMLPipelineDescriptorReader.ATTR_CLASSNAME + "=\"" + descriptor.getClassName() + "\">" );
	}

	protected void writePipelineElementEndTag( PipelineElementDescriptor descriptor ) {
		output.println( "\t</" + getTagName( descriptor ) + ">" );
	}

	protected void writePipelineElementPropertyTag( String name, String value ) {
		output.print( "\t\t<" + XMLPipelineDescriptorReader.TAG_PROPERTY );
		output.print( " " + XMLPipelineDescriptorReader.ATTR_NAME + "=\"" + name + "\"" );
		output.println( " " + XMLPipelineDescriptorReader.ATTR_VALUE + "=\"" + ( value != null ? value : "" ) + "\"/>" );
	}

	protected void writePipelinePipeElementTags( Iterable<PipelinePipeElementDescriptor> descriptors ) {
		for ( PipelinePipeElementDescriptor ped : descriptors ) {
			writePipelinePipeElementTag( ped );
		}
	}

	protected void writePipelinePipeElementTag( PipelinePipeElementDescriptor descriptor ) {
		output.print( "\t<" + XMLPipelineDescriptorReader.TAG_PIPE );
		output.print( " " + XMLPipelineDescriptorReader.ATTR_ID + "=\"" + descriptor.getID() + "\"" );
		output.print( " " + XMLPipelineDescriptorReader.ATTR_SOURCE + "=\"" + descriptor.getSource() + "\"" );
		output.print( " " + XMLPipelineDescriptorReader.ATTR_SINK + "=\"" + descriptor.getSink() + "\"" );
		if ( descriptor.getClassName() != null && descriptor.getClassName().length() != 0 ) {
			output.print( " " + XMLPipelineDescriptorReader.ATTR_CLASSNAME + "=\"" + descriptor.getClassName() + "\"" );
		}
		if ( descriptor.getPipeClassName() != null && descriptor.getPipeClassName().length() != 0 ) {
			output.print( " " + XMLPipelineDescriptorReader.ATTR_PIPECLASSNAME + "=\"" + descriptor.getPipeClassName() + "\"" );
		}
		output.println( "/>" );
	}

	protected String getTagName( PipelineElementDescriptor descriptor ) {
		if ( descriptor.isOfTypeSource() ) {
			return XMLPipelineDescriptorReader.TAG_SOURCE;
		} else if ( descriptor.isOfTypeFilter() ) {
			return XMLPipelineDescriptorReader.TAG_FILTER;
		} else if ( descriptor.isOfTypeSink() ) {
			return XMLPipelineDescriptorReader.TAG_SINK;
		} else if ( descriptor.getType() == PipelinePipeElementDescriptor.TYPE_PIPE ) {
			return XMLPipelineDescriptorReader.TAG_PIPE;
		}
		return null;
	}

}
