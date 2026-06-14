#!/bin/bash
# Script to run the JavaFX application on macOS
java --module-path javafx-sdk-21.0.10/lib --add-modules javafx.controls,javafx.fxml -cp "bin:sqlite-jdbc.jar:resources" app.Main
