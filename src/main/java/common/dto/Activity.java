package common.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Activity
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 3/14/2023
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Activity {
	@JsonAlias("application")
	private String application;
	@JsonAlias("lastSeen")
	private String lastSeen;
	@JsonAlias("themeID")
	private String themeID;
	@JsonAlias("page")
	private String page;
	@JsonAlias("pageName")
	private String pageName;
	@JsonAlias("pageLoaded")
	private String pageLoaded;
	@JsonAlias("currentService")
	private Service currentService;
	@JsonAlias("lastService")
	private Service lastService;

	/**
	 * Retrieves {@link #application}
	 *
	 * @return value of {@link #application}
	 */
	public String getApplication() {
		return application;
	}

	/**
	 * Sets {@link #application} value
	 *
	 * @param application new value of {@link #application}
	 */
	public void setApplication(String application) {
		this.application = application;
	}

	/**
	 * Retrieves {@link #lastSeen}
	 *
	 * @return value of {@link #lastSeen}
	 */
	public String getLastSeen() {
		return lastSeen;
	}

	/**
	 * Sets {@link #lastSeen} value
	 *
	 * @param lastSeen new value of {@link #lastSeen}
	 */
	public void setLastSeen(String lastSeen) {
		this.lastSeen = lastSeen;
	}

	/**
	 * Retrieves {@link #themeID}
	 *
	 * @return value of {@link #themeID}
	 */
	public String getThemeID() {
		return themeID;
	}

	/**
	 * Sets {@link #themeID} value
	 *
	 * @param themeID new value of {@link #themeID}
	 */
	public void setThemeID(String themeID) {
		this.themeID = themeID;
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

	/**
	 * Retrieves {@link #pageName}
	 *
	 * @return value of {@link #pageName}
	 */
	public String getPageName() {
		return pageName;
	}

	/**
	 * Sets {@link #pageName} value
	 *
	 * @param pageName new value of {@link #pageName}
	 */
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	/**
	 * Retrieves {@link #pageLoaded}
	 *
	 * @return value of {@link #pageLoaded}
	 */
	public String getPageLoaded() {
		return pageLoaded;
	}

	/**
	 * Sets {@link #pageLoaded} value
	 *
	 * @param pageLoaded new value of {@link #pageLoaded}
	 */
	public void setPageLoaded(String pageLoaded) {
		this.pageLoaded = pageLoaded;
	}

	/**
	 * Retrieves {@link #currentService}
	 *
	 * @return value of {@link #currentService}
	 */
	public Service getCurrentService() {
		return currentService;
	}

	/**
	 * Sets {@link #currentService} value
	 *
	 * @param currentService new value of {@link #currentService}
	 */
	public void setCurrentService(Service currentService) {
		this.currentService = currentService;
	}

	/**
	 * Retrieves {@link #lastService}
	 *
	 * @return value of {@link #lastService}
	 */
	public Service getLastService() {
		return lastService;
	}

	/**
	 * Sets {@link #lastService} value
	 *
	 * @param lastService new value of {@link #lastService}
	 */
	public void setLastService(Service lastService) {
		this.lastService = lastService;
	}

	@Override
	public String toString() {
		return "Activity{" +
				"application='" + application + '\'' +
				", lastSeen='" + lastSeen + '\'' +
				", themeID='" + themeID + '\'' +
				", page='" + page + '\'' +
				", pageName='" + pageName + '\'' +
				", pageLoaded='" + pageLoaded + '\'' +
				", currentService=" + currentService +
				", lastService=" + lastService +
				'}';
	}
}