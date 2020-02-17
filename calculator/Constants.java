package calculator;

import java.util.Map;
import java.util.HashMap;

public class Constants extends HashMap<String, Double> {
	public static final Constants JCONSTANTS;

	static {
		JCONSTANTS = new Constants();
		JCONSTANTS.put("π", Math.PI);
		JCONSTANTS.put("pi", Math.PI);
		JCONSTANTS.put("e", Math.E);
		JCONSTANTS.put("φ", 1.61803398874989484820);
		JCONSTANTS.put("phi", 1.61803398874989484820);
	}
}
