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
package com.shelljunkie.alcopop.pipeline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.shelljunkie.alcopop.logging.LoggerManager;

/**
 * @author Juergen Becker
 */
public class Pipeline {
	private static final String DEFAULT_NAME = "no name";
	private String name;
	private Map<Integer, IPipelineElement> processingElements;
	private List<IPipelineProcessingElement> sources;
	private List<IPipelineProcessingElement> filters;
	private List<IPipelineProcessingElement> sinks;
	private List<IPipe> pipes;

	public Pipeline( String name ) {
		this.name = name;
		processingElements = new HashMap<Integer, IPipelineElement>();
		sources = new ArrayList<IPipelineProcessingElement>();
		filters = new ArrayList<IPipelineProcessingElement>();
		sinks = new ArrayList<IPipelineProcessingElement>();
		pipes = new ArrayList<IPipe>();
	}

	public Pipeline() {
		this( DEFAULT_NAME );
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public IPipelineElement getElementByID( int id ) {
		Integer pid = new Integer( id );
		if ( processingElements.containsKey( pid ) ) {
			return processingElements.get( pid );
		}
		return null;
	}

	public void add( IPipelineProcessingElement elem ) {
		addPipelineElement( elem );
		if ( elem.isConsumer() ) {
			if ( elem.isProducer() ) {
				filters.add( elem );
			} else {
				sinks.add( elem );
			}
		} else if ( elem.isProducer() ) {
			sources.add( elem );
		} else {
			LoggerManager.getInstance().getDefaultLogger().error( "bad pipeline element: " + elem.getClass() + ". it is not a producer nor consumer." );
		}
	}

	public void remove( IPipelineProcessingElement elem ) {
		removePipelineElement( elem );
		if ( elem.isConsumer() ) {
			if ( elem.isProducer() ) {
				filters.remove( elem );
			} else {
				sinks.remove( elem );
			}
		} else if ( elem.isProducer() ) {
			sources.remove( elem );
		} else {
			LoggerManager.getInstance().getDefaultLogger().error( "bad pipeline element: " + elem.getClass() + ". it is not a producer nor consumer." );
		}
	}

	public Iterable<IPipelineProcessingElement> getSources() {
		return sources;
	}

	public List<IPipelineProcessingElement> getFilerts() {
		return filters;
	}

	public List<IPipelineProcessingElement> getSinks() {
		return sinks;
	}

	public void add( IPipe pipe ) {
		removePipelineElement( pipe );
		pipes.add( pipe );
	}

	public void remove( IPipe pipe ) {
		removePipelineElement( pipe );
		pipes.remove( pipe );
	}

	public List<IPipe> getPipes() {
		return pipes;
	}

	protected void addPipelineElement( IPipelineElement elem ) {
		processingElements.put( new Integer( elem.getID() ), elem );
	}

	protected void removePipelineElement( IPipelineElement elem ) {
		processingElements.remove( elem );
	}

}
