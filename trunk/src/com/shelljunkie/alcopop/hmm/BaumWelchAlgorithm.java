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

import java.util.List;

/**
 * @author Juergen Becker
 */
public class BaumWelchAlgorithm {
	private final static int DEFAULT_NO_OF_ITERATIONS = 20;
	private ForwardAlgorithm forwardAlgo;
	private BackwardAlgorithm backwardAlgo;
	private boolean scale = true;

	public BaumWelchAlgorithm() {
		forwardAlgo = new ForwardAlgorithm();
		backwardAlgo = new BackwardAlgorithm();
	}

	public boolean isScale() {
		return scale;
	}

	public void setScale( boolean scale ) {
		this.scale = scale;
	}

	public HiddenMarkovModel estimate( int noOfHiddenStates, int noOfObervationSymbols, List<IObservableSequence> observationSymbolSequences, int maxIterations ) {
		HiddenMarkovModel lambda = new HiddenMarkovModel( noOfHiddenStates, noOfObervationSymbols, true );
		return estimate( lambda, observationSymbolSequences, maxIterations );
	}

	public HiddenMarkovModel estimate( int noOfHiddenStates, int noOfObervationSymbols, List<IObservableSequence> observationSymbolSequences ) {
		return estimate( noOfHiddenStates, noOfObervationSymbols, observationSymbolSequences, DEFAULT_NO_OF_ITERATIONS );
	}

	public HiddenMarkovModel estimate( final HiddenMarkovModel lambda, List<IObservableSequence> observationSymbolSequences ) {
		return estimate( lambda, observationSymbolSequences, DEFAULT_NO_OF_ITERATIONS );
	}

	public HiddenMarkovModel estimate( final HiddenMarkovModel lambda, List<IObservableSequence> observationSymbolSequences, int maxIterations ) {
		HiddenMarkovModel lambdaOpt = lambda;
		double lastProbability = 0.0;
		for ( int i = 0; i < maxIterations; i++ ) {
			HiddenMarkovModel lambdaNew = new HiddenMarkovModel( lambdaOpt.getNoOfHiddenStates(), lambdaOpt.getNoOfObservableSymbols(), false );
			iterate( lambdaOpt, lambdaNew, observationSymbolSequences );
			// test( lambda );

			double probability = 0.0;
			for ( IObservableSequence observationSymbolSequence : observationSymbolSequences ) {
				probability += forwardAlgo.calc( lambdaNew, observationSymbolSequence, scale ).getProbability();
			}

			System.out.println( i + ": prop " + probability );
			if ( probability <= lastProbability ) {
				break;
			}
			lambdaOpt = lambdaNew;
			lastProbability = probability;
		}
		return lambdaOpt;
	} /*
		 * protected void test( HiddenMarkovModel hmm ) { System.out.println( "A" ); checkStochasticMatrix( hmm.A ); System.out.println( "B" );
		 * checkStochasticMatrix( hmm.B ); }
		 * 
		 * protected void checkStochasticMatrix( double[][] mat ) { for ( int i = 0; i < mat.length; i++ ) { double sum = 0.0; for ( int j = 0; j <
		 * mat[i].length; j++ ) { sum += mat[i][j]; } System.out.print( sum + " " ); } System.out.println(); }
		 */

