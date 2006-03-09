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
package com.shelljunkie.alcopop.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.dnd.DropTarget;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;

import com.shelljunkie.alcopop.pipeline.descriptor.PipelineDescriptor;
import com.shelljunkie.alcopop.pipeline.descriptor.PipelineElementDescriptor;
import com.shelljunkie.alcopop.pipeline.descriptor.PipelinePipeElementDescriptor;

/**
 * @author Juergen Becker
 */
public class PipelineView {
	private final static int HORIZONTAL_GAP = 20;
	private final static int VERTICAL_GAP = 20;
	private JLayeredPane pane;
	private PipelineDescriptor pipelineDescriptor;
	private Map<String, JComponent> views;
	private PropertyChangeListener pipelineChangeListener;
	private TemporaryPipeView temporaryPipe;
	private Point elementCreationLoction;
	private boolean editable = false;
	private int maxX;
	private int maxY;

	public PipelineView() {
		super();
		build();
		pipelineChangeListener = new PipelineChangeListener();
	}

	public JComponent getView() {
		return pane;
	}

	public void setEditable( boolean b ) {
		editable = b;
	}

	public void enableTemporaryPipe( Point start, Point end ) {
		temporaryPipe = new TemporaryPipeView( start, end );
		pane.add( temporaryPipe, JLayeredPane.DEFAULT_LAYER );
	}

	public void updateTemporaryPipe( Point end ) {
		temporaryPipe.updateEndPoint( end );
	}

	public void disableTemporaryPipe() {
		pane.remove( temporaryPipe );
		pane.repaint();
	}

	public int getWidth() {
		return pane.getWidth();
	}

	public int getHeight() {
		return pane.getHeight();
	}

	public void setPipelineDescriptor( PipelineDescriptor pipelineDescr ) {
		if ( this.pipelineDescriptor != null ) {
			this.pipelineDescriptor.removePropertyChangeListener( pipelineChangeListener );
			this.pipelineDescriptor = null;
		}
		views = new HashMap<String, JComponent>();
		this.pipelineDescriptor = pipelineDescr;
		pane.removeAll();
		if ( pipelineDescriptor != null ) {
			createElements( pipelineDescriptor );
			pipelineDescriptor.addPropertyChangeListener( pipelineChangeListener );
		}
		pane.repaint();
	}

	public Component getComponentAt( Point point ) {
		return pane.getComponentAt( point );
	}

	public void setElementCreationLoction( Point location ) {
		elementCreationLoction = location;
	}

	public void setDropTarget( DropTarget target ) {
		pane.setDropTarget( target );
	}

	public void addMouseListener( MouseListener ml ) {
		pane.addMouseListener( ml );
	}

	public void addMouseMotionListener( MouseMotionListener ml ) {
		pane.addMouseMotionListener( ml );
	}

	protected void createElements( PipelineDescriptor pipelineDescr ) {
		if ( pipelineDescr.getNoOfSinkDescriptors() == 0 ) {
			return;
		}
		Point location = new Point( 10, 10 );
		maxX = 0;
		maxY = 0;
		for ( PipelineElementDescriptor ped : pipelineDescr.getSourceDescriptors() ) {
			if ( location.y < maxY ) {
				location.setLocation( location.x, maxY );
			}
			createElements( ped, pipelineDescr.getPipeDescriptors(), location );
		}
		pane.setPreferredSize( new Dimension( maxX, maxY ) );
	}

	protected void createElements( PipelineElementDescriptor ped, Iterable<PipelinePipeElementDescriptor> pipes, Point location ) {
		JComponent pew = getView( ped );
		if ( pew == null ) {
			pew = addPipelineElementView( ped, location );
			if ( location.x + pew.getWidth() + HORIZONTAL_GAP > maxX ) {
				maxX = location.x + pew.getWidth() + HORIZONTAL_GAP;
			}
			if ( !ped.isOfTypeSink() ) {
				for ( PipelinePipeElementDescriptor pped : pipes ) {
					if ( pped.getSource().equals( ped.getID() ) ) {
						PipelineElementDescriptor sinkDescr = pipelineDescriptor.getDescriptorByID( pped.getSink() );
						if ( location.y < maxY ) {
							location.setLocation( location.x, maxY );
						}
						createElements( sinkDescr, pipes, new Point( location.x + pew.getWidth() + HORIZONTAL_GAP, location.y ) );
						addPipeView( pped );
						int newY = location.y + pew.getHeight() + VERTICAL_GAP;
						if ( newY > maxY ) {
							maxY = newY;
						}
						location.setLocation( location.x, newY );
					}
				}
			}
		}
	}

