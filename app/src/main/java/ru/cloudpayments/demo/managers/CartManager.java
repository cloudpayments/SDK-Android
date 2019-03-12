package ru.cloudpayments.demo.managers;

import java.util.ArrayList;

import ru.cloudpayments.demo.models.Product;

public class CartManager {

    private static CartManager instance;

    private ArrayList<Product> products;

    private CartManager() {

        clear();
    }

    public static synchronized CartManager getInstance(){
        if (instance == null) {
            synchronized (CartManager.class) {
                instance = new CartManager();
            }
        }
        return instance;
    }

    public void destroy() {

        products = null;
        instance = null;
    }

    public void clear() {

        products = new ArrayList<>();
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }
}
