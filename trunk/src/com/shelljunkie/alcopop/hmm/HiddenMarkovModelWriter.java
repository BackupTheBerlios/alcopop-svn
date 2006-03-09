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
package com.shelljunkie.alcopop.hmm;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import com.shelljunkie.alcopop.logging.LoggerManager;

/**
 * @author Juergen Becker
 */
public class HiddenMarkovModelWriter {

	public boolean write( HiddenMarkovModel hmm, String filename ) {
		try {
			BufferedWriter writer = new BufferedWriter( new FileWriter( filename ) );
			writeMatrix( hmm.A, writer );
			writeMatrix( hmm.B, writer );
			writeVector( hmm.pi, writer );
			writer.close();
			return true;
		} catch ( IOException excep ) {
			LoggerManager.getInstance().getDefaultLogger().error( "failed to write hmm", excep );
		}
		return false;
	}

	protected void writeMatrix( double[][] matrix, BufferedWriter writer ) throws IOException {
		writer.write( String.valueOf( matrix.length ) );
		writer.newLine();
		writer.write( String.valueOf( matrix[0].length ) );
		writer.newLine();
		for ( double[] row : matrix ) {
			for ( double value : row ) {
				writer.write( String.valueOf( value ) + " " );
			}
			writer.newLine();
		}
	}

	protected void writeVector( double[] vector, BufferedWriter writer ) throws IOException {
		writer.write( String.valueOf( vector.length ) );
		writer.newLine();
		for ( double value : vector ) {
			writer.write( String.valueOf( value ) + " " );
		}
		writer.newLine();
	}

}
