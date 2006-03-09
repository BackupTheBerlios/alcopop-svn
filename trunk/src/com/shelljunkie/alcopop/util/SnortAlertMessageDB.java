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
package com.shelljunkie.alcopop.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Juergen Becker
 */
public class SnortAlertMessageDB {
	private static final String SNORT_MESSAGES_FILENAME = "/tmp/snort-2.3.2/etc/sid-msg.map";

	/**
	 * @param args
	 */
	public static void main( String[] args ) {
		Set<String> alertGroups = new HashSet<String>();

		BufferedReader fi = null;
		try {
			fi = new BufferedReader( new FileReader( SNORT_MESSAGES_FILENAME ) );
		} catch ( FileNotFoundException excep ) {
			excep.printStackTrace( System.err );
			System.exit( 0 );
		}
		while ( true ) {
			String line;
			try {
				line = fi.readLine();
				if ( line == null ) {
					System.err.println( "end" );
					break;
				}
				if ( !line.startsWith( "#" ) && line.length() != 0 ) {

					String[] parts = getMessage( line ).split( "[^A-Z-\\d]+", 2 );
					if ( parts.length > 0 ) {
						if ( parts[0].length() == 1 && parts[0].equals( "V" ) ) {
							parts[0] = "VIRUS";
						}
						if ( !alertGroups.contains( parts[0] ) ) {
							alertGroups.add( parts[0] );
							System.out.println( "db.put(\"" + parts[0] + "\",new Integer (" + ( alertGroups.size() - 1 ) + "));" );
						}
					} else {
						System.err.println( "bad line: " + line + ", msg: " + getMessage( line ) );
					}
				}
			} catch ( IOException excep ) {
				excep.printStackTrace( System.err );
			}

		}

	}

	protected static String getMessage( String line ) {
		String[] parts = line.split( "\\|\\|" );
		return parts[1].trim();
	}
}
