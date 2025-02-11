package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/*
* The controller should be initialized in the main page view controller
*/
public class View extends Application {
    @Override
    public void start(Stage stage) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/layouts/pages/main-page.fxml"));
        try {
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Student Timetable Management System");
            stage.setMinWidth(400);
            stage.setMinHeight(600);
            stage.setMaximized(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
