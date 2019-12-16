import java.io.IOException;

public class Main {

  public static void main(String[] args) throws IOException {
    DriveQuickstart.init();

    NotesFrame notesFrame = new NotesFrame("Cute notes :3");
    notesFrame.start();
  }
}
