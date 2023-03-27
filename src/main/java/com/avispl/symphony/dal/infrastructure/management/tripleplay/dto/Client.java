/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.tripleplay.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Client
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 3/14/2023
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Client implements Comparable<Client> {
	@JsonAlias("clientId")
	private String clientId;
	@JsonAlias("locale")
	private String locale;
	@JsonAlias("location")
	private String location;
	@JsonAlias("auxiliaryID")
	private String auxiliaryID;
	@JsonAlias("description")
	private String description;
	@JsonAlias("type")
	private String type;
	@JsonAlias("typeDescription")
	private String typeDescription;
	@JsonAlias("connectionStatus")
	private String connectionStatus;
	@JsonAlias("hardware")
	private Hardware hardware;
	@JsonAlias("network")
	private Network network;
	@JsonAlias("activity")
	private Activity activity;
	@JsonAlias("services")
	private List<Service> services = new ArrayList<>();

	/**
	 * Retrieves {@link #clientId}
	 *
	 * @return value of {@link #clientId}
	 */
	public String getClientId() {
		return clientId;
	}

	/**
	 * Sets {@link #clientId} value
	 *
	 * @param clientId new value of {@link #clientId}
	 */
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	/**
	 * Retrieves {@link #locale}
	 *
	 * @return value of {@link #locale}
	 */
	public String getLocale() {
		return locale;
	}

	/**
	 * Sets {@link #locale} value
	 *
	 * @param locale new value of {@link #locale}
	 */
	public void setLocale(String locale) {
		this.locale = locale;
	}

	/**
	 * Retrieves {@link #location}
	 *
	 * @return value of {@link #location}
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Sets {@link #location} value
	 *
	 * @param location new value of {@link #location}
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * Retrieves {@link #auxiliaryID}
	 *
	 * @return value of {@link #auxiliaryID}
	 */
	public String getAuxiliaryID() {
		return auxiliaryID;
	}

	/**
	 * Sets {@link #auxiliaryID} value
	 *
	 * @param auxiliaryID new value of {@link #auxiliaryID}
	 */
	public void setAuxiliaryID(String auxiliaryID) {
		this.auxiliaryID = auxiliaryID;
	}

	/**
	 * Retrieves {@link #description}
	 *
	 * @return value of {@link #description}
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets {@link #description} value
	 *
	 * @param description new value of {@link #description}
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Retrieves {@link #type}
	 *
	 * @return value of {@link #type}
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets {@link #type} value
	 *
	 * @param type new value of {@link #type}
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Retrieves {@link #typeDescription}
	 *
	 * @return value of {@link #typeDescription}
	 */
	public String getTypeDescription() {
		return typeDescription;
	}

	/**
	 * Sets {@link #typeDescription} value
	 *
	 * @param typeDescription new value of {@link #typeDescription}
	 */
	public void setTypeDescription(String typeDescription) {
		this.typeDescription = typeDescription;
	}

	/**
	 * Retrieves {@link #connectionStatus}
	 *
	 * @return value of {@link #connectionStatus}
	 */
	public String getConnectionStatus() {
		return connectionStatus;
	}

	/**
	 * Sets {@link #connectionStatus} value
	 *
	 * @param connectionStatus new value of {@link #connectionStatus}
	 */
	public void setConnectionStatus(String connectionStatus) {
		this.connectionStatus = connectionStatus;
	}

	/**
	 * Retrieves {@link #hardware}
	 *
	 * @return value of {@link #hardware}
	 */
	public Hardware getHardware() {
		return hardware;
	}

	/**
	 * Sets {@link #hardware} value
	 *
	 * @param hardware new value of {@link #hardware}
	 */
	public void setHardware(Hardware hardware) {
		this.hardware = hardware;
	}

	/**
	 * Retrieves {@link #network}
	 *
	 * @return value of {@link #network}
	 */
	public Network getNetwork() {
		return network;
	}

	/**
	 * Sets {@link #network} value
	 *
	 * @param network new value of {@link #network}
	 */
	public void setNetwork(Network network) {
		this.network = network;
	}

	/**
	 * Retrieves {@link #activity}
	 *
	 * @return value of {@link #activity}
	 */
	public Activity getActivity() {
		return activity;
	}

	/**
	 * Sets {@link #activity} value
	 *
	 * @param activity new value of {@link #activity}
	 */
	public void setActivity(Activity activity) {
		this.activity = activity;
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
		return "Client{" +
				"clientId='" + clientId + '\'' +
				", locale='" + locale + '\'' +
				", location='" + location + '\'' +
				", auxiliaryID='" + auxiliaryID + '\'' +
				", description='" + description + '\'' +
				", type='" + type + '\'' +
				", typeDescription='" + typeDescription + '\'' +
				", connectionStatus='" + connectionStatus + '\'' +
				", hardware=" + hardware +
				", network=" + network +
				", activity=" + activity +
				", services=" + services +
				'}';
	}

	@Override
	public int compareTo(Client client) {
		return this.network.getIp().compareTo(client.getNetwork().getIp());
	}
}