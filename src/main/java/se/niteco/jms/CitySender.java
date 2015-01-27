package se.niteco.jms;

import java.util.List;

import se.niteco.model.City;

public interface CitySender {
	public void sendCities(List<City> cities);
}
