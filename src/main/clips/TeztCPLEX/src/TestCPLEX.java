import ilog.concert.*;
import ilog.cplex.*;

import java.util.ArrayList;
import java.util.List;

public class TestCPLEX {
    static void transpose(double A[][], double B[][])
    {
        int i, j;
        for (i = 0; i < 5; i++)
            for (j = 0; j < 5; j++)
                B[i][j] = A[j][i];
    }

    public static void main(String[] args) {
        try {
            IloCplex cplex = new IloCplex();
            IloIntVar z = cplex.intVar(0, Integer.MAX_VALUE, "z");
            IloIntVar[] x = new IloIntVar[5];
            IloIntVar[][] y = new IloIntVar[5][5];
            IloLinearNumExpr linearNumExpr2 = cplex.linearNumExpr();
            IloLinearIntExpr linearIntExpr = cplex.linearIntExpr();
            IloLinearNumExpr objective = cplex.linearNumExpr();
            List<IloRange> constraints = new ArrayList<>();
            IloIntExpr[] intExprs = new IloIntExpr[5];
            double[] profits = {1,2,3,4,5};
            double v[] = {0.45921, 0.81265, 0.60157, 0.15865, 0.95023};
            double[][] ppnT = {
                    {1,2,3,4,5},
                    {6,7,8,9,10},
                    {11,12,13,14,15},
                    {16,17,18,19,20},
                    {21,22,23,24,25},
            };
            int G = 7;
            int B = 500;

            for (int i = 0; i < 5; ++i) {
                IloLinearNumExpr linearNumExpr1 = cplex.linearNumExpr();
                x[i] = cplex.intVar(0, Integer.MAX_VALUE, ("x" + i));
                linearIntExpr.addTerm(1, x[i]);

                for (int j = 0; j < 5; ++j) {
                    y[i][j] = cplex.intVar(0, 1, ("y" + i + j));
                }

                objective.addTerm(profits[i], x[i]);
                linearNumExpr1.addTerms(ppnT[i], y[i]);
                linearNumExpr2.add(linearNumExpr1);
            }

            for (int i = 0; i < 5; ++i) {
                IloLinearIntExpr linearIntExpr1 = cplex.linearIntExpr();

                for (int j = 0; j < 5; ++j) {
                    linearIntExpr1.addTerm(1, y[j][i]);
                }

                intExprs[i] = cplex.abs(cplex.diff(x[i], 1));
                constraints.add((IloRange)cplex.addEq(linearIntExpr1, x[i]));
            }

            IloLinearNumExpr linearNumExpr_ = cplex.linearNumExpr();
            linearNumExpr_.addTerms(v, x);
            constraints.add(cplex.addGe(cplex.abs(cplex.diff(linearNumExpr_, 5.51137)), 0.001));
            constraints.add(cplex.addGe(cplex.abs(cplex.diff(linearNumExpr_, 4.71979)), 0.001));

            constraints.add((IloRange)cplex.addEq(cplex.sum(intExprs), z));
            constraints.add(cplex.addLe(linearIntExpr, G));
            constraints.add(cplex.addLe(linearNumExpr2, B));
            cplex.addMaximize(cplex.diff(objective, z));

            System.out.println(cplex);

            if (cplex.solve()) {
                System.out.println(cplex.getObjValue());

                for (int i = 0; i < 5; ++i) {
                    System.out.println("x" + i + ":" + cplex.getValue(x[i]));
                }

                for (int i = 0; i < 5; ++i) {
                    for (int j = 0; j < 5; ++j) {
                        System.out.println("y" + i + j + ":" + cplex.getValue(y[i][j]));
                    }
                }

            } else {
                System.out.println("Model not solved");
            }

            cplex.end();
            System.out.println("END");
        }
        catch (IloException e) {
            System.err.println("Concert exception '" + e + "' caught");
        }
    }
}