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
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainView extends Application
{
    private VBox centerPane;
    int pointer_for_button = 0;

    @Override
    public void start(Stage stage)
    {
        HBox root = new HBox();

        VBox leftPane = createPane("Categories", Color.LIGHTGRAY, 300);
        leftPane.setAlignment(Pos.TOP_CENTER);
        leftPane.setSpacing(30);

        centerPane = createPane("List of details", Color.WHITE, 1000);
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

        parts_button.setOnAction(e -> {

            if (!leftPane.getChildren().isEmpty() && pointer_for_button == 0)
            {
                pointer_for_button = 1;

                ComboBox<String> firstBox = new ComboBox<>();
                ComboBox<String> secondBox = new ComboBox<>();
                ComboBox<String> thirdBox = new ComboBox<>();


                try
                {
                    firstBox.getItems().addAll(TakeFromDatabase.getCategories());
                }
                catch (SQLException ex)
                {
                    throw new RuntimeException(ex);
                }

                firstBox.setPromptText("Pick part");

                secondBox.setDisable(true);
                thirdBox.setDisable(true);

                leftPane.getChildren().addAll(firstBox, secondBox, thirdBox);

                firstBox.setOnAction(event ->
                {
                    secondBox.getItems().clear();
                    secondBox.setDisable(false);
                    thirdBox.setDisable(true);

                    String selectedCategory = firstBox.getValue();
                    try
                    {
                        secondBox.getItems().addAll(TakeFromDatabase.getProducers(selectedCategory));
                    }
                    catch (SQLException ex)
                    {
                        throw new RuntimeException(ex);
                    }
                    secondBox.setPromptText("Select a producer");
                });

                secondBox.setOnAction(event ->
                {
                    thirdBox.getItems().clear();
                    thirdBox.setDisable(false);

                    String selectedProducer = secondBox.getValue();
                    try
                    {
                        List<Part> parts = TakeFromDatabase.getParts(selectedProducer);

                        thirdBox.getItems().addAll(
                                parts.stream().map(Part::getName).toList()
                        );
                    }
                    catch (SQLException ex)
                    {
                        throw new RuntimeException(ex);
                    }
                    thirdBox.setPromptText("Pick product");
                });

                thirdBox.setOnAction(event ->
                {
                    String selectedProduct = thirdBox.getValue();

                    try
                    {
                        System.out.println("Выбранная деталь: " + selectedProduct); // это для себя - можно убрать

                        Part partDetails = TakeFromDatabase.getPartByName(selectedProduct);

                        if (partDetails == null)
                        {
                            System.out.println("Я просто повешусь нахуй"); // это если деталь не описал
                            return;
                        }

                        updateCenterPane(partDetails);
                    }
                    catch (SQLException ex)
                    {
                        throw new RuntimeException(ex);
                    }
                });
            }
        });

        Scene scene = new Scene(root, 1400, 700);

        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/main_view.css")).toExternalForm());

        stage.setTitle("Test");
        stage.setScene(scene);
        stage.show();
    }

    private void updateCenterPane(Part partDetails)
    {
        centerPane.getChildren().clear();

        Label nameLabel = createBoldLabel("Name: ", partDetails.getName());
        Label descriptionLabel = createBoldLabel("Description: ", partDetails.getDescription());
        Label priceLabel = createBoldLabel("Price: $", String.valueOf(partDetails.getPrice()));
        Label stockLabel = createBoldLabel("In Stock: ", String.valueOf(partDetails.getQuantityInStock()));
        Label soldLabel = createBoldLabel("Sold: ", String.valueOf(partDetails.getQuantitySold()));

        ImageView imageView = new ImageView(new Image(partDetails.getImageUrl()));
        imageView.setFitWidth(300);
        imageView.setPreserveRatio(true);

        VBox textBox = new VBox(5, nameLabel, descriptionLabel, priceLabel, stockLabel, soldLabel);
        textBox.setAlignment(Pos.TOP_LEFT);
        textBox.setMaxWidth(400);

        HBox imageBox = new HBox(imageView);
        imageBox.setAlignment(Pos.BOTTOM_LEFT);

        VBox mainBox = new VBox(10, textBox, imageBox);
        mainBox.setAlignment(Pos.TOP_LEFT);
        mainBox.setPadding(new Insets(10));

        centerPane.getChildren().add(mainBox);
    }

    private Label createBoldLabel(String labelText, String valueText)
    {
        Label label = new Label();
        label.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

        Text boldText = new Text(labelText);
        boldText.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Text normalText = new Text(valueText);
        normalText.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

        TextFlow textFlow = new TextFlow(boldText, normalText);
        textFlow.setMaxWidth(350);

        label.setGraphic(textFlow);
        return label;

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