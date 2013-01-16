/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2011 Joern Huxhorn
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.huxhorn.lilith.services.sender;

import de.huxhorn.lilith.data.access.AccessEvent;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.engine.impl.sourceproducer.SerializingMessageBasedServerSocketEventSourceProducer;
import de.huxhorn.lilith.swing.MainFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

public class SenderService
{
	private final Logger logger = LoggerFactory.getLogger(SenderService.class);

	/**
	 * contains mDnsServiceName => eventSender
	 */
	private final Map<String, EventSender<LoggingEvent>> loggingEventSenders;

	/**
	 * contains mDnsServiceName => eventSender
	 */
	private final Map<String, EventSender<AccessEvent>> accessEventSenders;

	/**
	 * Can't use InetAddress as key because equals sucks for that class.
	 */
	private final Map<String, JmDNS> registries;
	private String mDnsName;
	private BonjourListener bonjourListener;
	private MainFrame mainFrame;
	private NetworkWatchdogRunnable networkWatchdogRunnable;
	private Set<SerializingMessageBasedServerSocketEventSourceProducer<AccessEvent>> accessProducers;
	private Set<SerializingMessageBasedServerSocketEventSourceProducer<LoggingEvent>> loggingProducers;
	//private ArrayList<ServiceInfo> serviceInfos;


	public SenderService(MainFrame mainFrame)
	{
		this(mainFrame, null);
	}

	public SenderService(MainFrame mainFrame, String mDnsName)
	{
		this.mDnsName = mDnsName;
		if(this.mDnsName == null)
		{
			this.mDnsName = System.getProperty("user.name");
		}

		this.mainFrame = mainFrame;

		loggingEventSenders = new HashMap<String, EventSender<LoggingEvent>>();
		accessEventSenders = new HashMap<String, EventSender<AccessEvent>>();

		registries = new HashMap<String, JmDNS>();
		//serviceInfos=new ArrayList<ServiceInfo>();
		accessProducers = new HashSet<SerializingMessageBasedServerSocketEventSourceProducer<AccessEvent>>();
		loggingProducers = new HashSet<SerializingMessageBasedServerSocketEventSourceProducer<LoggingEvent>>();
		bonjourListener = new BonjourListener();
	}

	public void start()
	{
		networkWatchdogRunnable = new NetworkWatchdogRunnable();
		Thread t = new Thread(networkWatchdogRunnable, "NetworkWatchdogRunnable");
		t.setDaemon(true);
		t.start();
	}

	public void stop()
	{
		if(logger.isInfoEnabled()) logger.info("Unregistering services...");
		// this can't be done in the shutdown hook...
		if(networkWatchdogRunnable != null)
		{
			// stop watchdog thread
			networkWatchdogRunnable.shutDown();
		}
		synchronized(registries)
		{
			for(Map.Entry<String, JmDNS> current : registries.entrySet())
			{
				String key = current.getKey();
				JmDNS jmDns = current.getValue();
				if(logger.isDebugEnabled()) logger.debug("Unregistering services for {}...", key);
				jmDns.unregisterAllServices();
			}
			registries.clear();
		}

	}

	private JmDNS createJmDNS(InetAddress address)
		throws IOException
	{
		JmDNS jmDns = JmDNS.create(address);
		jmDns.addServiceListener(LoggingEventSender.SERVICE_TYPE, bonjourListener);
		jmDns.addServiceListener(AccessEventSender.SERVICE_TYPE, bonjourListener);
		if(logger.isInfoEnabled()) logger.info("Created new JmDNS instance for {}.", address);
		return jmDns;
	}


	private void registerServices(JmDNS jmDns, List<ServiceInfo> serviceInfos)
	{
		if(logger.isInfoEnabled()) logger.info("Registering services for {}.", jmDns.getHostName());
		ServiceRegistrationRunnable r = new ServiceRegistrationRunnable(jmDns, serviceInfos);
		Thread t = new Thread(r);
		t.setDaemon(true);
		t.start();
	}

