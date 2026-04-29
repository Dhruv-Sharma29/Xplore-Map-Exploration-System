package util;

import model.User;
import service.DistanceCalculator;
import database.UserDAO;

public class LocationSimulator {

    public static void simulate(User user) {

        new Thread(() -> {
            try {
                java.util.List<model.Zone> allZones = database.ZoneDAO.getAllZones();
                String[] places = util.Session.customRoute.split(",");

                java.util.List<double[]> pathList = new java.util.ArrayList<>();

                for (String place : places) {
                    place = place.trim().toLowerCase();
                    if (place.isEmpty()) continue;

                    for (model.Zone z : allZones) {
                        if (z.name.toLowerCase().contains(place)) {
                            pathList.add(new double[]{z.lat, z.lon});
                            break;
                        }
                    }
                }

                if (pathList.size() < 2) {
                    System.out.println("Not enough valid places found. Using fallback.");
                    if (pathList.size() == 1) {
                        pathList.add(pathList.get(0)); // Stay at location
                    } else {
                        pathList.add(new double[]{30.2880, 77.9960}); // Default ISBT
                        pathList.add(new double[]{30.3950, 78.0850}); // Default Malsi
                    }
                }

                double[][] path = new double[pathList.size()][2];
                for (int i = 0; i < pathList.size(); i++) {
                    path[i] = pathList.get(i);
                }

                System.out.println("Routing dynamically through " + pathList.size() + " locations.");

                double totalDistance = 0;
                for (int i = 0; i < path.length - 1; i++) {
                    totalDistance += DistanceCalculator.calculate(path[i][0], path[i][1], path[i+1][0], path[i+1][1]);
                }

                double traveledSoFar = 0;
                double speed = totalDistance / 1000.0; // constant speed across all segments

                for (int i = 0; i < path.length - 1; i++) {

                    double lat1 = path[i][0];
                    double lon1 = path[i][1];
                    double lat2 = path[i+1][0];
                    double lon2 = path[i+1][1];

                    double segmentDist = DistanceCalculator.calculate(lat1, lon1, lat2, lon2);
                    int stepsForSegment = (int) (segmentDist / speed);
                    if (stepsForSegment <= 0) stepsForSegment = 1;

                    for (int step = 0; step <= stepsForSegment; step++) {

                        double lat = lat1 + (lat2 - lat1) * step / (double)stepsForSegment;
                        double lon = lon1 + (lon2 - lon1) * step / (double)stepsForSegment;

                        user.updateLocation(lat, lon);
                        user.progress = (traveledSoFar + segmentDist * step / (double)stepsForSegment) / totalDistance;
                        Thread.sleep(30);
                    }
                    traveledSoFar += segmentDist;
                }

                // Save to database when done
                UserDAO.saveUserProgress(util.Session.currentUser, 100.0);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();
    }
}