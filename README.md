# Coffee Billing System

A Java Swing coffee-shop ordering and billing project using **MySQL JDBC**.
The original NetBeans/Ant project and existing Menu → Cart → Customer → Bill flow are preserved.

## Run after a fresh clone (Windows / VS Code)

**Only prerequisites:** MySQL Server installed locally and a JDK (17+ for this launcher).
You do **not** need to install MySQL Connector/J yourself or execute SQL in Workbench.

1. Install MySQL Server if it is not already installed. Know the MySQL username/password (typically `root` and the password you chose when installing MySQL).
2. From the repository root, double-click `run.bat` or run `.\run.bat` in a Windows PowerShell terminal.
3. On first launch, enter the MySQL user/password at the prompts. The password is hidden. If incorrect, you can retry.
4. The launcher automatically downloads **MySQL Connector/J 9.7.0** into `lib/mysql-connector-j.jar`, compiles the Java sources, verifies the MySQL login, initializes the `coffee_shop` database, four tables, and original 24 menu products, then opens the app.
5. On future launches the driver is reused. Credentials are stored in Git-ignored `.coffee-db.json` encrypted for the current Windows user.

Internet is needed for the first JDBC-driver download. MySQL Server itself must still be installed; the launcher can start a stopped local MySQL Windows service (it may request administrator permission) and will stop only services it started when the app closes.

Change saved credentials using `.\run.bat -ResetDbConfig`.

The launcher accepts optional `COFFEE_DB_USER`, `COFFEE_DB_PASSWORD`, `COFFEE_DB_URL`, and `COFFEE_DB_SERVER_URL` environment variables, which override saved/default settings. By default, the connection is `localhost:3306` and database name `coffee_shop`. The database login must have permission to create the schema/tables on first use (a local MySQL `root` login works for a classroom demonstration).

## NetBeans / Ant

The existing `build.xml`, `nbproject/`, and `.form` files are unchanged. Its configured Java language level remains **JDK 26**.

To use NetBeans:
1. First run `run.bat` at least once to download `lib/mysql-connector-j.jar`.
2. Since NetBeans Run does not inherit the launcher's temporary environment variables, copy `db.properties.example` to `db.properties` and enter your local credentials. This file is Git-ignored.
3. Open the project folder in NetBeans and **Clean and Build → Run** with JDK 26.

Alternatively, use `run.bat` for routine launches from the project root without creating `db.properties`.

## Data

- `menu_items`: the original categories and 24 product prices, read from MySQL.
- `customers`: phone number and progress toward the next fifth-order offer.
- `orders`: completed bills and discounts.
- `order_items`: product names, prices, and quantities at purchase.

Pressing **CONTINUE** after entering a valid customer number commits the entire order, its lines, and the loyalty update in one JDBC transaction. The bill is shown only after a successful save. The 5th completed order receives 30% off. An unsuccessful checkout keeps the current cart. The previous `customers.txt` file is no longer used and is not automatically migrated.

`database/schema.sql` remains the canonical idempotent schema and menu seed, now executed automatically through JDBC. No unrelated screens or functionality were changed.

## Compile-only launcher

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\build.ps1
```
