package ru.cloudpayments.demo.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Product {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("price")
    private String price;

    @SerializedName("images")
    private List<Image> images;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getImageUrl() {
        return images.get(0).imageUrl;
    }

    public class Image {

        @SerializedName("src")
        private String imageUrl;
    }
}
