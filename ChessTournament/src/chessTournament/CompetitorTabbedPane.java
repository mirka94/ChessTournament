package chessTournament;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

@SuppressWarnings("serial")
public class CompetitorTabbedPane extends JFrame{
	
	JPanel removePanel = new JPanel();
	JPanel showPanel = new ShowEditCompetitorPanel();
	
	JTabbedPane tabbedPane = new JTabbedPane();
	
	public CompetitorTabbedPane(){
		setSize(700,700);
		setResizable(false);
		setTitle("Uczestnicy");
	    setVisible(true);
	    
	    tabbedPane.add("Dodaj nowego uczestnika", new AddCompetitorPanel());
	    tabbedPane.addTab("Pokaż lub edytuj dodanych uczestników", showPanel);
	    tabbedPane.add("Usuń wybranego uczestnika", removePanel);
	    	    	    
	    add(tabbedPane);
	    
	    
	    
	}
	
	
}
