@echo off
java --module-path javafx-sdk-21.0.2/lib --add-modules javafx.controls,javafx.fxml -cp "bin;sqlite-jdbc.jar;resources" app.Main
