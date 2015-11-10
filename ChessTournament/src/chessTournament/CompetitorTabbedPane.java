package chessTournament;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")
public class CompetitorTabbedPane extends JFrame{
	JMenuBar menuBar;
	JMenu comp, about;
	JMenuItem addC, rndC, authors, manual;
	JPanel removePanel = new JPanel();
	ShowEditCompetitorPanel showPanel = new ShowEditCompetitorPanel();
	
	JTabbedPane tabbedPane = new JTabbedPane();
	
	public CompetitorTabbedPane(){
		setSize(700,700);
		setResizable(false);
		setTitle("Uczestnicy");
		setMenu();
		
	    setVisible(true);
	    
	    tabbedPane.addTab("Pokaż lub edytuj dodanych uczestników", showPanel);
	    tabbedPane.add("Usuń wybranego uczestnika", removePanel);
	    tabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if(tabbedPane.getSelectedIndex()==0) {
					showPanel.setData();
					comp.setVisible(true);
				} else {
					comp.setVisible(false);
				}
			}
		});
	    	    	    
	    add(tabbedPane);
	}
	
	private void setMenu() {
		menuBar = new JMenuBar();
		comp = new JMenu("Uczestnicy");
		about = new JMenu("O programie");
		addC = new JMenuItem("Dodaj");
		rndC = new JMenuItem("Dodaj losowego gracza");
		authors = new JMenuItem("Autorzy");
		manual = new JMenuItem("Pomoc");
		comp.add(addC);
		comp.add(rndC);
		about.add(authors);
		about.add(manual);
		menuBar.add(comp);
		menuBar.add(about);
		setJMenuBar(menuBar);
		addC.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				 System.out.print("Dodaj");
				 Database DB = new Database();
				 Competitor c = new Competitor(null, "Imie", "Nazwisko", 0, 0, false);
				 DB.insertOrUpdateCompetitor(c, 2);
				 DB.close();
				 showPanel.setData();
			}
		});
		rndC.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.print("Dodaj");
				 Database DB = new Database();
				 Competitor c = Simulator.RandomPlayer();
				 DB.insertOrUpdateCompetitor(c, 2);
				 DB.close();
				 showPanel.setData();
			}
		});
	}
}
