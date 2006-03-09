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
package com.shelljunkie.alcopop.pipeline.descriptor;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for pipeline descriptor validation.
 * 
 * @author Juergen Becker
 */
public class PipelineDescriptorValidator {

	@SuppressWarnings("unchecked")
	public void validate( PipelineDescriptor pipelineDescriptor ) throws PipelineDescriptorValidatorException {
		if ( !isPipelineValid( pipelineDescriptor ) ) {
			throw new PipelineDescriptorValidatorException( null, "bad pipeline name" );
		}

		if ( !hasEnoughSources( pipelineDescriptor ) ) {
			throw new PipelineDescriptorValidatorException( null, "no source" );
		}

		if ( !hasEnoughSinks( pipelineDescriptor ) ) {
			throw new PipelineDescriptorValidatorException( null, "no sink" );
		}

		Map[] statistics = calculatePipeStatistics( pipelineDescriptor.getPipeDescriptors() );

		hasExactNoOfPipes( pipelineDescriptor.getSourceDescriptors(), statistics[0], 0 );
		hasEnoughPipes( pipelineDescriptor.getSourceDescriptors(), statistics[1], 1 );

		hasEnoughPipes( pipelineDescriptor.getFilterDescriptors(), statistics[0], 1 );
		hasEnoughPipes( pipelineDescriptor.getFilterDescriptors(), statistics[1], 1 );

		hasEnoughPipes( pipelineDescriptor.getSinkDescriptors(), statistics[0], 1 );
		hasExactNoOfPipes( pipelineDescriptor.getSinkDescriptors(), statistics[1], 0 );
	}

	protected boolean isPipelineValid( PipelineDescriptor pipelineDescriptor ) {
		return pipelineDescriptor.getName() != null && pipelineDescriptor.getName().length() != 0;
	}

	protected boolean hasEnoughSources( PipelineDescriptor pipelineDescriptor ) {
		return pipelineDescriptor.getNoOfSourceDescriptors() > 0;
	}

	protected boolean hasEnoughSinks( PipelineDescriptor pipelineDescriptor ) {
		return pipelineDescriptor.getNoOfSinkDescriptors() > 0;
	}

	protected boolean hasEnoughPipes( Iterable<PipelineElementDescriptor> elems, Map<String, Integer> inputs, int minValue )
		throws PipelineDescriptorValidatorException {
		for ( PipelineElementDescriptor elem : elems ) {
			Integer value = inputs.get( elem.getID() );
			if ( value == null || value.intValue() < minValue ) {
				throw new PipelineDescriptorValidatorException( elem, "element [" + elem.getName() + "] is not fully connected" );
			}
		}
		return true;
	}

	protected boolean hasExactNoOfPipes( Iterable<PipelineElementDescriptor> elems, Map<String, Integer> inputs, int no )
		throws PipelineDescriptorValidatorException {
		for ( PipelineElementDescriptor elem : elems ) {
			Integer value = inputs.get( elem.getID() );
			if ( value != null && value.intValue() != no ) {
				throw new PipelineDescriptorValidatorException( elem, "element [" + elem.getName() + "] has a wrong number of connections" );
			}
		}
		return true;
	}

	protected Map[] calculatePipeStatistics( Iterable<PipelinePipeElementDescriptor> pipes ) {
		Map<String, Integer> inputs = new HashMap<String, Integer>();
		Map<String, Integer> outputs = new HashMap<String, Integer>();

		for ( PipelinePipeElementDescriptor pipe : pipes ) {
			incValueInMap( inputs, pipe.getSink() );
			incValueInMap( outputs, pipe.getSource() );
		}

		return new Map[] { inputs, outputs };
	}

	protected void incValueInMap( Map<String, Integer> map, String key ) {
		Integer value = map.get( key );
		if ( value != null ) {
			value = new Integer( value.intValue() + 1 );
		} else {
			value = new Integer( 1 );
		}
		map.put( key, value );
	}

}
