import com.google.api.services.drive.model.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

public class NotesInDrive {

  private static final String CUTE_NOTES_FOLDER_NAME = "Cute notes folder";
  private static final String CUTE_NOTES_DATA_FILE_NAME = "notes-data.txt";
  public static java.io.File DATA_FILE = new java.io.File("./notes-data.txt");
  private static boolean IS_FIRST_START = false;

  private static File getOrCreateNotesFolder() throws IOException {
    List<File> folders = GoogleDriveUtils.getGoogleRootFolders();
    for (File folder : folders) {
      if (Objects.equals(folder.getName(), CUTE_NOTES_FOLDER_NAME)) {
        System.out.println("Папка на гугл диске СУЩЕСТВУЕТ.");
        return folder;
      }
    }
    return GoogleDriveUtils.createGoogleFolder(null, CUTE_NOTES_FOLDER_NAME);
  }

  private static File getOrCreateNotesDataFile() throws IOException {
    File folder = getOrCreateNotesFolder();
    List<File> googleFilesByName = GoogleDriveUtils.getGoogleFilesByName(CUTE_NOTES_DATA_FILE_NAME);
    for (File file : googleFilesByName) {
      if (Objects.equals(file.getName(), CUTE_NOTES_DATA_FILE_NAME)) {
        System.out.println("Файл на гугл диске СУЩЕСТВУЕТ.");
        return file;
      }
    }
    if (!DATA_FILE.isFile()) {
      IS_FIRST_START = DATA_FILE.createNewFile();
    }
    System.out.println("Файл на гугл диске СОЗДАН.");
    return GoogleDriveUtils
        .createGoogleFile(folder.getId(), "text/plain", DATA_FILE.getName(), DATA_FILE);
  }

  public static void main(String[] args) throws IOException {
    File googleFile = getOrCreateNotesDataFile();
    System.out.println("id: " + googleFile.getId() + ", name: " + googleFile.getName());
  }

  public void get_Start() throws IOException {
    File file = getOrCreateNotesDataFile();

    String fileId = file.getId();
    try (OutputStream fileOutputStream = new FileOutputStream(DATA_FILE)) {
      if (!DATA_FILE.exists()) {
        DATA_FILE.createNewFile();
      }
      GoogleDriveUtils.getDriveService().files().get(fileId).executeMediaAndDownloadTo(fileOutputStream);
      fileOutputStream.flush();

    }

    if (!IS_FIRST_START) {
    }
  }

}
