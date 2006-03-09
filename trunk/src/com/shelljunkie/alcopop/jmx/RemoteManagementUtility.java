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
package com.shelljunkie.alcopop.jmx;

import java.lang.management.ManagementFactory;
import java.util.Set;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.NotCompliantMBeanException;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import com.shelljunkie.alcopop.logging.LoggerManager;
import com.shelljunkie.alcopop.pipeline.IPipelineProcessingElement;
import com.shelljunkie.alcopop.pipeline.Pipeline;
import com.shelljunkie.alcopop.pipeline.PipelinedProcessingCore;
import com.shelljunkie.alcopop.pipeline.PipelinedProcessingCoreMBean;
import com.shelljunkie.alcopop.pipeline.descriptor.PipelineElementDescriptor;

/**
 * <<singleton>>
 * 
 * @author Juergen Becker
 */
public final class RemoteManagementUtility {
	private static final String JMX_DOMAIN = "com.shellkunkie.alcopop";
	private static volatile RemoteManagementUtility instance;
	private MBeanServerConnection mbs;

	private RemoteManagementUtility() {
	// singelton class
	}

	public static RemoteManagementUtility getInstance() {
		if ( instance == null ) {
			synchronized ( RemoteManagementUtility.class ) {
				if ( instance == null ) {
					instance = new RemoteManagementUtility();
				}
			}
		}
		return instance;
	}

	public boolean connect( String host ) {
		try {
			JMXServiceURL url = new JMXServiceURL( "service:jmx:rmi:///jndi/rmi://" + host + "/jmxrmi" );
			JMXConnector c = JMXConnectorFactory.connect( url );
			mbs = c.getMBeanServerConnection();
			return true;
		} catch ( Exception e ) {
			return false;
		}
	}

	public void disconnect() {
		mbs = null;
	}

	@SuppressWarnings("unchecked")
	public PipelinedProcessingCoreMBean getRemotePipelineProcessingCore() {
		try {
			Set<ObjectName> pipelineNames = mbs.queryNames( new ObjectName( JMX_DOMAIN + ":type=Pipeline,*" ), null );
			if ( pipelineNames.size() > 0 ) {
				PipelinedProcessingCoreMBean ppc = (PipelinedProcessingCoreMBean) MBeanServerInvocationHandler.newProxyInstance( mbs, pipelineNames.iterator()
					.next(), PipelinedProcessingCoreMBean.class, false );
				return ppc;
			}
		} catch ( Exception excep ) {
			LoggerManager.getInstance().getDefaultLogger().error( "could not get remote processing core", excep );
		}
		return null;
	}

	public void register( PipelinedProcessingCore ppc ) {
		Pipeline pipeline = ppc.getPipeline();
		register( pipeline.getName(), pipeline.getName(), "Pipeline", ppc );
		register( pipeline.getSources(), pipeline.getName(), "Source" );
		register( pipeline.getFilerts(), pipeline.getName(), "Filter" );
		register( pipeline.getSinks(), pipeline.getName(), "Sink" );
	}

	@SuppressWarnings("unchecked")
	public void addNotificationListener( PipelineElementDescriptor ped, NotificationListener nl ) {
		try {
			mbs.addNotificationListener( getObjectNameForDescriptor( ped ), nl, null, new Object() );
		} catch ( Exception excep ) {
			LoggerManager.getInstance().getDefaultLogger().error( "failed to add notification listener", excep );
		}
	}

	@SuppressWarnings("unchecked")
	public Object getRemoteMBean( PipelineElementDescriptor ped, String classname ) {
		try {
			Class type = Class.forName( classname + "MBean" );
			return MBeanServerInvocationHandler.newProxyInstance( mbs, getObjectNameForDescriptor( ped ), type, false );
		} catch ( Exception excep ) {
			LoggerManager.getInstance().getDefaultLogger().error( "could not get remote mbean for element " + ped.getName(), excep );
		}
		return null;
	}

	public Object getAttributeValue( PipelineElementDescriptor ped, String attributeName ) {
		try {
			return mbs.getAttribute( getObjectNameForDescriptor( ped ), attributeName );
		} catch ( Exception excep ) {
			LoggerManager.getInstance().getDefaultLogger().error( "could not get attribute " + attributeName, excep );
		}
		return null;
	}

	public String[] getAttributeNames( PipelineElementDescriptor ped ) {
		try {
			MBeanInfo info = mbs.getMBeanInfo( getObjectNameForDescriptor( ped ) );
			MBeanAttributeInfo[] mbInfos = info.getAttributes();
			String[] attributeNames = new String[mbInfos.length];
			for ( int i = 0; i < mbInfos.length; i++ ) {
				attributeNames[i] = mbInfos[i].getName();
			}
			return attributeNames;
		} catch ( Exception excep ) {
			LoggerManager.getInstance().getDefaultLogger().error( "could not get attribute names for element " + ped.getName(), excep );
		}
		return null;
	}

	protected void register( Iterable<IPipelineProcessingElement> elems, String pipelineName, String typeName ) {
		for ( IPipelineProcessingElement elem : elems ) {
			if ( !register( pipelineName, elem.getName() + " #" + elem.getID(), typeName, elem.getManagmentExtension() ) ) {
				register( pipelineName, elem.getName() + " #" + elem.getID(), typeName, elem );
			}
		}
	}

	protected ObjectName getObjectName( String pipeline, String name, String type ) {
		try {
			return new ObjectName( JMX_DOMAIN + ":" + ( pipeline != null ? "pipeline=" + pipeline + "," : "" ) + "type=" + type + ",name=" + name );
		} catch ( Exception excep ) {
			LoggerManager.getInstance().getDefaultLogger().error( "faild to create object name", excep );
		}
		return null;
	}

	protected ObjectName getObjectNameForDescriptor( PipelineElementDescriptor ped ) {
		return getObjectName( ped.getPipelineDescriptor().getName(), ped.getName() + " #" + ped.getID(), ped.getTypeAsString() );
	}

	protected boolean register( String pipeline, String name, String type, Object mbean ) {
		ObjectName on = getObjectName( pipeline, name, type );
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		try {
			mbs.registerMBean( mbean, on );
			return true;
		} catch ( NotCompliantMBeanException nce ) {
			LoggerManager.getInstance().getDefaultLogger().warning( "faild to register object", nce );
			// ok, not all elemenst are mbeans
		} catch ( Exception e ) {
			LoggerManager.getInstance().getDefaultLogger().error( "could not register mbean " + name, e );
		}
		return false;
	}
}
