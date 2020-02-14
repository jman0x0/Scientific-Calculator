package calculator;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.text.DecimalFormat;

public class Display extends VBox {
    private int active;
    private int query;
    private String currentText;
    private double answer;
    private double memory;
    private double width;
    private double height;

    @FXML
    public TextArea editor;

    @FXML
    public TextField output;

    @FXML
    public Label status;

    @FXML
    public ToggleGroup angle;

    @FXML
    public ToggleGroup mode;

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


    public Calculator loadCalculator() {
        final Calculator calculator = new Calculator();

        final String angleString = ((Labeled)angle.getSelectedToggle()).getText();

        switch (angleString.toUpperCase()) {
            case "DEGREES":
                calculator.getFunctions().setAngle(Functions.Angle.DEGREES);
                break;
            case "RADIANS":
                calculator.getFunctions().setAngle(Functions.Angle.RADIANS);
                break;
            case "REVOLUTIONS":
                calculator.getFunctions().setAngle(Functions.Angle.REVOLUTIONS);
                break;
            default:

        }

        calculator.getConstants().put("ANS", answer);
        calculator.getConstants().put("MR", memory);

        return calculator;
    }

    /**
     * Evaluate the input stored inside the editor field.
     * @return The value evaluated by the calculator.
     */
    public double evaluateInput() {
        final Calculator calculator = loadCalculator();

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
        final String modeString  = ((Labeled)mode.getSelectedToggle()).getText();
        final boolean useScientific = Math.abs(value) > Configuration.RATIONAL_UPPER_BOUND.doubleValue()
                ||Math.abs(value) < Configuration.RATIONAL_LOWER_BOUND.doubleValue();

        if (useScientific || modeString.equalsIgnoreCase("DECIMAL")) {
            final String formatted = formatDouble(value, Configuration.OUTPUT_DIGIT_COUNT.intValue());
            output.setText(formatted);
        }
        else {
            output.setText(Rational.valueOf(value).toString());
        }
        answer = value;
    }

    /**
     * Clear the display's editor and output fields.
     */
    public void clear() {
        setEditorText("");
        output.setText("0");
        active = 0;
        query = 0;
    }

