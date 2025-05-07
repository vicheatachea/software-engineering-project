package controller;

import dto.LocationDTO;
import model.LocationModel;

import java.util.List;

/**
 * Controller class for managing locations in the system.
 * Handles operations for retrieving, adding, updating, and deleting locations
 * by interfacing with the LocationModel layer.
 */
public class LocationController {
	private final LocationModel locationModel = new LocationModel();

	/**
	 * Retrieves all available locations from the system.
	 *
	 * @return A list of all location DTOs
	 */
	public List<LocationDTO> fetchAllLocations() {
		return locationModel.fetchAllLocations();
	}

	/**
	 * Creates a new location in the system.
	 *
	 * @param location The location data to add
	 */
	public void addLocation(LocationDTO location) {
		locationModel.addLocation(location);
	}

	/**
	 * Updates an existing location's information.
	 *
	 * @param locationDTO The updated location data
	 * @param currentName The current name of the location being updated
	 */
	public void updateLocation(LocationDTO locationDTO, String currentName) {
		locationModel.updateLocation(locationDTO, currentName);
	}

	/**
	 * Removes a location from the system.
	 *
	 * @param locationDTO The location to delete
	 */
	public void deleteLocation(LocationDTO locationDTO) {
		locationModel.deleteLocation(locationDTO);
	}

	/**
	 * Deletes all locations from the system.
	 * This is a maintenance operation and should be used with caution.
	 */
	public void deleteAllLocations() {
		locationModel.deleteAllLocations();
	}


}
