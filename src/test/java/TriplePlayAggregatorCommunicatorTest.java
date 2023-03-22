/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.TriplePlayConstrant;
import common.TriplePlayURL;
import common.monitoring.ClientInfoMetric;
import common.monitoring.NetworkMetric;
import dto.Client;
import dto.MonitoringData;
import dto.request.implement.ControllingRequest;
import dto.request.implement.QueryClientRequestBodyV2;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.avispl.symphony.api.dal.dto.monitor.aggregator.AggregatedDevice;
import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;

/**
 * TriplePlayTest
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 3/13/2023
 * @since 1.0.0
 */
public class TriplePlayAggregatorCommunicatorTest {
	private final TriplePlayAggregatorCommunicator triplePlayAggregatorCommunicator = new TriplePlayAggregatorCommunicator();
	private final ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach()
	public void setUp() throws Exception {
		triplePlayAggregatorCommunicator.setHost("10.34.41.125");
		triplePlayAggregatorCommunicator.setTrustAllCertificates(true);
		triplePlayAggregatorCommunicator.setPort(80);
		triplePlayAggregatorCommunicator.setTrustAllCertificates(true);
		triplePlayAggregatorCommunicator.setProtocol("http");
		triplePlayAggregatorCommunicator.setContentType("application/json");
		triplePlayAggregatorCommunicator.init();
	}

	@AfterEach()
	public void destroy() throws Exception {
		triplePlayAggregatorCommunicator.disconnect();
	}

	/**
	 * Test getMultipleStatistics
	 * Expect getMultipleStatistics successfully
	 */
	@Test
	public void testGetMultipleStatistics() throws Exception {
		int delayTime = 3000;
		int runNumber = 3;
		for (int i = 0; i < runNumber; ++i) {
			triplePlayAggregatorCommunicator.getMultipleStatistics();
			triplePlayAggregatorCommunicator.retrieveMultipleStatistics();
			Thread.sleep(delayTime);
			List<AggregatedDevice> aggregatedDevices = triplePlayAggregatorCommunicator.retrieveMultipleStatistics();

			for (AggregatedDevice aggregatedDevice : aggregatedDevices) {
				Assertions.assertNotNull(aggregatedDevice.getProperties().get(ClientInfoMetric.DEVICE_ID.getName()));
				Assertions.assertNotNull(aggregatedDevice.getProperties().get(ClientInfoMetric.DEVICE_TYPE.getName()));
				Assertions.assertNotNull(aggregatedDevice.getProperties().get(ClientInfoMetric.LOCALE.getName()));
				Assertions.assertNotNull(aggregatedDevice.getProperties().get(ClientInfoMetric.LOCALTION.getName()));
			}
		}
	}

	/**
	 * Test controlProperty
	 * Expect controlProperty successfully
	 */
	@Test
	public void testControlProperty() throws Exception {
		int delayTime = 2000;
		triplePlayAggregatorCommunicator.getMultipleStatistics();
		Thread.sleep(delayTime);
		triplePlayAggregatorCommunicator.retrieveMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setDeviceId("5");
		controllableProperty.setProperty("ChannelControl#SelectChannel");
		controllableProperty.setValue("3");
		triplePlayAggregatorCommunicator.controlProperty(controllableProperty);
	}
}