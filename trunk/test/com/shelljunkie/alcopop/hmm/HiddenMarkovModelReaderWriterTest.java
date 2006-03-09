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

import java.io.File;

import junit.framework.TestCase;

/**
 * @author Juergen Becker
 */
public class HiddenMarkovModelReaderWriterTest extends TestCase {
	private static final String FILENAME = "test.hmm";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		File f = new File( FILENAME );
		if ( f.exists() ) {
			f.delete();
		}
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		File f = new File( FILENAME );
		if ( f.exists() ) {
			f.delete();
		}
	}

	/**
	 * Test method for 'com.shelljunkie.alcove.hmm.HiddenMarkovModelReader.read(String)'
	 */
	public void testWriteRead() {
		HiddenMarkovModel hmm = new HiddenMarkovModel( new double[][] { { 0.5, 0.25, 0.25 }, { 0.375, 0.125, 0.375 }, { 0.125, 0.675, 0.375 } },
			new double[][] { { 0.6, 0.2, 0.15, 0.05 }, { 0.25, 0.25, 0.25, 0.25 }, { 0.05, 0.1, 0.35, 0.5 } }, new double[] { 0.63, 0.17, 0.2 } );

		HiddenMarkovModelWriter writer = new HiddenMarkovModelWriter();
		assertTrue( writer.write( hmm, FILENAME ) );

		HiddenMarkovModelReader reader = new HiddenMarkovModelReader();
		HiddenMarkovModel hmmRead = reader.read( FILENAME );
		assertEquals( hmm.A[0][0], hmmRead.A[0][0], 0 );
		assertEquals( hmm.B[0][0], hmmRead.B[0][0], 0 );
		assertEquals( hmm.pi[0], hmmRead.pi[0], 0 );
	}

}
