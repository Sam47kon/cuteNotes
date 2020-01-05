import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.text.Document;
import javax.swing.undo.UndoManager;

public class NotesFrame extends JFrame {

  private static volatile boolean WINDOW_IS_ACTIVE = true;

  public NotesFrame(String title) throws HeadlessException {
    super(title);
  }

  public void start() {
    this.initUI();
  }


  private void initUI() {
    JTextArea area = getJTextArea();
    this.setVisible(true);
    this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    this.setSize(new Dimension(400, 300));
    this.setAlwaysOnTop(true);
    this.setLocationRelativeTo(null);

    toPlugWindowListener();
    (new NotesFrame.IntervalRecording(area)).start();
  }


  private JTextArea getJTextArea() {
    JPanel panel = new JPanel();
    JTextArea area = new JTextArea();
    JScrollPane pane = new JScrollPane();

    area.setText(getTextFromDefaultFile());
    area.setFont(new Font("comic sans ms", Font.PLAIN, 14));
    area.setLineWrap(true);
    area.setWrapStyleWord(true);
    area.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    area.setBackground(new Color(250, 250, 180));
    toPlugUndoManager(area);

    pane.getViewport().add(area); // полоса прокрутки

    panel.setLayout(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    panel.add(pane);

    this.add(panel);
    return area;
  }

  private String getTextFromDefaultFile() {
    StringBuilder sb = new StringBuilder();

    try {
      if (!NotesInDrive.DATA_FILE.isFile()) {
        WINDOW_IS_ACTIVE = NotesInDrive.DATA_FILE.createNewFile(); // LOL
      }

      BufferedReader reader = new BufferedReader(new FileReader(NotesInDrive.DATA_FILE));
      for (String buffer = reader.readLine(); buffer != null; buffer = reader.readLine()) {
        sb.append(buffer).append("\n");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return sb.toString();
  }

  // Оптимизаци. Для того, чтобы обновление файла происходило только во время активного состояния окна.
  private void toPlugWindowListener() {
    WindowListener windowListener = new WindowAdapter() {
      @Override
      public void windowActivated(WindowEvent e) {
        WINDOW_IS_ACTIVE = true;
        super.windowActivated(e);
      }

      @Override
      public void windowDeactivated(WindowEvent e) {
        WINDOW_IS_ACTIVE = false;
        super.windowDeactivated(e);
      }
    };
    this.addWindowListener(windowListener);
  }

  // Подключение возможности "отменять" и "возвращать" изменения
  private void toPlugUndoManager(JTextArea area) {
    UndoManager undoManager = new UndoManager();
    Document document = area.getDocument();
    document.addUndoableEditListener(event -> undoManager.addEdit(event.getEdit()));
    InputMap inputMap = area.getInputMap();
    ActionMap actionMap = area.getActionMap();
    inputMap.put(KeyStroke.getKeyStroke(90, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
        "Undo"); // ctrl+z
    inputMap.put(KeyStroke.getKeyStroke(89, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
        "Redo"); // ctrl+y
    actionMap.put("Undo", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        if (undoManager.canUndo()) {
          undoManager.undo();
        }
      }
    });
    actionMap.put("Redo", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        if (undoManager.canRedo()) {
          undoManager.redo();
        }
      }
    });
  }


  // Авто сохранение файла notes-data.txt с переодичностью 300ms
  private static class IntervalRecording extends Thread {

    private JTextArea area;

    private IntervalRecording(JTextArea area) {
      this.area = area;
    }

    @Override
    public void run() {
      while (WINDOW_IS_ACTIVE) {
        String tmp = this.area.getText();
        this.waitMoment();
        if (!tmp.equals(this.area.getText())) {
          this.writeTextInFile(this.area.getText());
        }
      }
    }

    private void writeTextInFile(String text) {
      try (FileWriter fileWriter = new FileWriter(NotesInDrive.DATA_FILE, false)) {
        text = text.replaceAll("\n", "\r\n");
        fileWriter.write(text);
        System.out.println("Трололо");
      } catch (Exception var3) {
        var3.printStackTrace();
      }
    }

    private void waitMoment() {
      try {
        Thread.sleep(300L);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
