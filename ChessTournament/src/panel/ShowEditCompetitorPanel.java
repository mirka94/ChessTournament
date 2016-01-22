package panel;

import java.awt.BorderLayout;
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
import javax.swing.table.AbstractTableModel;

import model.Competitor;
import model.Database;
import model.Tournament;
import res.Strings;
import tools.Dialogs;
import tools.MyPlainDocument;
import tools.ValidatorException;

/**
 * Zakładka dodawania i edycji uczestników
 */
public class ShowEditCompetitorPanel extends JPanel{
	private static final long serialVersionUID = 5820295484965265306L;
	private final Tournament turniej;
	private final Database DB;
	private JTable table;
	List<Competitor> competitors;
	
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
	    table = new EditCompetitorJTable();
        
        // po klikinęciu prawym na tabelę - pokazanie opcji "usuń" / "dyskwalifikuj"
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
					Competitor c = competitors.get(rowindex);
                    if(turniej.isPlayersEditAllowed()) {
                        JMenuItem jmi = new JMenuItem(Strings.remove);
                        jmi.addActionListener(e2 -> {
							DB.removeCompetitor(c.getId(), turniej.getId());
							setData();
						});
	                    popup.add(jmi);
                    }
                    if(turniej.isDisqualificationAllowed() && !c.getIsDisqualified()) {
                        JMenuItem jmi = new JMenuItem(Strings.disqualify);
                    	jmi.addActionListener(e2 -> {
                    		if(!Dialogs.czyZdyskwalifikowac(c)) return;		
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
		competitors=DB.getCompetitors(turniej.getId());
		((EditCompetitorTableModel)table.getModel()).fireTableDataChanged();     
	}
	
	public void selectLast() {
		table.changeSelection(table.getRowCount()-1, 1, false,  false);
	}
	
	public class EditCompetitorJTable extends JTable {
		private static final long serialVersionUID = -9074329149984999956L;

		public EditCompetitorJTable() {
			super();
			setIntercellSpacing(new Dimension(25, 2));
		    setRowHeight(20);
		    setModel(new EditCompetitorTableModel());
		    
		    setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION); 
	        // pole tekstowe akceptujące tylko znaki a-Z, - i spację
	        final JTextField jtf = new JTextField(new MyPlainDocument(), null, 0);
	        // przy rozpoczęciu edyji zaznaczenie wszystkiego
	        jtf.addFocusListener(new FocusAdapter() {
				@Override
				public void focusGained(FocusEvent e) {
					jtf.selectAll();
				}
			});
	        
	        // dla pól imię i nazwisko ustawiony edytor na podstawie powyższego pola tekstowego 
	        columnModel.getColumn(1).setCellEditor(new DefaultCellEditor(jtf));
	        columnModel.getColumn(2).setCellEditor(new DefaultCellEditor(jtf));
	        columnModel.getColumn(4).setCellEditor(new DefaultCellEditor(
	        	new JComboBox<Integer>(new Integer[]{1,2,3,4,5,6})
	        ));
		}
		
		// po przejśiu do komórki (również tabulatorem) rozpoczęcie edycji
    	public void changeSelection(int row, int column, boolean toggle, boolean extend) {
    		super.changeSelection(row, column, toggle, extend);
	        if(editCellAt(row, column)) {
	            getEditorComponent().requestFocusInWindow();
	        }
    	}
	}
	
	protected class EditCompetitorTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 5974959488451548395L;
		final String[] columnNames = {"L.p.", "Nazwisko", "Imię", "Wiek", "Kategoria"};
		@Override
    	public Class<?> getColumnClass(int c) { 
    		return (c==3 || c==4) ? Integer.class : String.class;
    	}
    	@Override
    	public boolean isCellEditable(int row, int column) {
    		if(column==1 || column==2 || column==3) return true;
    		return turniej.isPlayersEditAllowed();
    	}
		@Override
		public int getColumnCount() {
			return 5;
		}
		@Override
		public String getColumnName(int columnIndex) {
			return columnNames[columnIndex];
		}
		@Override
		public int getRowCount() {
			return competitors.size();
		}
		@Override
		public Object getValueAt(int row, int col) {
			Competitor c = competitors.get(row);
			if(col==0) return row+1;
			if(col==1) return c.getSurname();
			if(col==2) return c.getName();
			if(col==3) return c.getAge();
			if(col==4) return c.getChessCategory();
	        return null;
		}
		@Override
		public void setValueAt(Object value, int row, int column) {
			Competitor c = competitors.get(row);
			try {
				switch(column) {
					case 1: c.setSurname((String)value); 		break;
					case 2: c.setName((String)value); 			break;
					case 3: c.setAge((int)value); 				break;
					case 4: c.setChessCategory((int)value); 	break;
				}
				DB.insertOrUpdateCompetitor(c, turniej.getId());
			} catch(ValidatorException exc) {
				System.out.print("Błąd walidacji\n"+exc.getMessage());
			}
		}
	}
}