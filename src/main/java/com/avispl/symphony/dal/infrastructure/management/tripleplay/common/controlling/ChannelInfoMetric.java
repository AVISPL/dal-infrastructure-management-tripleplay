/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.tripleplay.common.controlling;

import java.util.Arrays;
import java.util.Optional;

/**
 * ChannelInfoMetric
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 3/22/2023
 * @since 1.0.0
 */
public enum ChannelInfoMetric {
	SELECT_CHANNEL("SelectChannel"),
	LAST_CHANNEL("LastChannel"),
	OTHER("Other");

	private final String name;

	ChannelInfoMetric(String name) {
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

	public static ChannelInfoMetric getByName(String name) {
		Optional<ChannelInfoMetric> channelInfoMetric = Arrays.stream(ChannelInfoMetric.values()).filter(c -> name.equals(c.getName())).findFirst();
		if (channelInfoMetric.isPresent()) {
			return channelInfoMetric.get();
		}
		return ChannelInfoMetric.OTHER;
	}
}
