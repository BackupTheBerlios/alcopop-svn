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
import java.util.List;

import com.shelljunkie.alcopop.alert.IAlert;
import com.shelljunkie.alcopop.hmm.BaumWelchAlgorithm;
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
public abstract class AbstractHMMTrainingSink implements ISink {
	private static final int DEFAULT_NO_OF_TRAINING_SEQUENCES = 100;
	private static final int DEFAULT_NO_OF_ITERATIONS = 15;
	private static final String CFG_PROPERTY_HMM_FILENAME = "HMM filename";
	private static final String CFG_PROPERTY_NO_OF_TRAINING_SEQUENCES = "No of training sequences";
	private static final String CFG_PROPERTY_NO_OF_ITERATIONS = "No of iterations";
	protected HiddenMarkovModel hmm;
	private BaumWelchAlgorithm bwAlgo;
	private String hmmFilename;
	private List<IObservableSequence> observableSequnces;
	private int noOfSequences;
	private int noOfIterations;
	private boolean running;
	private ILogger logger;

	protected abstract HiddenMarkovModel createMarkovModel();

	protected abstract void process( IAlert alert );

	public boolean init( IPipelineElementConfiguration configuration ) {
		logger = LoggerManager.getInstance().getLogger( getClass() );

		try {
			hmmFilename = configuration.getConfigurationPropertyChecked( CFG_PROPERTY_HMM_FILENAME );
		} catch ( PipelineElementConfigurationException excep ) {
			logger.error( excep.getMessage() );
		}

		noOfSequences = configuration.getIntConfigurationProperty( CFG_PROPERTY_NO_OF_TRAINING_SEQUENCES, DEFAULT_NO_OF_TRAINING_SEQUENCES );
		logger.info( "training sequence size: " + noOfSequences );

		noOfIterations = configuration.getIntConfigurationProperty( CFG_PROPERTY_NO_OF_ITERATIONS, DEFAULT_NO_OF_ITERATIONS );

		bwAlgo = new BaumWelchAlgorithm();
		bwAlgo.setScale( true );
		hmm = createMarkovModel();
		observableSequnces = new ArrayList<IObservableSequence>();
		running = true;
		return true;
	}

	public void consume( Object data ) {
		if ( noOfSequences < 0 ) {
			return;
		}
		if ( data != null || data instanceof IAlert ) {
			process( (IAlert) data );
		}
		if ( noOfSequences == 0 ) {
			train();
			observableSequnces.clear();
			--noOfSequences;
			stop();
		}
	}

	public boolean isRunning() {
		return running;
	}

	protected void train() {
		long startTime = System.currentTimeMillis();

		logger.info( "starting hmm training" );
		hmm = bwAlgo.estimate( hmm, observableSequnces, noOfIterations );
		logger.info( "finished hmm training in " + ( ( System.currentTimeMillis() - startTime ) / 1000 ) + "sec" );
	}

	protected void addObservableSequnce( IObservableSequence os ) {
		// logger.info( "added seq [" + noOfSequences + "]: " + os.size() );
		--noOfSequences;
		observableSequnces.add( os );
	}

	public void stop() {
		if ( isRunning() ) {
			running = false;
			writeHMM();
			logger.info( "stopped" );
		}
	}

	protected void writeHMM() {
		HiddenMarkovModelWriter writer = new HiddenMarkovModelWriter();
		if ( writer.write( hmm, hmmFilename ) ) {
			logger.info( "hmm saved" );
		} else {
			logger.error( "hmm saving failed" );
		}
	}
}
