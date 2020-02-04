package calculator;

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
    public static final int OUTPUT_DIGIT_COUNT = 10;

    /**
     * Maximum amount of decimal places used to display the memory's contents.
     */
    public static final int MEMORY_DIGIT_COUNT = 3;

    /**
     * Upper value bound before transitioning to scientific format.
     */
    public static final double STANDARD_UPPER_BOUND = 10e12;

    /**
     * Lower value bound before transitioning to scientific format.
     */
    public static final double STANDARD_LOWER_BOUND = 10e-6;

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
}
