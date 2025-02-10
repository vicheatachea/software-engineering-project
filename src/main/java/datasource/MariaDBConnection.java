package datasource;

import jakarta.persistence.EntityManager;
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

	private static final String USER = "user";
	private static final String PASSWORD = "password";
	private static final String BASE_URL = "jdbc:mariadb://localhost:3306/";
	private static final String URL = "jdbc:mariadb://localhost:3306/stms";

	private static final Logger logger = LoggerFactory.getLogger(MariaDBConnection.class);

	public static Connection getConnection() throws SQLException {
		if (conn == null || conn.isClosed()) {
			try {
				try (Connection baseConn = DriverManager.getConnection(BASE_URL, USER, PASSWORD)) {
					baseConn.createStatement().executeUpdate("CREATE DATABASE IF NOT EXISTS `stms`");
				}

				conn = DriverManager.getConnection(URL, USER, PASSWORD);
				logger.info("Connected to database: {}", URL);
			} catch (SQLException e) {
				logger.error("Error creating database: {}", e.getMessage());
				throw e;
			}
		}
		return conn;
	}

	public static EntityManager getEntityManager() {
		if (emf == null) {
			emf = Persistence.createEntityManagerFactory("stms");
		}
		return emf.createEntityManager();
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
