package window;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import tools.Dialogs;

@SuppressWarnings("serial")
public class MainWindow extends JFrame {
	
	@SuppressWarnings("static-access")
	public MainWindow() {
		
		setMinimumSize(new Dimension(700, 500));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnTurniej = new JMenu("Turniej");
		menuBar.add(mnTurniej);
		
		JMenuItem dodajTurniej = new JMenuItem("Dodaj turniej");
		dodajTurniej.addActionListener(e -> {
				getContentPane().removeAll();
				add(new AddTPanel(MainWindow.this), BorderLayout.CENTER);
				pack();
		});
		dodajTurniej.setAccelerator(KeyStroke.getKeyStroke(
		        java.awt.event.KeyEvent.VK_F2, 0));
		mnTurniej.add(dodajTurniej);
		
		JMenuItem wybierzTurniej = new JMenuItem("Wybierz turniej");
		mnTurniej.add(wybierzTurniej);
		wybierzTurniej.setAccelerator(KeyStroke.getKeyStroke(
		        java.awt.event.KeyEvent.VK_F3, 0));
		wybierzTurniej.addActionListener(e -> {
				getContentPane().removeAll();
				add(new ShowTPanel(MainWindow.this), BorderLayout.CENTER);
				pack();
		});
		
		JMenu mnOProgramie = new JMenu("O programie");
		menuBar.add(mnOProgramie);
		
		JMenuItem mntmPomoc = new JMenuItem("Pomoc");
		mntmPomoc.setAlignmentY(Component.TOP_ALIGNMENT);
		mnOProgramie.add(mntmPomoc);
		
		// otwieranie pdf z instrukcją po wybraniu pomocy
		mntmPomoc.addActionListener(e->{
			if (Desktop.isDesktopSupported()) {
			    try {
			        File myFile = new File("turniej.pdf");
			        Desktop.getDesktop().open(myFile);
			    } catch (IOException ex) {
			        System.out.println(e);
			    }
			}
		});
		
		JMenuItem mntmAutorzy = new JMenuItem("Autorzy");
		mnOProgramie.add(mntmAutorzy);
		
		mntmAutorzy.addActionListener(e->Dialogs.autorzy());
		
		JMenuItem mntmOpis = new JMenuItem("Opis");
		mnOProgramie.add(mntmOpis);
		
		mntmOpis.addActionListener(e->Dialogs.opis());
		
		BufferedImage myPicture = null;
		try {
			myPicture = ImageIO.read(new File("szachy.png"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		JLabel picLabel = new JLabel(new ImageIcon(myPicture));
		add(picLabel, BorderLayout.CENTER);
		
	}
}
