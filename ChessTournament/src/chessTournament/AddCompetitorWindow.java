package chessTournament;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class AddCompetitorWindow {
	
	JLabel lName, lSurname, lAge, lCategory;
	JTextField tName, tSurname, tAge, tCategory;
	JButton dodaj;
	
	public AddCompetitorWindow() {
	      MenuBarForWindow window = new MenuBarForWindow();
	      
	      //tworzenie i formatowanie etykiet oraz p�l tekstowych
	      lName = new JLabel("Imie: ");
	      lName.setBounds(20, 20, 100, 20);

	      lSurname = new JLabel("Nazwisko: ");
	      lSurname.setBounds(20, 70, 100, 30);

	      lAge = new JLabel("Wiek: ");
	      lAge.setBounds(20, 120, 100, 30);

	      lCategory = new JLabel("Kategoria: ");
	      lCategory.setBounds(20, 170, 100, 30);

	      tName = new JTextField();
	      tName.setBounds(150, 20, 300, 30);
	      //tName.setPreferredSize(new Dimension(100,30));

	      tSurname = new JTextField();
	      tSurname.setBounds(150, 70, 300, 30);
	      //tSurname.setPreferredSize(new Dimension(100,30));

	      tAge = new JTextField();
	      tAge.setBounds(150, 120, 300, 30);
	      //tAge.setPreferredSize(new Dimension(100,30));

	      tCategory = new JTextField();
	      tCategory.setBounds(150, 170, 300, 30);
	      //tCategory.setPreferredSize(new Dimension(100,30));

	      dodaj = new JButton("Dodaj");
	      dodaj.setBounds(150, 220, 100, 30);

	      //dodawanie do okna etykiet i p�l tekstowych
	      window.add(lName);
	      window.add(tName);
	      window.add(lSurname);
	      window.add(tSurname);
	      window.add(lAge);
	      window.add(tAge);
	      window.add(lCategory);
	      window.add(tCategory);
	      window.add(dodaj);

	      //zapisuje do zmiennych pobrany z p�l tekstowych tekst

	      dodaj.addActionListener(new ActionListener() {
	          public void actionPerformed(ActionEvent e) {
	        	  Database db = new Database();
	        	  Competitor c = new Competitor(null, tName.getText(), tSurname.getText(), Integer.parseInt(tAge.getText()), Integer.parseInt(tCategory.getText()), false);
	        	  db.insertOrUpdateCompetitor(c, 2);
	        	  db.close();
	          }
	      });
	      window.setVisible(true);
	  }
}
