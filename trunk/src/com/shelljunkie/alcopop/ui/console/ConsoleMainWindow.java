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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import com.shelljunkie.alcopop.Configuration;
import com.shelljunkie.alcopop.jmx.RemoteManagementUtility;
import com.shelljunkie.alcopop.pipeline.PipelinedProcessingCoreMBean;
import com.shelljunkie.alcopop.ui.AbstractMainWindow;
import com.shelljunkie.alcopop.ui.MultiEmbeddedWindowPane;
import com.shelljunkie.alcopop.ui.UIConstants;

/**
 * Main window of the PEP Console.
 * 
 * @author Juergen Becker
 */
public class ConsoleMainWindow extends AbstractMainWindow {
	private PipelineConsoleView pipelineConsoleView;
	private ConnectionSelectorWindow connectionSelector;
	private Action disconnectAction;
	private Action pipelineStopAction;

	@Override
	protected String getTitle() {
		return UIConstants.CONSOLE_TITLE;
	}

	@Override
	protected JComponent buildRightPane() {
		pipelineConsoleView = new PipelineConsoleView();
		pipelineConsoleView.getView().setBorder( BorderFactory.createEmptyBorder( 0, 0, 8, 0 ) );
		return pipelineConsoleView.getView();
	}

	@Override
	protected JComponent buildLeftPane() {
		MultiEmbeddedWindowPane pane = new MultiEmbeddedWindowPane();
		pane.setPreferredSize( new Dimension( DEFAULT_LEFT_WIDTH, 10 ) );
		connectionSelector = new ConnectionSelectorWindow( Configuration.NAME_SHORT + " Server", UIConstants.SERVER_SELECTOR_COLOR );
		connectionSelector.addPropertyChangeListener( new ConnectionSelectorListener() );
		pane.addEmbeddedWindow( connectionSelector );
		return pane;
	}

	@Override
	protected JMenuBar buildMenubar() {
		JMenuBar menubar = new JMenuBar();
		menubar.add( Box.createHorizontalStrut( 3 ) );
		menubar.add( buildConnectionMenu() );
		menubar.add( buildPipelineMenu() );
		menubar.add( Box.createHorizontalGlue() );
		menubar.add( buildHelpMenu() );
		menubar.add( Box.createHorizontalStrut( 10 ) );
		return menubar;
	}

	protected JMenu buildConnectionMenu() {
		JMenu menu = new JMenu( "Connection" );
		menu.setMnemonic( KeyEvent.VK_C );

		JMenuItem item = menu.add( new NewConnectionAction() );
		item.setMnemonic( KeyEvent.VK_N );
		item.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_N, ActionEvent.CTRL_MASK ) );

		disconnectAction = new DisconnectAction();
		disconnectAction.setEnabled( false );
		item = menu.add( disconnectAction );
		item.setMnemonic( KeyEvent.VK_D );
		item.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_D, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK ) );

		item = menu.add( new DeleteConnectionAction() );
		item.setMnemonic( KeyEvent.VK_D );
		item.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_D, ActionEvent.CTRL_MASK ) );

		menu.addSeparator();

		item = menu.add( new QuitAction() );
		item.setMnemonic( KeyEvent.VK_Q );
		item.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_Q, ActionEvent.CTRL_MASK ) );
		return menu;
	}

	protected void newConnection() {
		String server = (String) JOptionPane.showInputDialog( getWindow(), "New PEP Server connection:\n" + "Format: [user:password@]server:port",
			"New connection", JOptionPane.PLAIN_MESSAGE, null, null, "localhost:8004" );
		if ( server != null ) {
			Configuration.getInstance().addConnection( server );
			connectionSelector.updateList();
		}
	}

	protected void deleteConnection() {
		String selection = connectionSelector.getSelection();
		if ( selection != null ) {
			int opt = JOptionPane.showConfirmDialog( getWindow(), "Realy delete connection: " + selection + "?", "Delete connection",
				JOptionPane.OK_CANCEL_OPTION );
			if ( opt == JOptionPane.OK_OPTION ) {
				Configuration.getInstance().removeConnection( selection );
				connectionSelector.updateList();
			}
		} else {
			JOptionPane.showMessageDialog( getWindow(), "No connection for deletion selected" );
		}
	}

	protected JMenu buildPipelineMenu() {
		JMenu menu = new JMenu( "Pipeline" );
		menu.setMnemonic( KeyEvent.VK_P );

		pipelineStopAction = new PipelineStopAction();
		pipelineStopAction.setEnabled( false );
		JMenuItem item = menu.add( pipelineStopAction );
		item.setMnemonic( KeyEvent.VK_S );
		item.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_S, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK ) );

		return menu;
	}

	protected void disconnect() {
		disconnectAction.setEnabled( false );
		pipelineStopAction.setEnabled( false );
		pipelineConsoleView.setPipelineDescriptor( null );
		connectionSelector.setSelection( null );
		RemoteManagementUtility.getInstance().disconnect();
	}

	private final class NewConnectionAction extends AbstractAction {
		NewConnectionAction() {
			super( "New..." );
		}

		public void actionPerformed( ActionEvent e ) {
			newConnection();
		}
	}

	private final class DeleteConnectionAction extends AbstractAction {
		DeleteConnectionAction() {
			super( "Delete" );
		}

		public void actionPerformed( ActionEvent e ) {
			deleteConnection();
		}
	}

	private final class PipelineStopAction extends AbstractAction {
		PipelineStopAction() {
			super( "Stop" );
		}

		public void actionPerformed( ActionEvent e ) {
			PipelinedProcessingCoreMBean ppc = RemoteManagementUtility.getInstance().getRemotePipelineProcessingCore();
			if ( ppc != null ) {
				ppc.stop();
				disconnect();
			}
		}
	}

	private final class DisconnectAction extends AbstractAction {
		DisconnectAction() {
			super( "Disconnect" );
		}

		public void actionPerformed( ActionEvent e ) {
			disconnect();
		}
	}

	private final class ConnectionSelectorListener implements PropertyChangeListener {
		public void propertyChange( PropertyChangeEvent evt ) {
			if ( "selection".equals( evt.getPropertyName() ) && evt.getNewValue() != null ) {
				RemoteManagementUtility jcm = RemoteManagementUtility.getInstance();
				String host = (String) evt.getNewValue();
				if ( jcm.connect( host ) ) {
					PipelinedProcessingCoreMBean ppc = jcm.getRemotePipelineProcessingCore();
					if ( ppc != null ) {
						pipelineConsoleView.setPipelineDescriptor( ppc.getPipelineDescriptor() );
						disconnectAction.setEnabled( true );
						pipelineStopAction.setEnabled( true );
					} else {
						JOptionPane.showMessageDialog( getWindow(), "No processing core running!" );
					}
				} else {
					JOptionPane.showMessageDialog( getWindow(), "Failed to connect to " + host );
				}
			}
		}
	}

}
