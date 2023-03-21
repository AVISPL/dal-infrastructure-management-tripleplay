package common;

public enum ChanneInforMetric {
	SELECT_CHANNEL("SelectChannel"),
	CURRENT_CHANNEL("CurrentChannel");

	private final String name;
	ChanneInforMetric(String name) {
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
