package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.controllers.pages.main.MainPageViewController;

import java.io.IOException;

/*
* The controller should be initialized in the main page view controller
*/
public class View extends Application {
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/layouts/pages/main/main-page.fxml"));
            stage.setScene(new Scene(loader.load()));
            MainPageViewController controller = loader.getController();
            controller.setStage(stage);

            stage.setTitle("Student Timetable Management System");
            stage.setMinWidth(400);
            stage.setMinHeight(600);
            stage.setMaximized(true);
            stage.show();
        } catch (NullPointerException e) {
            System.out.println("View could not start the application");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
