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

public class MariaDBConnection {

	private static final String USER =
			System.getenv("DB_USERNAME") != null ? System.getenv("DB_USERNAME") : "stms_user";
	private static final String PASSWORD =
			System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "password";
	private static final String DB_HOST = System.getenv("DB_HOST") != null ? System.getenv("DB_HOST") : "localhost";
	private static final String DB_PORT = System.getenv("DB_PORT") != null ? System.getenv("DB_PORT") : "3306";
	private static final String BASE_URL =
			"jdbc:mariadb://" + DB_HOST + ":" + DB_PORT + "?useUnicode=true&characterEncoding=UTF-8";
	private static final Logger logger = LoggerFactory.getLogger(MariaDBConnection.class);
	private EntityManagerFactory emf = null;

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

	public void terminate() {
		try {
			if (emf != null && emf.isOpen()) {
				emf.close();
			}
		} catch (Exception e) {
			logger.error("Error closing database resources: {}", e.getMessage());
		}
	}

	public synchronized EntityManagerFactory getEntityManagerFactory() {
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
}