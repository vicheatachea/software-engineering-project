package controller;

import datasource.MariaDBConnection;
import dto.LocationDTO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LocationControllerTest {

	private static final LocationController locationController = new LocationController();

	@BeforeAll
	static void ensureDatabase() {
		try {
			MariaDBConnection.verifyDatabase();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@BeforeEach
	void setUp() {
		locationController.deleteAllLocations();
		try {
			Thread.sleep(0);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@AfterAll
	static void tearDown() {
		locationController.deleteAllLocations();
	}

	LocationDTO createLocationDTO(String name) {
		return new LocationDTO(name, "Metropolia Myllypuro", "Building B");
	}

	@Test
	void fetchAllLocations() {
		LocationDTO location1 = createLocationDTO("B2005");
		LocationDTO location2 = createLocationDTO("B7035");

		locationController.saveLocation(location1);
		locationController.saveLocation(location2);

		assertEquals(2, locationController.fetchAllLocations().size());
	}

	@Test
	void saveLocation() {
		LocationDTO location = createLocationDTO("B2005");

		locationController.saveLocation(location);

		assertEquals(location, locationController.fetchAllLocations().getFirst());
	}

	@Test
	void deleteLocation() {
		LocationDTO location = createLocationDTO("B2005");

		locationController.saveLocation(location);

		locationController.deleteLocation(location);

		assertEquals(0, locationController.fetchAllLocations().size());
	}

	@Test
	void deleteAllLocations() {

	}
}