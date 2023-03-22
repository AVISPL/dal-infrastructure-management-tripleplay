	package common;

	/**
	 * TriplePlayConstrant
	 *
	 * @author Harry / Symphony Dev Team<br>
	 * Created on 3/16/2023
	 * @since 1.0.0
	 */
	public class TriplePlayConstrant {
		public static final String NONE="NONE";
		public static final String SPLIT_LOCALE = "_";
		public static final String SET_TOP_BOX = "STB";
		public static final int JSON_RPC = 2;
		public static final String EMPTY="";

		//Information
		public static final String HARDWARE_INFORMATION = "hardware";
		public static final String NETWORK_INFORMATION = "network";
		public static final String ACTIVITY_INFORMATION = "activity";
		public static final String SERVICES_INFORMATION = "services";
		// Thread metric
		public static final int MAX_THREAD_QUANTITY = 2;//8
		public static final int MIN_THREAD_QUANTITY = 1;
		public static final int MAX_CLIENT_QUANTITY_PER_THREAD = 10;//196
		public static final int MIN_POLLING_INTERVAL = 1;
		public static final int MAX_CLIENT_QUANTITY_PER_REQUEST = 5;//28

		public static final String HASH = "#";
	}