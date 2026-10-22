package com.coffeeshop;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

/**
 * Cart screen with a responsive layout and quantity controls.
 */
public class CartFrame extends JFrame {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(CartFrame.class.getName());

    private final JLabel totalLabel = new JLabel("TOTAL: ₹0.00", SwingConstants.CENTER);

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ITEM", "QUANTITY", "PRICE", "TOTAL"}, 0) {
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return switch (columnIndex) {
                case 1 -> Integer.class;
                default -> String.class;
            };
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(model);

    public CartFrame() {
        setTitle("Coffee Billing System - Cart");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(680, 500));

        buildUi();
        loadCart();

        setPreferredSize(new Dimension(760, 560));
        pack();
        setLocationRelativeTo(null);
    }

    private void buildUi() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        setContentPane(root);

        JLabel title = new JLabel("YOUR CART", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        root.add(title, BorderLayout.NORTH);

        table.setFillsViewportHeight(true);
        table.setRowHeight(26);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);
        root.add(scrollPane, BorderLayout.CENTER);

        JButton minusButton = new JButton("−");
        JButton plusButton = new JButton("+");
        JButton removeButton = new JButton("REMOVE ITEM");
        JButton backButton = new JButton("BACK TO MENU");
        JButton continueButton = new JButton("CONTINUE");

        minusButton.setToolTipText("Decrease selected item quantity");
        plusButton.setToolTipText("Increase selected item quantity");
        minusButton.setFont(minusButton.getFont().deriveFont(Font.BOLD, 18f));
        plusButton.setFont(plusButton.getFont().deriveFont(Font.BOLD, 18f));
        minusButton.setPreferredSize(new Dimension(54, 34));
        plusButton.setPreferredSize(new Dimension(54, 34));

        minusButton.addActionListener(event -> changeSelectedQuantity(-1));
        plusButton.addActionListener(event -> changeSelectedQuantity(1));
        removeButton.addActionListener(event -> removeSelectedItem());
        backButton.addActionListener(event -> WindowNavigator.open(this, new MenuFrame()));
        continueButton.addActionListener(event -> continueToCustomer());

        JPanel quantityBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        quantityBar.add(new JLabel("QUANTITY:"));
        quantityBar.add(minusButton);
        quantityBar.add(plusButton);

        JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 8));
        actionBar.add(removeButton);
        actionBar.add(backButton);
        actionBar.add(continueButton);

        JPanel controls = new JPanel(new BorderLayout(8, 4));
        controls.add(quantityBar, BorderLayout.NORTH);
        controls.add(actionBar, BorderLayout.SOUTH);

        JPanel footer = new JPanel(new BorderLayout(8, 8));
        footer.add(totalLabel, BorderLayout.NORTH);
        footer.add(controls, BorderLayout.SOUTH);
        root.add(footer, BorderLayout.SOUTH);
    }

    private void loadCart() {
        model.setRowCount(0);

        for (CartItem item : Cart.items) {
            model.addRow(new Object[]{
                item.getName(),
                item.getQuantity(),
                formatCurrency(item.getPrice()),
                formatCurrency(item.getTotal())
            });
        }

        totalLabel.setText("TOTAL: " + formatCurrency(Cart.getTotal()));

        table.revalidate();
        table.repaint();
    }

    private String formatCurrency(double value) {
        return "₹" + String.format("%.2f", value);
    }

    private void changeSelectedQuantity(int change) {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item first.");
            return;
        }

        if (change > 0) {
            Cart.increaseQuantity(selectedRow);
        } else {
            Cart.decreaseQuantity(selectedRow);
        }

        loadCart();

        if (!Cart.items.isEmpty()) {
            int rowToSelect = Math.min(selectedRow, Cart.items.size() - 1);
            table.setRowSelectionInterval(rowToSelect, rowToSelect);
            table.scrollRectToVisible(table.getCellRect(rowToSelect, 0, true));
        }
    }

    private void removeSelectedItem() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to remove.");
            return;
        }

        Cart.removeItem(selectedRow);
        loadCart();

        if (!Cart.items.isEmpty()) {
            int rowToSelect = Math.min(selectedRow, Cart.items.size() - 1);
            table.setRowSelectionInterval(rowToSelect, rowToSelect);
        }
    }

    private void continueToCustomer() {
        if (Cart.items.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty!");
            return;
        }

        WindowNavigator.open(this, new CustomerFrame());
    }

    public static void main(String[] args) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info
                    : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> new CartFrame().setVisible(true));
    }
}
