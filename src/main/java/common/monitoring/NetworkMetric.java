/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package common.monitoring;

/**
 * NetworkMetric
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 3/22/2023
 * @since 1.0.0
 */
public enum NetworkMetric {
	IP_ADDRESS("IPAddress"),
	MAC_ADDRESS("MACAddress"),
	DHCP_SUBNET("DHCPSubnet");

	private final String name;
	NetworkMetric(String name) {
		this.name=name;
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
