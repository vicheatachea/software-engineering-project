package view.controllers.pages.main;

import controller.BaseController;
import controller.LocaleController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import view.controllers.ControllerAware;
import view.controllers.components.SidebarViewController;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class SettingsViewController implements ControllerAware {
    private LocaleController localeController;
    private SidebarViewController sidebarViewController;

    @FXML
    private Label settingsLabel;
    @FXML
    private Label languageLabel;
    @FXML
    private ComboBox<String> languageComboBox;

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            List<Locale> locales = localeController.getAvailableLocales();
            locales.forEach(locale ->
                    languageComboBox.getItems().add(locale.getDisplayLanguage(locale))
            );
            languageComboBox.addEventHandler(ActionEvent.ACTION, event -> handleLanguageChange());

            Locale currentLocale = localeController.getUserLocale();
            languageComboBox.setValue(currentLocale.getDisplayLanguage(currentLocale));
            updateTranslations();
        });
    }

    @Override
    public void setBaseController(BaseController baseController) {
        this.localeController = baseController.getLocaleController();
    }

    public void setSidebarViewController(SidebarViewController sidebarViewController) {
        this.sidebarViewController = sidebarViewController;
    }

    private void handleLanguageChange() {
        String selectedLanguage = languageComboBox.getValue();

        for (Locale locale : localeController.getAvailableLocales()) {
            if (locale.getDisplayLanguage(locale).equals(selectedLanguage)) {
                localeController.setUserLocale(locale);
                break;
            }
        }
        updateTranslations();
    }

    private void updateTranslations() {
        ResourceBundle viewText = localeController.getUIBundle();

        settingsLabel.setText(viewText.getString("sidebar.settings"));
        languageLabel.setText(viewText.getString("settings.language"));

        sidebarViewController.updateTranslations();
    }
}
