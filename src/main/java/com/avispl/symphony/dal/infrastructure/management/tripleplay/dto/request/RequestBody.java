/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.tripleplay.dto.request;

/**
 * Request body use to build body for request
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 3/15/2023
 * @since 1.0.0
 */
public interface RequestBody {
	/**
	 * Build request body
	 *
	 * @return Request body
	 */
	String buildRequestBody();
}