	private void unregisterServices(JmDNS dns)
	{
		synchronized(loggingEventSenders)
		{
			List<String> obsoleteSenders = new ArrayList<String>();
			for(Map.Entry<String, EventSender<LoggingEvent>> current : loggingEventSenders.entrySet())
			{
				EventSender<LoggingEvent> sender = current.getValue();
				if(sender.getJmDNS() == dns)
				{
					sender.discard();
					obsoleteSenders.add(current.getKey());
				}
			}
			for(String current : obsoleteSenders)
			{
				loggingEventSenders.remove(current);
				if(logger.isDebugEnabled()) logger.debug("Removed loggingEventSender for key {}.", current);
			}
		}

		synchronized(accessEventSenders)
		{
			List<String> obsoleteSenders = new ArrayList<String>();
			for(Map.Entry<String, EventSender<AccessEvent>> current : accessEventSenders.entrySet())
			{
				EventSender<AccessEvent> sender = current.getValue();
				if(sender.getJmDNS() == dns)
				{
					sender.discard();
					obsoleteSenders.add(current.getKey());
				}
			}
			for(String current : obsoleteSenders)
			{
				accessEventSenders.remove(current);
				if(logger.isDebugEnabled()) logger.debug("Removed accessEventSender for key {}.", current);
			}
		}

	}

	/**
	 * Returns a sorted map containing resolved source name mapped to sender. If there is both a compressed
	 * and uncompressed sender the compressed one will be used.
	 *
	 * @param senders a map of all senders
	 * @return a  map of senders.
	 */
	private <T extends Serializable> Map<String, EventSender<T>> getEventSenders(Map<String, EventSender<T>> senders)
	{
		Map<String, EventSender<T>> serviceNameSenderMapping;
		synchronized(senders)
		{
			serviceNameSenderMapping = new HashMap<String, EventSender<T>>(senders);
		}

		SortedMap<String, EventSender<T>> result = new TreeMap<String, EventSender<T>>();
		for(Map.Entry<String, EventSender<T>> current : serviceNameSenderMapping.entrySet())
		{
			EventSender<T> value = current.getValue();
			String hostName = value.getHostAddress();
			hostName = mainFrame.getPrimarySourceTitle(hostName);
			EventSender<T> prevValue = result.get(hostName);
			if(prevValue == null)
			{
				result.put(hostName, value);
			}
			else
			{
				if(value instanceof AbstractEventSender)
				{
					AbstractEventSender aes = (AbstractEventSender) value;
					if(aes.isCompressing())
					{
						result.put(hostName, value);
						if(logger.isDebugEnabled()) logger.debug("Replaced previous sender with compressing one.");
					}
				}
			}
		}
		if(logger.isDebugEnabled()) logger.debug("EventSenders: {}", result);
		return result;
	}

	public Map<String, EventSender<LoggingEvent>> getLoggingEventSenders()
	{
		return getEventSenders(loggingEventSenders);
	}

	public Map<String, EventSender<AccessEvent>> getAccessEventSenders()
	{
		return getEventSenders(accessEventSenders);
	}

	public static Set<InetAddress> resolveInetAddresses()
	{
		final Logger logger = LoggerFactory.getLogger(SenderService.class);

		Set<InetAddress> inetAddresses = new HashSet<InetAddress>();
		try
		{
			Enumeration<NetworkInterface> netIfcs = NetworkInterface.getNetworkInterfaces();

			while(netIfcs.hasMoreElements())
			{
				NetworkInterface ni = netIfcs.nextElement();
				Enumeration<InetAddress> inetAddrs = ni.getInetAddresses();
				while(inetAddrs.hasMoreElements())
				{
					InetAddress iadd = inetAddrs.nextElement();
					if(!iadd.isLoopbackAddress())
					{
						inetAddresses.add(iadd);
					}
				}

			}
		}
		catch(SocketException ex)
		{
			if(logger.isWarnEnabled()) logger.warn("Exception while retrieving InetAddresses!", ex);
		}
		if(logger.isDebugEnabled()) logger.debug("InetAddresses: {}", inetAddresses);
		return inetAddresses;
	}

	public void addLoggingProducer(SerializingMessageBasedServerSocketEventSourceProducer<LoggingEvent> producer)
	{
		loggingProducers.add(producer);
	}

	public void addAccessProducer(SerializingMessageBasedServerSocketEventSourceProducer<AccessEvent> producer)
	{
		accessProducers.add(producer);
	}

