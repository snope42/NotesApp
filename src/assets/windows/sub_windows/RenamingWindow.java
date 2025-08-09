package assets.windows.sub_windows;

import assets.App;
import assets.utils.AlgoUtils;
import assets.utils.JFrameUtils;
import assets.utils.VarUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

public class RenamingWindow extends JFrame {

    public int DEFAULT_WIDTH = 400;
    public int DEFAULT_HEIGHT = 200;

    JTextField renameField = new JTextField("New Title");

    public RenamingWindow(JButton note) {

        JFrameUtils.setupFrame(this, "Rename", DEFAULT_WIDTH, DEFAULT_HEIGHT, false);

        setupUI();
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setLocation(VarUtils.screenDimension.width/2 - this.getWidth()/2, VarUtils.screenDimension.height/2 - this.getHeight()/2);

        renameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {

                if (e.getKeyCode() == KeyEvent.VK_ENTER) {

                    if (renameField.getText().trim().isEmpty()) {

                        System.out.println("Name can't be empty.");
                        return;

                    }
                    if (hasUnsupportedChar(renameField.getText())) {

                        System.out.println("The following characters '\\', '/', ':', '*', '?', '\"', '<', '>', '|' are not allowed.");
                        return;

                    }

                    try {

                        renameNote(note);
                        RenamingWindow.super.dispose();

                    } catch (IOException ex) {
                        System.out.println("The FilePath '" + App.PATH + "order.txt' doesn't exist.");
                    }

                }
                
            }
        });

        this.add(renameField);

        this.pack();
        this.setVisible(true);

    }

    private void setupUI() {

        renameField.setCaretColor(Color.LIGHT_GRAY);
        renameField.setForeground(Color.BLUE);
        renameField.setBackground(Color.BLACK);
        renameField.setFont(new Font("aFont", Font.BOLD, 30));
        renameField.setHorizontalAlignment(SwingConstants.CENTER);
        renameField.setBorder(null);
        renameField.setSize(this.getSize());

    }

    private void renameNote(JButton note) throws IOException {

        String oldTitle = note.getText();
        String newTitle = renameField.getText();

        try (BufferedReader reader = new BufferedReader(new FileReader(App.orderFile))){

            StringBuilder resultText = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {

                if (line.equals(oldTitle)) resultText.append(newTitle).append('\n');
                else resultText.append(line).append('\n');

            }
            Files.writeString(App.orderFile.toPath(), resultText.toString());

        } catch (IOException ex) {
            System.out.println("The file '" + App.orderFile + "' does not exist.");
        }

        note.setText(newTitle);

        boolean wasRenamed = new File(App.PATH + "notes/" + oldTitle + ".txt").renameTo(new File(App.PATH + "notes/" + newTitle + ".txt"));
        if (!wasRenamed) System.out.println("Failed to rename '" + App.PATH + "notes/" + oldTitle + ".txt'");

        App.registerOrder();

    }

    private boolean hasUnsupportedChar(String str) {

        Set<Character> unsupportedChars = new HashSet<>(Set.of(
                '\\', '/', ':', '*', '?', '\"', '<', '>', '|'
        ));

        return AlgoUtils.containsCharFromSet(str, unsupportedChars);

    }

}
