package view.controllers.components;

import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

public class TimeTextField extends TextField {
    public TimeTextField() {
        this.setPromptText("HH:MM");
        this.addEventFilter(KeyEvent.KEY_TYPED, this::handleKeyTyped);
    }

    private void handleKeyTyped(KeyEvent event) {
        String character = event.getCharacter();
        String text = this.getText();

        if (!character.matches("[0-9]") || text.length() >= 5) {
            event.consume();
            return;
        }

        int caretPosition = this.getCaretPosition();

        switch (text.length()) {
            case 0:
                if (character.matches("[3-9]")) {
                    event.consume();
                    this.setText("2");
                    this.positionCaret(caretPosition + 1);
                }
                break;
            case 1:
                if (text.charAt(0) == '2' && character.matches("[4-9]")) {
                    event.consume();
                    this.setText("23:");
                    this.positionCaret(caretPosition + 2);
                } else {
                    event.consume();
                    this.setText(text + character + ":");
                    this.positionCaret(caretPosition + 2);
                }
                break;
            case 3:
                if (character.matches("[6-9]")) {
                    event.consume();
                    this.setText(text + "5");
                    this.positionCaret(caretPosition + 1);
                }
                break;
        }
    }
}
