package calculator;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.*;

public class Operator {
    public enum Associativity {
        LEFT_TO_RIGHT,
        RIGHT_TO_LEFT
    }

    private int operands;
    private int precedence;
    private String identifier;
    private Associativity associativity;
    private Function<ArrayList<Double>, Double> operation;

    public Operator(int operands, int precedence, String identifier, Associativity associativity, Function<ArrayList<Double>, Double> operation)
    {
        this.operands = operands;
        this.precedence = precedence;
        this.identifier = identifier;
        this.associativity = associativity;
        this.operation = operation;
    }

    public int getOperands() {
        return operands;
    }

    public int getPrecedence() {
        return precedence;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Associativity getAssociativity() {
        return associativity;
    }

    public Double apply(ArrayList<Double> args) {
        return operation.apply(args);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!Operator.class.isAssignableFrom(o.getClass())) {
            return false;
        }
        Operator operator = (Operator)o;
        return      this.operands == operator.operands
                &&  this.precedence == operator.precedence
                &&  this.identifier == operator.identifier;
    }

    @Override
    public int hashCode() {
        return Objects.hash(operands, precedence, identifier, operation);
    }
}

