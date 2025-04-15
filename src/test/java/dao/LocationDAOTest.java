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

	private static final LocationDAO locationDAO = new LocationDAO();

	@BeforeAll
	static void ensureDatabase() {
		try {
			MariaDBConnection.getInstance().verifyDatabase();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@AfterAll
	static void tearDown() {
		locationDAO.deleteAll();
	}

	@BeforeEach
	void setUp() {
		locationDAO.deleteAll();
	}

	@Test
	void persist() {
		LocationEntity location = new LocationEntity("MPA5026", "Metropolia Myllypuro", "A");

		locationDAO.persist(location);

		LocationEntity foundLocation = locationDAO.findById(location.getId());

		assertEquals(location.getName(), foundLocation.getName());
		assertEquals(location.getCampus(), foundLocation.getCampus());
		assertEquals(location.getBuilding(), foundLocation.getBuilding());
	}

	@Test
	void persistUpdate() {
		LocationEntity location = new LocationEntity("MPA5026", "Metropolia Myllypuro", "A");

		locationDAO.persist(location);

		location.setName("Metropolia Myllypuro 2");
		location.setCampus("Myllypuro");
		location.setBuilding("B");

		locationDAO.update(location);

		LocationEntity foundLocation = locationDAO.findById(location.getId());

		assertEquals(location.getName(), foundLocation.getName());
		assertEquals(location.getCampus(), foundLocation.getCampus());
		assertEquals(location.getBuilding(), foundLocation.getBuilding());
	}

	@Test
	void findById() {
		LocationEntity location = new LocationEntity("MPA5026", "Metropolia Myllypuro", "A");

		locationDAO.persist(location);

		assertEquals(location, locationDAO.findById(location.getId()));
	}

	@Test
	void findAll() {
		LocationEntity location1 = new LocationEntity("MPA5026", "Metropolia Myllypuro", "A");
		LocationEntity location2 = new LocationEntity("MPA5027", "Metropolia Myllypuro", "A");

		locationDAO.persist(location1);
		locationDAO.persist(location2);

		assertEquals(2, locationDAO.findAll().size());
	}

	@Test
	void delete() {
		LocationEntity location = new LocationEntity("MPA5026", "Metropolia Myllypuro", "A");

		locationDAO.persist(location);
		locationDAO.delete(location);
		assertEquals(0, locationDAO.findAll().size());
	}

	@Test
	void deleteAll() {
		LocationEntity location1 = new LocationEntity("MPA5026", "Metropolia Myllypuro", "A");
		LocationEntity location2 = new LocationEntity("MPA5027", "Metropolia Myllypuro", "A");

		locationDAO.persist(location1);
		locationDAO.persist(location2);

		assertEquals(2, locationDAO.findAll().size());

		locationDAO.deleteAll();

		assertEquals(0, locationDAO.findAll().size());
	}
}