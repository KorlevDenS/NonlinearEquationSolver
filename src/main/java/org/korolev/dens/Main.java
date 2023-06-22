package org.korolev.dens;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws WrongInputException {
        Scanner sc = new Scanner(System.in);

        System.out.println("Введите номер нужного метода:");
        System.out.println("1. Половинное деление");
        System.out.println("2. Метод Ньютона");
        System.out.println("3. Метод Простой итерации для одного уравнения");
        System.out.println("4. Метод Простой итерации для системы двух* уравнений");

        int methodId = sc.nextInt();

        switch (methodId) {
            case 1 -> {
                System.out.println("Введите ваше уравнение:");
                String exp = new Scanner(System.in).nextLine();
                System.out.println("Введите левый интервал:");
                double borderA = new Scanner(System.in).nextDouble();
                System.out.println("Введите правый интервал:");
                double borderB = new Scanner(System.in).nextDouble();
                System.out.println("Введите точность:");
                double accuracy = new Scanner(System.in).nextDouble();
                HalfDivisionMethod method = new HalfDivisionMethod(exp, borderA, borderB, accuracy);
                method.displayFunctions(1000, 600);
                method.solve();
                printResult(method, "Половинное деление");
            }
            case 2 -> {
                System.out.println("Введите ваше уравнение:");
                String exp = new Scanner(System.in).nextLine();
                System.out.println("Введите левый интервал:");
                double borderA = new Scanner(System.in).nextDouble();
                System.out.println("Введите правый интервал:");
                double borderB = new Scanner(System.in).nextDouble();
                System.out.println("Введите точность:");
                double accuracy = new Scanner(System.in).nextDouble();
                NewtonMethod method = new NewtonMethod(exp, borderA, borderB, accuracy);
                method.displayFunctions(1000, 600);
                method.solve();
                printResult(method, "Ньютон");
            }
            case 3 -> {
                System.out.println("Введите ваше уравнение:");
                String exp = new Scanner(System.in).nextLine();
                System.out.println("Введите левый интервал:");
                double borderA = new Scanner(System.in).nextDouble();
                System.out.println("Введите правый интервал:");
                double borderB = new Scanner(System.in).nextDouble();
                System.out.println("Введите точность:");
                double accuracy = new Scanner(System.in).nextDouble();
                SimpleIterationMethod method = new SimpleIterationMethod(exp, borderA, borderB, accuracy);
                method.displayFunctions(1000, 600);
                method.solve();
                printResult(method, "Простая итерация");
            }
            case 4 -> {
                ArrayList<String> sys = new ArrayList<>();
                System.out.println("Введите первое уравнение:");
                sys.add(new Scanner(System.in).nextLine());
                System.out.println("Введите второе уравнение:");
                sys.add(new Scanner(System.in).nextLine());
                ArrayList<Double> approx = new ArrayList<>();
                System.out.println("Введите приближение для первого корня:");
                approx.add(new Scanner(System.in).nextDouble());
                System.out.println("Введите приближение для второго корня:");
                approx.add(new Scanner(System.in).nextDouble());
                System.out.println("Введите точность:");
                SimpleIterationSystem method = new SimpleIterationSystem(sys, approx, new Scanner(System.in).nextDouble());
                method.solve();
                printResult(method, "Система простой итерацией");
            }
            default -> System.out.println("Такого метода нету.");
        }

    }

    public static void printResult(NonlinearSolver solver, String name) {
        System.out.println(name + ":");
        if (!solver.messages.isEmpty()) System.out.println(solver.messages);
        System.out.println("Количество итераций: " + solver.iterationsNumber);
        if (solver instanceof SimpleIterationSystem) {
            Expression exp1 = new ExpressionBuilder(solver.equationSystem.get(0)).variables("x1", "x2").build();
            Expression exp2 = new ExpressionBuilder(solver.equationSystem.get(1)).variables("x1", "x2").build();
            if (solver.nextApproximations.size() == 2) {
                BigDecimal x1Expr = BigDecimal.valueOf(exp1.setVariable("x1", solver.nextApproximations.get(0))
                                .setVariable("x2", solver.nextApproximations.get(1)).evaluate());
                BigDecimal x2Expr = BigDecimal.valueOf(exp2.setVariable("x1", solver.nextApproximations.get(0))
                                .setVariable("x2", solver.nextApproximations.get(1)).evaluate());

                System.out.println("Решение: " + solver.nextApproximations);
                System.out.println("Значение первой функции при найденном корне: "
                        + BigDecimal.valueOf(solver.nextApproximations.get(0)).subtract(x1Expr));

                System.out.println("Значение второй функции при найденном корне: "
                        + BigDecimal.valueOf(solver.nextApproximations.get(1)).subtract(x2Expr));
            }
        } else {
            System.out.println("Решение: " + solver.resultX);
            System.out.println("Значение функции при найденном корне: "
                    + BigDecimal.valueOf(solver.equation.setVariable("x", solver.resultX).evaluate())
                    .setScale(BigDecimal.valueOf(solver.accuracy).scale(), RoundingMode.HALF_UP));

            if (solver.rootsNumber == RootsNumber.ONLY_ROOT) {
                System.out.println("Гарантирован один корень на выбранном отрезке");
            } else if (solver.rootsNumber == RootsNumber.MANY_ROOTS) {
                System.out.println("Рекомендуется уточнить интервал, возможно более одного корня");
            } else {
                System.out.println("На отрезке корней нету");
            }
        }
        System.out.println();
    }
}