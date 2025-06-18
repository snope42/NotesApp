import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

public class Note extends JFrame {

    JTextArea textArea = new JTextArea();
    JScrollPane scrollPane = new JScrollPane(textArea, VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_NEVER);
    JButton homeButton = new JButton("Home");
    String title = "New Note";
    JTextField textField = new JTextField(title);

    int fileCount = 0;
    String path = "src/data/";
    File file;
    boolean isListEmpty = true;

    public Note(Home home) throws IOException {

        create();

        this.setResizable(true);
        this.setTitle("Notes");

        this.setLayout(null);
        this.setSize((int) (UtilityVariables.screenDimension.width/1.5), (int) (UtilityVariables.screenDimension.height/1.5));
        this.getContentPane().setBackground(Color.BLACK);

        setup();

        homeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                homeButton.setBackground(Color.LIGHT_GRAY);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                Note.super.dispose();
                home.setVisible(true);

                try {
                    save();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                if (isListEmpty) {
                    isListEmpty = false;
                    home.defaultText.setVisible(false);
                }

            }

            @Override
            public void mouseExited(MouseEvent e) {
                homeButton.setBackground(null);
            }
        });

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

        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                try {
                    save();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                try {
                    save();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                try {
                    save();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                home.setVisible(true);
            }
        });

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resize();
            }
        });

        this.add(scrollPane);
        this.add(homeButton);
        this.add(textField);

        this.setVisible(true);
        textArea.requestFocusInWindow();
    }

    void setup() {
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

    void create() throws IOException {
        file = new File(path + "note-" + fileCount++ + ".txt");

        boolean created;

        if(!file.exists()) created = file.createNewFile();
    }

    void save() throws IOException {

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(textArea.getText());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }

    void resize() {
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
        textField.setLocation(
                windowWidth / 2 - textFieldWidth / 2,
                10
        );

        int textFieldFontSize = Math.max(16, textFieldHeight / 2);
        textField.setFont(new Font("aFont", Font.BOLD, textFieldFontSize));

        Note.super.repaint();
    }

}
