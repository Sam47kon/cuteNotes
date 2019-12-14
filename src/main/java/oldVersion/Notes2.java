package oldVersion;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

public class Notes2 extends JFrame {

  public static final String DEFAULT_FILE = "./notesdata.txt";

  public Notes2() {
    this.setAlwaysOnTop(true);
    this.initUI();
  }

  public static void main(String[] args) {
    Notes2 ex = new Notes2();
    ex.setVisible(true);
  }

  private void initUI() {
    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    JScrollPane pane = new JScrollPane();
    JTextArea area = new JTextArea();
    area.setText(this.getStringFromDefaultFile());
    area.setFont(new Font("comic sans ms", 0, 14));
    area.setLineWrap(true);
    area.setWrapStyleWord(true);
    area.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));


    final UndoManager undoManager = new UndoManager();
    Document doc = area.getDocument();
    doc.addUndoableEditListener(new UndoableEditListener() {
      public void undoableEditHappened(UndoableEditEvent e) {
        undoManager.addEdit(e.getEdit());
      }
    });
    InputMap im = area.getInputMap(0);
    ActionMap am = area.getActionMap();
    im.put(KeyStroke.getKeyStroke(90, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
        "Undo");
    im.put(KeyStroke.getKeyStroke(89, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
        "Redo");
    am.put("Undo", new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        try {
          if (undoManager.canUndo()) {
            undoManager.undo();
          }
        } catch (CannotUndoException var3) {
          var3.printStackTrace();
        }

      }
    });
    am.put("Redo", new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        try {
          if (undoManager.canRedo()) {
            undoManager.redo();
          }
        } catch (CannotUndoException var3) {
          var3.printStackTrace();
        }

      }
    });



    pane.getViewport().add(area);
    panel.add(pane);
    this.add(panel);
    (new Notes2.IntervalWriter(area)).start();
    this.setTitle("Notes :3");
    area.setBackground(new Color(252, 251, 179));
    this.setSize(new Dimension(600, 300));
    this.setDefaultCloseOperation(3);
    this.setLocationRelativeTo((Component) null);
  }

  public String getStringFromDefaultFile() {
    String result = "";

    try {
      if (!(new File("./notesdata.txt")).exists()) {
        (new FileOutputStream("./notesdata.txt")).close();
      }

      BufferedReader reader = new BufferedReader(new FileReader("./notesdata.txt"));

      for (String buffer = reader.readLine(); buffer != null; buffer = reader.readLine()) {
        result = result + buffer + "\n";
      }
    } catch (Exception var4) {
      var4.printStackTrace();
    }

    return result;
  }

  public static class IntervalWriter extends Thread {

    private JTextArea area;

    public IntervalWriter(JTextArea area) {
      this.area = area;
    }

    public void run() {
      while (true) {
        String a = this.area.getText();
        this.waitAmoment();
        if (!a.equals(this.area.getText())) {
          this.writeStringToFile(this.area.getText());
        }
      }
    }

    private void writeStringToFile(String s) {
      try {
        s = s.replaceAll("\n", "\r\n");
        FileWriter fileWriter = new FileWriter("./notesdata.txt", false);
        fileWriter.write(s);
        fileWriter.close();
      } catch (Exception var3) {
        var3.printStackTrace();
      }

    }

    private void waitAmoment() {
      try {
        Thread.sleep(300L);
      } catch (InterruptedException var2) {
        var2.printStackTrace();
      }

    }
  }
}