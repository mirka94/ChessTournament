package szachy;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class Window extends JFrame{
	
	JLabel lName, lSurname, lAge, lCategory;
	JTextField tName, tSurname, tAge, tCategory;
	
	public Window(){
		setSize(500,500);
		setLayout(null);
	}
	
	public void windowMain(){
		
		Window window = new Window();
		
		window.setDefaultCloseOperation(EXIT_ON_CLOSE);
		addCompetitor();
		
	}
	
	public void addCompetitor(){
		
		Window window = new Window();
		
		lName = new JLabel("Imie: ");
		lName.setBounds(20, 20, 100, 30);
		
		lSurname = new JLabel("Nazwisko: ");
		lSurname.setBounds(20, 50, 100, 30);
		
		lAge = new JLabel("Wiek: ");
		lAge.setBounds(20, 80, 100, 30);
		
		lCategory = new JLabel("Kategoria: ");
		lCategory.setBounds(20, 110, 100, 30);
		
		window.add(lName);
		window.add(lSurname);
		window.add(lAge);
		window.add(lCategory);
		
		window.setVisible(true);
		
	}
	
}
