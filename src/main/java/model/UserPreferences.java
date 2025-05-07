package model;

import entity.Role;

import java.util.prefs.Preferences;

/**
 * Manages user preferences and session data using Java's Preferences API.
 * This utility class provides static methods to store and retrieve user information
 * such as user ID, role, and locale preferences.
 */
public final class UserPreferences {
	private static final String USER_ID_KEY = "user_id";
	private static final String ROLE = "role";
	private static final String LOCALE = "locale";
	private static final Preferences preferences = Preferences.userNodeForPackage(UserPreferences.class);

	/**
	 * Private constructor to prevent instantiation of this utility class.
	 */
	private UserPreferences() {
		// Prevent instantiation
	}

	/**
	 * Stores the user ID and role in preferences when a user logs in.
	 *
	 * @param userId The ID of the logged-in user
	 * @param role   The role of the logged-in user
	 */
	public static void setUser(long userId, Role role) {
		preferences.put(USER_ID_KEY, String.valueOf(userId));
		preferences.put(ROLE, role.toString());
	}

	/**
	 * Retrieves the ID of the currently logged-in user.
	 *
	 * @return The user ID, or -1 if no user is logged in
	 */
	public static long getUserId() {
		return Long.parseLong(preferences.get(USER_ID_KEY, "-1"));
	}

	/**
	 * Retrieves the role of the currently logged-in user.
	 *
	 * @return The user's role, defaults to STUDENT if not set
	 */
	public static Role getUserRole() {
		return Role.valueOf(preferences.get(ROLE, "STUDENT"));
	}

	/**
	 * Removes user session data during logout.
	 */
	public static void deleteUser() {
		preferences.remove(USER_ID_KEY);
		preferences.remove(ROLE);
	}

	/**
	 * Retrieves the user's locale preference.
	 *
	 * @return The locale string, defaults to "en-US" if not set
	 */
	public static String getLocale() {
		return preferences.get(LOCALE, "en-US");
	}

	/**
	 * Sets the user's locale preference.
	 *
	 * @param locale The locale string to store
	 */
	public static void setLocale(String locale) {
		preferences.put(LOCALE, locale);
	}
}