/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.mobicents.slee.services.sip.proxy.mbean;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.mobicents.slee.container.SleeContainer;


public class ProxyConfigurator implements Serializable, ProxyConfiguratorMBean, Cloneable {

	//Name of this configuration
	private String name="only";
	
	//Change to concurrent ones
	private Set localDomains=new HashSet(5);
	//private ArrayList mustPassThrough=new ArrayList();
	private Set supportedUriSchemes=new HashSet(2);
	
	
	private String hostName=null;
	//private long minExpires,maxExpires;
	private int port=5060;
	//private double cTimer=180;
	private String[] transports;
	public ProxyConfigurator() {

			//Read initial configuration
		
		InputStream IS=ProxyConfigurator.class.getResourceAsStream("configuration.properties");
    	Properties props=new Properties();
    	try {
			props.load(IS);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	String d=props.getProperty("domains", "nist.gov,mobicents.org");
    	String splited[]=d.split(",");
    	
    	for(int i=0;i<splited.length;i++)
    		localDomains.add(splited[i]);
    	
    	// add jboss binding address too
    	String jbossBindingAddress = System.getProperty("bind.address","127.0.0.1");
    	if (!localDomains.contains(jbossBindingAddress)) {
    		localDomains.add(jbossBindingAddress);
    	}
    	
    	name=props.getProperty("configuration.name", "only_human");

    	
	}

	public void addLocalDomain(String localDomainToAdd) {
		localDomains.add(localDomainToAdd);
		
	}


	public void addSupportedURIScheme(String schemeToAdd) {
		supportedUriSchemes.add(schemeToAdd);
		
	}

	public String getSipHostName() {
		
		return hostName;
	}

	

	public void removeLocalDomain(String localDomainToRemove) {
		localDomains.remove(localDomainToRemove);
		
	}

	public void removeSupportedURIScheme(String schemeToRemove) {
		supportedUriSchemes.remove(schemeToRemove);
		
	}



	public void setSipHostName(String sipHostName) {
		if(sipHostName==null || sipHostName.equals(""))
			throw new IllegalArgumentException("Sip HostName cant be["+sipHostName+"]");
		this.hostName=sipHostName;
	}

	public void setSipPort(int port) {
		this.port=port;
		
	}

	public void setSipTransports(String[] transports) {
		if(transports==null )
			throw new IllegalArgumentException("Sip Transport cant be one of ["+transports+"]");
		this.transports=transports;
		
	}

	public String[] getLocalDomainNames() {
		String[] tmp=new String[1];
		return (String[]) localDomains.toArray(tmp);
	}

	public String getSipHostname() {
		
		return hostName;
	}

	public int getSipPort() {
		
		return port;
	}

	public String[] getSipTransports() {
		// TODO Auto-generated method stub
		return this.transports;
	}

	public String[] getSupportedURISchemes() {
		String[] tmp=new String[1];
		return (String[]) supportedUriSchemes.toArray(tmp);
	}

	
	public  boolean startService()
	{
		
		
		MBeanServer mbs=SleeContainer.lookupFromJndi().getMBeanServer();
		ObjectName on=null;
		try {
			on=new ObjectName(MBEAN_NAME_PREFIX+name);
			
		} catch (MalformedObjectNameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			if (mbs.getObjectInstance(on) != null) {
				mbs.unregisterMBean(on);
			}
		} catch (InstanceNotFoundException e) {
			// ignore
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			mbs.registerMBean(this, on);
		} catch (InstanceAlreadyExistsException e) {
			
			e.printStackTrace();
			return false;
		} catch (MBeanRegistrationException e) {
			
			e.printStackTrace();
			return false;
		} catch (NotCompliantMBeanException e) {
			
			e.printStackTrace();
			return false;
		}
		
		return true;
		
	}
	
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name=name;
	}
	public Object clone()
	{
		
		
		
		ProxyConfigurator proxy=new ProxyConfigurator();
		proxy.setSipHostName(this.getSipHostname());
		proxy.setSipPort(this.getSipPort());
		
		proxy.setSipTransports(this.getSipTransports());
		
		Iterator it=this.localDomains.iterator();
		while(it.hasNext())
			proxy.addLocalDomain((String) it.next());
		it=this.supportedUriSchemes.iterator();
		while(it.hasNext())
			proxy.addSupportedURIScheme((String) it.next());
		
		
		proxy.name=this.name;
		
		return proxy;
		
	}

	
}