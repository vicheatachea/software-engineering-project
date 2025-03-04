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

	public void saveLocation(LocationDTO locationDTO) {
		LocationEntity location = convertToLocationEntity(locationDTO);
		locationDAO.persist(location);
	}

	public void deleteLocation(LocationDTO locationDTO) {
		LocationEntity location = convertToLocationEntity(locationDTO);
		locationDAO.delete(location);
	}

	private LocationDTO convertToLocationDTO(LocationEntity location) {
		return new LocationDTO(location.getId(), location.getName(), location.getCampus(), location.getBuilding());
	}

	private LocationEntity convertToLocationEntity(LocationDTO locationDTO) {
		return new LocationEntity(locationDTO.id(), locationDTO.name(), locationDTO.campus(), locationDTO.building());
	}
}
