package view.controllers.pages.main;

import controller.BaseController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import util.LocaleUtil;
import view.controllers.ControllerAware;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class SettingsViewController implements ControllerAware {
    private BaseController baseController;

    @FXML
    private Label settingsLabel;
    @FXML
    private Label languageLabel;
    @FXML
    private ComboBox<String> languageComboBox;

    @FXML
    private void initialize() {
        List<Locale> locales = LocaleUtil.getAvailableLocales();
        locales.forEach(locale ->
                languageComboBox.getItems().add(locale.getDisplayLanguage(locale))
        );
        languageComboBox.addEventHandler(ActionEvent.ACTION, event -> handleLanguageChange());

        Platform.runLater(() -> {
            Locale currentLocale = Locale.forLanguageTag(baseController.getLocale());
            languageComboBox.setValue(currentLocale.getDisplayLanguage(currentLocale));
            updateTranslations();
        });
    }

    @Override
    public void setBaseController(BaseController baseController) {
        this.baseController = baseController;
    }

    private void handleLanguageChange() {
        String selectedLanguage = languageComboBox.getValue();

        for (Locale locale : LocaleUtil.getAvailableLocales()) {
            if (locale.getDisplayLanguage(locale).equals(selectedLanguage)) {
                baseController.setLocale(locale.toLanguageTag());
                break;
            }
        }
        updateTranslations();
    }

    private void updateTranslations() {
        String localeString = baseController.getLocale();
        ResourceBundle bundle = LocaleUtil.getUIBundle(localeString);

        settingsLabel.setText(bundle.getString("settings.title"));
        languageLabel.setText(bundle.getString("settings.language"));
    }
}
