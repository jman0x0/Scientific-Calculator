package calculator;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.function.*;

public class OperatorList {
    private Map<String, ArrayList<Operator>> m_operators;

    static public final OperatorList PEMDAS;
    static public final OperatorList NAIVE;
    static {
        PEMDAS = new OperatorList();
        PEMDAS.add("==", 6, 2, (args) -> {
            return args.get(0).equals(args.get(1)) ? 1. : 0.;
        });
        PEMDAS.add("<", 6, 2, (args) -> {
           return args.get(0) < args.get(1) ? 1. : 0.;
        });
        PEMDAS.add("<=", 6, 2, (args) -> {
            return args.get(0) <= args.get(1) ? 1. : 0.;
        });
        PEMDAS.add(">", 6, 2, (args) -> {
            return args.get(0) > args.get(1) ? 1. : 0.;
        });
        PEMDAS.add(">=", 6, 2, (args) -> {
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

        NAIVE = new OperatorList();
        NAIVE.add("+", 1, 2, (args) -> {
            return args.get(0) + args.get(1);
        });
        NAIVE.add("-", 1, 2, (args) -> {
            return args.get(0) - args.get(1);
        });
        NAIVE.add("/", 1, 2, (args) -> {
            return args.get(0) / args.get(1);
        });
        NAIVE.add("*", 1, 2, (args) -> {
            return args.get(0) * args.get(1);
        });
        NAIVE.add("%", 1, 2, (args) -> {
            return args.get(0) % args.get(1);
        });
        NAIVE.add("^", 1, 2, (args) -> {
            return Math.pow(args.get(0), args.get(1));
        });
        NAIVE.add("-", 1, 1, (args) -> {
            return -args.get(0);
        });
        NAIVE.add("!", 1, 1, (args) -> {
            double factorial = 1;

            for (int i = 2; i <= args.get(0); ++i) {
                factorial *= i;
            }

            return factorial;
        });
    }

    public OperatorList() {
        m_operators = new HashMap<String, ArrayList<Operator>>();
    }
    
    private ArrayList<Operator> getOperators(String identifier) {
    	if (!contains(identifier)) {
    		m_operators.put(identifier, new ArrayList<Operator>());
    	}
    	return m_operators.get(identifier);
    }
    
    public void add(String identifier, int precedence, int operands, Function<ArrayList<Double>, Double> operation) {
    	add(identifier, precedence, operands, operation, Operator.Associativity.LEFT_TO_RIGHT);
    }

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

    public boolean contains(String identifier) {
        return m_operators.containsKey(identifier);
    }

    public boolean isEmpty() {
        return m_operators.isEmpty();
    }

    public void clear() {
        m_operators.clear();
    }
}
