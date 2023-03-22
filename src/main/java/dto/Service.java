package dto;


import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Service
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 3/14/2023
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Service {
	@JsonAlias("id")
	private long id;
	@JsonAlias("channelNumber")
	private long channelNumber;
	@JsonAlias("name")
	private String name;
	@JsonAlias("type")
	private long type;
	@JsonAlias("startedViewing")
	private String startedViewing;
	@JsonAlias("endedViewing")
	private String endedViewing;

	/**
	 * Retrieves {@link #id}
	 *
	 * @return value of {@link #id}
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets {@link #id} value
	 *
	 * @param id new value of {@link #id}
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Retrieves {@link #channelNumber}
	 *
	 * @return value of {@link #channelNumber}
	 */
	public long getChannelNumber() {
		return channelNumber;
	}

	/**
	 * Sets {@link #channelNumber} value
	 *
	 * @param channelNumber new value of {@link #channelNumber}
	 */
	public void setChannelNumber(long channelNumber) {
		this.channelNumber = channelNumber;
	}

	/**
	 * Retrieves {@link #name}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets {@link #name} value
	 *
	 * @param name new value of {@link #name}
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Retrieves {@link #type}
	 *
	 * @return value of {@link #type}
	 */
	public long getType() {
		return type;
	}

	/**
	 * Sets {@link #type} value
	 *
	 * @param type new value of {@link #type}
	 */
	public void setType(long type) {
		this.type = type;
	}

	/**
	 * Retrieves {@link #startedViewing}
	 *
	 * @return value of {@link #startedViewing}
	 */
	public String getStartedViewing() {
		return startedViewing;
	}

	/**
	 * Sets {@link #startedViewing} value
	 *
	 * @param startedViewing new value of {@link #startedViewing}
	 */
	public void setStartedViewing(String startedViewing) {
		this.startedViewing = startedViewing;
	}

	/**
	 * Retrieves {@link #endedViewing}
	 *
	 * @return value of {@link #endedViewing}
	 */
	public String getEndedViewing() {
		return endedViewing;
	}

	/**
	 * Sets {@link #endedViewing} value
	 *
	 * @param endedViewing new value of {@link #endedViewing}
	 */
	public void setEndedViewing(String endedViewing) {
		this.endedViewing = endedViewing;
	}

	@Override
	public String toString() {
		return "Service{" +
				"id=" + id +
				", channelNumber=" + channelNumber +
				", name='" + name + '\'' +
				", type=" + type +
				", startedViewing='" + startedViewing + '\'' +
				", endedViewing='" + endedViewing + '\'' +
				'}';
	}
}