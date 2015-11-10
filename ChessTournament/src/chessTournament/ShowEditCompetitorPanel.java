package chessTournament;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

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
		setLayout(new BorderLayout()); 
	    setVisible(true);
	    
		                       
        model = new DefaultTableModel(){
        	@Override
        	public boolean isCellEditable(int row, int column) {
        		if(column==0) return false;
        		return super.isCellEditable(row, column);
        	}
        	@Override
        	public Class<?> getColumnClass(int columnIndex) {
        		if(columnIndex==3 || columnIndex==4) return Integer.class;
        		return super.getColumnClass(columnIndex);
        	}
        };
        
        model.setColumnIdentifiers(columnNames);
        table = new JTable();
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        setData();
        
        JTextField jtf = new JTextField();
        jtf.setDocument(new PlainDocument() {
        	@Override
        	public void insertString(int offs, String str, AttributeSet a)
        			throws BadLocationException {
        		if(str.length()>50-offs) str = str.substring(0, 50-offs);
        		str = str.replaceAll("[^a-zA-ZżółćęśąźńŻÓŁĆĘŚĄŹŃ\\- ]+", "");
        		super.insertString(offs, str, a);
        	}
        });
        table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(jtf));
        table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(jtf));
        
        JComboBox<Integer> comboBox = new JComboBox<Integer>();
        comboBox.addItem(1);
        comboBox.addItem(2);
        comboBox.addItem(3);
        comboBox.addItem(4);
        comboBox.addItem(5);
        comboBox.addItem(6);
        table.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(comboBox));
        
        model.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				int row = e.getFirstRow();
				int column = e.getColumn();
				if(column>=0 && row>=0 && model.getRowCount()>row) {
					Database db = new Database();
					Object value = model.getValueAt(row, column); 
					System.out.print(
						value + "\n"
					);
					Competitor c = db.getCompetitors(2).get(row);
					try {
					switch(column) {
						case 1: c.setName((String)value); 			break;
						case 2: c.setSurname((String)value); 		break;
						case 3: c.setAge((int)value); 				break;
						case 4: c.setChessCategory((int)value); 	break;
					}
					} catch(ValidatorException exc) {
						System.out.print("Błąd walidacji");
					}
					db.insertOrUpdateCompetitor(c, 2);
					db.close();
				}
			}
		});
        
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int r = table.rowAtPoint(e.getPoint());
                if (r >= 0 && r < table.getRowCount()) {
                    table.setRowSelectionInterval(r, r);
                } else {
                    table.clearSelection();
                }

                final int rowindex = table.getSelectedRow();
                if (rowindex < 0)
                    return;
                if (e.isPopupTrigger() && e.getComponent() instanceof JTable ) {
                    JPopupMenu popup = new JPopupMenu();
                    JMenuItem jmi = new JMenuItem("Usuń");
                    jmi.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							Database db = new Database();
							Competitor c = db.getCompetitors(2).get(rowindex);
							db.removeCompetitor(c.getId());
							db.close();
							setData();
						}
					});
                    popup.add(jmi);
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
               
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
