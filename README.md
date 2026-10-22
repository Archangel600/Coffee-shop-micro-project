# Coffee Billing System

A Java Swing coffee shop ordering and billing application built with NetBeans/Ant and MySQL JDBC.

## Requirements

- JDK 26 (configured in `nbproject/project.properties`)
- Apache NetBeans with Java SE and Ant support
- MySQL Server 8.0+ (MySQL Workbench is optional)
- MySQL Connector/J JDBC driver

## One-time setup on each computer

1. Install and start MySQL Server. In MySQL Workbench, open `database/schema.sql` and execute the complete script using a MySQL administrator account. This creates the `coffee_shop` database, four tables and the 24 existing menu products. Running the script again will not delete orders.
2. Recommended: create a local database account in MySQL with:
   ```sql
   CREATE USER IF NOT EXISTS 'coffee_app'@'localhost' IDENTIFIED BY 'your_password';
   GRANT SELECT, INSERT, UPDATE ON coffee_shop.* TO 'coffee_app'@'localhost';
   ```
   Change `your_password` to a password you choose. Alternatively, use an existing local MySQL account.
3. Copy `db.properties.example` to `db.properties` in the project root. Set `db.user` and `db.password` to your MySQL credentials. For a non-default server, also edit `db.url`.
4. Download **MySQL Connector/J** from [MySQL's official Connector/J download page](https://dev.mysql.com/downloads/connector/j/). Extract its JAR, create a `lib` directory under the project root, and copy/rename the JAR to `lib/mysql-connector-j.jar` (exact filename).
5. Open the project folder containing `build.xml` in NetBeans, select JDK 26, then **Clean and Build** → **Run**. The main class is `com.coffeeshop.MenuFrame`.

`db.properties` (credentials) and `lib/*.jar` are intentionally ignored by Git. Each machine running the project needs access to MySQL, the JDBC driver, and its own configuration file. Run the application with the project folder as its working directory.

## Existing application flow

`Menu → Cart → Customer Registration → Bill → New Order`

- Menu categories, products and prices load from MySQL.
- The cart remains in memory while an order is being prepared.
- Enter a 10-digit phone number and press **CONTINUE** to save the customer, order, line items and loyalty count in one JDBC transaction.
- The bill appears only if checkout succeeds; a database error keeps the cart intact and allows retry.
- Every fifth completed order receives **30% off**, then its loyalty cycle starts again.
- Completed orders and loyalty counts persist across restarts.

## Database tables

- `menu_items`: available products, categories, prices and their display order
- `customers`: unique phone number and loyalty progress (0–4)
- `orders`: customer, totals, discount and order timestamp
- `order_items`: quantities and historical purchase prices

The previous `customers.txt` file is no longer used. Existing local loyalty records do **not** migrate automatically; migrate them separately if there is important historical data.

## Source layout

- `src/com/coffeeshop/MenuFrame.java`: database-backed menu and existing Swing layout
- `src/com/coffeeshop/MenuDAO.java`: JDBC menu queries
- `src/com/coffeeshop/DatabaseConnection.java`: JDBC connection configuration
- `src/com/coffeeshop/OrderService.java`: transactional checkout and loyalty logic
- `src/com/coffeeshop/Cart.java`, `CartItem.java`, `CartFrame.java`: in-memory shopping cart
- `src/com/coffeeshop/CustomerFrame.java`: checkout action
- `src/com/coffeeshop/BillFrame.java`: displays the saved order
- `database/schema.sql`: MySQL schema and initial menu data

The existing NetBeans `.form` files are preserved for GUI Builder editing.
