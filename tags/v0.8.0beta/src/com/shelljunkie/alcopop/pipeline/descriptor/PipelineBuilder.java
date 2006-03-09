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

import com.shelljunkie.alcopop.logging.LoggerManager;
import com.shelljunkie.alcopop.pipe.AsynchronousBoundedPipe;
import com.shelljunkie.alcopop.pipeline.IFilter;
import com.shelljunkie.alcopop.pipeline.IPipe;
import com.shelljunkie.alcopop.pipeline.IPipelineProcessingElement;
import com.shelljunkie.alcopop.pipeline.ISink;
import com.shelljunkie.alcopop.pipeline.ISource;
import com.shelljunkie.alcopop.pipeline.Pipeline;
import com.shelljunkie.alcopop.pipeline.internal.DefaultPipelineElementConfiguration;
import com.shelljunkie.alcopop.pipeline.internal.FilterWrapper;
import com.shelljunkie.alcopop.pipeline.internal.SinkWrapper;
import com.shelljunkie.alcopop.pipeline.internal.SourceWrapper;

/**
 * takes a pipeline descriptor and builds the pipeline from the information
 * 
 * @author Juergen Becker
 */
public class PipelineBuilder {

	public Pipeline build( IPipelineDescriptorProvider provider ) {
		return build( provider.getDescriptor() );
	}

	public Pipeline build( PipelineDescriptor pcd ) {
		Pipeline pipeline = new Pipeline( pcd.getName() );
		createPipelineElements( pipeline, pcd );
		createPipelinePipeElements( pipeline, pcd );
		return pipeline;
	}

	protected void createPipelineElements( Pipeline pipeline, PipelineDescriptor pcd ) {
		for ( PipelineElementDescriptor ped : pcd.getDescriptors() ) {
			IPipelineProcessingElement pelem = createPipelineProcessingElement( ped.getClassName() );
			if ( pelem != null ) {
				configurePipelineElement( pelem, ped );
				pipeline.add( pelem );
				if ( pelem.isConsumer() ) {
					pelem.setInputPipe( createPipelinePipeElement( ped ) );
				}
			}
		}
	}

	protected void createPipelinePipeElements( Pipeline pipeline, PipelineDescriptor pcd ) {
		for ( PipelinePipeElementDescriptor pipeDescriptor : pcd.getPipeDescriptors() ) {
			IPipelineProcessingElement source = (IPipelineProcessingElement) pipeline.getElementByID( Integer.parseInt( pipeDescriptor.getSource() ) );
			IPipelineProcessingElement sink = (IPipelineProcessingElement) pipeline.getElementByID( Integer.parseInt( pipeDescriptor.getSink() ) );
			source.addOutputPipe( sink.getInputPipe() );
		}
	}

	protected void configurePipelineElement( IPipelineProcessingElement pelem, PipelineElementDescriptor ped ) {
		pelem.setID( ped.getID() );
		pelem.setName( ped.getName() );
		if ( !pelem.init( new DefaultPipelineElementConfiguration( ped.getConfigurationProperties() ) ) ) {
			LoggerManager.getInstance().getDefaultLogger().error( "Error configuring pipeline element: " + ped.getName() + " id #" + ped.getID() );
			System.exit( 1 );
		}
	}

	protected IPipe createPipelinePipeElement( PipelineElementDescriptor ped ) {
		IPipe pelem = null;
		if ( ped.getPipeClassName() != null ) {
			pelem = (IPipe) createNewInstance( ped.getPipeClassName() );
		}
		if ( pelem == null ) {
			pelem = createDefaultPipe();
		}
		pelem.setID( ped.getID() );
		pelem.init( new DefaultPipelineElementConfiguration( ped.getConfigurationProperties() ) );
		return pelem;
	}

	protected IPipelineProcessingElement createPipelineProcessingElement( String className ) {
		Object ppe = createNewInstance( className );
		if ( ppe == null ) {
			return null;
		}
		if ( ppe instanceof IFilter ) {
			return new FilterWrapper( (IFilter) ppe );
		} else if ( ppe instanceof ISource ) {
			return new SourceWrapper( (ISource) ppe );
		} else if ( ppe instanceof ISink ) {
			return new SinkWrapper( (ISink) ppe );
		} else {
			LoggerManager.getInstance().getDefaultLogger().error(
				"Bad pipeline processing element: " + ppe.getClass() + ".Must implement ISource, IFilter or ISink." );
		}
		return null;
	}

	protected Object createNewInstance( String classname ) {
		try {
			return Class.forName( classname ).newInstance();
		} catch ( Exception excep ) {
			LoggerManager.getInstance().getDefaultLogger().error( "unable to create instance of " + classname, excep );
		}
		return null;
	}

	protected IPipe createDefaultPipe() {
		return new AsynchronousBoundedPipe();
	}
}
