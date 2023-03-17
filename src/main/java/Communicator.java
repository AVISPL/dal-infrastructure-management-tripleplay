import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.math.IntMath;
import common.TriplePlayConstrant;
import common.TriplePlayURL;
import common.dto.Client;
import common.dto.MonitoringData;
import common.dto.Service;
import common.dto.ServiceWrapper;
import common.dto.request.implement.GetAllServicesRequest;
import common.dto.request.implement.QueryClientsListRequest;
import common.dto.request.implement.QueryClientsRequest;

import com.avispl.symphony.api.dal.control.Controller;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.Statistics;
import com.avispl.symphony.api.dal.dto.monitor.aggregator.AggregatedDevice;
import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
import com.avispl.symphony.api.dal.monitor.Monitorable;
import com.avispl.symphony.api.dal.monitor.aggregator.Aggregator;
import com.avispl.symphony.dal.communicator.RestCommunicator;
import com.avispl.symphony.dal.util.StringUtils;

/**
 * Communicator
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 3/13/2023
 * @since 1.0.0
 */
public class Communicator extends RestCommunicator implements Aggregator, Monitorable, Controller {

	class ClientLoader implements Runnable {
		private volatile int threadNumber;

		public ClientLoader(int threadNumber) {
			this.threadNumber = threadNumber;
		}

