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

import java.util.ArrayList;
import java.util.List;

import com.shelljunkie.alcopop.alert.IAlert;
import com.shelljunkie.alcopop.pipeline.IFilter;
import com.shelljunkie.alcopop.pipeline.IPipelineElementConfiguration;

/**
 * @author Juergen Becker
 */
public abstract class BaseAlertFusionFilter implements IFilter, BaseAlertFusionFilterMBean {
	private static final String CFG_PROPERTY_WINDOW_SIZE = "Window Size";
	private static final int DEFAULT_WINDOW_SIZE = 120000; // 120 seconds

	private int windowSize;
	private BufferWithTimestamp<IAlert> alertBuffer;
	private long alertCount;
	private long alertsMerged;
	private boolean running;

	public boolean init( IPipelineElementConfiguration configuration ) {
		windowSize = configuration.getIntConfigurationProperty( CFG_PROPERTY_WINDOW_SIZE, DEFAULT_WINDOW_SIZE );
		alertBuffer = new BufferWithTimestamp<IAlert>();
		alertCount = 0;
		alertsMerged = 0;
		running = true;
		return true;
	}

	public Object filter( Object data ) {
		updateBuffer( (IAlert) data );
		mergeAlerts();
		return getOveragedAlert();
	}

	public boolean isRunning() {
		return running;
	}

	public void stop() {
		running = false;
	}

	public long getAlertCount() {
		return alertCount;
	}

	protected void incAlertCount() {
		++alertCount;
	}

	public long getAlertsMerged() {
		return alertsMerged;
	}

	protected void incAlertsMerged() {
		++alertsMerged;
	}

	public double getAlertReduction() {
		return 100.0 * alertsMerged / alertCount;
	}

	protected void updateBuffer( IAlert alert ) {
		if ( alert != null ) {
			alertBuffer.add( alert );
			incAlertCount();
		}
	}

	@SuppressWarnings("unchecked")
	protected void mergeAlerts() {
		if ( alertBuffer.size() > 1 ) {
			for ( int i = 0; i < alertBuffer.size(); i++ ) {
				IAlert masterAlert = alertBuffer.get( i );
				for ( int j = i + 1; j < alertBuffer.size(); ) {
					IAlert testAlert = alertBuffer.get( j );
					if ( areAlertsSimilar( masterAlert, testAlert ) ) {
						List<IAlert> similarAlerts = (List<IAlert>) masterAlert.getMetaData().getAttribute( getFusedAlertMetaDataAttributeName() );
						if ( similarAlerts == null ) {
							similarAlerts = new ArrayList<IAlert>();
							masterAlert.getMetaData().addAttribute( getFusedAlertMetaDataAttributeName(), similarAlerts );
						}
						similarAlerts.add( testAlert );
						alertBuffer.remove( j );
						incAlertsMerged();
					} else {
						++j;
					}
				}
			}
		}
	}

	protected abstract String getFusedAlertMetaDataAttributeName();

	protected abstract boolean areAlertsSimilar( IAlert alertOne, IAlert alertTwo );

	protected IAlert getOveragedAlert() {
		if ( alertBuffer.size() > 1 ) {
			IAlert first = alertBuffer.get( 0 );
			IAlert last = alertBuffer.get( alertBuffer.size() - 1 );
			if ( Math.abs( last.getTime() - first.getTime() ) >= windowSize ) {
				alertBuffer.remove( 0 );
				return first;
			}
		}
		if ( alertBuffer.size() > 0 ) {
			if ( System.currentTimeMillis() - alertBuffer.getTimestamp( 0 ) > windowSize ) {
				return alertBuffer.remove( 0 );
			}
		}
		return null;
	}

	private final class BufferWithTimestamp<T> {
		private List<T> buffer;
		private List<Long> timestamps;

		BufferWithTimestamp() {
			buffer = new ArrayList<T>();
			timestamps = new ArrayList<Long>();
		}

		public void add( T elem ) {
			buffer.add( elem );
			timestamps.add( new Long( System.currentTimeMillis() ) );
		}

		public T remove( int index ) {
			timestamps.remove( index );
			return buffer.remove( index );
		}

		public T get( int index ) {
			return buffer.get( index );
		}

		public long getTimestamp( int index ) {
			return timestamps.get( index ).longValue();
		}

		public int size() {
			return buffer.size();
		}
	}
}
