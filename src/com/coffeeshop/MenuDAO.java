package com.coffeeshop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Retrieves the existing menu layout and prices from MySQL. */
final class MenuDAO {
    private MenuDAO() {
    }

    record MenuItem(String category, String name, double price) {
    }

    static List<MenuItem> loadMenu() throws SQLException {
        DatabaseConnection.initializeDatabase();
        String sql = "SELECT category, name, price FROM menu_items ORDER BY display_order, item_id";
        List<MenuItem> menu = new ArrayList<>();
        try (Connection connection = DatabaseConnection.open();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                menu.add(new MenuItem(result.getString("category"),
                        result.getString("name"), result.getDouble("price")));
            }
        }
        if (menu.isEmpty()) {
            throw new SQLException("Menu is empty. Check the MySQL menu_items table.");
        }
        return menu;
    }
}
