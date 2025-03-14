package com.example.lera;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ComboBox;
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

        VBox leftPane = createPane("Categories", Color.LIGHTGRAY, 300);
        leftPane.setAlignment(Pos.TOP_CENTER);

        VBox centerPane = createPane("Список запчастей", Color.WHITE, 1000);
        centerPane.setAlignment(Pos.TOP_LEFT);

        VBox rightPane = createPane("Options", Color.LIGHTBLUE, 100);
        rightPane.setAlignment(Pos.TOP_CENTER);
        rightPane.setSpacing(30);

        root.getChildren().addAll(leftPane, centerPane, rightPane);

        Button user_button = new Button("Users");
        Button tran_button = new Button("Buy");
        Button order_button = new Button("Order");
        Button parts_button = new Button("Parts");

        rightPane.getChildren().addAll(user_button, tran_button, order_button, parts_button);

        ComboBox<String> firstBox = new ComboBox<>();
        ComboBox<String> secondBox = new ComboBox<>();
        ComboBox<String> thirdBox = new ComboBox<>();

        firstBox.getItems().addAll("Звёзды", "Цепи", "Фильтры");
        firstBox.setPromptText("Выберите категорию");

        secondBox.setDisable(true);
        thirdBox.setDisable(true);

        leftPane.getChildren().addAll(firstBox, secondBox, thirdBox);

        firstBox.setOnAction(event ->
        {
            secondBox.getItems().clear();
            secondBox.setDisable(false);
            thirdBox.setDisable(true);

            String selected = firstBox.getValue();
            if ("Звёзды".equals(selected))
            {
                secondBox.getItems().addAll("Поршни", "Коленвал", "Фильтр");
            }
            else if ("Цепи".equals(selected))
            {
                secondBox.getItems().addAll("Амортизаторы", "Рычаги", "Пружины");
            }
            else if ("Фильтры".equals(selected))
            {
                secondBox.getItems().addAll("Аккумулятор", "Стартер", "Фара");
            }
            secondBox.setPromptText("Выберите подкатегорию");
        });

        secondBox.setOnAction(event ->
        {
            thirdBox.getItems().clear();
            thirdBox.setDisable(false);

            String selected = secondBox.getValue();
            if ("Поршни".equals(selected))
            {
                thirdBox.getItems().addAll("Поршень A", "Поршень B");
            }
            else if ("Коленвал".equals(selected))
            {
                thirdBox.getItems().addAll("Коленвал X", "Коленвал Y");
            }
            else if ("Амортизаторы".equals(selected))
            {
                thirdBox.getItems().addAll("Передний", "Задний");
            }
            else if ("Фара".equals(selected))
            {
                thirdBox.getItems().addAll("Фара LED", "Галоген");
            }
            thirdBox.setPromptText("Выберите деталь");
        });

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