package calculator;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.function.*;

public class OperatorList {
    private Map<String, ArrayList<Operator>> m_operators;

    /**
     * PEMDAS contains a list of operators with priorities that adhere to the order of operations described by PEMDAS.
     * PARENTHESES & BRACKET > EXPONENTS > MULTIPLICATION & DIVISION > ADDITION & SUBTRACTION.
     */
    static public final OperatorList PEMDAS;

    /**
     * IMMEDIATE contains a list of operator with equivalent priorities.
     * Left associative operators are invoked as soon as they reached.
     * Right associative operators are invoked as soon as the operand is found.
     */
    static public final OperatorList IMMEDIATE;
    static {
        PEMDAS = new OperatorList();
        PEMDAS.add("==", 6, 2, (args) -> {
            return args.get(0).equals(args.get(1)) ? 1. : 0.;
        });
        PEMDAS.add("=", 6, 2, (args) -> {
            return args.get(0).equals(args.get(1)) ? 1. : 0.;
        });
        PEMDAS.add("≠", 6, 2, (args) -> {
            return args.get(0).equals(args.get(1)) ? 0. : 1.;
        });
        PEMDAS.add("<", 6, 2, (args) -> {
           return args.get(0) < args.get(1) ? 1. : 0.;
        });
        PEMDAS.add("<=", 6, 2, (args) -> {
            return args.get(0) <= args.get(1) ? 1. : 0.;
        });
        PEMDAS.add("≤", 6, 2, (args) -> {
            return args.get(0) <= args.get(1) ? 1. : 0.;
        });
        PEMDAS.add(">", 6, 2, (args) -> {
            return args.get(0) > args.get(1) ? 1. : 0.;
        });
        PEMDAS.add(">=", 6, 2, (args) -> {
            return args.get(0) >= args.get(1) ? 1. : 0.;
        });
        PEMDAS.add("≥", 6, 2, (args) -> {
            return args.get(0) >= args.get(1) ? 1. : 0.;
        });
        PEMDAS.add("+", 5, 2, (args) -> {
            return args.get(0) + args.get(1);
        });
        PEMDAS.add("-", 5, 2, (args) -> {
            return args.get(0) - args.get(1);
        });
        PEMDAS.add("–", 5, 2, (args) -> {
            return args.get(0) - args.get(1);
        });
        PEMDAS.add("/", 4, 2, (args) -> {
            return args.get(0) / args.get(1);
        });
        PEMDAS.add("*", 4, 2, (args) -> {
            return args.get(0) * args.get(1);
        });
        PEMDAS.add("÷", 4, 2, (args) -> {
            return args.get(0) / args.get(1);
        });
        PEMDAS.add("×", 4, 2, (args) -> {
            return args.get(0) * args.get(1);
        });
        PEMDAS.add("%", 4, 2, (args) -> {
            return args.get(0) % args.get(1);
        });
        PEMDAS.add("-", 3, 1, (args) -> {
        	return -args.get(0);
        }, Operator.Associativity.RIGHT_TO_LEFT);
        PEMDAS.add("√", 3, 1, (args) -> {
            return Math.sqrt(args.get(0));
        }, Operator.Associativity.RIGHT_TO_LEFT);
        PEMDAS.add("^", 3, 2, (args) -> {
            return Math.pow(args.get(0), args.get(1));
        }, Operator.Associativity.RIGHT_TO_LEFT);
        PEMDAS.add("!", 2, 1, (args) -> {
            double factorial = 1;

            for (int i = 2; i <= args.get(0); ++i) {
                factorial *= i;
            }

            return factorial;
        });

        IMMEDIATE = new OperatorList();
        IMMEDIATE.add("==", 1, 2, (args) -> {
            return args.get(0).equals(args.get(1)) ? 1. : 0.;
        });
        IMMEDIATE.add("<", 1, 2, (args) -> {
            return args.get(0) < args.get(1) ? 1. : 0.;
        });
        IMMEDIATE.add("<=", 1, 2, (args) -> {
            return args.get(0) <= args.get(1) ? 1. : 0.;
        });
        IMMEDIATE.add("≤", 1, 2, (args) -> {
            return args.get(0) <= args.get(1) ? 1. : 0.;
        });
        IMMEDIATE.add(">", 1, 2, (args) -> {
            return args.get(0) > args.get(1) ? 1. : 0.;
        });
        IMMEDIATE.add(">=", 1, 2, (args) -> {
            return args.get(0) >= args.get(1) ? 1. : 0.;
        });
        IMMEDIATE.add("≥", 1, 2, (args) -> {
            return args.get(0) >= args.get(1) ? 1. : 0.;
        });
        IMMEDIATE.add("+", 1, 2, (args) -> {
            return args.get(0) + args.get(1);
        });
        IMMEDIATE.add("-", 1, 2, (args) -> {
            return args.get(0) - args.get(1);
        });
        IMMEDIATE.add("–", 1, 2, (args) -> {
            return args.get(0) - args.get(1);
        });
        IMMEDIATE.add("/", 1, 2, (args) -> {
            return args.get(0) / args.get(1);
        });
        IMMEDIATE.add("*", 1, 2, (args) -> {
            return args.get(0) * args.get(1);
        });
        IMMEDIATE.add("÷", 1, 2, (args) -> {
            return args.get(0) / args.get(1);
        });
        IMMEDIATE.add("×", 1, 2, (args) -> {
            return args.get(0) * args.get(1);
        });
        IMMEDIATE.add("%", 1, 2, (args) -> {
            return args.get(0) % args.get(1);
        });
        IMMEDIATE.add("-", 1, 1, (args) -> {
            return -args.get(0);
        }, Operator.Associativity.RIGHT_TO_LEFT);
        IMMEDIATE.add("√", 1, 1, (args) -> {
            return Math.sqrt(args.get(0));
        }, Operator.Associativity.RIGHT_TO_LEFT);
        IMMEDIATE.add("^", 1, 2, (args) -> {
            return Math.pow(args.get(0), args.get(1));
        }, Operator.Associativity.LEFT_TO_RIGHT);
        IMMEDIATE.add("!", 1, 1, (args) -> {
            double factorial = 1;

            for (int i = 2; i <= args.get(0); ++i) {
                factorial *= i;
            }

            return factorial;
        });
    }

