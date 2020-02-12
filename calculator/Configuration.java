package calculator;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.ObservableList;

/**
 * Class used to configure aspects of the calculator.
 */
public class Configuration {

    /**
     * Controls the width of the starting window.
     */
    public static final int ASPECT_X = 14;
    /**
     * Controls the height of the starting window.
     */
    public static final int ASPECT_Y = 9;

    /**
     * Factor used to specify the magnitude of the starting window.
     */
    public static final int FACTOR = 45;

    /**
     * Width of the starting screen.
     */
    public static final int SCREEN_WIDTH = ASPECT_X * FACTOR;

    /**
     * Height of the starting screen.
     */
    public static final int SCREEN_HEIGHT = ASPECT_Y * FACTOR;

    /**
     * The minimum amount of editor entries to be viewable at once.
     */
    public static final int DISPLAY_LINES = 4;

    /**
     * The minimum amount of characters inside of a equation without line breaking.
     */
    public static final int DISPLAY_CHARACTERS = 20;

    /**
     * Portion of the screen dedicated towards the display.
     */
    public static final double DISPLAY_RESERVATION = 1/3.;

    /**
     * Portion of the screen dedicated towards the keypad.
     */
    public static final double KEYPAD_RESERVATION = 1.0 - DISPLAY_RESERVATION;

    /**
     * Maximum amount of decimal places used to display the result of an expression.
     */
    public static final int OUTPUT_DIGIT_COUNT = 12;

    /**
     * Maximum amount of decimal places used to display the memory's contents.
     */
    public static final int MEMORY_DIGIT_COUNT = 3;

    /**
     * Upper value bound before transitioning to scientific format.
     */
    public static final DoubleProperty STANDARD_UPPER_BOUND = new SimpleDoubleProperty(10e12);

    /**
     * Lower value bound before transitioning to scientific format.
     */
    public static final DoubleProperty STANDARD_LOWER_BOUND = new SimpleDoubleProperty(1e-6);

    public static final double RATIONAL_UPPER_BOUND = 10e14;


    public static final double RATIONAL_LOWER_BOUND = 10e-14;

    /**
     * Lower value bound before transition to 0.
     */
    public static final double SCIENTIFIC_LOWER_BOUND = 10e-308;

    public static boolean isFloatingPoint(char ch) {
        return Character.isDigit(ch) || ch == '.';
    }

    public static boolean isIdentifierChar(char ch) {
        return Character.isJavaIdentifierPart(ch);
    }

    public static boolean isOperatorChar(char ch) {
        return !isIdentifierChar(ch) && !Character.isWhitespace(ch);
    }

    public static boolean isOpeningBracket(char ch) {
        return "([{".indexOf(ch) != -1;
    }

    public static boolean isClosingBracket(char ch) {
        return ")]}".indexOf(ch) != -1;
    }

    public static char getClosingBracket(char opener) {
        return ")]}".charAt("([{".indexOf(opener));
    }
    
    public static Menu search(ObservableList<MenuItem> node, String id) {
    	for (var item : node) {
    		if (item.getText().equalsIgnoreCase(id)) {
    			return (Menu)item;
    		}
    	}
    	
    	return null;
    }

    public static StringConverter<Number> defaultingConverter(double defaultValue) {
        return new NumberStringConverter() {
            @Override
            public Number fromString(String string) {
                try {
                    Double value = Double.parseDouble(string);
                    return value;
                } catch (NumberFormatException e) {
                }
                return defaultValue;
            }
        };
    }

    public static void attachDoubleFormatter(TextField field) {

    }

    public static void listenFor(ObservableList<MenuItem> items) {
        final Menu constraints = search(items, "Constraints");
        final CustomMenuItem data = (CustomMenuItem) constraints.getItems().get(0);
        final Node pane = data.getContent();
        final TextField stdLB = (TextField) pane.lookup("#stdLB");
        final TextField stdUB = (TextField) pane.lookup("#stdUB");
        final StringConverter<Number> cv = new NumberStringConverter() {
            @Override
            public Number fromString(String string) {
                try {
                    Double.parseDouble(string);
                } catch (NumberFormatException e) {
                    return null;
                }
                return 0;
            }
        };

        final StringConverter<Number> converter = new NumberStringConverter();
        //Bindings.bindBidirectional(stdLB.textProperty(), STANDARD_UPPER_BOUND, converter);
        Bindings.bindBidirectional(stdLB.textProperty(), STANDARD_LOWER_BOUND, defaultingConverter(1e-6));
        Bindings.bindBidirectional(stdUB.textProperty(), STANDARD_UPPER_BOUND, converter);

        stdLB.setTextFormatter(new TextFormatter<>(change -> {
            final String newText = change.getControlNewText();
            if (change.isContentChange() && !newText.isEmpty()) {
                if (change.getText().contains("-")) {
                    final int negation = change.getControlText().trim().lastIndexOf('-');

                    if (negation > 0) {
                        return null;
                    }
                }
                else if (change.getText().toUpperCase().contains("E")) {
                    final int scientific = change.getControlText().trim().toUpperCase().lastIndexOf('E');

                    if (scientific > 0) {
                        return null;
                    }
                }
                else {
                    try {
                        Double.parseDouble(change.getControlNewText());
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
            }
            return change;
        }));
    }
}
