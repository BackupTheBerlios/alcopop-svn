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

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.shelljunkie.alcopop.jmx.RemoteManagementUtility;
import com.shelljunkie.alcopop.pipeline.descriptor.PipelineElementDescriptor;
import com.shelljunkie.alcopop.ui.view.AbstractView;

/**
 * @author Juergen Becker
 */
public class DefaultMonitorView extends AbstractView implements IMonitorView {
	private JTable table;
	private AttributeTableModel tableModel;
	private PipelineElementDescriptor ped;
	private String[] attributeNames;
	private RemoteManagementUtility rmu;

	@Override
	public void initialize( Object data ) {
		super.initialize( data );
		ped = (PipelineElementDescriptor) data;
		rmu = RemoteManagementUtility.getInstance();
		attributeNames = rmu.getAttributeNames( ped );
	}

	public void update() {
		tableModel.update();
	}

	@Override
	protected JComponent build() {
		tableModel = new AttributeTableModel();
		table = new JTable( tableModel );
		JScrollPane spane = new JScrollPane( table );
		return spane;
	}

	private final class AttributeTableModel extends AbstractTableModel {
		private final String[] COLUMN_NAMES = { "Attribute", "Value" };

		@Override
		public String getColumnName( int column ) {
			return COLUMN_NAMES[column];
		}

		public int getRowCount() {
			return attributeNames.length;
		}

		public int getColumnCount() {
			return COLUMN_NAMES.length;
		}

		public Object getValueAt( int rowIndex, int columnIndex ) {
			if ( columnIndex == 0 ) {
				return attributeNames[rowIndex];
			}
			return rmu.getAttributeValue( ped, attributeNames[rowIndex] );
		}

		void update() {
			fireTableDataChanged();
		}
	}

}
