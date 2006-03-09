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

import java.util.Random;

/**
 * @author Juergen Becker
 */
public class HiddenMarkovModel {
	public double[] pi;
	public double[][] A;
	public double[][] B;

	public HiddenMarkovModel( double[][] A, double[][] B, double[] pi ) {
		assert A.length == B.length;
		assert A.length == pi.length;
		this.A = A;
		this.B = B;
		this.pi = pi;
	}

	public HiddenMarkovModel( int noOfHiddenStates, int noOfObervationSymbols, boolean init ) {
		pi = new double[noOfHiddenStates];
		A = new double[noOfHiddenStates][noOfHiddenStates];
		B = new double[noOfHiddenStates][noOfObervationSymbols];
		if ( init ) {
			init();
		}
	}

	public int getNoOfHiddenStates() {
		return pi.length;
	}

	public int getNoOfObservableSymbols() {
		if ( B != null ) {
			return B[0].length;
		}
		return 0;
	}

	public double getInitialState( int index ) {
		return pi[index];
	}

	public double getStateTransitionProbability( int stateOne, int stateTwo ) {
		return A[stateOne][stateTwo];
	}

	public double getObservationSymbolProbability( int state, int symbol ) {
		return B[state][symbol];
	}

	protected void init() {
		// randomStochasticMatrixFill( A );
		uniformStochasticMatrixFill( A );
		randomStochasticMatrixFill( B );
		uniformStochasticVectorFill( pi );
	}

	protected static void uniformStochasticMatrixFill( double[][] matrix ) {
		// uniform a_ij an pi_i
		for ( int i = 0; i < matrix.length; i++ ) {
			double val = 1.0 / matrix[i].length;
			for ( int j = 0; j < matrix[i].length; j++ ) {
				matrix[i][j] = val;
			}
		}
	}

	protected static void uniformStochasticVectorFill( double[] vector ) {
		// uniform a_ij an pi_i
		double val = 1.0 / vector.length;
		for ( int j = 0; j < vector.length; j++ ) {
			vector[j] = val;
		}
	}

	protected static void randomStochasticMatrixFill( double[][] matrix ) {
		// random values for b_ij, but normalized to 1
		Random ran = new Random( System.currentTimeMillis() );
		for ( int i = 0; i < matrix.length; i++ ) {
			double sum = 0.0;
			for ( int j = 0; j < matrix[i].length; j++ ) {
				double val = ran.nextDouble();
				sum += val;
				matrix[i][j] = val;
			}
			for ( int j = 0; j < matrix[i].length; j++ ) {
				matrix[i][j] /= sum;
			}
		}

	}
}
