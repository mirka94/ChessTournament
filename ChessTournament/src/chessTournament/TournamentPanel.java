package chessTournament;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class TournamentPanel extends JPanel{
	private static final long serialVersionUID = -6884083935424698553L;
	private final Tournament turniej;
	private final Database DB;
	private final String roundRobinT = "Kołowy z eliminacjami";
	private final String sgTimeT = "Średni czas na rozgrwkę: ";
	private final String groupsT = "Ilość drużyn: ";
	private final String roundsT = "Ilość rund: ";
	private final String swissT = "Systemem szwajcarskim";
	private final String gamesT = "Rozgrywek: ";
	private final String timeST = "Przewidywany czas: ";
	private final String timeRRET = "Przewidywany czas eliminacji: ";
	private final JLabel nameL, yearL, typeL, sgTimeL, roundsL, groupsL, stats1L, stats2L;
	final JTextField nameTF, yearTF;
	final JToggleButton typeTB;
	final Scrollbar roundsSB, groupsSB, sgTimeSB;
	/**
	 * @param t - id turnieju
	 * @param db - baza danych
	 */
	public TournamentPanel(Tournament t, Database db){
		this.turniej = t;
		this.DB = db;
		nameL 	= new JLabel("Nazwa turnieju: ");
		yearL 	= new JLabel("Rok: ");
		typeL	= new JLabel("Typ turnieju: ");
		sgTimeL = new JLabel(sgTimeT+"10 min");
		roundsL = new JLabel(roundsT+"3");
		groupsL = new JLabel(groupsT+"2");
		stats1L = new JLabel(gamesT);
		stats2L = new JLabel(timeRRET);
		nameTF 	= new JTextField();
		yearTF  = new JTextField();
		typeTB 	= new JToggleButton(roundRobinT, true);
		roundsSB 	= new Scrollbar(Scrollbar.HORIZONTAL, 3, 1, 3, 9);
		groupsSB 	= new Scrollbar(Scrollbar.HORIZONTAL, 2, 1, 2, 9);
		sgTimeSB 	= new Scrollbar(Scrollbar.HORIZONTAL, 20, 4, 2, 40);
		
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(10, 10, 10, 10));
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 2, 10, 20));
		add(panel, BorderLayout.NORTH);
		//nameL.setHorizontalAlignment(JLabel.RIGHT);
		//yearL.setHorizontalAlignment(JLabel.RIGHT);
		nameTF.setDocument(new MainProgram.MyPlainDocument());
		yearTF.setDocument(new MainProgram.MyPlainDocument());
		
		setComponentsActions();
		
		panel.add(nameL);
		panel.add(nameTF);
		panel.add(yearL);
		panel.add(yearTF);
		panel.add(typeL);
		panel.add(typeTB);
		panel.add(sgTimeL);
		panel.add(sgTimeSB);
		panel.add(roundsL);
		panel.add(roundsSB);
		panel.add(groupsL);
		panel.add(groupsSB);
		panel.add(stats1L);
		panel.add(stats2L);
		nameTF.setText(turniej.getName());
		yearTF.setText(turniej.getYear());
		recalcStats();
	}
	
	private void setComponentsActions() {
		nameTF.getDocument().addDocumentListener(new DocumentListener() {
			@Override public void removeUpdate(DocumentEvent e) {System.out.print(nameTF.getText()+"\n");}
			@Override public void insertUpdate(DocumentEvent e) {System.out.print(nameTF.getText()+"\n");}
			@Override
			public void changedUpdate(DocumentEvent e) {
				System.out.print(nameTF.getText());
			}
		});
		yearTF.getDocument().addDocumentListener(new DocumentListener() {
			@Override public void removeUpdate(DocumentEvent e) {}
			@Override public void insertUpdate(DocumentEvent e) {}
			@Override
			public void changedUpdate(DocumentEvent e) {
				System.out.print(yearTF.getText());
			}
		});
		typeTB.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				typeTB.setText(typeTB.isSelected()?roundRobinT:swissT);
				recalcStats();
			}
		});
		roundsSB.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				int v = roundsSB.getValue();
				roundsL.setText(roundsT+v);
				recalcStats();
			}
		});
		groupsSB.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				int v = groupsSB.getValue();
				groupsL.setText(groupsT+v);
				recalcStats();
			}
		});
		sgTimeSB.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				float v = sgTimeSB.getValue()/2f;
				sgTimeL.setText(sgTimeT+v+" min");
				recalcStats();
			}
		});
	}
	
	public void recalcStats() {
		int graczy = DB.getCompetitors(turniej.getId()).size();
		float czasSG = sgTimeSB.getValue()/2f;
		if(typeTB.isSelected()) {
			int grup = groupsSB.getValue();
			int rozgrywek = Simulator.rozgrywek_eliminacje(graczy, grup);
			stats1L.setText(gamesT+String.valueOf(rozgrywek));
			int gier_naraz = (int)Math.min(Math.floor(graczy/2), turniej.getBoards());
			stats2L.setText(timeRRET+Math.ceil(rozgrywek/gier_naraz)*czasSG+" min");
		} 
		else {
			int rund = roundsSB.getValue();
			stats1L.setText(gamesT+String.valueOf((int)Math.floor(graczy/2)*rund));
			stats2L.setText(timeST+Math.ceil(Math.floor(graczy/2)/turniej.getBoards())*rund*czasSG+" min");
		}
	}
}
