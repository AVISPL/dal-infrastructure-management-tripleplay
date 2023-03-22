import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
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
import common.controlling.AggregatorGroupControllingMetric;
import common.controlling.ChannelInfoMetric;
import common.monitoring.ClientInfoMetric;
import common.monitoring.HardwareMetric;
import common.monitoring.NetworkMetric;
import common.TriplePlayConstrant;
import common.TriplePlayURL;
import dto.Activity;
import dto.Client;
import dto.MonitoringData;
import dto.Service;
import dto.ServiceWrapper;
import dto.request.implement.ControllingRequest;
import dto.request.implement.GetAllServicesRequestBody;
import dto.request.implement.QueryClientRequestBodyV2;
import dto.request.implement.QueryClientsListRequestBody;

import com.avispl.symphony.api.dal.control.Controller;
import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.Statistics;
import com.avispl.symphony.api.dal.dto.monitor.aggregator.AggregatedDevice;
import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
import com.avispl.symphony.api.dal.monitor.Monitorable;
import com.avispl.symphony.api.dal.monitor.aggregator.Aggregator;
import com.avispl.symphony.dal.communicator.RestCommunicator;
import com.avispl.symphony.dal.util.StringUtils;
import com.avispl.symphony.dal.util.ControllablePropertyFactory;

/**
 * Communicator
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 3/13/2023
 * @since 1.0.0
 */
public class TriplePlayAggregatorCommunicator extends RestCommunicator implements Aggregator, Monitorable, Controller {

	class ClientLoader implements Runnable {
		private volatile List<String> clientIps;

		public ClientLoader(List<String> clientIps) {
			this.clientIps = clientIps;
		}

