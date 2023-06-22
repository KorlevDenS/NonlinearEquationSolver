package org.korolev.dens;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class SimpleIterationSystem extends NonlinearSolver {

    private final ArrayList<Expression> equations = new ArrayList<>();
    private final int varsAmount;
    public SimpleIterationSystem(ArrayList<String> equationSystem, ArrayList<Double> initApproximations, double accuracy)
            throws WrongInputException {
        super(equationSystem, initApproximations, accuracy);
        this.varsAmount = equationSystem.size();

    }

    @Override
    public void solve() {
        if (!checkConvergence()) {
            messages.add("Метод простой итерации не сходится!!!");
            //return;
        } else messages.add("Метод простой итерации сходится");
        for (int i = 0; i < equationSystem.size(); i++) {
            ExpressionBuilder builder = new ExpressionBuilder(equationSystem.get(i));
            for (int j = 1; j <= equationSystem.size(); j++) {
                builder.variable("x" + j);
            }
            equations.add(builder.build());
        }

        try {
            simpleIteration();
        } catch (NumberFormatException e) {
            System.out.println("Произошло расхождение процесса");
            this.messages.clear();
        }

    }


    private void simpleIteration() {
        while (true) {
            iterationsNumber += 1;

            for (int i = 0; i < equations.size(); i++) {
                for (int j = 0; j < equations.size(); j++) {
                    equations.get(i).setVariable("x" + (j + 1), initApproximations.get(j));
                    //System.out.println(initApproximations.get(j) + " x" + (j + 1));/////////////////
                }
                nextApproximations.add(BigDecimal.valueOf(equations.get(i).evaluate())
                        .setScale(BigDecimal.valueOf(accuracy).scale(), RoundingMode.HALF_UP).doubleValue());
                //System.out.println(equations.get(i).evaluate()); //////////////////////
            }

            for (int i = 0; i < equations.size(); i++) {
                if (Math.abs(nextApproximations.get(i) - initApproximations.get(i)) <= accuracy) return;
            }

            initApproximations = new ArrayList<>(nextApproximations);
            nextApproximations.clear();
        }
    }

    private boolean checkConvergence() {
        for (int i = 0; i < varsAmount; i++) {
            double sum = 0;
            for (int j = 1; j <= varsAmount; j++) {
                sum += calcPartialDerivative(equationSystem.get(i), initApproximations, j);
            }
            if (sum >= 1) return false;
        }
        return true;
    }

}