	protected void iterate( final HiddenMarkovModel lambda, HiddenMarkovModel lambdaNew, List<IObservableSequence> observationSymbolSequences ) {
		int noOfHiddenStates = lambda.getNoOfHiddenStates();
		int noOfObervationSymbols = lambda.getNoOfObservableSymbols();

		double[][] A_nom = new double[noOfHiddenStates][noOfHiddenStates];
		double[] A_denom = new double[noOfHiddenStates];

		double[][] B_nom = new double[noOfHiddenStates][noOfObervationSymbols];
		double[][] B_denom = new double[noOfHiddenStates][noOfObervationSymbols];

		for ( IObservableSequence observationSymbolSequence : observationSymbolSequences ) {
			double[][][] xi = estimateXi( lambda, observationSymbolSequence );
			double[][] gamma = estimateGamma( xi );
			int sequenceSize = observationSymbolSequence.size();

			// estimate a_ij
			for ( int i = 0; i < noOfHiddenStates; i++ ) {
				for ( int t = 0; t < sequenceSize - 1; t++ ) {
					A_denom[i] += gamma[t][i];
				}
				for ( int j = 0; j < noOfHiddenStates; j++ ) {
					for ( int t = 0; t < sequenceSize - 2; t++ ) {
						A_nom[i][j] += xi[t][i][j];
					}
				}
			}

			// estimate b_ij
			for ( int i = 0; i < noOfHiddenStates; i++ ) {
				for ( int j = 0; j < noOfObervationSymbols; j++ ) {
					for ( int t = 0; t < sequenceSize; t++ ) {
						if ( j == observationSymbolSequence.getObservable( t ) ) {
							B_nom[i][j] += gamma[t][i];
						}
						B_denom[i][j] += gamma[t][i];
					}
				}
			}

			// estimate p_i
			for ( int i = 0; i < noOfHiddenStates; i++ ) {
				lambdaNew.pi[i] += gamma[0][i];
			}
		}

		// estimate a_ij
		for ( int i = 0; i < noOfHiddenStates; i++ ) {
			for ( int j = 0; j < noOfHiddenStates; j++ ) {
				lambdaNew.A[i][j] = A_nom[i][j] / A_denom[i];
			}
		}

		// estimate b_ij
		for ( int i = 0; i < noOfHiddenStates; i++ ) {
			for ( int j = 0; j < noOfObervationSymbols; j++ ) {
				lambdaNew.B[i][j] = B_nom[i][j] / B_denom[i][j];
			}
		}

		// estimate p_i
		for ( int i = 0; i < noOfHiddenStates; i++ ) {
			lambdaNew.pi[i] /= observationSymbolSequences.size();
		}
	}

	protected double[][][] estimateXi( final HiddenMarkovModel lambda, final IObservableSequence observationSymbolSequence ) {
		int noOfHiddenStates = lambda.getNoOfHiddenStates();
		double[][][] xi = new double[observationSymbolSequence.size() - 1][noOfHiddenStates][noOfHiddenStates];

		ForwardAlgorithm.Result forwardResult = forwardAlgo.calc( lambda, observationSymbolSequence, scale );
		double[][] alpha = forwardResult.getAlpha();
		double[][] beta = backwardAlgo.calc( lambda, observationSymbolSequence, forwardResult.getScaleFactors() ).getBeta();
		for ( int t = 0; t < xi.length; t++ ) {
			for ( int i = 0; i < noOfHiddenStates; i++ ) {
				for ( int j = 0; j < noOfHiddenStates; j++ ) {
					xi[t][i][j] = alpha[t][i] * lambda.A[i][j] * lambda.B[j][observationSymbolSequence.getObservable( t + 1 )] * beta[t + 1][j];
					if ( !scale ) {
						xi[t][i][j] /= forwardResult.getProbability();
					}
				}
			}
		}

		return xi;
	}

	protected double[][] estimateGamma( double[][][] xi ) {
		double[][] gamma = new double[xi.length + 1][xi[0].length];
		for ( int t = 0; t < xi.length; t++ ) {
			for ( int i = 0; i < xi[0].length; i++ ) {
				for ( int j = 0; j < xi[0].length; j++ ) {
					gamma[t][i] += xi[t][i][j];
				}
			}
		}

		for ( int j = 0; j < xi[0].length; j++ ) {
			for ( int i = 0; i < xi[0].length; i++ ) {
				gamma[xi.length][j] += xi[xi.length - 1][i][j];
			}
		}
		return gamma;
	}

	/*
	 * protected void printMatrix( double[][] a ) { for ( int i = 0; i < a.length; i++ ) { for ( int j = 0; j < a[i].length; j++ ) { System.out.print( a[i][j] +
	 * "\t" ); } System.out.println(); } }
	 */
}
