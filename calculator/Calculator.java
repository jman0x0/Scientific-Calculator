package calculator;

import java.util.*;

public class Calculator {
    private Functions m_functions;
    private OperatorList m_operators;
	private Constants m_constants;
    public static class Term {
        public enum Type {
            OPERATOR,
            OPERAND
        }

        public Term(Type type, int extract, Object value)
        {
            this.type = type;
            this.extract = extract;
            this.value = value;
        }

        public Type type;
        public int extract;
        public Object value;
    }

    public static class Parsing {
        public Double value;
        public int extract;
        public Character closer;

        public Parsing(Double value, int extract, Character closer) {
            this.value = value;
            this.extract = extract;
            this.closer = closer;
        }
    }
	
	public static class MultipleParse {
		public ArrayList<Double> arguments;
		public int extract;
		
		public MultipleParse() {
			this.arguments = new ArrayList<>();
		}
	}

    public static class ExpressionInfo {
        final String expression;
        final int start;
        final int end;

        ExpressionInfo(String expression, int start, int end) {
            this.expression = expression;
            this.start = start;
            this.end = end;
        }
    }

    public Calculator()
    {
        this(Functions.JMATH, OperatorList.PEMDAS, Constants.JCONSTANTS);
    }

    public Calculator(Functions functions, OperatorList operators, Constants constants)
    {
		this.m_functions = functions;
		this.m_operators = operators;
		this.m_constants = constants;
    }

    public Functions getFunctions() {
        return m_functions;
    }

    public void setFunctions(Functions functions) {
        m_functions = functions;
    }

    public OperatorList getOperators() {
        return m_operators;
    }

    public void setOperators(OperatorList operators) {
        m_operators = operators;
    }

    public Constants getConstants() {
    	return m_constants;
    }
    
    public void setConstants(Constants constants) {
    	m_constants = constants;
    }

    /**
     * Evaluate
     * @param expression The expression to evaluate.
     * @return The value of the expression, interpreted by the calculator.
     */
    public double evaluate(String expression)
    {
        //Strip whitespace before processing.
        final String stripped = expression.replaceAll("\\s", "");
        return evaluate(stripped, 0, stripped.length());
    }

    public double evaluatePostfix(String expression)
    {
        return evaluatePostfix(expression, 0, expression.length());
    }

    public double evaluatePostfix(String expression, int start, int end)
    {
        Stack<Double> values = new Stack<>();

        while ((start = skipWhitespace(expression, start, end)) < end) {
            if (Character.isDigit(expression.charAt(start))) {
                final Parsing parsing = parseDouble(expression, start, end);
                values.push(parsing.value);
                start = parsing.extract;
            }
            else {
                final String token = parseOperator(expression, start, end);
                final int operands = Math.min(2, Math.max(1, values.size()));
                final Operator operator = m_operators.getPreferenceOrAny(token, operands);
                final ArrayList<Double> arguments = new ArrayList<>();

                for (int op = 0; op < operator.getOperands(); ++op) {
                    arguments.add(0, values.pop());
                }
                values.push(operator.apply(arguments));
                start += token.length();
            }
        }

        return values.peek();
    }

    public static int skipWhitespace(String expression, int start, int end)
    {
        while (start < end && Character.isWhitespace(expression.charAt(start))) {
            ++start;
        }

        return start;
    }

    /**
     * Evaluate and interpret an expression with respect to starting and ending indices.
     * @param expression Expression and characters to evaluate.
     * @param start Starting index to evaluate from.
     * @param end Ending limiting index.
     * @return The value of the expression, interpreted by the calculator.
     */
    public double evaluate(String expression, int start, int end)
    {
        return evaluateGrouping(expression, start, end, new ArrayList<Character>()).value;
    }

    /**
     * Determine if this operator has precedence over the other.
     * @param recent
     * @param next
     * @return
     */
    public boolean hasPrecedence(Operator recent, Operator next)
    {
    	final int lp = recent.getPrecedence();
    	final int rp = next.getPrecedence();
    	//Lower precedence always has priority.
    	if (lp < rp) {
    		return true;
    	}
    	//Otherwise they must have equivalent precedence and left associativity.
    	//Right associativity with equal precedence has priority.
    	return (lp == rp) && (next.getAssociativity() == Operator.Associativity.LEFT_TO_RIGHT);
    }

