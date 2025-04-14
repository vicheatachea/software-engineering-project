package controller;

/*
* The controller should not manipulate the view directly to maintain separation of concerns
* The view controllers should be able to call each controller to manipulate the model and retrieve data
* The base controller should be passed among the views until the specific controllers are needed
*/
public class BaseController {
    public EventController getEventController() {
        return new EventController();
    }

    public GroupController getGroupController() {
        return new GroupController();
    }

    public LocationController getLocationController() {
        return new LocationController();
    }

    public SubjectController getSubjectController() {
        return new SubjectController();
    }

    public TimetableController getTimetableController() {
        return new TimetableController();
    }

    public UserController getUserController() {
        return new UserController();
    }

    public LocaleController getLocaleController() {
        return new LocaleController();
    }
}
