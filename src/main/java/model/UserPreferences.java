package model;

import java.util.prefs.Preferences;

public class UserPreferences {
    private static final String USER_ID_KEY = "user_id";
    private static final Preferences preferences = Preferences.userNodeForPackage(UserPreferences.class);

    // Store the user ID
    public static void setUserId(long userId) {
        preferences.put(USER_ID_KEY, String.valueOf(userId));
    }

    // Retrieve the user ID
    public static long getUserId() {
        return Long.parseLong(preferences.get(USER_ID_KEY, "-1"));
    }

    // Delete the user ID
    public static void deleteUserId() {
        preferences.remove(USER_ID_KEY);
    }
}