	public List<ServiceInfo> createServiceInfos()
	{
		List<ServiceInfo> result = new ArrayList<ServiceInfo>();
		for(SerializingMessageBasedServerSocketEventSourceProducer<LoggingEvent> current : loggingProducers)
		{
			Hashtable<String, String> props = new Hashtable<String, String>();
			int port = current.getPort();

			props.put(AbstractEventSender.COMPRESSED_MDNS_PROPERTY_NAME, "" + current.isCompressing());
			int weight = 0;
			int priority;
			if(current.isCompressing())
			{
				priority = 65535;
			}
			else
			{
				priority = 0;
			}
			ServiceInfo serviceInfo = ServiceInfo
				.create(LoggingEventSender.SERVICE_TYPE, mDnsName, port, weight, priority, props);
			result.add(serviceInfo);
		}
		for(SerializingMessageBasedServerSocketEventSourceProducer<AccessEvent> current : accessProducers)
		{
			Hashtable<String, String> props = new Hashtable<String, String>();
			int port = current.getPort();

			props.put(AbstractEventSender.COMPRESSED_MDNS_PROPERTY_NAME, "" + current.isCompressing());
			int weight = 0;
			int priority;
			if(current.isCompressing())
			{
				priority = 65535;
			}
			else
			{
				priority = 0;
			}
			ServiceInfo serviceInfo = ServiceInfo
				.create(AccessEventSender.SERVICE_TYPE, mDnsName, port, weight, priority, props);
			result.add(serviceInfo);
		}
		return result;
	}

	private class BonjourListener
		implements ServiceListener
	{

		public void serviceAdded(ServiceEvent serviceEvent)
		{
			if(logger.isInfoEnabled()) logger.info("serviceAdded!");
			GetServiceInfoRunnable r = new GetServiceInfoRunnable(serviceEvent);
			// TODO: threadpool?
			Thread t = new Thread(r);
			t.setDaemon(true);
			t.start();
		}

		public void serviceRemoved(ServiceEvent serviceEvent)
		{
			if(logger.isInfoEnabled()) logger.info("serviceRemoved!");
			String type = serviceEvent.getType();
			String name = serviceEvent.getName();
			if(name != null)
			{
				if(LoggingEventSender.SERVICE_TYPE.equals(type))
				{
					EventSender<LoggingEvent> sender;
					synchronized(loggingEventSenders)
					{
						sender = loggingEventSenders.remove(name);
					}
					if(sender != null)
					{
						sender.discard();
						if(logger.isInfoEnabled()) logger.info("LoggingEventSender discarded.");
					}
				}
				else if(AccessEventSender.SERVICE_TYPE.equals(type))
				{
					EventSender<AccessEvent> sender;
					synchronized(accessEventSenders)
					{
						sender = accessEventSenders.remove(name);
					}
					if(sender != null)
					{
						sender.discard();
						if(logger.isInfoEnabled()) logger.info("AccessEventSender discarded.");
					}
				}
			}
		}

		public void serviceResolved(ServiceEvent serviceEvent)
		{
			if(logger.isInfoEnabled()) logger.info("serviceResolved!");
			JmDNS jmDns = serviceEvent.getDNS();
			ServiceInfo info = serviceEvent.getInfo();
			if(logger.isInfoEnabled()) logger.info("Info: {}", info);
			String type = serviceEvent.getType();
			String name = serviceEvent.getName();
			if(name != null && info != null)
			{
				String compressedStr = info.getPropertyString(AbstractEventSender.COMPRESSED_MDNS_PROPERTY_NAME);
				boolean compressed = Boolean.valueOf(compressedStr);
				if(LoggingEventSender.SERVICE_TYPE.equals(type))
				{
					EventSender<LoggingEvent> sender = new LoggingEventSender(jmDns, name, info.getHostAddress(), info.getPort(), compressed);
					synchronized(loggingEventSenders)
					{
						sender = loggingEventSenders.put(name, sender);
						if(logger.isInfoEnabled()) logger.info("LoggingEventSender created.");
					}
					if(sender != null)
					{
						sender.discard();
						if(logger.isInfoEnabled()) logger.info("Previous LoggingEventSender discarded.");
					}
				}
				else if(AccessEventSender.SERVICE_TYPE.equals(type))
				{
					EventSender<AccessEvent> sender = new AccessEventSender(jmDns, name, info.getHostAddress(), info.getPort(), compressed);
					synchronized(accessEventSenders)
					{
						sender = accessEventSenders.put(name, sender);
						if(logger.isInfoEnabled()) logger.info("AccessEventSender created.");
					}
					if(sender != null)
					{
						sender.discard();
						if(logger.isInfoEnabled()) logger.info("Previous AccessEventSender discarded.");
					}
				}
			}
		}
	}

