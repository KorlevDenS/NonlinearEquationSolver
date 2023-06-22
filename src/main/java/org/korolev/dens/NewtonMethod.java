package org.korolev.dens;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NewtonMethod extends NonlinearSolver {

    public NewtonMethod(String equation, double borderA, double borderB, double accuracy) throws WrongInputException {
        super(equation, borderA, borderB, accuracy);
    }

    @Override
    public void solve() {
        if (this.rootsNumber == RootsNumber.NO_ROOTS) {
            messages.add("Нету корней на заданном отрезке");
            return;
        }
        chooseInitialApproximation();
        double x0;
        double x1 = initialApproximation;
        do {
            x0 = x1;
            double Fx = equation.setVariable("x", x0).evaluate();
            double F1x = calcDerivativeVal(x0);
            x1 = x0 - (Fx / F1x);
            iterationsNumber += 1;
            //System.out.println(x0 + " " + Fx + " " + F1x + " " + x1);
        } while ((BigDecimal.valueOf(x1).subtract(BigDecimal.valueOf(x0))).abs().doubleValue() > accuracy);

        resultX = BigDecimal.valueOf(x1)
                .setScale(BigDecimal.valueOf(accuracy).scale(), RoundingMode.HALF_UP).doubleValue();

    }


    protected double calc2DerivativeVal(double x) {
        double h = 0.00001;
        double FxPlusH = equation.setVariable("x", x + h).evaluate();
        double FxMinusH = equation.setVariable("x", x - h).evaluate();
        double Fx = equation.setVariable("x", x).evaluate();
        return (FxPlusH - 2 * Fx + FxMinusH) / (h * h);
    }

    private void chooseInitialApproximation() {
        double Fa = equation.setVariable("x", borderA).evaluate();
        double Fb = equation.setVariable("x", borderB).evaluate();
        double F2a = calc2DerivativeVal(borderA);
        double F2b = calc2DerivativeVal(borderB);
        if (Fa * F2a > 0) {
            this.initialApproximation = borderA;
        } else if (Fb * F2b > 0) {
            this.initialApproximation = borderB;
        }
    }
}