		@Override
		public void run() {

			if (!cachedClients.isEmpty()) {
				retrieveClient(this.clientIps);
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

	private volatile int localPollingInterval = TriplePlayConstrant.MIN_POLLING_INTERVAL;
	private volatile int deviceStatisticsCollectionThreads;

	/**
	 * store pollingInterval adapter properties
	 */
	private volatile String pollingInterval;

	private volatile String lastClientMac;
	private List<Future> clientExecutionPool = new ArrayList<>();
	private TreeMap<String, Client> cachedClients = new TreeMap<>();
	private Map<String, AggregatedDevice> cachedAggregatedDevices = new ConcurrentHashMap<>();
	private HashMap<String, Service> cachedServices = new HashMap<>();
	private boolean isEmergencyDelivery = false;
	private ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public List<Statistics> getMultipleStatistics() throws Exception {
		if (executorService != null) {
			for (Future future : clientExecutionPool) {
				future.cancel(true);
			}
		}
		reentrantLock.lock();
		try {
			if (!isEmergencyDelivery) {
				int currentSizeCacheClients = cachedClients.size();
				retrieveClients();
				retrieveService();
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

	@Override
	public void controlProperty(ControllableProperty controllableProperty) throws Exception {
		String property = controllableProperty.getProperty();
		String value = String.valueOf(controllableProperty.getValue());
		String deviceId = controllableProperty.getDeviceId();
		reentrantLock.lock();
		try {
			if (StringUtils.isNullOrEmpty(deviceId) || !cachedAggregatedDevices.containsKey(deviceId)) {
				return;
			}
			aggregatedDeviceControl(property, value, cachedAggregatedDevices.get(deviceId));
		} finally {
			reentrantLock.unlock();
		}
	}

	@Override
	public void controlProperties(List<ControllableProperty> list) throws Exception {
		for (ControllableProperty controllableProperty : list) {
			controlProperty(controllableProperty);
		}
	}

	@Override
	protected void authenticate() throws Exception {

	}

	@Override
	public List<AggregatedDevice> retrieveMultipleStatistics() throws Exception {
		if (logger.isWarnEnabled()) {
			logger.warn("Start call retrieveMultipleStatistic");
		}
		return cachedAggregatedDevices.values().stream().collect(Collectors.toList());

	}

	@Override
	public List<AggregatedDevice> retrieveMultipleStatistics(List<String> list) throws Exception {
//		 return retrieveMultipleStatistics().stream().filter(aggregatedDevice -> listDeviceId.contains(aggregatedDevice.getDeviceId())).collect(Collectors.toList());
		return null;
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
	 * Get key value of client in cached client list
	 *
	 * @param client client to get key value
	 */
	private String getKeyInCachedClients(Client client) {
		return Optional.ofNullable(client.getNetwork().getMac()).orElse(null);
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
	 * Populate device monitoring
	 *
	 * @param properties properties of device
	 * @param client client to get information for properties
	 */
	private void populateDeviceMonitoring(Map<String, String> properties, Client client) {
		AggregatedDevice aggregatedDevice = new AggregatedDevice();
		properties.put(ClientInfoMetric.DEVICE_ID.getName(), getDefaultValueForNullOrEmpty(client.getClientId()));
		properties.put(ClientInfoMetric.DEVICE_TYPE.getName(), getDefaultValueForNullOrEmpty(client.getType()));
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
		aggregatedDevice.setProperties(properties);

		cachedAggregatedDevices.put(client.getClientId(), aggregatedDevice);
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

		addAdvanceControlProperties(advancedControllableProperties, properties,
				ControllablePropertyFactory.createDropdown(ChannelInfoMetric.SELECT_CHANNEL.getName(),
						serviceNames, Optional.ofNullable(client.getActivity()).map(Activity::getCurrentService).map(Service::getName).orElse(TriplePlayConstrant.NONE)));
		properties.put(ChannelInfoMetric.LAST_CHANNEL.getName(), Optional.ofNullable(client.getActivity()).map(Activity::getLastService).map(Service::getName).orElse(TriplePlayConstrant.NONE));
	}

	/**
	 * Retrieve information of the clients in a thread
	 *
	 * @param clientIps List client will be get information in a thread
	 */
	private void retrieveClient(List<String> clientIps) {
		int clientNumber = 0;
		while (clientNumber < clientIps.size()) {
			QueryClientRequestBodyV2 queryClientRequest = new QueryClientRequestBodyV2();
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
					convertServicetoAllService(client);
					addClientToCachedClients(client);
					Map<String, String> properties = new HashMap<>();
					populateDeviceMonitoring(properties, client);
					populateDeviceControlling(properties, client, new ArrayList<>());
					AggregatedDevice aggregatedDevice = new AggregatedDevice();
					aggregatedDevice.setProperties(properties);
					aggregatedDevice.setDeviceId(getDefaultValueForNullOrEmpty(client.getClientId()));
					aggregatedDevice.setType(getDefaultValueForNullOrEmpty(client.getType()));
					cachedAggregatedDevices.put(client.getClientId(), aggregatedDevice);
				}
			} catch (Exception e) {
				throw new ResourceNotReachableException(e.getMessage(), e);
			}
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
	 * calculating minimum of polling interval
	 */
	private int calculatingMinPollingInterval() {
		if (!cachedClients.isEmpty()) {
			return IntMath.divide(cachedClients.size(), TriplePlayConstrant.MAX_THREAD_QUANTITY * TriplePlayConstrant.MAX_CLIENT_QUANTITY_PER_THREAD, RoundingMode.CEILING);
		}
		return TriplePlayConstrant.MIN_POLLING_INTERVAL;
	}

	/**
	 * calculating minimum of polling interval
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
			throw new IllegalArgumentException(String.format("Unexpected pollingInterval value: %s", pollingInterval));
		}
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
		if (cachedClients.size() == 0) {
			lastClientMac = null;
			return;
		}

		Iterator<Entry<String, Client>> cacheClientIt = cachedClients.entrySet().iterator();

		String itClient = null;
		if (lastClientKey != null) {
			while (cacheClientIt.hasNext()) {
				if (cacheClientIt.next().getKey() == lastClientKey) {
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
			executorService.submit(new ClientLoader(requestClientMac));
		}

		//Update next ClientIp to next getMultipleStatistics
		lastClientMac = itClient != cachedClients.lastKey() ? itClient : null;
	}

	/**
	 * Retrieve all service
	 */
	private void retrieveService() {
		try {
			GetAllServicesRequestBody requestBody = new GetAllServicesRequestBody();
			requestBody.setJsonrpc(TriplePlayConstrant.JSON_RPC);
			requestBody.setParam(-1);
			String response = doPost(TriplePlayURL.BASE_URI, requestBody.buildRequestBody());
			ServiceWrapper monitoringData = objectMapper.readValue(response, ServiceWrapper.class);
			for (Service service : monitoringData.getServices()) {
				cachedServices.put(String.valueOf(service.getId()), service);
			}
			logger.debug(cachedServices);
		} catch (Exception e) {
			throw new ResourceNotReachableException(e.getMessage(), e);
		}
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
		return null;
	}

	/**
	 * Retrieve number of clients and ips of them
	 */
	private void retrieveClients() {
		try {
			QueryClientsListRequestBody queryClientsListRequest = new QueryClientsListRequestBody();
			queryClientsListRequest.setJsonrpc(TriplePlayConstrant.JSON_RPC);
			queryClientsListRequest.setField("clientType");
			queryClientsListRequest.setOperation("is");
			queryClientsListRequest.setValue(TriplePlayConstrant.SET_TOP_BOX);
			queryClientsListRequest.getInformation().add("network");
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
			throw new ResourceNotReachableException(e.getMessage(), e);
		}
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
			ControllingRequest controllingRequest = new ControllingRequest(client.getDeviceId(), value);
			ResponseEntity<?> response = doRequest(TriplePlayURL.BASE_URI, HttpMethod.PUT, headers, controllingRequest.buildRequestBody(), String.class);
			Optional<?> responseBody = Optional.ofNullable(response)
					.map(HttpEntity::getBody);
			if (response.getStatusCode().is2xxSuccessful() && responseBody.isPresent()) {
				client.getProperties().put(ChannelInfoMetric.SELECT_CHANNEL.getName(), cachedServices.get(value).getName());

				//Create request to check channel was change
				QueryClientRequestBodyV2 requestBodyV2 = new QueryClientRequestBodyV2();
				requestBodyV2.setJsonrpc(2);
				requestBodyV2.getClientMAC().add(client.getProperties().get(NetworkMetric.MAC_ADDRESS.getName()));
				requestBodyV2.getInformation().add(TriplePlayConstrant.ACTIVITY_INFORMATION);
				String responseOfQueryRequest = doPost(TriplePlayURL.BASE_URI, requestBodyV2.buildRequestBody());
				MonitoringData monitoringData = objectMapper.readValue(responseOfQueryRequest, MonitoringData.class);
				Client responseClient = monitoringData.getClientWrapper().getClients().get(0);

				if (client.getProperties().get(ChannelInfoMetric.SELECT_CHANNEL.getName()) != responseClient.getActivity().getCurrentService().getName()) {
					throw new IllegalStateException(String.format("Can not control channel of device %s, unknow error", client.getDeviceId()));
				}
				cachedAggregatedDevices.put(client.getDeviceId(), client);
			} else {
				throw new IllegalStateException(String.format("Can not change channel of device %s", client.getDeviceId()));
			}
		} catch (Exception e) {
			throw new ResourceNotReachableException(e.getMessage(), e);
		}
	}

	/**
	 * This method is used for calling control channel properties
	 *
	 * @param property name of controllable property
	 * @param value value of controllable property
	 * @param client device need control
	 */
	private void channelControl(String property, String value, AggregatedDevice client) {
		ChannelInfoMetric channelInfoMetric = ChannelInfoMetric.getByName(property);
		switch (channelInfoMetric) {
			case SELECT_CHANNEL:
				selectChannelControl(value, client);
		}
	}

	/**
	 * Because amino device doesn't have serivces(Maybe amino die is an issue), so I use cachedServices instead.
	 */
	private void convertServicetoAllService(Client client) {
		client.setServices(cachedServices.values().stream().collect(Collectors.toCollection(ArrayList::new)));
	}
}