		@Override
		public void run() {
			if (!cacheClients.isEmpty()) {
				retrieveClient(threadNumber);
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
	 * configManagement in boolean value
	 */
	private boolean isConfigManagement;

	/**
	 * store pollingInterval adapter properties
	 */
	private volatile String pollingInterval;

	private volatile int currentPhase = 0;
	private List<Future> clientExecutionPool = new ArrayList<>();
	private Map<String, Client> cacheClients = new HashMap<>();
	private List<Service> cacheServices = new ArrayList<>();
	private ExtendedStatistics localExtendedStatistics;
	private boolean isEmergencyDelivery = false;
	private ObjectMapper objectMapper = new ObjectMapper();

	private void updateCacheClient(List<Client> clients) {
		for (Client client : clients) {
			if (!StringUtils.isNullOrEmpty(client.getLocale())) {
				client.setLocale(getDisplayLocalByLocale(client.getLocale()));
			}
			cacheClients.put(client.getClientId(), client);
		}
	}

	private void retrieveClient(int threadNumber) {
		QueryClientsRequest queryClientsRequest = new QueryClientsRequest();
		queryClientsRequest.setJsonrpc(2);
		queryClientsRequest.setField("clientType");
		queryClientsRequest.setOperation("is");
		queryClientsRequest.setValue("STB");
		queryClientsRequest.getInformation().add("hardware");
		queryClientsRequest.getInformation().add("network");
		queryClientsRequest.getInformation().add("activity");
		queryClientsRequest.getInformation().add("services");
		queryClientsRequest.setClientNumber(String.valueOf(TriplePlayConstrant.MAX_CLIENT_QUANTITY_PER_THREAD));
		int devicesPerPollingIntervalQuantity = IntMath.divide(cacheClients.size(), localPollingInterval, RoundingMode.CEILING);
		queryClientsRequest.setPage(String.valueOf(currentPhase * TriplePlayConstrant.MAX_THREAD_QUANTITY + threadNumber + 1));
		try {
			String response = doPost(TriplePlayURL.BASE_URI, queryClientsRequest.buildRequest());
			MonitoringData monitoringData = objectMapper.readValue(response, MonitoringData.class);
			updateCacheClient(monitoringData.getClientWrapper().getClients());
		} catch (Exception e) {
			throw new ResourceNotReachableException(e.getMessage(), e);
		}

	}

	/**
	 * calculating thread quantity
	 */
	private int calculatingThreadQuantity() {
		if (cacheClients.isEmpty()) {
			return TriplePlayConstrant.MIN_THREAD_QUANTITY;
		}
		if (cacheClients.size() / localPollingInterval < TriplePlayConstrant.MAX_THREAD_QUANTITY * TriplePlayConstrant.MAX_CLIENT_QUANTITY_PER_THREAD) {
			return IntMath.divide(cacheClients.size(), localPollingInterval * TriplePlayConstrant.MAX_CLIENT_QUANTITY_PER_THREAD, RoundingMode.CEILING);
		}
		return TriplePlayConstrant.MAX_THREAD_QUANTITY;
	}

	/**
	 * calculating minimum of polling interval
	 */
	private int calculatingMinPollingInterval() {
		if (!cacheClients.isEmpty()) {
			return IntMath.divide(cacheClients.size()
					, TriplePlayConstrant.MAX_THREAD_QUANTITY * TriplePlayConstrant.MAX_CLIENT_QUANTITY_PER_THREAD
					, RoundingMode.CEILING);
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
				Map<String, String> stats = new HashMap<>();
				int currentSizeCacheClients = cacheClients.size();
				retrieveClients();
				retrieveService();
				localPollingInterval = calculatingLocalPollingInterval();
				deviceStatisticsCollectionThreads = calculatingThreadQuantity();
				//Multithread for get information of client
				if (executorService == null || currentSizeCacheClients != cacheClients.size()) {
					executorService = Executors.newFixedThreadPool(deviceStatisticsCollectionThreads);
				}
				for (int threadNumber = 0; threadNumber < deviceStatisticsCollectionThreads; threadNumber++) {
					Future future = executorService.submit(new ClientLoader(threadNumber));
					clientExecutionPool.add(future);
				}
				currentPhase = (currentPhase + 1) % localPollingInterval;
			}
			isEmergencyDelivery = false;
		} finally {
			reentrantLock.unlock();
		}
		return null;
	}

	private void retrieveService() {
		try {
			GetAllServicesRequest requestBody = new GetAllServicesRequest();
			requestBody.setJsonrpc(2);
			requestBody.setParam(-1);
			String response = doPost(TriplePlayURL.BASE_URI, requestBody.buildRequest());
			ServiceWrapper monitoringData = objectMapper.readValue(response, ServiceWrapper.class);
			cacheServices = monitoringData.getServices();
		} catch (Exception e) {
			throw new ResourceNotReachableException(e.getMessage(), e);
		}
	}

	private String getDisplayLocalByLocale(String locale) {
		if (locale.contains("_")) {
			Locale local = new Locale(locale.substring(0, locale.indexOf("_")), locale.substring(locale.indexOf("_") + 1));
			return local.getDisplayName();
		}
		return null;
	}

	public double calcTimePerRequest(int numRequest) throws Exception {
		QueryClientsRequest queryClientsRequest = new QueryClientsRequest();
		queryClientsRequest.setJsonrpc(2);
		queryClientsRequest.setField("clientType");
		queryClientsRequest.setOperation("is");
		queryClientsRequest.setValue("STB");
		queryClientsRequest.getInformation().add("hardware");
		queryClientsRequest.getInformation().add("network");
		queryClientsRequest.getInformation().add("activity");
		queryClientsRequest.getInformation().add("configuration");
		queryClientsRequest.getInformation().add("services");

		long sumSecond = 0;
		for (int i = 0; i < numRequest; ++i) {
			long time1 = System.currentTimeMillis();
			String response = doPost(TriplePlayURL.BASE_URI,
					"{\"jsonrpc\":2.0,\"method\":\"QueryClients\",\"params\":[[{\"field\":\"clientType\",\"operator\":\"is\",\"value\":\"STB\"},{\"logical\":\"OR\",\"field\":\"clientType\",\"operator\":\"is\",\"value\":\"MVP\"},{\"logical\":\"OR\",\"field\":\"clientType\",\"operator\":\"is\",\"value\":\"PC\"}],{\"hardware\":true,\"network\":true,\"activity\":true,\"services\":true,\"configuration\":true},\"ipAddress\",-1]}");
			long time2 = System.currentTimeMillis();
			sumSecond += (time2 - time1);
			MonitoringData monitoringData = objectMapper.readValue(response, MonitoringData.class);
			System.out.println("Num clients: " + monitoringData.getClientWrapper().getClients().size());
			System.out.println("Request " + (i + 1) + ": " + (time2 - time1));
			if (time2 - time1 > 5000) {
				System.out.println(response);
			}
		}
		return sumSecond / numRequest;
	}

	private void retrieveClients() {
		try {
			QueryClientsListRequest queryClientsListRequest = new QueryClientsListRequest();
			queryClientsListRequest.setJsonrpc(2);
			queryClientsListRequest.setField("clientType");
			queryClientsListRequest.setOperation("is");
			queryClientsListRequest.setValue("STB");
			String response = doPost(TriplePlayURL.BASE_URI, queryClientsListRequest.buildRequest());
			MonitoringData monitoringData = objectMapper.readValue(response, MonitoringData.class);
			updateCacheClient(monitoringData.getClientWrapper().getClients());
		} catch (Exception e) {
			throw new ResourceNotReachableException(e.getMessage(), e);
		}
	}

	private String buildDeviceFullPath(String path) {

		return "";
	}

	@Override
	public void controlProperty(ControllableProperty controllableProperty) throws Exception {

	}

	@Override
	public void controlProperties(List<ControllableProperty> list) throws Exception {

	}

	@Override
	protected void authenticate() throws Exception {

	}

	@Override
	public List<AggregatedDevice> retrieveMultipleStatistics() throws Exception {
		return null;
	}

	@Override
	public List<AggregatedDevice> retrieveMultipleStatistics(List<String> list) throws Exception {
		return null;
	}
}