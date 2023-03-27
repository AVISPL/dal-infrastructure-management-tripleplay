/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.tripleplay.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * ClientWrapper
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 3/14/2023
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientWrapper {
	private List<Client> clients = new ArrayList<>();

	/**
	 * Retrieves {@link #clients}
	 *
	 * @return value of {@link #clients}
	 */
	public List<Client> getClients() {
		return clients;
	}

	/**
	 * Sets {@link #clients} value
	 *
	 * @param clients new value of {@link #clients}
	 */
	public void setClients(List<Client> clients) {
		this.clients = clients;
	}

	@Override
	public String toString() {
		return "ClientWrapper{" +
				"clients=" + clients +
				'}';
	}
}