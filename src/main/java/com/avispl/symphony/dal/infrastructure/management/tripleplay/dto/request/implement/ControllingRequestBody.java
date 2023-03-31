/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.tripleplay.dto.request.implement;

import com.avispl.symphony.dal.infrastructure.management.tripleplay.dto.request.RequestBody;

/**
 * Request body to control client channel
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 3/22/2023
 * @since 1.0.0
 */
public class ControllingRequestBody implements RequestBody {
	private String clientId;
	private String serviceId;

	/**
	 * Create a Controlling request with full param
	 *
	 * @param clientId id of client
	 * @param serviceId id of service
	 */
	public ControllingRequestBody(String clientId, String serviceId) {
		this.clientId = clientId;
		this.serviceId = serviceId;
	}

	@Override
	public String buildRequestBody() {
		return String.format("{\"jsonrpc\":\"2.0\",\"method\":\"ChangeChannel\",\"params\":[%s,%s]}", this.clientId, this.serviceId);
	}
}