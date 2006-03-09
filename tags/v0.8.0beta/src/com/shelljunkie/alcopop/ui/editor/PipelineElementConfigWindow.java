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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import com.shelljunkie.alcopop.pipeline.descriptor.PipelineElementDescriptor;

/**
 * @author Juergen Becker
 */
public class PipelineElementConfigWindow {
	private static final int WINDOW_WIDTH = 400;
	private static final int WINDOW_HEIGHT = 300;
	private JFrame parent;
	private JDialog dialog;
	private JTable table;
	private ConfigPropertiesTableModel tableModel;
	private PipelineElementDescriptor pipelineElementDescriptor;

	public PipelineElementConfigWindow( JFrame parent, PipelineElementDescriptor ped ) {
		this.pipelineElementDescriptor = ped;
		dialog = build();
	}

	public JDialog getDialog() {
		return dialog;
	}

	public void show() {
		dialog.setVisible( true );
	}

	public void hide() {
		dialog.setVisible( false );
	}

	public void close() {
		dialog.dispose();
	}

	protected JDialog build() {
		JDialog frame = new JDialog( parent, pipelineElementDescriptor.getName() + " Config", true );
		frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );

		JPanel panel = new JPanel( new BorderLayout( 0, 4 ) );
		panel.add( buildTablePanel(), BorderLayout.CENTER );
		panel.add( buildControlPanel(), BorderLayout.SOUTH );
		panel.setBorder( BorderFactory.createEmptyBorder( 4, 4, 4, 4 ) );
		frame.setContentPane( panel );

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int posX = ( screenSize.width - WINDOW_WIDTH ) / 2;
		int posY = ( screenSize.height - WINDOW_HEIGHT ) / 2;
		frame.setBounds( posX, posY, WINDOW_WIDTH, WINDOW_HEIGHT );
		return frame;
	}

	protected JComponent buildTablePanel() {
		tableModel = new ConfigPropertiesTableModel();
		table = new JTable( tableModel );
		TableColumn valueColumn = table.getColumnModel().getColumn( 1 );
		valueColumn.setCellEditor( new DefaultCellEditor( new JTextField() ) );
		JScrollPane spane = new JScrollPane( table );
		return spane;
	}

	protected JComponent buildControlPanel() {
		Box box = new Box( BoxLayout.X_AXIS );
		box.add( Box.createHorizontalGlue() );
		box.add( new JButton( new CancelAction() ) );
		box.add( Box.createHorizontalStrut( 5 ) );
		box.add( new JButton( new SaveAction() ) );
		box.add( Box.createHorizontalStrut( 25 ) );
		return box;
	}

	private final class CancelAction extends AbstractAction {
		CancelAction() {
			super( "Cancel" );
		}

		public void actionPerformed( ActionEvent e ) {
			close();
		}

	}

	private final class SaveAction extends AbstractAction {
		SaveAction() {
			super( "Save" );
		}

		public void actionPerformed( ActionEvent e ) {
			tableModel.save();
			close();
		}
	}

	private final class ConfigPropertiesTableModel extends AbstractTableModel {
		private final String[] COLUMN_NAMES = { "Property", "Value" };
		private Map<String, String> configProperties;

		public ConfigPropertiesTableModel() {
			super();
			configProperties = new HashMap<String, String>();
			for ( String name : pipelineElementDescriptor.getConfigurationPropertyNames() ) {
				configProperties.put( name, pipelineElementDescriptor.getConfigurationPropertyValue( name ) );
			}
		}

		@Override
		public boolean isCellEditable( int rowIndex, int columnIndex ) {
			return columnIndex == 1;
		}

		@Override
		public String getColumnName( int column ) {
			return COLUMN_NAMES[column];
		}

		public int getRowCount() {
			return pipelineElementDescriptor.getNoOfConfigurationPropertyNames();
		}

		public int getColumnCount() {
			return COLUMN_NAMES.length;
		}

		public Object getValueAt( int rowIndex, int columnIndex ) {
			if ( columnIndex == 0 ) {
				return pipelineElementDescriptor.getConfigurationPropertyName( rowIndex );
			}
			return configProperties.get( pipelineElementDescriptor.getConfigurationPropertyName( rowIndex ) );
		}

		@Override
		public void setValueAt( Object aValue, int rowIndex, int columnIndex ) {
			configProperties.put( pipelineElementDescriptor.getConfigurationPropertyName( rowIndex ), (String) aValue );
		}

		void save() {
			for ( String name : pipelineElementDescriptor.getConfigurationPropertyNames() ) {
				pipelineElementDescriptor.setConfigurationPropertyValue( name, configProperties.get( name ) );
			}
		}

		void update() {
			fireTableDataChanged();
		}
	}

}
