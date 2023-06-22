package org.korolev.dens;

import lombok.Getter;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
public class SimpleIterationMethod extends NonlinearSolver {

    private String equationString;
    private Expression iterateEquation;
    private double lambda;

    public SimpleIterationMethod(String equation, double borderA, double borderB, double accuracy)
            throws WrongInputException {
        super(equation, borderA, borderB, accuracy);
        this.equationString = equation;
    }

    @Override
    public void solve() {
        if (this.rootsNumber == RootsNumber.NO_ROOTS) {
            messages.add("Нету корней на заданном отрезке");
            return;
        }
        formIterateEquation();
        if (!checkConvergence()) {
            messages.add("Метод простой итерации может не сходиться на отрезке [" + borderA + ";" + borderB + "]");
            //return;
        }
        initialApproximation = borderA;
        simpleIteration();
    }

    private void simpleIteration() {
        double x0;
        double x1 = initialApproximation;
        do {
            x0 = x1;
            x1 = iterateEquation.setVariable("x", x0).evaluate();
            System.out.println(x0 + "  " + x1);
            iterationsNumber += 1;
            //boolean d = Math.abs(this.equation.setVariable("x", x1).evaluate()) > accuracy;
        } while (Math.abs(this.equation.setVariable("x", x1).evaluate()) > accuracy);

        //igDecimal.valueOf(x1).subtract(BigDecimal.valueOf(x0)).doubleValue() > accuracy

        resultX = BigDecimal.valueOf(x1)
                .setScale(BigDecimal.valueOf(accuracy).scale(), RoundingMode.HALF_UP).doubleValue();
    }

    private void formIterateEquation() {
        double F1a = Math.abs(calcDerivativeVal(borderA));
        double F1b = Math.abs(calcDerivativeVal(borderB));
        this.lambda = (-1) / Math.max(F1a, F1b);
        this.equationString = "x + " + lambda + " * (" + equationString + ")";
        //System.out.println("fi(x) = " + this.equationString);
        this.iterateEquation = new ExpressionBuilder(equationString).variable("x").build();
    }

    private boolean checkConvergence() {
        double x = borderA;
        while (x <= borderB) {
            x = BigDecimal.valueOf(x).add(BigDecimal.valueOf(accuracy)).doubleValue();
            double der = Math.abs(1 + lambda * calcDerivativeVal(x));
            //System.out.println(der + " " + x);
            if (der >= 1) return false;
        }
        return true;
    }
}
