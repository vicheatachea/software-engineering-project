package controller;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class LocaleControllerTest {

	private static final LocaleController localeController = new BaseController().getLocaleController();

	@Test
	void getAvailableLocales() {
		assertFalse(localeController.getAvailableLocales().isEmpty());
		List<Locale> availableLocales = localeController.getAvailableLocales();

		assertTrue(availableLocales.contains(Locale.forLanguageTag("en-US")));
		assertTrue(availableLocales.contains(Locale.forLanguageTag("ja-JP")));
		assertTrue(availableLocales.contains(Locale.forLanguageTag("km-KH")));
	}

	@Test
	void setUserLocale() {
		localeController.setUserLocale(Locale.forLanguageTag("ja-JP"));
		assertEquals(localeController.getUserLocale(), Locale.forLanguageTag("ja-JP"));

		localeController.setUserLocale(Locale.forLanguageTag("km-KH"));
		assertEquals(localeController.getUserLocale(), Locale.forLanguageTag("km-KH"));

		localeController.setUserLocale(Locale.forLanguageTag("en-US"));
		assertEquals(localeController.getUserLocale(), Locale.forLanguageTag("en-US"));
	}

	@Test
	void setUnsupportedUserLocale() {
		localeController.setUserLocale(Locale.forLanguageTag("fr-FR"));
		assertEquals(localeController.getUserLocale(), Locale.forLanguageTag("fr-FR"));

		assertFalse(localeController.getAvailableLocales().contains(localeController.getUserLocale()));
	}

	@Test
	void getUserLocale() {
		localeController.setUserLocale(Locale.forLanguageTag("ja-JP"));

		Locale userLocale = localeController.getUserLocale();
		assertTrue(userLocale.equals(Locale.forLanguageTag("en-US")) ||
		           userLocale.equals(Locale.forLanguageTag("ja-JP")) ||
		           userLocale.equals(Locale.forLanguageTag("km-KH")));
	}

	@Test
	void getUIBundle() {
		localeController.setUserLocale(Locale.forLanguageTag("ja-JP"));
		assertNotNull(localeController.getUIBundle());
		assertEquals(localeController.getUIBundle().getLocale(), Locale.forLanguageTag("ja-JP"));

		localeController.setUserLocale(Locale.forLanguageTag("km-KH"));
		assertNotNull(localeController.getUIBundle());
		assertEquals(localeController.getUIBundle().getLocale(), Locale.forLanguageTag("km-KH"));

		localeController.setUserLocale(Locale.forLanguageTag("en-US"));
		assertNotNull(localeController.getUIBundle());
		assertEquals(localeController.getUIBundle().getLocale(), Locale.forLanguageTag("en-US"));
	}
}