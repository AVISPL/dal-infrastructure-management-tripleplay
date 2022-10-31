/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.tripleplay;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import com.avispl.symphony.api.dal.control.Controller;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.Statistics;
import com.avispl.symphony.api.dal.dto.monitor.aggregator.AggregatedDevice;
import com.avispl.symphony.api.dal.monitor.Monitorable;
import com.avispl.symphony.api.dal.monitor.aggregator.Aggregator;
import com.avispl.symphony.dal.communicator.RestCommunicator;

/**
 * TriplePlay Aggregator Communicator Adapter
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 10/30/2022
 * @since 1.0.0
 */
public class TriplePlayDeviceCommunicator extends RestCommunicator implements Aggregator, Monitorable, Controller {
	private ExtendedStatistics localExtendedStatistics;

	/**
	 * ReentrantLock to prevent null pointer exception to localExtendedStatistics when controlProperty method is called before GetMultipleStatistics method.
	 */
	private final ReentrantLock reentrantLock = new ReentrantLock();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Statistics> getMultipleStatistics() {

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Getting statistics from Philips Hue at host %s with port %s", this.host, this.getPort()));
		}
		reentrantLock.lock();
		try {
			ExtendedStatistics extendedStatistics = new ExtendedStatistics();
			Map<String, String> stats = new HashMap<>();

			stats.put("TriplePlay","Name");
			extendedStatistics.setStatistics(stats);
			localExtendedStatistics = extendedStatistics;
		} finally {
			reentrantLock.unlock();
		}
		return Collections.singletonList(localExtendedStatistics);
	}

	@Override
	public void controlProperty(ControllableProperty controllableProperty) throws Exception {

	}

	@Override
	public void controlProperties(List<ControllableProperty> list) throws Exception {

	}

	@Override
	public List<AggregatedDevice> retrieveMultipleStatistics() throws Exception {
		return null;
	}

	@Override
	public List<AggregatedDevice> retrieveMultipleStatistics(List<String> list) throws Exception {
		return null;
	}

	@Override
	protected void authenticate() throws Exception {

	}
}