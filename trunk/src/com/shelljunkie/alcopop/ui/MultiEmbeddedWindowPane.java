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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

/**
 * @author Juergen Becker
 */
public class MultiEmbeddedWindowPane extends JPanel {
	private static final int SPACER_SIZE = 5;
	public static final int HORIZONTAL_ORIENTATION = 1;
	public static final int VERTICAL_ORIENTATION = 2;
	private int orientation;

	public MultiEmbeddedWindowPane( int orientation ) {
		super();
		if ( orientation != HORIZONTAL_ORIENTATION && orientation != VERTICAL_ORIENTATION ) {
			throw new IllegalArgumentException( "orientation must be one of HORIZONTAL_ORIENTATION or VERTICAL_ORIENTATION." );
		}
		this.orientation = orientation;
		build();
	}

	public MultiEmbeddedWindowPane() {
		this( VERTICAL_ORIENTATION );
	}

	public void addEmbeddedWindow( EmbeddedWindow ew ) {
		if ( getComponentCount() > 1 ) {
			remove( getComponentCount() - 1 );
		}
		add( ew );
		if ( isVerticalOrientation() ) {
			add( Box.createVerticalStrut( SPACER_SIZE ) );
			add( Box.createVerticalGlue() );
		} else {
			add( Box.createHorizontalStrut( SPACER_SIZE ) );
			add( Box.createHorizontalGlue() );

		}
	}

	protected boolean isVerticalOrientation() {
		return orientation == VERTICAL_ORIENTATION;
	}

	protected void build() {
		if ( isVerticalOrientation() ) {
			setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
		} else {
			setLayout( new BoxLayout( this, BoxLayout.X_AXIS ) );
		}
		setBorder( BorderFactory.createEmptyBorder( 0, 0, 5, 5 ) );
		setOpaque( false );
	}

}
