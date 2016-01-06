package panel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Scrollbar;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import chessTournament.MainProgram;
import model.Database;
import model.Tournament;
import res.Strings;
import tools.Simulator;

public class TournamentPanel extends JPanel{
	private final Tournament turniej;
	private final Database DB;
	private final JLabel nameL, yearL, typeL, sgTimeL, roundsL, groupsL, boardsL, stats1L, stats2L;
	final JTextField nameTF, yearTF;
	final JToggleButton typeTB;
	final Scrollbar roundsSB, groupsSB, sgTimeSB, boardsSB;
	final JPanel panel = new JPanel();
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
		sgTimeL = new JLabel(Strings.sgTimeT+"10 min");
		roundsL = new JLabel(Strings.roundsT+"3");
		groupsL = new JLabel(Strings.groupsT+"2");
		boardsL = new JLabel(Strings.boardsT);
		stats1L = new JLabel(Strings.gamesT);
		stats2L = new JLabel(Strings.timeRRET);
		nameTF 	= new JTextField();
		yearTF  = new JTextField();
		typeTB 	= new JToggleButton("", turniej.isSwiss());
		roundsSB 	= new Scrollbar(Scrollbar.HORIZONTAL, 3, 1, 3, 9+1);
		groupsSB 	= new Scrollbar(Scrollbar.HORIZONTAL, 2, 1, 2, 9+1);
		sgTimeSB 	= new Scrollbar(Scrollbar.HORIZONTAL, 20, 4, 2, 40+4);
		boardsSB 	= new Scrollbar(Scrollbar.HORIZONTAL, 8, 1, 2, 20+1);
		
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(10, 10, 10, 10));
		panel.setLayout(new GridLayout(0, 2, 10, 20));
		add(panel, BorderLayout.NORTH);
		nameTF.setDocument(new MainProgram.MyPlainDocument());
		yearTF.setDocument(new MainProgram.MyPlainDocument());
		
		setComponentsActions();
		
		Component[] cs = {
			nameL, nameTF, yearL, yearTF, typeL, typeTB, sgTimeL, sgTimeSB, roundsL, 
			roundsSB, groupsL, groupsSB, boardsL, boardsSB, stats1L, stats2L
		};
		for(Component c : cs) panel.add(c);

		nameTF.setText(turniej.getName());
		yearTF.setText(turniej.getYear());
		typeTB.doClick();
		setSBBounds();
		recalcStats();
	}
	
	private void setComponentsActions() {
		nameTF.getDocument().addDocumentListener(new MyDocumentListener() {			
			@Override
			public void action() {
				turniej.setName(nameTF.getText());
				DB.insertOrUpdateTournament(turniej);
			}
		});
		yearTF.getDocument().addDocumentListener(new MyDocumentListener() {
			@Override
			public void action() {
				turniej.setYear(yearTF.getText());
				DB.insertOrUpdateTournament(turniej);
			}
		});
		turniej.addTypeChangeListener((type) -> {
			if(type==Tournament.Type.GROUP_ELIMINATIONS) {
				typeTB.setText(Strings.roundRobinT);
				groupsL.setVisible(true);
				groupsSB.setVisible(true);
				roundsL.setVisible(false);
				roundsSB.setVisible(false);
			}
			else {
				typeTB.setText(Strings.swissT);
				groupsL.setVisible(false);
				groupsSB.setVisible(false);
				roundsL.setVisible(true);
				roundsSB.setVisible(true);
			}
			recalcStats();
		});
		typeTB.addChangeListener(e -> {
				if(!turniej.isPlayersEditAllowed()) return;
				if(typeTB.isSelected()) {
					turniej.setType(Tournament.Type.GROUP_ELIMINATIONS);
				}
				else {
					turniej.setType(Tournament.Type.SWISS);
				}
				DB.insertOrUpdateTournament(turniej);
		});
		roundsSB.addAdjustmentListener(e -> {
				if(!turniej.isPlayersEditAllowed()) {
					roundsSB.setValue(turniej.getRounds());
					return;
				}
				int v = roundsSB.getValue();
				roundsL.setText(Strings.roundsT+v);
				turniej.setRounds(v);
				DB.insertOrUpdateTournament(turniej);
				recalcStats();
		});
		groupsSB.addAdjustmentListener(e -> {
				if(!turniej.isPlayersEditAllowed()) {
					groupsSB.setValue(turniej.getRounds());
					return;
				}
				int g = groupsSB.getValue();
				turniej.setRounds(g);
				DB.insertOrUpdateTournament(turniej);
				recalcStats();
		});
		boardsSB.addAdjustmentListener(e -> {
			if(!turniej.isPlayersEditAllowed()) {
				boardsSB.setValue(turniej.getBoards());
				return;
			}
			int g = boardsSB.getValue();
			turniej.setBoards(g);
			DB.insertOrUpdateTournament(turniej);
			recalcStats();
	});
		sgTimeSB.addAdjustmentListener(e -> {
				recalcStats();
		});
	}
	
	private abstract class MyDocumentListener implements DocumentListener {
		public abstract void action();
		@Override public final void removeUpdate(DocumentEvent e) { action(); }
		@Override public final void insertUpdate(DocumentEvent e) { action(); }
		@Override public final void changedUpdate(DocumentEvent e){ action(); }
	}
	
	public void recalcStats() { // TODO - poprawić przewidywany czas turnieju
		groupsL.setText(Strings.groupsT+groupsSB.getValue());
		boardsL.setText(Strings.boardsT+boardsSB.getValue());
		sgTimeL.setText(Strings.sgTimeT+(sgTimeSB.getValue()/2f)+" min");
		int graczy = DB.getCompetitors(turniej.getId()).size();
		if(turniej.getBoards()<1 || graczy<2) return;
		float czasSG = sgTimeSB.getValue()/2f;
		if(typeTB.isSelected()) {
			int grup = groupsSB.getValue();
			int rozgrywek = Simulator.rozgrywek_eliminacje(graczy, grup);
			stats1L.setText(Strings.gamesT+String.valueOf(rozgrywek));
			int gier_naraz = (int)Math.min(Math.floor(graczy/2), turniej.getBoards());
			stats2L.setText(Strings.timeRRET+Math.ceil(rozgrywek/gier_naraz)*czasSG+" min");
		} 
		else {
			int rund = roundsSB.getValue();
			stats1L.setText(Strings.gamesT+String.valueOf((int)Math.floor(graczy/2)*rund));
			stats2L.setText(Strings.timeST+Math.ceil(Math.floor(graczy/2)/turniej.getBoards())*rund*czasSG+" min");
		}
	}
	public void setSBBounds() {
		int graczy = DB.getCompetitors(turniej.getId()).size();
		roundsSB.setMinimum(1);
		roundsSB.setMaximum((int)Math.ceil(graczy/1.5)+1);
		groupsSB.setMinimum(2);
		groupsSB.setMaximum((int)Math.ceil(graczy/2)+2);
		roundsSB.setValue(turniej.getRounds());
		groupsSB.setValue(turniej.getRounds());
		recalcStats();
	}
}
