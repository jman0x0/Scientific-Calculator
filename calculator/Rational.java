package calculator;

public class Rational {
    private long numerator;
    private long denominator;

    public Rational(double value) {
        setValue(value);
    }

    public Rational(long value) {
        numerator = value;
        denominator = 1;
    }

    public void setValue(double value) {
        continuedFractions(Math.abs(value));

        if (value < 0) {
            numerator = -numerator;
        }
    }

    public static Rational valueOf(double value) {
        return new Rational(value);
    }

    double toReal() {
        return (double)numerator/denominator;
    }

    long getNumerator() {
        return numerator;
    }

    long getDenominator() {
        return denominator;
    }

    @Override
    public String toString() {
        if (denominator != 1) {
            return numerator + "/" + denominator;
        }
        else {
            return String.valueOf(numerator);
        }
    }


    public void continuedFractions(double x) {
        //Original implementation developed by Joni on this page
        //https://jonisalonen.com/2012/converting-decimal-numbers-to-ratios/
        final double tolerance = 1e-15;
        double h1=1; double h2=0;
        double k1=0; double k2=1;
        double b = x;
        do {
            double a = Math.floor(b);
            double aux = h1; h1 = a*h1+h2; h2 = aux;
            aux = k1; k1 = a*k1+k2; k2 = aux;
            b = 1/(b-a);
        } while (Math.abs(x-h1/k1) > x*tolerance);
        numerator = Math.round(h1);
        denominator = Math.round(k1);
    }
}
