package common;

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
