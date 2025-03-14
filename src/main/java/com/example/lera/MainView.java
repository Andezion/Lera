package com.example.lera;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainView extends Application
{
    @Override
    public void start(Stage stage)
    {
        HBox root = new HBox();

        VBox leftPane = createPane("Категории", Color.LIGHTGRAY, 300);
        leftPane.setAlignment(Pos.TOP_CENTER);

        VBox centerPane = createPane("Список запчастей", Color.WHITE, 1000);
        centerPane.setAlignment(Pos.TOP_LEFT);

        VBox rightPane = createPane("Описание детали", Color.LIGHTBLUE, 100);
        rightPane.setAlignment(Pos.TOP_CENTER);
        rightPane.setSpacing(30);

        root.getChildren().addAll(leftPane, centerPane, rightPane);

        Button user_button = new Button("User");
        Button tran_button = new Button("Transaction");
        Button order_button = new Button("Order");
        Button parts_button = new Button("Parts");

        rightPane.getChildren().addAll(user_button, tran_button, order_button, parts_button);

        Scene scene = new Scene(root, 1400, 700);

        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/main_view.css")).toExternalForm());

        stage.setTitle("Выбор запчастей для мотоциклов");
        stage.setScene(scene);
        stage.show();
    }


    private VBox createPane(String text, Color color, double width)
    {
        VBox pane = new VBox();
        pane.setPrefWidth(width);
        pane.setBackground(new Background(new BackgroundFill(color, null, null)));
        pane.setAlignment(Pos.CENTER);
        pane.getChildren().add(new Label(text));

        return pane;
    }

        public static void main(String[] args)
    {
        launch();
    }
}