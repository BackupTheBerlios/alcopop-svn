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

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.shelljunkie.alcopop.logging.LoggerManager;

/**
 * @author Juergen Becker
 */
public class XMLPipelineDescriptorReader implements IPipelineDescriptorProvider {
	protected static final String ATTR_NAME = "name";
	protected static final String ATTR_ID = "id";
	protected static final String ATTR_CLASSNAME = "classname";
	protected static final String ATTR_PIPECLASSNAME = "pipeclassname";
	protected static final String ATTR_VALUE = "value";
	protected static final String ATTR_SOURCE = "source";
	protected static final String ATTR_SINK = "sink";
	protected static final String TAG_PIPELINE = "pipeline";
	protected static final String TAG_SOURCE = "source";
	protected static final String TAG_FILTER = "filter";
	protected static final String TAG_SINK = "sink";
	protected static final String TAG_PIPE = "pipe";
	protected static final String TAG_PROPERTY = "property";

	private File file;
	private PipelineDescriptor pipelineDescriptor;

	public XMLPipelineDescriptorReader( String filename ) {
		this( new File( filename ) );
	}

	public XMLPipelineDescriptorReader( File file ) {
		if ( !file.exists() ) {
			throw new IllegalArgumentException( "file does not exist: " + file.getName() );
		}
		this.file = file;
	}

	public PipelineDescriptor getDescriptor() {
		if ( pipelineDescriptor == null ) {
			try {
				Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse( file );
				Node rootNode = doc.getElementsByTagName( TAG_PIPELINE ).item( 0 );
				pipelineDescriptor = new PipelineDescriptor( rootNode.getAttributes().getNamedItem( ATTR_NAME ).getNodeValue() );
				NodeList elements = rootNode.getChildNodes();
				for ( int i = 0; i < elements.getLength(); i++ ) {
					Node element = elements.item( i );
					if ( element.getNodeType() != Node.TEXT_NODE ) {
						PipelineElementDescriptor ped = createPipelineElementDescriptor( element );
						if ( ped != null ) {
							if ( ped instanceof PipelinePipeElementDescriptor ) {
								pipelineDescriptor.add( (PipelinePipeElementDescriptor) ped );
							} else {
								pipelineDescriptor.add( ped );
							}
						}
					}
				}
				pipelineDescriptor.setFilename( file.getPath() );
			} catch ( Exception excep ) {
				LoggerManager.getInstance().getDefaultLogger().error( "failed to load xml pipeline descriptor", excep );
			}

		}
		return pipelineDescriptor;
	}

	protected PipelineElementDescriptor createPipelineElementDescriptor( Node element ) {
		String elemType = element.getNodeName();
		NamedNodeMap attributes = element.getAttributes();
		PipelineElementDescriptor ped = null;
		if ( element.getNodeType() != Node.COMMENT_NODE ) {
			if ( TAG_PIPE.equals( elemType ) ) {
				ped = new PipelinePipeElementDescriptor();
				( (PipelinePipeElementDescriptor) ped ).setSource( attributes.getNamedItem( ATTR_SOURCE ).getNodeValue() );
				( (PipelinePipeElementDescriptor) ped ).setSink( attributes.getNamedItem( ATTR_SINK ).getNodeValue() );
				Node clsName = attributes.getNamedItem( ATTR_CLASSNAME );
				if ( clsName != null ) {
					ped.setClassName( clsName.getNodeValue() );
				}
				clsName = attributes.getNamedItem( ATTR_PIPECLASSNAME );
				if ( clsName != null ) {
					ped.setPipeClassName( clsName.getNodeValue() );
				}
			} else {
				ped = new PipelineElementDescriptor();
				ped.setName( attributes.getNamedItem( ATTR_NAME ).getNodeValue() );
				ped.setClassName( attributes.getNamedItem( ATTR_CLASSNAME ).getNodeValue() );
				processElementConfigurationProperties( ped, element );
				if ( TAG_SOURCE.equals( elemType ) ) {
					ped.setType( PipelineElementDescriptor.TYPE_SOURCE );
				} else if ( TAG_FILTER.equals( elemType ) ) {
					ped.setType( PipelineElementDescriptor.TYPE_FILTER );
				} else if ( TAG_SINK.equals( elemType ) ) {
					ped.setType( PipelineElementDescriptor.TYPE_SINK );
				}
			}
			ped.setID( attributes.getNamedItem( ATTR_ID ).getNodeValue() );
			return ped;
		}
		return null;
	}

	protected void processElementConfigurationProperties( PipelineElementDescriptor ped, Node element ) {
		NodeList properties = element.getChildNodes();
		for ( int i = 0; i < properties.getLength(); i++ ) {
			Node prop = properties.item( i );
			if ( TAG_PROPERTY.equals( prop.getNodeName() ) ) {
				NamedNodeMap attributes = prop.getAttributes();
				ped.setConfigurationPropertyValue( attributes.getNamedItem( ATTR_NAME ).getNodeValue(), attributes.getNamedItem( ATTR_VALUE ).getNodeValue() );
			}
		}
	}
}
