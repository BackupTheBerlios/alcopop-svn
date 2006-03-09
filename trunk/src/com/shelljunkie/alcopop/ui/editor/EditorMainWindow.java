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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import com.shelljunkie.alcopop.pipeline.descriptor.IPipelineDescriptorProvider;
import com.shelljunkie.alcopop.pipeline.descriptor.PipelineDescriptor;
import com.shelljunkie.alcopop.pipeline.descriptor.PipelineDescriptorValidator;
import com.shelljunkie.alcopop.pipeline.descriptor.PipelineDescriptorValidatorException;
import com.shelljunkie.alcopop.pipeline.descriptor.PipelineElementDescriptor;
import com.shelljunkie.alcopop.pipeline.descriptor.XMLPipelineDescriptorReader;
import com.shelljunkie.alcopop.pipeline.descriptor.XMLPipelineDescriptorWriter;
import com.shelljunkie.alcopop.ui.AbstractMainWindow;
import com.shelljunkie.alcopop.ui.MultiEmbeddedWindowPane;
import com.shelljunkie.alcopop.ui.UIConstants;

/**
 * @author Juergen Becker
 */
public class EditorMainWindow extends AbstractMainWindow {
	private PipelineEditView pipelineEditView;

	@Override
	protected String getTitle() {
		return UIConstants.EDITOR_TITLE;
	}

	@Override
	protected JMenuBar buildMenubar() {
		JMenuBar menubar = new JMenuBar();
		menubar.add( Box.createHorizontalStrut( 3 ) );
		menubar.add( buildPipelineMenu() );
		menubar.add( Box.createHorizontalGlue() );
		menubar.add( buildHelpMenu() );
		menubar.add( Box.createHorizontalStrut( 10 ) );
		return menubar;
	}

