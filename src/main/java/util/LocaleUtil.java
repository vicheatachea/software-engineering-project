package util;

import java.util.List;
import java.util.Locale;

public class LocaleUtil {
//    private static final String LOCALE_BUNDLE = "view_text";

    // Should be changed to return locales in view_text resource bundle if possible
    public static List<Locale> getAvailableLocales() {
        return List.of(
                new Locale.Builder().setLanguage("en").setRegion("US").build(),
                new Locale.Builder().setLanguage("ja").setRegion("JP").build(),
                new Locale.Builder().setLanguage("km").setRegion("KH").build()
        );
    }
}
