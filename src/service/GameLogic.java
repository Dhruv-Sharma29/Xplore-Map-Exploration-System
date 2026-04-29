package service;

import model.Zone;
import model.User;

import java.util.List;

public class GameLogic {

    public static void run(User user, List<Zone> zones) {

        new Thread(() -> {

            while (true) {

                for (Zone z : zones) {

                    if (!z.unlocked) {

                        double distance = DistanceCalculator.calculate(
                                user.lat, user.lon,
                                z.lat, z.lon
                        );

                        if (distance <= z.radius) {
                            z.unlocked = true;
                            System.out.println("Unlocked: " + z.name);
                        }
                    }
                }

                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }).start();
    }
}