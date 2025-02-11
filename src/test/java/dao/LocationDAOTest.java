package dao;

import datasource.MariaDBConnection;
import entity.LocationEntity;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LocationDAOTest {

	@BeforeAll
	static void setUpDatabase() throws SQLException {
		MariaDBConnection.resetDatabaseForTests();
	}

	@AfterAll
	static void tearDown() {
		LocationDAO locationDAO = new LocationDAO();
		locationDAO.deleteAll();
	}

	@BeforeEach
	void setUp() {
		LocationDAO locationDAO = new LocationDAO();
		locationDAO.deleteAll();
	}

	@Test
	void persist() {
		LocationDAO locationDAO = new LocationDAO();

		LocationEntity location = new LocationEntity("MPA5026", "Metropolia Myllypuro", "A");

		locationDAO.persist(location);

		LocationEntity foundLocation = locationDAO.findById(location.getId());

		assertEquals(location.getName(), foundLocation.getName());
		assertEquals(location.getCampus(), foundLocation.getCampus());
		assertEquals(location.getBuilding(), foundLocation.getBuilding());
	}

	@Test
	void findById() {
		LocationDAO locationDAO = new LocationDAO();

		LocationEntity location = new LocationEntity("MPA5026", "Metropolia Myllypuro", "A");

		locationDAO.persist(location);

		assertEquals(location, locationDAO.findById(location.getId()));
	}

	@Test
	void findAll() {
		LocationDAO locationDAO = new LocationDAO();

		LocationEntity location1 = new LocationEntity("MPA5026", "Metropolia Myllypuro", "A");
		LocationEntity location2 = new LocationEntity("MPA5027", "Metropolia Myllypuro", "A");

		locationDAO.persist(location1);
		locationDAO.persist(location2);

		assertEquals(2, locationDAO.findAll().size());
	}

	@Test
	void delete() {
		LocationDAO locationDAO = new LocationDAO();

		LocationEntity location = new LocationEntity("MPA5026", "Metropolia Myllypuro", "A");

		locationDAO.persist(location);
		locationDAO.delete(location);
		assertEquals(0, locationDAO.findAll().size());
	}

	@Test
	void deleteAll() {
		LocationDAO locationDAO = new LocationDAO();

		LocationEntity location1 = new LocationEntity("MPA5026", "Metropolia Myllypuro", "A");
		LocationEntity location2 = new LocationEntity("MPA5027", "Metropolia Myllypuro", "A");

		locationDAO.persist(location1);
		locationDAO.persist(location2);

		assertEquals(2, locationDAO.findAll().size());

		locationDAO.deleteAll();

		assertEquals(0, locationDAO.findAll().size());
	}
}