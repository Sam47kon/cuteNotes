import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

public class DriveQuickstart {

  private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
  private static final String APPLICATION_NAME = "Google Drive API Java Quickstart";
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

  private static final java.io.File CREDENTIALS_FOLDER
      = new java.io.File(System.getProperty("user.home"), "credentials");
  private static FileDataStoreFactory DATA_STORE_FACTORY;

  private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);

  private static NetHttpTransport HTTP_TRANSPORT;

  private static Drive driveService;

  static {
    try {
      HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
      DATA_STORE_FACTORY = new FileDataStoreFactory(CREDENTIALS_FOLDER);
    } catch (Throwable t) {
      t.printStackTrace();
      System.exit(1);
    }
  }

  private static Credential getCredentials() throws IOException {
    InputStream in = DriveQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
    if (in == null) {
      throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
    }
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
        .setDataStoreFactory(DATA_STORE_FACTORY)
        .setAccessType("offline")
        .build();
    LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
    return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
  }

  public static void init() throws IOException {
    // Build a new authorized API client service.
    driveService = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials())
        .setApplicationName(APPLICATION_NAME)
        .build();
    System.out.println("Google API сработало, авторизация прошла успешно");
  }

  public static Drive getDriveService() throws IOException {
    return driveService;
  }
}
