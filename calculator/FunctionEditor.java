package calculator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class FunctionEditor extends EditorWindow {
    @FXML
    private TextField definitionField;

    @FXML
    private TextField identifierField;

    @FXML
    private TextField variableField;

    @FXML
    private TextField expressionField;

    private UserFunction getSelectedFunction() {
        final var selectionModel = selector.getSelectionModel();
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
        final int index = items.indexOf(functionToString(identifier, arguments));
        if (index >= 0) {
            items.remove(index);
        }
        return index;
    }

    @FXML
    public void initialize() {
        final var selectionModel = selector.getSelectionModel();
        updateFunctions();

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
                    items.set(newSelection, functionToString(identifier, arguments));
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
                    items.set(newSelection, functionToString(identifier, newArguments));
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
        super("calculator_functions.fxml");
    }

    @Override
    public String getTitle() {
        return "Calculator - Functions";
    }

    @FXML
    private void onAddAction(ActionEvent action) {
        final CreateFunction creator = new CreateFunction();

        creator.display(Configuration.STAGE_STACK.peek());
        updateFunctions();
    }

    @FXML
    private void onDeleteAction(ActionEvent action) {
        final var selectionModel = selector.getSelectionModel();
        final int selection = selectionModel.getSelectedIndex();

        if (selection >= 0) {
            Functions.JMATH.remove(selectionModel.getSelectedItem());
            items.remove(selection);
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

    @Override
    protected void updateInformation(String overload) {
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
                if (function instanceof UserFunction && !items.contains(overload)) {
                    items.add(overload);
                }
            }
        }
    }
}
