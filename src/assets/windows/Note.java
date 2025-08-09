package assets.windows;

import assets.App;
import assets.utils.JFrameUtils;
import assets.utils.VarUtils;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

public class Note extends JFrame {

    JTextArea textArea = new JTextArea();
    JScrollPane scrollPane = new JScrollPane(textArea, VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_NEVER);
    JButton homeButton = new JButton("home");
    JTextField textField = new JTextField("New note");

    public File noteFile;
    public static boolean wasNotePanelEmpty = true;

    public static final int DEFAULT_WIDTH = (int) (VarUtils.screenDimension.width/1.5);
    public static final int DEFAULT_HEIGHT = (int) (VarUtils.screenDimension.height/1.5);

    public Note(Home home, boolean isNew, JButton note) {

        if (isNew) createNoteFile();
        else fetchNoteContent(note);

        JFrameUtils.setupFrame(this, "Notes", DEFAULT_WIDTH, DEFAULT_HEIGHT, true);

        setupUI();
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        homeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                homeButton.setBackground(Color.LIGHT_GRAY);
            }

            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getButton() != MouseEvent.BUTTON1) return; // BUTTON1 == Left Click

                saveNoteContent(noteFile, textArea);
                switchToHomeTab(Note.this, home);

            }

            @Override
            public void mouseExited(MouseEvent e) {
                homeButton.setBackground(null);
            }
        });

        setTitleCaretPosition(); // Move the caret to the end when editing, and to the front when exiting.
        listenToContentChanges(noteFile, textArea);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                home.setVisible(true);
            }
        });

        // Manual Window and Component Resizing for Null Layout.
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resize();
            }
        });

        JFrameUtils.addComponentsTo(this, scrollPane, homeButton, textField);

        this.setVisible(true);
        home.setVisible(false);
        textArea.requestFocusInWindow();

    }

    private void setupUI() {

        textArea.setMargin(new Insets(10, 10, 10, 10));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setCaretColor(Color.LIGHT_GRAY);
        textArea.setSelectedTextColor(Color.LIGHT_GRAY);
        textArea.setSelectionColor(Color.BLUE);
        scrollPane.getViewport().getView().setBackground(Color.BLACK);
        scrollPane.getViewport().getView().setForeground(Color.WHITE);
        scrollPane.getViewport().getView().setFont(new Font("aFont", Font.BOLD, 20));
        scrollPane.setSize((int) (this.getWidth()/1.2), (int) (this.getHeight()/1.4));
        scrollPane.setLocation(this.getWidth()/2 - scrollPane.getWidth()/2, this.getHeight()/2 - scrollPane.getHeight()/2);
        scrollPane.getVerticalScrollBar().setBackground(Color.DARK_GRAY);

        homeButton.setFocusable(false);
        homeButton.setBorder(new LineBorder(Color.BLUE, 3));
        homeButton.setForeground(Color.BLUE);
        homeButton.setSize(70, 70);
        homeButton.setLocation(0, 1);
        homeButton.setBackground(null);
        homeButton.setFont(new Font("aFont", Font.BOLD, 20));

        textField.setCaretColor(Color.LIGHT_GRAY);
        textField.setForeground(Color.LIGHT_GRAY);
        textField.setBackground(Color.BLACK);
        textField.setFont(new Font("aFont", Font.BOLD, 30));
        textField.setSize(380, 70);
        textField.setLocation(this.getWidth()/2 - textField.getWidth()/2, 5);
        textField.setHorizontalAlignment(SwingConstants.CENTER);
        textField.setBorder(null);

    }

    private void createNoteFile() {
        noteFile = new File(App.PATH + "notes/" + (App.count +1) + ".txt");

        boolean created;
        if(!noteFile.exists()) {

            try {

                created = noteFile.createNewFile();

            } catch (IOException e) {
                System.out.println("Couldn't create file '" + App.PATH + "notes/" + (App.count +1) + ".txt'");
                return;
            }

        }

        try {
            Files.writeString(App.countFile.toPath(), String.valueOf(App.count +1));
        } catch (IOException e) {
            System.out.println("The file '" + App.countFile.toPath() + "' does not exist.");
            return;
        }

        App.count++;

    }

    public void fetchNoteContent(JButton note) {

        noteFile = new File(App.PATH + "notes/" + note.getText() + ".txt");

        try {

            textArea.setText(Files.readString(noteFile.toPath()).trim());

        } catch (IOException e) {
            System.out.println("The file '" + noteFile.toPath() + "' does not exist.");
            return;
        }

    }

    public static void switchToHomeTab(Note note, Home home) {

        note.dispose();
        home.setVisible(true);

        if (wasNotePanelEmpty) {
            wasNotePanelEmpty = false;
            Home.hideWelcomeText();
        }

    }

    private void setTitleCaretPosition() {

        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                textField.setCaretPosition(textField.getText().length());
            }

            @Override
            public void focusLost(FocusEvent e) {
                textField.setCaretPosition(0);
            }
        });

    }

    private void listenToContentChanges(File noteFile, JTextArea textArea) {

        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                saveNoteContent(noteFile, textArea);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                saveNoteContent(noteFile, textArea);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                saveNoteContent(noteFile, textArea);
            }
        });

    }
    public static void saveNoteContent(File noteFile, JTextArea textArea) {

        try {

            Files.writeString(noteFile.toPath(), textArea.getText());

        } catch (IOException e) {
            System.out.println("The file '" + noteFile.toPath() + "' does not exist.");
        }

    }

    public void resize() {
        int windowWidth = Note.super.getWidth();
        int windowHeight = Note.super.getHeight();

        int scrollPaneWidth = (int) (windowWidth / 1.2);
        int scrollPaneHeight = (int) (windowHeight / 1.4);
        scrollPane.setSize(scrollPaneWidth, scrollPaneHeight);
        scrollPane.setLocation(
                windowWidth / 2 - scrollPaneWidth / 2,
                windowHeight / 2 - scrollPaneHeight / 2
        );

        int buttonSize = Math.min(70, Math.max(50, windowWidth / 15));
        homeButton.setSize(buttonSize, buttonSize);
        homeButton.setLocation(5, 5);

        int buttonFontSize = Math.max(12, buttonSize / 4);
        homeButton.setFont(new Font("aFont", Font.BOLD, buttonFontSize));

        int textFieldWidth = Math.min(400, windowWidth - 100);
        int textFieldHeight = Math.max(50, windowHeight / 15);
        textField.setSize(textFieldWidth, textFieldHeight);
        textField.setLocation(windowWidth / 2 - textFieldWidth / 2, 10);

        int textFieldFontSize = Math.max(16, textFieldHeight / 2);
        textField.setFont(new Font("aFont", Font.BOLD, textFieldFontSize));

        Note.super.repaint();
    }

}
