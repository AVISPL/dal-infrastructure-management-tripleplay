package dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * ServiceWrapper
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 3/14/2023
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceWrapper {
	@JsonAlias("result")
	private List<Service> services;

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
		return "ServiceWrapper{" +
				"services=" + services +
				'}';
	}
}