import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

public class Home extends JFrame {

    static JLabel defaultText = new JLabel("Start by creating your first note!", JLabel.CENTER);
    static JButton newButton = new JButton("+");
    static JPanel newButtonPanel = new JPanel();
    static JPanel notePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 22, 22)) {
        @Override
        public Dimension getPreferredSize() {
            if (getParent() instanceof JViewport) {
                if (getComponentCount() == 0) return new Dimension(getParent().getWidth(), 100);

                FlowLayout layout = (FlowLayout) getLayout();

                int rows = (int) Math.ceil((double) getComponentCount() / 3);
                int totalHeight = layout.getVgap() + (rows * (getComponent(0).getPreferredSize().height + layout.getVgap()));

                return new Dimension(getParent().getWidth(), totalHeight);
            }
            return super.getPreferredSize();
        }
    };
    static JScrollPane scrollPane = new JScrollPane(notePanel, VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_NEVER);

    Home() {

        this.setResizable(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("Notes");

        this.setLayout(null);
        this.setSize(1280, 720);
        this.setMinimumSize(this.getSize());
        this.getContentPane().setBackground(Color.BLACK);

        defaultText.setForeground(Color.LIGHT_GRAY);
        defaultText.setFont(new Font("aFont", Font.BOLD, 25));
        defaultText.setSize(380, 100);
        defaultText.setLocation(this.getWidth()/2 - defaultText.getWidth()/2, this.getHeight()/2 - defaultText.getHeight()/2);

        newButtonPanel.setBounds(this.getWidth() - 30 - 100, 17, 100, 100);
        newButtonPanel.setLayout(null);
        newButtonPanel.setBackground(null);
        newButtonPanel.add(newButton);
        newButton.setSize(newButton.getParent().getSize());
        newButton.setFocusable(false);
        newButton.setBorder(new LineBorder(Color.BLUE, 3));
        newButton.setForeground(Color.BLUE);
        newButton.setBackground(Color.BLACK);
        newButton.setFont(new Font("aFont", Font.BOLD, 40));

        scrollPane.setBounds(100, 100, this.getWidth() - 200, this.getHeight() - 200);
        scrollPane.getViewport().getView().setBackground(Color.BLACK);
        scrollPane.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLUE));
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = Color.DARK_GRAY;
                this.trackColor = Color.DARK_GRAY.darker();
                this.thumbLightShadowColor = Color.BLACK;
                this.thumbDarkShadowColor = Color.BLACK;
            }
        });

        newButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                newButton.setBackground(Color.LIGHT_GRAY);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    new Note(Home.this);

                    JButton note = new JButton();

                    note.setFocusable(false);
                    note.setForeground(Color.BLUE);
                    note.setBackground(null);
                    note.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
                    note.setPreferredSize(new Dimension(notePanel.getWidth()/3 - 30, Home.super.getHeight()/4 - 35));

                    notePanel.add(note);

                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                Home.super.setVisible(false);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                newButton.setBackground(null);
            }
        });

        this.add(defaultText);
        this.add(newButtonPanel);
        this.add(scrollPane);

        this.pack();
        this.setVisible(true);
    }

}
