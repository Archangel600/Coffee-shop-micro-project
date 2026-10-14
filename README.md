# Coffee Billing System

A Java Swing desktop application for a coffee shop ordering and billing workflow, built as an Apache NetBeans project.

## Features

- Menu-based ordering across coffee, tea, snacks, soft drinks, lime drinks, ice cream, burgers, and desserts.
- Shopping cart with quantity aggregation, item removal, and total calculation.
- Customer phone-number validation before checkout.
- Bill generation with item quantities, subtotal, loyalty discount, and final amount.
- Simple loyalty system that awards **30% off on every 5th order**.
- Local persistence of loyalty order counts in `customers.txt`.

## Project structure

```text
.
├── src/com/coffeeshop/
│   ├── MenuFrame.java
│   ├── MenuFrame.form
│   ├── Cart.java
│   ├── CartItem.java
│   ├── CartFrame.java
│   ├── CartFrame.form
│   ├── CustomerFrame.java
│   ├── CustomerFrame.form
│   ├── LoyaltyManager.java
│   ├── BillFrame.java
│   └── BillFrame.form
├── nbproject/
├── build.xml
├── manifest.mf
├── .gitignore
└── README.md
```

## Requirements

- **JDK 26** (the NetBeans project is configured with source/target level 26)
- Apache NetBeans with Java SE support
- Apache Ant (normally provided/integrated by NetBeans)

## Run in NetBeans

1. Clone or download this repository.
2. In Apache NetBeans, choose **File → Open Project**.
3. Select the repository folder containing `build.xml`.
4. Make sure the project uses **JDK 26**.
5. Choose **Clean and Build**, then **Run**.

The configured main class is:

```text
com.coffeeshop.MenuFrame
```

## Application flow

```text
Menu → Cart → Customer Registration → Bill → New Order
```

The loyalty manager stores completed-order counts locally. On a customer's 5th order, the billing screen applies a 30% discount and resets the loyalty cycle for subsequent orders.

## Repository notes

- NetBeans GUI Builder `.form` files are included so the Swing forms remain editable in NetBeans.
- Generated `build/` and `dist/` directories are ignored because they can be regenerated.
- `nbproject/private/` is ignored because it contains machine-specific NetBeans settings.
- `customers.txt` is ignored because it is runtime data and may contain customer phone numbers.
