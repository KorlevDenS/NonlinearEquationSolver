package org.korolev.dens;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;

public class MainT {

    public static void main(String[] args) {
        ArrayList<Double> start = new ArrayList<>(Arrays.asList(1d, 1d));
        System.out.println(calcPartialDerivative("0.3 - 0.1x1^2 - 0.2x2^2", start, 1));
        System.out.println(calcPartialDerivative("0.3 - 0.1x1^2 - 0.2x2^2", start, 2));

    }


    public static double calcPartialDerivative(String equation, ArrayList<Double> varsValues, int varNumber) {
        ExpressionBuilder builder = new ExpressionBuilder(equation);
        for (int i = 1; i <= varsValues.size(); i++) {
            builder.variable("x" + i);
        }
        Expression exp = builder.build();
        for (int i = 0; i < varsValues.size(); i++) {
            exp.setVariable("x" + (i + 1), varsValues.get(i));
        }
        double h = 0.0000000001;
        double Fx = exp.evaluate();
        double FxPlusH = exp.setVariable("x" + varNumber, varsValues.get(varNumber - 1) + h).evaluate();
        return (BigDecimal.valueOf(FxPlusH).subtract(BigDecimal.valueOf(Fx)))
                .divide(BigDecimal.valueOf(h), BigDecimal.valueOf(h).scale(), RoundingMode.HALF_UP).doubleValue();
    }
}
