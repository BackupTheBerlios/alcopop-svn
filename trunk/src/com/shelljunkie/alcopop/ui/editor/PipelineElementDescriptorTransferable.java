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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import com.shelljunkie.alcopop.logging.LoggerManager;
import com.shelljunkie.alcopop.pipeline.descriptor.PipelineElementDescriptor;

/**
 * @author Juergen Becker
 */
public class PipelineElementDescriptorTransferable implements Transferable {
	private static final String MIMETYPE = DataFlavor.javaJVMLocalObjectMimeType
		+ ";class=com.shelljunkie.alcopop.pipeline.descriptor.PipelineElementDescriptor";
	public static DataFlavor FLAVOR;
	private static DataFlavor[] flavors;
	private PipelineElementDescriptor ped;

	static {
		try {
			FLAVOR = new DataFlavor( MIMETYPE );
			flavors = new DataFlavor[] { FLAVOR };
		} catch ( ClassNotFoundException e ) {
			LoggerManager.getInstance().getDefaultLogger().error( "DnD data flavor init failed", e );
		}

	}

	public PipelineElementDescriptorTransferable( PipelineElementDescriptor ped ) {
		super();
		this.ped = ped;
	}

	public DataFlavor[] getTransferDataFlavors() {
		return flavors;
	}

	public boolean isDataFlavorSupported( DataFlavor flavor ) {
		return FLAVOR.equals( flavor );
	}

	public Object getTransferData( DataFlavor flavor ) throws UnsupportedFlavorException, IOException {
		return ped;
	}

}
