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
package com.shelljunkie.alcopop.source;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.shelljunkie.alcopop.alert.Alert;
import com.shelljunkie.alcopop.alert.IAlert;
import com.shelljunkie.alcopop.logging.ILogger;
import com.shelljunkie.alcopop.logging.LoggerManager;
import com.shelljunkie.alcopop.pipeline.IPipelineElementConfiguration;
import com.shelljunkie.alcopop.pipeline.ISource;
import com.shelljunkie.alcopop.pipeline.PipelineElementConfigurationException;
import com.voytechs.jnetstream.codec.Decoder;
import com.voytechs.jnetstream.codec.Header;
import com.voytechs.jnetstream.codec.Packet;
import com.voytechs.jnetstream.io.PacketInputStream;
import com.voytechs.jnetstream.io.RawformatInputStream;
import com.voytechs.jnetstream.io.StreamFormatException;
import com.voytechs.jnetstream.npl.SyntaxError;
import com.voytechs.jnetstream.primitive.address.Address;
import com.voytechs.jnetstream.primitive.address.IpAddress;

/**
 * @author Juergen Becker
 */
public class PCAPDumpSource implements ISource {
	private static final String PCAPDUMP_FILE = "PCAPDump file";
	private static final String CFG_PROPERTY_PORTS = "Ports";
	private Set<Integer> ports;
	private Decoder decoder;
	private boolean running;
	private ILogger logger;
	private int packetCount;

	public boolean init( IPipelineElementConfiguration configuration ) {
		logger = LoggerManager.getInstance().getLogger( getClass() );

		String fileName = null;
		String portNumbers = null;

		try {
			fileName = configuration.getConfigurationPropertyChecked( PCAPDUMP_FILE );
		} catch ( PipelineElementConfigurationException excep ) {
			logger.error( excep.getMessage() );
			return false;
		}

		portNumbers = configuration.getConfigurationProperty( CFG_PROPERTY_PORTS );

		if ( portNumbers != null && portNumbers.length() != 0 ) {
			ports = new HashSet<Integer>();
			for ( String port : portNumbers.split( "," ) ) {
				ports.add( new Integer( port ) );
			}
		}

		try {
			PacketInputStream input = new RawformatInputStream( fileName );
			decoder = new Decoder( input );
		} catch ( Exception e ) {
			e.printStackTrace();
			return false;
		}
		packetCount = 0;
		running = true;
		return true;
	}

	public Object produce() {
		Packet packet = null;
		try {
			packet = decoder.nextPacket();
		} catch ( StreamFormatException excep ) {
			excep.printStackTrace();
		} catch ( SyntaxError excep ) {
			excep.printStackTrace();
		} catch ( IOException excep ) {
			excep.printStackTrace();
		}

		if ( packet == null ) {
			stop();
			return null;
		}
		Header header = packet.getHeader( "IPv4" );
		if ( header != null ) {
			packetCount++;
			if ( packetCount % 1000 == 0 ) {
				System.out.println( "packets out: " + packetCount );
			}
			String protocol;
			Integer port;
			short proto = ( (Short) header.getValue( "proto" ) ).shortValue();
			if ( proto == 6 ) {
				protocol = IAlert.PROTOCOL_TCP;
				port = (Integer) packet.getValue( "TCP", "dport" );
			} else if ( proto == 17 ) {
				protocol = IAlert.PROTOCOL_UDP;
				port = (Integer) packet.getValue( "UDP", "dport" );
			} else {
				return null;
			}
			if ( !isPortDesired( port ) ) {
				return null;
			}

			Header dataHeader = packet.getHeader( "Data" );
			if ( dataHeader == null ) {
				return null;
			}
			Address payload = (Address) dataHeader.getValue( "data" );
			if ( payload == null ) {
				return null;
			}

			Alert alert = new Alert();
			alert.setProtocol( protocol );
			alert.setDestinationPort( port.intValue() );
			alert.setName( "RAW" );
			alert.setSourceIP( ( (IpAddress) header.getValue( "saddr" ) ).stringValue() );
			alert.setDestinationIP( ( (IpAddress) header.getValue( "daddr" ) ).stringValue() );
			alert.setPayload( payload.byteArrayValue() );
			alert.setTime( ( (Date) packet.getValue( Packet.CAPTURE_TIMESTAMP ) ).getTime() );
			return alert;
		}

		return null;
	}

	protected boolean isPortDesired( Integer port ) {
		if ( ports == null ) {
			return true;
		}
		return ports.contains( port );
	}

	public void stop() {
		running = false;
		logger.info( "stopped" );
	}

	public boolean isRunning() {
		return running;
	}
}
