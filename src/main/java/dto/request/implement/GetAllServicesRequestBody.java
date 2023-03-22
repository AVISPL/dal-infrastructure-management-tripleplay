package dto.request.implement;

import dto.request.RequestBody;

/**
 * GetAllServicesRequest
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 3/15/2023
 * @since 1.0.0
 */
public class GetAllServicesRequestBody implements RequestBody {
	private int jsonrpc;
	private final String METHOD="GetAllServices";
	private int param;

	public GetAllServicesRequestBody() {
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
	public String buildRequestBody() {
		return String.format("{\"jsonrpc\":%d,\"method\":\"%s\",\"params\":[%d]}",this.jsonrpc,this.METHOD,this.param);
	}
}