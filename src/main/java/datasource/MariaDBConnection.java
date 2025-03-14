package datasource;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MariaDBConnection {

	private static Connection conn = null;
	private static EntityManagerFactory emf = null;

	private static final String USER = "stms_user";
	private static final String PASSWORD = "password";
	private static final String DB_HOST = System.getenv("DB_HOST") != null ? System.getenv("DB_HOST") : "localhost";
	private static final String BASE_URL = "jdbc:mariadb://" + DB_HOST + ":3306/";

	private static final Logger logger = LoggerFactory.getLogger(MariaDBConnection.class);

	public static void verifyDatabase() throws SQLException {
		try (Connection conn = DriverManager.getConnection(BASE_URL, USER, PASSWORD)) {
			conn.createStatement().executeUpdate("CREATE DATABASE IF NOT EXISTS `stms`");
			conn.createStatement().executeUpdate("USE `stms`");
		} catch (SQLException e) {
			logger.error("Error resetting database for tests: {}", e.getMessage());
			throw e;
		}
	}

	public static EntityManagerFactory getEntityManagerFactory() {
		if (emf == null || !emf.isOpen()) {
			emf = Persistence.createEntityManagerFactory("stms");
		}
		return emf;
	}

	public static void terminate() throws SQLException {
		try {
			if (conn != null && !conn.isClosed()) {
				conn.close();
			}
			if (emf != null && emf.isOpen()) {
				emf.close();
			}
		} catch (SQLException e) {
			logger.error("Error closing database resources: {}", e.getMessage());
			throw new SQLException(e);
		}
	}
}
