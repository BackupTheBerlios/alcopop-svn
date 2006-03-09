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
package com.shelljunkie.alcopop.ui.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceAdapter;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

import com.shelljunkie.alcopop.pipeline.PipelineElementRegistry;
import com.shelljunkie.alcopop.pipeline.descriptor.PipelineElementDescriptor;
import com.shelljunkie.alcopop.ui.EmbeddedWindow;

/**
 * @author Juergen Becker
 */
public class PipelineElementSelectorWindow extends EmbeddedWindow {
	private static final int BOTTOM_SPACER_FOR_THE_EYE_HEIGHT = 10;

	public PipelineElementSelectorWindow( String xpID, String title, int type, Color titleBGColor ) {
		super( title );
		build( xpID, type, titleBGColor );
	}

	protected void build( String xpID, int type, Color titleBGColor ) {
		setTitleBackground( titleBGColor );

		Vector<PipelineElementDescriptor> elements = new Vector<PipelineElementDescriptor>();
		for ( PipelineElementDescriptor ped : PipelineElementRegistry.getInstance().getDescriptors( type ) ) {
			elements.add( ped );
		}

		JList list = new DnDJList( elements );
		setContentPane( list );
	}

	@Override
	public void addNotify() {
		super.addNotify();
		Dimension fixedDimension = new Dimension( (int) getParent().getPreferredSize().getWidth(), (int) getPreferredSize().getHeight()
			+ BOTTOM_SPACER_FOR_THE_EYE_HEIGHT );
		setPreferredSize( fixedDimension );
		setMinimumSize( fixedDimension );
		setMaximumSize( fixedDimension );
		invalidate();
	}

	private final class DnDJList extends JList {
		private DragSourceListener dsListener;

		public DnDJList( Vector<?> data ) {
			super( data );
			setBorder( BorderFactory.createEmptyBorder( 2, 2, 2, 2 ) );
			setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
			DragSource dragSource = DragSource.getDefaultDragSource();
			DragGestureListener dgListener = new MyDragGestureListener();
			dragSource.createDefaultDragGestureRecognizer( this, DnDConstants.ACTION_COPY_OR_MOVE, dgListener );
			dsListener = new MyDragSourceListener();
		}

		private final class MyDragSourceListener extends DragSourceAdapter {

			@Override
			public void dragEnter( DragSourceDragEvent dsde ) {
				dsde.getDragSourceContext().setCursor( DragSource.DefaultCopyDrop );
			}

			@Override
			public void dragExit( DragSourceEvent dse ) {
				dse.getDragSourceContext().setCursor( DragSource.DefaultCopyNoDrop );
			}

		}

		private final class MyDragGestureListener implements DragGestureListener {

			public void dragGestureRecognized( DragGestureEvent dge ) {
				PipelineElementDescriptor ped = new PipelineElementDescriptor( (PipelineElementDescriptor) getSelectedValue() );
				Transferable transferable = new PipelineElementDescriptorTransferable( ped );
				dge.startDrag( DragSource.DefaultCopyNoDrop, transferable, dsListener );
			}

		}
	}
}
