/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.tripleplay.dto.request.implement;

import java.util.ArrayList;
import java.util.List;

import com.avispl.symphony.dal.infrastructure.management.tripleplay.dto.request.RequestBody;

import com.avispl.symphony.dal.util.StringUtils;

/**
 * Request to get information of client
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 3/17/2023
 * @since 1.0.0
 */
public class QueryClientRequestBody implements RequestBody {
	private int jsonrpc;
	private final String METHOD = "QueryClients";
	private List<String> clientMAC = new ArrayList<>();
	private List<String> information = new ArrayList<>();

	/**
	 * Retrieves {@link #clientMAC}
	 *
	 * @return value of {@link #clientMAC}
	 */
	public List<String> getClientMAC() {
		return clientMAC;
	}

	/**
	 * Sets {@link #clientMAC} value
	 *
	 * @param clientMAC new value of {@link #clientMAC}
	 */
	public void setClientMAC(List<String> clientMAC) {
		this.clientMAC = clientMAC;
	}

	/**
	 * Retrieves {@link #jsonrpc}
	 *
	 * @return value of {@link #jsonrpc}
	 */
	public int getJsonrpc() {
		return jsonrpc;
	}

	/**
	 * Sets {@link #jsonrpc} value
	 *
	 * @param jsonrpc new value of {@link #jsonrpc}
	 */
	public void setJsonrpc(int jsonrpc) {
		this.jsonrpc = jsonrpc;
	}

	/**
	 * Retrieves {@link #METHOD}
	 *
	 * @return value of {@link #METHOD}
	 */
	public String getMETHOD() {
		return METHOD;
	}

	/**
	 * Retrieves {@link #information}
	 *
	 * @return value of {@link #information}
	 */
	public List<String> getInformation() {
		return information;
	}

	/**
	 * Sets {@link #information} value
	 *
	 * @param information new value of {@link #information}
	 */
	public void setInformation(List<String> information) {
		this.information = information;
	}

	@Override
	public String buildRequestBody() {

		//Generate params string
		StringBuilder params = new StringBuilder();
		for (int i = 0; i < this.clientMAC.size(); ++i) {
			params.append("{");
			if (i != 0) {
				params.append("\"logical\":\"OR\",");
			}
			params.append(String.format("\n"
					+ "        \"field\": \"macAddress\",\n"
					+ "        \"operator\": \"is\",\n"
					+ "        \"value\": \"%s\"\n"
					+ "      }", this.clientMAC.get(i)));
			if (i != clientMAC.size() - 1) {
				params.append(",");
			}
		}

		//Generate information string
		StringBuilder informationString = new StringBuilder();
		for (String info : this.information) {
			informationString.append("\"" + info + "\":true,");
		}
		if (!StringUtils.isNullOrEmpty(informationString.toString())) {
			informationString.deleteCharAt(informationString.length() - 1);
		}

		StringBuilder request = new StringBuilder(String.format("{\"jsonrpc\":%d,\"method\":\"%s\",\""
				+ "params\":[[%s],{%s"
				+ "},\"ipAddress\",-1]}", this.jsonrpc, this.METHOD, params, informationString));
		return request.toString();
	}
}