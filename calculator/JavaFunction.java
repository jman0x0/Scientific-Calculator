package calculator;

import java.util.function.Function;

public class JavaFunction extends MathFunction {
    private Function<ParameterPack, Number> function;

    public JavaFunction(String identifier, int arguments, Function<ParameterPack, Number> function) {
        super(identifier, arguments);
        this.function = function;
    }

    @Override
    public Number apply(ParameterPack pack) {
        return function.apply(pack);
    }
}