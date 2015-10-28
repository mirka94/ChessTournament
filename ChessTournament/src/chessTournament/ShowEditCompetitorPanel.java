package chessTournament;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class ShowEditCompetitorPanel extends JPanel{

	private JTable table;
	private DefaultTableModel model;
	private Database db;
	private Object[] columnNames={
			"ID",
			"Imie",
			"Nazwisko",
			"Wiek",
			"Kategoria"
		};
	private Object[] rowData = new Object[5];;
	
	public ShowEditCompetitorPanel(){
		setSize(700,700);
	    setVisible(true);
	    
		                       
        model = new DefaultTableModel(){
        	@Override
        	public boolean isCellEditable(int row, int column) {
        		if(column==0) return false;
        		return super.isCellEditable(row, column);
        	}
        };
        
        model.setColumnIdentifiers(columnNames);
        
        
        table = new JTable();
        setData();
        
        JScrollPane scrollPane = new JScrollPane(table);
	    add(scrollPane, BorderLayout.CENTER);
	    setLayout(new BorderLayout());        
        JScrollPane pane = new JScrollPane(table);
        add(pane,BorderLayout.CENTER);
        
	}
	
	
	public void setData() {
		db = new Database();
		model.setRowCount(0);
        for(Competitor c : db.getCompetitors(2)){
            rowData[0] = c.getId();
            rowData[1] = c.getName();
            rowData[2] = c.getSurname();
            rowData[3] = c.getAge();
            rowData[4] = c.getChessCategory();
               
            model.addRow(rowData);
        }
        
        table.setModel(model);
        model.fireTableDataChanged();
        db.close();
	}
}
