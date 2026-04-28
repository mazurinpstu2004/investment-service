package ru.coursework.gui;

import javax.swing.*;
import java.awt.*;

public class OptionPane {

    public static void applyErrorStyles() {
        UIManager.put("OptionPane.background", new Color(0xFFEBEE));
        UIManager.put("Panel.background", new Color(0xFFEBEE));
        UIManager.put("OptionPane.messageForeground", new Color(0xD32F2F));
        UIManager.put("Button.background", new Color(0xD32F2F));
        UIManager.put("Button.foreground", Color.WHITE);
    }

    public static void applyInfoStyles() {
        UIManager.put("OptionPane.background", new Color(0xE3F2FD));
        UIManager.put("Panel.background", new Color(0xE3F2FD));
        UIManager.put("OptionPane.messageForeground", new Color(0x1565C0));
        UIManager.put("Button.background", new Color(0x1565C0));
        UIManager.put("Button.foreground", Color.WHITE);
    }

    public static void showStyledDialog(Component parent, String message, String title, int messageType) {
        switch (messageType) {
            case JOptionPane.ERROR_MESSAGE:
                applyErrorStyles();
                break;
            case JOptionPane.INFORMATION_MESSAGE:
                applyInfoStyles();
                break;
        }
        JOptionPane.showMessageDialog(parent, message, title, messageType);
    }
}
