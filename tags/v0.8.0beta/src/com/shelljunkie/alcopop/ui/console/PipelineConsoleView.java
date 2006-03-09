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

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import com.shelljunkie.alcopop.pipeline.descriptor.PipelineDescriptor;
import com.shelljunkie.alcopop.pipeline.descriptor.PipelineElementDescriptor;
import com.shelljunkie.alcopop.ui.EmbeddedWindow;
import com.shelljunkie.alcopop.ui.PipelineElementView;
import com.shelljunkie.alcopop.ui.PipelineView;
import com.shelljunkie.alcopop.ui.view.IView;
import com.shelljunkie.alcopop.ui.view.ViewFactory;

/**
 * @author Juergen Becker
 */
public class PipelineConsoleView {
	private static final String DEFAULT_TITLE = "not connected";
	private EmbeddedWindow window;
	private PipelineView pipelineView;
	private Map<String, MonitorWindow> views;

	public PipelineConsoleView() {
		views = new HashMap<String, MonitorWindow>();
		build();
	}

	public JComponent getView() {
		return window;
	}

	public void setPipelineDescriptor( PipelineDescriptor pipelineDescr ) {
		pipelineView.setPipelineDescriptor( pipelineDescr );
		if ( pipelineDescr != null ) {
			window.setTitle( pipelineDescr.getName() );
		} else {
			window.setTitle( DEFAULT_TITLE );
			for ( MonitorWindow win : views.values() ) {
				win.close();
			}
			views = new HashMap<String, MonitorWindow>();
		}
	}

	protected void build() {
		window = new EmbeddedWindow( DEFAULT_TITLE );
		window.setTitleBackground( new Color( 65, 80, 175 ) );

		pipelineView = new PipelineView();
		JScrollPane pane = new JScrollPane( pipelineView.getView() );
		pane.setBorder( null );
		window.setContentPane( pane );

		MyMouseListener ml = new MyMouseListener();
		pipelineView.addMouseListener( ml );
	}

	protected void showInfoWindow( PipelineElementDescriptor ped ) {
		MonitorWindow win = views.get( ped.getID() );
		if ( win == null ) {
			IView view = ViewFactory.getInstance().createView( ped.getName() );
			if ( view == null || !( view instanceof IMonitorView ) ) {
				view = new DefaultMonitorView();
			}
			view.initialize( ped );
			win = new MonitorWindow( ped.getName(), (IMonitorView) view );
			views.put( ped.getID(), win );
		}
		win.show();
	}

	private final class MyMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked( MouseEvent e ) {
			if ( e.getClickCount() == 2 ) {
				Component target = pipelineView.getComponentAt( e.getPoint() );
				if ( target != null && target instanceof PipelineElementView ) {
					showInfoWindow( ( (PipelineElementView) target ).getElementDescriptor() );
				}
			}
		}
	}

}
