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

/**
 * @author Juergen Becker
 */
public class BackwardAlgorithm {

	public Result calc( final HiddenMarkovModel lambda, final IObservableSequence observationSymbolSequence ) {
		return calc( lambda, observationSymbolSequence, null );
	}

	public Result calc( final HiddenMarkovModel lambda, final IObservableSequence observationSymbolSequence, double[] scaleFactors ) {
		int noOfHiddenStates = lambda.getNoOfHiddenStates();
		int noOfObservationSymbols = observationSymbolSequence.size();
		double beta[][] = new double[noOfObservationSymbols][noOfHiddenStates];

		for ( int i = 0; i < noOfHiddenStates; i++ ) {
			beta[noOfObservationSymbols - 1][i] = 1;
		}
		scaleBeta( beta, scaleFactors, noOfObservationSymbols - 1 );

		for ( int t = noOfObservationSymbols - 2; t >= 0; t-- ) {
			for ( int i = 0; i < noOfHiddenStates; i++ ) {
				double sum = 0.0;
				for ( int j = 0; j < noOfHiddenStates; j++ ) {
					sum += lambda.A[i][j] * lambda.B[j][observationSymbolSequence.getObservable( t + 1 )] * beta[t + 1][j];
				}
				beta[t][i] = sum;
			}
			scaleBeta( beta, scaleFactors, t );
		}
		return new Result( beta );
	}

	protected void scaleBeta( double[][] beta, double[] scaleFactors, int time ) {
		if ( scaleFactors != null ) {
			for ( int i = 0; i < beta[time].length; i++ ) {
				beta[time][i] *= scaleFactors[time];
			}
		}
	}

	public final static class Result {
		private double beta[][];

		private Result( double[][] beta ) {
			this.beta = beta;
		}

		public double[][] getBeta() {
			return beta;
		}

		public double getBetaValue( int i, int j ) {
			return beta[i][j];
		}
	}
}
