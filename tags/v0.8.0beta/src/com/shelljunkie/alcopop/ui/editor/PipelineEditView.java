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

import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import com.shelljunkie.alcopop.logging.LoggerManager;
import com.shelljunkie.alcopop.pipeline.descriptor.PipelineDescriptor;
import com.shelljunkie.alcopop.pipeline.descriptor.PipelineElementDescriptor;
import com.shelljunkie.alcopop.pipeline.descriptor.PipelinePipeElementDescriptor;
import com.shelljunkie.alcopop.ui.EmbeddedWindow;
import com.shelljunkie.alcopop.ui.PipeView;
import com.shelljunkie.alcopop.ui.PipelineElementView;
import com.shelljunkie.alcopop.ui.PipelineView;
import com.shelljunkie.alcopop.ui.UIConstants;

/**
 * @author Juergen Becker
 */
public class PipelineEditView {
	private JFrame parent;
	private EmbeddedWindow window;
	private PipelineView pipelineView;
	private PipelineDescriptor pipelineDescriptor;
	private PropertyChangeListener pipelineChangeListener;

	public PipelineEditView( JFrame parent ) {
		this.parent = parent;
		build();
		pipelineChangeListener = new PipelineChangeListener();
	}

	public JComponent getView() {
		return window;
	}

	public PipelineDescriptor getPipelineDescriptor() {
		return pipelineDescriptor;
	}

	public void setPipelineDescriptor( PipelineDescriptor pipelineDescr ) {
		if ( pipelineDescriptor != null ) {
			pipelineDescriptor.removePropertyChangeListener( pipelineChangeListener );
		}
		window.setTitle( pipelineDescr.getName() );
		pipelineView.setPipelineDescriptor( pipelineDescr );
		pipelineDescriptor = pipelineDescr;
		pipelineDescriptor.addPropertyChangeListener( pipelineChangeListener );
	}

	protected void build() {
		window = new EmbeddedWindow( "abc" );
		window.setTitleBackground( UIConstants.DEFAULT_EMBEDDED_WINDOW_TITLE_COLOR );

		pipelineView = new PipelineView();
		pipelineView.setDropTarget( new DropTarget( window, new MyDropListener() ) );
		pipelineView.setEditable( true );

		JScrollPane pane = new JScrollPane( pipelineView.getView() );
		pane.setBorder( null );
		window.setContentPane( pane );

		MyMouseListener ml = new MyMouseListener();
		pipelineView.addMouseListener( ml );
		pipelineView.addMouseMotionListener( ml );
	}

	private final class MyMouseListener extends MouseAdapter implements MouseMotionListener {
		private static final int MODE_NORMAL = 1;
		private static final int MODE_MOVE = 2;
		private static final int MODE_CONNECT = 4;
		private int mouseXOffset = 0;
		private int mouseYOffset = 0;
		private int mode = MODE_NORMAL;
		private PipelineElementView source;

		@Override
		public void mouseClicked( MouseEvent e ) {
			if ( e.getClickCount() == 2 ) {
				Component comp = pipelineView.getComponentAt( e.getPoint() );
				if ( comp != null ) {
					if ( comp instanceof PipelineElementView ) {
						PipelineElementDescriptor ped = ( (PipelineElementView) comp ).getElementDescriptor();
						if ( ( (PipelineElementView) comp ).isWithinTrashIconArea( e.getX(), e.getY() ) ) {
							pipelineDescriptor.remove( ped );
							// JOptionPane.showMessageDialog( getView(), "Pipeline element removed: " + ped.getName() );
						} else {
							if ( ped.getNoOfConfigurationPropertyNames() != 0 ) {
								PipelineElementConfigWindow win = new PipelineElementConfigWindow( parent, ped );
								win.show();
							} else {
								JOptionPane.showMessageDialog( getView(), "Element ist not configurable." );
							}
						}
					} else if ( comp instanceof PipeView ) {
						pipelineDescriptor.remove( ( (PipeView) comp ).getPipeDescriptor() );
						JOptionPane.showMessageDialog( getView(), "Pipe removed" );
					}
				}
			}
		}

