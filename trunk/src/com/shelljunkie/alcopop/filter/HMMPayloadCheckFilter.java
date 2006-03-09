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

import com.shelljunkie.alcopop.alert.IAlert;
import com.shelljunkie.alcopop.hmm.ByteArrayObservableSequence;
import com.shelljunkie.alcopop.hmm.ForwardAlgorithm;
import com.shelljunkie.alcopop.pipeline.IPipelineElementConfiguration;

/**
 * @author Juergen Becker
 */
public class HMMPayloadCheckFilter extends AbstractHMMFilter {
	private static final String ALERT_PAYLOAD_PROBABILITY = "ALERT_PAYLOAD_PROBABILITY";
	private ByteArrayObservableSequence observableSequnece;

	@Override
	public boolean init( IPipelineElementConfiguration configuration ) {
		observableSequnece = new ByteArrayObservableSequence();
		return super.init( configuration );
	}

	public Object filter( Object data ) {
		if ( data != null && data instanceof IAlert ) {
			IAlert alert = (IAlert) data;
			if ( alert.getPayload().length > 0 ) {
				observableSequnece.setSequence( alert.getPayload() );
				ForwardAlgorithm.Result result = forwardAlgo.calc( hmm, observableSequnece );
				alert.getMetaData().addAttribute( ALERT_PAYLOAD_PROBABILITY, new Double( result.getProbability() ) );
			}
		}
		return data;
	}

}
