@echo off
if not exist bin mkdir bin
dir /s /B pranjal\src\*.java > sources.txt
javac --module-path D:\Graph_Algo_Visualizer\javafx-sdk\javafx-sdk-21.0.2\lib --add-modules javafx.controls,javafx.fxml,javafx.graphics -d bin @sources.txt
if %errorlevel% neq 0 (
    echo Compilation Failed!
    pause
    exit /b %errorlevel%
)
java --module-path D:\Graph_Algo_Visualizer\javafx-sdk\javafx-sdk-21.0.2\lib --add-modules javafx.controls,javafx.fxml,javafx.graphics -cp bin ui.MainApp
