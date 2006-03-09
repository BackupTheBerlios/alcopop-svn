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
package com.shelljunkie.alcopop.sink;

import java.util.List;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.shelljunkie.alcopop.jmx.RemoteManagementUtility;
import com.shelljunkie.alcopop.pipeline.descriptor.PipelineElementDescriptor;
import com.shelljunkie.alcopop.ui.console.IMonitorView;
import com.shelljunkie.alcopop.ui.view.AbstractView;

/**
 * @author Juergen Becker
 */
public class ConsoleLoggerSinkView extends AbstractView implements IMonitorView {
	private JTextArea textarea;
	private PipelineElementDescriptor ped;

	@Override
	public void initialize( Object data ) {
		super.initialize( data );
		ped = (PipelineElementDescriptor) data;
	}

	@SuppressWarnings("unchecked")
	public void update() {
		textarea.setText( "" );
		List<String> lines = (List<String>) RemoteManagementUtility.getInstance().getAttributeValue( ped, "ConsoleLines" );
		if ( lines != null ) {
			for ( String line : lines ) {
				textarea.append( line + "\n" );
			}
		} else {
			textarea.append( "no output\n" );
		}
	}

	@Override
	protected JComponent build() {
		textarea = new JTextArea();
		textarea.setEditable( false );
		JScrollPane spane = new JScrollPane( textarea );
		return spane;
	}

}