    /**
     * Completely clear and reset the display's contents and memory.
     */
    public void reset() {
        clear();
        answer = 0;
        memory = 0;
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
        this.width = width;
        this.height = height;
        final double reservedHeight = height * Configuration.DISPLAY_RESERVATION.doubleValue();
        super.setPrefHeight(reservedHeight);

        final double CHAR_PADDING = 1.2;
        final double LINE_PADDING = 1.4;
        final double optimalWidth = width / Configuration.DISPLAY_CHARACTERS.intValue() * CHAR_PADDING;
        final double optimalHeight = reservedHeight / (Configuration.DISPLAY_LINES.intValue()+1) / LINE_PADDING;
        final long fontSize = Math.round(Math.min(optimalWidth, optimalHeight));
        status.setStyle("-fx-font-size:" + fontSize*2/3);
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
        if (Math.abs(value) > Configuration.STANDARD_UPPER_BOUND.doubleValue()) {
            return new DecimalFormat("#." + "#".repeat(max) + "E0").format(value);
        }
        else if (Math.abs(value) < Configuration.SCIENTIFIC_LOWER_BOUND) {
            return "0";
        }
        else if (Math.abs(value) < Configuration.STANDARD_LOWER_BOUND.doubleValue()) {
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

    public void setEditorText(String text) {
        final var formatter = editor.getTextFormatter();
        editor.setTextFormatter(null); //Override text formatter.
        editor.setText(text);
        editor.setTextFormatter(formatter);
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

    /**
     * Determine whether a string corresponds to a binary operator.
     * @param identifier The name of the operator.
     * @return True if the identifier maps to a binary operator, else false.
     */
    private boolean isBinaryOperator(String identifier) {
        return OperatorList.PEMDAS.get(identifier, 2) != null;
    }

    /**
     * Trim off excess newlines past the preservation point.
     * @param value The string to be trimmed.
     * @param preserve The index to search for newlines from.
     * @return The same string if there are no newlines, else a string that has been trimmed to the next newline.
     */
    public String trimNewlines(String value, int preserve) {
        final int newline = value.indexOf('\n', preserve);

        return newline == -1 ? value : value.substring(0, newline);
    }

    private void attachListeners() {
        attachModeListener();
        attachTextFormatter();
        attachTextConverter();
        attachKeyListener();


        Configuration.RATIONAL_LOWER_BOUND.addListener((observable, v0, v1) -> {
            updateDisplay(answer);
        });
        Configuration.RATIONAL_UPPER_BOUND.addListener((observable, v0, v1) -> {
            updateDisplay(answer);
        });
        Configuration.STANDARD_LOWER_BOUND.addListener((observable, v0, v1) -> {
            updateDisplay(answer);
        });
        Configuration.STANDARD_UPPER_BOUND.addListener((observable, v0, v1) -> {
            updateDisplay(answer);
        });
        Configuration.OUTPUT_DIGIT_COUNT.addListener((observable, v0, v1) -> {
            updateDisplay(answer);
        });
        Configuration.DISPLAY_CHARACTERS.addListener((observable, v0, v1) -> {
            scaleTo(width, height);
        });
        Configuration.DISPLAY_RESERVATION.addListener((observable, v0, v1) -> {
            scaleTo(width, height);
        });
        Configuration.DISPLAY_LINES.addListener((observable, v0, v1) -> {
            scaleTo(width, height);
        });
    }

    private void attachModeListener() {
        mode.selectedToggleProperty().addListener(((observableValue, oldToggle, newToggle) -> {
            updateDisplay(answer);
        }));
    }

    private void attachTextFormatter() {
        editor.setTextFormatter(new TextFormatter<String>((TextFormatter.Change change) -> {
            final String oldText = change.getControlText();
            final int caret = change.getRangeStart();
            final int lineNumber = getLineNumber(oldText, caret);

            //Prevent the user from modifying previous entries.
            if (lineNumber != active && change.isContentChange()) {
                return null;
            }

            final String expression = getActiveExpression();
            final String addition = change.getText();
            final int newline = addition.indexOf("\n");
            if (expression.isEmpty() && newline == 0) {
                //Prevent user from skipping a line.
                return null;
            }

            final int start = getActiveCursor();
            if (caret < start + expression.length() && newline >= 0) {
                change.setText(addition.substring(0, newline));
                Platform.runLater(() -> {
                    //Schedule newline and thus expression evaluation later.
                    final String trailing = addition.substring(newline);
                    editor.appendText(trimNewlines(trailing, 1));
                    editor.positionCaret(editor.getLength());
                });
            }
            else if (newline >= 0) {
                change.setText(trimNewlines(addition, newline + 1));
                Platform.runLater(() -> {
                    //Evaluate input after text has been changed.
                    evaluateInput();
                    active++;
                    query = active;
                });
            }
            if (expression.isEmpty() && isBinaryOperator(addition)) {
                change.setText("ANS" + change.getText());
                Platform.runLater(() -> {
                    editor.positionCaret(editor.getLength());
                });
            }
            return change;
        }));
    }

    private void attachTextConverter() {
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

            if (newText.equals(builder.toString())) {
                return;
            }
            //Can't modify text to a shorter length while here,
            //otherwise an IllegalArgumentException is thrown internally by JFX indicating out of bounds access.
            //Thus, we temporarily delay modification.
            final int caret = editor.getCaretPosition() + delta;
            Platform.runLater(() -> {
                setEditorText(builder.toString());
                editor.positionCaret(caret);
            });
        }));
    }

    private void attachKeyListener() {
        editor.setOnKeyPressed((keyEvent ->  {
            switch (keyEvent.getCode()) {
                case UP:
                    if (!keyEvent.isControlDown() && !keyEvent.isShiftDown()) {
                        moveQuery(-1);
                    }
                    break;
                case DOWN:
                    if (!keyEvent.isControlDown() && !keyEvent.isShiftDown()) {
                        moveQuery(1);
                    }
                    break;
                default:
                    break;
            }
        }));
    }
}
