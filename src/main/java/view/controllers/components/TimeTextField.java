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

        if (!character.matches("\\d|:") || text.length() >= 5) {
            event.consume();
            return;
        }

        int caretPosition = this.getCaretPosition();

        switch (text.length()) {
            case 0:
                if (character.matches("[3-9]")) {
                    event.consume();
                    this.setText("0" + character + ":");
                    this.positionCaret(caretPosition + 3);
                } else if (character.matches("[0-2]")) {
                    event.consume();
                    this.setText(character);
                    this.positionCaret(caretPosition + 1);
                } else {
                    event.consume();
                }
                break;
            case 1:
                if (character.matches(":")) {
                    event.consume();
                    this.setText(0 + text + ":");
                    this.positionCaret(caretPosition + 2);
                } else if (text.charAt(0) == '2') {
                    if (character.matches("[0-3]")) {
                        event.consume();
                        this.setText(text + character + ":");
                        this.positionCaret(caretPosition + 2);
                    } else {
                        event.consume();
                    }
                } else {
                    event.consume();
                    this.setText(text + character + ":");
                    this.positionCaret(caretPosition + 2);
                }
                break;
            case 2:
                if (character.matches(":")) {
                    event.consume();
                    this.setText(text + ":");
                    this.positionCaret(caretPosition + 1);
                } else {
                    event.consume();
                }
                break;
            case 3:
                if (character.matches("[0-5]")) {
                    event.consume();
                    this.setText(text + character);
                    this.positionCaret(caretPosition + 1);
                } else {
                    event.consume();
                }
                break;
            case 4:
                if (character.matches("\\d")) {
                    event.consume();
                    this.setText(text + character);
                    this.positionCaret(caretPosition + 1);
                } else {
                    event.consume();
                }
                break;
            default:
                event.consume();
                break;
        }
    }
}
