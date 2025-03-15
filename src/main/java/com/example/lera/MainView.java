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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class MainView extends Application
{
    private VBox centerPane;

    final Map<String, String> productDescriptions = Map.of(
            "JTF 1264", "JT Sprockets - высококачественная звёздочка для мотоциклов...",
            "JTF 1180", "JT Sprockets - универсальная звёздочка для спортивных мотоциклов..."
    );

    final Map<String, String> productImages = Map.of(
            "JTF 1264", "/photo1.png",
            "JTF 1180", "/photo2.png"
    );

    @Override
    public void start(Stage stage)
    {
        HBox root = new HBox();

        VBox leftPane = createPane("Categories", Color.LIGHTGRAY, 300);
        leftPane.setAlignment(Pos.TOP_CENTER);
        leftPane.setSpacing(30);

        centerPane = createPane("Список запчастей", Color.WHITE, 1000);
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

        firstBox.getItems().addAll("Motostars", "Chains", "Dampers", "Engine");
        firstBox.setPromptText("Pick part");

        secondBox.setDisable(true);
        thirdBox.setDisable(true);

        thirdBox.setOnAction(event -> {
            String selectedProduct = thirdBox.getValue();
            updateCenterPane(selectedProduct);
        });


        leftPane.getChildren().addAll(firstBox, secondBox, thirdBox);

        firstBox.setOnAction(event ->
        {
            secondBox.getItems().clear();
            secondBox.setDisable(false);
            thirdBox.setDisable(true);

            String selected = firstBox.getValue();
            if ("Motostars".equals(selected))
            {
                secondBox.getItems().addAll("Jt", "Ognibene", "Renthal");
            }
            else if ("Chains".equals(selected))
            {
                secondBox.getItems().addAll("CZ Chains", "DID", "EK Chain");
            }
            else if ("Dampers".equals(selected))
            {
                secondBox.getItems().addAll("Shock absorbers", "Bearings", "Forks");
            }
            else if ("Engine".equals(selected))
            {
                secondBox.getItems().addAll("Clutch", "Pistons", "Crankshaft");
            }
            secondBox.setPromptText("Select a subcategory");
        });

        secondBox.setOnAction(event ->
        {
            thirdBox.getItems().clear();
            thirdBox.setDisable(false);

            String selected = secondBox.getValue();
            if ("Jt".equals(selected))
            {
                thirdBox.getItems().addAll("JTF 1264", "JTF 1180");
            }
            else if ("Ognibene".equals(selected))
            {
                thirdBox.getItems().addAll("8144-Z45", "525.46");
            }
            else if ("Renthal".equals(selected))
            {
                thirdBox.getItems().addAll("J416-13", "J558-14");
            }

            else if("CZ Chains".equals(selected))
            {
                thirdBox.getItems().addAll("420-98", "420-90");
            }
            else if("DID".equals(selected))
            {
                thirdBox.getItems().addAll("NZ SDH 428 124", "NZ SDH 428 134");
            }
            else if("EK Chain".equals(selected))
            {
                thirdBox.getItems().addAll("420Sh 140", "428Shdr 140");
            }

            else if("Shock absorbers".equals(selected))
            {
                thirdBox.getItems().addAll("37-1119", "37-1125");
            }
            else if("Bearings".equals(selected))
            {
                thirdBox.getItems().addAll("CH25-1244", "CH25-1327");
            }
            else if("Forks".equals(selected))
            {
                thirdBox.getItems().addAll("38-6075", "38-6020");
            }

            else if("Clutch".equals(selected))
            {
                thirdBox.getItems().addAll("CRF 250 R", "CRF 251 R");
            }
            else if("Pistons".equals(selected))
            {
                thirdBox.getItems().addAll("SX65 09-18", "SX65 97-08");
            }
            else if("Crankshaft".equals(selected))
            {
                thirdBox.getItems().addAll("YZ125 01-04", "SX 16-18");
            }
            thirdBox.setPromptText("Pick product");
        });

        Scene scene = new Scene(root, 1400, 700);

        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/main_view.css")).toExternalForm());

        stage.setTitle("Test");
        stage.setScene(scene);
        stage.show();
    }

    private void updateCenterPane(String productName)
    {
        centerPane.getChildren().clear();

        String description = productDescriptions.getOrDefault(productName, "Описание не найдено");

        Label titleLabel = new Label(productName);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        Label descriptionLabel = new Label(description);
        descriptionLabel.setWrapText(true);

        ImageView imageView = new ImageView();
        imageView.setFitWidth(300);
        imageView.setPreserveRatio(true);

        String imagePath = productImages.get(productName);
        if (imagePath != null)
        {
            imageView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath))));
        }

        centerPane.getChildren().addAll(titleLabel, imageView, descriptionLabel);
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