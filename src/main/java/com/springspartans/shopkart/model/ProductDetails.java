package com.springspartans.shopkart.model;

public class ProductDetails {

    private String name;
    private String category;
    private String brand;
    private double price;
    private String image;
    private int stock;
    private double discount;

    public ProductDetails(String name, String category, String brand, double price, String image, int stock, double discount) {
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.price = price;
        this.image = image;
        this.stock = stock;
        this.discount = discount;
    }

    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getBrand() { return brand; }
    public double getPrice() { return price; }
    public String getImage() { return image; }
    public int getStock() { return stock; }
    public double getDiscount() { return discount; }
}
