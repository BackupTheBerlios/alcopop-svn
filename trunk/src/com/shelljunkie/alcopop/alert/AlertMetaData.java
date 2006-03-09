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
import java.util.Iterator;
import java.util.Map;

/**
 * @author Juergen Becker
 */
public class AlertMetaData {
	private Map<String, Object> attributes;

	public boolean isMetaDataAvaillable() {
		return attributes != null && !attributes.isEmpty();
	}

	public void addAttribute( String name, Object value ) {
		if ( attributes == null ) {
			attributes = new HashMap<String, Object>();
		}
		attributes.put( name, value );
	}

	public void removeAttribute( String name ) {
		if ( attributes != null ) {
			attributes.remove( name );
		}
	}

	public Object getAttribute( String name ) {
		if ( attributes != null ) {
			return attributes.get( name );
		}
		return null;
	}

	public Iterator<String> getAllAttributeNames() {
		if ( isMetaDataAvaillable() ) {
			return attributes.keySet().iterator();
		}
		return null;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for ( String attributeName : attributes.keySet() ) {
			buf.append( " Attribute (" + attributeName + "=" + attributes.get( attributeName ) + ") " );
		}
		return buf.toString();
	}
}
