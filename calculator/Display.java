package calculator;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Display extends VBox {
    private int active;
    private int query;
    private String currentText;
    private double answer;
    private double memory;

    @FXML
    public TextArea editor;

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
            final String expression = getActiveExpression();
            final double value = calculator.evaluate(expression);
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

        final long fontSize = Math.round(Math.min(width/16, reservedHeight/6));
        status.setStyle("-fx-font-size:" + fontSize*2/3);
        editor.setStyle("-fx-font-size:" + fontSize*3/4);
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
        editor.replaceSelection(text);
    }

    /**
     * Query for a previously stored operation.
     * @param displacement The amount to displace the query.
     */
    public void moveQuery(int displacement) {
        final int lastParagraph = getLastParagraph();
        final int index = query + displacement;
        final int request = Math.min(lastParagraph, Math.max(0, index));
        if (query == request) {
            return;
        }

        //Moving up operation history, save active text.
        if (displacement < 0 && query == lastParagraph) {
            currentText = getParagraph(query);
        }

        //Moving down operation history, choose appropriate paragraph.
        if (displacement > 0 && request == lastParagraph) {
            updateActive(currentText);
        }
        else {
            updateActive(getParagraph(request));
        }
        query = request;
        //Update the cursor position to the end of the text area.
        Platform.runLater(() -> {
            editor.positionCaret(editor.getText().length());
        });
    }

    /**
     * Get the number of newlines preceding a index of text.
     * @param text The characters to evaluate and process.
     * @param position The ending delimiter to count upto.
     * @return The number of lines preceding the index.
     */
    private int getLineNumber(String text, int position) {
        int lineNumber = 0;
        for (int i = 0; i < position; ++i) {
            if (text.charAt(i) == '\n') {
                ++lineNumber;
            }
        }

        return lineNumber;
    }

    /**
     * Get the active expression of the TextArea.
     * @return The last line/paragraph of the TextArea.
     */
    private String getActiveExpression() {
        final var paragraphs = editor.getParagraphs();
        return paragraphs.get(active).toString();
    }

    /**
     * Get the start of the active line.
     * @return The start(caret position) of the active line.
     */
    private int getActiveCursor() {
        int concern = 0;
        final int end = editor.getText().length();
        for (int i = 0; i < end; ++i) {
            if (editor.getText().charAt(i) == '\n') {
                concern = i+1;
            }
        }
        return concern;
    }

    /**
     * Set the text of the last line/paragraph of the TextArea.
     * @param value The text the last line will be assigned to.
     */
    private void updateActive(String value) {
        final int start = getActiveCursor();
        final int end = editor.getText().length();
        editor.replaceText(start, end, value);
    }

    /**
     * Quick way of getting the last paragraph.
     * @return Index of the last accessible paragraph.
     */
    private int getLastParagraph() {
    	return editor.getParagraphs().size() - 1;
    }

    /**
     * Get a paragraph from the active TextArea editor.
     * @param index The paragraph to retrieve.
     * @return The requested paragraph from the TextArea.
     */
    private String getParagraph(int index) {
    	return editor.getParagraphs().get(index).toString();
    }

    private void attachListeners() {
        //Prevent the user from modifying previous entries.
        editor.setTextFormatter(new TextFormatter<String>((TextFormatter.Change change) -> {
            final String oldText = change.getControlText();
            final int caret = change.getRangeStart();
            final int lineNumber = getLineNumber(oldText, caret);
            if (lineNumber != active && change.isContentChange()) {
                return null;
            }

            final String expression = getActiveExpression();
            final String addition = change.getText();
            final int splicing = addition.indexOf("\n");
            if (expression.isEmpty() && splicing == 0) {
                //Prevent user from skipping a line.
                return null;
            }

            final int start = getActiveCursor();
            if (caret < start + expression.length() && splicing >= 0) {
                Platform.runLater(() -> {
                    final int end = start + expression.length();
                    editor.positionCaret(end + addition.length());
                    editor.replaceSelection(addition);
                });
                change = null;
            }
            else if (splicing >= 0) {
                evaluateInput();
                active = 1 + getLineNumber(oldText, oldText.length());
                query = active;
            }

            return change;
        }));

        editor.textProperty().addListener(((observableValue, oldText, newText) -> {
            //Translate all characters and strings into appropriate symbols.
            final StringBuilder builder = new StringBuilder(editor.getText());
            int delta = newText.length() - oldText.length();
            for (var keypair : KeyConverter.converter.entrySet()) {
                final String key = keypair.getKey();
                final String value = keypair.getValue();
                int index = 0;

                //Replace all characters.
                while ((index = builder.indexOf(key, index)) != -1) {
                    builder.delete(index, index+key.length());
                    builder.insert(index, value);
                    index += key.length();
                    delta += value.length() - key.length();
                }
            }
            final int caret = editor.getCaretPosition() + delta;
            //Can't modify text to a shorter length while here,
            //otherwise an IllegalArgumentException is thrown internally by JFX indicating out of bounds access.
            //Thus, we temporarily delay modification.
            Platform.runLater(() -> {
                final var formatter = editor.getTextFormatter();
                editor.setTextFormatter(null);
                editor.setText(builder.toString());
                editor.setTextFormatter(formatter);
                editor.positionCaret(caret);
            });
        }));
        editor.setOnKeyPressed((keyEvent ->  {
            switch (keyEvent.getCode()) {
                case UP:
                    moveQuery(-1);
                    break;
                case DOWN:
                    moveQuery(1);
                    break;
                default:
                    break;
            }
        }));
    }
}
