package com.coffeeshop;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

        try {
            Map<String, List<MenuDAO.MenuItem>> categories = new LinkedHashMap<>();
            for (MenuDAO.MenuItem item : MenuDAO.loadMenu()) {
                categories.computeIfAbsent(item.category(), key -> new ArrayList<>())
                        .add(item);
            }
            for (Map.Entry<String, List<MenuDAO.MenuItem>> category : categories.entrySet()) {
                addCategory(category.getKey(),
                        category.getValue().toArray(new MenuDAO.MenuItem[0]));
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not load the menu from MySQL: "
                    + exception.getMessage(), exception);
        }

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

    private void addCategory(String titleText, MenuDAO.MenuItem... items) {
        JPanel category = new JPanel(new BorderLayout(8, 8));
        category.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JLabel title = new JLabel(titleText, SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD));
        category.add(title, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(items.length, 1, 6, 6));
        for (MenuDAO.MenuItem menuItem : items) {
            JButton button = new JButton(menuItem.name());
            button.addActionListener(e -> addToCart(menuItem));
            buttons.add(button);
        }

        category.add(buttons, BorderLayout.CENTER);
        menuGrid.add(category);
    }

    private void addToCart(MenuDAO.MenuItem menuItem) {
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

        java.awt.EventQueue.invokeLater(() -> {
            try {
                new MenuFrame().setVisible(true);
            } catch (IllegalStateException exception) {
                JOptionPane.showMessageDialog(null, exception.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
