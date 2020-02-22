package calculator;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class CreateConstant extends BorderPane implements SubWindow {
    @FXML
    private TextField identifier;

    @FXML
    private TextField constant;

    @FXML
    private Button confirm;

    @FXML
    public void initialize() {
        constant.textProperty().addListener(((observableValue, oldText, newText) -> {
            //Translate all characters and strings into appropriate symbols.
            final int oldCaret = constant.getCaretPosition() + newText.length() - oldText.length();
            final var edit = KeyConverter.converter.replaceAll(newText, oldCaret);

            if (newText.equals(edit.text)) {
                return;
            }
            //Can't modify text to a shorter length while here,
            //otherwise an IllegalArgumentException is thrown internally by JFX indicating out of bounds access.
            //Thus, we temporarily delay modification.
            Platform.runLater(() -> {
                constant.setText(edit.text);
                constant.positionCaret(edit.cursor);
            });
        }));
    }

    public CreateConstant() {
        final String pathway = "calculator_constants_create.fxml";
        final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(pathway));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public Scene buildScene() {
        return new Scene(this);
    }

    @Override
    public String getTitle() {
        return "Calculator - Constants - Create";
    }

    @FXML
    private void confirmConstant(ActionEvent action) {
        final String identity = identifier.getText();
        if (identity.isEmpty()) {
            return;
        }
        if (!Configuration.isFirstIdentifierChar(identity.charAt(0))) {
            return;
        }

        final Double value;
        try {
            final Calculator calculator = new Calculator();
            value = calculator.evaluate(constant.getText());
        } catch (Exception exception) {
            return;
        }
        if (value.isInfinite() || value.isNaN()) {
            return;
        }

        try {
            Constants.JCONSTANTS.put(identifier.getText(), value);
        } catch (Exception exception) {
            return;
        }
        Configuration.STAGE_STACK.peek().close();
    }

    @Override
    public double getWindowWidth() {
        return Configuration.ASPECT_X * 26;
    }

    @Override
    public double getWindowHeight() {
        return Configuration.ASPECT_Y * 20;
    }
}
