package com.coffeeshop;

import java.sql.SQLException;

/** Checks credentials and creates the schema before launching the Swing UI. */
public final class DatabaseCheck {
    private DatabaseCheck() {
    }

    public static void main(String[] args) {
        try {
            DatabaseConnection.initializeDatabase();
            System.out.println("MySQL connection and coffee_shop schema verified.");
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
            System.exit(2);
        }
    }
}
