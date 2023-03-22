/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */
package dto.request.implement;

import dto.request.RequestBody;

/**
 * ControllingRequest
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 3/22/2023
 * @since 1.0.0
 */
public class ControllingRequest implements RequestBody {
	private String clientId;
	private String serviceId;

	/**
	 * Create a Controlling request with full param
	 * @param clientId id of client
	 * @param serviceId id of service
	 */
	public ControllingRequest(String clientId, String serviceId) {
		this.clientId = clientId;
		this.serviceId = serviceId;
	}

	@Override
	public String buildRequestBody() {
		return String.format("{\"jsonrpc\":\"2.0\",\"method\":\"ChangeChannel\",\"params\":[%s,%s]}",this.clientId,this.serviceId);
	}
}