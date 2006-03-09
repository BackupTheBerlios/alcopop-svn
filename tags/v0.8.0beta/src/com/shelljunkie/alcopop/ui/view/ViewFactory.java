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
package com.shelljunkie.alcopop.ui.view;

import java.util.HashMap;
import java.util.Map;

import com.shelljunkie.alcopop.logging.LoggerManager;
import com.shelljunkie.alcopop.sink.ConsoleLoggerSinkView;

/**
 * @author Juergen Becker
 */
public class ViewFactory {
	private static ViewFactory instance = null;
	private Map<String, Class> views;

	private ViewFactory() {
		views = new HashMap<String, Class>();
		initialize();
	}

	protected void initialize() {
		register( "Console Logger", ConsoleLoggerSinkView.class );
	}

	public static ViewFactory getInstance() {
		if ( instance == null ) {
			instance = new ViewFactory();
		}
		return instance;
	}

	public IView createView( String name ) {
		Class clazz = views.get( name );
		if ( clazz == null ) {
			return null;
		}
		return createInstance( clazz );
	}

	public boolean register( String name, Class clazz ) {
		if ( !views.containsKey( name ) ) {
			views.put( name, clazz );
			return true;
		}
		return false;
	}

	protected IView createInstance( Class clazz ) {
		try {
			Object obj = clazz.newInstance();
			if ( obj instanceof IView ) {
				return (IView) obj;
			}
		} catch ( Exception excep ) {
			LoggerManager.getInstance().getDefaultLogger().error( "unable to create instance of " + clazz, excep );
		}
		return null;
	}
}
