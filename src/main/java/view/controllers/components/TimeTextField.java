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

    @Override
    public void replaceText(int start, int end, String text) {
        if (validate(text)) {
            super.replaceText(start, end, text);
        }
    }

    @Override
    public void replaceSelection(String text) {
        if (validate(text)) {
            super.replaceSelection(text);
        }
    }

    private boolean validate(String text) {
        if (text.isEmpty() || text.matches("[0-9:]*")) {
            String newText = getText().substring(0, getCaretPosition()) + text + getText().substring(getCaretPosition());
            return newText.matches("([01]?[0-9]|2[0-3]):[0-5][0-9]");
        }
        return false;
    }
}
