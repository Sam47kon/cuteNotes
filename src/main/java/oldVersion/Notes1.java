package oldVersion;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Notes1 extends JFrame {

  public static final String DEFAULT_FILE = "./notesdata";

  public Notes1() {
    this.setAlwaysOnTop(true);
    this.initUI();
  }

  public static void main(String[] args) {
    Notes1 ex = new Notes1();
    ex.setVisible(true);
  }

  private void initUI() {
    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    JScrollPane pane = new JScrollPane();
    JTextArea area = new JTextArea();
    area.setText(this.getStringFromDefaultFile());

    area.setFont(new Font("comic sans ms", Font.PLAIN, 14));
    area.setLineWrap(true);
    area.setWrapStyleWord(true);
    area.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

    pane.getViewport().add(area);

    panel.add(pane);
    this.add(panel);

    (new Notes1.IntervalWriter(area)).start();
    this.setTitle("Notes :3");
    area.setBackground(new Color(252, 251, 179));

    this.setSize(new Dimension(350, 300));
    this.setDefaultCloseOperation(3);
    this.setLocationRelativeTo((Component) null);
  }

  public String getStringFromDefaultFile() {
    StringBuilder result = new StringBuilder();

    try {
      if (!(new File("./notesdata")).exists()) {
        (new FileOutputStream("./notesdata")).close();
      }

      BufferedReader reader = new BufferedReader(new FileReader("./notesdata"));

      for (String buffer = reader.readLine(); buffer != null; buffer = reader.readLine()) {
        result.append(buffer).append("\n");
      }
    } catch (Exception var4) {
      var4.printStackTrace();
    }

    return result.toString();
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
        FileWriter fileWriter = new FileWriter("./notesdata", false);
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
