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
package com.shelljunkie.alcopop.alert;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Juergen Becker
 */
public class Alert implements IAlert {
	protected static final long BASE_TIME_2036 = 2085978496000L;
	protected static final long BASE_TIME_1900 = -2208988800000L;
	private long ID;
	private long analyzerID;
	private long time;
	private String name;
	private String sourceIP;
	private String destinationIP;
	private int destinationPort;
	private String protocol;
	private byte[] payload;
	private AlertMetaData metaData;
	private final static SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );

	public long getID() {
		return ID;
	}

	public void setID( long id ) {
		ID = id;
	}

	public long getAnalyzerID() {
		return analyzerID;
	}

	public void setAnalyzerID( long analyzerID ) {
		this.analyzerID = analyzerID;
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public long getTime() {
		return time;
	}

	public void setTime( long startTime ) {
		this.time = startTime;
	}

	public void setStartTimeFromNTPHexString( String hexNTP ) throws NumberFormatException {
		if ( hexNTP == null ) {
			throw new NumberFormatException( "the hex ntp timestamp must nor be null" );
		}
		int ind = hexNTP.indexOf( '.' );
		if ( ind == -1 ) {
			if ( hexNTP.length() == 0 ) {
				time = 0;
				return;
			}
			time = Long.parseLong( hexNTP, 16 ) << 32;
			return;
		}

		long ntpTime = Long.parseLong( removeHexPrefix( hexNTP.substring( 0, ind ) ), 16 ) << 32
			| Long.parseLong( removeHexPrefix( hexNTP.substring( ind + 1 ) ), 16 );

		long seconds = ( ntpTime >>> 32 ) & 0xffffffffL;
		long fraction = ntpTime & 0xffffffffL;

		fraction = Math.round( 1000D * fraction / 0x100000000L );

		long msb = seconds & 0x80000000L;
		if ( msb == 0 ) {
			time = BASE_TIME_2036 + ( seconds * 1000 ) + fraction;
		} else {
			time = BASE_TIME_1900 + ( seconds * 1000 ) + fraction;
		}

	}

	protected String removeHexPrefix( String hex ) {
		if ( hex.startsWith( "0x" ) ) {
			return hex.substring( 2 );
		}
		return hex;
	}

	public String getSourceIP() {
		return sourceIP;
	}

	public void setSourceIP( String sourceIP ) {
		this.sourceIP = sourceIP;
	}

	public String getDestinationIP() {
		return destinationIP;
	}

	public void setDestinationIP( String targetIP ) {
		this.destinationIP = targetIP;
	}

	public int getDestinationPort() {
		return destinationPort;
	}

	public void setDestinationPort( int targetPort ) {
		this.destinationPort = targetPort;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol( String protocol ) {
		if ( PROTOCOL_TCP.equals( protocol ) || PROTOCOL_UDP.equals( protocol ) ) {
			this.protocol = protocol;
		} else {
			throw new RuntimeException( "Bad protocol: " + protocol );
		}
	}

	public byte[] getPayload() {
		return payload;
	}

	public void setPayload( byte[] payload ) {
		this.payload = payload;
	}

	public boolean isMetaDataAvailable() {
		return metaData != null;
	}

	public AlertMetaData getMetaData() {
		if ( metaData == null ) {
			metaData = new AlertMetaData();
		}
		return metaData;
	}

	@Override
	public String toString() {
		return "Alert[" + ID + "," + dateFormat.format( new Date( time ) ) + "," + sourceIP + "," + destinationIP + "," + protocol + "," + destinationPort
			+ "," + name + ( isMetaDataAvailable() ? ",MetaData: " + metaData : "" ) + "]";
	}

}
