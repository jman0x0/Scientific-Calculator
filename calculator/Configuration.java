package calculator;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.util.Stack;

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
    public static final int FACTOR = 50;

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
    public static final IntegerProperty DISPLAY_LINES = new SimpleIntegerProperty(4);

    /**
     * The minimum amount of characters inside of a equation without line breaking.
     */
    public static final IntegerProperty DISPLAY_CHARACTERS = new SimpleIntegerProperty(20);

    /**
     * Portion of the screen dedicated towards the display.
     */
    public static final DoubleProperty DISPLAY_RESERVATION = new SimpleDoubleProperty(1/3.);

    /**
     * Portion of the screen dedicated towards the keypad.
     */
    public static final DoubleProperty KEYPAD_RESERVATION = new SimpleDoubleProperty(1.0 - DISPLAY_RESERVATION.doubleValue());

    /**
     * Maximum amount of decimal places used to display the result of an expression.
     */
    public static final IntegerProperty OUTPUT_DIGIT_COUNT = new SimpleIntegerProperty(12);

    /**
     * Maximum amount of decimal places used to display the memory's contents.
     */
    public static final int MEMORY_DIGIT_COUNT = 3;

    /**
     * Upper value bound before decimal transitions to scientific format.
     */
    public static final DoubleProperty STANDARD_UPPER_BOUND = new SimpleDoubleProperty(1e12);

    /**
     * Lower value bound before decimal transitions to scientific format.
     */
    public static final DoubleProperty STANDARD_LOWER_BOUND = new SimpleDoubleProperty(1e-6);

    /**
     * Upper value bound before fractions transition to scientific format.
     */
    public static final DoubleProperty RATIONAL_UPPER_BOUND = new SimpleDoubleProperty(1e14);

    /**
     * Lower value bound before fractions transition to scientific format.
     */
    public static final DoubleProperty RATIONAL_LOWER_BOUND = new SimpleDoubleProperty(1e-14);

    public static final Stack<Stage> STAGE_STACK = new Stack<>();

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

    public static MenuItem search(ObservableList<MenuItem> node, String id) {
        for (var item : node) {
            if (item.getText().equalsIgnoreCase(id)) {
                return item;
            }
        }

        return null;
    }

    public static void attachDoubleFormatter(TextField field, DoubleProperty property) {
        final double defaultValue = property.doubleValue();

        field.focusedProperty().addListener((observable, old, newFocus) -> {
            try {
                final Double value = Double.parseDouble(field.getText());
                property.setValue(value);
            } catch(NumberFormatException except) {
                property.setValue(defaultValue);
                field.setText(String.valueOf(property.get()));
            }
        });
    }

    public static Node getInnerPane(ObservableList<MenuItem> items, String name) {
        final Menu constraints = (Menu)search(items, name);
        final CustomMenuItem data = (CustomMenuItem) constraints.getItems().get(0);
        return data.getContent();
    }

    public static void listenFor(ObservableList<MenuItem> items) {
        final Node pane = getInnerPane(items, "Constraints");
        final Node display = getInnerPane(items, "Display");
        final TextField stdLB = (TextField) pane.lookup("#stdLB");
        final TextField stdUB = (TextField) pane.lookup("#stdUB");
        final TextField ratLB = (TextField) pane.lookup("#ratLB");
        final TextField ratUB = (TextField) pane.lookup("#ratUB");
        final Slider lines = (Slider) display.lookup("#slider_lines");
        final Slider precision = (Slider)display.lookup("#slider_precision");
        final Slider scale = (Slider)display.lookup("#slider_scale");
        final Slider characters = (Slider)display.lookup("#slider_characters");
        search(items, "Functions").setOnAction(action -> {
            final FunctionEditor editor = new FunctionEditor();
            editor.display(Configuration.STAGE_STACK.peek());
        });
        search(items, "Constants").setOnAction(action -> {
            final ConstantEditor editor = new ConstantEditor();
            editor.display(Configuration.STAGE_STACK.peek());
        });
        DISPLAY_LINES.bind(lines.valueProperty());
        OUTPUT_DIGIT_COUNT.bind(precision.valueProperty());
        DISPLAY_RESERVATION.bind(scale.valueProperty());
        DISPLAY_CHARACTERS.bind(characters.valueProperty());

        attachDoubleFormatter(stdLB, STANDARD_LOWER_BOUND);
        attachDoubleFormatter(stdUB, STANDARD_UPPER_BOUND);
        attachDoubleFormatter(ratLB, RATIONAL_LOWER_BOUND);
        attachDoubleFormatter(ratUB, RATIONAL_UPPER_BOUND);
    }
}
