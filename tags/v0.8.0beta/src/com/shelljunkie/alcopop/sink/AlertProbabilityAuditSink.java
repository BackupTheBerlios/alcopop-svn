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

import com.shelljunkie.alcopop.alert.IAlert;
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
public class AlertProbabilityAuditSink implements ISink {
	private static final String CFG_PROPERTY_METADATA_FIELDNAME = "Metadata fieldname";
	private static final String CFG_PROPERTY_THRESHOLD = "Threshold";
	private static final String CFG_PROPERTY_ABOVE = "Above";

	private String fieldname;
	private double threshold;
	private boolean above;
	private boolean running;
	private ILogger logger;

	public boolean init( IPipelineElementConfiguration configuration ) {
		logger = LoggerManager.getInstance().getLogger( getClass() );

		try {
			fieldname = configuration.getConfigurationPropertyChecked( CFG_PROPERTY_METADATA_FIELDNAME );
			threshold = configuration.getDoubleConfigurationPropertyChecked( CFG_PROPERTY_THRESHOLD );
			above = configuration.getBooleanConfigurationPropertyChecked( CFG_PROPERTY_ABOVE );
		} catch ( PipelineElementConfigurationException excep ) {
			logger.error( "missing configuration parameter: " + CFG_PROPERTY_ABOVE );
			return false;
		}

		running = true;
		return true;
	}

	public void consume( Object data ) {
		if ( data == null || !( data instanceof IAlert ) ) {
			return;
		}
		IAlert alert = (IAlert) data;
		if ( alert.isMetaDataAvailable() ) {
			Object metadata = alert.getMetaData().getAttribute( fieldname );
			if ( metadata != null && metadata instanceof Double ) {
				double value = ( (Double) metadata ).doubleValue();
				if ( ( above && value >= threshold ) || ( !above && value <= threshold ) ) {
					logger.warning( "Suspicious: " + alert );
				}
			}
		}
	}

	public boolean isRunning() {
		return running;
	}

	public void stop() {
		running = false;
	}

}
