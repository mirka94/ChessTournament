package window;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import model.Database;
import model.Tournament;
import panel.CompetitorTabbedPane;

public class AddTPanel extends JPanel {
	private JTextField textField;
	private String nazwa;

	public AddTPanel(final JFrame jframe){
		
		setLayout(null);
		
		JLabel nameTour = new JLabel("Nazwa turnieju");
		nameTour.setFont(new Font("Consolas", Font.PLAIN, 16));
		nameTour.setHorizontalTextPosition(SwingConstants.CENTER);
		nameTour.setHorizontalAlignment(SwingConstants.CENTER);
		nameTour.setBounds(100, 100, 484, 30);
		add(nameTour);
		
		textField = new JTextField();
		textField.setBounds(100, 141, 484, 25);
		add(textField);
		textField.setColumns(10);
		
		JButton addButton = new JButton("Utwórz");
		addButton.setFont(new Font("Consolas", Font.PLAIN, 16));
		addButton.setBounds(100, 293, 484, 30);
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nazwa=textField.getText();
				String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
				Tournament t = new Tournament(null,nazwa,year,8,5,-1,Tournament.Type.GROUP_ELIMINATIONS);
				Database db = new Database();
				db.insertOrUpdateTournament(t);
				jframe.getContentPane().removeAll();
				new CompetitorTabbedPane(t, jframe);
				db.close();
			}
		});
		
		// po naciśnięciu enter aktywuje się guzik DODAJ
		jframe.getRootPane().setDefaultButton(addButton);
		
		add(addButton);
		
	}
}
