package calculator;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ButtonBase;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class Keypad extends GridPane {
    @FXML
    public void initialize() {
        attachListeners();
        disableFocus();
    }

    public Keypad() {
        //Load the keypad's design and make this the root and controller.
        final String pathway = "calculator_keypad.fxml";
        final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(pathway));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void listenForKey(EventHandler<ActionEvent> event) {
        for (Node node : getChildren()) {
            final ButtonBase button = (ButtonBase)node;
            button.setOnAction(event);
        }
    }

    private void attachListeners() {
        final int ROWS = getRowCount();
        final int COLUMNS = getColumnCount();
        final double XPADDING = getHgap() * (COLUMNS + 1);
        final double YPADDING = getVgap() * (ROWS + 1);

        //Scale each button when the pane changes dimensions.
        widthProperty().addListener((observableValue, oldWidth, newWidth) -> {
            for (Node node : getChildren()) {
                final ButtonBase button = (ButtonBase)node;
                final double width = newWidth.doubleValue();
                button.setPrefWidth((width - XPADDING) / COLUMNS);
            }
        });
        heightProperty().addListener((observableValue, oldHeight, newHeight) -> {
            for (Node node : getChildren()) {
                final ButtonBase button = (ButtonBase)node;
                final double height = newHeight.doubleValue();
                button.setPrefHeight((height - YPADDING) / ROWS);
            }
        });

        //Make sure each key will scale its font.
        for (Node node : getChildren()) {
            final ButtonBase button = (ButtonBase)node;
            button.widthProperty().addListener((observableValue, oldWidth, newWidth) -> {
                scaleFont(button);
            });
            button.heightProperty().addListener((observableValue, oldHeight, newHeight) -> {
                scaleFont(button);
            });
        }
    }

    public void shiftButtons() {
        for (Node node : getChildren()) {
            if (node instanceof ShiftableButton) {
                ((ShiftableButton) node).shiftState();
            }
        }
    }

    private void disableFocus() {
        //Button should never take focus.
        for (Node node : getChildren()) {
            node.setFocusTraversable(false);
        }
    }

    private static void scaleFont(ButtonBase button)
    {
        //Scale the button's font in accordance to its dimensions.
        final long width = Math.round(button.getWidth());
        final long height = Math.round(button.getHeight());
        final long min = Math.min(width, height*4/3);
        button.setStyle("-fx-font-size:" + (min/4 + 1));
    }
}
