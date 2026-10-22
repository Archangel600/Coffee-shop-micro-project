CREATE DATABASE IF NOT EXISTS coffee_shop
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE coffee_shop;

CREATE TABLE IF NOT EXISTS customers (
    customer_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    phone VARCHAR(10) NOT NULL UNIQUE,
    loyalty_count TINYINT UNSIGNED NOT NULL DEFAULT 0
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS menu_items (
    item_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    category VARCHAR(40) NOT NULL,
    name VARCHAR(100) NOT NULL UNIQUE,
    price DECIMAL(10,2) NOT NULL,
    display_order INT NOT NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS orders (
    order_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    discount DECIMAL(10,2) NOT NULL DEFAULT 0,
    final_amount DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_orders_customer FOREIGN KEY (customer_id)
        REFERENCES customers(customer_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS order_items (
    order_item_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_name VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    line_total DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id)
        REFERENCES orders(order_id)
) ENGINE=InnoDB;

-- INSERT IGNORE makes re-running this setup safe for existing menu prices.
INSERT IGNORE INTO menu_items (category, name, price, display_order) VALUES
('SNACKS', 'Samosa', 20.00, 1),
('SNACKS', 'Sandwich', 60.00, 2),
('SNACKS', 'French Fries', 70.00, 3),
('COFFEE', 'Espresso', 80.00, 4),
('COFFEE', 'Cappuccino', 100.00, 5),
('COFFEE', 'Latte', 110.00, 6),
('TEA', 'Black Tea', 30.00, 7),
('TEA', 'Green Tea', 40.00, 8),
('TEA', 'Milk Tea', 40.00, 9),
('LIME DRINK', 'Lime Juice', 40.00, 10),
('LIME DRINK', 'Mint Lime', 50.00, 11),
('LIME DRINK', 'Fresh Lime', 40.00, 12),
('SOFT DRINKS', 'Coke', 50.00, 13),
('SOFT DRINKS', 'Sprite', 50.00, 14),
('SOFT DRINKS', 'Pepsi', 50.00, 15),
('ICE CREAM', 'Vanilla Ice Cream', 60.00, 16),
('ICE CREAM', 'Chocolate Ice Cream', 70.00, 17),
('ICE CREAM', 'Strawberry Ice Cream', 70.00, 18),
('BURGERS', 'Veg Burger', 90.00, 19),
('BURGERS', 'Cheese Burger', 120.00, 20),
('BURGERS', 'Chicken Burger', 150.00, 21),
('DESSERTS', 'Brownie', 80.00, 22),
('DESSERTS', 'Cake', 90.00, 23),
('DESSERTS', 'Donut', 50.00, 24);
