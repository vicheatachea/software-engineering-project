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

	// Adds a location
	public void addLocation(LocationDTO location) {
		locationModel.addLocation(location);
	}

	// Updates a location
	public void updateLocation(LocationDTO locationDTO, String currentName) {
		locationModel.updateLocation(locationDTO, currentName);
	}

	// Deletes a location
	public void deleteLocation(LocationDTO locationDTO) {
		locationModel.deleteLocation(locationDTO);
	}

	public void deleteAllLocations() {
		locationModel.deleteAllLocations();
	}


}
