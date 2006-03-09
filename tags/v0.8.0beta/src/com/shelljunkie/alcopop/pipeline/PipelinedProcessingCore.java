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
import java.util.List;

import com.shelljunkie.alcopop.logging.LoggerManager;
import com.shelljunkie.alcopop.pipeline.descriptor.PipelineDescriptor;

/**
 * @author Juergen Becker
 */
public class PipelinedProcessingCore implements PipelinedProcessingCoreMBean {
	public static final String SIMBUS_ID = "pep.processing_core";
	private List<AsyncPipelineElementExecutor> executors;
	private Pipeline pipeline;
	private PipelineDescriptor pipelineDescriptor;
	private Thread shutdownHook;

	public PipelinedProcessingCore( Pipeline pipeline, PipelineDescriptor pipelineDescr ) {
		executors = new ArrayList<AsyncPipelineElementExecutor>();
		this.pipeline = pipeline;
		this.pipelineDescriptor = pipelineDescr;
	}

	public Pipeline getPipeline() {
		return pipeline;
	}

	public PipelineDescriptor getPipelineDescriptor() {
		return pipelineDescriptor;
	}

	public void start() {
		shutdownHook = new Thread( new ShutdownHook() );
		Runtime.getRuntime().addShutdownHook( shutdownHook );
		startActivePipelineElements( pipeline.getSinks() );
		startActivePipelineElements( pipeline.getFilerts() );
		startActivePipelineElements( pipeline.getSources() );
	}

	public void stop() {
		Runtime.getRuntime().removeShutdownHook( shutdownHook );
		shutdownHook = null;
		for ( AsyncPipelineElementExecutor exec : executors ) {
			exec.stop();
		}
		executors.clear();
	}

	protected void startActivePipelineElements( Iterable<? extends IPipelineProcessingElement> elements ) {
		for ( IPipelineProcessingElement elem : elements ) {
			AsyncPipelineElementExecutor exec = new AsyncPipelineElementExecutor( new PipelineElementExecutor( elem ), elem.getName() );
			executors.add( exec );
			exec.start();
		}
	}

	private static final class AsyncPipelineElementExecutor {
		private static final long THREAD_STOP_TIMEOUT = 100;

		private Thread runnerThread;
		private PipelineElementExecutor runner;
		private String name;

		public AsyncPipelineElementExecutor( PipelineElementExecutor exec, String name ) {
			this.runner = exec;
			this.name = name;
		}

		public void start() {
			runnerThread = new Thread( runner, "PeP Runner " + name );
			runnerThread.setPriority( Thread.NORM_PRIORITY );
			runnerThread.setDaemon( false );
			runnerThread.start();
		}

		public void stop() {
			runner.stop();
			if ( runnerThread != null ) {
				try {
					runnerThread.join( THREAD_STOP_TIMEOUT );
				} catch ( InterruptedException excep ) {
					LoggerManager.getInstance().getDefaultLogger().error( "stopping of " + name + " failed", excep );
				}
				runnerThread.interrupt();
				runnerThread = null;
			}
		}

	}

	private static final class PipelineElementExecutor implements Runnable {
		private static final long PIPE_READ_TIMEOUT = 500; // milliseconds

		IPipelineProcessingElement pelem;
		boolean isCosumer;
		boolean isProducer;

		PipelineElementExecutor( IPipelineProcessingElement pelem ) {
			this.pelem = pelem;
		}

		public void run() {
			while ( pelem.isRunning() ) {
				try {
					Object input = null;
					if ( pelem.isConsumer() ) {
						input = pelem.getInputPipe().read( PIPE_READ_TIMEOUT );
					}
					Object ouput = pelem.process( input );
					if ( ouput != null && pelem.isProducer() ) {
						pelem.writeToOutput( ouput );
					}
				} catch ( Exception e ) {
					LoggerManager.getInstance().getDefaultLogger().error( "error during execution of " + pelem.getName(), e );
				}
			}
		}

		public void stop() {
			pelem.stop();
		}

	}

	private final class ShutdownHook implements Runnable {
		public void run() {
			for ( AsyncPipelineElementExecutor exec : executors ) {
				exec.stop();
			}
			executors.clear();
		}
	}

}
