package chessTournament;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//klasa odpowiadaj�ca za rozwijane menu

import javax.swing.*;

@SuppressWarnings("serial")
public class MenuBarForWindow extends JFrame{
  private JMenuBar menuBar;
  private JMenu comp;
  private JMenuItem addC, showC;

  public MenuBarForWindow(){
	  setSize(1000,600);
	  setLayout(null);
	  
      menuBar = new JMenuBar();
      setJMenuBar(menuBar);
      
      comp = new JMenu("Uczestnicy");
      menuBar.add(comp);
      
      addC = new JMenuItem("Dodaj");
      comp.add(addC);
      showC = new JMenuItem("Wyświetl");
      comp.add(showC);
      
      
      
      addC.addActionListener(new ActionListener(){
    	 public void actionPerformed(ActionEvent arg0){
    		 new AddCompetitorWindow();
    	 }
      });
      
      showC.addActionListener(new ActionListener(){
     	 public void actionPerformed(ActionEvent arg0){
     		 new ShowCompetitorTableWindow().play();
     	 }
       });
      
  }
  
  public void windowMain(){
	  setDefaultCloseOperation(EXIT_ON_CLOSE);
	  setVisible(true);
  }

}
