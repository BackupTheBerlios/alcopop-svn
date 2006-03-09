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

import java.util.List;

import com.shelljunkie.alcopop.alert.AlertDB;
import com.shelljunkie.alcopop.alert.IAlert;
import com.shelljunkie.alcopop.hmm.ForwardAlgorithm;
import com.shelljunkie.alcopop.hmm.IntegerObservableSequence;
import com.shelljunkie.alcopop.logging.LoggerManager;
import com.shelljunkie.alcopop.pipeline.IPipelineElementConfiguration;

/**
 * @author Juergen Becker
 */
public class HMMAlerttypeCheckFilter extends AbstractHMMFilter {
	private static final String ALERT_PAYLOAD_PROBABILITY = "ALERT_PAYLOAD_PROBABILITY";
	private AlertDB alertDB;

	@Override
	public boolean init( IPipelineElementConfiguration configuration ) {
		if ( super.init( configuration ) ) {
			alertDB = AlertDB.getInstance();
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public Object filter( Object data ) {
		if ( data != null && data instanceof IAlert ) {
			IAlert alert = (IAlert) data;
			if ( alert.isMetaDataAvailable() ) {
				List<IAlert> alerts = (List<IAlert>) alert.getMetaData().getAttribute( AttackOne2OneFilter.META_DATA_ALERT_AGGREGATION );
				if ( alerts != null ) {
					IntegerObservableSequence sequence = new IntegerObservableSequence();
					appendtoSequence( sequence, alert );
					for ( IAlert subAlert : alerts ) {
						appendtoSequence( sequence, subAlert );
					}
					if ( sequence.size() > 2 ) {
						ForwardAlgorithm.Result result = forwardAlgo.calc( hmm, sequence );
						alert.getMetaData().addAttribute( ALERT_PAYLOAD_PROBABILITY, new Double( result.getProbability() ) );
					}
				}
			}
		}
		return data;
	}

	protected void appendtoSequence( IntegerObservableSequence currentSequence, IAlert alert ) {
		Integer catID = alertDB.getCategoryIDForAlert( alert.getName() );
		if ( catID != null ) {
			currentSequence.add( catID );
		} else {
			LoggerManager.getInstance().getLogger( getClass() ).error( "could not find category for alert: " + alert );
		}
	}

}
