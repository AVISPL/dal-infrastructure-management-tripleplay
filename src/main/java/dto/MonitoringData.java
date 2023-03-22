/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package dto;

import java.util.List;

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
	@JsonAlias("result")
	private List<Service> services;
	@JsonAlias("jsonrpc")
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

	/**
	 * Retrieves {@link #services}
	 *
	 * @return value of {@link #services}
	 */
	public List<Service> getServices() {
		return services;
	}

	/**
	 * Sets {@link #services} value
	 *
	 * @param services new value of {@link #services}
	 */
	public void setServices(List<Service> services) {
		this.services = services;
	}

	@Override
	public String toString() {
		return "MonitoringData{" +
				"clientWrapper=" + clientWrapper +
				", jsonrpc='" + jsonrpc + '\'' +
				'}';
	}
}