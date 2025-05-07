package datasource;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides database connectivity for the STMS application with MariaDB.
 * <p>
 * This class manages database connections, setup, and JPA entity manager factory creation.
 * It reads configuration from environment variables with fallback to default values,
 * handles database verification, and provides centralized access to the EntityManagerFactory.
 * </p>
 */
public class MariaDBConnection {

	/**
	 * Database username from DB_USERNAME environment variable or default "stms_user".
	 */
	private static final String USER =
			System.getenv("DB_USERNAME") != null ? System.getenv("DB_USERNAME") : "stms_user";

	/**
	 * Database password from DB_PASSWORD environment variable or default "password".
	 */
	private static final String PASSWORD =
			System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "password";

	/**
	 * Database host from DB_HOST environment variable or default "localhost".
	 */
	private static final String DB_HOST = System.getenv("DB_HOST") != null ? System.getenv("DB_HOST") : "localhost";

	/**
	 * Database port from DB_PORT environment variable or default "3306".
	 */
	private static final String DB_PORT = System.getenv("DB_PORT") != null ? System.getenv("DB_PORT") : "3306";

	/**
	 * Base JDBC URL for MariaDB connection with Unicode support.
	 */
	private static final String BASE_URL =
			"jdbc:mariadb://" + DB_HOST + ":" + DB_PORT + "?useUnicode=true&characterEncoding=UTF-8";

	/**
	 * Logger instance for this class.
	 */
	private static final Logger logger = LoggerFactory.getLogger(MariaDBConnection.class);

	/**
	 * Singleton instance of the JPA EntityManagerFactory.
	 */
	private static EntityManagerFactory emf = null;

	/**
	 * Gets the singleton instance of the JPA EntityManagerFactory.
	 * <p>
	 * This method implements a lazy initialization pattern for the EntityManagerFactory.
	 * If the factory doesn't exist or is closed, it creates a new one with the
	 * configured database properties. The method is synchronized to ensure thread safety.
	 * </p>
	 *
	 * @return the EntityManagerFactory instance for database operations
	 */
	public static synchronized EntityManagerFactory getEntityManagerFactory() {
		if (emf == null || !emf.isOpen()) {
			Map<String, String> props = new HashMap<>();
			props.put("jakarta.persistence.jdbc.url",
			          "jdbc:mariadb://" + DB_HOST + ":" + DB_PORT + "/stms?useUnicode=true&characterEncoding=UTF-8");
			props.put("jakarta.persistence.jdbc.user", USER);
			props.put("jakarta.persistence.jdbc.password", PASSWORD);

			emf = Persistence.createEntityManagerFactory("stms", props);
		}
		return emf;
	}

	/**
	 * Verifies that the STMS database exists and creates it if it doesn't.
	 * <p>
	 * This method establishes a connection to the MariaDB server and executes
	 * SQL statements to create the database if it doesn't exist and sets it to use
	 * UTF-8 character encoding. It also selects the database for subsequent operations.
	 * </p>
	 *
	 * @throws SQLException if a database access error occurs during verification
	 */
	public void verifyDatabase() throws SQLException {
		try (Connection conn = DriverManager.getConnection(BASE_URL, USER, PASSWORD);
		     Statement statement = conn.createStatement()
		) {
			statement.addBatch(
					"CREATE DATABASE IF NOT EXISTS `stms` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
			statement.addBatch("USE `stms`");
			statement.executeBatch();
		} catch (SQLException e) {
			logger.error("Error verifying database: {}", e.getMessage());
		}
	}

	/**
	 * Terminates the database connection by closing the EntityManagerFactory.
	 * <p>
	 * This method should be called when the application is shutting down or
	 * when database connections are no longer needed to properly release resources.
	 * It safely closes the EntityManagerFactory if it exists and is open.
	 * </p>
	 */
	public void terminate() {
		try {
			if (emf != null && emf.isOpen()) {
				emf.close();
			}
		} catch (Exception e) {
			logger.error("Error closing database resources: {}", e.getMessage());
		}
	}
}