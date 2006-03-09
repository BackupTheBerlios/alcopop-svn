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

import java.util.HashSet;
import java.util.Set;

import com.shelljunkie.alcopop.alert.IAlert;
import com.shelljunkie.alcopop.logging.ILogger;
import com.shelljunkie.alcopop.logging.LoggerManager;
import com.shelljunkie.alcopop.pipeline.IPipelineElementConfiguration;
import com.shelljunkie.alcopop.pipeline.ISink;

/**
 * checks the stream of alerts and logs all unknown alerts
 * 
 * @author Juergen Becker
 */
public class AlertTypeFinderSink implements ISink {
	private Set<String> alertStates;
	private int stateCounter = 0;
	private ILogger logger;
	private boolean running;

	public boolean init( IPipelineElementConfiguration configuration ) {
		alertStates = new HashSet<String>();
		logger = LoggerManager.getInstance().getLogger( getClass() );
		running = true;
		return true;
	}

	public void consume( Object data ) {
		IAlert alert = (IAlert) data;
		if ( alert != null && !alertStates.contains( alert.getName() ) ) {
			Integer state = new Integer( ++stateCounter );
			alertStates.add( alert.getName() );
			logger.info( "Alert: {0} \tName: {1}", state, alert.getName() );
		}
	}

	public boolean isRunning() {
		return running;
	}

	public void stop() {
		running = false;
	}

}
