import com.google.api.services.drive.model.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class NotesInDrive {

  private static final String CUTE_NOTES_FOLDER_NAME = "Cute notes folder";
  private static final String CUTE_NOTES_DATA_FILE = "notes-data.txt";

  private static File getOrCreateNotesFolder() throws IOException {
    List<File> folders = GoogleDriveUtils.getGoogleRootFolders();
    for (File folder : folders) {
      if (Objects.equals(folder.getName(), CUTE_NOTES_FOLDER_NAME)) {
        System.out.println("НАШЛАСЬ! ");
        return folder;
      }
    }
    return GoogleDriveUtils.createGoogleFolder(null, CUTE_NOTES_FOLDER_NAME);
  }

  private static File getOrCreateNotesDataFile() throws IOException {
    File folder = getOrCreateNotesFolder();
    List<File> googleFilesByName = GoogleDriveUtils.getGoogleFilesByName(CUTE_NOTES_DATA_FILE);
    for (File file : googleFilesByName) {
      if (Objects.equals(file.getName(), CUTE_NOTES_DATA_FILE)) {
        System.out.println("Файл есть в диске! ");
        return file;
      }
    }
    return GoogleDriveUtils.createGoogleFile(folder, ) // TODO
  }

  public static void main(String[] args) throws IOException {

  }
}
