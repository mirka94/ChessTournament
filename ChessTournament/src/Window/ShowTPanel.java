package Window;

import javax.swing.JPanel;
import java.awt.Dimension;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import model.Database;
import model.Tournament;
import panel.CompetitorTabbedPane;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@SuppressWarnings("serial")
public class ShowTPanel extends JPanel {
	private Database db;

	/**
	 * Create the panel.
	 */
	public ShowTPanel() {
		
		db = new Database();
		
		setMinimumSize(new Dimension(684, 440));
		setMaximumSize(new Dimension(684, 440));
		setLayout(null);
		
		final JComboBox<String> comboBox = new JComboBox<String>();
		comboBox.setFont(new Font("Consolas", Font.PLAIN, 15));
		comboBox.setBounds(10, 100, 664, 30);
		 
		for(Tournament t: db.getTournaments()){
			String nazwa = t.getName();
			comboBox.addItem(nazwa);
		}
		
		comboBox.addActionListener (new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		    	String value = comboBox.getSelectedItem().toString();
		    	int tId = db.getTournamentId(value);
		    	new CompetitorTabbedPane(db.getTournaments().get(tId-1));
				db.close();
		    }
		});
			
		add(comboBox);
		
		JLabel wybierz = new JLabel("Wybierz turniej");
		wybierz.setFont(new Font("Consolas", Font.PLAIN, 16));
		wybierz.setHorizontalTextPosition(SwingConstants.CENTER);
		wybierz.setHorizontalAlignment(SwingConstants.CENTER);
		wybierz.setBounds(10, 59, 664, 30);
		add(wybierz);
		
		

	}
	
}
