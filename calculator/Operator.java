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

    /**
     * An operator has operands, precedence, identification, associativity, and an actual function.
     * @param operands The number of operands needed to invoke this operator.
     * @param precedence The precedence relative to other operators.
     * @param identifier The named identifier for this operator.
     * @param associativity The associativity needed to process this operator.
     * @param operation The actual functional object that computes the operator.
     */
    public Operator(int operands, int precedence, String identifier, Associativity associativity, Function<ArrayList<Double>, Double> operation)
    {
        this.operands = operands;
        this.precedence = precedence;
        this.identifier = identifier;
        this.associativity = associativity;
        this.operation = operation;
    }

    /**
     * @return How many operands needed to invoke this operator.
     */
    public int getOperands() {
        return operands;
    }

    /**
     * @return The precedence/priority of this operator.
     */
    public int getPrecedence() {
        return precedence;
    }

    /**
     * @return The identifier corresponding to this operator.
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * @return The associativity of this operator.
     */
    public Associativity getAssociativity() {
        return associativity;
    }

    /**
     * Invoke this operator with an argument list.
     * @param args The numeric values to be processed by the operator.
     * @return The output of this operator.
     */
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

