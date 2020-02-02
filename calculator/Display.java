package calculator;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.text.DecimalFormat;

public class Display extends VBox {
    private double answer;
    private double memory;
    @FXML
    public TextField editor;

    @FXML
    public TextField output;

    @FXML
    public Label status;

    @FXML
    public void initialize() {
        attachListeners();
    }

    public Display() {
        final String pathway = "calculator_display.fxml";
        final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(pathway));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public double getMemory() {
        return memory;
    }

    public double getAnswer() {
        return answer;
    }

    /**
     * Evaluate the input stored inside the editor field.
     * @return The value evaluated by the calculator.
     */
    public double evaluateInput() {
        final Calculator calculator = new Calculator();
        calculator.getConstants().put("ANS", answer);
        calculator.getConstants().put("MR", memory);

        try {
            final double value = calculator.evaluate(editor.getText());
            updateDisplay(value);

        } catch (Exception exception) {
            output.setText("NaN");
        }

        return answer;
    }

    /**
     * Update the display's output field to the given value.
     * @param value The value for the output field.
     */
    public void updateDisplay(double value) {
        final String formatted = formatDouble(value, Configuration.OUTPUT_DIGIT_COUNT);
        output.setText(formatted);
        answer = value;
    }

    /**
     * Clear the display's editor and output fields.
     */
    public void clear() {
        editor.setText("");
        output.setText("0");
    }

    /**
     * Delete a character in the editor with respect to the caret's position.
     */
    public void deleteCharacter() {
        final int caret = editor.getCaretPosition();

        if (caret > 0) {
            editor.deleteText(caret-1, caret);
        }
    }

    /**
     * Scale the display's font size to optimally fill the text to the given bounds.
     * @param width Horizontal component to constrain the text to.
     * @param height Vertical component to constrain the text to.
     */
    public void scaleTo(double width, double height) {
        final double reservedHeight = height * Configuration.DISPLAY_RESERVATION;
        super.setPrefHeight(reservedHeight);

        final long fontSize = Math.round(Math.min(width/18, reservedHeight/3));
        status.setStyle("-fx-font-size:" + fontSize*4/9);
        editor.setStyle("-fx-font-size:" + fontSize);
        output.setStyle("-fx-font-size:" + fontSize);
    }

    /**
     * Format a double with respect to some precision.
     * @param value Value to be formatted.
     * @param max Maximum amount of digits to be stored.
     * @return The converted value as a string.
     */
    public static String formatDouble(double value, int max) {
        if (Math.abs(value) > Configuration.STANDARD_UPPER_BOUND) {
            return new DecimalFormat("#." + "#".repeat(max) + "E0").format(value);
        }
        else if (Math.abs(value) < Configuration.SCIENTIFIC_LOWER_BOUND) {
            return "0";
        }
        else if (Math.abs(value) < Configuration.STANDARD_LOWER_BOUND) {
            return new DecimalFormat("#." + "#".repeat(max) + "E0").format(value);
        }
        else if (value == Math.floor(value)) {
            return String.format("%.0f", value);
        }
        else {
            return new DecimalFormat("#." + "#".repeat(max)).format(value);
        }
    }

    /**
     * Update and modify the display by responding to a command.
     * @param command Command to be processed.
     */
    public void processCommand(String command) {
        switch (command) {
            case "=":
                evaluateInput();
                break;
            case "AC":
                clear();
                break;
            case "DEL":
                deleteCharacter();
                break;
            case "MC":
                memory = 0;
                break;
            case "M-":
                memory -= answer;
                break;
            case "M+":
                memory += answer;
                break;
            case "M÷":
                memory /= answer;
                break;
            case "M×":
                memory *= answer;
                break;
            default:
                insertText(command);
                break;
        }

        //Update memory indicator.
        status.setText("MR: " + formatDouble(memory, Configuration.MEMORY_DIGIT_COUNT));
    }

    /**
     * Insert text into the editor with respect to its current caret position.
     * @param text The text to be added to the editor.
     */
    public void insertText(String text) {
        final int caret = editor.getCaretPosition();
        editor.insertText(caret, text);
    }

    private void attachListeners() {
        //Listen to changes in text.
        editor.textProperty().addListener(
            (observableValue, oldValue, newValue) -> {
            final StringBuilder builder = new StringBuilder(newValue);
                //Make sure that the updated text is transformed.
                for (int i = 0; i < builder.length(); ++i) {
                final String fixed = KeyConverter.converter.replace(""+builder.charAt(i));
                builder.setCharAt(i, fixed.charAt(0));
            }
                editor.setText(builder.toString());
        });
        editor.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {

                //Evaluate the expression.
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    evaluateInput();
                }
            }
        });
    }
}
