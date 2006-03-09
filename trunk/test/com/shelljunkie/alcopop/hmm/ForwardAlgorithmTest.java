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

import junit.framework.TestCase;

/**
 * @author Juergen Becker
 */
public class ForwardAlgorithmTest extends TestCase {
	private HiddenMarkovModel lambda;
	private IntegerObservableSequence obsSequence;

	/**
	 * example from http://www.comp.leeds.ac.uk/roger/HiddenMarkovModels/html_dev/forward_algorithm/s3_pg1.html
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		lambda = new HiddenMarkovModel( new double[][] { { 0.5, 0.25, 0.25 }, { 0.375, 0.125, 0.375 }, { 0.125, 0.675, 0.375 } }, new double[][] {
			{ 0.6, 0.2, 0.15, 0.05 }, { 0.25, 0.25, 0.25, 0.25 }, { 0.05, 0.1, 0.35, 0.5 } }, new double[] { 0.63, 0.17, 0.2 } );
		obsSequence = new IntegerObservableSequence();
		obsSequence.add( new Integer( 0 ) );
		obsSequence.add( new Integer( 2 ) );
		obsSequence.add( new Integer( 3 ) );
		obsSequence.add( new Integer( 0 ) );
		obsSequence.add( new Integer( 2 ) );
	}

	/**
	 * Test method for 'com.shelljunkie.alcove.hmm.ForwardAlgorithm.calc(HiddenMarkovModel, int[])'
	 */
	public void testCalc() {
		ForwardAlgorithm fw = new ForwardAlgorithm();
		ForwardAlgorithm.Result result = fw.calc( lambda, obsSequence );

		assertEquals( 0.0017103717895507811, result.getProbability(), 0 );

		assertEquals( 0.378, result.getAlphaValue( 0, 0 ), 0.0 );
		assertEquals( 0.0425, result.getAlphaValue( 0, 1 ), 0.0 );
		assertEquals( 0.010000000000000002, result.getAlphaValue( 0, 2 ), 0.0 );

		assertEquals( 0.030928124999999997, result.getAlphaValue( 1, 0 ), 0.0 );
		assertEquals( 0.026640625, result.getAlphaValue( 1, 1 ), 0.0 );
		assertEquals( 0.039965625, result.getAlphaValue( 1, 2 ), 0.0 );

		assertEquals( 0.0015225, result.getAlphaValue( 2, 0 ), 0.0 );
		assertEquals( 0.009509726562500001, result.getAlphaValue( 2, 1 ), 0.0 );
		assertEquals( 0.0163546875, result.getAlphaValue( 2, 2 ), 0.0 );

		assertEquals( 0.0038230400390625, result.getAlphaValue( 3, 0 ), 0.0 );
		assertEquals( 0.003152188720703125, result.getAlphaValue( 3, 1 ), 0.0 );
		assertEquals( 5.03989013671875E-4, result.getAlphaValue( 3, 2 ), 0.0 );

		assertEquals( 4.734884124755859E-4, result.getAlphaValue( 4, 0 ), 0.0 );
		assertEquals( 4.224940460205078E-4, result.getAlphaValue( 4, 1 ), 0.0 );
		assertEquals( 8.143893310546874E-4, result.getAlphaValue( 4, 2 ), 0.0 );
	}

	public void testCalcScaled() {
		ForwardAlgorithm fw = new ForwardAlgorithm();
		ForwardAlgorithm.Result result = fw.calc( lambda, obsSequence, true );

		System.out.println( result.getProbability() );
		assertEquals( 0.0017103717895507811, result.getProbability(), 1.0E-10 );
	}

}
