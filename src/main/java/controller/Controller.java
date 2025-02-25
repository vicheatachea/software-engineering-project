package controller;

import dto.AssignmentDTO;
import dto.LocationDTO;
import dto.SubjectDTO;
import dto.TeachingSessionDTO;
import model.Model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/*
* The controller should not manipulate the view directly to maintain separation of concerns
* The view controllers should be able to call the controller to manipulate the model and retrieve data
*/
public class Controller {
    private final Model model;
    private boolean userLoggedIn = false;

    public boolean isUserLoggedIn() {
        return userLoggedIn;
    }

    public void setUserLoggedIn(boolean status) {
        userLoggedIn = status;
    }

    public Controller() {
        this.model = new Model();
    }

    // Fetch all available locations
    public List<LocationDTO> fetchLocations() {
        // Placeholder
        return new ArrayList<>();
    }

    // Fetch all available subjects
    public List<SubjectDTO> fetchSubjects() {
        // Placeholder
        return new ArrayList<>();
    }

    // Fetch all teaching sessions for a user within a time interval
    public List<TeachingSessionDTO> fetchTeachingSessionsByUser(String username, LocalDate startDate, LocalDate endDate) {
        // Placeholder
        return new ArrayList<>();
    }

    // Fetch all assignments for a user within a time interval
    public List<AssignmentDTO> fetchAssignmentsByUser(String username, LocalDate startDate, LocalDate endDate) {
        // Placeholder
        return new ArrayList<>();
    }
}
