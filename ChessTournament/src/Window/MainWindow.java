package window;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;

import tools.Tools;

@SuppressWarnings("serial")
public class MainWindow extends JFrame {
	
	public MainWindow() {
		
		setMinimumSize(new Dimension(700, 500));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		Tools.aboutMenu(menuBar, MainWindow.this);
		
		BufferedImage myPicture = null;
		try {
			InputStream imgIS = getClass().getResourceAsStream("/szachy.png");
			myPicture = ImageIO.read(imgIS);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		JLabel picLabel = new JLabel(new ImageIcon(myPicture));
		add(picLabel, BorderLayout.CENTER);
		
	}
}
