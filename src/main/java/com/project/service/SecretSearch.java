package com.project.service;

import com.project.model.Alternative;
import ilog.concert.*;
import ilog.cplex.IloCplex;
import javafx.util.Pair;
import net.sf.clipsrules.jni.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SecretSearch implements ISecretSearch{
    private static final ISecretSearch INSTANCE = new SecretSearch();
    private Environment clips;
    private IloCplex cplex;

    public static ISecretSearch getInstance() {
        return INSTANCE;
    }
    
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
    public List<Alternative> getAllAlternatives(Object... args) throws CLIPSException, IloException {
        double[][] pricePerNight = {
                {1,2,3,4,5},
                {6,7,8,9,10},
                {11,12,13,14,15},
                {16,17,18,19,20},
                {21,22,23,24,25},
        };
        double[][] places = {
                {1,2,2,1,1},
                {2,2,1,2,2},
                {2,1,2,1,2},
                {2,1,2,2,2},
                {1,2,2,2,2},
        };

        List<Alternative> alternatives = new LinkedList<>();
        List<String> hotelsName = new LinkedList<>();
        List<Double> certainties = new LinkedList<>();
        List<Double> solutionToDiscard = new LinkedList<>();
        int days = (Integer)args[1];
        double budget = (Double)args[2];
        int people = (Integer)args[3];
        int maxStars = (Integer)args[6];
        int minStars = (Integer)args[7];
        Random rand = new Random();

        clips.reset();

        for (Object obj : (ArrayList<Object>)args[0]) {
            LinkedHashMap<String, Object> hm = (LinkedHashMap<String, Object>)obj;

            clips.assertString("(attribute (name city) " +
                                         "(value " + hm.get("region") + "-" +  hm.get("city") + ") " +
                                         "(certainty 100.0))");

            clips.assertString("(attribute (name region) " +
                                         "(value " + hm.get("region") + ") " +
                                         "(certainty 100.0))");
        }

        for (String region : (ArrayList<String>)args[4]) {
            clips.assertString("(attribute (name only-region) " +
                                         "(value " + region + ") " +
                                         "(certainty 100.0))");

            clips.assertString("(attribute (name region) " +
                                         "(value " + region + ") " +
                                         "(certainty 100.0))");
        }

        for (String region : (ArrayList<String>)args[5]) {
            clips.assertString("(attribute (name only-not-region) " +
                                         "(value " + region + ") " +
                                         "(certainty 100.0))");

            clips.assertString("(attribute (name not-region) " +
                                         "(value " + region + ") " +
                                         "(certainty 100.0))");
        }

        clips.assertString("(attribute (name max-stars) " +
                                     "(value " + maxStars + ") " +
                                     "(certainty 100.0))");

        clips.assertString("(attribute (name min-stars) " +
                                     "(value " + minStars + ") " +
                                     "(certainty 100.0))");

        for (String tt : (ArrayList<String>)args[8]) {
            clips.assertString("(attribute (name tourism-type) " +
                                         "(value " + tt + ") " +
                                         "(certainty 100.0))");
        }

        clips.run();

        String evalStr = "(MAIN::get-hotel-attribute-list)";
        MultifieldValue mv = (MultifieldValue) clips.eval(evalStr);
        double[] coefficients = new double[mv.size()];
        int k = 0;

        for (PrimitiveValue pv : mv)
        {
            FactAddressValue fv = (FactAddressValue) pv;
            hotelsName.add(((LexemeValue) fv.getSlotValue("name")).getValue());
            certainties.add(((NumberValue) fv.getSlotValue("certainty")).doubleValue());
            coefficients[k++] = rand.nextDouble();
        }

        System.out.println(hotelsName);
        System.out.println(certainties);

        for (int i = 0; i < 3; ++i) {
            Pair<Alternative, Double> p = getSolution(hotelsName.subList(0,5), certainties.subList(0,5), places, pricePerNight,
                    days, budget, Arrays.copyOfRange(coefficients, 0, 5), solutionToDiscard, 5, people);
            solutionToDiscard.add(p.getValue());
            alternatives.add(p.getKey());
        }

        return alternatives;
    }

    private Pair<Alternative, Double> getSolution(List<String> hotels, List<Double> certainties, double places[][],
                                                  double pricePerNight[][], int days, double budget,
                                                  double coefficients[], List<Double> solToDiscard,
                                                  int maxNumberOfRooms, int numPeople)
            throws IloException {
        cplex = new IloCplex();
        IloIntVar z = cplex.intVar(0, Integer.MAX_VALUE, "z");
        IloIntVar[] x = new IloIntVar[5];
        IloIntVar[][] y = new IloIntVar[5][5];
        IloIntVar[][] g = new IloIntVar[5][5];
        IloLinearNumExpr linearNumExpr2 = cplex.linearNumExpr();
        IloLinearNumExpr linearNumExpr4 = cplex.linearNumExpr();
        IloLinearIntExpr linearIntExpr = cplex.linearIntExpr();
        IloLinearNumExpr objective = cplex.linearNumExpr();
        List<IloRange> constraints = new ArrayList<>();
        IloIntExpr[] intExprs = new IloIntExpr[5];
        List<HashMap<String, Object>> hotelsRooms = new LinkedList<>();
        double newScalarProduct = 0.0;
        double yMax = days;
        double yMin = 0.1;

        for (int i = 0; i < 5; ++i) {
            IloLinearNumExpr linearNumExpr1 = cplex.linearNumExpr();
            IloLinearNumExpr linearNumExpr3 = cplex.linearNumExpr();
            x[i] = cplex.intVar(0, Integer.MAX_VALUE, ("x" + i));
            linearIntExpr.addTerm(1, x[i]);

            for (int j = 0; j < 5; ++j) {
                y[j][i] = cplex.intVar(0, days, ("y" + j + i));
                g[j][i] = cplex.intVar(0, 1, ("g" + j + i));
                linearNumExpr1.addTerm(pricePerNight[j][i], y[j][i]);
                linearNumExpr3.addTerm(places[j][i], g[j][i]);
            }

            objective.addTerm(certainties.get(i), x[i]);
            linearNumExpr2.add(linearNumExpr1);
            linearNumExpr4.add(linearNumExpr3);
        }

        for (int i = 0; i < 5; ++i) {
            IloLinearIntExpr linearIntExpr1 = cplex.linearIntExpr();

            for (int j = 0; j < 5; ++j) {
                linearIntExpr1.addTerm(1, y[j][i]);
                constraints.add((IloRange) cplex.addGe(y[j][i], cplex.prod(yMin, g[j][i])));
                constraints.add((IloRange) cplex.addLe(y[j][i], cplex.prod(yMax, g[j][i])));
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
        constraints.add(cplex.addEq(linearNumExpr4, numPeople));
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

            //System.out.println(hotelsRooms);

            for (int i = 0; i < hotels.size(); ++i) {
                newScalarProduct += (cplex.getValue(x[i]) * coefficients[i]);
            }

            //System.out.println(newScalarProduct);

            Alternative alt = new Alternative (hotelsRooms, days);
            return new Pair<>(alt, newScalarProduct);
        } else {
            System.out.println("Model not solved");
            return null;
        }
    }
}
