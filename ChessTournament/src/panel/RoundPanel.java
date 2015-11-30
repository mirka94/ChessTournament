package panel;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import model.Database;
import model.SingleGame;

@SuppressWarnings("serial")
public class RoundPanel extends JPanel{

	private Database db;
	private JTable table;
	private DefaultTableModel model;
	private JButton nextButton;
	
	public RoundPanel(){ //Tournament turniej
		
		setLayout(new BorderLayout());
		setVisible(true);
		
		db = new Database();
		table = new JTable();
		nextButton = new JButton("Zakończ tę rundę");
		model = new DefaultTableModel();
		
		model.setColumnIdentifiers(new String[]{"Gracz 1", "Gracz 2"});		
		table.setModel(model);
		
		setData();
		
		table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int r = table.rowAtPoint(e.getPoint());
                if(r >= 0 && r < table.getRowCount()) 
                    table.setRowSelectionInterval(r, r);
                else 
                	table.clearSelection();

                final int rowindex = table.getSelectedRow();
                if(rowindex < 0) return;
                if(e.isPopupTrigger() && e.getComponent() instanceof JTable ) {
                    JPopupMenu popup = new JPopupMenu();
                    JMenuItem jmi1 = new JMenuItem("Wygrał gracz 1");
                    JMenuItem jmi2 = new JMenuItem("Wygrał gracz 2");
                    // akcja po kliknięciu
                    //Competitor c = db.getCompetitors(turniej.getId()).get(rowindex);
                	//SingleGame sg = db.getSingleGames().get(rowindex);
                    
                    jmi1.addActionListener(e2 -> {
                    	/*if(c.getId()==sg.getCompetitor1()){
                    		c.setStage(1);
                    		db.insertOrUpdateCompetitor(c, turniej.getId());
                    	}*/
                    	
					});
                    jmi2.addActionListener(e2 -> {
                    	/*if(c.getId()==sg.getCompetitor1()){
                    		c.setStage(1);
                    		db.insertOrUpdateCompetitor(c, turniej.getId());
                    	}*/
					});
                    popup.add(jmi1);
                    popup.add(jmi2);
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
		
		/*nextButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setData();
			}
		});*/
		
		add(nextButton, BorderLayout.PAGE_END);
		add(new JScrollPane(table), BorderLayout.CENTER);
		
	}
	
	public void setData() {
		model.setRowCount(0);
        for(SingleGame sg : db.getSingleGames()){
        	model.addRow(new Object[]{
        		sg.getCompetitor1(),
            	sg.getCompetitor2()
            });
        }
        model.fireTableDataChanged();        
	}
	
	
}
