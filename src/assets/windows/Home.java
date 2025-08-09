package assets.windows;

import assets.App;
import assets.utils.JFrameUtils;
import assets.windows.sub_windows.RenamingWindow;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

public class Home extends JFrame {

    public static final int DEFAULT_WIDTH = 1280;
    public static final int DEFAULT_HEIGHT = 780;
    public static final int COMPONENT_GAP = 22;

    static JLabel welcomeText = new JLabel("Start by creating your first note!", JLabel.CENTER);
    JButton newButton = new JButton("+");
    JPanel newButtonPanel = new JPanel();
    static JPanel notePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, COMPONENT_GAP, COMPONENT_GAP)) {
        @Override
        public Dimension getPreferredSize() {
            try {

                /* Forcing the FlowLayout to have a limited width in order to prevent components
                   from stacking up on one row, which is caused by the Panel being inside a ScrollPane. */
                return forcePanelColumnLimit(this,3);

            } catch (IllegalAccessException e) {
                System.out.println("The 'forcePanelColumnLimit' function accepts FlowLayouts only.");
            }
            return super.getPreferredSize();
        }
    };
    JScrollPane scrollPane = new JScrollPane(notePanel, VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_NEVER);

    public Home() {

        JFrameUtils.setupFrame(this, "Notes", DEFAULT_WIDTH, DEFAULT_HEIGHT, true);

        setupUI();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        scrollPane.addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) { newButtonPanel.repaint(); }
        }); // Repaint the NewButtonPanel when ScrollPane UI goes over it.

        newButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { newButton.setBackground(Color.LIGHT_GRAY); }

            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getButton() != MouseEvent.BUTTON1) return; // BUTTON1 == Left Click

                switchToNoteTab(Home.this, true, null);
                registerNote(Home.this, true);

            }

            @Override
            public void mouseExited(MouseEvent e) {
                newButton.setBackground(null);
            }

        });

        JFrameUtils.addComponentsTo(this, welcomeText, newButtonPanel, scrollPane);

        this.pack();
        this.setVisible(true);
    }



    private static Dimension forcePanelColumnLimit(JPanel panel, int limit) throws IllegalAccessException {

        if (!(panel.getLayout() instanceof FlowLayout)) throw new IllegalAccessException();

        final int DEFAULT_HEIGHT = 100;

        if (panel.getParent() instanceof JViewport) {
            if (panel.getComponentCount() == 0) return new Dimension(panel.getParent().getWidth(), DEFAULT_HEIGHT);

            FlowLayout layout = (FlowLayout) panel.getLayout();

            int rows = (int) Math.ceil((double) panel.getComponentCount() / limit);
            int totalHeight = layout.getVgap() + (rows * (panel.getComponent(0).getPreferredSize().height + layout.getVgap()));

            return new Dimension(panel.getParent().getWidth(), totalHeight);
        }
        return panel.getPreferredSize();

    }

    private void setupUI() {
        welcomeText.setForeground(Color.LIGHT_GRAY);
        welcomeText.setFont(new Font("aFont", Font.BOLD, 25));
        welcomeText.setSize(380, 100);
        welcomeText.setLocation(this.getWidth()/2 - welcomeText.getWidth()/2, this.getHeight()/2 - welcomeText.getHeight()/2);

        newButtonPanel.setBounds(this.getWidth() - 30 - 100, 18, 100, 100);
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
    }



    private static void switchToNoteTab(Home home, boolean isNew, JButton note) {

        new Note(home, isNew, note);

    }
    public static void registerNote(Home home, boolean isNew) {

        try {

            JButton note = createNoteEntry(home);

            note.setText(String.valueOf(App.count));
            App.noteQ.offer(note.getText());

            try (FileWriter writer = new FileWriter(App.orderFile, true)) {
                writer.append(note.getText() + "\n");
            } catch (Exception ex) {
                throw new Exception(ex);
            }

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }
    public static JButton createNoteEntry(Home home) {
        JButton note = new JButton();
        note.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getButton() == MouseEvent.BUTTON1 /* Left click */) {

                    switchToNoteTab(home, false, note);

                } else if (e.getButton() == MouseEvent.BUTTON3 /* Right click */) {

                    setupActionMenu(note, home).show(note, 10, 10);

                }

            }
        });

        note.setFocusable(false);
        note.setForeground(Color.BLUE);
        note.setBackground(null);
        note.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
        note.setPreferredSize(new Dimension(notePanel.getWidth()/3 - 30, home.getHeight()/4 - 35));
        note.setFont(new Font("aFont", Font.BOLD, 40));

        notePanel.add(note);

        return note;
    }

    private static JPopupMenu setupActionMenu(JButton note, Home home) {

        JPopupMenu actionMenu = new JPopupMenu();

        JMenuItem deleteAction = new JMenuItem("Delete note");
        JMenuItem renameAction = new JMenuItem("Rename note");

        deleteAction.addActionListener(event -> {

            App.noteQ.remove(note.getText()); // Remove Note from Queue for consistent data.

            notePanel.remove(note); // Remove Note from Parent.

            notePanel.updateUI(); // Trigger the UI to remove the Note visually.

            if (App.noteQ.isEmpty()) showWelcomeText();

            try (BufferedReader reader = new BufferedReader(new FileReader(App.orderFile))){

                String resultText = "";

                String line;
                while ((line = reader.readLine()) != null) {

                    if (line.equals(note.getText())) continue;

                    resultText += line + "\n";

                }
                Files.writeString(App.orderFile.toPath(), resultText);
                new File(App.PATH + "notes/" + note.getText() + ".txt").delete();


            } catch (IOException ex) {
                System.out.println("The file '" + App.orderFile + "' does not exist.");
            }

        });
        renameAction.addActionListener(event -> {

            new RenamingWindow(note);


        });

        actionMenu.setBackground(Color.DARK_GRAY);
        actionMenu.setBorderPainted(false);

        setupActionMenuComponent(deleteAction, actionMenu);
        setupActionMenuComponent(renameAction, actionMenu);

        return actionMenu;

    }

    private static void setupActionMenuComponent(JMenuItem action, JPopupMenu menu) {

        action.setBackground(Color.DARK_GRAY);
        action.setForeground(Color.WHITE.darker());
        action.setBorderPainted(false);

        menu.add(action);

    }

    public static void hideWelcomeText() {
        welcomeText.setVisible(false);
    }
    public static void showWelcomeText() {
        welcomeText.setVisible(true);
    }

}