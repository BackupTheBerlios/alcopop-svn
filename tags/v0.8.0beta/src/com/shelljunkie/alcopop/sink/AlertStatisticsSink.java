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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.shelljunkie.alcopop.alert.IAlert;
import com.shelljunkie.alcopop.logging.ILogger;
import com.shelljunkie.alcopop.logging.LoggerManager;
import com.shelljunkie.alcopop.pipeline.IPipelineElementConfiguration;
import com.shelljunkie.alcopop.pipeline.ISink;

/**
 * collects some statitics for the alerts
 * 
 * @author Juergen Becker
 */
public class AlertStatisticsSink implements ISink {
	private Map<String, Counter> attacks;
	private Map<String, Counter> ports;
	private Map<String, Counter> networks;
	private Map<String, Counter> sourceHosts;
	private Map<String, Counter> targetHosts;

	private int alertCounter;
	private int reportAlertCounter;
	private int reportInterval;
	private ILogger logger;
	private boolean running;

	public boolean init( IPipelineElementConfiguration configuration ) {
		attacks = new HashMap<String, Counter>();
		ports = new HashMap<String, Counter>();
		networks = new HashMap<String, Counter>();
		sourceHosts = new HashMap<String, Counter>();
		targetHosts = new HashMap<String, Counter>();
		alertCounter = 0;
		reportAlertCounter = 0;
		reportInterval = 2500;
		logger = LoggerManager.getInstance().getLogger( getClass() );
		running = true;
		return true;
	}

	public void consume( Object data ) {
		IAlert alert = (IAlert) data;
		if ( alert != null ) {
			updateAttacks( alert );
			updatePorts( alert );
			updateNetworks( alert );
			updateSourceHosts( alert );
			updateTargetHosts( alert );
			++alertCounter;
			++reportAlertCounter;
			if ( reportAlertCounter > reportInterval ) {
				reportAlertCounter = 0;
				printStatistic();
			}
		}
	}

	public boolean isRunning() {
		return running;
	}

	public void stop() {
		running = false;
	}

	@SuppressWarnings("unchecked")
	protected void printStatistic() {
		List<Counter> col = new ArrayList<Counter>( attacks.values() );
		Collections.sort( col );
		printSingleStatistic( "Attack types", col, 0 );

		col = new ArrayList<Counter>( ports.values() );
		Collections.sort( col, new StringNumberCamparator() );
		printSingleStatistic( "Attacked ports", col, 1 );

		col = new ArrayList<Counter>( networks.values() );
		Collections.sort( col );
		printSingleStatistic( "Source networks", col, 2 );

		col = new ArrayList<Counter>( sourceHosts.values() );
		Collections.sort( col );
		printSingleStatistic( "Source hosts", col, 1 );

		logger.info( ":" );
		col = new ArrayList<Counter>( targetHosts.values() );
		Collections.sort( col );
		printSingleStatistic( "Target hosts", col, 1 );

	}

	protected void printSingleStatistic( String headline, List<Counter> data, int treshold ) {
		logger.info( "*** " + headline + ":" );
		for ( Counter cnt : data ) {
			if ( cnt.getValue() > treshold ) {
				logger.info( cnt.toString() );
			}
		}
		logger.info( "********************" );
	}

	protected void updateSourceHosts( IAlert alert ) {
		Counter counter = sourceHosts.get( alert.getSourceIP() );
		if ( counter == null ) {
			counter = new Counter( alert.getSourceIP() );
			sourceHosts.put( counter.getName(), counter );
		}
		counter.inc();
	}

	protected void updateTargetHosts( IAlert alert ) {
		Counter counter = targetHosts.get( alert.getDestinationIP() );
		if ( counter == null ) {
			counter = new Counter( alert.getDestinationIP() );
			targetHosts.put( counter.getName(), counter );
		}
		counter.inc();
	}

	protected void updateNetworks( IAlert alert ) {
		String sourceNetwork = getSourceNet( alert.getSourceIP() );
		Counter counter = networks.get( sourceNetwork );
		if ( counter == null ) {
			counter = new Counter( sourceNetwork );
			networks.put( counter.getName(), counter );
		}
		counter.inc();
	}

	protected String getSourceNet( String sourceIP ) {
		return sourceIP.substring( 0, sourceIP.lastIndexOf( "." ) ) + ".xxx";
	}

	protected void updatePorts( IAlert alert ) {
		String port = String.valueOf( alert.getDestinationPort() );
		Counter counter = ports.get( port );
		if ( counter == null ) {
			counter = new Counter( port );
			ports.put( counter.getName(), counter );
		}
		counter.inc();

	}

	protected void updateAttacks( IAlert alert ) {
		Counter counter = attacks.get( alert.getName() );
		if ( counter == null ) {
			counter = new Counter( alert.getName() );
			attacks.put( counter.getName(), counter );
		}
		counter.inc();
	}

	private static final class StringNumberCamparator implements Comparator<Counter> {

		public int compare( Counter o1, Counter o2 ) {
			return Integer.parseInt( o1.getName() ) - Integer.parseInt( o2.getName() );
		}
	}

	private static final class Counter implements Comparable {
		private String name;
		private int value;

		Counter( String name ) {
			this.name = name;
		}

		public int inc() {
			return ++value;
		}

		public int dec() {
			return --value;
		}

		public int getValue() {
			return value;
		}

		public String getName() {
			return name;
		}

		public int compareTo( Object o ) {
			return ( (Counter) o ).value - value;
		}

		@Override
		public String toString() {
			return name + ": " + value;
		}
	}

}
