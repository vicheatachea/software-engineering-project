package controller;

import dto.LocationDTO;
import model.LocationModel;

import java.util.List;

public class LocationController {
	private final LocationModel locationModel = new LocationModel();

	// Fetch all available locations
	public List<LocationDTO> fetchAllLocations() {
		return locationModel.fetchAllLocations();
	}

	// Adds/Updates a location
	public void saveLocation(LocationDTO locationDTO) {
		locationModel.saveLocation(locationDTO);
	}

	// Deletes a location
	public void deleteLocation(LocationDTO locationDTO) {
		locationModel.deleteLocation(locationDTO);
	}
}
