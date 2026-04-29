package controller;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import model.User;
import model.Zone;
import service.ZoneManager;
import service.DistanceCalculator;
import util.LocationSimulator;

import java.util.*;

public class MapController {

    @FXML private javafx.scene.image.ImageView mapImage;
    @FXML private Canvas fogCanvas;
    @FXML private Canvas markerCanvas;
    @FXML private Canvas playerCanvas;

    @FXML private Label progressLabel;
    @FXML private ProgressBar progressBar;

    @FXML private VBox questContainer;

    private GraphicsContext fogGC;
    private GraphicsContext markerGC;
    private GraphicsContext playerGC;

    private Image fogImage;

    private User user;
    private List<Zone> zones;

    private int questCounter = 1;

    // 🔥 store revealed areas
    private List<double[]> revealedAreas = new ArrayList<>();
    private javafx.scene.shape.Shape fogMask;

    private static final double MIN_LAT = 30.27;
    private static final double MAX_LAT = 30.41;
    private static final double MIN_LON = 77.97;
    private static final double MAX_LON = 78.10;

    public void addQuest(String locationName) {
        javafx.scene.layout.VBox box = new javafx.scene.layout.VBox(8);
        box.setStyle("-fx-background-color: #2a2a2a; -fx-padding: 15; -fx-background-radius: 8; -fx-border-color: #444; -fx-border-radius: 8;");

        Label title = new Label(questCounter + ". " + locationName + " Explorer");
        title.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        Label desc = new Label("You have discovered " + locationName + "! Investigate the area completely.");
        desc.setWrapText(true);
        desc.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 12px;");

        javafx.scene.layout.HBox progressBox = new javafx.scene.layout.HBox(10);
        progressBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        ProgressBar pb = new ProgressBar(0.0);
        pb.setPrefWidth(180);
        pb.setStyle("-fx-accent: limegreen;");

        Label pct = new Label("0%");
        pct.setStyle("-fx-text-fill: white; -fx-font-size: 10px;");

        progressBox.getChildren().addAll(pb, pct);
        box.getChildren().addAll(title, desc, progressBox);

        questContainer.getChildren().add(box);
        questCounter++;
    }

    @FXML
    public void initialize() {

        mapImage.setImage(new Image(getClass().getResource("/map.png").toExternalForm()));
        fogImage = new Image(getClass().getResource("/fog.png").toExternalForm());

        fogGC = fogCanvas.getGraphicsContext2D();
        markerGC = markerCanvas.getGraphicsContext2D();
        playerGC = playerCanvas.getGraphicsContext2D();

        fogMask = new javafx.scene.shape.Rectangle(0, 0, 900, 600);
        fogCanvas.setClip(fogMask);
        fogGC.drawImage(fogImage, 0, 0, 900, 600);

        user = new User();
        zones = ZoneManager.loadZones();

        drawMarkers();

        LocationSimulator.simulate(user);

        startGameLoop();
    }



    private void drawMarkers() {

        markerGC.clearRect(0, 0, 900, 600);

        for (Zone z : zones) {

            double x = convertX(z.lon);
            double y = convertY(z.lat);

            markerGC.setFill(z.unlocked ? Color.LIMEGREEN : Color.RED);
            markerGC.fillOval(x - 6, y - 6, 12, 12);
        }
    }

    private void drawPlayer(double x, double y) {

        playerGC.clearRect(0, 0, 900, 600);
        playerGC.setFill(Color.BLUE);
        playerGC.fillOval(x - 6, y - 6, 12, 12);
    }

    // 🔥 just store reveal position and punch hole
    private void reveal(double x, double y) {
        if (!revealedAreas.isEmpty()) {
            double[] last = revealedAreas.get(revealedAreas.size() - 1);
            double dx = x - last[0];
            double dy = y - last[1];
            double length = Math.hypot(dx, dy);

            if (length < 2) {
                return;
            }

            double nx = -dy / length * 60; // 60 is radius
            double ny = dx / length * 60;

            javafx.scene.shape.Polygon rect = new javafx.scene.shape.Polygon(
                    last[0] + nx, last[1] + ny,
                    last[0] - nx, last[1] - ny,
                    x - nx, y - ny,
                    x + nx, y + ny
            );
            javafx.scene.shape.Circle hole = new javafx.scene.shape.Circle(x, y, 60);
            javafx.scene.shape.Shape segmentHole = javafx.scene.shape.Shape.union(rect, hole);
            fogMask = javafx.scene.shape.Shape.subtract(fogMask, segmentHole);
        } else {
            javafx.scene.shape.Circle hole = new javafx.scene.shape.Circle(x, y, 60);
            fogMask = javafx.scene.shape.Shape.subtract(fogMask, hole);
        }
        revealedAreas.add(new double[]{x, y});
        fogCanvas.setClip(fogMask);
    }

    private void updateProgress() {
        double totalProgress = Math.min(1.0, Math.max(0.0, user.progress));
        progressBar.setProgress(totalProgress);
        progressLabel.setText(String.format("Progress: %.1f%%", totalProgress * 100));
    }

    private void showPopup(String zone) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Zone Unlocked");
        alert.setHeaderText(null);
        alert.setContentText(zone + " unlocked!");
        alert.show();
    }

    private void startGameLoop() {

        new Thread(() -> {

            while (true) {

                double userX = convertX(user.lon);
                double userY = convertY(user.lat);

                javafx.application.Platform.runLater(() -> {
                    drawPlayer(userX, userY);
                    reveal(userX, userY);
                    updateProgress();
                });

                for (Zone z : zones) {

                    if (!z.unlocked) {

                        double d = DistanceCalculator.calculate(
                                user.lat, user.lon,
                                z.lat, z.lon
                        );

                        if (d <= z.radius) {

                            z.unlocked = true;

                            double x = convertX(z.lon);
                            double y = convertY(z.lat);

                            javafx.application.Platform.runLater(() -> {
                                reveal(x, y);
                                drawMarkers();
                                updateProgress();
                                showPopup(z.name);
                                addQuest(z.name);
                            });
                        }
                    }
                }

                try {
                    Thread.sleep(30);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }).start();
    }

    private double convertX(double lon) {
        return ((lon - MIN_LON) / (MAX_LON - MIN_LON)) * 900;
    }

    private double convertY(double lat) {
        return ((MAX_LAT - lat) / (MAX_LAT - MIN_LAT)) * 600;
    }
}