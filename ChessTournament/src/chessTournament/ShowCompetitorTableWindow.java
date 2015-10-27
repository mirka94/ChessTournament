package chessTournament;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class ShowCompetitorTableWindow extends JFrame{
	
	private JTable table;
	//private List<Competitor> competitorList;
	private DefaultTableModel model;
	//private Competitor c;
	private Database db;
	
	public ShowCompetitorTableWindow() {
	    setSize(1000,600);
	    setVisible(true);
    }


	public void play(){
		
		Object[] columnNames={
				"ID",
				"Imie",
				"Nazwisko",
				"Wiek",
				"Kategoria"
			};
                       
        model = new DefaultTableModel();
        model.setColumnIdentifiers(columnNames);
        
        Object[] rowData = new Object[5];
        table = new JTable();
        db = new Database();
        for(Competitor c : db.getCompetitors(2)){
            
            rowData[0] = c.getId();
             rowData[1] = c.getName();
              rowData[2] = c.getSurname();
               rowData[3] = c.getAge();
               	rowData[4] = c.getChessCategory();
               
               model.addRow(rowData);
        }
        db.close();
        table.setModel(model);
        
        //System.out.println(getUsers().size());
        
        MenuBarForWindow window = new MenuBarForWindow();
        JPanel panel = new JPanel();
        JScrollPane scrollPane = new JScrollPane(table);
	    add(scrollPane, BorderLayout.CENTER);
	    setLayout(new BorderLayout());        
        JScrollPane pane = new JScrollPane(table);
        add(pane,BorderLayout.CENTER);
        window.setContentPane(panel);
	}
}
