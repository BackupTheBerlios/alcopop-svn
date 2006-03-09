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
public class ForwardAlgorithm {

	public Result calc( final HiddenMarkovModel lambda, final IObservableSequence observationSymbolSequence ) {
		return calc( lambda, observationSymbolSequence, false );
	}

	public Result calc( final HiddenMarkovModel lambda, final IObservableSequence observationSymbolSequence, boolean scale ) {
		int noOfHiddenStates = lambda.getNoOfHiddenStates();
		int noOfObservationSymbols = observationSymbolSequence.size();
		double[][] alpha = new double[noOfObservationSymbols][noOfHiddenStates];
		double[] scaleFactors = null;
		if ( scale ) {
			scaleFactors = new double[noOfObservationSymbols];
		}

		for ( int i = 0; i < noOfHiddenStates; i++ ) {
			alpha[0][i] = lambda.getInitialState( i ) * lambda.getObservationSymbolProbability( i, observationSymbolSequence.getObservable( 0 ) );
		}
		if ( scale ) {
			scaleAlpha( scaleFactors, alpha, 0 );
		}

		for ( int t = 1; t < noOfObservationSymbols; t++ ) {
			for ( int j = 0; j < noOfHiddenStates; j++ ) {
				double sum = 0.0;
				for ( int i = 0; i < noOfHiddenStates; i++ ) {
					sum += alpha[t - 1][i] * lambda.getStateTransitionProbability( i, j );
				}
				alpha[t][j] = sum * lambda.getObservationSymbolProbability( j, observationSymbolSequence.getObservable( t ) );
			}
			if ( scale ) {
				scaleAlpha( scaleFactors, alpha, t );
			}
		}

		double probability = 0.0;
		if ( scale ) {
			for ( int i = 0; i < noOfObservationSymbols; i++ ) {
				probability += Math.log( scaleFactors[i] );
			}
			probability *= -1.0;
		} else {
			for ( int i = 0; i < noOfHiddenStates; i++ ) {
				probability += alpha[noOfObservationSymbols - 1][i];
			}
		}
		return new Result( alpha, probability, scaleFactors );
	}

	protected void scaleAlpha( double[] scaleFactors, double[][] alpha, int time ) {
		double sum = 0.0;
		for ( double d : alpha[time] ) {
			sum += d;
		}
		if ( sum != 0.0 ) {
			scaleFactors[time] = 1 / sum;
			for ( int i = 0; i < alpha[time].length; i++ ) {
				alpha[time][i] /= sum;
			}
		}
	}

	public final static class Result {
		private double alpha[][];
		private double probability;
		private double[] scaleFactors;

		private Result( double[][] alpha, double probability, double[] scaleFactors ) {
			this.alpha = alpha;
			this.probability = probability;
			this.scaleFactors = scaleFactors;
		}

		public double[][] getAlpha() {
			return alpha;
		}

		public double getAlphaValue( int i, int j ) {
			return alpha[i][j];
		}

		public double getProbability() {
			if ( isScaled() ) {
				return Math.exp( probability );
			}
			return probability;
		}

		public double[] getScaleFactors() {
			return scaleFactors;
		}

		public boolean isScaled() {
			return scaleFactors != null;
		}

	}
}
