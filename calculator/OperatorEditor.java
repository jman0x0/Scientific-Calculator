package calculator;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class OperatorEditor extends GridPane implements SubWindow {
    @FXML
    private Button add;

    @FXML
    private Button delete;

    @FXML
    private ObservableList<String> operatorList;

    @FXML
    private ListView<String> operatorSelector;

    @FXML
    private GridPane infoPane;

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

        final var selectionModel = operatorSelector.getSelectionModel();
        selectionModel.selectedItemProperty().addListener(((observableValue, oldValue, newValue) -> {
            updateInformation(newValue);
        }));
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
        final String pathway = "calculator_operators.fxml";
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
        return "Calculator - Operators";
    }

    @Override
    public double getWindowWidth() {
        return Configuration.ASPECT_X * 40;
    }

    @Override
    public double getWindowHeight() {
        return Configuration.ASPECT_Y * 40;
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

                if (!operatorList.contains(identity)) {
                    operatorList.add(identity);
                }
            }
        }
    }

    private void updateInformation(String operatorIdentity) {
        if (operatorIdentity == null) {
            infoPane.setVisible(false);
            return;
        }
        if (!infoPane.isVisible()) {
            infoPane.setVisible(true);
        }
        final String identifier = extractIdentifier(operatorIdentity);
        final int operands = extractOperands(operatorIdentity);

        final Operator operator = OperatorList.PEMDAS.get(identifier, operands);
        identifierField.setText(identifier);
        conversionsField.setText(KeyConverter.converter.getAllConversions(identifier).toString());
        associativityField.getSelectionModel().select(operator.getAssociativity().prettyName());
        precedenceField.setText(String.valueOf(operator.getPrecedence()));
    }

}
