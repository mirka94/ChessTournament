package window;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;

import tools.Tools;

public class MainWindow extends JFrame {
	
	public MainWindow() {
		
		setMinimumSize(new Dimension(700, 500));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		Tools.aboutMenu(menuBar, MainWindow.this);
		
		try {
			InputStream imgIS = getClass().getResourceAsStream("/szachy.png");
			JLabel picLabel = new JLabel(new ImageIcon(ImageIO.read(imgIS)));
			add(picLabel, BorderLayout.CENTER);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
