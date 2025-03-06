package model;

import dao.LocationDAO;
import dto.LocationDTO;
import entity.LocationEntity;

import java.util.ArrayList;
import java.util.List;

public class LocationModel {

	private static final LocationDAO locationDAO = new LocationDAO();

	public List<LocationDTO> fetchAllLocations() {

		List<LocationDTO> locations = new ArrayList<>();

		List<LocationEntity> locationEntities = locationDAO.findAll();

		for (LocationEntity location : locationEntities) {
			locations.add(convertToLocationDTO(location));
		}

		return locations;
	}

	public void addLocation(LocationDTO locationDTO) {
		LocationEntity existingLocation = locationDAO.findByName(locationDTO.name());

		if(existingLocation != null) {
			throw new IllegalArgumentException("Location already exists");
		}

		LocationEntity location = convertToLocationEntity(locationDTO);
		locationDAO.persist(location);
	}

	public void updateLocation(LocationDTO location, String currentName) {
		LocationEntity locationEntity = locationDAO.findByName(currentName);

		if (locationEntity == null) {
			throw new IllegalArgumentException("Location not found");
		}

		if (location.name().isEmpty() || location.campus().isEmpty() || location.building().isEmpty()) {
			throw new IllegalArgumentException("Location name, campus, and building cannot be empty");
		}

		locationEntity.setName(location.name());
		locationEntity.setCampus(location.campus());
		locationEntity.setBuilding(location.building());

		locationDAO.update(locationEntity);
	}

	public void deleteLocation(LocationDTO locationDTO) {
		LocationEntity location = convertToLocationEntity(locationDTO);
		locationDAO.delete(location);
	}

	private LocationDTO convertToLocationDTO(LocationEntity location) {
		return new LocationDTO(location.getName(), location.getCampus(), location.getBuilding());
	}

	private LocationEntity convertToLocationEntity(LocationDTO locationDTO) {
		return new LocationEntity(locationDTO.name(), locationDTO.campus(), locationDTO.building());
	}

	public void deleteAllLocations() {
		locationDAO.deleteAll();
	}
}
