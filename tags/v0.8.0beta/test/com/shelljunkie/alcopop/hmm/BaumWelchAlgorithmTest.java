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

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

/**
 * @author Juergen Becker
 */
public class BaumWelchAlgorithmTest extends TestCase {
	private BaumWelchAlgorithm bwAlgo;
	private List<IObservableSequence> lernSequences;
	private IntegerObservableSequence lernSequence;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		bwAlgo = new BaumWelchAlgorithm();
		lernSequence = new IntegerObservableSequence();
		lernSequence.add( 3 );
		lernSequence.add( 1 );
		lernSequence.add( 2 );
		lernSequence.add( 3 );
		lernSequence.add( 0 );
		lernSequences = new ArrayList<IObservableSequence>( 1 );
		lernSequences.add( lernSequence );
	}

	/**
	 * Test method for 'com.shelljunkie.alcove.hmm.BaumWelchAlgorithm.estimate(int, int, int[], int, double)'
	 */
	public void testEstimateIntIntIntArrayIntDouble() {
		HiddenMarkovModel hmm = bwAlgo.estimate( 3, 4, lernSequences, 100 );

		System.out.println( "A" );
		printMatrix( hmm.A );
		System.out.println( "---\nB" );
		printMatrix( hmm.B );
		System.out.println( "---\npi" );
		printVector( hmm.pi );
		System.out.println( "---" );
	}

	/**
	 * Test method for 'com.shelljunkie.alcove.hmm.BaumWelchAlgorithm.estimate(HiddenMarkovModel, int[], int, double)'
	 */
	public void testEstimateHiddenMarkovModelIntArrayIntDouble() {
		ForwardAlgorithm fw = new ForwardAlgorithm();
		HiddenMarkovModel orgHMM = new HiddenMarkovModel( new double[][] { { 0.5, 0.25, 0.25 }, { 0.375, 0.125, 0.375 }, { 0.125, 0.675, 0.375 } },
			new double[][] { { 0.6, 0.2, 0.15, 0.05 }, { 0.25, 0.25, 0.25, 0.25 }, { 0.05, 0.1, 0.35, 0.5 } }, new double[] { 0.63, 0.17, 0.2 } );

		double startProp = fw.calc( orgHMM, lernSequence ).getProbability();
		System.out.println( "start prop: " + startProp );
		HiddenMarkovModel hmm = bwAlgo.estimate( orgHMM, lernSequences, 100 );
		double stopProp = fw.calc( hmm, lernSequence ).getProbability();

		System.out.println( "stop prop: " + stopProp );
		System.out.println( "A" );
		printMatrix( hmm.A );
		System.out.println( "---\nB" );
		printMatrix( hmm.B );
		System.out.println( "---\npi" );
		printVector( hmm.pi );

		assertTrue( stopProp > startProp );
		System.out.println( "*********************" );
		startProp = fw.calc( orgHMM, lernSequence, true ).getProbability();
		System.out.println( "start prop: " + startProp );
		bwAlgo.setScale( true );
		HiddenMarkovModel hmmScaled = bwAlgo.estimate( orgHMM, lernSequences, 100 );
		stopProp = fw.calc( hmmScaled, lernSequence, true ).getProbability();
		System.out.println( "stop prop: " + stopProp );

		System.out.println( "Scaled ---" );
		System.out.println( "A" );
		printMatrix( hmmScaled.A );
		System.out.println( "---\nB" );
		printMatrix( hmmScaled.B );
		System.out.println( "---\npi" );
		printVector( hmmScaled.pi );

		assertTrue( stopProp > startProp );

		assertEquals( hmm.A[0][0], hmmScaled.A[0][0], 1E-2 );
		assertEquals( hmm.B[0][0], hmmScaled.B[0][0], 1E-2 );
		assertEquals( hmm.pi[0], hmmScaled.pi[0], 1E-2 );
	}

	private void printMatrix( double[][] a ) {
		for ( int i = 0; i < a.length; i++ ) {
			for ( int j = 0; j < a[i].length; j++ ) {
				System.out.print( a[i][j] + "\t" );
			}
			System.out.println();
		}
	}

	private void printVector( double[] a ) {
		for ( int i = 0; i < a.length; i++ ) {
			System.out.print( a[i] + "\t" );
		}
		System.out.println();
	}

}
