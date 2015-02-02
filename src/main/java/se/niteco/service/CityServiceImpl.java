package se.niteco.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import se.niteco.model.City;
import se.niteco.service.CityService;

/**
 * Class for managing the list of cities
 */
@Service(value="cityService")
public class CityServiceImpl implements CityService {
	//List of cities
	private List<City> cityList = Collections.synchronizedList(new ArrayList<City>());
	
	/**
	 * Constructor
	 */
	public CityServiceImpl() {
	
	}

	/**
	 * Set the list of cities
	 * @param cities
	 */
	public void setCities(List<City> cities) {
		this.cityList = cities;
	}

	/**
	 * Adding a city
	 * @param city
	 */
	public void addCity(City city) {
		this.cityList.add(city);
	}

	/**
	 * Get a city from its id
	 * @param id
	 * @return matchingCity
	 */
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

	/**
	 * Remove a city with its id
	 * @param id
	 */
	public void removeCity(int id) {
		this.cityList.remove(getCity(id));
	}

	/**
	 * Update city
	 * @param city
	 */
	public void updateCity(City city) {
		int index = getCityIndex(city.getCityId());
		cityList.set(index, city);
	}

	/**
	 * get list of cities
	 * @return cityList
	 */
	public List<City> getCities() {
		return this.cityList;
	}

	/**
	 * Get the index list of a city
	 * @param id
	 * @return index
	 */
	public int getCityIndex(int id) {
		int index;
		for (index = 0; index < cityList.size(); index++) {
			if(cityList.get(index).getCityId() == id)
				break;
		}
		return index;
	}

	/**
	 * Get the id of a new created city
	 * @return newId
	 */
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
