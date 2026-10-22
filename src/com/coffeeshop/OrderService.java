package com.coffeeshop;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Saves an entire checkout (customer, bill, items and loyalty) atomically. */
final class OrderService {
    private static final BigDecimal LOYALTY_DISCOUNT = new BigDecimal("0.30");

    private OrderService() {
    }

    record BillLine(String name, int quantity, BigDecimal total) {
    }

    record Receipt(long orderId, String phone, BigDecimal subtotal,
                   BigDecimal discount, BigDecimal finalAmount,
                   boolean redeemed, List<BillLine> lines) {
    }

    static Receipt checkout(String phone, List<CartItem> cart) throws SQLException {
        if (phone == null || !phone.matches("\\d{10}")) {
            throw new IllegalArgumentException("A valid 10-digit phone number is required.");
        }
        if (cart == null || cart.isEmpty()) {
            throw new IllegalArgumentException("The cart is empty.");
        }

        List<BillLine> lines = new ArrayList<>();
        BigDecimal subtotal = money(BigDecimal.ZERO);
        for (CartItem item : cart) {
            if (item.getQuantity() <= 0 || !Double.isFinite(item.getPrice())
                    || item.getPrice() < 0) {
                throw new IllegalArgumentException("Cart contains an invalid item.");
            }
            BigDecimal price = money(BigDecimal.valueOf(item.getPrice()));
            BigDecimal total = money(price.multiply(BigDecimal.valueOf(item.getQuantity())));
            lines.add(new BillLine(item.getName(), item.getQuantity(), total));
            subtotal = subtotal.add(total);
        }
        subtotal = money(subtotal);

        try (Connection connection = DatabaseConnection.open()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement insertCustomer = connection.prepareStatement(
                        "INSERT INTO customers (phone, loyalty_count) VALUES (?, 0) "
                        + "ON DUPLICATE KEY UPDATE phone = phone")) {
                    insertCustomer.setString(1, phone);
                    insertCustomer.executeUpdate();
                }

                long customerId;
                int loyaltyCount;
                try (PreparedStatement findCustomer = connection.prepareStatement(
                        "SELECT customer_id, loyalty_count FROM customers "
                        + "WHERE phone = ? FOR UPDATE")) {
                    findCustomer.setString(1, phone);
                    try (ResultSet customer = findCustomer.executeQuery()) {
                        if (!customer.next()) {
                            throw new SQLException("Customer could not be loaded.");
                        }
                        customerId = customer.getLong("customer_id");
                        loyaltyCount = customer.getInt("loyalty_count");
                    }
                }

                boolean redeemed = loyaltyCount == 4;
                BigDecimal discount = redeemed
                        ? money(subtotal.multiply(LOYALTY_DISCOUNT))
                        : money(BigDecimal.ZERO);
                BigDecimal finalAmount = subtotal.subtract(discount);

                long orderId;
                try (PreparedStatement insertOrder = connection.prepareStatement(
                        "INSERT INTO orders (customer_id, subtotal, discount, final_amount) "
                        + "VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                    insertOrder.setLong(1, customerId);
                    insertOrder.setBigDecimal(2, subtotal);
                    insertOrder.setBigDecimal(3, discount);
                    insertOrder.setBigDecimal(4, finalAmount);
                    insertOrder.executeUpdate();
                    try (ResultSet keys = insertOrder.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("Could not generate the order ID.");
                        }
                        orderId = keys.getLong(1);
                    }
                }

                try (PreparedStatement insertItem = connection.prepareStatement(
                        "INSERT INTO order_items (order_id, product_name, quantity, "
                        + "unit_price, line_total) VALUES (?, ?, ?, ?, ?)")) {
                    for (CartItem item : cart) {
                        BigDecimal unitPrice = money(BigDecimal.valueOf(item.getPrice()));
                        insertItem.setLong(1, orderId);
                        insertItem.setString(2, item.getName());
                        insertItem.setInt(3, item.getQuantity());
                        insertItem.setBigDecimal(4, unitPrice);
                        insertItem.setBigDecimal(5, money(unitPrice.multiply(
                                BigDecimal.valueOf(item.getQuantity()))));
                        insertItem.addBatch();
                    }
                    insertItem.executeBatch();
                }

                try (PreparedStatement updateLoyalty = connection.prepareStatement(
                        "UPDATE customers SET loyalty_count = ? WHERE customer_id = ?")) {
                    updateLoyalty.setInt(1, redeemed ? 0 : loyaltyCount + 1);
                    updateLoyalty.setLong(2, customerId);
                    if (updateLoyalty.executeUpdate() != 1) {
                        throw new SQLException("Failed to update the loyalty count.");
                    }
                }

                connection.commit();
                return new Receipt(orderId, phone, subtotal, discount, finalAmount,
                        redeemed, List.copyOf(lines));
            } catch (SQLException | RuntimeException exception) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackFailure) {
                    exception.addSuppressed(rollbackFailure);
                }
                throw exception;
            }
        }
    }

    private static BigDecimal money(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }
}
