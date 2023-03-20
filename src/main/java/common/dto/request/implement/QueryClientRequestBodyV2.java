package common.dto.request.implement;

import java.util.ArrayList;
import java.util.List;

import common.dto.request.RequestBody;

import com.avispl.symphony.dal.util.StringUtils;

/**
 * QueryClientRequestV2
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 3/17/2023
 * @since 1.0.0
 */
public class QueryClientRequestBodyV2 implements RequestBody {
	private int jsonrpc;
	private final String METHOD = "QueryClients";
	private List<String> clientIp = new ArrayList<>();
	private List<String> information = new ArrayList<>();

	/**
	 * Retrieves {@link #clientIp}
	 *
	 * @return value of {@link #clientIp}
	 */
	public List<String> getClientIp() {
		return clientIp;
	}

	/**
	 * Sets {@link #clientIp} value
	 *
	 * @param clientIp new value of {@link #clientIp}
	 */
	public void setClientIp(List<String> clientIp) {
		this.clientIp = clientIp;
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
		for (int i = 0; i < this.clientIp.size(); ++i) {
			params.append("{");
			if (i != 0) {
				params.append("\"logical\":\"OR\",");
			}
			params.append(String.format("\n"
					+ "        \"field\": \"ipAddress\",\n"
					+ "        \"operator\": \"is\",\n"
					+ "        \"value\": \"%s\"\n"
					+ "      }", this.clientIp.get(i)));
			if (i != clientIp.size() - 1) {
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

	public String getClientIpListToString()
	{
		String temp = "";
		for (String ip : this.clientIp)
			temp=temp+ip+" ";
		return temp;
	}
}