	protected void build() {
		pane = new JLayeredPane();
		pane.setBackground( new Color( 250, 250, 215 ) );
		pane.setOpaque( true );
		pane.setPreferredSize( new Dimension( 500, 500 ) );
		pane.setDoubleBuffered( true );
	}

	protected JComponent addPipelineElementView( PipelineElementDescriptor ped, Point location ) {
		PipelineElementView pew = new PipelineElementView( ped, location );
		if ( editable ) {
			pew.setDeleteIconEnabled( true );
		}
		views.put( ped.getID(), pew );
		pane.add( pew, JLayeredPane.DRAG_LAYER );
		return pew;
	}

	protected void removePipelineElementView( PipelineElementDescriptor ped ) {
		JComponent comp = views.remove( ped.getID() );
		pane.remove( comp );
	}

	protected JComponent addPipeView( PipelinePipeElementDescriptor ped ) {
		PipelineElementView sourceView = (PipelineElementView) views.get( ped.getSource() );
		PipelineElementView sinkView = (PipelineElementView) views.get( ped.getSink() );
		PipeView pew = new PipeView( ped, sourceView, sinkView );
		views.put( ped.getID(), pew );
		pane.add( pew, JLayeredPane.DEFAULT_LAYER );
		return pew;
	}

	protected void removePipeView( PipelinePipeElementDescriptor ped ) {
		JComponent pew = views.remove( ped.getID() );
		pane.remove( pew );
	}

	protected JComponent getView( PipelineElementDescriptor ped ) {
		return views.get( ped.getID() );
	}

	private final class PipelineChangeListener implements PropertyChangeListener {
		public void propertyChange( PropertyChangeEvent evt ) {
			if ( PipelineDescriptor.PROPERTY_ELEMENTS.equals( evt.getPropertyName() ) ) {
				if ( evt.getNewValue() != null ) {
					addPipelineElementView( (PipelineElementDescriptor) evt.getNewValue(), elementCreationLoction );
					elementCreationLoction = null;
				} else if ( evt.getOldValue() != null ) {
					removePipelineElementView( (PipelineElementDescriptor) evt.getOldValue() );
				}
				pane.repaint();
			} else if ( PipelineDescriptor.PROPERTY_PIPES.equals( evt.getPropertyName() ) ) {
				if ( evt.getNewValue() != null ) {
					addPipeView( (PipelinePipeElementDescriptor) evt.getNewValue() );
				} else if ( evt.getOldValue() != null ) {
					removePipeView( (PipelinePipeElementDescriptor) evt.getOldValue() );
				}
				pane.repaint();
			}
		}
	}

	private class TemporaryPipeView extends JComponent {
		private Point start;
		private Point end;

		public TemporaryPipeView( Point source, Point target ) {
			super();
			this.start = source;
			this.end = target;
		}

		protected void updateBounds() {
			int width = Math.abs( start.x - end.x );
			int height = Math.abs( start.y - end.y ) + 1;
			setBounds( Math.min( start.x, end.x ), Math.min( start.y, end.y ), width, height );
		}

		public void updateEndPoint( Point endPoint ) {
			this.end = endPoint;
			updateBounds();
			invalidate();
		}

		@Override
		public void addNotify() {
			super.addNotify();
			updateBounds();
		}

		@Override
		public void paint( Graphics g ) {
			super.paint( g );
			Color oldColor = g.getColor();

			g.setColor( Color.BLACK );
			int x1 = 0;
			int x2 = getWidth() - 1;
			int y1, y2;
			if ( start.x <= end.x ) {
				if ( start.y <= end.y ) {
					y1 = 0;
					y2 = getHeight() - 1;
				} else {
					y1 = getHeight() - 1;
					y2 = 0;
				}
			} else {
				if ( start.y <= end.y ) {
					y1 = getHeight() - 1;
					y2 = 0;
				} else {
					y1 = 0;
					y2 = getHeight() - 1;
				}
			}
			g.drawLine( x1, y1, x2, y2 );

			g.setColor( oldColor );
		}
	}

}
