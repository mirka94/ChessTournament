package window;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import model.Database;
import model.Tournament;
import panel.CompetitorTabbedPane;

public class ShowTPanel extends JPanel {
	private static final long serialVersionUID = -1094699102373510646L;
	private Database db;

	public ShowTPanel(final JFrame jframe) {
		db = new Database();
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
		    	int sIndex = comboBox.getSelectedIndex();
		    	jframe.getContentPane().removeAll();
		    	new CompetitorTabbedPane(db.getTournaments().get(sIndex),jframe);
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