	class GetServiceInfoRunnable
		implements Runnable
	{
		private ServiceEvent serviceEvent;

		public GetServiceInfoRunnable(ServiceEvent serviceEvent)
		{
			this.serviceEvent = serviceEvent;
		}

		public void run()
		{
			JmDNS dns = serviceEvent.getDNS();
			ServiceInfo serviceInfo = dns.getServiceInfo(serviceEvent.getType(), serviceEvent.getName());
			if(logger.isInfoEnabled()) logger.info("serviceInfo: {}", serviceInfo);
		}
	}

	class ServiceRegistrationRunnable
		implements Runnable
	{
		private List<ServiceInfo> serviceInfos;
		private JmDNS jmDns;

		public ServiceRegistrationRunnable(JmDNS jmDns, List<ServiceInfo> serviceInfos)
		{
			this.jmDns = jmDns;
			this.serviceInfos = serviceInfos;

		}

		public void run()
		{
			if(jmDns != null)
			{
				for(ServiceInfo current : serviceInfos)
				{
					try
					{
						jmDns.registerService(current);
						if(logger.isDebugEnabled()) logger.debug("Registered {}.", current);
					}
					catch(IOException e)
					{
						if(logger.isWarnEnabled()) logger.warn("Exception while registering service!", e);
						e.printStackTrace();
					}
				}
			}
		}
	}

	private class NetworkWatchdogRunnable
		implements Runnable
	{
		private boolean shutDown;

		private NetworkWatchdogRunnable()
		{
			shutDown = false;
		}

		public void shutDown()
		{
			shutDown = true;
		}

		public void run()
		{
			for(; ;)
			{
				if(shutDown)
				{
					break;
				}
				Set<InetAddress> inetAddresses = resolveInetAddresses();
				Set<InetAddress> newAddresses = new HashSet<InetAddress>();
				Set<JmDNS> obsoleteDns = new HashSet<JmDNS>();
				synchronized(registries)
				{
					Set<String> obsoleteAddresses = new HashSet<String>();
					for(Map.Entry<String, JmDNS> current : registries.entrySet())
					{
						String key = current.getKey();
						boolean found = false;
						for(InetAddress add : inetAddresses)
						{
							if(add.getHostAddress().equals(key))
							{
								found = true;
							}
						}
						if(!found)
						{
							// remove
							obsoleteAddresses.add(key);
							obsoleteDns.add(current.getValue());
						}
					}

					for(InetAddress current : inetAddresses)
					{
						if(!registries.containsKey(current.getHostAddress()))
						{
							// add
							newAddresses.add(current);
						}
					}

					for(String current : obsoleteAddresses)
					{
						registries.remove(current);
						if(logger.isDebugEnabled()) logger.debug("Removed {} from registry.", current);
					}
//					if(logger.isDebugEnabled()) logger.debug("Registry after removing: {}", registries);
				}

				for(JmDNS current : obsoleteDns)
				{
					if(logger.isDebugEnabled()) logger.debug("Unregistering all services for {}.", current);
					//current.unregisterAllServices();
					unregisterServices(current);
					current.close();
				}

				Set<JmDNS> newDns = new HashSet<JmDNS>();
				for(InetAddress current : newAddresses)
				{
					try
					{
						JmDNS jmDns = createJmDNS(current);
						newDns.add(jmDns);
					}
					catch(IOException ex)
					{
						if(logger.isWarnEnabled()) logger.warn("Exception while creating new JmDNS instance for address {}!", current, ex);
					}
				}
				if(newDns.size() > 0)
				{
					synchronized(registries)
					{
						for(JmDNS current : newDns)
						{
							try
							{
								registries.put(current.getInterface().getHostAddress(), current);
							}
							catch(IOException ex)
							{
								if(logger.isWarnEnabled())
								{
									logger.warn("Exception while resolving interface of existing JmDNS instance!", ex);
								}
							}
						}
						if(logger.isDebugEnabled()) logger.debug("Registry after adding: {}", registries);
					}
					for(JmDNS current : newDns)
					{
						registerServices(current, createServiceInfos());
					}
				}

				try
				{
					Thread.sleep(60000);
				}
				catch(InterruptedException e)
				{
					if(logger.isInfoEnabled())
					{
						logger.info("Exiting network watchdog thread because of interruption.", e);
					}
					break;
				}
			}
		}
	}

}
