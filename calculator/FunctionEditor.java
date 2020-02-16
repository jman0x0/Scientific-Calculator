package calculator;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class FunctionEditor extends GridPane implements SubWindow {
    @FXML
    private Button add;

    @FXML
    private Button delete;

    @FXML
    private ObservableList<String> functionList;

    @FXML
    private ListView<String> functionSelector;

    @FXML
    private GridPane infoPane;

    @FXML
    private TextField definitionField;

    @FXML
    private TextField identifierField;

    @FXML
    private TextField variableField;

    @FXML
    private TextField expressionField;

    private UserFunction getSelectedFunction() {
        final var selectionModel = functionSelector.getSelectionModel();
        final int selection = selectionModel.getSelectedIndex();

        if (selection >= 0) {
            final String identifier = identifierField.getText();
            final MathFunction function = Functions.JMATH.getFunction(identifier);

            if (function instanceof UserFunction) {
                return (UserFunction)function;
            }
        }
        return null;
    }

    @FXML
    public void initialize() {
        final var selectionModel = functionSelector.getSelectionModel();

        updateFunctions();
        selectionModel.selectedItemProperty().addListener(((observableValue, oldValue, newValue) -> {
            updateInformation(newValue);
        }));
        identifierField.focusedProperty().addListener(((observableValue, v0, focused) -> {
            final int selection = selectionModel.getSelectedIndex();
            if (!focused && selection >= 0) {
                final String identifier = identifierField.getText();
                final String oldIdentifier = selectionModel.getSelectedItem();
                final UserFunction custom = (UserFunction)Functions.JMATH.getFunction(oldIdentifier);

                if (!identifier.equals(oldIdentifier)) {
                    functionList.remove(identifier);
                    Functions.JMATH.remove(oldIdentifier, custom.getArguments());
                    functionList.set(selection, identifier);
                    custom.setIdentifier(identifier);
                    Functions.JMATH.loadFunctionFromString(custom.getDefinition());
                    definitionField.setText(custom.getDefinition());
                    selectionModel.select(selection);
                }
            }
        }));
        variableField.focusedProperty().addListener(((observableValue, v0, focused) -> {
            final UserFunction custom = getSelectedFunction();
            if (!focused && custom != null) {
                final String identifier = identifierField.getText();
                final String variables = variableField.getText();
                final int oldArguments = custom.getArguments();
                final int newArguments = 1+(int)variables.chars().filter(ch -> ch == ',').count();
                final MathFunction shadowed = Functions.JMATH.getFunction(identifier, newArguments);
                try {
                    custom.setVariables(variables.substring(1));
                } catch (Exception exception) {
                    variableField.setText(custom.getVariables().toString());
                    return;
                }
                if (oldArguments != newArguments) {
                    Functions.JMATH.remove(identifier, oldArguments);
                    Functions.JMATH.remove(shadowed);
                }
                definitionField.setText(custom.getDefinition());
            }
        }));
        expressionField.focusedProperty().addListener(((observableValue, v0, focused) -> {
            final UserFunction custom = getSelectedFunction();
            if (!focused && custom != null) {
                custom.setExpression(expressionField.getText());
                definitionField.setText(custom.getDefinition());
            }
        }));
    }

    public FunctionEditor() {
        final String pathway = "calculator_functions.fxml";
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
        return "Calculator - Functions";
    }

    @Override
    public double getWindowWidth() {
        return Configuration.ASPECT_X * 40;
    }

    @Override
    public double getWindowHeight() {
        return Configuration.ASPECT_Y * 40;
    }

    @FXML
    private void onAddAction(ActionEvent action) {
        final CreateFunction creator = new CreateFunction();

        creator.display(Configuration.STAGE_STACK.peek());
        updateFunctions();
    }

    @FXML
    private void onDeleteAction(ActionEvent action) {
        final var selectionModel = functionSelector.getSelectionModel();
        final int selection = selectionModel.getSelectedIndex();

        if (selection >= 0) {
            Functions.JMATH.remove(selectionModel.getSelectedItem());
            functionList.remove(selection);
        }
    }

    @FXML
    private void updateInformation(String identifier) {
        if (identifier == null) {
            infoPane.setVisible(false);
            return;
        }

        if (!infoPane.isVisible()) {
            infoPane.setVisible(true);
        }
        final MathFunction function = Functions.JMATH.getFunction(identifier);
        if (!(function instanceof UserFunction)) {
            return;
        }
        final UserFunction custom = (UserFunction)function;
        definitionField.setText(custom.getDefinition());
        identifierField.setText(custom.getIdentifier());
        variableField.setText(custom.getVariables().toString());
        expressionField.setText(custom.getExpression());
    }

    private void updateFunctions() {
        for (var value : Functions.JMATH.entrySet()) {
            for (var function : value.getValue()) {
                if (function instanceof UserFunction && !functionList.contains(function.getIdentifier())) {
                    functionList.add(function.getIdentifier());
                }
            }
        }
    }
}
