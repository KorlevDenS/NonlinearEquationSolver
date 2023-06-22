package org.korolev.dens;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class HalfDivisionMethod extends NonlinearSolver {

    public HalfDivisionMethod(String equation, double borderA, double borderB, double accuracy) throws WrongInputException {
        super(equation, borderA, borderB, accuracy);
    }

    @Override
    public void solve() {
        if (this.rootsNumber == RootsNumber.NO_ROOTS) {
            messages.add("Нету корней на заданном отрезке");
            return;
        }

        do {
            resultX = (borderA + borderB) / 2;
            /*System.out.println(borderA + " " + borderB + " " + BigDecimal.valueOf(resultX).setScale(3, RoundingMode.HALF_UP) + " "
                    + BigDecimal.valueOf(equation.setVariable("x", borderA).evaluate()).setScale(3, RoundingMode.HALF_UP) + " "
                    + BigDecimal.valueOf(equation.setVariable("x", borderB).evaluate()).setScale(3, RoundingMode.HALF_UP) + " "
                    + BigDecimal.valueOf(equation.setVariable("x", resultX).evaluate()).setScale(3, RoundingMode.HALF_UP) + " "
                    + BigDecimal.valueOf(Math.abs(borderA - borderB)).setScale(3, RoundingMode.HALF_UP)
            );*/
            double Fa = equation.setVariable("x", borderA).evaluate();
            double Fx = equation.setVariable("x", resultX).evaluate();
            if (Fa * Fx > 0) {
                borderA = resultX;
            } else {
                borderB = resultX;
            }
            iterationsNumber += 1;

        } while (Math.abs(BigDecimal.valueOf(borderA).subtract(BigDecimal.valueOf(borderB)).doubleValue()) > accuracy);

        iterationsNumber += 1;
        resultX = (BigDecimal.valueOf(borderA).add(BigDecimal.valueOf(borderB))).divide(BigDecimal.valueOf(2),
                BigDecimal.valueOf(accuracy).scale(), RoundingMode.HALF_UP).doubleValue();
        /*System.out.println(borderA + " " + borderB + " " + BigDecimal.valueOf(resultX).setScale(3, RoundingMode.HALF_UP) + " "
                + BigDecimal.valueOf(equation.setVariable("x", borderA).evaluate()).setScale(3, RoundingMode.HALF_UP) + " "
                + BigDecimal.valueOf(equation.setVariable("x", borderB).evaluate()).setScale(3, RoundingMode.HALF_UP) + " "
                + BigDecimal.valueOf(equation.setVariable("x", resultX).evaluate()).setScale(3, RoundingMode.HALF_UP) + " "
                + BigDecimal.valueOf(Math.abs(borderA - borderB)).setScale(3, RoundingMode.HALF_UP)
        );*/
    }
}
