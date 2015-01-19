package se.niteco.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import se.niteco.model.City;

@Service(value="cityService")
public class CityServiceImpl implements CityService {
	
	private List<City> cityList = Collections.synchronizedList(new ArrayList<City>());
	
	public CityServiceImpl() {
	
	}

	public void setCities(List<City> cities) {
		this.cityList = cities;
	}

	public void addCity(City city) {
		this.cityList.add(city);
	}

	public City getCity(int id) {
		City matchingCity = null;
		for(City city : cityList) {
			if(city.getCityId() == id) {
				matchingCity = city;
				break;
			}
		}
		return matchingCity;
	}

	public void removeCity(int id) {
		this.cityList.remove(getCity(id));
	}

	public void updateCity(City city) {
		int index = getCityIndex(city.getCityId());
		cityList.set(index, city);
	}

	public List<City> getCities() {
		return this.cityList;
	}

	public int getCityIndex(int id) {
		int index;
		for (index = 0; index < cityList.size(); index++) {
			if(cityList.get(index).getCityId() == id)
				break;
		}
		return index;
	}

	public int getNewCityId() {
		int newId = 0;
		for(City city : cityList) {
			if(city.getCityId() > newId) {
				newId = city.getCityId();
			}
		}

		return (newId+1);
	}

}
