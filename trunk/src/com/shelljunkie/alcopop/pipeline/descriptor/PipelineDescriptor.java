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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Juergen Becker
 */
public class PipelineDescriptor implements Serializable {
	public static final String PROPERTY_PIPES = "pipes";
	public static final String PROPERTY_ELEMENTS = "elements";
	public static final String PROPERTY_NAME = "name";
	private static final long serialVersionUID = -3762366129768194848L;
	private static final String DEFAULT_ID = "-1";
	private String name;
	private List<PipelineElementDescriptor> elements;
	private List<PipelinePipeElementDescriptor> pipes;
	private int pipelineElementIDCounter;
	private PropertyChangeSupport propertySupport;
	private String filename;

	public PipelineDescriptor( String name ) {
		this.name = name;
		elements = new ArrayList<PipelineElementDescriptor>();
		pipes = new ArrayList<PipelinePipeElementDescriptor>();
		propertySupport = new PropertyChangeSupport( this );
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		if ( name != null ) {
			String old = this.name;
			this.name = name;
			propertySupport.firePropertyChange( PROPERTY_NAME, old, this.name );
		}
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename( String filename ) {
		this.filename = filename;
	}

	public void add( PipelineElementDescriptor pmd ) {
		checkID( pmd );
		pmd.setPipelineDescriptor( this );
		elements.add( pmd );
		propertySupport.firePropertyChange( PROPERTY_ELEMENTS, null, pmd );
	}

	public void remove( PipelineElementDescriptor pmd ) {
		removeConnectedPipes( pmd );
		elements.remove( pmd );
		pmd.setPipelineDescriptor( null );
		propertySupport.firePropertyChange( PROPERTY_ELEMENTS, pmd, null );
	}

	public void add( PipelinePipeElementDescriptor pmd ) {
		checkID( pmd );
		pipes.add( pmd );
		propertySupport.firePropertyChange( PROPERTY_PIPES, null, pmd );
	}

	public void remove( PipelinePipeElementDescriptor pmd ) {
		pipes.remove( pmd );
		propertySupport.firePropertyChange( PROPERTY_PIPES, pmd, null );
	}

	public boolean containsPipe( PipelinePipeElementDescriptor pped ) {
		return elements.contains( pped );
	}

	public PipelineElementDescriptor getDescriptorByID( String id ) {
		for ( PipelineElementDescriptor ped : elements ) {
			if ( ped.getID().equals( id ) ) {
				return ped;
			}
		}
		return null;
	}

	public Iterable<PipelineElementDescriptor> getDescriptors() {
		return elements;
	}

	public Iterable<PipelineElementDescriptor> getSourceDescriptors() {
		return getListOfType( PipelineElementDescriptor.TYPE_SOURCE );
	}

	public int getNoOfSourceDescriptors() {
		return getListOfType( PipelineElementDescriptor.TYPE_SOURCE ).size();
	}

	public Iterable<PipelineElementDescriptor> getFilterDescriptors() {
		return getListOfType( PipelineElementDescriptor.TYPE_FILTER );
	}

	public Iterable<PipelineElementDescriptor> getSinkDescriptors() {
		return getListOfType( PipelineElementDescriptor.TYPE_SINK );
	}

	public int getNoOfSinkDescriptors() {
		return getListOfType( PipelineElementDescriptor.TYPE_SINK ).size();
	}

	public Iterable<PipelinePipeElementDescriptor> getPipeDescriptors() {
		return pipes;
	}

	public void addPropertyChangeListener( PropertyChangeListener listener ) {
		propertySupport.addPropertyChangeListener( listener );
	}

	public void removePropertyChangeListener( PropertyChangeListener listener ) {
		propertySupport.removePropertyChangeListener( listener );
	}

	protected List<PipelineElementDescriptor> getListOfType( int type ) {
		List<PipelineElementDescriptor> list = new ArrayList<PipelineElementDescriptor>();
		for ( PipelineElementDescriptor ped : elements ) {
			if ( ped.getType() == type ) {
				list.add( ped );
			}
		}
		return list;
	}

	protected void checkID( PipelineElementDescriptor pmd ) {
		if ( pmd.getID() == null || DEFAULT_ID.equals( pmd.getID() ) ) {
			pmd.setID( getNextPipelineElementID() );
		} else {
			int id = Integer.parseInt( pmd.getID() );
			if ( id >= pipelineElementIDCounter ) {
				pipelineElementIDCounter = id;
			}
		}
	}

	protected void checkIDs( List<? extends PipelineElementDescriptor> pmds ) {
		for ( PipelineElementDescriptor pmd : pmds ) {
			checkID( pmd );
		}
	}

	protected void removeConnectedPipes( PipelineElementDescriptor pmd ) {
		for ( int i = 0; i < pipes.size(); ) {
			PipelinePipeElementDescriptor pped = pipes.get( i );
			if ( pped.getSource().equals( pmd.getID() ) || pped.getSink().equals( pmd.getID() ) ) {
				remove( pped );
			} else {
				++i;
			}
		}
	}

	protected String getNextPipelineElementID() {
		return String.valueOf( ++pipelineElementIDCounter );
	}

}
