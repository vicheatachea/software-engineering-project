package controller;

import model.UserPreferences;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Controller class for managing localization settings in the system.
 * Provides methods to fetch available locales, get and set user locale preferences,
 * and retrieve localized resource bundles for UI rendering.
 */
public class LocaleController {
	/**
	 * The resource bundle name for UI text localization
	 */
	private static final String LOCALE_BUNDLE = "view_text";

	/**
	 * Retrieves all available locales supported by the application.
	 *
	 * @return A list of supported Locale objects
	 * @implNote Currently returns a hardcoded list, but should be changed to
	 * dynamically return locales from the view_text resource bundle if possible
	 */
	public List<Locale> getAvailableLocales() {
		return List.of(
				new Locale.Builder().setLanguage("en").setRegion("US").build(),
				new Locale.Builder().setLanguage("ja").setRegion("JP").build(),
				new Locale.Builder().setLanguage("km").setRegion("KH").build()
		);
	}

	/**
	 * Gets the currently selected locale for the user.
	 *
	 * @return The user's preferred Locale
	 */
	public Locale getUserLocale() {
		return Locale.forLanguageTag(UserPreferences.getLocale());
	}

	/**
	 * Sets the locale preference for the current user.
	 *
	 * @param locale The Locale to set as the user's preference
	 */
	public void setUserLocale(Locale locale) {
		UserPreferences.setLocale(locale.toLanguageTag());
	}

	/**
	 * Retrieves the resource bundle for UI text based on the user's locale.
	 *
	 * @return The ResourceBundle containing localized UI text
	 */
	public ResourceBundle getUIBundle() {
		return ResourceBundle.getBundle(LOCALE_BUNDLE, getUserLocale());
	}
}
