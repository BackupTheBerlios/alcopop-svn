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

import java.util.HashMap;
import java.util.Map;

import com.shelljunkie.alcopop.alert.IAlert;
import com.shelljunkie.alcopop.hmm.ByteArrayObservableSequence;
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
public class HMMPayloadPerPortCheckFilter implements IFilter {
	private static final String ALERT_PAYLOAD_PROBABILITY = "ALERT_PAYLOAD_PROBABILITY";
	private ByteArrayObservableSequence observableSequnece;
	private static final String CFG_PROPERTY_HMM_FILENAME = "HMM base filename";
	private static final String CFG_PROPERTY_PORTS = "Ports";
	protected Map<Integer, HiddenMarkovModel> hmms;
	protected ForwardAlgorithm forwardAlgo;
	private boolean running;
	protected ILogger logger;

	public boolean init( IPipelineElementConfiguration configuration ) {
		logger = LoggerManager.getInstance().getLogger( getClass() );

		String hmmBaseFilename = null;
		String portNumbers = null;
		try {
			hmmBaseFilename = configuration.getConfigurationPropertyChecked( CFG_PROPERTY_HMM_FILENAME );
			portNumbers = configuration.getConfigurationPropertyChecked( CFG_PROPERTY_PORTS );
		} catch ( PipelineElementConfigurationException e ) {
			logger.error( e.getMessage() );
			return false;
		}

		hmms = new HashMap<Integer, HiddenMarkovModel>();
		for ( String port : portNumbers.split( "," ) ) {
			Integer portNr = new Integer( port );
			HiddenMarkovModelReader reader = new HiddenMarkovModelReader();
			String filename = hmmBaseFilename + "-" + port + ".hmm";
			HiddenMarkovModel hmm = reader.read( filename );
			if ( hmm != null ) {
				hmms.put( portNr, hmm );
			} else {
				logger.error( "unable to load hmm for port " + port + " from file: " + filename );
			}
		}

		if ( hmms.isEmpty() ) {
			logger.error( "no hmms loaded." );
			return false;
		}

		forwardAlgo = new ForwardAlgorithm();

		observableSequnece = new ByteArrayObservableSequence();

		running = true;
		return true;
	}

	public Object filter( Object data ) {
		if ( data != null && data instanceof IAlert ) {
			IAlert alert = (IAlert) data;
			if ( alert.getPayload().length > 2 ) {
				observableSequnece.setSequence( alert.getPayload() );
				HiddenMarkovModel hmm = hmms.get( new Integer( alert.getDestinationPort() ) );
				if ( hmm != null ) {
					ForwardAlgorithm.Result result = forwardAlgo.calc( hmm, observableSequnece );
					alert.getMetaData().addAttribute( ALERT_PAYLOAD_PROBABILITY, new Double( result.getProbability() ) );
				}
			}
		}
		return data;
	}

	public boolean isRunning() {
		return running;
	}

	public void stop() {
		running = false;
	}

}
