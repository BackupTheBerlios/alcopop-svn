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
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JComponent;

import com.shelljunkie.alcopop.pipeline.descriptor.PipelinePipeElementDescriptor;

/**
 * @author Juergen Becker
 */
public class PipeView extends JComponent {
	private PipelineElementView sourceView;
	private PipelineElementView sinkView;
	private PipelinePipeElementDescriptor pipeDescriptor;

	public PipeView( PipelinePipeElementDescriptor pipe, PipelineElementView sourceView, PipelineElementView sinkView ) {
		super();
		this.pipeDescriptor = pipe;
		this.sourceView = sourceView;
		ComponentListener cl = new MyComponentListener();
		sourceView.addComponentListener( cl );
		sinkView.addComponentListener( cl );
		this.sinkView = sinkView;
		setOpaque( false );
	}

	public PipelinePipeElementDescriptor getPipeDescriptor() {
		return pipeDescriptor;
	}

	protected void updateBounds() {
		Point start = sourceView.getRightConnetionPoint();
		Point end = sinkView.getLeftConnetionPoint();
		int width = Math.abs( start.x - end.x );
		int height = Math.abs( start.y - end.y ) + 1;
		setBounds( Math.min( start.x, end.x ), Math.min( start.y, end.y ), width, height );
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
		Point start = sourceView.getRightConnetionPoint();
		Point end = sinkView.getLeftConnetionPoint();
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

	private final class MyComponentListener extends ComponentAdapter {
		@Override
		public void componentMoved( ComponentEvent e ) {
			updateBounds();
		}
	}
}