    /**
     * Process the operator stack if the future
     * @param operations Stack containing all operations, ordered by high precedence.
     * @param values Stack containing all extracted values.
     * @param futureOp The next operator to account for.
     */
    public void processNextOperator(Stack<Operator> operations, Stack<Double> values, Operator futureOp)
    {
        while (!operations.empty() && hasPrecedence(operations.peek(), futureOp)) {
        	final Operator operator = operations.pop();
            final ArrayList<Double> arguments = new ArrayList<>();
            
            //Obtain all of the necessary arguments.
            for (int op = 0; op < operator.getOperands(); ++op) {
            	arguments.add(0, values.pop());
            }
            final Double computed = operator.apply(arguments);
            //Use computed value for further operations.
            values.push(computed);
        }
        //Future operation must be processed later.
        //It could have greater or smaller precedence than the following operations.
        operations.push(futureOp);
    }

    /**
     * Evaluate a grouping(formed via brackets/starting and ending indices).
     * @param expression The expression to be parsed.
     * @param start The starting index to begin evaluating from.
     * @param end The ending and limiting index.
     * @param closers List of limiting delimeters.
     * @return Parsing containing the expression's value, ending index, and closing character.
     */
    public Parsing evaluateGrouping(String expression, int start, int end, List<Character> closers)
    {
    	//Stacks are FILO/LIFO data structures.
    	//Processing infix notation via PEMDAS necessitates precedence.
    	//Precedence means first occurring operators will process last,
    	//iff they have lower precedence than sequential operators. 
        Stack<Operator> operations = new Stack<>();
        Stack<Double> values = new Stack<>();
        Term.Type previous = null;
        Character closer = null;
        boolean implicitMultiplication = false;
        
        while (start < end) {
            if (closers.contains(expression.charAt(start))) {
                closer = expression.charAt(start);
                break;
            }
            final Term term = extractTerm(expression, start, end);
            final Object backup = term.value;
            final Term.Type type = term.type;
            
            boolean implicit = term.type != Term.Type.OPERATOR && implicitMultiplication;
            if (implicit) {
                term.type = Term.Type.OPERATOR;
                term.value = "*";
            }
            else if (term.type == Term.Type.OPERAND) {
            	values.push((Double)term.value);
            	implicitMultiplication = true;
            }

            if (term.type == Term.Type.OPERATOR) {
            	final int operands = (previous != Term.Type.OPERAND ? 1 : 2);
                final Operator futureOp = m_operators.getPreferenceOrAny((String)term.value, operands);
                final int precedence = futureOp.getPrecedence();
                final int UNARY = 1;
                final int BINARY = 2;
                
                if (implicitMultiplication && futureOp.getOperands() == UNARY && futureOp.getAssociativity() == Operator.Associativity.RIGHT_TO_LEFT) {
                	processNextOperator(operations, values, m_operators.getPreferenceOrAny("*", 2));
                	implicitMultiplication = false;
                }
                processNextOperator(operations, values, futureOp);
                if (futureOp.getOperands() != UNARY) {
                	implicitMultiplication = false;
                }
                
            }
            if (implicit) {
                values.push((Double)backup);
                implicitMultiplication = true;
            }
            //Update term and index.
            previous = type;
            start = term.extract;
        }
        if (!closers.isEmpty() && closer == null) {
            throw new RuntimeException("Closing delimeter not found");
        }

        //Pop off remaining operations.
        while (!operations.empty()) {
            final Operator operator = operations.pop();
            final ArrayList<Double> arguments = new ArrayList<>();
            
            for (int op = 0; op < operator.getOperands(); ++op) {
            	arguments.add(0, values.pop());
            }
            final Double computed = operator.apply(arguments);
            values.push(computed);
        }

       final Double result = values.empty() ? null : values.peek();
        return new Parsing(result, start+1, closer);
    }

    /**
     * Extract the value of the corresponding term.
     * @param expression The expression to be parsed.
     * @param start The starting index to begin reading from.
     * @param end The ending and limiting index.
     * @return The term corresponding to an OPERAND or OPERATOR and extracted value.
     */
    public Term extractTerm(String expression, int start, int end) {
        final char ch = expression.charAt(start);

        if (Character.isDigit(ch) || ch == '.') {
            //Must be an LHS operand.
            final Parsing parsing = parseDouble(expression, start, end);
            return new Term(Term.Type.OPERAND, parsing.extract, parsing.value);
        }
        else if (isIdentifierChar(ch)) {
            //Must be an identifier mapped to a function or a constant.
            final Parsing parsing = parseIdentifier(expression, start, end);
            return new Term(Term.Type.OPERAND, parsing.extract, parsing.value);
        }
        else if (isOpeningBracket(ch)) {
            //Evaluate internal expression first.
            final Parsing parsing = evaluateGrouping(expression, start+1, end, Arrays.asList(getClosingBracket(ch)));
            return new Term(Term.Type.OPERAND, parsing.extract, parsing.value);
        }
        else {
            //Must be an operator.
            final String operator = parseOperator(expression, start, end);
            return new Term(Term.Type.OPERATOR, start+operator.length(), operator);
        }
    }

