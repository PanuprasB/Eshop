package com.example.kursinis.model;

import lombok.AllArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Setter


public class Cart {
    private List<Product> itemsInCart;
    private LocalDate dateCartCreated;
    private double total;

    public Cart() {
        this.itemsInCart = new ArrayList<>();
        this.dateCartCreated = LocalDate.now();
        this.total = 0.0;
    }



    public void addProduct(Product product) {
        // Check if the product is already in the cart
        for (Product cartItem : itemsInCart) {
            if (cartItem.getId() == product.getId()) {
                // Increase the quantity of the existing product
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                total += product.getPrice()*cartItem.getQuantity();
                return;
            }
        }
        // If product is not in the cart, add it
        product.setQuantity(1);
        itemsInCart.add(product);
        total += product.getPrice();
    }

    public void removeProduct(Product product) {
        itemsInCart.remove(product);
        total -= product.getPrice();
    }

    public List<Product> getItemsInCart() {
        return itemsInCart;
    }

    public double getTotal() {
        return total;
    }
}
