/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.tripleplay.common.monitoring;

/**
 * HardwareMetric defined the enum for monitoring hardware
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 3/22/2023
 * @since 1.0.0
 */
public enum HardwareMetric {
	HARDWARE_TYPE("HardwareType"),
	HARDWARE_VERSION("HardwareVersion"),
	HARDWARE_MODEL("HardwareModel"),
	SOFTWARE_VERSION("SoftwareVersion"),
	SERIAL_NUMBER("SerialNumber");

	private final String name;


	HardwareMetric(String name) {
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
}
