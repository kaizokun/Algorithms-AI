package exercice;

import org.junit.Test;
import util.CombinatoryAnalysisUtil;

import static util.CombinatoryAnalysisUtil.factorial;
import static util.CombinatoryAnalysisUtil.nThHarmonicNumber;

public class AnalyseProbabilisteRandomise {

    public double n = 50;

    @Test
    /**
     * ma solution
     *
     * [1][2][3][4][5][6]
     *
     * exemple embauche 3 en premier les possibilités sont :
     *
     * [3][6][][][][]
     * [3][][6][][][]
     * [3][][][6][][]
     *
     * */
    public void hireTwiceTestMethod1() {

        double total = 0;

        for (int i = 1; i <= n - 1; i++) {

            for (int b = 0; b <= i - 1; b++) {

                total += (factorial(i - 1) / factorial(i - 1 - b)) * factorial(n - b - 2);
            }
        }

        double rs = total / factorial(n);

        System.out.println(rs);
    }

    @Test
    /**
     * formule manuel solution officiel
     * */
    public void hireTwiceTestMethod2() {

        double rs = nThHarmonicNumber(n - 1) / n;

        System.out.println(rs);
    }

    @Test
    /**
     * formule solution Michelle Bodnar, Andrew Lohr
     * aparemment fausse
     */
    public void hireTwiceTestMethod3() {

        double rs = (Math.pow(2, n) - n - 1) / factorial(n);

        System.out.println((Math.pow(2, n) - n - 1)+"/"+factorial(n));

        System.out.println(rs);
    }


}
