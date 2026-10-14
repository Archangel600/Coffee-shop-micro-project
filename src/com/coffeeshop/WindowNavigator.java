package com.coffeeshop;

import java.awt.Frame;
import java.awt.Rectangle;
import javax.swing.JFrame;

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
        current.dispose();
    }
}
