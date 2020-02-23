package calculator;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import java.util.ArrayList;
import java.util.Arrays;

public class OperatorEditor extends EditorWindow {
    @FXML
    private TextField identifierField;

    @FXML
    private TextField conversionsField;

    @FXML
    private TextField precedenceField;

    @FXML
    private ComboBox associativityField;

    @FXML
    public void initialize() {
        updateOperators();

        final var selectionModel = selector.getSelectionModel();
        conversionsField.focusedProperty().addListener(((observableValue, v0, focused) -> {
            if (!focused) {
                final String target = identifierField.getText();
                final String conversions = conversionsField.getText();
                final String trimmed = conversions.substring(1, conversions.length()-1);

                final ArrayList<String> keys = new ArrayList<>(Arrays.asList(trimmed.split(",")));
                final ArrayList<String> stored = KeyConverter.converter.getAllConversions(target);
                stored.removeAll(keys);

                for (String old : stored) {
                    KeyConverter.converter.remove(old);
                }
                for (String proxy : keys) {
                    KeyConverter.converter.put(proxy.trim(), target);
                }
            }
        }));
        precedenceField.focusedProperty().addListener(((observableValue, v0, focused) -> {
            if (!focused) {
                final String identifier = identifierField.getText();
                final int operands = extractOperands(selectionModel.getSelectedItem());
                final Operator operator = OperatorList.PEMDAS.get(identifier, operands);

                final Integer value;
                try {
                    value = Integer.parseInt(precedenceField.getText());
                } catch (Exception exception) {
                    precedenceField.setText(String.valueOf(operator.getPrecedence()));
                    return;
                }
                operator.setPrecedence(value);
            }
        }));
        associativityField.getSelectionModel().selectedItemProperty().addListener(((observableValue, v0, newValue) -> {
            final String identifier = identifierField.getText();
            final int operands = extractOperands(selectionModel.getSelectedItem());
            final Operator operator = OperatorList.PEMDAS.get(identifier, operands);
            operator.setAssociativity(Operator.Associativity.fromPrettyName((String)newValue));
        }));

    }

    public OperatorEditor() {
        super("calculator_operators.fxml");
    }

    @Override
    public String getTitle() {
        return "Calculator - Operators";
    }

    private String extractIdentifier(String overload) {
        final int end = overload.indexOf('(');
        return overload.substring(0, end);
    }

    private int extractOperands(String overload) {
        final int start = overload.indexOf('(');
        final int end = overload.length();
        try {
            Integer value = Integer.parseInt(overload.substring(start+1, end-1));
            return value;
        } catch (Exception exception) {

        }
        return 0;
    }

    public String operatorIdentity(String identifier, int operands) {
        return identifier + "(" + operands + ")";
    }

    private void updateOperators() {
        for (var entry : OperatorList.PEMDAS.entrySet()) {
            for (var operator : entry.getValue()) {
                final String identity = operatorIdentity(operator.getIdentifier(), operator.getOperands());

                if (!items.contains(identity)) {
                    items.add(identity);
                }
            }
        }
    }

    @Override
    protected void updateInformation(String operatorIdentity) {
        final String identifier = extractIdentifier(operatorIdentity);
        final int operands = extractOperands(operatorIdentity);

        final Operator operator = OperatorList.PEMDAS.get(identifier, operands);
        identifierField.setText(identifier);
        conversionsField.setText(KeyConverter.converter.getAllConversions(identifier).toString());
        associativityField.getSelectionModel().select(operator.getAssociativity().prettyName());
        precedenceField.setText(String.valueOf(operator.getPrecedence()));
    }

}
