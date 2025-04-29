package main;

import datasource.MariaDBConnection;
import javafx.application.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.View;

/*
 * main.Main initializes view which initializes controller which initializes model
 * Two-way data binding is not supported due to this schema and should not be needed
 * Creating everything here and passing it down does not seem to be possible
 */
public class Main {

	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		try {
			new MariaDBConnection().verifyDatabase();
		} catch (Exception e) {
			logger.error("Error verifying database: {}", e.getMessage());
		}
		Application.launch(View.class);
	}
}