    /**
     * Extract a set of arguments from enclosing brackets.
     * @param expression The expression to be parsed.
     * @param start The starting index to begin parsing from.
     * @param end The ending and limiting index.
     * @param closingBracket Limiting closing bracket.
     * @return A multiparsing containing the ending index, optional closer, and extracted list of arguments.
     */
	public MultipleParse evaluateArguments(String expression, int start, int end, char closingBracket) {
        final MultipleParse parse = new MultipleParse();

        while (start < end) {
            final Parsing argument = evaluateGrouping(expression, start, end, Arrays.asList(',', closingBracket));
            if (!parse.arguments.isEmpty() || argument.value != null) {
                parse.arguments.add(argument.value);
            }
            start = argument.extract;
            if (argument.closer == closingBracket) {
                break;
            }
        }
        parse.extract = start;

        return parse;
    }

    /**
     * Parse and evaluate an identifier composed of alphanumeric values.
     * @param expression The expression to be parsed.
     * @param start The starting index to begin parsing from.
     * @param end The ending and limiting index.
     * @return Parsing containing ending index, numeric value, and closing character.
     */
    public Parsing parseIdentifier(String expression, int start, int end) {
        String identifier = null;
		//Extract the entire identifier.
		int idx = start;
        for (; idx <= end; ++idx) {
            if (idx == end || !isIdentifierChar(expression.charAt(idx))) {
                identifier = expression.substring(start, idx);
                break;
            }
        }
		
		//Expression and/or bounds are ill-formed.
        if (identifier == null) {
            throw new StringIndexOutOfBoundsException();
        }
		
		final char endingChar = idx < end ? expression.charAt(idx) : '\0';
		final boolean isFunction = isOpeningBracket(endingChar);
        if (isFunction && m_functions.contains(identifier)) {
            final MultipleParse multiParse = evaluateArguments(expression, idx+1, end, getClosingBracket(endingChar));
            final double value = m_functions.apply(identifier, multiParse.arguments);
			return new Parsing(value, multiParse.extract, '\0');
        }
		else if (m_constants.containsKey(identifier)) {
			final double value = m_constants.get(identifier);
			return new Parsing(value, idx, '\0');
		}
		else {
			//Identifier is not mapped to a function or constant.
			throw new RuntimeException("Unknown identifier");
		}
    }


    public Parsing parseDouble(String expression, int start, int end) {
        int idx = start;
        boolean decimal = false; //Can only exist one decimal point.
        
        for (; idx < end; ++idx) {
            final char ch = expression.charAt(idx);
            //Check for a decimal point, if one isn't yet found.
            if (ch == '.' && !decimal) {
                decimal = true; //Toggle decimal flag.
            }
            else if (ch == '.' || !Character.isDigit(ch)) {
                break; //Break if another decimal point is encountered or any non-digit character.
            }
        }
        //Convert string to double.
        final double value = Double.parseDouble(expression.substring(start, idx));
        return new Parsing(value, idx, '\0');
    }

    public String parseOperator(String expression, int start, int end) {
        int idx = start;
        String operator = null;
        for (; idx <= end; ++idx) {
            final String token = expression.substring(start, idx);

            if (m_operators.contains(token)) {
                operator = token;
            }

            if (idx != end) {
                final char next = expression.charAt(idx);

                if (isIdentifierChar(next) || Character.isWhitespace(next)) {
                    break;
                }
            }
        }
        if (operator == null) {
            throw new RuntimeException("No operator found.");
        }

        return operator;
    }
    
	public static boolean isIdentifierChar(char ch) {
		return Character.isJavaIdentifierPart(ch) || ch == '_';
	}
	
    public static boolean isOpeningBracket(char ch) {
        return "([{".indexOf(ch) != -1;
    }

    public static boolean isClosingBracket(char ch) {
        return ")]}".indexOf(ch) != -1;
    }

    public static char getClosingBracket(char opener) {
        return ")]}".charAt("([{".indexOf(opener));
    }
}
