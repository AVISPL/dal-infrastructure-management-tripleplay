/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.tripleplay.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * MonitoringData
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 3/15/2023
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MonitoringData {
	@JsonAlias("result")
	private ClientWrapper clientWrapper;
	private String jsonrpc;

	/**
	 * Retrieves {@link #clientWrapper}
	 *
	 * @return value of {@link #clientWrapper}
	 */
	public ClientWrapper getClientWrapper() {
		return clientWrapper;
	}

	/**
	 * Sets {@link #clientWrapper} value
	 *
	 * @param clientWrapper new value of {@link #clientWrapper}
	 */
	public void setClientWrapper(ClientWrapper clientWrapper) {
		this.clientWrapper = clientWrapper;
	}

	/**
	 * Retrieves {@link #jsonrpc}
	 *
	 * @return value of {@link #jsonrpc}
	 */
	public String getJsonrpc() {
		return jsonrpc;
	}

	/**
	 * Sets {@link #jsonrpc} value
	 *
	 * @param jsonrpc new value of {@link #jsonrpc}
	 */
	public void setJsonrpc(String jsonrpc) {
		this.jsonrpc = jsonrpc;
	}

	@Override
	public String toString() {
		return "MonitoringData{" +
				"clientWrapper=" + clientWrapper +
				", jsonrpc='" + jsonrpc + '\'' +
				'}';
	}
}