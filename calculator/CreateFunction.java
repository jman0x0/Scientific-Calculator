package calculator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class CreateFunction extends BorderPane implements SubWindow {
    @FXML
    private TextField expression;

    @FXML
    private Button confirm;

    public CreateFunction() {
        final String pathway = "calculator_function_create.fxml";
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
        return "Calculator - Functions - Create";
    }

    @FXML
    private void confirmExpression(ActionEvent action) {
        try {
            Functions.JMATH.loadFunctionFromString(expression.getText());
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
