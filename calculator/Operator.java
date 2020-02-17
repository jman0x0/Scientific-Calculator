package calculator;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.*;

public class Operator {
    public enum Associativity {
        LEFT_TO_RIGHT,
        RIGHT_TO_LEFT;

        public String prettyName() {
            switch (this) {
                case LEFT_TO_RIGHT:
                    return "Left to Right";
                case RIGHT_TO_LEFT:
                    return "Right to Left";
                default:
                    return "";
            }
        }

        public static Associativity fromPrettyName(String pretty) {
            if (pretty.equals("Left to Right")) {
                return LEFT_TO_RIGHT;
            }
            return RIGHT_TO_LEFT;
        }
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

    public void setPrecedence(int precedence) {
        this.precedence = precedence;
    }

    public void setAssociativity(Associativity associativity) {
        this.associativity = associativity;
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

    /**
     * Determine if this operator has precedence over the other.
     * @param other The operator to have it's precedence compared.
     * @return True if this operator has precedence over the other, else false.
     */
    public boolean hasPrecedence(Operator other)
    {
        final int rp = other.getPrecedence();
        //Lower precedence always has priority.
        if (precedence < rp) {
            return true;
        }
        //Otherwise they must have equivalent precedence and left associativity.
        //Right associativity with equal precedence has priority.
        return (precedence == rp) && (other.getAssociativity() == Operator.Associativity.LEFT_TO_RIGHT);
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

