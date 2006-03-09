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

import java.util.List;

import com.shelljunkie.alcopop.alert.AlertDB;
import com.shelljunkie.alcopop.alert.IAlert;
import com.shelljunkie.alcopop.filter.AttackOne2OneFilter;
import com.shelljunkie.alcopop.hmm.HiddenMarkovModel;
import com.shelljunkie.alcopop.hmm.IntegerObservableSequence;
import com.shelljunkie.alcopop.logging.LoggerManager;
import com.shelljunkie.alcopop.pipeline.IPipelineElementConfiguration;

/**
 * Hidden Markov Model Trainig
 * 
 * @author Juergen Becker
 */
/**
 * @author Juergen Becker
 */
public class HMMAlertTypeTrainingSink extends AbstractHMMTrainingSink {
	private static final int NO_OF_HMM_HIDDENSTATES = 16;
	private AlertDB alertDB;

	@Override
	public boolean init( IPipelineElementConfiguration configuration ) {
		if ( super.init( configuration ) ) {
			alertDB = AlertDB.getInstance();
			return true;
		}
		return false;
	}

	@Override
	protected void process( IAlert alert ) {
		IntegerObservableSequence sequence = new IntegerObservableSequence();
		processMetaData( sequence, alert );
		if ( sequence.size() > 2 ) {
			addObservableSequnce( sequence );
		}
	}

	@SuppressWarnings("unchecked")
	protected void processMetaData( IntegerObservableSequence sequence, IAlert alert ) {
		if ( alert.isMetaDataAvailable() ) {
			List<IAlert> alerts = (List<IAlert>) alert.getMetaData().getAttribute( AttackOne2OneFilter.META_DATA_ALERT_AGGREGATION );
			if ( alerts != null ) {
				appendtoSequence( sequence, alert );
				for ( IAlert subAlert : alerts ) {
					appendtoSequence( sequence, subAlert );
					process( subAlert );
				}
			}
		}
	}

	protected void appendtoSequence( IntegerObservableSequence currentSequence, IAlert alert ) {
		Integer catID = alertDB.getCategoryIDForAlert( alert.getName() );
		if ( catID != null ) {
			currentSequence.add( catID );
		} else {
			LoggerManager.getInstance().getLogger( getClass() ).error( "could not find category for alert: " + alert );
		}
	}

	@Override
	protected HiddenMarkovModel createMarkovModel() {
		return new HiddenMarkovModel( NO_OF_HMM_HIDDENSTATES, AlertDB.getInstance().getNoOfCategories(), true );
	}

}
