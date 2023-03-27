/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.tripleplay.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Hardware store some hardware information of client like type, software version, serial number, hardware version, model...
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 3/13/2023
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Hardware {
	private String type;
	private String softwareVersion;
	private String serialNumber;
	private String hardwareVersion;
	private String model;

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
	 * Retrieves {@link #softwareVersion}
	 *
	 * @return value of {@link #softwareVersion}
	 */
	public String getSoftwareVersion() {
		return softwareVersion;
	}

	/**
	 * Sets {@link #softwareVersion} value
	 *
	 * @param softwareVersion new value of {@link #softwareVersion}
	 */
	public void setSoftwareVersion(String softwareVersion) {
		this.softwareVersion = softwareVersion;
	}

	/**
	 * Retrieves {@link #serialNumber}
	 *
	 * @return value of {@link #serialNumber}
	 */
	public String getSerialNumber() {
		return serialNumber;
	}

	/**
	 * Sets {@link #serialNumber} value
	 *
	 * @param serialNumber new value of {@link #serialNumber}
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	/**
	 * Retrieves {@link #hardwareVersion}
	 *
	 * @return value of {@link #hardwareVersion}
	 */
	public String getHardwareVersion() {
		return hardwareVersion;
	}

	/**
	 * Sets {@link #hardwareVersion} value
	 *
	 * @param hardwareVersion new value of {@link #hardwareVersion}
	 */
	public void setHardwareVersion(String hardwareVersion) {
		this.hardwareVersion = hardwareVersion;
	}

	/**
	 * Retrieves {@link #model}
	 *
	 * @return value of {@link #model}
	 */
	public String getModel() {
		return model;
	}

	/**
	 * Sets {@link #model} value
	 *
	 * @param model new value of {@link #model}
	 */
	public void setModel(String model) {
		this.model = model;
	}

	@Override
	public String toString() {
		return "Hardware{" +
				"type='" + type + '\'' +
				", softwareVersion='" + softwareVersion + '\'' +
				", serialNumber='" + serialNumber + '\'' +
				", hardwareVersion='" + hardwareVersion + '\'' +
				", model='" + model + '\'' +
				'}';
	}
}