package dto.request.implement;

import java.util.ArrayList;
import java.util.List;

import dto.request.RequestBody;

/**
 * QueryClientsRequest
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 3/15/2023
 * @since 1.0.0
 */
public class QueryClientsRequestBody implements RequestBody {
	private int jsonrpc;
	private final String METHOD="QueryClients";
	private String field;
	private String operation;
	private String value;
	private List<String> information=new ArrayList<>();
	private String clientNumber;
	private String page;

	public QueryClientsRequestBody() {
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
	 * Retrieves {@link #field}
	 *
	 * @return value of {@link #field}
	 */
	public String getField() {
		return field;
	}

	/**
	 * Sets {@link #field} value
	 *
	 * @param field new value of {@link #field}
	 */
	public void setField(String field) {
		this.field = field;
	}

	/**
	 * Retrieves {@link #operation}
	 *
	 * @return value of {@link #operation}
	 */
	public String getOperation() {
		return operation;
	}

	/**
	 * Sets {@link #operation} value
	 *
	 * @param operation new value of {@link #operation}
	 */
	public void setOperation(String operation) {
		this.operation = operation;
	}

	/**
	 * Retrieves {@link #value}
	 *
	 * @return value of {@link #value}
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets {@link #value} value
	 *
	 * @param value new value of {@link #value}
	 */
	public void setValue(String value) {
		this.value = value;
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

	/**
	 * Retrieves {@link #METHOD}
	 *
	 * @return value of {@link #METHOD}
	 */
	public String getMETHOD() {
		return METHOD;
	}

	/**
	 * Retrieves {@link #clientNumber}
	 *
	 * @return value of {@link #clientNumber}
	 */
	public String getClientNumber() {
		return clientNumber;
	}

	/**
	 * Sets {@link #clientNumber} value
	 *
	 * @param clientNumber new value of {@link #clientNumber}
	 */
	public void setClientNumber(String clientNumber) {
		this.clientNumber = clientNumber;
	}

	/**
	 * Retrieves {@link #page}
	 *
	 * @return value of {@link #page}
	 */
	public String getPage() {
		return page;
	}

	/**
	 * Sets {@link #page} value
	 *
	 * @param page new value of {@link #page}
	 */
	public void setPage(String page) {
		this.page = page;
	}

	@Override
	public String buildRequestBody() {
		StringBuilder informationString=new StringBuilder();
		for (String info:this.information)
			informationString.append("\""+info+"\":true,");
		informationString.deleteCharAt(informationString.length()-1);
		StringBuilder request= new StringBuilder(String.format("{\"jsonrpc\":%d,\"method\":\"%s\",\""
				+ "params\":[[{\"field\":\"%s\",\"operator\":\"%s\",\"value\":\"%s\"}],{%s"
				+ "},\"ipAddress\",%s,%s]}",this.jsonrpc,this.METHOD,this.field,this.operation,this.value,informationString,this.getClientNumber(),this.getPage()));
		return request.toString();
	}
}