	protected JMenu buildPipelineMenu() {
		JMenu menu = new JMenu( "Pipeline" );
		menu.setMnemonic( KeyEvent.VK_F );

		JMenuItem item = menu.add( new PipelineNewAction() );
		item.setMnemonic( KeyEvent.VK_N );
		item.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_N, ActionEvent.CTRL_MASK ) );

		item = menu.add( new PipelineOpenAction() );
		item.setMnemonic( KeyEvent.VK_O );
		item.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_O, ActionEvent.CTRL_MASK ) );

		item = menu.add( new PipelineSaveAction() );
		item.setMnemonic( KeyEvent.VK_S );
		item.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_S, ActionEvent.CTRL_MASK ) );

		item = menu.add( new PipelineSaveAsAction() );
		item.setMnemonic( KeyEvent.VK_S );
		item.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_S, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK ) );

		item = menu.add( new PipelineRenameAction() );
		item.setMnemonic( KeyEvent.VK_R );
		item.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_R, ActionEvent.CTRL_MASK ) );

		menu.addSeparator();
		item = menu.add( new QuitAction() );
		item.setMnemonic( KeyEvent.VK_Q );
		item.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_Q, ActionEvent.CTRL_MASK ) );

		return menu;
	}

	@Override
	protected JComponent buildLeftPane() {
		MultiEmbeddedWindowPane pane = new MultiEmbeddedWindowPane();
		pane.setPreferredSize( new Dimension( DEFAULT_LEFT_WIDTH, 10 ) );
		pane.addEmbeddedWindow( new PipelineElementSelectorWindow( "pep.sources", "Sources", PipelineElementDescriptor.TYPE_SOURCE, UIConstants.SOURCE_COLOR ) );
		pane.addEmbeddedWindow( new PipelineElementSelectorWindow( "pep.filters", "Filters", PipelineElementDescriptor.TYPE_FILTER, UIConstants.FILTER_COLOR ) );
		pane.addEmbeddedWindow( new PipelineElementSelectorWindow( "pep.sinks", "Sinks", PipelineElementDescriptor.TYPE_SINK, UIConstants.SINK_COLOR ) );
		return pane;
	}

	@Override
	protected JComponent buildRightPane() {
		pipelineEditView = new PipelineEditView( getWindow() );
		pipelineEditView.setPipelineDescriptor( new PipelineDescriptor( "no name" ) );
		pipelineEditView.getView().setBorder( BorderFactory.createEmptyBorder( 0, 0, 8, 0 ) );
		return pipelineEditView.getView();
	}

	protected JFileChooser getFileChooser() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter( new FileFilter() {
			@Override
			public boolean accept( File f ) {
				return f.isDirectory() || f.getName().toLowerCase().endsWith( "xml" );
			}

			@Override
			public String getDescription() {
				return "xml file filter";
			}
		} );
		chooser.setMultiSelectionEnabled( false );
		return chooser;
	}

	protected void pipelineNew() {
		String pipelineName = (String) JOptionPane.showInputDialog( getWindow(), "New Pipeline name:\n", "Pipeline name", JOptionPane.PLAIN_MESSAGE, null,
			null, "" );
		if ( pipelineName != null ) {
			pipelineEditView.setPipelineDescriptor( new PipelineDescriptor( pipelineName ) );
		}
	}

	protected void pipelineRename() {
		String pipelineName = (String) JOptionPane.showInputDialog( getWindow(), "New Pipeline name:\n", "Pipeline name", JOptionPane.PLAIN_MESSAGE, null,
			null, pipelineEditView.getPipelineDescriptor().getName() );
		if ( pipelineName != null ) {
			pipelineEditView.getPipelineDescriptor().setName( pipelineName );
		}
	}

	protected void pipelineSave() {
		PipelineDescriptor pd = pipelineEditView.getPipelineDescriptor();
		if ( pd.getFilename() != null ) {
			PipelineDescriptorValidator validator = new PipelineDescriptorValidator();
			try {
				validator.validate( pd );
				savePipeline( pd, new File( pd.getFilename() ) );
			} catch ( PipelineDescriptorValidatorException pex ) {
				JOptionPane.showMessageDialog( getWindow(), "Pipeline is not valid: " + pex.getMessage() );
			}
		} else {
			pipelineSaveAs();
		}
	}

	protected void savePipeline( PipelineDescriptor pd, File file ) {
		XMLPipelineDescriptorWriter writer = new XMLPipelineDescriptorWriter( file );
		if ( writer.write( pd ) ) {
			pd.setFilename( file.getAbsolutePath() );
			JOptionPane.showMessageDialog( getWindow(), "Pipeline saved to: " + file.getName() );
		} else {
			JOptionPane.showMessageDialog( getWindow(), "Failed to save Pipeline  to: " + file.getName() );
		}
	}

	protected void pipelineSaveAs() {
		PipelineDescriptor pd = pipelineEditView.getPipelineDescriptor();
		PipelineDescriptorValidator validator = new PipelineDescriptorValidator();
		try {
			validator.validate( pd );

			JFileChooser chooser = getFileChooser();
			if ( chooser.showSaveDialog( getWindow() ) == JFileChooser.APPROVE_OPTION ) {
				savePipeline( pd, chooser.getSelectedFile() );
			}
		} catch ( PipelineDescriptorValidatorException pex ) {
			JOptionPane.showMessageDialog( getWindow(), "Pipeline is not valid: " + pex.getMessage() );
		}
	}

	protected void pipelineOpen() {
		JFileChooser chooser = getFileChooser();
		if ( chooser.showOpenDialog( getWindow() ) == JFileChooser.APPROVE_OPTION ) {
			IPipelineDescriptorProvider pdp = new XMLPipelineDescriptorReader( chooser.getSelectedFile() );
			pipelineEditView.setPipelineDescriptor( pdp.getDescriptor() );
		}
	}

	private final class PipelineNewAction extends AbstractAction {
		PipelineNewAction() {
			super( "New..." );
		}

		public void actionPerformed( ActionEvent e ) {
			pipelineNew();
		}
	}

	private final class PipelineRenameAction extends AbstractAction {
		PipelineRenameAction() {
			super( "Rename..." );
		}

		public void actionPerformed( ActionEvent e ) {
			pipelineRename();
		}
	}

	private final class PipelineOpenAction extends AbstractAction {
		PipelineOpenAction() {
			super( "Open..." );
		}

		public void actionPerformed( ActionEvent e ) {
			pipelineOpen();
		}
	}

	private final class PipelineSaveAction extends AbstractAction {
		PipelineSaveAction() {
			super( "Save" );
		}

		public void actionPerformed( ActionEvent e ) {
			pipelineSave();
		}
	}

	private final class PipelineSaveAsAction extends AbstractAction {
		PipelineSaveAsAction() {
			super( "Save as..." );
		}

		public void actionPerformed( ActionEvent e ) {
			pipelineSaveAs();
		}
	}

}
