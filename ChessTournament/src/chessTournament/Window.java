package chessTournament;

//klasa odpowiada za obs�ug� okna (intersejsu)

import javax.swing.*;

@SuppressWarnings("serial")
public class Window extends JFrame {
  
  MenuBarForWindow menuBarW = new MenuBarForWindow();

  //okre�lam wymiary okna
  public Window() {
      setSize(500, 500);
      setLayout(null);
  }

  public void windowMain() {
      Window window = new Window();
      window.setDefaultCloseOperation(EXIT_ON_CLOSE);
      window.menuBarW.setVisible(true);
  }

  
}

