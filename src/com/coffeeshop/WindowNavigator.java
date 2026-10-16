package com.coffeeshop;

import java.awt.Frame;
import java.awt.Rectangle;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Keeps the current window size/maximized state when moving between screens.
 */
final class WindowNavigator {

    private WindowNavigator() {
    }

    static void open(JFrame current, JFrame next) {
        Rectangle bounds = current.getBounds();
        int state = current.getExtendedState() & ~Frame.ICONIFIED;

        next.setBounds(bounds);
        next.setExtendedState(state);
        next.setVisible(true);

        // Some Swing layouts are rebuilt after construction. Validate the final
        // component tree once the new frame is displayable so Windows paints
        // every child immediately instead of waiting for mouse-triggered repaints.
        SwingUtilities.invokeLater(() -> {
            next.getContentPane().revalidate();
            next.validate();
            next.repaint();
        });

        current.dispose();
    }
}
