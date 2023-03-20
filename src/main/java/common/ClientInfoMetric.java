package common;

/**
 * ClientMetric
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 3/20/2023
 * @since 1.0.0
 */
public enum ClientInfoMetric {
	DEVICE_ID("deviceId"),
	DEVICE_TYPE("deviceType"),
	LOCALE("Locale"),
	LOCALTION("location");

	private final String name;

	ClientInfoMetric(String name) {
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