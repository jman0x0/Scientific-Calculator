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
    public static final double DISPLAY_RESERVATION = 0.25;

    /**
     * Portion of the screen dedicated towards the keypad.
     */
    public static final double KEYPAD_RESERVATION = 0.75;

    /**
     * Maximum amount of decimal places used to display the result of an expression.
     */
    public static final int OUTPUT_DIGIT_COUNT = 10;

    /**
     * Maximum amount of decimal places used to display the memory's contents.
     */
    public static final int MEMORY_DIGIT_COUNT = 3;
}
