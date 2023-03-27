/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.tripleplay.common.controlling;

import java.util.Arrays;

/**
 * AggregatorGroupControllingMetric defined the constant for display type of controllable properties ( switch, slider, button,...)
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 3/22/2023
 * @since 1.0.0
 */
public enum AggregatorGroupControllingMetric {
	CHANNEL("ChannelControl"),
	AUDIO_CONTROL("AudioControl");


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
		return Arrays.stream(AggregatorGroupControllingMetric.values()).filter(c -> name.equals(c.getName())).
				findFirst().orElseThrow(()->new IllegalStateException(String.format("The %s control not exist", name)));
	}
}