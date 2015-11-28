package panel;

import java.awt.BorderLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
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
import javax.swing.table.DefaultTableModel;

import chessTournament.MainProgram;
import chessTournament.ValidatorException;
import model.Competitor;
import model.Database;
import model.Tournament;

@SuppressWarnings("serial")
public class ShowEditCompetitorPanel extends JPanel{
	private final Tournament turniej;
	private final Database DB;
	private JTable table = new JTable();
	private DefaultTableModel model;
	
	/**
	 * @param t - id turnieju
	 * @param db - baza danych
	 */
	public ShowEditCompetitorPanel(Tournament t, Database db){
		this.turniej = t;
		this.DB = db;
		setSize(700,700);
		setLayout(new BorderLayout()); 
	    setVisible(true);
	    
	    model = new DefaultTableModel(){
        	// w kolumny wiek i kategoria można wprowadzać tylko liczby
        	@Override
        	public Class<?> getColumnClass(int c) { 
        		return (c==2 || c==3) ? Integer.class : super.getColumnClass(c);
        	}
        	@Override
        	public boolean isCellEditable(int row, int column) {
        		return isEditAllowed();
        	}
        };
        model.setColumnIdentifiers(new String[]{"Imię", "Nazwisko", "Wiek", "Kategoria"});
        // przy edycji tabeli - zapis do bazy
        model.addTableModelListener((e) -> {
			int row = e.getFirstRow();
			int column = e.getColumn();
			if(column>=0 && row>=0 && model.getRowCount()>row) {
				Object value = model.getValueAt(row, column); 
				Competitor c = DB.getCompetitors(turniej.getId()).get(row);
				try {
					switch(column) {
						case 0: c.setName((String)value); 			break;
						case 1: c.setSurname((String)value); 		break;
						case 2: c.setAge((int)value); 				break;
						case 3: c.setChessCategory((int)value); 	break;
					}
				} catch(ValidatorException exc) {
					System.out.print("Błąd walidacji\n");
				}
				DB.insertOrUpdateCompetitor(c, turniej.getId());
			}
		});


	    // USTAWIENIA TABELI;
        table.setModel(model);
	    // zaznaczanie tylko jednego wiersza (mamy proste usuwanie)
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION); 
        // pole tekstowe akceptujące tylko znaki a-Z, - i spację
        final JTextField jtf = new JTextField();
        jtf.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				jtf.selectAll();
			}
		});
        jtf.setDocument(new MainProgram.MyPlainDocument());
        // dla pól imię i nazwisko ustawiony edytor na podstawie powyższego pola tekstowego 
        table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(jtf));
        table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(jtf));
        // dla kategorii szachowej wybór z listy wartości
        table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(
        		new JComboBox<Integer>(new Integer[]{1,2,3,4,5,6})
        ));
        // po klikinęciu prawym na tabelę - pokazanie opcji "usuń"
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
            	if(!isEditAllowed()) return;
                int r = table.rowAtPoint(e.getPoint());
                if(r >= 0 && r < table.getRowCount()) 
                    table.setRowSelectionInterval(r, r);
                else 
                	table.clearSelection();

                final int rowindex = table.getSelectedRow();
                if(rowindex < 0) return;
                if(e.isPopupTrigger() && e.getComponent() instanceof JTable ) {
                    JPopupMenu popup = new JPopupMenu();
                    JMenuItem jmi = new JMenuItem("Usuń");
                    // akcja po kliknięciu na "Usuń"
                    jmi.addActionListener(e2 -> {
							Competitor c = DB.getCompetitors(turniej.getId()).get(rowindex);
							DB.removeCompetitor(c.getId());
							model.fireTableRowsDeleted(rowindex, rowindex);
							setData();
					});
                    popup.add(jmi);
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
         
        setData();      
        add(new JScrollPane(table),BorderLayout.CENTER);
	}
	
	/**
	 * odświeża widok tabeli danymi pobranymi z bazy
	 */
	public void setData() {
		model.setRowCount(0);
        for(Competitor c : DB.getCompetitors(turniej.getId())){
        	model.addRow(new Object[]{
        		c.getName(),
            	c.getSurname(),
            	c.getAge(),
            	c.getChessCategory()
            });
        }
        model.fireTableDataChanged();        
	}
	
	public boolean isEditAllowed() {
		return turniej.getRoundsCompleted()<0;
	}
}
