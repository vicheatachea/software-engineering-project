package controller;

import dto.UserDTO;

import java.time.LocalDate;

/*
 * The controller should not manipulate the view directly to maintain separation of concerns
 * The view controllers should be able to call each controller to manipulate the model and retrieve data
 * The base controller should be passed among the views until the specific controllers are needed
 */

public class BaseController {
    private final EventController eventController = new EventController();
    private final GroupController groupController = new GroupController();
    private final LocationController locationController = new LocationController();
    private final SubjectController subjectController = new SubjectController();
    private final UserController userController = new UserController(new UserDTO(
            "username",
            "password",
            "salt",
            "firstName",
            "lastName",
            LocalDate.of(1990, 1, 1),
            "123-45-6789",
            "STUDENT"
    ));

    public EventController getEventController() {
        return eventController;
    }

    public GroupController getGroupController() {
        return groupController;
    }

    public LocationController getLocationController() {
        return locationController;
    }

    public SubjectController getSubjectController() {
        return subjectController;
    }

    public UserController getUserController() {
        return userController;
    }
}