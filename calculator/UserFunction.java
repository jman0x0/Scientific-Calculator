package calculator;

import java.util.ArrayList;
import java.util.Arrays;

public class UserFunction extends MathFunction {
    private String expression;
    private ArrayList<String> variables;

    public UserFunction(String definition) {
        this.variables = new ArrayList<>();
        definition = definition.trim();

        final int header = extractHeader(definition);
        final int variables = extractVariables(this.variables, definition, header);
        this.expression = extractExpression(definition, variables);
    }

    @Override
    public Number apply(ParameterPack pack) {
        final Constants constants = (Constants)pack.constants.clone();
        for (int i = 0; i != variables.size(); ++i) {
            final String variable = variables.get(i);
            final Number value = pack.values.get(i);
            constants.put(variable, value.doubleValue());
        }

        final Calculator calculator = new Calculator(pack.functions, pack.operators, constants);
        return calculator.evaluate(expression);
    }

    public String getDefinition() {
        final String parameters = variables.toString();
        final int pl = parameters.length();
        return identifier + "(" + parameters.substring(1, pl-1) + ") = " + expression;
    }

    public String getExpression() {
        return expression;
    }

    public ArrayList<String> getVariables() {
        return variables;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setVariables(String parameters) {
        ArrayList<String> newVars = new ArrayList<>();
        extractVariables(newVars, parameters, 0);
        variables = newVars;
    }

    private int extractHeader(String definition) {
        for (int i = 0; i != definition.length(); ++i) {
            final char token = definition.charAt(i);

            if (Configuration.isOpeningBracket(token)) {
                if (i == 0) {
                    throw new IllegalArgumentException("Empty function identifier encountered.");
                }
                super.identifier = definition.substring(0, i);
                return i+1;
            }
        }
        throw new IllegalArgumentException("Invalid function definition passed in.");
    }

    private int extractVariables(ArrayList<String> variables, String definition, int start) {
        int beginning = start;
        for (int i = start; i != definition.length(); ++i) {
            final char token = definition.charAt(i);

            if (token == ',' || Configuration.isClosingBracket(token)) {
                if (i == beginning) {
                    throw new IllegalArgumentException("Empty variable identifier encountered.");
                }
                variables.add(definition.substring(beginning, i).trim());
                beginning = i+1;
            }
            else if (!Configuration.isIdentifierChar(token) && !Character.isWhitespace(token)) {
                throw new IllegalArgumentException("Invalid variable token encountered.");
            }
            if (Configuration.isClosingBracket(token)) {
                super.arguments = variables.size();
                return i+1;
            }
        }
        throw new IllegalArgumentException("Function definition is missing closing bracket.");
    }

    private String extractExpression(String definition, int start) {
        for (int i = start; i < definition.length(); ++i) {
            final char token = definition.charAt(i);

            if (!Character.isWhitespace(token) && token != '=') {
                return definition.substring(i);
            }
        }
        throw new IllegalArgumentException("Function definition has no evaluable expression.");
    }
}
