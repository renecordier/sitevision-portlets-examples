package se.niteco.jms;

import java.util.List;

import se.niteco.model.City;

/**
 * Factory class to send list of cities messages
 */
public interface CitySender {
	public void sendCities(List<City> cities);
}
