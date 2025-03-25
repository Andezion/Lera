package com.example.lera;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainView extends Application
{
    private VBox leftPane;
    private VBox centerPane;
    int pointer_for_button = 0;

    @Override
    public void start(Stage stage)
    {
        HBox root = new HBox();

        leftPane = createPane("Categories", Color.LIGHTGRAY, 300);
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

        user_button.setOnAction(e -> loadUsers());
        order_button.setOnAction(event -> loadDatabase());
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

    private void showClientDetails(int clientId)
    {
        String URL = "jdbc:postgresql://localhost:5432/postgres";
        String USER = "postgres";
        String PASSWORD = "Ffdss321!";

        String query = "SELECT c.id AS client_id, c.first_name, c.last_name, " +
                "o.id AS order_id, o.total_amount " +
                "FROM clients c " +
                "JOIN client_orders co ON c.id = co.client_id " +
                "JOIN orders o ON co.order_id = o.id " +
                "WHERE c.id = ? " +
                "ORDER BY o.order_date DESC;";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query,
                     ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY))
        {

            pstmt.setInt(1, clientId);
            ResultSet rs = pstmt.executeQuery();

            StringBuilder orderList = new StringBuilder();
            double totalSum = 0;

            if (rs.first())
            {
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");

                rs.beforeFirst();
                while (rs.next())
                {
                    String orderId = rs.getString("order_id");
                    String totalAmount = rs.getString("total_amount");
                    orderList.append(orderId).append("\n");
                    totalSum += Double.parseDouble(totalAmount);
                }

                VBox clientDetails = new VBox();
                clientDetails.setPadding(new Insets(10));
                clientDetails.setSpacing(10);

                Label clientIdLabel = new Label();
                Text clientIdText = new Text("Client ID: ");
                clientIdText.setStyle("-fx-font-weight: bold;");
                Text clientIdValueText = new Text(String.valueOf(clientId));
                clientIdValueText.setStyle("-fx-font-weight: normal;");

                clientIdLabel.setGraphic(new HBox(clientIdText, clientIdValueText));


                Label nameLabel = new Label();
                Text nameText = new Text("Name: ");
                nameText.setStyle("-fx-font-weight: bold;");
                Text nameValueText = new Text(firstName);
                nameValueText.setStyle("-fx-font-weight: normal;");

                nameLabel.setGraphic(new HBox(nameText, nameValueText));


                Label lastNameLabel = new Label();
                Text lastnameText = new Text("Name: ");
                lastnameText.setStyle("-fx-font-weight: bold;");
                Text lastnameValueText = new Text(lastName);
                lastnameValueText.setStyle("-fx-font-weight: normal;");

                lastNameLabel.setGraphic(new HBox(lastnameText, lastnameValueText));


                Label orderListLabel = new Label();
                Text orderListText = new Text("Orders: ");
                orderListText.setStyle("-fx-font-weight: bold;");
                Text orderListValueText = new Text(orderList.toString());
                orderListValueText.setStyle("-fx-font-weight: normal;");

                orderListLabel.setGraphic(new HBox(orderListText, orderListValueText));


                Label totalAmountLabel = new Label();
                Text totalAmountText = new Text("Total: ");
                totalAmountText.setStyle("-fx-font-weight: bold;");
                Text totalAmountValueText = new Text(String.valueOf(totalSum));
                totalAmountValueText.setStyle("-fx-font-weight: normal;");

                totalAmountLabel.setGraphic(new HBox(totalAmountText, totalAmountValueText));

                clientDetails.getChildren().add(clientIdLabel);
                clientDetails.getChildren().add(nameLabel);
                clientDetails.getChildren().add(lastNameLabel);
                clientDetails.getChildren().add(orderListLabel);
                clientDetails.getChildren().add(totalAmountLabel);

                leftPane.getChildren().clear();
                leftPane.getChildren().add(clientDetails);
            }

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    private void loadUsers()
    {
        centerPane.getChildren().clear();

        String URL = "jdbc:postgresql://localhost:5432/postgres";
        String USER = "postgres";
        String PASSWORD = "Ffdss321!";

        String query = "SELECT c.id AS client_id, c.first_name, c.last_name, " +
                "o.id AS order_id, o.order_date, o.total_amount " +
                "FROM clients c " +
                "JOIN client_orders co ON c.id = co.client_id " +
                "JOIN orders o ON co.order_id = o.id " +
                "ORDER BY o.order_date DESC;";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query))
        {

            GridPane grid = new GridPane();
            grid.setPadding(new Insets(10));
            grid.setHgap(1);
            grid.setVgap(1);
            grid.setAlignment(Pos.CENTER);
            grid.setStyle("-fx-grid-lines-visible: true; -fx-border-color: black; -fx-border-width: 1px;");

            String[] headers = {"ID", "First Name", "Last Name", "Order ID", "Order Date", "Total Amount"};
            for (int col = 0; col < headers.length; col++)
            {
                Label headerLabel = new Label(headers[col]);
                headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width: 1px; -fx-padding: 5px;");
                headerLabel.setAlignment(Pos.CENTER);
                headerLabel.setMaxWidth(Double.MAX_VALUE);
                GridPane.setHalignment(headerLabel, HPos.CENTER);
                grid.add(headerLabel, col, 0);
            }

            int row = 1;
            while (rs.next())
            {
                int clientId = rs.getInt("client_id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                int orderId = rs.getInt("order_id");
                String orderDate = rs.getString("order_date");
                String totalAmount = rs.getString("total_amount");

                Label clientIdLabel = new Label(String.valueOf(clientId));
                Label firstNameLabel = new Label(firstName);
                Label lastNameLabel = new Label(lastName);
                Label orderIdLabel = new Label(String.valueOf(orderId));
                Label orderDateLabel = new Label(orderDate);
                Label totalAmountLabel = new Label(totalAmount);

                clientIdLabel.setOnMouseClicked(event -> showClientDetails(clientId)); // !

                clientIdLabel.setAlignment(Pos.CENTER);
                firstNameLabel.setAlignment(Pos.CENTER);
                lastNameLabel.setAlignment(Pos.CENTER);
                orderIdLabel.setAlignment(Pos.CENTER);
                orderDateLabel.setAlignment(Pos.CENTER);
                totalAmountLabel.setAlignment(Pos.CENTER_LEFT);

                grid.add(clientIdLabel, 0, row);
                grid.add(firstNameLabel, 1, row);
                grid.add(lastNameLabel, 2, row);
                grid.add(orderIdLabel, 3, row);
                grid.add(orderDateLabel, 4, row);
                grid.add(totalAmountLabel, 5, row);

                row++;
            }

            ColumnConstraints col1 = new ColumnConstraints(80);
            ColumnConstraints col2 = new ColumnConstraints(150, 200, Double.MAX_VALUE);
            ColumnConstraints col3 = new ColumnConstraints(150, 200, Double.MAX_VALUE);
            ColumnConstraints col4 = new ColumnConstraints(100, 150, Double.MAX_VALUE);
            ColumnConstraints col5 = new ColumnConstraints(150, 200, Double.MAX_VALUE);
            ColumnConstraints col6 = new ColumnConstraints(150, 200, Double.MAX_VALUE);

            col2.setHalignment(HPos.CENTER);
            col3.setHalignment(HPos.CENTER);
            col4.setHalignment(HPos.CENTER);
            col5.setHalignment(HPos.CENTER);
            col6.setHalignment(HPos.CENTER);

            grid.getColumnConstraints().addAll(col1, col2, col3, col4, col5, col6);

            ScrollPane scrollPane = new ScrollPane(grid);
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefHeight(centerPane.getHeight());

            centerPane.getChildren().add(scrollPane);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    private void loadDatabase()
    {
        centerPane.getChildren().clear();

        String URL = "jdbc:postgresql://localhost:5432/postgres";
        String USER = "postgres";
        String PASSWORD = "Ffdss321!";

        String query = "SELECT o.id, o.order_date, o.total_amount, " +
                "STRING_AGG(p.name, ', ') AS parts_list " +
                "FROM orders o " +
                "JOIN order_parts op ON o.id = op.order_id " +
                "JOIN warehouse w ON op.warehouse_id = w.id " +
                "JOIN parts p ON w.part_id = p.id " +
                "GROUP BY o.id, o.order_date, o.total_amount " +
                "ORDER BY o.id;";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query))
        {

            GridPane grid = new GridPane();
            grid.setPadding(new Insets(10));
            grid.setHgap(1);
            grid.setVgap(1);
            grid.setAlignment(Pos.CENTER);
            grid.setStyle("-fx-grid-lines-visible: true; -fx-border-color: black; -fx-border-width: 1px;");

            String[] headers = {"ID", "Date of Order", "Sum of Order", "Details of Order"};
            for (int col = 0; col < headers.length; col++)
            {
                Label headerLabel = new Label(headers[col]);
                headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width: 1px; -fx-padding: 5px;");
                headerLabel.setAlignment(Pos.CENTER);
                headerLabel.setMaxWidth(Double.MAX_VALUE);
                GridPane.setHalignment(headerLabel, HPos.CENTER);
                grid.add(headerLabel, col, 0);
            }

            int row = 1;
            while (rs.next())
            {
                int orderId = rs.getInt("id");
                String orderDate = rs.getString("order_date");
                String totalAmount = rs.getString("total_amount");
                String partsList = rs.getString("parts_list");

                Label orderIdLabel = new Label(String.valueOf(orderId));
                Label orderDateLabel = new Label(orderDate);
                Label totalAmountLabel = new Label(totalAmount);
                Label partsListLabel = new Label(partsList);

                orderIdLabel.setAlignment(Pos.CENTER);
                orderDateLabel.setAlignment(Pos.CENTER);
                totalAmountLabel.setAlignment(Pos.CENTER_LEFT);
                partsListLabel.setAlignment(Pos.CENTER_LEFT);

                grid.add(orderIdLabel, 0, row);
                grid.add(orderDateLabel, 1, row);
                grid.add(totalAmountLabel, 2, row);
                grid.add(partsListLabel, 3, row);

                row++;
            }

            ColumnConstraints col1 = new ColumnConstraints(80);
            ColumnConstraints col2 = new ColumnConstraints(150, 200, Double.MAX_VALUE);
            ColumnConstraints col3 = new ColumnConstraints(150, 200, Double.MAX_VALUE);
            ColumnConstraints col4 = new ColumnConstraints(300, 500, Double.MAX_VALUE);

            col2.setHalignment(HPos.CENTER);
            col3.setHalignment(HPos.CENTER);
            col4.setHgrow(Priority.ALWAYS);

            grid.getColumnConstraints().addAll(col1, col2, col3, col4);

            ScrollPane scrollPane = new ScrollPane(grid);
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefHeight(centerPane.getHeight());

            centerPane.getChildren().add(scrollPane);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
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