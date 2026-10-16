package com.coffeeshop;

import java.util.ArrayList;

public class Cart {

    public static ArrayList<CartItem> items = new ArrayList<>();

    public static void addItem(String name, double price) {

        for (CartItem item : items) {

            if (item.getName().equals(name)) {
                item.increaseQuantity();
                return;
            }
        }

        items.add(new CartItem(name, price));
    }

    public static void increaseQuantity(int index) {
        if (index >= 0 && index < items.size()) {
            items.get(index).increaseQuantity();
        }
    }

    public static void decreaseQuantity(int index) {
        if (index < 0 || index >= items.size()) {
            return;
        }

        CartItem item = items.get(index);

        if (item.getQuantity() <= 1) {
            items.remove(index);
        } else {
            item.decreaseQuantity();
        }
    }

    public static void removeItem(int index) {

        if (index >= 0 && index < items.size()) {
            items.remove(index);
        }
    }

    public static double getTotal() {

        double total = 0;

        for (CartItem item : items) {
            total += item.getTotal();
        }

        return total;
    }

    public static void clearCart() {
        items.clear();
    }
}
