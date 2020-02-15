package calculator;

import java.util.ArrayList;

public abstract class MathFunction {
    public static class ParameterPack {
        ArrayList<Number> values;
        Functions functions;
        Constants constants;
        OperatorList operators;
        Calculator.Angle angle;

        public ParameterPack(ArrayList<Number> values) {
            this.values = values;
        }

        public ParameterPack(ArrayList<Number> values, Functions functions, Constants constants, OperatorList operators, Calculator.Angle angle) {
            this.values = values;
            this.functions = functions;
            this.constants = constants;
            this.operators = operators;
            this.angle = angle;
        }
    }

    protected String identifier;
    protected int arguments;

    public MathFunction() {
        this(null, 0);
    }

    public MathFunction(String identifier, int arguments) {
        this.identifier = identifier;
        this.arguments = arguments;
    }

    public String getIdentifier() {
        return identifier;
    }

    public int getArguments() {
        return arguments;
    }

    public abstract Number apply(ParameterPack pack);
}