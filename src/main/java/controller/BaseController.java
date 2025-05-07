package controller;

/**
 * Serves as a central access point for all controllers in the application.
 * Maintains separation of concerns by providing specific controllers to views.
 * The view controllers call appropriate controllers to manipulate the model and retrieve data.
 * This base controller should be passed among views until specific controllers are needed.
 */
public class BaseController {
	/**
	 * Provides access to the event management controller.
	 *
	 * @return A new EventController instance
	 */
	public EventController getEventController() {
		return new EventController();
	}

	/**
	 * Provides access to the group management controller.
	 *
	 * @return A new GroupController instance
	 */
	public GroupController getGroupController() {
		return new GroupController();
	}

	/**
	 * Provides access to the location management controller.
	 *
	 * @return A new LocationController instance
	 */
	public LocationController getLocationController() {
		return new LocationController();
	}

	/**
	 * Provides access to the subject management controller.
	 *
	 * @return A new SubjectController instance
	 */
	public SubjectController getSubjectController() {
		return new SubjectController();
	}

	/**
	 * Provides access to the timetable management controller.
	 *
	 * @return A new TimetableController instance
	 */
	public TimetableController getTimetableController() {
		return new TimetableController();
	}

	/**
	 * Provides access to the user management controller.
	 *
	 * @return A new UserController instance
	 */
	public UserController getUserController() {
		return new UserController();
	}

	/**
	 * Provides access to the locale management controller.
	 *
	 * @return A new LocaleController instance
	 */
	public LocaleController getLocaleController() {
		return new LocaleController();
	}
}
