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
package com.shelljunkie.alcopop.ui.console;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

import com.shelljunkie.alcopop.Configuration;
import com.shelljunkie.alcopop.ui.EmbeddedWindow;

/**
 * @author Juergen Becker
 */
public class ConnectionSelectorWindow extends EmbeddedWindow {
	private static final int BOTTOM_SPACER_FOR_THE_EYE_HEIGHT = 10;
	private JList list;
	private String selection;

	public ConnectionSelectorWindow( String title, Color titleBGColor ) {
		super( title );
		build( titleBGColor );
	}

	protected void build( Color titleBGColor ) {
		setTitleBackground( titleBGColor );
		list = new JList( Configuration.getInstance().getConnections() );
		list.setBorder( BorderFactory.createEmptyBorder( 2, 2, 2, 2 ) );
		list.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		list.addMouseListener( new DoubleClickListener() );
		setContentPane( list );
	}

	public String getSelection() {
		if ( selection == null ) {
			selection = (String) list.getSelectedValue();
		}
		return selection;
	}

	public void updateList() {
		list.setListData( Configuration.getInstance().getConnections() );
		list.revalidate();
		updateSize();
	}

	@Override
	public void addNotify() {
		super.addNotify();
		updateSize();
	}

	public void setSelection( String selection ) {
		String old = this.selection;
		this.selection = selection;
		if ( this.selection == null ) {
			list.clearSelection();
		} else {
			list.setSelectedValue( selection, true );
		}
		firePropertyChange( "selection", old, this.selection );
	}

	protected void updateSize() {
		Dimension fixedDimension = new Dimension( (int) getParent().getPreferredSize().getWidth(), (int) getPreferredSize().getHeight()
			+ BOTTOM_SPACER_FOR_THE_EYE_HEIGHT );
		setPreferredSize( fixedDimension );
		setMinimumSize( fixedDimension );
		setMaximumSize( fixedDimension );
		invalidate();
	}

	private final class DoubleClickListener extends MouseAdapter {
		@Override
		public void mouseClicked( MouseEvent e ) {
			if ( e.getClickCount() == 2 ) {
				setSelection( (String) list.getSelectedValue() );
			}
		}
	}

}
