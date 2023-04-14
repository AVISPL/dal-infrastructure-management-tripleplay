/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.tripleplay;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.math.IntMath;

import com.avispl.symphony.api.dal.control.Controller;
import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.Statistics;
import com.avispl.symphony.api.dal.dto.monitor.aggregator.AggregatedDevice;
import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
import com.avispl.symphony.api.dal.monitor.Monitorable;
import com.avispl.symphony.api.dal.monitor.aggregator.Aggregator;
import com.avispl.symphony.dal.communicator.RestCommunicator;
import com.avispl.symphony.dal.infrastructure.management.tripleplay.common.TriplePlayConstrant;
import com.avispl.symphony.dal.infrastructure.management.tripleplay.common.TriplePlayURL;
import com.avispl.symphony.dal.infrastructure.management.tripleplay.common.controlling.AggregatorGroupControllingMetric;
import com.avispl.symphony.dal.infrastructure.management.tripleplay.common.controlling.ChannelMetric;
import com.avispl.symphony.dal.infrastructure.management.tripleplay.common.monitoring.ClientInfoMetric;
import com.avispl.symphony.dal.infrastructure.management.tripleplay.common.monitoring.HardwareMetric;
import com.avispl.symphony.dal.infrastructure.management.tripleplay.common.monitoring.NetworkMetric;
import com.avispl.symphony.dal.infrastructure.management.tripleplay.dto.Activity;
import com.avispl.symphony.dal.infrastructure.management.tripleplay.dto.Client;
import com.avispl.symphony.dal.infrastructure.management.tripleplay.dto.MonitoringData;
import com.avispl.symphony.dal.infrastructure.management.tripleplay.dto.Service;
import com.avispl.symphony.dal.infrastructure.management.tripleplay.dto.ServiceWrapper;
import com.avispl.symphony.dal.infrastructure.management.tripleplay.dto.request.implement.ControllingRequestBody;
import com.avispl.symphony.dal.infrastructure.management.tripleplay.dto.request.implement.GetAllServicesRequestBody;
import com.avispl.symphony.dal.infrastructure.management.tripleplay.dto.request.implement.QueryClientRequestBody;
import com.avispl.symphony.dal.infrastructure.management.tripleplay.dto.request.implement.QueryClientsListRequestBody;
import com.avispl.symphony.dal.util.ControllablePropertyFactory;
import com.avispl.symphony.dal.util.StringUtils;

/**
 * TriplePlayAggregatorCommunicator
 * An implementation of RestCommunicator to provide communication and interaction with TriplePlay cloud and its aggregated devices
 * Supported aggregated device categories are:
 * <li>Device</li>
 * <li>Channel</li>
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 3/13/2023
 * @since 1.0.0
 */
public class TriplePlayAggregatorCommunicator extends RestCommunicator implements Aggregator, Monitorable, Controller {

	/**
	 * Process is running constantly and triggers collecting data from TriplePlay API endpoints base on getMultipleStatistic
	 *
	 * @author Harry
	 * @since 1.0.0
	 */
	class ClientLoader implements Runnable {
		private volatile List<String> clientIps;

		/**
		 * Parameters constructors
		 *
		 * @param clientIps ip address of clients what need to get information
		 */
		public ClientLoader(List<String> clientIps) {
			this.clientIps = clientIps;
		}

		@Override
		public void run() {

			if (!cachedClients.isEmpty()) {
				retrieveClient(this.clientIps);
			}
		}

