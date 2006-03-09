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

import java.util.HashMap;
import java.util.Map;

import com.shelljunkie.alcopop.logging.ILogger;
import com.shelljunkie.alcopop.logging.LoggerManager;

/**
 * @author Juergen Becker
 */
public abstract class PipelineProcessingElement implements IPipelineProcessingElement, PipelineProcessingElementMBean {
	private String id;
	private String name;
	private IPipe inputPipe;
	private Map<String, IPipe> outputPipes;
	private ILogger logger;

	protected PipelineProcessingElement( boolean isProducer ) {
		if ( isProducer ) {
			outputPipes = new HashMap<String, IPipe>();
		}
	}

	public String getID() {
		return id;
	}

	public void setID( String id ) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public void stop() {
		clearPipe();
	}

	public IPipe getInputPipe() {
		return inputPipe;
	}

	public void setInputPipe( IPipe inputPipe ) {
		this.inputPipe = inputPipe;
	}

	public IPipe getOutputPipeByID( String id ) {
		return outputPipes.get( id );
	}

	public void addOutputPipe( IPipe pipe ) {
		outputPipes.put( pipe.getID(), pipe );
	}

	public void removeOutputPipe( IPipe pipe ) {
		outputPipes.remove( pipe.getID() );
	}

	public void writeToOutput( Object data ) {
		for ( IPipe output : outputPipes.values() ) {
			output.write( data );
		}
	}

	protected void clearPipe() {
		if ( inputPipe != null ) {
			inputPipe.clear();
		}
	}

	protected ILogger getLogger() {
		if ( logger == null ) {
			logger = LoggerManager.getInstance().getLogger( this.getClass() );
		}
		return logger;
	}
}
