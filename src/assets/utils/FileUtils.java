package assets.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;

public class FileUtils {

    public static int loadNumeralFileData(File dataFile) throws IOException {

        boolean created = false;
        if (!dataFile.exists()) created = dataFile.createNewFile();

        if (!created) {
            try {

                return Integer.parseInt( readFileAsStr(dataFile) );

            } catch (NumberFormatException nfe) {
                System.out.println("Warning: '" + dataFile.toPath() + "' is invalid or empty.");
                return 0;
            }
        }
        return 0;

    }
    public static String loadStrFileData(File dataFile) throws IOException {

        boolean created = false;
        if(!dataFile.exists()) created = dataFile.createNewFile();

        if (!created) return readFileAsStr(dataFile);
        return "";

    }

    public static String readFileAsStr(File file) throws IOException {
        return Files.readString(file.toPath()).trim();
    }
    public static <T extends Collection<String>> void readFileAsList(File file, T list) throws IOException {

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;
            while ((line = reader.readLine()) != null) {
                list.add(line);
            }

        }

    }

    public static void overwriteFile(File file, String newContent) throws IOException {
        Files.writeString(file.toPath(), newContent);
    }

}
