package calculator;

import java.util.Map;
import java.util.HashMap;

public class Constants extends HashMap<String, Double> {
	public static final Constants JCONSTANTS;
	static {
		JCONSTANTS = new Constants();
		JCONSTANTS.put("π", 3.14159265358979323846);
		JCONSTANTS.put("pi", 3.14159265358979323846);
		JCONSTANTS.put("e", 2.71828182845904523536);
		JCONSTANTS.put("φ", 1.61803398874989484820);
		JCONSTANTS.put("phi", 1.61803398874989484820);
	}
}
