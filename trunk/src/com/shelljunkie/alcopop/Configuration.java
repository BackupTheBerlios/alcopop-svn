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
package com.shelljunkie.alcopop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.Vector;

/**
 * Central configuration utility class.<br> <<singleton>>
 * 
 * @author Juergen Becker
 */
public final class Configuration {
	public final static String VERSION = "v0.8.0";
	public final static String NAME_SHORT = "alcopop";
	public final static String NAME_LONG = "alert correlation and processing pipeline";
	public final static String COPYRIGHT = "(c) Juergen Becker 2005,2006";

	public final static String CONNECTION_LIST_FILE = ".alcopop-connections";
	public final static String CONNECTION_PROPERTY_NAME = "alcopop.connection.";

	private static volatile Configuration instance;

	private boolean jmxEnabled = false;
	private boolean consoleLoggingEnabled = false;
	private Vector<String> connnections;

	private Configuration() {
		connnections = loadConnections();
	}

	public static Configuration getInstance() {
		if ( instance == null ) {
			synchronized ( Configuration.class ) {
				if ( instance == null ) {
					instance = new Configuration();
				}
			}
		}
		return instance;
	}

	public boolean isJmxEnabled() {
		return jmxEnabled;
	}

	public void setJmxEnabled( boolean jmxEnabled ) {
		this.jmxEnabled = jmxEnabled;
	}

	public boolean isConsoleLoggingEnabled() {
		return consoleLoggingEnabled;
	}

	public void setConsoleLoggingEnabled( boolean consoleLoggingEnabled ) {
		this.consoleLoggingEnabled = consoleLoggingEnabled;
	}

	public void addConnection( String c ) {
		connnections.add( c );
		saveConnections();
	}

	public void removeConnection( String c ) {
		connnections.remove( c );
		saveConnections();
	}

	public Vector<String> getConnections() {
		return connnections;
	}

	protected Vector<String> loadConnections() {
		String homedirName = System.getProperty( "user.home" );
		Properties props = new Properties();
		Vector<String> cons = new Vector<String>();
		try {
			props.load( new FileInputStream( homedirName + File.separator + CONNECTION_LIST_FILE ) );
			int count = 1;
			String connection;
			do {
				connection = props.getProperty( CONNECTION_PROPERTY_NAME + String.valueOf( count ) );
				if ( connection != null ) {
					cons.add( connection );
					++count;
				}
			} while ( connection != null );
		} catch ( FileNotFoundException fnfex ) {
			// ok
		} catch ( Exception excep ) {
			System.err.println( "could not load saved connections: " + excep.getMessage() );
			cons.add( "localhost:8004" );
		}
		return cons;
	}

	protected void saveConnections() {
		String homedirName = System.getProperty( "user.home" );
		Properties props = new Properties();
		try {
			int count = 1;
			for ( String con : connnections ) {
				props.setProperty( CONNECTION_PROPERTY_NAME + String.valueOf( count ), con );
				++count;
			}
			props.store( new FileOutputStream( homedirName + File.separator + CONNECTION_LIST_FILE ), null );
		} catch ( Exception excep ) {
			System.err.println( "could not save connections: " + excep.getMessage() );
		}
	}

}
