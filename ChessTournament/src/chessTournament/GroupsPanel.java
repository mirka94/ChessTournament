package chessTournament;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class GroupsPanel extends JPanel{
	private final Tournament turniej;
	private final Database DB;
	/**
	 * @param t - id turnieju
	 * @param db - baza danych
	 */
	public GroupsPanel(Tournament t, Database db){
		this.turniej = t;
		this.DB = db;
		this.setLayout(new BorderLayout());
		JPanel container = new JPanel();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		add(new JScrollPane(container));
		DefaultTableModel model = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		model.setColumnIdentifiers(new String[]{"Imię", "Nazwisko", "Wiek", "Kategoria"});
		JTable table = new JTable();
        table.setModel(model);
        for(Competitor c : DB.getCompetitors(turniej.getId())){
        	model.addRow(new Object[]{
        		c.getName(),
            	c.getSurname(),
            	c.getAge(),
            	c.getChessCategory()
            });
        }
        model.fireTableDataChanged();
        container.add(table.getTableHeader());
        container.add(table);
		for(int i=0; i<50; i++) {
			container.add(new JLabel("Test "+i, JLabel.RIGHT));
			
		}
	}
}
