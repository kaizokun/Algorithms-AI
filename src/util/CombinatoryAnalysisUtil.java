package util;

public class CombinatoryAnalysisUtil {

    public static double factorial(double n) {

        if (n <= 1) {
            return 1;
        }

        return n * factorial(n - 1);
    }

    public static double nThHarmonicNumber(double n){

        double total = 0;

        while( n >= 1 ){

            total += 1.0 / n;

            n --;
        }

        return total;
    }

}
