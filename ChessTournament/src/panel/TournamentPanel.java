package panel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Scrollbar;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import model.Database;
import model.Tournament;
import res.Strings;
import tools.MyPlainDocument;
import tools.Simulator;

public class TournamentPanel extends JPanel{
	private static final long serialVersionUID = -3657045958131642437L;
	private final Tournament turniej;
	private final Database DB;
	private final JLabel nameL, yearL, sgTimeL, groupsL, boardsL, stats1L, stats2L;
	final JTextField nameTF, yearTF;
	final Scrollbar groupsSB, sgTimeSB, boardsSB;
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
		sgTimeL = new JLabel(Strings.sgTimeT+"10 min");
		groupsL = new JLabel(Strings.groupsT+"2");
		boardsL = new JLabel(Strings.boardsT);
		stats1L = new JLabel(Strings.gamesT);
		stats2L = new JLabel(Strings.timeRRET);
		nameTF 	= new JTextField();
		yearTF  = new JTextField();
		groupsSB 	= new Scrollbar(Scrollbar.HORIZONTAL, 2, 1, 2, 9+1);
		sgTimeSB 	= new Scrollbar(Scrollbar.HORIZONTAL, 20, 4, 2, 40+4);
		boardsSB 	= new Scrollbar(Scrollbar.HORIZONTAL, 8, 1, 2, 20+1);
		
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(10, 10, 10, 10));
		panel.setLayout(new GridLayout(0, 2, 10, 20));
		add(panel, BorderLayout.NORTH);
		nameTF.setDocument(new MyPlainDocument());
		yearTF.setDocument(new MyPlainDocument());
		
		setComponentsActions();
		
		Component[] cs = {
			nameL, nameTF, yearL, yearTF, sgTimeL, sgTimeSB, groupsL, groupsSB, boardsL, boardsSB, stats1L, stats2L
		};
		for(Component c : cs) panel.add(c);

		nameTF.setText(turniej.getName());
		yearTF.setText(turniej.getYear());
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
		int grup = groupsSB.getValue();
		int rozgrywek = Simulator.rozgrywek_eliminacje(graczy, grup);
		stats1L.setText(Strings.gamesT+String.valueOf(rozgrywek));
		int gier_naraz = (int)Math.min(Math.floor(graczy/2), turniej.getBoards());
		stats2L.setText(Strings.timeRRET+Math.ceil(rozgrywek/gier_naraz)*czasSG+" min");
	}
	public void setSBBounds() {
		int graczy = DB.getCompetitors(turniej.getId()).size();
		groupsSB.setMinimum(2);
		groupsSB.setMaximum((int)Math.ceil(graczy/2)+2);
		groupsSB.setValue(turniej.getRounds());
		recalcStats();
	}
}
