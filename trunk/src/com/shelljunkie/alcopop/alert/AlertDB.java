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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Juergen Becker
 */
public class AlertDB {
	private static volatile AlertDB instance;
	private Map<String, Integer> db;

	private AlertDB() {
		db = new HashMap<String, Integer>();
		buildDB();
	}

	public static AlertDB getInstance() {
		if ( instance == null ) {
			synchronized ( AlertDB.class ) {
				if ( instance == null ) {
					instance = new AlertDB();
				}
			}
		}
		return instance;
	}

	public int getNoOfCategories() {
		return db.size();
	}

	public String getCategoryName( int id ) {
		Integer idObj = new Integer( id );
		for ( String key : db.keySet() ) {
			Integer i = db.get( key );
			if ( i.equals( idObj ) ) {
				return key;
			}
		}
		return null;
	}

	public Integer getCategoryIDForAlert( String alert ) {
		String prefix = getPrefix( alert );
		if ( prefix != null ) {
			Integer id = db.get( prefix );
			if ( id != null ) {
				return id;
			}
		}
		return null;
	}

	protected String getPrefix( String alert ) {
		int index = alert.indexOf( ' ' );
		if ( index == -1 ) {
			return alert;
		}
		return alert.substring( 0, index );
	}

	/*
	 * String[] parts = alert.split( "[^A-Z-\\d]+", 2 ); if ( parts.length > 0 ) { if ( parts[0].length() == 1 && parts[0].equals( "V" ) ) { return "VIRUS"; }
	 * return parts[0]; } return null; }
	 */

	protected void buildDB() {
		db.put( "BACKDOOR", new Integer( 0 ) );
		db.put( "FTP", new Integer( 1 ) );
		db.put( "DDOS", new Integer( 2 ) );
		db.put( "DNS", new Integer( 3 ) );
		db.put( "DOS", new Integer( 4 ) );
		db.put( "EXPLOIT", new Integer( 5 ) );
		db.put( "POP2", new Integer( 6 ) );
		db.put( "POP3", new Integer( 7 ) );
		db.put( "NNTP", new Integer( 8 ) );
		db.put( "IMAP", new Integer( 9 ) );
		db.put( "FINGER", new Integer( 10 ) );
		db.put( "ICMP", new Integer( 11 ) );
		db.put( "INFO", new Integer( 12 ) );
		db.put( "ATTACK-RESPONSES", new Integer( 13 ) );
		db.put( "ATTACK", new Integer( 14 ) );
		db.put( "MISC", new Integer( 15 ) );
		db.put( "WEB-MISC", new Integer( 16 ) );
		db.put( "POLICY", new Integer( 17 ) );
		db.put( "TFTP", new Integer( 18 ) );
		db.put( "BAD-TRAFFIC", new Integer( 19 ) );
		db.put( "NETBIOS", new Integer( 20 ) );
		db.put( "CHAT", new Integer( 21 ) );
		db.put( "P2P", new Integer( 22 ) );
		db.put( "RPC", new Integer( 23 ) );
		db.put( "RSERVICES", new Integer( 24 ) );
		db.put( "SCAN", new Integer( 25 ) );
		db.put( "Scanning", new Integer( 25 ) );
		db.put( "SMTP", new Integer( 26 ) );
		db.put( "SHELLCODE", new Integer( 27 ) );
		db.put( "MS-SQL", new Integer( 28 ) );
		db.put( "TELNET", new Integer( 29 ) );
		db.put( "VIRUS", new Integer( 30 ) );
		db.put( "WEB-CGI", new Integer( 31 ) );
		db.put( "WEB-COLDFUSION", new Integer( 32 ) );
		db.put( "WEB-FRONTPAGE", new Integer( 33 ) );
		db.put( "WEB-IIS", new Integer( 34 ) );
		db.put( "WEB-PHP", new Integer( 35 ) );
		db.put( "X11", new Integer( 36 ) );
		db.put( "WEB-CLIENT", new Integer( 37 ) );
		db.put( "WEB-ATTACKS", new Integer( 38 ) );
		db.put( "SNMP", new Integer( 39 ) );
		db.put( "MULTIMEDIA", new Integer( 40 ) );
		db.put( "EXPERIMENTAL", new Integer( 41 ) );
		db.put( "BAD", new Integer( 42 ) );
		db.put( "OTHER-IDS", new Integer( 43 ) );
		db.put( "ORACLE", new Integer( 44 ) );
		db.put( "MYSQL", new Integer( 45 ) );
		db.put( "HTTP", new Integer( 46 ) );
		db.put( "Invalid", new Integer( 47 ) );
		db.put( "Unknown", new Integer( 48 ) );
		db.put( "Overlong", new Integer( 49 ) );
	}
}