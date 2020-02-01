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

    public void updateDisplay(double value) {
        final String formatted = formatDouble(value, Configuration.OUTPUT_DIGIT_COUNT);
        output.setText(formatted);
        answer = value;
    }

    public void clear() {
        editor.setText("");
        output.setText("0");
    }

    public void backspace() {
        final int caret = editor.getCaretPosition();

        if (caret > 0) {
            editor.deleteText(caret-1, caret);
        }
    }

    public void scaleTo(double width, double height) {
        final double reservedHeight = height * Configuration.DISPLAY_RESERVATION;
        super.setPrefHeight(reservedHeight);

        final long fontSize = Math.round(Math.min(width/18, reservedHeight/3));
        status.setStyle("-fx-font-size:" + fontSize*4/9);
        editor.setStyle("-fx-font-size:" + fontSize);
        output.setStyle("-fx-font-size:" + fontSize);
    }

    public static String formatDouble(double value, int max) {
        if (value == Math.floor(value)) {
            return String.format("%.0f", value);
        }
        else {
            return new DecimalFormat("#." + "#".repeat(max)).format(value);
        }
    }

    public void processCommand(String command) {
        switch (command) {
            case "=":
                evaluateInput();
                break;
            case "AC":
                clear();
                break;
            case "DEL":
                backspace();
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

        status.setText("MR: " + formatDouble(memory, Configuration.MEMORY_DIGIT_COUNT));
    }

    public void insertText(String text) {
        final int caret = editor.getCaretPosition();
        editor.insertText(caret, text);
    }

    private void attachListeners() {
        editor.textProperty().addListener(
            (observableValue, oldValue, newValue) -> {
            final StringBuilder builder = new StringBuilder(newValue);

            for (int i = 0; i < builder.length(); ++i) {
                final String fixed = KeyConverter.converter.replace(""+builder.charAt(i));
                builder.setCharAt(i, fixed.charAt(0));
            }
                editor.setText(builder.toString());
        });
        editor.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    evaluateInput();
                }
            }
        });
    }
}
