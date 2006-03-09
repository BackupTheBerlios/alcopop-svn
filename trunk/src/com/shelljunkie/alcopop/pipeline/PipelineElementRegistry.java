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
package com.shelljunkie.alcopop.pipeline;

import java.util.ArrayList;
import java.util.List;

import com.shelljunkie.alcopop.pipeline.descriptor.PipelineElementDescriptor;

/**
 * <<singleton>>
 * 
 * @author Juergen Becker
 */
/**
 * @author Juergen Becker
 */
public class PipelineElementRegistry {
	private volatile static PipelineElementRegistry instance = null;
	private List<PipelineElementDescriptor> sourceDescriptors;
	private List<PipelineElementDescriptor> filterDescriptors;
	private List<PipelineElementDescriptor> sinkDescriptors;

	private PipelineElementRegistry() {
		initialize();
	}

	public static PipelineElementRegistry getInstance() {
		if ( instance == null ) {
			synchronized ( PipelineElementRegistry.class ) {
				if ( instance == null ) {
					instance = new PipelineElementRegistry();
				}
			}
		}
		return instance;
	}

	public Iterable<PipelineElementDescriptor> getDescriptors( int type ) {
		switch ( type ) {
			case PipelineElementDescriptor.TYPE_SOURCE:
				return sourceDescriptors;
			case PipelineElementDescriptor.TYPE_FILTER:
				return filterDescriptors;
			case PipelineElementDescriptor.TYPE_SINK:
				return sinkDescriptors;
			default:
				break;
		}
		return null;
	}

	public Iterable<PipelineElementDescriptor> getSourceDescriptors() {
		return sourceDescriptors;
	}

	public Iterable<PipelineElementDescriptor> getFilterDescriptors() {
		return filterDescriptors;
	}

	public Iterable<PipelineElementDescriptor> getSinkDescriptors() {
		return sinkDescriptors;
	}

	private void initialize() {
		initSourceDescriptors();
		initFilterDescriptors();
		initSinkDescriptors();
	}

	private void initSourceDescriptors() {
		sourceDescriptors = new ArrayList<PipelineElementDescriptor>();
		sourceDescriptors.add( new PipelineElementDescriptor( PipelineElementDescriptor.TYPE_SOURCE, "Prelude DB",
			"com.shelljunkie.alcopop.source.PreludeDBSource", new String[] { "DB Host", "DB Port", "DB Name", "DB User", "DB Password" } ) );
		sourceDescriptors.add( new PipelineElementDescriptor( PipelineElementDescriptor.TYPE_SOURCE, "Prelude DB (HMM Training)",
			"com.shelljunkie.alcopop.source.PreludeDBHMMTrainingSource", new String[] { "DB Host", "DB Port", "DB Name", "DB User", "DB Password" } ) );
		sourceDescriptors.add( new PipelineElementDescriptor( PipelineElementDescriptor.TYPE_SOURCE, "PCAPDump file",
			"com.shelljunkie.alcopop.source.PCAPDumpSource", new String[] { "PCAPDump file", "Ports" } ) );
	}

	private void initFilterDescriptors() {
		filterDescriptors = new ArrayList<PipelineElementDescriptor>();

		filterDescriptors.add( new PipelineElementDescriptor( PipelineElementDescriptor.TYPE_FILTER, "Attack One2One",
			"com.shelljunkie.alcopop.filter.AttackOne2OneFilter", new String[] { "Window Size" } ) );
		filterDescriptors.add( new PipelineElementDescriptor( PipelineElementDescriptor.TYPE_FILTER, "Attack One2Many",
			"com.shelljunkie.alcopop.filter.AttackOne2ManyFilter", new String[] { "Window Size" } ) );
		filterDescriptors.add( new PipelineElementDescriptor( PipelineElementDescriptor.TYPE_FILTER, "Attack Many2One",
			"com.shelljunkie.alcopop.filter.AttackMany2OneFilter", new String[] { "Window Size" } ) );

		filterDescriptors.add( new PipelineElementDescriptor( PipelineElementDescriptor.TYPE_FILTER, "Alert Fusion",
			"com.shelljunkie.alcopop.filter.AlertFusionFilter", new String[] { "Window Size" } ) );

		filterDescriptors.add( new PipelineElementDescriptor( PipelineElementDescriptor.TYPE_FILTER, "HMM payload check",
			"com.shelljunkie.alcopop.filter.HMMPayloadCheckFilter", new String[] { "HMM filename" } ) );

		filterDescriptors.add( new PipelineElementDescriptor( PipelineElementDescriptor.TYPE_FILTER, "HMM payload per port check",
			"com.shelljunkie.alcopop.filter.HMMPayloadPerPortCheckFilter", new String[] { "HMM filename", "Ports" } ) );
	}

	private void initSinkDescriptors() {
		sinkDescriptors = new ArrayList<PipelineElementDescriptor>();
		sinkDescriptors.add( new PipelineElementDescriptor( PipelineElementDescriptor.TYPE_SINK, "Console Logger",
			"com.shelljunkie.alcopop.sink.ConsoleLoggerSink", null ) );
		sinkDescriptors
			.add( new PipelineElementDescriptor( PipelineElementDescriptor.TYPE_SINK, "Dev Null", "com.shelljunkie.alcopop.sink.DevNullSink", null ) );
		sinkDescriptors.add( new PipelineElementDescriptor( PipelineElementDescriptor.TYPE_SINK, "Alert Type Finder",
			"com.shelljunkie.alcopop.sink.AlertTypeFinderSink", null ) );

		sinkDescriptors.add( new PipelineElementDescriptor( PipelineElementDescriptor.TYPE_SINK, "Alert Statistics",
			"com.shelljunkie.alcopop.sink.AlertStatisticsSink", null ) );

		sinkDescriptors.add( new PipelineElementDescriptor( PipelineElementDescriptor.TYPE_SINK, "HMM Alert Type Training",
			"com.shelljunkie.alcopop.sink.HMMAlertTypeTrainingSink", new String[] { "HMM filename" } ) );

		sinkDescriptors.add( new PipelineElementDescriptor( PipelineElementDescriptor.TYPE_SINK, "HMM Payload Training",
			"com.shelljunkie.alcopop.sink.HMMPayloadTrainingSink", new String[] { "HMM filename" } ) );

		sinkDescriptors.add( new PipelineElementDescriptor( PipelineElementDescriptor.TYPE_SINK, "HMM Payload Per Port Training",
			"com.shelljunkie.alcopop.sink.HMMPayloadPerPortTrainingSink", new String[] { "HMM base filename", "Ports" } ) );

		sinkDescriptors.add( new PipelineElementDescriptor( PipelineElementDescriptor.TYPE_SINK, "Alert probability audit",
			"com.shelljunkie.alcopop.sink.AlertProbabilityAuditSink", new String[] { "Metadata fieldname", "Threshold, Above" } ) );
	}

}
