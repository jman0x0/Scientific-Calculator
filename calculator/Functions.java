package calculator;

import java.util.Set;
import java.util.function.Function;
import java.util.HashMap;
import java.util.Map;
import java.lang.Math;
import java.util.ArrayList;

/**
 * Class used to map identifiers composed of strings to functional objects.
 */
public class Functions {
	private Map<String, ArrayList<MathFunction>> m_functions;
	
	public Functions() {
		m_functions = new HashMap<>();
	}

	public int size() {
		return m_functions.size();
	}
	
	public boolean contains(String name) {
		return m_functions.containsKey(name);
	}

	/**
	 * Check if an overloaded function exists.
	 * @param identifier Name of the overloaded function.
	 * @param arguments Number of arguments that overload accepts.
	 * @return True if a function with a matching name and overload is found, else false.
	 */
	public boolean contains(String identifier, int arguments) {
		if (contains(identifier)) {
			final ArrayList<MathFunction> overloads = m_functions.get(identifier);
		
			for (MathFunction function : overloads) {
				if (function.getArguments() == arguments) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Remove all overloads.
	 * @param identifier Name pertaining to the overloads.
	 * @return True if the overloads were found and removed, else false.
	 */
	public boolean remove(String identifier) {
		return m_functions.remove(identifier) != null;
	}


	public MathFunction remove(MathFunction function) {
		if (function != null && contains(function.getIdentifier())) {
			final ArrayList<MathFunction> overloads = getOverloads(function.getIdentifier());

			if (overloads.contains(function)) {
				overloads.remove(function);
				return function;
			}
		}
		return null;
	}

	/**
	 * Remove an overloaded function.
	 * @param identifier The name of the overloaded function.
	 * @param arguments Number of arguments for that overload.
	 * @return reference to the removed function if the function was found and removed, else null.
	 */
	public MathFunction remove(String identifier, int arguments) {
		if (contains(identifier)) {
			final ArrayList<MathFunction> overloads = m_functions.get(identifier);
		
			for (int idx = 0; idx != overloads.size(); ++idx) {
				if (overloads.get(idx).getArguments() == arguments) {
					return overloads.remove(idx);
				}
			}
		}
		return null;
	}

	/**
	 * Get all of the overloads from a given identifier.
	 * @param identifier Shared name between all overloaded functions.
	 * @return A list containing all the overloads.
	 */
	private ArrayList<MathFunction> getOverloads(String identifier) {
		if (!contains(identifier)) {
			m_functions.put(identifier, new ArrayList<>());
		}
		return m_functions.get(identifier);
	}

	/**
	 * Map a function to an identifier.
	 * @param identifier Name used to map the function to.
	 * @param arguments Number of arguments accepted by this function.
	 * @param function The actual functional object.
	 */
	public void emplace(String identifier, int arguments, Function<MathFunction.ParameterPack, Number> function) {
		final ArrayList<MathFunction> overloads = getOverloads(identifier);

		for (MathFunction fn : overloads) {
			if (fn.getArguments() == arguments) {
				throw new RuntimeException("Overlapping function signatures");
			}
		}
		overloads.add(new JavaFunction(identifier, arguments, function));
	}

	/**
	 * Invoke a mapped math function.
	 * @param identifier The name of the function to call.
	 * @param arguments The numeric arguments to pass to the function.
	 * @return The result of the invoked math function.
	 */
	public Double apply(String identifier, MathFunction.ParameterPack arguments) {
		final ArrayList<MathFunction> overloads = m_functions.get(identifier);

		for (MathFunction fn : overloads) {
			if (fn.getArguments() == arguments.values.size()) {
				return fn.apply(arguments).doubleValue();
			}
		}
		throw new RuntimeException("No overload of " + identifier + " accepts " + arguments.values.size() + " parameters");
	}
	
	public void clear() {
		m_functions.clear();
	}
	
	public boolean isEmpty() {
		return m_functions.isEmpty();
	}

	public void loadFunctionFromString(String definition) {
		final UserFunction fn = new UserFunction(definition);
		getOverloads(fn.getIdentifier()).add(fn);
	}

	public MathFunction getFunction(String identifier, int arguments) {
		if (contains(identifier)) {
			final ArrayList<MathFunction> overloads = getOverloads(identifier);

			for (MathFunction fn : overloads) {
				if (fn.getArguments() == arguments) {
					return fn;
				}
			}
		}
		return null;
	}

	public MathFunction getFunction(String identifier) {
		if (contains(identifier)) {
			return m_functions.get(identifier).get(0);
		}
		return null;
	}

	public Set<Map.Entry<String, ArrayList<MathFunction>>> entrySet() {
		return m_functions.entrySet();
	}

	/**
	 * Default set of functions that are available within the java library plus a few more.
	 */
	public static final Functions JMATH;
	static {
		JMATH = new Functions();
		JMATH.emplace("abs", 1, (args) -> {
			return Math.abs(args.values.get(0).doubleValue());
		});
		JMATH.emplace("sqrt", 1, (args) -> {
			return Math.sqrt(args.values.get(0).doubleValue());
		});
		JMATH.emplace("min", 2, (args) -> {
			return Math.min(args.values.get(0).doubleValue(), args.values.get(1).doubleValue());
		});
		JMATH.emplace("max", 2, (args) -> {
			return Math.max(args.values.get(0).doubleValue(), args.values.get(1).doubleValue());
		});
		JMATH.emplace("floor", 1, (args) -> {
			return Math.floor(args.values.get(0).doubleValue());
		});
		JMATH.emplace("ceil", 1, (args) -> {
			return Math.ceil(args.values.get(0).doubleValue());
		});
		JMATH.emplace("round", 1, (args) -> {
			return (double)Math.round(args.values.get(0).doubleValue());
		});
		JMATH.emplace("sin", 1, (args) -> {
			return Math.sin(args.angle.convertValue(args.values.get(0).doubleValue()));
		});
		JMATH.emplace("sinh", 1, (args) -> {
			return Math.sinh(args.values.get(0).doubleValue());
		});
		JMATH.emplace("asin", 1, (args) -> {
			return Math.asin(args.values.get(0).doubleValue());
		});
		JMATH.emplace("asinh", 1, (args) -> {
			final double x = args.values.get(0).doubleValue();
			return Math.log(x + Math.sqrt(1.0 + x*x));
		});
		JMATH.emplace("cos", 1, (args) -> {
			return Math.cos(args.angle.convertValue(args.values.get(0).doubleValue()));
		});
		JMATH.emplace("cosh", 1, (args) -> {
			return Math.cosh(args.values.get(0).doubleValue());
		});
		JMATH.emplace("acos", 1, (args) -> {
			return Math.acos(args.values.get(0).doubleValue());
		});
		JMATH.emplace("acosh", 1, (args) -> {
			final double x = args.values.get(0).doubleValue();
			return Math.log(x + Math.sqrt(x+1)*Math.sqrt(x-1));
		});
		JMATH.emplace("tan", 1, (args) -> {
			return Math.tan(args.angle.convertValue(args.values.get(0).doubleValue()));
		});
		JMATH.emplace("tanh", 1, (args) -> {
			return Math.tanh(args.values.get(0).doubleValue());
		});
		JMATH.emplace("atan", 1, (args) -> {
			return Math.atan(args.values.get(0).doubleValue());
		});
		JMATH.emplace("atanh", 1, (args) -> {
			final double x = args.values.get(0).doubleValue();
			return 0.5 * (Math.log(1+x) - Math.log(1-x));
		});
		JMATH.emplace("log", 1, (args) -> {
			return Math.log10(args.values.get(0).doubleValue());
		});
		JMATH.emplace("exp", 1, (args) -> {
			return Math.exp(args.values.get(0).doubleValue());
		});
		JMATH.emplace("ln", 1, (args) -> {
			return Math.log(args.values.get(0).doubleValue());
		});
		JMATH.emplace("log10", 1, (args) -> {
			return Math.log10(args.values.get(0).doubleValue());
		});
		JMATH.emplace("rand", 0, (args) -> {
			return Math.random();
		});
		JMATH.emplace("rand", 2, (args) -> {
			final double lower = args.values.get(0).doubleValue();
			final double upper = args.values.get(1).doubleValue();
			return lower + Math.random() * (upper - lower);
		});
		JMATH.loadFunctionFromString("nthroot(value, n) = value ^ (1/n)");
		JMATH.loadFunctionFromString("randInt(l, u) = floor(rand(floor(l), floor(u)+1))");
		JMATH.loadFunctionFromString("pick(cnd, lhs, rhs) = lhs(cnd != 0) + rhs(cnd == 0)");
		JMATH.loadFunctionFromString("choose(n, c) = n! / (c!(n-c)!)");
		JMATH.loadFunctionFromString("degrees(rads) = rads * (180/pi)");
		JMATH.loadFunctionFromString("radians(degs) = degs * (pi/180)");

	}
}
