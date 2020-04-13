package com.project.service;

import com.project.model.Alternative;
import ilog.concert.*;
import ilog.cplex.IloCplex;
import javafx.util.Pair;
import net.sf.clipsrules.jni.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Service
public class SecretSearch implements ISecretSearch{
    private Environment clips;
    private IloCplex cplex;

    private static final ISecretSearch INSTANCE = new SecretSearch();

    public static ISecretSearch getInstance() {
        return INSTANCE;
    }

    //LOL
    private SecretSearch () {
        clips = new Environment();

        try
        {
            clips.loadFromResource("/bikers-companion.clp");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public List<Alternative> getAllAlternatives(/*PARAMETERS*/) throws CLIPSException, IloException {
        double[] certaintives = {1,2,3,4,5};
        List<Alternative> alternatives = new LinkedList<>();
        double v[] = {0.45921, 0.81265, 0.60157, 0.15865, 0.95023};
        double[][] pricePerNight = {
                {1,2,3,4,5},
                {6,7,8,9,10},
                {11,12,13,14,15},
                {16,17,18,19,20},
                {21,22,23,24,25},
        };

        List<String> hotelsName = new LinkedList<>();
        List<Double> solutionToDiscard = new LinkedList<>();
        int days = 7;
        double certainty, budget = 500.0;

        clips.reset();
        clips.assertString("(attribute (name tourism-type) " +
                "                       (value balneare) " +
                "                       (certainty 100.0))");

        clips.run();

        String evalStr = "(MAIN::get-hotel-attribute-list)";
        MultifieldValue mv = (MultifieldValue) clips.eval(evalStr);

        for (PrimitiveValue pv : mv)
        {
            FactAddressValue fv = (FactAddressValue) pv;

            hotelsName.add(((LexemeValue) fv.getSlotValue("name")).getValue());
            certainty = ((NumberValue) fv.getSlotValue("certainty")).doubleValue();
        }

        /* TEST */
        hotelsName = new LinkedList<>();
        for (int i = 0; i < 5; ++i) {
            hotelsName.add("HOTEL " + i);
        }

        for (int i = 0; i < 3; ++i) {
            Pair<Alternative, Double> p = getSolution(hotelsName, certaintives, pricePerNight,
                    days, budget, v, solutionToDiscard, 5);
            solutionToDiscard.add(p.getValue());
            alternatives.add(p.getKey());
        }

        return alternatives;
    }

    private Pair<Alternative, Double> getSolution(List<String> hotels, double certainties[], double pricePerNight[][],
                                                  int days, double budget, double coefficients[],
                                                  List<Double> solToDiscard, int maxNumberOfRooms)
            throws IloException {
        cplex = new IloCplex();
        IloIntVar z = cplex.intVar(0, Integer.MAX_VALUE, "z");
        IloIntVar[] x = new IloIntVar[5];
        IloIntVar[][] y = new IloIntVar[5][5];
        IloLinearNumExpr linearNumExpr2 = cplex.linearNumExpr();
        IloLinearIntExpr linearIntExpr = cplex.linearIntExpr();
        IloLinearNumExpr objective = cplex.linearNumExpr();
        List<IloRange> constraints = new ArrayList<>();
        IloIntExpr[] intExprs = new IloIntExpr[5];
        List<HashMap<String, Object>> hotelsRooms = new LinkedList<>();
        double newScalarProduct = 0.0;

        for (int i = 0; i < 5; ++i) {
            IloLinearNumExpr linearNumExpr1 = cplex.linearNumExpr();
            x[i] = cplex.intVar(0, Integer.MAX_VALUE, ("x" + i));
            linearIntExpr.addTerm(1, x[i]);

            for (int j = 0; j < 5; ++j) {
                y[i][j] = cplex.intVar(0, days, ("y" + i + j));
            }

            objective.addTerm(certainties[i], x[i]);
            linearNumExpr1.addTerms(pricePerNight[i], y[i]);
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

        for (Double scalarProduct : solToDiscard) {
            IloLinearNumExpr linearNumExpr_ = cplex.linearNumExpr();
            linearNumExpr_.addTerms(coefficients, x);
            constraints.add(cplex.addGe(cplex.abs(cplex.diff(linearNumExpr_, scalarProduct)), 0.001));
        }

        constraints.add((IloRange)cplex.addEq(cplex.sum(intExprs), z));
        constraints.add(cplex.addLe(linearIntExpr, days));
        constraints.add(cplex.addLe(linearNumExpr2, budget));
        cplex.addMaximize(cplex.diff(objective, z));

        if (cplex.solve()) {
            for (int j = 0; j  < hotels.size(); ++j) {
                for (int i = 0; i < maxNumberOfRooms; ++i) {
                    if (cplex.getValue(y[i][j]) > 0) {
                        HashMap<String, Object> hm = new HashMap<String, Object>();
                        hm.put("HotelName", hotels.get(j));
                        hm.put("RoomId", i);
                        hm.put("DaysInRoom", cplex.getValue(y[i][j]));
                        hotelsRooms.add(hm);
                    }
                }
            }

            for (int i = 0; i < 5; ++i) {
                for (int j = 0; j < 5; ++j) {
                    System.out.println("y" + i + j + ":" + cplex.getValue(y[i][j]));
                }
            }

            System.out.println(hotelsRooms);

            for (int i = 0; i < hotels.size(); ++i) {
                newScalarProduct += (cplex.getValue(x[i]) * coefficients[i]);
            }

            System.out.println(newScalarProduct);

            Alternative alt = new Alternative (hotelsRooms, days);
            return new Pair<>(alt, newScalarProduct);
        } else {
            System.out.println("Model not solved");
            return null;
        }
    }
}
