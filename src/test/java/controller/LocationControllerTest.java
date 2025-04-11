package controller;

import datasource.MariaDBConnection;
import dto.LocationDTO;
import dto.UserDTO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LocationControllerTest {

	private static final LocationController locationController = new LocationController();
	private static final UserController userController = new UserController();

	@BeforeAll
	static void ensureDatabase() {
		try {
			MariaDBConnection.verifyDatabase();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@AfterAll
	static void tearDown() {
		locationController.deleteAllLocations();
		userController.deleteAllUsers();
	}

	private static UserDTO createTeacher() {
		return new UserDTO("testLocation", "testPassword", "Test", "Teacher",
		                   LocalDateTime.of(2000, 1, 1, 0, 0), "BA987654321", "TEACHER");
	}

	@BeforeEach
	void setUp() {
		locationController.deleteAllLocations();
		userController.deleteAllUsers();
	}

	LocationDTO createLocationDTO(String name) {
		return new LocationDTO(name, "Metropolia Myllypuro", "Building B");
	}

	@Test
	void fetchAllLocations() {
		UserDTO teacher = createTeacher();
		userController.registerUser(teacher);
		userController.authenticateUser(teacher.username(), teacher.password());

		LocationDTO location1 = createLocationDTO("B2005");
		LocationDTO location2 = createLocationDTO("B7035");

		locationController.addLocation(location1);
		locationController.addLocation(location2);

		assertEquals(2, locationController.fetchAllLocations().size());
	}

	@Test
	void addLocation() {
		UserDTO teacher = createTeacher();
		userController.registerUser(teacher);
		userController.authenticateUser(teacher.username(), teacher.password());

		LocationDTO location = createLocationDTO("B2005");

		locationController.addLocation(location);

		assertEquals(location, locationController.fetchAllLocations().getFirst());
	}

	@Test
	void addLocationWithInvalidData() {
		UserDTO teacher = createTeacher();
		userController.registerUser(teacher);
		userController.authenticateUser(teacher.username(), teacher.password());

		LocationDTO location = new LocationDTO("", "", "");

		try {
			locationController.addLocation(location);
		} catch (IllegalArgumentException e) {
			assertEquals("Location name cannot be empty", e.getMessage());
		}
	}

	@Test
	void addLocationUnauthorized() {
		LocationDTO location = createLocationDTO("B2005");

		try {
			locationController.addLocation(location);
		} catch (IllegalArgumentException e) {
			assertEquals("Only teachers can add locations.", e.getMessage());
		}
	}

	@Test
	void addDuplicateLocation() {
		UserDTO teacher = createTeacher();
		userController.registerUser(teacher);
		userController.authenticateUser(teacher.username(), teacher.password());

		LocationDTO location = createLocationDTO("B2005");

		locationController.addLocation(location);

		try {
			locationController.addLocation(location);
		} catch (IllegalArgumentException e) {
			assertEquals("Location already exists", e.getMessage());
		}
	}

	@Test
	void updateLocation() {
		UserDTO teacher = createTeacher();
		userController.registerUser(teacher);
		userController.authenticateUser(teacher.username(), teacher.password());

		LocationDTO location = createLocationDTO("B2005");
		locationController.addLocation(location);

		LocationDTO updatedLocation = createLocationDTO("B7035");
		locationController.updateLocation(updatedLocation, location.name());

		assertEquals(updatedLocation, locationController.fetchAllLocations().getFirst());
	}

	@Test
	void updateLocationUnauthorized() {
		UserDTO teacher = createTeacher();
		userController.registerUser(teacher);
		userController.authenticateUser(teacher.username(), teacher.password());

		LocationDTO location = createLocationDTO("B2005");
		locationController.addLocation(location);

		userController.logout();

		LocationDTO updatedLocation = createLocationDTO("B7035");

		try {
			locationController.updateLocation(updatedLocation, "B2006");
		} catch (IllegalArgumentException e) {
			assertEquals("Only teachers can update locations.", e.getMessage());
		}
	}

	@Test
	void deleteLocation() {
		UserDTO teacher = createTeacher();
		userController.registerUser(teacher);
		userController.authenticateUser(teacher.username(), teacher.password());

		LocationDTO location = createLocationDTO("B2005");

		locationController.addLocation(location);

		locationController.deleteLocation(location);

		assertEquals(0, locationController.fetchAllLocations().size());
	}

	@Test
	void deleteLocationUnauthorized() {
		userController.registerUser(createTeacher());
		userController.authenticateUser("testLocation", "testPassword");

		LocationDTO location = createLocationDTO("B2005");

		locationController.addLocation(location);

		userController.logout();

		try {
			locationController.deleteLocation(new LocationDTO("B2006", "", ""));
		} catch (IllegalArgumentException e) {
			assertEquals("Only teachers can delete locations.", e.getMessage());
		}
	}

	@Test
	void deleteAllLocations() {
		UserDTO teacher = createTeacher();
		userController.registerUser(teacher);
		userController.authenticateUser(teacher.username(), teacher.password());

		LocationDTO location1 = createLocationDTO("B2005");
		LocationDTO location2 = createLocationDTO("B7035");

		locationController.addLocation(location1);
		locationController.addLocation(location2);

		locationController.deleteAllLocations();

		assertEquals(0, locationController.fetchAllLocations().size());
	}
}