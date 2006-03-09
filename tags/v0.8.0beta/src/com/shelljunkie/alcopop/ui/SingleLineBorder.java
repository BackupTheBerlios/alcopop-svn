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
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.Border;

/**
 * @author Juergen Becker
 */
public class SingleLineBorder implements Border {
	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;

	private static final Color DEFAULT_COLOR = Color.BLACK;

	private int position;
	private Color color;

	public SingleLineBorder( int position, Color color ) {
		if ( !isPositionValid( position ) ) {
			throw new IllegalArgumentException( "position is wrong" );
		}
		this.position = position;
		this.color = color;
	}

	public SingleLineBorder() {
		this( SOUTH, DEFAULT_COLOR );
	}

	public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
		Color oldColor = g.getColor();
		g.setColor( color );
		switch ( position ) {
			case NORTH:
				g.drawLine( 0, 0, c.getWidth() - 1, 0 );
				break;
			case EAST:
				g.drawLine( c.getWidth() - 1, 0, c.getWidth() - 1, c.getHeight() - 1 );
				break;
			case WEST:
				g.drawLine( 0, 0, 0, c.getHeight() - 1 );
				break;
			case SOUTH:
			default:
				g.drawLine( 0, c.getHeight() - 1, c.getWidth(), c.getHeight() - 1 );
		}

		g.setColor( oldColor );
	}

	public Insets getBorderInsets( Component c ) {
		switch ( position ) {
			case NORTH:
				return new Insets( 1, 0, 0, 0 );
			case EAST:
				return new Insets( 0, 0, 0, 1 );
			case WEST:
				return new Insets( 0, 1, 0, 0 );
			case SOUTH:
			default:
				return new Insets( 0, 0, 1, 0 );
		}
	}

	public boolean isBorderOpaque() {
		return true;
	}

	protected boolean isPositionValid( int position ) {
		return position >= 0 && position <= 3;
	}

}