    public OperatorList() {
        m_operators = new HashMap<>();
    }

    /**
     * Get all operators associated with a specific identifier.
     * @param identifier The operator's signature.
     * @return A list of operators associated with that identifier.
     */
    private ArrayList<Operator> getOperators(String identifier) {
    	if (!contains(identifier)) {
    		m_operators.put(identifier, new ArrayList<Operator>());
    	}
    	return m_operators.get(identifier);
    }

    /**
     * Register an operator to the OperatorList.
     * @param identifier The operator's signature.
     * @param precedence The operator's precedence/priority over other operators.
     * @param operands The number of operands needed to properly invoke the operator.
     * @param operation The functional operation performed by this operator.
     */
    public void add(String identifier, int precedence, int operands, Function<ArrayList<Double>, Double> operation) {
    	add(identifier, precedence, operands, operation, Operator.Associativity.LEFT_TO_RIGHT);
    }

    /**
     *
     * @param identifier
     * @param precedence
     * @param operands
     * @param operation
     * @param associativity
     */
    public void add(String identifier, int precedence, int operands, Function<ArrayList<Double>, Double> operation, Operator.Associativity associativity) {
    	final ArrayList<Operator> operators = getOperators(identifier);
    	operators.add(new Operator(operands, precedence, identifier, associativity, operation));
    }
    
    public Operator get(String identifier, int operands) {
    	if (contains(identifier)) {
        	final ArrayList<Operator> operators = m_operators.get(identifier);
        	
        	for (Operator operator : operators) {
        		if (operator.getOperands() == operands) {
        			return operator;
        		}
        	}
    	}

        return null;
    }

    /**
     * Search for an operator with a specific identifier, and then return the operator with
     * @param identifier The operator's signature, used for searching.
     * @param operands The preferred number of operands to
     * @return null if the operator doesn't exist, else the operator with the requested operands, else any operator.
     */
    public Operator getPreferenceOrAny(String identifier, int operands) {
    	if (contains(identifier)) {
        	final ArrayList<Operator> operators = m_operators.get(identifier);
        	
        	for (Operator operator : operators) {
        		if (operator.getOperands() == operands) {
        			return operator;
        		}
        	}
        	return operators.get(0);
    	}

        return null;
    }

    /**
     * Determine whether or not an operator is mapped.
     * @param identifier The operator to look for.
     * @return True if the operator is mapped, else false.
     */
    public boolean contains(String identifier) {
        return m_operators.containsKey(identifier);
    }

    /**
     * Determine if this OperatorList has any operators mapped.
     * @return
     */
    public boolean isEmpty() {
        return m_operators.isEmpty();
    }

    /**
     * Remove all mapped operators.
     */
    public void clear() {
        m_operators.clear();
    }
}
