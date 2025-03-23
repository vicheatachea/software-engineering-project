package controller;

import model.UserPreferences;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class LocaleController {
    private static final String LOCALE_BUNDLE = "view_text";

    /*
    Should be changed to return locales in view_text resource bundle
    instead of manually creating them if possible
    */
    // Fetch all available locales
    public List<Locale> getAvailableLocales() {
        return List.of(
                new Locale.Builder().setLanguage("en").setRegion("US").build(),
                new Locale.Builder().setLanguage("ja").setRegion("JP").build(),
                new Locale.Builder().setLanguage("km").setRegion("KH").build()
        );
    }

    // Fetch the locale for the user
    public Locale getUserLocale() {
        return Locale.forLanguageTag(UserPreferences.getLocale());
    }

    // Set the locale for the user
    public void setUserLocale(Locale locale) {
        UserPreferences.setLocale(locale.toLanguageTag());
    }

    // Fetch the bundle for UI translations
    public ResourceBundle getUIBundle() {
        return ResourceBundle.getBundle(LOCALE_BUNDLE, getUserLocale());
    }
}
