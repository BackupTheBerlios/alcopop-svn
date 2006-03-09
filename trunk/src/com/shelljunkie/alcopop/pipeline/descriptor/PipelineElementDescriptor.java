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
package com.shelljunkie.alcopop.pipeline.descriptor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A single pipeline element descriptor
 * 
 * @author Juergen Becker
 */
public class PipelineElementDescriptor implements Serializable {
	/** <code>serialVersionUID</code> */
	private static final long serialVersionUID = 2226782519524780886L;
	public static final int TYPE_SOURCE = 2;
	public static final int TYPE_FILTER = 4;
	public static final int TYPE_SINK = 8;
	private PipelineDescriptor pipelineDescriptor;
	private String name;
	private String id;
	private String className;
	private String pipeClassName;
	private int type;
	private Map<String, String> configurationProperties;
	private List<String> configurationPropertyNames;

	public PipelineElementDescriptor( int type, String name, String classname, String[] configurationPropertyNames ) {
		configurationProperties = new HashMap<String, String>();
		this.type = type;
		this.name = name;
		this.className = classname;
		if ( configurationPropertyNames != null ) {
			this.configurationPropertyNames = new ArrayList<String>( configurationPropertyNames.length );
			Collections.addAll( this.configurationPropertyNames, configurationPropertyNames );
		} else {
			this.configurationPropertyNames = new ArrayList<String>();
		}
	}

	public PipelineElementDescriptor( int type ) {
		this( type, null, null, null );
	}

	public PipelineElementDescriptor( PipelineElementDescriptor master ) {
		this( master.type, master.name, master.className, null );
		configurationPropertyNames.addAll( master.configurationPropertyNames );
	}

	public PipelineElementDescriptor() {
		this( 0, null, null, null );
	}

	public String getID() {
		return id;
	}

	public void setID( String id ) {
		this.id = id;
	}

	public PipelineDescriptor getPipelineDescriptor() {
		return pipelineDescriptor;
	}

	public void setPipelineDescriptor( PipelineDescriptor pipelineDescriptor ) {
		this.pipelineDescriptor = pipelineDescriptor;
	}

	public boolean isOfTypeSource() {
		return type == TYPE_SOURCE;
	}

	public boolean isOfTypeFilter() {
		return type == TYPE_FILTER;
	}

	public boolean isOfTypeSink() {
		return type == TYPE_SINK;
	}

	public int getType() {
		return type;
	}

	public String getTypeAsString() {
		switch ( type ) {
			case TYPE_SOURCE:
				return "Source";
			case TYPE_FILTER:
				return "Filter";
			case TYPE_SINK:
				return "Sink";
		}
		return null;
	}

	public void setType( int type ) {
		if ( !isValidType( type ) ) {
			throw new IllegalArgumentException( "bad type: " + type );
		}
		this.type = type;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName( String className ) {
		this.className = className;
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public String getPipeClassName() {
		return pipeClassName;
	}

	public void setPipeClassName( String pipeClassName ) {
		this.pipeClassName = pipeClassName;
	}

	public void addConfigurationPropertyName( String name ) {
		configurationPropertyNames.add( name );
	}

	public void addConfigurationPropertyNames( String[] names ) {
		for ( String name : names ) {
			configurationPropertyNames.add( name );
		}
	}

	public Iterable<String> getConfigurationPropertyNames() {
		return configurationPropertyNames;
	}

	public String getConfigurationPropertyName( int index ) {
		return configurationPropertyNames.get( index );
	}

	public int getNoOfConfigurationPropertyNames() {
		return configurationPropertyNames.size();
	}

	public String getConfigurationPropertyValue( String name ) {
		return configurationProperties.get( name );
	}

	public void setConfigurationPropertyValue( String name, String value ) {
		configurationProperties.put( name, value );
	}

	public Map<String, String> getConfigurationProperties() {
		return configurationProperties;
	}

	@Override
	public String toString() {
		return name;
	}

	protected boolean isValidType( int type ) {
		return type == TYPE_SOURCE || type == TYPE_FILTER || type == TYPE_SINK;
	}

}
