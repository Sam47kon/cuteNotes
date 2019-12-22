import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class GoogleDriveUtils {

  public static Drive getDriveService() throws IOException {
    return DriveQuickstart.getDriveService();
  }

  public static List<File> getGoogleFilesByName(String fileNameLike) throws IOException {
    String pageToken = null;
    List<File> list = new ArrayList<>();
    String query = " name contains '" + fileNameLike + "' " + " and mimeType != 'application/vnd.google-apps.folder' ";

    do {
      FileList result = getDriveService().files().list().setQ(query).setSpaces("drive")
          .setFields("nextPageToken, files(id, name, createdTime, mimeType)")
          .setPageToken(pageToken).execute();
      list.addAll(result.getFiles());
      pageToken = result.getNextPageToken();
    } while (pageToken != null);
    return list;
  }

  public static List<File> getGoogleFolders(String googleFolderIdParent) throws IOException {
    String pageToken = null;
    List<File> list = new ArrayList<>();
    String query;
    if (googleFolderIdParent == null) {
      query = " mimeType = 'application/vnd.google-apps.folder' " + " and 'root' in parents";
    } else {
      query = " mimeType = 'application/vnd.google-apps.folder' " + " and '" + googleFolderIdParent + "' in parents";
    }

    do {
      FileList result = getDriveService().files().list().setQ(query).setSpaces("drive")
          .setFields("nextPageToken, files(id, name, createdTime)")//
          .setPageToken(pageToken).execute();
      list.addAll(result.getFiles());
      pageToken = result.getNextPageToken();
    } while (pageToken != null);
    return list;
  }

  public static List<File> getGoogleRootFolders() throws IOException {
    return getGoogleFolders(null);
  }

  public static File createGoogleFolder(String folderIdParent, String folderName) {
    File fileMetadata = new File();

    fileMetadata.setName(folderName);
    fileMetadata.setMimeType("application/vnd.google-apps.folder");
    if (folderIdParent != null) {
      List<String> parents = Collections.singletonList(folderIdParent);
      fileMetadata.setParents(parents);
    }

    File folder = null;
    try {
      folder = getDriveService().files().create(fileMetadata).setFields("id, name").execute();
      System.out.println("Папка создана.");
    } catch (IOException e) {
      e.printStackTrace();
    }
    return folder;
  }

  private static File _createGoogleFile(String googleFolderIdParent, String customFileName,
      AbstractInputStreamContent uploadStreamContent) throws IOException {

    File fileMetadata = new File();
    fileMetadata.setName(customFileName);

    List<String> parents = Collections.singletonList(googleFolderIdParent);
    fileMetadata.setParents(parents);

    Drive driveService = GoogleDriveUtils.getDriveService();

    return driveService.files().create(fileMetadata, uploadStreamContent)
        .setFields("id, webContentLink, webViewLink, parents").execute();
  }

  public static File createGoogleFile(String googleFolderIdParent, String contentType,
      String customFileName, java.io.File uploadFile) throws IOException {

    AbstractInputStreamContent uploadStreamContent = new FileContent(contentType, uploadFile);
    return _createGoogleFile(googleFolderIdParent, customFileName, uploadStreamContent);
  }


  private static void printNamesAndIDs(int count) throws IOException {
    Drive service = GoogleDriveUtils.getDriveService();
    FileList result = service.files().list()
        .setPageSize(count)
        .setFields("nextPageToken, files(id, name)")
        .execute();
    List<File> files = result.getFiles();
    if (files == null || files.isEmpty()) {
      System.out.println("No files found.");
    } else {
      System.out.println("Files:");
      for (File file : files) {
        System.out.printf("%s (%s)\n", file.getName(), file.getId());
      }
    }
  }
}