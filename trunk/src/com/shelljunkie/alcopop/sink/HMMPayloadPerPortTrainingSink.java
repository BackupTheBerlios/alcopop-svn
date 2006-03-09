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
package com.shelljunkie.alcopop.sink;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.shelljunkie.alcopop.alert.IAlert;
import com.shelljunkie.alcopop.hmm.BaumWelchAlgorithm;
import com.shelljunkie.alcopop.hmm.ByteArrayObservableSequence;
import com.shelljunkie.alcopop.hmm.HiddenMarkovModel;
import com.shelljunkie.alcopop.hmm.HiddenMarkovModelWriter;
import com.shelljunkie.alcopop.hmm.IObservableSequence;
import com.shelljunkie.alcopop.logging.ILogger;
import com.shelljunkie.alcopop.logging.LoggerManager;
import com.shelljunkie.alcopop.pipeline.IPipelineElementConfiguration;
import com.shelljunkie.alcopop.pipeline.ISink;
import com.shelljunkie.alcopop.pipeline.PipelineElementConfigurationException;

/**
 * Base class for Hidden Markov Model Trainig
 * 
 * @author Juergen Becker
 */
public class HMMPayloadPerPortTrainingSink implements ISink {
	private static final int DEFAULT_NO_OF_TRAINING_SEQUENCES = 100;
	private static final int DEFAULT_NO_OF_ITERATIONS = 15;
	private static final String CFG_PROPERTY_HMM_FILENAME = "HMM base filename";
	private static final String CFG_PROPERTY_NO_OF_TRAINING_SEQUENCES = "No of training sequences";
	private static final String CFG_PROPERTY_NO_OF_ITERATIONS = "No of iterations";
	private static final String CFG_PROPERTY_PORTS = "Ports";
	private BaumWelchAlgorithm bwAlgo;
	private String hmmBaseFilename;
	private Map<Integer, List<IObservableSequence>> observableSequnces;
	private int noOfSequences;
	private int noOfIterations;
	private boolean running;
	private ILogger logger;
	private int packetCount;

	public boolean init( IPipelineElementConfiguration configuration ) {
		logger = LoggerManager.getInstance().getLogger( getClass() );

		String portNumbers = null;

		try {
			hmmBaseFilename = configuration.getConfigurationPropertyChecked( CFG_PROPERTY_HMM_FILENAME );
			portNumbers = configuration.getConfigurationPropertyChecked( CFG_PROPERTY_PORTS );
		} catch ( PipelineElementConfigurationException excep ) {
			logger.error( excep.getMessage() );
			return false;
		}

		noOfSequences = configuration.getIntConfigurationProperty( CFG_PROPERTY_NO_OF_TRAINING_SEQUENCES, DEFAULT_NO_OF_TRAINING_SEQUENCES );
		noOfIterations = configuration.getIntConfigurationProperty( CFG_PROPERTY_NO_OF_ITERATIONS, DEFAULT_NO_OF_ITERATIONS );

		observableSequnces = new HashMap<Integer, List<IObservableSequence>>();
		for ( String port : portNumbers.split( "," ) ) {
			Integer portNr = new Integer( port );
			observableSequnces.put( portNr, new ArrayList<IObservableSequence>() );
		}

		logger.info( "training sequence size: " + noOfSequences );
		bwAlgo = new BaumWelchAlgorithm();
		bwAlgo.setScale( true );

		running = true;
		return true;
	}

	public void consume( Object data ) {
		if ( data == null || !( data instanceof IAlert ) ) {
			return;
		}

		IAlert alert = (IAlert) data;
		byte[] payload = ( alert.getPayload() );
		if ( payload.length > 2 ) {
			Integer port = new Integer( alert.getDestinationPort() );
			List<IObservableSequence> seqs = observableSequnces.get( port );
			if ( seqs != null ) {
				packetCount++;
				if ( packetCount % 100 == 0 ) {
					System.out.println( "packets in: " + packetCount );
				}

				seqs.add( new ByteArrayObservableSequence( payload ) );
				if ( seqs.size() >= noOfSequences ) {
					save( train( seqs, port ), port );
					observableSequnces.remove( port );
				}
				// } else {
				// System.out.println( "port: " + port );
			}
		} else {
			// System.out.println( "payload to short" );
		}
		running = !observableSequnces.isEmpty();
	}

	public boolean isRunning() {
		return running;
	}

	public void stop() {
		if ( isRunning() ) {
			running = false;
			/*
			 * for ( Integer port : observableSequnces.keySet() ) { List<IObservableSequence> seqs = observableSequnces.get( port ); if ( !seqs.isEmpty() ) {
			 * save( train( seqs, port ), port ); } }
			 */observableSequnces.clear();
			logger.info( "stopped" );
		}
	}

	protected HiddenMarkovModel train( List<IObservableSequence> obsSequnces, Integer port ) {
		long startTime = System.currentTimeMillis();
		HiddenMarkovModel hmm = createMarkovModel();
		logger.info( "starting hmm training for port: " + port );
		hmm = bwAlgo.estimate( hmm, obsSequnces, noOfIterations );
		logger.info( "finished hmm training in " + ( ( System.currentTimeMillis() - startTime ) / 1000 ) + "sec" );
		return hmm;
	}

	protected void save( HiddenMarkovModel hmm, Integer port ) {
		HiddenMarkovModelWriter writer = new HiddenMarkovModelWriter();
		if ( writer.write( hmm, hmmBaseFilename + "-" + port + ".hmm" ) ) {
			logger.info( "hmm for port " + port + " saved" );
		} else {
			logger.error( "hmm saving failed" );
		}
	}

	protected HiddenMarkovModel createMarkovModel() {
		return new HiddenMarkovModel( 128, 256, true );
	}
}
