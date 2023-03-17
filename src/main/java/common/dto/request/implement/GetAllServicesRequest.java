package common.dto.request.implement;

import common.dto.request.Request;

/**
 * GetAllServicesRequest
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 3/15/2023
 * @since 1.0.0
 */
public class GetAllServicesRequest implements Request {
	private int jsonrpc;
	private final String METHOD="GetAllServices";
	private int param;

	public GetAllServicesRequest() {
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
	 * Retrieves {@link #param}
	 *
	 * @return value of {@link #param}
	 */
	public int getParam() {
		return param;
	}

	/**
	 * Sets {@link #param} value
	 *
	 * @param param new value of {@link #param}
	 */
	public void setParam(int param) {
		this.param = param;
	}

	@Override
	public String buildRequest() {
		return String.format("{\"jsonrpc\":%d,\"method\":\"%s\",\"params\":[%d]}",this.jsonrpc,this.METHOD,this.param);
	}
}