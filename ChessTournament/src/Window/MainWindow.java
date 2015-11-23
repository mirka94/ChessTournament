package Window;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.Dimension;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class MainWindow extends JFrame {
	
	public MainWindow() {
		
		setMinimumSize(new Dimension(700, 500));
		setMaximumSize(new Dimension(700, 500));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnTurniej = new JMenu("Turniej");
		menuBar.add(mnTurniej);
		
		JMenuItem dodajTurniej = new JMenuItem("Dodaj turniej");
		dodajTurniej.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getContentPane().removeAll();
				add(new AddTPanel(MainWindow.this));
				pack();
			}
		});
		
		mnTurniej.add(dodajTurniej);
		
		JMenuItem wybierzTurniej = new JMenuItem("Wybierz turniej");
		mnTurniej.add(wybierzTurniej);
		
		wybierzTurniej.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getContentPane().removeAll();
				add(new ShowTPanel(MainWindow.this));
				pack();
			}
		});
		
		JMenu mnOProgramie = new JMenu("O programie");
		menuBar.add(mnOProgramie);
		
		JMenuItem mntmPomoc = new JMenuItem("Pomoc");
		mntmPomoc.setAlignmentY(Component.TOP_ALIGNMENT);
		mnOProgramie.add(mntmPomoc);
		
		JMenuItem mntmAutorzy = new JMenuItem("Autorzy");
		mnOProgramie.add(mntmAutorzy);
		
		JMenuItem mntmOpis = new JMenuItem("Opis");
		mnOProgramie.add(mntmOpis);
		
		BufferedImage myPicture = null;
		try {
			myPicture = ImageIO.read(new File("szachy.png"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		JLabel picLabel = new JLabel(new ImageIcon(myPicture));
		add(picLabel);
		
	}
}
