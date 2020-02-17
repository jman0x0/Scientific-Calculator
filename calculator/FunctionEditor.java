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

    private int removeListing(String identifier, int arguments) {
        final int index = functionList.indexOf(functionToString(identifier, arguments));
        if (index >= 0) {
            functionList.remove(index);
        }
        return index;
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
                final String oldIdentifier = extractIdentifier(selectionModel.getSelectedItem());
                final int arguments = extractArguments(selectionModel.getSelectedItem());

                if (!identifier.equals(oldIdentifier)) {
                    final UserFunction custom = (UserFunction)Functions.JMATH.remove(oldIdentifier, arguments);
                    final int shadowed = removeListing(identifier, arguments);
                    Functions.JMATH.remove(identifier, arguments);
                    custom.setIdentifier(identifier);
                    Functions.JMATH.loadFunctionFromString(custom.getDefinition());

                    final int newSelection = selection - (shadowed >= 0 && shadowed < selection ? 1 : 0);
                    functionList.set(newSelection, functionToString(identifier, arguments));
                    selectionModel.select(newSelection);
                    definitionField.setText(custom.getDefinition());
                }
            }
        }));
        variableField.focusedProperty().addListener(((observableValue, v0, focused) -> {
            final int selection = selectionModel.getSelectedIndex();
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

                    final int oldListing = removeListing(identifier, newArguments);
                    final int newSelection = selection - (oldListing >= 0 && oldListing < selection ? 1 : 0);
                    functionList.set(newSelection, functionToString(identifier, newArguments));
                    selectionModel.select(newSelection);
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

    private String extractIdentifier(String overload) {
        final int end = overload.indexOf('(');
        return overload.substring(0, end);
    }

    private int extractArguments(String overload) {
        final int start = overload.indexOf('(');
        final int end = overload.length();
        try {
            Integer value = Integer.parseInt(overload.substring(start+1, end-1));
            return value;
        } catch (Exception exception) {

        }
        return 0;
    }

    @FXML
    private void updateInformation(String overload) {
        if (overload == null) {
            infoPane.setVisible(false);
            return;
        }

        if (!infoPane.isVisible()) {
            infoPane.setVisible(true);
        }
        final String identifier = extractIdentifier(overload);
        final int arguments = extractArguments(overload);

        final MathFunction function = Functions.JMATH.getFunction(identifier, arguments);
        if (!(function instanceof UserFunction)) {
            return;
        }
        final UserFunction custom = (UserFunction)function;
        definitionField.setText(custom.getDefinition());
        identifierField.setText(custom.getIdentifier());
        variableField.setText(custom.getVariables().toString());
        expressionField.setText(custom.getExpression());
    }

    private String functionToString(String identifier, int arguments) {
        return identifier + "(" + arguments + ")";
    }

    private void updateFunctions() {
        for (var value : Functions.JMATH.entrySet()) {
            for (var function : value.getValue()) {
                final String overload = functionToString(function.getIdentifier(), function.getArguments());
                if (function instanceof UserFunction && !functionList.contains(overload)) {
                    functionList.add(overload);
                }
            }
        }
    }
}
