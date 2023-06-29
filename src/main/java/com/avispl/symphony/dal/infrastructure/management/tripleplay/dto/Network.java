/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.tripleplay.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Network store some network information of client like ip adress, mac adress, hamepage, dhcp subnet,...
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 3/13/2023
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Network {
	private String ip;
	private String mac;
	private String homepage;
	private String dhcpSubnet;

	/**
	 * Retrieves {@link #ip}
	 *
	 * @return value of {@link #ip}
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * Sets {@link #ip} value
	 *
	 * @param ip new value of {@link #ip}
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * Retrieves {@link #mac}
	 *
	 * @return value of {@link #mac}
	 */
	public String getMac() {
		return mac;
	}

	/**
	 * Sets {@link #mac} value
	 *
	 * @param mac new value of {@link #mac}
	 */
	public void setMac(String mac) {
		this.mac = mac;
	}

	/**
	 * Retrieves {@link #homepage}
	 *
	 * @return value of {@link #homepage}
	 */
	public String getHomepage() {
		return homepage;
	}

	/**
	 * Sets {@link #homepage} value
	 *
	 * @param homepage new value of {@link #homepage}
	 */
	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}

	/**
	 * Retrieves {@link #dhcpSubnet}
	 *
	 * @return value of {@link #dhcpSubnet}
	 */
	public String getDhcpSubnet() {
		return dhcpSubnet;
	}

	/**
	 * Sets {@link #dhcpSubnet} value
	 *
	 * @param dhcpSubnet new value of {@link #dhcpSubnet}
	 */
	public void setDhcpSubnet(String dhcpSubnet) {
		this.dhcpSubnet = dhcpSubnet;
	}

	@Override
	public String toString() {
		return "Network{" +
				"ip='" + ip + '\'' +
				", mac='" + mac + '\'' +
				", homepage='" + homepage + '\'' +
				", dhcpSubnet='" + dhcpSubnet + '\'' +
				'}';
	}
}