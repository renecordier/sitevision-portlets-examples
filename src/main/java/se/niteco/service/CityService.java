package se.niteco.service;

import java.util.List;

import se.niteco.model.City;

public interface CityService {
	List<City> getCities();
	void setCities(List<City> cities);
	void addCity(City city);
	City getCity(int id);
	void removeCity(int id);
	void updateCity(City city);
	int getCityIndex(int id);
}
