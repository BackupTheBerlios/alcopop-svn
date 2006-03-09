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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Paint;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

/**
 * @author Juergen Becker
 */
public class EmbeddedWindow extends JPanel {
	private static final Color DEFAULT_TITLE_BG_COLOR = Color.BLUE;
	private static final Color DEFAULT_TITLE_FG_COLOR = Color.WHITE;
	private JComponent window;
	private JComponent contentPane;
	private JComponent titlePane;
	private JLabel title;

	public EmbeddedWindow() {
		super( new BorderLayout( 0, 0 ) );
		build();
	}

	public EmbeddedWindow( String title ) {
		this();
		setTitle( title );
	}

	public String getTitle() {
		return title.getText();
	}

	public void setTitle( String title ) {
		this.title.setText( title );
	}

	public void setTitleBackground( Color bgColor ) {
		titlePane.setBackground( bgColor );
	}

	public void setContentPane( JComponent contentPane ) {
		if ( this.contentPane != null ) {
			window.remove( this.contentPane );
			window.getLayout().removeLayoutComponent( this.contentPane );
			this.contentPane = null;
		}
		if ( contentPane != null ) {
			window.add( contentPane, BorderLayout.CENTER );
		}
	}

	protected void build() {
		window = new JPanel( new BorderLayout( 0, 0 ) );
		window.setBorder( new LightShadowBorder() );
		titlePane = buildTitlePane();
		window.add( titlePane, BorderLayout.NORTH );
		add( window, BorderLayout.CENTER );
	}

	protected JComponent buildTitlePane() {
		JPanel panel = new GradientPanel( new BorderLayout( 0, 0 ), DEFAULT_TITLE_BG_COLOR );
		panel.setBorder( new SingleLineBorder( SingleLineBorder.SOUTH, Color.GRAY ) );
		title = new JLabel();
		title.setOpaque( false );
		title.setBorder( BorderFactory.createEmptyBorder( 3, 5, 3, 5 ) );
		title.setForeground( DEFAULT_TITLE_FG_COLOR );
		panel.add( title, BorderLayout.CENTER );
		return panel;
	}

	private static final class GradientPanel extends JPanel {

		private GradientPanel( LayoutManager lm, Color background ) {
			super( lm );
			setBackground( background );
		}

		@Override
		public void paintComponent( Graphics g ) {
			super.paintComponent( g );
			if ( !isOpaque() ) {
				return;
			}
			Color control = UIManager.getColor( "control" );
			int width = getWidth();
			int height = getHeight();

			Graphics2D g2 = (Graphics2D) g;
			Paint storedPaint = g2.getPaint();
			g2.setPaint( new GradientPaint( 0, 0, getBackground(), width, 0, control ) );
			g2.fillRect( 0, 0, width, height );
			g2.setPaint( storedPaint );
		}
	}

}
