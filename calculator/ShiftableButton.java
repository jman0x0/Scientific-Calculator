package calculator;

import javafx.scene.control.Button;

import java.util.ArrayList;
import java.util.Arrays;

public class ShiftableButton extends Button {
    private String[] states;
    private int active;

    public ShiftableButton() {
        textProperty().addListener(((observableValue, oldText, newText) -> {
            final String[] splits = newText.split("\\s");

            if (states == null) {
                states = Arrays.stream(splits).filter(str -> !str.isEmpty()).toArray(String[]::new);
                setText(states[active]);
            }
        }));
    }

    void shiftState() {
        setState(active + 1);
    }

    void setState(int state) {
        active = state % states.length;
        super.setText(states[active]);
    }

    int getState() {
        return active;
    }
}
