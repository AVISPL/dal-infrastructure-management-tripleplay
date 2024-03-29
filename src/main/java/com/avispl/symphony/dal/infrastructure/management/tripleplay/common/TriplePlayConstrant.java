	/*
	 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
	 */

	package com.avispl.symphony.dal.infrastructure.management.tripleplay.common;

	/**
	 * TriplePlayConstrant
	 *
	 * @author Harry / Symphony Dev Team<br>
	 * Created on 3/16/2023
	 * @since 1.0.0
	 */
	public class TriplePlayConstrant {
		public static final String NONE = "None";
		public static final String SPLIT_LOCALE = "_";
		public static final String SET_TOP_BOX = "STB";
		public static final int JSON_RPC = 2;
		public static final String EMPTY = "";
		public static final String HASH = "#";
		public static final String CLIENT_TYPE = "clientType";
		public static final String IS = "is";
		public static final String ONLINE = "Connected";

		//Information
		public static final String HARDWARE_INFORMATION = "hardware";
		public static final String NETWORK_INFORMATION = "network";
		public static final String ACTIVITY_INFORMATION = "activity";
		public static final String SERVICES_INFORMATION = "services";
		// Thread metric
		public static final int MAX_THREAD_QUANTITY = 8;
		public static final int MIN_THREAD_QUANTITY = 1;
		public static final int MAX_CLIENT_QUANTITY_PER_THREAD = 196;
		public static final int MIN_POLLING_INTERVAL = 1;
		public static final int MAX_CLIENT_QUANTITY_PER_REQUEST = 28;
		public static final String COMMA = ",";
	}