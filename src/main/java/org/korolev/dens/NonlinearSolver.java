package org.korolev.dens;

import lombok.Getter;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class NonlinearSolver implements Solvable {

    protected double borderA;
    protected double borderB;
    protected ArrayList<Double> initApproximations;
    protected ArrayList<Double> nextApproximations = new ArrayList<>();
    protected double initialApproximation;
    protected ArrayList<String> messages = new ArrayList<>();
    protected Expression equation;
    protected ArrayList<String> equationSystem;
    protected ArrayList<Double> resultsXi;
    protected double accuracy;
    protected RootsNumber rootsNumber;
    protected int iterationsNumber;
    protected double resultX;

    public NonlinearSolver(String equation, double borderA, double borderB, double accuracy) throws WrongInputException {
        try {
            this.equation = new ExpressionBuilder(equation).variable("x").build();
        } catch (Exception e) {
            throw new WrongInputException("Выражение содержит ошибки.");
        }
        this.borderA = borderA;
        this.borderB = borderB;
        this.accuracy = accuracy;
        validateInputData();
        checkRootsNumber();
    }

    public NonlinearSolver(ArrayList<String> equationSystem, ArrayList<Double> initApproximations, double accuracy)
            throws WrongInputException {
        this.equationSystem = new ArrayList<>(equationSystem);
        this.initApproximations = new ArrayList<>(initApproximations);
        this.accuracy = accuracy;
        //validateInputData();
    }

    protected void validateInputData() throws WrongInputException {
        if (borderB <= borderA) throw new WrongInputException("Границы интервала заданы неверно");
        if (accuracy >= Math.abs(BigDecimal.valueOf(borderB).subtract(BigDecimal.valueOf(borderA)).doubleValue()))
            throw new WrongInputException("Значение точности не может быть больше длины заданного интервала.");
    }
    protected void checkRootsNumber() {
        double Fa = equation.setVariable("x", borderA).evaluate();
        double Fb = equation.setVariable("x", borderB).evaluate();

        if (Fa * Fb >= 0) {
            this.rootsNumber = RootsNumber.NO_ROOTS;
        } else {
            double x = borderA;
            boolean isMinus = calcDerivativeVal(x) < 0;
            while (x < borderB) {
                x = BigDecimal.valueOf(x).add(BigDecimal.valueOf(accuracy)).doubleValue();
                double der = calcDerivativeVal(x);
                //System.out.println(der + " " + x);
                if (der < 0 != isMinus) {
                    this.rootsNumber = RootsNumber.MANY_ROOTS;
                    return;
                }
            }
            this.rootsNumber = RootsNumber.ONLY_ROOT;
        }
    }

    protected double calcDerivativeVal(double x) {
        double h = 0.0000000001;
        double FxPlusH = equation.setVariable("x", x + h).evaluate();
        double Fx = equation.setVariable("x", x).evaluate();
        return (BigDecimal.valueOf(FxPlusH).subtract(BigDecimal.valueOf(Fx)))
                .divide(BigDecimal.valueOf(h), BigDecimal.valueOf(h).scale(), RoundingMode.HALF_UP).doubleValue();
    }

    protected double calcPartialDerivative(String equation, ArrayList<Double> varsValues, int varNumber) {
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

    public void displayFunctions(int width, int height) {
        XYChart chart = new XYChartBuilder().width(width).height(height)
                .theme(Styler.ChartTheme.Matlab).title("NonLinear Expressions").build();
        drawFunctionOnChart(new ExpressionBuilder("0*x")
                .variable("x").build(), borderA, borderB, chart, "Ox");
        if (equationSystem == null) {
            drawFunctionOnChart(equation, borderA, borderB, chart, "f");
        }/* else {
            int n = 0;
            for (Expression e : equationSystem) {
                n++;
                drawFunctionOnChart(e, borderA, borderB, chart, "f" + n);
            }
        }*/
        new SwingWrapper<>(chart).displayChart();
    }

    private void drawFunctionOnChart(Expression exp, double x1, double x2, XYChart chart, String name) {
        List<Double> xData = new ArrayList<>();
        List<Double> yData = new ArrayList<>();
        double xMin = x1;
        double len = x2 - xMin;
        double part = len / 100;
        while (xMin < x2) {
            xData.add(xMin);
            yData.add(exp.setVariable("x", xMin).evaluate());
            xMin = BigDecimal.valueOf(xMin).add(BigDecimal.valueOf(part)).doubleValue();
        }
        xData.add(xMin);
        yData.add(exp.setVariable("x", xMin).evaluate());
        XYSeries series = chart.addSeries(name, xData, yData);
        series.setMarker(SeriesMarkers.NONE);
    }
}
