package model;

import dao.LocationDAO;
import dto.LocationDTO;
import entity.LocationEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Model class responsible for managing location-related operations.
 * Handles creation, retrieval, updates, and deletion of locations where
 * teaching sessions can be held.
 */
public class LocationModel {

	private static final LocationDAO locationDAO = new LocationDAO();
	private static final UserModel userModel = new UserModel();

	/**
	 * Retrieves all locations in the system.
	 *
	 * @return List of data transfer objects containing location information
	 */
	public List<LocationDTO> fetchAllLocations() {
		List<LocationDTO> locations = new ArrayList<>();
		List<LocationEntity> locationEntities = locationDAO.findAll();

		for (LocationEntity location : locationEntities) {
			locations.add(convertToLocationDTO(location));
		}

		return locations;
	}

	/**
	 * Adds a new location to the system.
	 * Only teachers are allowed to add locations.
	 *
	 * @param locationDTO Data transfer object containing location information
	 * @throws IllegalArgumentException if the user is not a teacher or if the location already exists
	 */
	public void addLocation(LocationDTO locationDTO) {
		if (!userModel.isCurrentUserTeacher()) {
			throw new IllegalArgumentException("Only teachers can add locations.");
		}

		LocationEntity existingLocation = locationDAO.findByName(locationDTO.name());

		if (existingLocation != null) {
			throw new IllegalArgumentException("Location already exists");
		}

		LocationEntity location = convertToLocationEntity(locationDTO);
		locationDAO.persist(location);
	}

	/**
	 * Updates an existing location in the system.
	 * Only teachers are allowed to update locations.
	 *
	 * @param location    Data transfer object containing updated location information
	 * @param currentName The current name of the location to update
	 * @throws IllegalArgumentException if the user is not a teacher, if the location is not found,
	 *                                  if the new name is already taken, or if required fields are empty
	 */
	public void updateLocation(LocationDTO location, String currentName) {
		if (!userModel.isCurrentUserTeacher()) {
			throw new IllegalArgumentException("Only teachers can update locations.");
		}

		if (!Objects.equals(location.name(), currentName)) {
			LocationEntity existingLocation = locationDAO.findByName(location.name());
			if (existingLocation != null) {
				throw new IllegalArgumentException("Location already exists.");
			}
		}

		LocationEntity locationEntity = locationDAO.findByName(currentName);

		if (locationEntity == null) {
			throw new IllegalArgumentException("Location not found.");
		}

		if (location.name().isEmpty() || location.campus().isEmpty() || location.building().isEmpty()) {
			throw new IllegalArgumentException("Location name, campus, and building cannot be empty.");
		}

		locationEntity.setName(location.name());
		locationEntity.setCampus(location.campus());
		locationEntity.setBuilding(location.building());

		locationDAO.update(locationEntity);
	}

	/**
	 * Deletes a location from the system.
	 * Only teachers are allowed to delete locations.
	 *
	 * @param locationDTO Data transfer object of the location to delete
	 * @throws IllegalArgumentException if the user is not a teacher
	 */
	public void deleteLocation(LocationDTO locationDTO) {
		if (!userModel.isCurrentUserTeacher()) {
			throw new IllegalArgumentException("Only teachers can delete locations.");
		}

		LocationEntity location = convertToLocationEntity(locationDTO);
		locationDAO.delete(location);
	}

	/**
	 * Converts a location entity to a data transfer object.
	 *
	 * @param location The location entity to convert
	 * @return Data transfer object containing location information
	 */
	private LocationDTO convertToLocationDTO(LocationEntity location) {
		return new LocationDTO(location.getName(), location.getCampus(), location.getBuilding());
	}

	/**
	 * Converts a data transfer object to a location entity.
	 *
	 * @param locationDTO The data transfer object to convert
	 * @return Location entity
	 */
	private LocationEntity convertToLocationEntity(LocationDTO locationDTO) {
		return new LocationEntity(locationDTO.name(), locationDTO.campus(), locationDTO.building());
	}

	/**
	 * Deletes all locations from the system.
	 */
	public void deleteAllLocations() {
		locationDAO.deleteAll();
	}
}
