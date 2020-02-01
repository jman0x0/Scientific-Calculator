package calculator;

import java.util.function.*;
import java.util.HashMap;
import java.util.Map;
import java.lang.Math;
import java.util.ArrayList;

public class Functions {
	public static class Functor {
		public Function<ArrayList<Double>, Double> function;
		public int arguments;
		
		public Functor(Function<ArrayList<Double>, Double> fn, int args) {
			this.function = fn;
			this.arguments = args;
		}
	}
	
	private Map<String, ArrayList<Functor>> m_functions;
	
	public Functions() {
		m_functions = new HashMap<String, ArrayList<Functor>>();
	}
	
	public int size() {
		return m_functions.size();
	}
	
	public boolean contains(String name) {
		return m_functions.containsKey(name);
	}
	
	public boolean contains(String name, int args) {
		if (contains(name)) {
			final ArrayList<Functor> overloads = m_functions.get(name);
		
			for (Functor function : overloads) {
				if (function.arguments == args) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean remove(String name) {
		return m_functions.remove(name) != null;
	}
	
	public boolean remove(String name, int args) {
		if (contains(name)) {
			final ArrayList<Functor> overloads = m_functions.get(name);
		
			for (int idx = 0; idx != overloads.size(); ++idx) {
				if (overloads.get(idx).arguments == args) {
					return overloads.remove(idx) != null;
				}
			}
		}
		return false;
	}

	private ArrayList<Functor> getOverloads(String identifier) {
		if (!contains(identifier)) {
			m_functions.put(identifier, new ArrayList<>());
		}
		return m_functions.get(identifier);
	}

	public void emplace(String identifier, int arguments, Function<ArrayList<Double>, Double> function) {
		final ArrayList<Functor> overloads = getOverloads(identifier);

		for (Functor fn : overloads) {
			if (fn.arguments == arguments) {
				throw new RuntimeException("Overlapping function signatures");
			}
		}
		overloads.add(new Functor(function, arguments));
	}

	public Double apply(String identifier, ArrayList<Double> arguments) {
		final ArrayList<Functor> overloads = m_functions.get(identifier);

		for (Functor fn : overloads) {
			if (fn.arguments == arguments.size()) {
				return fn.function.apply(arguments);
			}
		}
		throw new RuntimeException("No overload of " + identifier + " accepts " + arguments.size() + " parameters");
	}
	
	public void clear() {
		m_functions.clear();
	}
	
	public boolean isEmpty() {
		return m_functions.isEmpty();
	}
	
	public static final Functions JMATH;
	static {
		JMATH = new Functions();
		JMATH.emplace("abs", 1, (args) -> {
			return Math.abs(args.get(0));
		});
		JMATH.emplace("sqrt", 1, (args) -> {
			return Math.sqrt(args.get(0));
		});
		JMATH.emplace("min", 2, (args) -> {
			return Math.min(args.get(0), args.get(1));
		});
		JMATH.emplace("max", 2, (args) -> {
			return Math.max(args.get(0), args.get(1));
		});
		JMATH.emplace("floor", 1, (args) -> {
			return Math.floor(args.get(0));
		});
		JMATH.emplace("ceil", 1, (args) -> {
			return Math.ceil(args.get(0));
		});
		JMATH.emplace("round", 1, (args) -> {
			return (double)Math.round(args.get(0));
		});
		JMATH.emplace("sin", 1, (args) -> {
			return Math.sin(args.get(0));
		});
		JMATH.emplace("sinh", 1, (args) -> {
			return Math.sinh(args.get(0));
		});
		JMATH.emplace("asin", 1, (args) -> {
			return Math.asin(args.get(0));
		});
		JMATH.emplace("asinh", 1, (args) -> {
			final double x = args.get(0);
			return Math.log(x + Math.sqrt(1.0 + x*x));
		});
		JMATH.emplace("cos", 1, (args) -> {
			return Math.cos(args.get(0));
		});
		JMATH.emplace("cosh", 1, (args) -> {
			return Math.cosh(args.get(0));
		});
		JMATH.emplace("acos", 1, (args) -> {
			return Math.acos(args.get(0));
		});
		JMATH.emplace("acosh", 1, (args) -> {
			final double x = args.get(0);
			return Math.log(x + Math.sqrt(x+1)*Math.sqrt(x-1));
		});
		JMATH.emplace("tan", 1, (args) -> {
			return Math.tan(args.get(0));
		});
		JMATH.emplace("tanh", 1, (args) -> {
			return Math.tanh(args.get(0));
		});
		JMATH.emplace("atan", 1, (args) -> {
			return Math.atan(args.get(0));
		});
		JMATH.emplace("atanh", 1, (args) -> {
			final double x = args.get(0);
			return 0.5 * (Math.log(1+x) - Math.log(1-x));
		});
		JMATH.emplace("log", 1, (args) -> {
			return Math.log(args.get(0));
		});
		JMATH.emplace("log10", 1, (args) -> {
			return Math.log10(args.get(0));
		});
		JMATH.emplace("rand", 0, (args) -> {
			return Math.random();
		});
		JMATH.emplace("rand", 2, (args) -> {
			final double lower = args.get(0);
			final double upper = args.get(1);
			return lower + Math.random() * (upper - lower);
		});
	}
}
