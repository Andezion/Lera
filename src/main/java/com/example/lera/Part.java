package com.example.lera;

public class Part
{
    private String name;
    private String description;
    private String imageUrl;
    private double price;
    private int quantityInStock;
    private int quantitySold;

    public Part(String name, String description, String imageUrl, double price, int quantityInStock, int quantitySold)
    {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.quantityInStock = quantityInStock;
        this.quantitySold = quantitySold;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public double getPrice() { return price; }
    public int getQuantityInStock() { return quantityInStock; }
    public int getQuantitySold() { return quantitySold; }
}
