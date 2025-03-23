package controller;

import model.UserPreferences;

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
    private final TimetableController timetableController = new TimetableController();
    private final UserController userController = new UserController();

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

    public TimetableController getTimetableController() {
        return timetableController;
    }

    public UserController getUserController() {
        return userController;
    }

    public String getLocale() {
        return UserPreferences.getLocale();
    }

    public void setLocale(String locale) {
        UserPreferences.setLocale(locale);
    }
}
