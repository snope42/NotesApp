import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class App {

    static String path = "src/data/";
    static File countFile;
    static int count = 0;

    public static void main(String[] args) throws IOException {

        createCount();

        Home home = new Home();

        File noteFolder = new File(path + "notes/");
        File[] notes = noteFolder.listFiles();



        if (notes != null) {
            for (File file : notes) {
                home.createNoteEntry().setText(file.getName().substring(0, file.getName().length() - 4));
            }
        }

    }

    static void createCount() throws IOException {
        countFile = new File(path + "count.txt");

        boolean created = false;
        if(!countFile.exists()) created = countFile.createNewFile();

        if (!created) {

            String content = Files.readString(countFile.toPath()).trim();
            count = Integer.parseInt(content);

        } else Files.writeString(countFile.toPath(), String.valueOf(count));
    }

}
