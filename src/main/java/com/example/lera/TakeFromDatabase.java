package com.example.lera;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TakeFromDatabase
{
    public static List<String> getCategories() throws SQLException
    {
        final String URL = "jdbc:postgresql://localhost:5432/postgres";
        final String USER = "postgres";
        final String PASSWORD = "Ffdss321!";

        List<String> categories = new ArrayList<>();

        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT name FROM categories");

        while (rs.next())
        {
            categories.add(rs.getString("name"));
        }

        conn.close();
        return categories;
    }

    public static List<String> getProducers(String category) throws SQLException
    {
        final String URL = "jdbc:postgresql://localhost:5432/postgres";
        final String USER = "postgres";
        final String PASSWORD = "Ffdss321!";

        List<String> producers = new ArrayList<>();

        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        PreparedStatement stmt = conn.prepareStatement(
                "SELECT p.name FROM producers p JOIN categories c ON p.category_id = c.id WHERE c.name = ?");

        stmt.setString(1, category);
        ResultSet rs = stmt.executeQuery();

        while (rs.next())
        {
            producers.add(rs.getString("name"));
        }

        conn.close();
        return producers;
    }

    public static List<Part> getParts(String product) throws SQLException
    {
        final String URL = "jdbc:postgresql://localhost:5432/postgres";
        final String USER = "postgres";
        final String PASSWORD = "Ffdss321!";

        List<Part> parts = new ArrayList<>();

        System.out.println("Ищу детали с именем: " + product);

        String query = "SELECT p.name, p.description, p.image_url, w.price, w.quantity_in_stock, w.quantity_sold " +
                "FROM parts p " +
                "JOIN producers pr ON p.producer_id = pr.id " +
                "JOIN warehouse w ON p.id = w.part_id " +
                "WHERE pr.name = ?";

        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        PreparedStatement stmt = conn.prepareStatement(query);

        stmt.setString(1, product);
        ResultSet rs = stmt.executeQuery();

        while (rs.next())
        {
            Part part = new Part(
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getString("image_url"),
                    rs.getDouble("price"),
                    rs.getInt("quantity_in_stock"),
                    rs.getInt("quantity_sold")
            );

            parts.add(part);
        }

        conn.close();

        return parts;
    }

    public static Part getPartByName(String partName) throws SQLException
    {
        final String URL = "jdbc:postgresql://localhost:5432/postgres";
        final String USER = "postgres";
        final String PASSWORD = "Ffdss321!";

        String query = "SELECT p.name, p.description, p.image_url, w.price, w.quantity_in_stock, w.quantity_sold " +
                "FROM parts p " +
                "JOIN warehouse w ON p.id = w.part_id " +
                "WHERE p.name = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, partName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next())
            {
                return new Part(
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("image_url"),
                        rs.getDouble("price"),
                        rs.getInt("quantity_in_stock"),
                        rs.getInt("quantity_sold")
                );
            }
        }

        return null; // Если деталь не найдена
    }
}