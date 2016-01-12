package panel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

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
import res.Strings;

public class ShowEditCompetitorPanel extends JPanel{
	private final Tournament turniej;
	private final Database DB;
	private JTable table;
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
	    table = new JTable() {
	    	// po przejśiu do komórki (również tabulatorem) rozpoczęcie edycji
	    	public void changeSelection(int row, int column, boolean toggle, boolean extend) {
	    		super.changeSelection(row, column, toggle, extend);
    	        if(editCellAt(row, column)) {
    	            Component editor = getEditorComponent();
    	            editor.requestFocusInWindow();
    	        }
	    	}
	    };
	    table.setIntercellSpacing(new Dimension(25, 2));
	    table.setRowHeight(20);
	    model = new DefaultTableModel(){
        	// w kolumny wiek i kategoria można wprowadzać tylko liczby
        	@Override
        	public Class<?> getColumnClass(int c) { 
        		return (c==2 || c==3) ? Integer.class : super.getColumnClass(c);
        	}
        	@Override
        	public boolean isCellEditable(int row, int column) {
        		return turniej.isPlayersEditAllowed();
        	}
        };
        model.setColumnIdentifiers(new String[]{"Nazwisko", "Imię", "Wiek", "Kategoria"});
        // przy edycji tabeli - zapis do bazy
        model.addTableModelListener((e) -> {
			int row = e.getFirstRow();
			int column = e.getColumn();
			if(column>=0 && row>=0 && model.getRowCount()>row) {
				Object value = model.getValueAt(row, column); 
				Competitor c = DB.getCompetitors(turniej.getId()).get(row);
				try {
					switch(column) {
						case 0: c.setSurname((String)value); 		break;
						case 1: c.setName((String)value); 			break;
						case 2: c.setAge((int)value); 				break;
						case 3: c.setChessCategory((int)value); 	break;
					}
				} catch(ValidatorException exc) {
					System.out.print("Błąd walidacji\n"+exc.getMessage());
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
        // przy rozpoczęciu edyji zaznaczenie wszystkiego
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
                int r = table.rowAtPoint(e.getPoint());
                if(r >= 0 && r < table.getRowCount()) 
                    table.setRowSelectionInterval(r, r);
                else 
                	table.clearSelection();

                final int rowindex = table.getSelectedRow();
                if(rowindex < 0) return;
                if(e.isPopupTrigger() && e.getComponent() instanceof JTable ) {
                    JPopupMenu popup = new JPopupMenu();
                    if(turniej.isPlayersEditAllowed()) {
                        JMenuItem jmi = new JMenuItem(Strings.remove);
	                    // akcja po kliknięciu na "Usuń"
                        jmi.addActionListener(e2 -> {
							Competitor c = DB.getCompetitors(turniej.getId()).get(rowindex);
							DB.removeCompetitor(c.getId(), turniej.getId());
							model.fireTableRowsDeleted(rowindex, rowindex);
							setData();
						});
	                    popup.add(jmi);
                    }
                    if(turniej.isDisqualificationAllowed()) {
                        JMenuItem jmi = new JMenuItem(Strings.disqualify);
                    	jmi.addActionListener(e2 -> {
							Competitor c = DB.getCompetitors(turniej.getId()).get(rowindex);
							c.setIsDisqualified(true);
							DB.insertOrUpdateCompetitor(c,turniej.getId());
							setData();
                    	});
	                    popup.add(jmi);
                    }
                    if(popup.getComponentCount()>0) popup.show(e.getComponent(), e.getX(), e.getY());
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
		System.out.print("SECP.setData()");
		model.setRowCount(0);
		List<Competitor> competitors = DB.getCompetitors(turniej.getId());
        for(Competitor c : competitors){
        	model.addRow(new Object[]{
                c.getSurname(),
        		c.getName(),
            	c.getAge(),
            	c.getChessCategory()
            });
        }
        model.fireTableDataChanged();        
	}
}