		/**
		 * Retrieve information of the clients in a thread
		 *
		 * @param clientIps List client will be get information in a thread
		 */
		private void retrieveClient(List<String> clientIps) {
			int clientNumber = 0;
			while (clientNumber < clientIps.size()) {
				QueryClientRequestBody queryClientRequest = new QueryClientRequestBody();
				queryClientRequest.setJsonrpc(TriplePlayConstrant.JSON_RPC);
				for (int i = 0; i < TriplePlayConstrant.MAX_CLIENT_QUANTITY_PER_REQUEST && clientNumber < clientIps.size(); ++i) {
					queryClientRequest.getClientMAC().add(clientIps.get(clientNumber));
					++clientNumber;
				}
				queryClientRequest.getInformation().add(TriplePlayConstrant.HARDWARE_INFORMATION);
				queryClientRequest.getInformation().add(TriplePlayConstrant.SERVICES_INFORMATION);
				queryClientRequest.getInformation().add(TriplePlayConstrant.ACTIVITY_INFORMATION);
				queryClientRequest.getInformation().add(TriplePlayConstrant.NETWORK_INFORMATION);
				try {
					String requestBody = queryClientRequest.buildRequestBody();
					String response = doPost(TriplePlayURL.BASE_URI, requestBody);
					MonitoringData monitoringData = objectMapper.readValue(response, MonitoringData.class);
					for (Client client : monitoringData.getClientWrapper().getClients()) {
						convertServiceToAllService(client);
						addClientToCachedClients(client);
						List<AdvancedControllableProperty> controllableProperties = new ArrayList<>();
						Map<String, String> properties = new HashMap<>();
						populateDeviceMonitoring(properties, client);
						populateDeviceControlling(properties, client, controllableProperties);
						AggregatedDevice aggregatedDevice = new AggregatedDevice();
						aggregatedDevice.setDeviceId(getDefaultValueForNullOrEmpty(client.getClientId()));
						aggregatedDevice.setProperties(properties);
						aggregatedDevice.setDeviceName(getDefaultValueForNullOrEmpty(client.getTypeDescription()));
						if (client.getConnectionStatus() != null) {
							aggregatedDevice.setDeviceOnline(client.getConnectionStatus().equals(TriplePlayConstrant.ONLINE));
						} else {
							aggregatedDevice.setDeviceOnline(false);
						}
						if (!controllableProperties.isEmpty()) {
							aggregatedDevice.setControllableProperties(controllableProperties);
						}

						cachedAggregatedDevices.put(aggregatedDevice.getDeviceId(), aggregatedDevice);
					}
				} catch (Exception e) {
					throw new ResourceNotReachableException(String.format("Error while retrieving the client: %s", e.getMessage()), e);
				}
			}
		}
	}

	/**
	 * ReentrantLock to prevent null pointer exception to localExtendedStatistics when controlProperty method is called before GetMultipleStatistics method.
	 */
	private final ReentrantLock reentrantLock = new ReentrantLock();

	/**
	 * Executor that runs all the async operations
	 */
	private static ExecutorService executorService;

	/**
	 * Polling interval which applied in adapter
	 */
	private volatile int localPollingInterval = TriplePlayConstrant.MIN_POLLING_INTERVAL;

	/**
	 * Number of threads in a thread pool reserved for the device statistics collection
	 */
	private volatile int deviceStatisticsCollectionThreads;

	/**
	 * store pollingInterval adapter properties
	 */
	private volatile String pollingInterval;

	/**
	 * filter devices by name
	 */
	private String deviceNameFilter;

	/**
	 * the last customer's mac address was updated in getMultipleStatistics before
	 */
	private volatile String lastClientMac;

	/**
	 * list all thread
	 */
	private List<Future> clientExecutionPool = new ArrayList<>();

	/**
	 * cachedClients store all client to update information of clients
	 */
	private TreeMap<String, Client> cachedClients = new TreeMap<>();

	/**
	 * cachedAggregatedDevices store all AggregatedDevice to map and show information of clients
	 */
	private Map<String, AggregatedDevice> cachedAggregatedDevices = new ConcurrentHashMap<>();

	/**
	 * cachedServices store all service
	 */
	private Map<String, Service> cachedServices = new HashMap<>();

