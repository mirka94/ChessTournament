package chessTournament;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class MainWindow extends JFrame{
	
	JButton bCompetitors, bTournament, bRestart;
	
	public MainWindow(){
		setSize(700,700);
		setResizable(false);
		setLayout(null);
		setVisible(true);
		
		bCompetitors = new JButton("Uczestnicy");
		bCompetitors.setBounds(15, 200, 660, 50);
		add(bCompetitors);
		
		bTournament = new JButton("Turniej");
		bTournament.setBounds(15, 300, 660, 50);
		add(bTournament);
		
		bRestart = new JButton("Restart");
		bRestart.setBounds(15, 400, 660, 50);
		add(bRestart);
		
		bCompetitors.addActionListener(new ActionListener() {
	          public void actionPerformed(ActionEvent e) {
	        	  new CompetitorTabbedPane();
	          }
	      });
		
	}

}
