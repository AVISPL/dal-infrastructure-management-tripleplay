/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.tripleplay.common.controlling;

import java.util.Arrays;

/**
 * defined the ChannelInfo in controlling device for monitoring and controlling process
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 3/22/2023
 * @since 1.0.0
 */
public enum ChannelMetric {
	SELECT_CHANNEL("SelectChannel"),
	LAST_CHANNEL("LastChannel");

	private final String name;

	ChannelMetric(String name) {
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

	public static ChannelMetric getByName(String name) {
		return Arrays.stream(ChannelMetric.values()).filter(c -> name.equals(c.getName())).
				findFirst().orElseThrow(()->new IllegalStateException(String.format("The %s control not exist", name)));
	}
}
