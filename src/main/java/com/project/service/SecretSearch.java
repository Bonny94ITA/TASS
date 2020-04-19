package com.project.service;

import com.project.model.*;
import ilog.concert.*;
import ilog.cplex.IloCplex;
import javafx.util.Pair;
import net.sf.clipsrules.jni.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SecretSearch implements ISecretSearch{
    private static final ISecretSearch INSTANCE = new SecretSearch();
    private Environment clips;
    private IloCplex cplex;
    private static final short NUMBER_OF_SOLUTIONS_PROPOSED = 3;

    @Autowired
    IHotelService hotelService;

    @Autowired
    public SecretSearch () {
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
        Date arrival = (Date)args[9]; //input
        Date departure = (Date)args[10]; // input

        List<Room> roomFreeList = hotelService.findFreeRooms(arrival,departure);
        List<Hotel> hotelList = hotelService.findAllHotels();
        Map<Long, List<Room>> hotelRooms = new HashMap<>();
        List<TourismType> tourismTypeList = hotelService.findAllTourismTypes();
        List<City> citiesList = hotelService.findAllCities();
        int max_room = 0;

        for(Room r: roomFreeList){
            Hotel h = r.getHotel();

            if (!hotelRooms.containsKey(h.getId())) {
                hotelRooms.put(h.getId(), new ArrayList<>());
            }

            List<Room> rooms = hotelRooms.get(h.getId());
            rooms.add(r);
            hotelRooms.replace(h.getId(), rooms);
        }

        for (List l : hotelRooms.values()){
            if (max_room < l.size())
                max_room = l.size();
        }

        /*List<Hotel> hotelList = hotelService.findAllHotels();
        List<TourismType> tourismTypeList = hotelService.findAllTourismTypes();
        List<City> citiesList = hotelService.findAllCities();
        List<List<Room>> roomList = new ArrayList<>();
        int max_room = 0;

        for(int i=0;i<hotelList.size();i++){
            Long hotel_id = hotelList.get(i).getId();
            List<Room> rooms = hotelService.findRooms(hotel_id);
            roomList.add(rooms);
            if(max_room<rooms.size())
                max_room=rooms.size();
        }*/

        double[][] pricePerNight = new double[max_room][hotelList.size()]; //riga stanze, colonna hotel
        double[][] places = new double[max_room][hotelList.size()];

        for(int i=0;i<hotelList.size();i++){
            Hotel h = hotelList.get(i);
            List<Room> rooms = hotelRooms.get(h.getId());

            if (rooms == null) { rooms = new ArrayList<>(); }

                for(int j=0;j<max_room;j++) {
                    if(j>=rooms.size()){
                        pricePerNight[j][i] = Double.MAX_VALUE;
                        places[j][i] = 0;
                    }else{
                        Room r = rooms.get(j);
                        pricePerNight[j][i] = r.getPricePerNight();
                        places[j][i] = r.getNumPlaces();
                    }
                }
        }

        List<Alternative> alternatives = new LinkedList<>();
        List<String> hotelsName = new LinkedList<>();
        List<Double> certainties = new LinkedList<>();
        List<Double> solutionToDiscard = new LinkedList<>();
        int days = (Integer)args[1];
        double budget = (Double)args[2];
        int people = (Integer)args[3];
        String onlyRegion = (String)args[4];
        String onlyNotRegion = (String)args[5];
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

        clips.assertString("(attribute (name only-region) " +
                                     "(value " + onlyRegion + ") " +
                                     "(certainty 100.0))");

        clips.assertString("(attribute (name region) " +
                                     "(value " + onlyRegion + ") " +
                                     "(certainty 100.0))");

        clips.assertString("(attribute (name only-not-region) " +
                                     "(value " + onlyNotRegion + ") " +
                                     "(certainty 100.0))");

        clips.assertString("(attribute (name not-region) " +
                                     "(value " + onlyNotRegion + ") " +
                                     "(certainty 100.0))");

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

        for (Hotel h : hotelList) {
            clips.assertString("(hotel (name " + h.getName() + ") " +
                                         "(tr " + h.getCity().getName() + ") " +
                                         "(stars " + h.getStars() + "))");
        }

        for (TourismType tt : tourismTypeList) {
            clips.assertString("(tourism-type (tt " + tt.getType() + "))");
        }

        for (City city : citiesList) {
            String str = "";
            List<TourismType> types = city.getTourismTypes();
            for (TourismType tt : types) { str += tt.getType() + " "; }
            str = str.substring(0, str.length() - 1);
            clips.assertString("(tourism-resort " +
                                         "(name " + city.getName() + ") " +
                                         "(region " + city.getRegion() + ") " +
                                         "(type " + str + "))");
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

        if (hotelsName.size() > 0) {
            for (int i = 0; i < NUMBER_OF_SOLUTIONS_PROPOSED; ++i) {
                Pair<Alternative, Double> p = getSolution(hotelsName, certainties, places, pricePerNight,
                        days, budget, coefficients, solutionToDiscard, max_room, people);
                solutionToDiscard.add(p.getValue());
                alternatives.add(p.getKey());
            }
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
        IloIntVar[] x = new IloIntVar[hotels.size()];
        IloIntVar[][] y = new IloIntVar[maxNumberOfRooms][hotels.size()];
        IloIntVar[][] g = new IloIntVar[maxNumberOfRooms][hotels.size()];
        IloLinearNumExpr linearNumExpr2 = cplex.linearNumExpr();
        IloLinearNumExpr linearNumExpr4 = cplex.linearNumExpr();
        IloLinearIntExpr linearIntExpr = cplex.linearIntExpr();
        IloLinearNumExpr objective = cplex.linearNumExpr();
        List<IloRange> constraints = new ArrayList<>();
        IloIntExpr[] intExprs = new IloIntExpr[hotels.size()];
        List<HashMap<String, Object>> hotelsRooms = new LinkedList<>();
        double newScalarProduct = 0.0;
        double yMax = days;
        double yMin = 0.1;

        for (int i = 0; i < hotels.size(); ++i) {
            IloLinearNumExpr linearNumExpr1 = cplex.linearNumExpr();
            IloLinearNumExpr linearNumExpr3 = cplex.linearNumExpr();
            x[i] = cplex.intVar(0, Integer.MAX_VALUE, ("x" + i));
            linearIntExpr.addTerm(1, x[i]);

            for (int j = 0; j < maxNumberOfRooms; ++j) {
                y[j][i] = cplex.intVar(0, days, ("y" + j + i));
                g[j][i] = cplex.intVar(0, 1, ("g" + j + i));
                linearNumExpr1.addTerm(pricePerNight[j][i], y[j][i]);
                linearNumExpr3.addTerm(places[j][i], g[j][i]);
            }

            objective.addTerm(certainties.get(i), x[i]);
            linearNumExpr2.add(linearNumExpr1);
            linearNumExpr4.add(linearNumExpr3);
        }

        for (int i = 0; i < hotels.size(); ++i) {
            IloLinearIntExpr linearIntExpr1 = cplex.linearIntExpr();

            for (int j = 0; j < maxNumberOfRooms; ++j) {
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
                        hm.put("RoomId", (i + 1));
                        hm.put("DaysInRoom", cplex.getValue(y[i][j]));
                        hotelsRooms.add(hm);
                    }
                }
            }

            for (int i = 0; i < hotels.size(); ++i) {
                newScalarProduct += (cplex.getValue(x[i]) * coefficients[i]);
            }

            Alternative alt = new Alternative (hotelsRooms, days);
            return new Pair<>(alt, newScalarProduct);
        } else {
            System.out.println("Model not solved");
            return null;
        }
    }
}
