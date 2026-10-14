package com.coffeeshop;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Main ordering screen with a responsive menu grid.
 */
public class MenuFrame extends JFrame {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(MenuFrame.class.getName());

    private final JPanel menuGrid = new JPanel();

    public MenuFrame() {
        setTitle("Coffee Billing System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(680, 560));

        buildUi();
        setPreferredSize(new Dimension(820, 650));
        pack();
        setLocationRelativeTo(null);
    }

    private void buildUi() {
        JPanel root = new JPanel(new BorderLayout(16, 16));
        root.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        setContentPane(root);

        JLabel title = new JLabel("COFFEE SHOP ORDERING SYSTEM", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        root.add(title, BorderLayout.NORTH);

        menuGrid.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        root.add(menuGrid, BorderLayout.CENTER);

        addCategory("SNACKS",
                item("Samosa", 20),
                item("Sandwich", 60),
                item("French Fries", 70));

        addCategory("COFFEE",
                item("Espresso", 80),
                item("Cappuccino", 100),
                item("Latte", 110));

        addCategory("TEA",
                item("Black Tea", 30),
                item("Green Tea", 40),
                item("Milk Tea", 40));

        addCategory("LIME DRINK",
                item("Lime Juice", 40),
                item("Mint Lime", 50),
                item("Fresh Lime", 40));

        addCategory("SOFT DRINKS",
                item("Coke", 50),
                item("Sprite", 50),
                item("Pepsi", 50));

        addCategory("ICE CREAM",
                item("Vanilla Ice Cream", 60),
                item("Chocolate Ice Cream", 70),
                item("Strawberry Ice Cream", 70));

        addCategory("BURGERS",
                item("Veg Burger", 90),
                item("Cheese Burger", 120),
                item("Chicken Burger", 150));

        addCategory("DESSERTS",
                item("Brownie", 80),
                item("Cake", 90),
                item("Donut", 50));

        JButton viewCart = new JButton("VIEW CART");
        viewCart.addActionListener(e -> WindowNavigator.open(this, new CartFrame()));

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        footer.add(viewCart);
        root.add(footer, BorderLayout.SOUTH);

        updateColumns();
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateColumns();
            }
        });
    }

    private void addCategory(String titleText, MenuItem... items) {
        JPanel category = new JPanel(new BorderLayout(8, 8));
        category.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JLabel title = new JLabel(titleText, SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD));
        category.add(title, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(items.length, 1, 6, 6));
        for (MenuItem menuItem : items) {
            JButton button = new JButton(menuItem.name());
            button.addActionListener(e -> addToCart(menuItem));
            buttons.add(button);
        }

        category.add(buttons, BorderLayout.CENTER);
        menuGrid.add(category);
    }

    private void addToCart(MenuItem menuItem) {
        Cart.addItem(menuItem.name(), menuItem.price());
        JOptionPane.showMessageDialog(this, menuItem.name() + " added to cart!");
    }

    private void updateColumns() {
        int width = Math.max(getContentPane().getWidth(), getWidth());
        int columns = width >= 1100 ? 4 : width >= 760 ? 3 : 2;

        if (!(menuGrid.getLayout() instanceof GridLayout grid)
                || grid.getColumns() != columns) {
            menuGrid.setLayout(new GridLayout(0, columns, 16, 16));
            menuGrid.revalidate();
            menuGrid.repaint();
        }
    }

    private static MenuItem item(String name, double price) {
        return new MenuItem(name, price);
    }

    private record MenuItem(String name, double price) {
    }

    public static void main(String[] args) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> new MenuFrame().setVisible(true));
    }
}
