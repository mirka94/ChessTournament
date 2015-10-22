package chessTournament;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//klasa odpowiadaj¹ca za rozwijane menu

import javax.swing.*;

@SuppressWarnings("serial")
public class MenuBarForWindow extends JFrame{
  JMenuBar menuBar;
  JMenu comp;
  JMenuItem addC;
  AddCompetitorWindow acw = new AddCompetitorWindow();

  public MenuBarForWindow(){
	  setSize(500,500);
	  
      menuBar = new JMenuBar();
      setJMenuBar(menuBar);
      
      comp = new JMenu("Uczestnicy");
      menuBar.add(comp);
      
      addC = new JMenuItem("Dodaj");
      comp.add(addC);
      
      addC.addActionListener(new ActionListener(){
    	 public void actionPerformed(ActionEvent arg0){
    		 acw.addCompetitor();
    	 }
      });
  }

}
