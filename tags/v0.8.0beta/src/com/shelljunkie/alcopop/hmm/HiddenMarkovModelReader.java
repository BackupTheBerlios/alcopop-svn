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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.shelljunkie.alcopop.logging.LoggerManager;

/**
 * @author Juergen Becker
 */
public class HiddenMarkovModelReader {

	public HiddenMarkovModel read( String filename ) {
		try {
			BufferedReader reader = new BufferedReader( new FileReader( filename ) );
			double[][] A = readMatrix( reader );
			double[][] B = readMatrix( reader );
			double[] pi = readVector( reader );
			reader.close();
			return new HiddenMarkovModel( A, B, pi );
		} catch ( Exception e ) {
			LoggerManager.getInstance().getDefaultLogger().error( "reading hmm failed", e );
		}
		return null;
	}

	protected double[][] readMatrix( BufferedReader reader ) throws NumberFormatException, IOException {
		int rows = Integer.parseInt( reader.readLine() );
		int cols = Integer.parseInt( reader.readLine() );
		double[][] matrix = new double[rows][cols];
		for ( int r = 0; r < rows; r++ ) {
			String[] parts = reader.readLine().split( " " );
			if ( parts.length != cols ) {
				LoggerManager.getInstance().getDefaultLogger().error( "bad hmm file. not enough values in line." );
				return null;
			}
			for ( int c = 0; c < cols; c++ ) {
				matrix[r][c] = Double.parseDouble( parts[c] );
			}
		}
		return matrix;
	}

	protected double[] readVector( BufferedReader reader ) throws NumberFormatException, IOException {
		int cols = Integer.parseInt( reader.readLine() );
		double[] vector = new double[cols];
		String[] parts = reader.readLine().split( " " );
		if ( parts.length != cols ) {
			LoggerManager.getInstance().getDefaultLogger().error( "bad hmm file. not enough values in line." );
			return null;
		}
		for ( int c = 0; c < cols; c++ ) {
			vector[c] = Double.parseDouble( parts[c] );
		}
		return vector;
	}
}
