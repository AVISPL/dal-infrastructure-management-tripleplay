/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.tripleplay.common.controlling;

import java.util.Arrays;
import java.util.Optional;

/**
 * AggregatorGroupControllingMetric
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 3/22/2023
 * @since 1.0.0
 */
public enum AggregatorGroupControllingMetric {
	CHANNEL("ChannelControl"),
	AUDIO_CONTROL("AudioControl"),
	AGGREGATED_DEVICE("AggregatedDevice");


	private final String name;

	AggregatorGroupControllingMetric(String name) {
		this.name = name;
	}

	/**
	 * Retrieves {@link #name}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * This method is used to get controlling metric group by name
	 *
	 * @param name is the name of management metric that want to get
	 * @return AggregatorManagementGroupMetric is the management metric group that want to get
	 */
	public static AggregatorGroupControllingMetric getByName(String name) {
		Optional<AggregatorGroupControllingMetric> managementGroupMetric = Arrays.stream(AggregatorGroupControllingMetric.values()).filter(c -> name.equals(c.getName())).findFirst();
		if (managementGroupMetric.isPresent()) {
			return managementGroupMetric.get();
		}
		return AggregatorGroupControllingMetric.AGGREGATED_DEVICE;
	}
}