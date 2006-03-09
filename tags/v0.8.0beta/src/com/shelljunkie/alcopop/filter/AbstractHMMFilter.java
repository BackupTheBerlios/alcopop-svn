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
package com.shelljunkie.alcopop.filter;

import com.shelljunkie.alcopop.hmm.ForwardAlgorithm;
import com.shelljunkie.alcopop.hmm.HiddenMarkovModel;
import com.shelljunkie.alcopop.hmm.HiddenMarkovModelReader;
import com.shelljunkie.alcopop.logging.ILogger;
import com.shelljunkie.alcopop.logging.LoggerManager;
import com.shelljunkie.alcopop.pipeline.IFilter;
import com.shelljunkie.alcopop.pipeline.IPipelineElementConfiguration;
import com.shelljunkie.alcopop.pipeline.PipelineElementConfigurationException;

/**
 * @author Juergen Becker
 */
public abstract class AbstractHMMFilter implements IFilter {
	private static final String CFG_PROPERTY_HMM_FILENAME = "HMM filename";
	protected HiddenMarkovModel hmm;
	protected ForwardAlgorithm forwardAlgo;
	private boolean running;
	protected ILogger logger;

	public boolean init( IPipelineElementConfiguration configuration ) {
		logger = LoggerManager.getInstance().getLogger( getClass() );

		String hmmFilename = null;

		try {
			hmmFilename = configuration.getConfigurationPropertyChecked( CFG_PROPERTY_HMM_FILENAME );
		} catch ( PipelineElementConfigurationException excep ) {
			logger.error( excep.getMessage() );
		}

		HiddenMarkovModelReader reader = new HiddenMarkovModelReader();
		hmm = reader.read( hmmFilename );
		if ( hmm == null ) {
			logger.error( "unable to load hmm from file: " + hmmFilename );
			return false;
		}

		forwardAlgo = new ForwardAlgorithm();

		running = true;
		return true;
	}

	public boolean isRunning() {
		return running;
	}

	public void stop() {
		running = false;
	}

}
