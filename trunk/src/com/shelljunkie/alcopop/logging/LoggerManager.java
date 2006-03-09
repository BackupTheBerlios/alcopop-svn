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
package com.shelljunkie.alcopop.logging;

import java.util.HashMap;
import java.util.Map;

import com.shelljunkie.alcopop.Configuration;

/**
 * << singleton >>
 * 
 * @author Juergen Becker
 */
public class LoggerManager {
	private static volatile LoggerManager instance;
	private Map<String, ILogger> namedLogger;

	private LoggerManager() {
		namedLogger = new HashMap<String, ILogger>();
	}

	public static LoggerManager getInstance() {
		if ( instance == null ) {
			synchronized ( LoggerManager.class ) {
				if ( instance == null ) {
					instance = new LoggerManager();
				}
			}
		}
		return instance;
	}

	public ILogger getLogger( String name ) {
		synchronized ( namedLogger ) {
			ILogger log = namedLogger.get( name );
			if ( log == null ) {
				log = new DefaultLogger( name );
				namedLogger.put( name, log );
			}
			return log;
		}
	}

	public ILogger getLogger( Class clazz ) {
		String classname = clazz.getName();
		int index = classname.lastIndexOf( "." );
		if ( index != -1 ) {
			classname = classname.substring( index + 1 );
		}
		return getLogger( classname );
	}

	public ILogger getDefaultLogger() {
		return getLogger( Configuration.NAME_SHORT );
	}
}
