package assets;

import assets.windows.Home;
import assets.utils.FileUtils;

import java.io.*;
import java.util.*;

public class App {

    public static final String PATH = "src/data/";
    public static File countFile;
    public static int count = 0;
    public static File orderFile;
    public static Queue<String> noteQ = new LinkedList<>();

    public static void main(String[] args) throws IOException {

        // Recreate main data folder if missing.
        new File(PATH).mkdir();

        // Make sure the notes directory exists before loading Count and Order data, recreate if missing.
        new File(PATH + "notes/").mkdir();
        registerCount();
        registerOrder();

        // Initialize the App.
        Home home = new Home();

        // Load the Note Entries.
        if (!noteQ.isEmpty()) {

            Home.hideWelcomeText();
            for (String title : noteQ) {
                Home.createNoteEntry(home).setText(title);
            }

        }

    }

    public static void registerCount() throws IOException {

        countFile = new File(PATH + "count.txt");
        count = FileUtils.loadNumeralFileData(countFile);

        if (count == 0) FileUtils.overwriteFile(countFile, "0");

    }
    public static void registerOrder() throws IOException {
        orderFile = new File(PATH + "order.txt");

        boolean created = false;
        if(!orderFile.exists()) created = orderFile.createNewFile();

        if (!created) FileUtils.readFileAsList(orderFile, noteQ);
    }

}
