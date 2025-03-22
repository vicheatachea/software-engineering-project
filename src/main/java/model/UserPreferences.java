package model;

import entity.Role;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public final class UserPreferences {
	private UserPreferences() {
		// Prevent instantiation
	}

	private static final String USER_ID_KEY = "user_id";
	private static final String ROLE = "role";
	private static final String LOCALE = "locale";
	private static final Preferences preferences = Preferences.userNodeForPackage(UserPreferences.class);

	// Store the user ID
	public static void setUser(long userId, Role role) {
		preferences.put(USER_ID_KEY, String.valueOf(userId));
		preferences.put(ROLE, role.toString());
	}

	// Retrieve the user ID
	public static long getUserId() {
		return Long.parseLong(preferences.get(USER_ID_KEY, "-1"));
	}

	// Retrieve the user role
	public static Role getUserRole() {
		return Role.valueOf(preferences.get(ROLE, "STUDENT"));
	}

	// Delete the user ID
	public static void deleteUser() {
		try {
			preferences.clear();
		} catch (BackingStoreException e) {
			throw new IllegalArgumentException("Failed to delete user preferences");
		} finally {
			preferences.put(USER_ID_KEY, "-1");
			preferences.put(ROLE, "STUDENT");
		}
	}

	// Store the locale
	public static void setLocale(String locale) {
		preferences.put(LOCALE, locale);
	}

	// Retrieve the locale
	public static String getLocale() {
		return preferences.get(LOCALE, "en_US");
	}
}