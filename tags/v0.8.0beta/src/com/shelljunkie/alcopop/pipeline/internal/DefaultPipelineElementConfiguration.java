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
package com.shelljunkie.alcopop.pipeline.internal;

import java.util.Map;

import com.shelljunkie.alcopop.pipeline.IPipelineElementConfiguration;
import com.shelljunkie.alcopop.pipeline.PipelineElementConfigurationException;

/**
 * @author Juergen Becker
 */
public class DefaultPipelineElementConfiguration implements IPipelineElementConfiguration {
	private Map<String, String> properties;

	public DefaultPipelineElementConfiguration( Map<String, String> properties ) {
		if ( properties == null ) {
			throw new RuntimeException( "properties must not bu null" );
		}
		this.properties = properties;
	}

	public String getConfigurationProperty( String name ) {
		return properties.get( name );
	}

	public String getConfigurationPropertyChecked( String name ) throws PipelineElementConfigurationException {
		String value = getConfigurationProperty( name );
		if ( value == null ) {
			throw new PipelineElementConfigurationException( name );
		}
		return value;
	}

	public boolean getBooleanConfigurationPropertyChecked( String name ) throws PipelineElementConfigurationException {
		String booleanStr = getConfigurationProperty( name );
		if ( booleanStr == null || booleanStr.length() == 0 ) {
			throw new PipelineElementConfigurationException( name );
		}
		return Boolean.parseBoolean( booleanStr );
	}

	public int getIntConfigurationProperty( String name, int defaultValue ) {
		String intStr = getConfigurationProperty( name );
		if ( intStr == null || intStr.length() == 0 ) {
			return defaultValue;
		}
		try {
			return Integer.parseInt( intStr );
		} catch ( NumberFormatException ex ) {
			return defaultValue;
		}
	}

	public int getIntConfigurationPropertyChecked( String name ) throws PipelineElementConfigurationException {
		String intStr = getConfigurationProperty( name );
		if ( intStr == null || intStr.length() == 0 ) {
			throw new PipelineElementConfigurationException( name );
		}
		try {
			return Integer.parseInt( intStr );
		} catch ( NumberFormatException ex ) {
			throw new PipelineElementConfigurationException( name );
		}
	}

	public double getDoubleConfigurationPropertyChecked( String name ) throws PipelineElementConfigurationException {
		String doubleStr = getConfigurationProperty( name );
		if ( doubleStr == null || doubleStr.length() == 0 ) {
			throw new PipelineElementConfigurationException( name );
		}
		try {
			return Double.parseDouble( doubleStr );
		} catch ( NumberFormatException ex ) {
			throw new PipelineElementConfigurationException( name );
		}
	}

}
