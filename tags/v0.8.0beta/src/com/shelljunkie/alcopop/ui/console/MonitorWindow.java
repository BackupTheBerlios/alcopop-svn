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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * @author Juergen Becker
 */
public class MonitorWindow {
	private static final int WINDOW_WIDTH = 500;
	private static final int WINDOW_HEIGHT = 400;
	private JFrame window;
	private IMonitorView monitorView;
	private JButton refreshButton;
	private JCheckBox automaticRefreshCheckbox;
	private Updater updater;
	private Thread updateThread;

	public MonitorWindow( String title, IMonitorView monitorView ) {
		this.monitorView = monitorView;
		window = build( title );
	}

	public JFrame getWindow() {
		return window;
	}

	public void show() {
		window.setVisible( true );
		monitorView.update();
		window.requestFocus();
	}

	public void hide() {
		window.setVisible( false );
	}

	public void close() {
		stopUpdater();
		monitorView.dispose();
		window.dispose();
	}

	protected void startUpdater() {
		updater = new Updater();
		updateThread = new Thread( updater );
		updateThread.start();
	}

	protected void stopUpdater() {
		if ( updater != null && updateThread != null ) {
			updater.stop();
			updateThread.interrupt();
			updater = null;
			updateThread = null;
		}
	}

	protected JFrame build( String title ) {
		JFrame frame = new JFrame( title );
		frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );

		JPanel panel = new JPanel( new BorderLayout( 0, 4 ) );
		panel.add( monitorView.getView(), BorderLayout.CENTER );
		panel.add( buildControlPanel(), BorderLayout.SOUTH );
		panel.setBorder( BorderFactory.createEmptyBorder( 4, 4, 4, 4 ) );
		frame.setContentPane( panel );

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int posX = ( screenSize.width - WINDOW_WIDTH ) / 2;
		int posY = ( screenSize.height - WINDOW_HEIGHT ) / 2;
		frame.setBounds( posX, posY, WINDOW_WIDTH, WINDOW_HEIGHT );
		return frame;
	}

	protected JComponent buildControlPanel() {
		Box box = new Box( BoxLayout.X_AXIS );
		box.add( Box.createHorizontalGlue() );
		automaticRefreshCheckbox = new JCheckBox( new AutomicRefreshAction() );
		box.add( automaticRefreshCheckbox );
		box.add( Box.createHorizontalStrut( 5 ) );
		refreshButton = new JButton( new RefreshAction() );
		box.add( refreshButton );
		box.add( Box.createHorizontalStrut( 25 ) );
		return box;
	}

	private final class RefreshAction extends AbstractAction {
		RefreshAction() {
			super( "Refresh" );
		}

		public void actionPerformed( ActionEvent e ) {
			monitorView.update();
		}

	}

	private final class AutomicRefreshAction extends AbstractAction {
		AutomicRefreshAction() {
			super( "automatic refresh" );
		}

		public void actionPerformed( ActionEvent e ) {
			if ( automaticRefreshCheckbox.isSelected() ) {
				refreshButton.setEnabled( false );
				startUpdater();
			} else {
				refreshButton.setEnabled( true );
				stopUpdater();
			}
		}
	}

	private final class Updater implements Runnable {
		private boolean enabled = true;

		public void stop() {
			enabled = false;
		}

		public void run() {
			while ( enabled ) {
				monitorView.update();
				try {
					Thread.sleep( 1000 );
				} catch ( InterruptedException excep ) {
					// ok
				}
			}
		}

	}

}