	private boolean isEmergencyDelivery;
	private ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * Retrieves {@link #pollingInterval}
	 *
	 * @return value of {@link #pollingInterval}
	 */
	public String getPollingInterval() {
		return pollingInterval;
	}

	/**
	 * Sets {@link #pollingInterval} value
	 *
	 * @param pollingInterval new value of {@link #pollingInterval}
	 */
	public void setPollingInterval(String pollingInterval) {
		this.pollingInterval = pollingInterval;
	}

	/**
	 * Retrieves {@link #deviceNameFilter}
	 *
	 * @return value of {@link #deviceNameFilter}
	 */
	public String getDeviceNameFilter() {
		return deviceNameFilter;
	}

	/**
	 * Sets {@link #deviceNameFilter} value
	 *
	 * @param deviceNameFilter new value of {@link #deviceNameFilter}
	 */
	public void setDeviceNameFilter(String deviceNameFilter) {
		this.deviceNameFilter = deviceNameFilter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Statistics> getMultipleStatistics() throws Exception {
		//Because there are some threads that keep running when the next getMultiple is called,
		// so we have to stop all those threads just before the next getMultiple runs
		if (executorService != null) {
			for (Future future : clientExecutionPool) {
				future.cancel(true);
			}
			clientExecutionPool.clear();
		}
		reentrantLock.lock();
		try {
			if (!isEmergencyDelivery) {
				int currentSizeCacheClients = cachedClients.size();
				retrieveClients();
				filterByName();
				retrieveServices();
				localPollingInterval = calculatingLocalPollingInterval();
				deviceStatisticsCollectionThreads = calculatingThreadQuantity();

				//Multi thread for get information of client
				retrieveInformationOfAllClients(currentSizeCacheClients);
			}
			isEmergencyDelivery = false;
		} finally {
			reentrantLock.unlock();
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void controlProperty(ControllableProperty controllableProperty) throws Exception {
		String property = controllableProperty.getProperty();
		String value = String.valueOf(controllableProperty.getValue());
		String deviceId = controllableProperty.getDeviceId();
		reentrantLock.lock();
		try {
			if (StringUtils.isNotNullOrEmpty(deviceId) && cachedAggregatedDevices.containsKey(deviceId)) {
				aggregatedDeviceControl(property, value, cachedAggregatedDevices.get(deviceId));
			}
		} finally {
			reentrantLock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void controlProperties(List<ControllableProperty> list) throws Exception {
		for (ControllableProperty controllableProperty : list) {
			controlProperty(controllableProperty);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void authenticate() throws Exception {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AggregatedDevice> retrieveMultipleStatistics() throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("Start call retrieveMultipleStatistic");
		}
		return cachedAggregatedDevices.values().stream().collect(Collectors.toList());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AggregatedDevice> retrieveMultipleStatistics(List<String> list) throws Exception {
		return retrieveMultipleStatistics();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void internalDestroy() {
		cachedClients.clear();
		cachedAggregatedDevices.clear();
		localPollingInterval = TriplePlayConstrant.MIN_POLLING_INTERVAL;
		clientExecutionPool.clear();
		cachedServices.clear();
	}

	/**
	 * Retrieve number of clients and ips of them
	 */
	private void retrieveClients() {
		try {
			QueryClientsListRequestBody queryClientsListRequest = new QueryClientsListRequestBody();
			queryClientsListRequest.setJsonrpc(TriplePlayConstrant.JSON_RPC);
			queryClientsListRequest.setField(TriplePlayConstrant.CLIENT_TYPE);
			queryClientsListRequest.setOperation(TriplePlayConstrant.IS);
			queryClientsListRequest.setValue(TriplePlayConstrant.SET_TOP_BOX);
			queryClientsListRequest.getInformation().add(TriplePlayConstrant.NETWORK_INFORMATION);
			String response = doPost(TriplePlayURL.BASE_URI, queryClientsListRequest.buildRequestBody());
			MonitoringData monitoringData = objectMapper.readValue(response, MonitoringData.class);

			if (monitoringData == null) {
				return;
			}
			//Remove All Client was removed on Server
			HashSet<String> clientMACs = new HashSet<>();
			for (Client client : monitoringData.getClientWrapper().getClients()) {
				clientMACs.add(getKeyInCachedClients(client));
			}
			for (Entry<String, Client> clientEntry : cachedClients.entrySet()) {
				if (!clientMACs.contains(clientEntry.getKey())) {
					cachedClients.remove(clientEntry.getKey());
				}
			}
			for (Client client : monitoringData.getClientWrapper().getClients()) {
				if (!cachedClients.containsKey(getKeyInCachedClients(client))) {
					cachedClients.put(getKeyInCachedClients(client), client);
				}
			}
		} catch (Exception e) {
			throw new ResourceNotReachableException(String.format("Error while retrieving the clients: %s", e.getMessage()), e);
		}
	}

	/**
	 * filter all device by name
	 */
	private void filterByName() {
		if (StringUtils.isNullOrEmpty(deviceNameFilter)) {
			return;
		}
		Set<String> filterNames = convertUserInput(deviceNameFilter);
		TreeMap<String, Client> localCachedClient = new TreeMap<>();
		for (Entry<String, Client> clientEntry : cachedClients.entrySet()) {
			if (!filterNames.contains(clientEntry.getValue().getTypeDescription())) {
				cachedAggregatedDevices.remove(clientEntry.getValue().getClientId());
			} else {
				localCachedClient.put(clientEntry.getKey(), clientEntry.getValue());
			}
		}
		cachedClients = localCachedClient;
	}

	/**
	 * Retrieve all service
	 */
	private void retrieveServices() {
		try {
			GetAllServicesRequestBody requestBody = new GetAllServicesRequestBody();
			requestBody.setJsonrpc(TriplePlayConstrant.JSON_RPC);
			requestBody.setParam(-1);
			String response = doPost(TriplePlayURL.BASE_URI, requestBody.buildRequestBody());
			ServiceWrapper monitoringData = objectMapper.readValue(response, ServiceWrapper.class);
			for (Service service : monitoringData.getServices()) {
				cachedServices.put(String.valueOf(service.getId()), service);
			}
		} catch (Exception e) {
			throw new ResourceNotReachableException(String.format("Error while retrieving the services: %s", e.getMessage()), e);
		}
	}

	/**
	 * calculating local polling interval
	 *
	 * @throws IllegalArgumentException when get limit rate exceed error
	 */
	private int calculatingLocalPollingInterval() {

		try {
			int pollingIntervalValue = TriplePlayConstrant.MIN_POLLING_INTERVAL;
			if (StringUtils.isNotNullOrEmpty(pollingInterval)) {
				pollingIntervalValue = Integer.parseInt(pollingInterval);
			}

			int minPollingInterval = calculatingMinPollingInterval();
			if (pollingIntervalValue < minPollingInterval) {
				logger.error(String.format("invalid pollingInterval value, pollingInterval must greater than: %s", minPollingInterval));
				return minPollingInterval;
			}
			return pollingIntervalValue;
		} catch (Exception e) {
			throw new IllegalArgumentException(String.format("Unexpected pollingInterval value: %s", pollingInterval), e);
		}
	}

	/**
	 * calculating thread quantity
	 */
	private int calculatingThreadQuantity() {
		if (cachedClients.isEmpty()) {
			return TriplePlayConstrant.MIN_THREAD_QUANTITY;
		}
		if (cachedClients.size() / localPollingInterval < TriplePlayConstrant.MAX_THREAD_QUANTITY * TriplePlayConstrant.MAX_CLIENT_QUANTITY_PER_THREAD) {
			return IntMath.divide(cachedClients.size(), localPollingInterval * TriplePlayConstrant.MAX_CLIENT_QUANTITY_PER_THREAD, RoundingMode.CEILING);
		}
		return TriplePlayConstrant.MAX_THREAD_QUANTITY;
	}

	/**
	 * Retrieve information of all clients
	 *
	 * @param currentSizeCachedClients Current size of cachedClients List, this param use to check number of clients was change or not
	 */
	private void retrieveInformationOfAllClients(int currentSizeCachedClients) {
		if (executorService == null || currentSizeCachedClients != cachedClients.size()) {
			executorService = Executors.newFixedThreadPool(deviceStatisticsCollectionThreads);
		}

		String lastClientKey = lastClientMac != null ? cachedClients.floorKey(lastClientMac) : null;
		if (cachedClients.isEmpty()) {
			lastClientMac = null;
			return;
		}

		Iterator<Entry<String, Client>> cacheClientIt = cachedClients.entrySet().iterator();

		String itClient = null;
		if (lastClientKey != null) {
			while (cacheClientIt.hasNext()) {
				if (cacheClientIt.next().getKey().equals(lastClientKey)) {
					break;
				}
			}
		}

		for (int threadNumber = 0; threadNumber < deviceStatisticsCollectionThreads && cacheClientIt.hasNext(); ++threadNumber) {
			List<String> requestClientMac = new ArrayList<>();
			for (int clientNumber = 0; clientNumber < TriplePlayConstrant.MAX_CLIENT_QUANTITY_PER_THREAD && cacheClientIt.hasNext(); ++clientNumber) {
				itClient = cacheClientIt.next().getKey();
				requestClientMac.add(itClient);
			}
			Future future = executorService.submit(new ClientLoader(requestClientMac));
			clientExecutionPool.add(future);
		}

		//Update next ClientIp to next getMultipleStatistics
		lastClientMac = itClient != cachedClients.lastKey() ? itClient : null;
	}

	/**
	 * This method is used for calling control properties of aggregated device
	 *
	 * @param property name of controllable property
	 * @param value value of controllable property
	 * @param client device need control
	 */
	private void aggregatedDeviceControl(String property, String value, AggregatedDevice client) {
		String[] splitProperty = property.split(TriplePlayConstrant.HASH);
		AggregatorGroupControllingMetric aggregatorGroupControllingMetric = AggregatorGroupControllingMetric.getByName(splitProperty[0]);
		switch (aggregatorGroupControllingMetric) {
			case CHANNEL:
				channelControl(splitProperty[1], value, client);
				isEmergencyDelivery = true;
				break;
			default:
				logger.debug(String.format("%s are not supported", splitProperty[0]));
		}
	}

	/**
	 * calculating minimum of polling interval
	 *
	 * @return Number of polling interval
	 */
	private int calculatingMinPollingInterval() {
		if (!cachedClients.isEmpty()) {
			return IntMath.divide(cachedClients.size(), TriplePlayConstrant.MAX_THREAD_QUANTITY * TriplePlayConstrant.MAX_CLIENT_QUANTITY_PER_THREAD, RoundingMode.CEILING);
		}
		return TriplePlayConstrant.MIN_POLLING_INTERVAL;
	}

	/**
	 * This method is used for calling control channel properties
	 *
	 * @param property name of controllable property
	 * @param value value of controllable property
	 * @param client device need control
	 */
	private void channelControl(String property, String value, AggregatedDevice client) {
		ChannelMetric channelInfoMetric = ChannelMetric.getByName(property);
		switch (channelInfoMetric) {
			case SELECT_CHANNEL:
				selectChannelControl(value, client);
			default:
				logger.debug(String.format("%s are not supported", property));
		}
	}

	/**
	 * This method is used for calling change channel
	 *
	 * @param value value of controllable property
	 * @param client device need control
	 */
	private void selectChannelControl(String value, AggregatedDevice client) {
		try {
			HttpHeaders headers = new HttpHeaders();
			ControllingRequestBody controllingRequest = new ControllingRequestBody(client.getDeviceId(), value);
			ResponseEntity<?> response = doRequest(TriplePlayURL.BASE_URI, HttpMethod.PUT, headers, controllingRequest.buildRequestBody(), String.class);
			Optional<?> responseBody = Optional.ofNullable(response)
					.map(HttpEntity::getBody);
			if (response.getStatusCode().is2xxSuccessful() && responseBody.isPresent()) {
				//Check channel was change
				List<String> macAddress = new ArrayList<>();
				List<String> information = new ArrayList<>();
				macAddress.add(client.getProperties().get(NetworkMetric.MAC_ADDRESS.getName()));
				information.add(TriplePlayConstrant.ACTIVITY_INFORMATION);
				String responseOfQueryRequest = getResponseOfQueryClientRequest(macAddress, information);
				MonitoringData monitoringData = objectMapper.readValue(responseOfQueryRequest, MonitoringData.class);
				Client responseClient = monitoringData.getClientWrapper().getClients().get(0);

				if (!value.equals(responseClient.getActivity().getCurrentService().getName())) {
					throw new IllegalStateException(String.format("Can not control channel of device %s, unknown error", client.getDeviceId()));
				}
				Map<String, String> properties = client.getProperties();
				List<AdvancedControllableProperty> advancedControllableProperties = client.getControllableProperties();

				Client cachedClient = cachedClients.get(getKeyInAggregatedDevice(client));
				List<String> serviceNames = getAllServiceOfClient(cachedClient);

				//update service in cachedClients
				updateServiceInCachedClients(client, cachedClient, value);

				addAdvanceControlProperties(advancedControllableProperties, properties,
						ControllablePropertyFactory.createDropdown(buildNameForChannel(ChannelMetric.SELECT_CHANNEL.getName()),
								serviceNames, value));

				client.setProperties(properties);
				client.setControllableProperties(advancedControllableProperties);

				cachedAggregatedDevices.put(client.getDeviceId(), client);
			} else {
				throw new IllegalStateException(String.format("Can not change channel of device %s", client.getDeviceId()));
			}
		} catch (Exception e) {
			throw new ResourceNotReachableException(String.format("Error while controlling select channel: %s", e.getMessage()), e);
		}
	}

	/**
	 * Build name for channel
	 *
	 * @param channelName channel name need to build
	 * @return name of channel
	 */
	private String buildNameForChannel(String channelName) {
		return AggregatorGroupControllingMetric.CHANNEL.getName() + TriplePlayConstrant.HASH + channelName;
	}

	/**
	 * Return value if value not null, else return NONE
	 *
	 * @param value value need to check null or not
	 */
	private String getDefaultValueForNullOrEmpty(String value) {
		return StringUtils.isNotNullOrEmpty(value) ? value : TriplePlayConstrant.NONE;
	}

	/**
	 * Populate device monitoring
	 *
	 * @param properties properties of device
	 * @param client client to get information for properties
	 */
	private void populateDeviceMonitoring(Map<String, String> properties, Client client) {
		properties.put(ClientInfoMetric.DEVICE_ID.getName(), getDefaultValueForNullOrEmpty(client.getClientId()));
		properties.put(ClientInfoMetric.LOCALE.getName(), getDefaultValueForNullOrEmpty(client.getLocale()));
		properties.put(ClientInfoMetric.LOCALTION.getName(), getDefaultValueForNullOrEmpty(client.getLocation()));
		if (client.getHardware() != null) {
			properties.put(HardwareMetric.HARDWARE_TYPE.getName(), getDefaultValueForNullOrEmpty(client.getHardware().getType()));
			properties.put(HardwareMetric.HARDWARE_VERSION.getName(), getDefaultValueForNullOrEmpty(client.getHardware().getHardwareVersion()));
			properties.put(HardwareMetric.HARDWARE_MODEL.getName(), getDefaultValueForNullOrEmpty(client.getHardware().getModel()));
			properties.put(HardwareMetric.SOFTWARE_VERSION.getName(), getDefaultValueForNullOrEmpty(client.getHardware().getSoftwareVersion()));
			properties.put(HardwareMetric.SERIAL_NUMBER.getName(), getDefaultValueForNullOrEmpty(client.getHardware().getSerialNumber()));
		}
		if (client.getNetwork() != null) {
			properties.put(NetworkMetric.IP_ADDRESS.getName(), getDefaultValueForNullOrEmpty(client.getNetwork().getIp()));
			properties.put(NetworkMetric.MAC_ADDRESS.getName(), getDefaultValueForNullOrEmpty(client.getNetwork().getMac()));
			properties.put(NetworkMetric.DHCP_SUBNET.getName(), getDefaultValueForNullOrEmpty(client.getNetwork().getDhcpSubnet()));
		}
	}

	/**
	 * Populate device controlling
	 *
	 * @param properties properties of device
	 * @param client client to get information for properties and advancedControllableProperties
	 * @param advancedControllableProperties advancedControllableProperties to add controlling property of device
	 */
	private void populateDeviceControlling(Map<String, String> properties, Client client, List<AdvancedControllableProperty> advancedControllableProperties) {
		if (client.getActivity() == null || client.getServices().isEmpty()) {
			return;
		}
		List<String> serviceNames = new ArrayList<>();
		for (Service service : client.getServices()) {
			serviceNames.add(service.getName());
		}
		if (client.getActivity().getCurrentService() != null) {
			addAdvanceControlProperties(advancedControllableProperties, properties,
					ControllablePropertyFactory.createDropdown(buildNameForChannel(ChannelMetric.SELECT_CHANNEL.getName()), serviceNames,
							Optional.ofNullable(client.getActivity()).map(Activity::getCurrentService).map(Service::getName).orElse(TriplePlayConstrant.NONE)));
		}
		if (client.getActivity().getLastService() != null) {
			properties.put(buildNameForChannel(ChannelMetric.LAST_CHANNEL.getName()),
					Optional.ofNullable(client.getActivity()).map(Activity::getLastService).map(Service::getName).orElse(TriplePlayConstrant.NONE));
		}
	}

	/**
	 * Add client to cached client list
	 *
	 * @param client client need to add
	 */
	private void addClientToCachedClients(Client client) {
		if (!StringUtils.isNullOrEmpty(client.getLocale())) {
			client.setLocale(getDisplayLocalByLocale(client.getLocale()));
		}
		if (getKeyInCachedClients(client) != null) {
			cachedClients.put(getKeyInCachedClients(client), client);
		}
	}

	/**
	 * Add advancedControllableProperties if advancedControllableProperties different empty
	 *
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 * @param stats store all statistics
	 * @param property the property is item advancedControllableProperties
	 * @return String response
	 * @throws IllegalStateException when exception occur
	 */
	private void addAdvanceControlProperties(List<AdvancedControllableProperty> advancedControllableProperties, Map<String, String> stats, AdvancedControllableProperty property) {
		if (property != null) {
			for (AdvancedControllableProperty controllableProperty : advancedControllableProperties) {
				if (controllableProperty.getName().equals(property.getName())) {
					advancedControllableProperties.remove(controllableProperty);
					break;
				}
			}
			stats.put(property.getName(), TriplePlayConstrant.EMPTY);
			advancedControllableProperties.add(property);
		}
	}

	/**
	 * Get key value of aggregated device in cached client list
	 *
	 * @param client client to get key value
	 * @return key of client
	 */
	private String getKeyInAggregatedDevice(AggregatedDevice client) {
		return Optional.ofNullable(client.getProperties().get(NetworkMetric.MAC_ADDRESS.getName())).orElse(null);
	}

	/**
	 * Get key value of client in cached client list
	 *
	 * @param client client to get key value
	 * @return key of client
	 */
	private String getKeyInCachedClients(Client client) {
		return Optional.ofNullable(client.getNetwork().getMac()).orElse(null);
	}

	/**
	 * Get display name of locale
	 *
	 * @param locale locale need to get display name, it must follow format locale in response of tripleplay
	 */
	private String getDisplayLocalByLocale(String locale) {
		if (locale.contains(TriplePlayConstrant.SPLIT_LOCALE)) {
			Locale local = new Locale(locale.substring(0, locale.lastIndexOf(TriplePlayConstrant.SPLIT_LOCALE)), locale.substring(locale.lastIndexOf(TriplePlayConstrant.SPLIT_LOCALE) + 1));
			return local.getDisplayName();
		}
		return TriplePlayConstrant.EMPTY;
	}

	/**
	 * Providing the list of services from cachedServices
	 */
	private void convertServiceToAllService(Client client) {
		client.setServices(cachedServices.values().stream().collect(Collectors.toCollection(ArrayList::new)));
	}

	/**
	 * update service in cached clients
	 *
	 * @param client client to update
	 * @param cachedClient client in cached clients list need to update
	 * @param value name of service to set
	 */
	private void updateServiceInCachedClients(AggregatedDevice client, Client cachedClient, String value) {
		for (AdvancedControllableProperty advancedControllableProperty : client.getControllableProperties()) {
			if (advancedControllableProperty.getName().equals(buildNameForChannel(ChannelMetric.SELECT_CHANNEL.getName()))) {
				client.getProperties().put(buildNameForChannel(ChannelMetric.LAST_CHANNEL.getName()), advancedControllableProperty.getValue().toString());
				break;
			}
		}
		for (Service service : cachedClient.getServices()) {
			if (service.getName().equals(client.getProperties().get(buildNameForChannel(ChannelMetric.LAST_CHANNEL.getName())))) {
				cachedClient.getActivity().setLastService(service);
			}
			if (service.getName().equals(value)) {
				cachedClient.getActivity().setCurrentService(service);
			}
		}
	}

	/**
	 * get all service of client
	 *
	 * @param client client to get services
	 * @return list all service of client
	 */
	private List<String> getAllServiceOfClient(Client client) {
		List<String> serviceNames = new ArrayList<>();
		for (Service service : client.getServices()) {
			serviceNames.add(service.getName());
		}
		return serviceNames;
	}

	/**
	 * get response of query client request
	 *
	 * @param macAddress list all mac address of clients
	 * @param information list all information need to get
	 * @return response of request
	 */
	private String getResponseOfQueryClientRequest(List<String> macAddress, List<String> information) throws Exception {
		QueryClientRequestBody requestBody = new QueryClientRequestBody();
		requestBody.setJsonrpc(TriplePlayConstrant.JSON_RPC);
		requestBody.setClientMAC(macAddress);
		requestBody.setInformation(information);
		return doPost(TriplePlayURL.BASE_URI, requestBody.buildRequestBody());
	}

	/**
	 * This method is used to handle input from adapter properties and convert it to Set of String for control
	 *
	 * @return Set<String> is the Set of String of filter element
	 */
	private Set<String> convertUserInput(String input) {
		try {
			if (!StringUtils.isNullOrEmpty(input)) {
				String[] listAdapterPropertyElement = input.split(TriplePlayConstrant.COMMA);

				// Remove start and end spaces of each adapterProperty
				Set<String> setAdapterPropertiesElement = new HashSet<>();
				for (String adapterPropertyElement : listAdapterPropertyElement) {
					setAdapterPropertiesElement.add(adapterPropertyElement.trim());
				}
				return setAdapterPropertiesElement;
			}
		} catch (Exception e) {
			logger.error(String.format("Invalid adapter properties input: %s", e.getMessage()),e);
		}
		return Collections.emptySet();
	}
}