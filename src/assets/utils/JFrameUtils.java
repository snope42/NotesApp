package assets.utils;

import javax.swing.*;
import java.awt.*;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class JFrameUtils {

    public static void setupFrame(JFrame frame, String title, int width, int height, boolean isResizable) {

        frame.setResizable(isResizable);
        frame.setTitle(title);

        frame.setLayout(null);
        frame.setSize(width, height);
        frame.setMinimumSize(frame.getSize());
        frame.getContentPane().setBackground(Color.BLACK);

    }

    public static void addComponentsTo( JFrame parent, JComponent ...components) {

        for (JComponent comp : components)
            parent.add(comp);

    }
    public static void addComponentsTo( JComponent parent, JComponent ...components) {

        for (JComponent comp : components)
            parent.add(comp);

    }

}
