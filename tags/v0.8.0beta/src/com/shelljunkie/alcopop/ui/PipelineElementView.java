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
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.UIManager;

import com.shelljunkie.alcopop.pipeline.descriptor.PipelineElementDescriptor;

/**
 * @author Juergen Becker
 */
public class PipelineElementView extends JComponent {
	private static final int MAIN_WIDTH = 150;
	private static final int MAIN_HEIGHT = 60;
	private static final int PIPECONNECTOR_WIDTH = 10;
	private static final int PIPECONNECTOR_HEIGHT = 20;
	private static final ImageIcon TRASH_ICON = new ImageIcon( PipelineElementView.class.getResource( "trash.png" ) );
	private PipelineElementDescriptor pipelineElementDescriptor;
	private boolean deleteIconEnabled = false;

	public PipelineElementView( PipelineElementDescriptor ped, Point location ) {
		super();
		this.pipelineElementDescriptor = ped;
		setPreferredSize( new Dimension( MAIN_WIDTH, MAIN_HEIGHT ) );
		setBounds( location.x, location.y, MAIN_WIDTH, MAIN_HEIGHT );
	}

	public PipelineElementDescriptor getElementDescriptor() {
		return pipelineElementDescriptor;
	}

	public boolean isDeleteIconEnabled() {
		return deleteIconEnabled;
	}

	public void setDeleteIconEnabled( boolean deleteIconEnabled ) {
		this.deleteIconEnabled = deleteIconEnabled;
	}

	public boolean isWithinConnectorArea( int x, int y ) {
		return x <= PIPECONNECTOR_WIDTH || x >= MAIN_WIDTH - PIPECONNECTOR_WIDTH;
	}

	public Point getLeftConnetionPoint() {
		return new Point( getX() + 1, getY() + getHeight() / 2 );
	}

	public Point getRightConnetionPoint() {
		return new Point( getX() + getWidth() - 1, getY() + getHeight() / 2 );
	}

	public boolean isWithinTrashIconArea( int x, int y ) {
		return x - getX() >= getWidth() - TRASH_ICON.getIconWidth() - 1 && y - getY() > getHeight() - TRASH_ICON.getIconHeight() - 2;
	}

	protected Color getBackgroundColor() {
		if ( pipelineElementDescriptor.isOfTypeSource() ) {
			return UIConstants.SOURCE_COLOR;
		}
		if ( pipelineElementDescriptor.isOfTypeFilter() ) {
			return UIConstants.FILTER_COLOR;
		}
		if ( pipelineElementDescriptor.isOfTypeSink() ) {
			return UIConstants.SINK_COLOR;
		}
		return Color.WHITE;
	}

	@Override
	public void paint( Graphics g ) {
		super.paint( g );
		Color oldColor = g.getColor();

		Color control = UIManager.getColor( "control" );
		int width = getWidth();
		int height = getHeight();

		Graphics2D g2 = (Graphics2D) g;
		Paint storedPaint = g2.getPaint();
		g2.setPaint( new GradientPaint( 0, 0, getBackgroundColor(), width, 0, control ) );
		g2.fillRect( 0, 0, width, height );
		g2.setPaint( storedPaint );

		g.setColor( Color.WHITE );
		g.drawString( pipelineElementDescriptor.getName(), PIPECONNECTOR_WIDTH, 15 );

		g.setColor( Color.BLACK );
		g.drawRect( 0, 0, MAIN_WIDTH - 1, MAIN_HEIGHT - 1 );
		if ( pipelineElementDescriptor.isOfTypeFilter() || pipelineElementDescriptor.isOfTypeSink() ) {
			g.fillRect( 0, PIPECONNECTOR_HEIGHT, PIPECONNECTOR_WIDTH, PIPECONNECTOR_HEIGHT );
		}
		if ( pipelineElementDescriptor.isOfTypeFilter() || pipelineElementDescriptor.isOfTypeSource() ) {
			g.fillRect( MAIN_WIDTH - PIPECONNECTOR_WIDTH, PIPECONNECTOR_HEIGHT, PIPECONNECTOR_WIDTH, PIPECONNECTOR_HEIGHT );
		}

		if ( deleteIconEnabled ) {
			g.drawImage( TRASH_ICON.getImage(), width - TRASH_ICON.getIconWidth() - 1, height - TRASH_ICON.getIconHeight() - 2, this );
		}

		g.setColor( oldColor );
	}
}