		@Override
		public void mousePressed( MouseEvent e ) {
			Component comp = pipelineView.getComponentAt( e.getPoint() );
			if ( comp != null && comp instanceof PipelineElementView ) {
				source = (PipelineElementView) comp;
			} else {
				return;
			}
			mouseXOffset = e.getX() - source.getX();
			mouseYOffset = e.getY() - source.getY();
			if ( source.isWithinConnectorArea( mouseXOffset, mouseYOffset ) ) {
				if ( source.getElementDescriptor().getType() == PipelineElementDescriptor.TYPE_SOURCE
					|| source.getElementDescriptor().getType() == PipelineElementDescriptor.TYPE_FILTER ) {
					mode = MODE_CONNECT;
					pipelineView.enableTemporaryPipe( source.getRightConnetionPoint(), new Point( e.getPoint() ) );
				}
			} else {
				mode = MODE_MOVE;
			}
		}

		@Override
		public void mouseReleased( MouseEvent e ) {
			if ( mode == MODE_CONNECT ) {
				pipelineView.disableTemporaryPipe();
				Component comp = pipelineView.getComponentAt( e.getPoint() );
				if ( comp != null && comp instanceof PipelineElementView && comp != source ) {
					PipelineElementView target = (PipelineElementView) comp;
					if ( target.getElementDescriptor().getType() != PipelineElementDescriptor.TYPE_SOURCE ) {
						PipelinePipeElementDescriptor pped = new PipelinePipeElementDescriptor();
						pped.setSource( source.getElementDescriptor().getID() );
						pped.setSink( target.getElementDescriptor().getID() );
						if ( !pipelineDescriptor.containsPipe( pped ) ) {
							pipelineDescriptor.add( pped );
						} else {
							JOptionPane.showMessageDialog( getView(), "Elements are allready connected" );
						}
					}
				}
			}
			mode = MODE_NORMAL;
		}

		public void mouseDragged( MouseEvent e ) {
			switch ( mode ) {
				case MODE_MOVE:
					int x = e.getX() - mouseXOffset;
					int y = e.getY() - mouseYOffset;
					if ( x > 0 && x < pipelineView.getWidth() - source.getWidth() && y >= 0 && y <= pipelineView.getHeight() - source.getHeight() ) {
						source.setLocation( x, y );
					}
					break;
				case MODE_CONNECT:
					pipelineView.updateTemporaryPipe( e.getPoint() );
					break;
				default:
					break;
			}
		}

		public void mouseMoved( MouseEvent e ) {
		// nothing
		}
	}

	private class MyDropListener implements DropTargetListener {

		public void dragEnter( DropTargetDragEvent dtde ) {
			dtde.acceptDrag( DnDConstants.ACTION_COPY_OR_MOVE );
		}

		public void dragOver( DropTargetDragEvent dtde ) {
			dtde.acceptDrag( dtde.getDropAction() );
		}

		public void dropActionChanged( DropTargetDragEvent dtde ) {
			dtde.acceptDrag( DnDConstants.ACTION_COPY_OR_MOVE );
		}

		public void dragExit( DropTargetEvent dte ) {
		// nothing to do
		}

		public void drop( DropTargetDropEvent dtde ) {
			dtde.acceptDrop( dtde.getDropAction() );
			Transferable tr = dtde.getTransferable();
			try {
				pipelineView.setElementCreationLoction( dtde.getLocation() );
				PipelineElementDescriptor ped = (PipelineElementDescriptor) tr.getTransferData( PipelineElementDescriptorTransferable.FLAVOR );
				pipelineDescriptor.add( ped );
			} catch ( Exception excep ) {
				LoggerManager.getInstance().getDefaultLogger().error( "DnD drop failed", excep );
				JOptionPane.showMessageDialog( getView(), "Pipeline element creation failed: " + excep.getMessage() );
			}

			dtde.dropComplete( true );
		}
	}

	private final class PipelineChangeListener implements PropertyChangeListener {
		public void propertyChange( PropertyChangeEvent evt ) {
			if ( PipelineDescriptor.PROPERTY_NAME.equals( evt.getPropertyName() ) ) {
				window.setTitle( (String) evt.getNewValue() );
			}
		}
	}

}
