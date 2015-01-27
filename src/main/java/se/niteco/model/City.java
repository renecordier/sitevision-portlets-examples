package se.niteco.model;

import java.io.Serializable;

public class City implements Serializable {
	private static final long serialVersionUID = 1L;
	private int cityId;
	private String cityName;
	
	public City(int cityId, String cityName) {
		this.cityId = cityId;
		this.cityName = cityName;
	}
	
	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
}
