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
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.shelljunkie.alcopop.Configuration;

/**
 * Base window class with a left-right layout.
 * 
 * @author Juergen Becker
 */
public abstract class AbstractMainWindow {
	protected static final int DEFAULT_LEFT_WIDTH = 180;
	private static final int WINDOW_WIDTH = 1024;
	private static final int WINDOW_HEIGHT = 768;
	private JFrame window;

	public AbstractMainWindow() {
		window = build();
	}

	public JFrame getWindow() {
		return window;
	}

	public void show() {
		window.setVisible( true );
	}

	public void hide() {
		window.setVisible( false );
	}

	protected JFrame build() {
		JFrame frame = new JFrame( getTitle() );
		frame.setJMenuBar( buildMenubar() );
		frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		frame.setContentPane( buildMainPane() );

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int posX = ( screenSize.width - WINDOW_WIDTH ) / 2;
		int posY = ( screenSize.height - WINDOW_HEIGHT ) / 2;
		frame.setBounds( posX, posY, WINDOW_WIDTH, WINDOW_HEIGHT );
		return frame;
	}

	protected JComponent buildMainPane() {
		JPanel panel = new JPanel( new BorderLayout( 0, 0 ) );
		panel.add( buildLeftPane(), BorderLayout.WEST );
		panel.add( buildRightPane(), BorderLayout.CENTER );
		panel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 3, 5 ) );
		return panel;
	}

	protected abstract JMenuBar buildMenubar();

	protected JMenuItem buildHelpMenu() {
		JMenu menu = new JMenu( "Help" );
		menu.setMnemonic( KeyEvent.VK_H );
		JMenuItem item = menu.add( new AboutAction() );
		item.setMnemonic( KeyEvent.VK_A );
		return menu;
	}

	protected abstract JComponent buildLeftPane();

	protected abstract JComponent buildRightPane();

	protected abstract String getTitle();

	protected static final class QuitAction extends AbstractAction {
		public QuitAction() {
			super( "Quit" );
		}

		public void actionPerformed( ActionEvent e ) {
			System.exit( 0 );
		}
	}

	protected final class AboutAction extends AbstractAction {
		public AboutAction() {
			super( "About" );
		}

		public void actionPerformed( ActionEvent e ) {
			JOptionPane.showMessageDialog( window, getTitle() + "\n         " + Configuration.COPYRIGHT );
		}
	}

}
