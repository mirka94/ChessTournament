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
	private static final long serialVersionUID = -4321522332774571523L;

	public static void main (String[]args){
		new MainWindow().setVisible(true);
	}
	
	public MainWindow() {
		setMinimumSize(new Dimension(700, 500));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		
		setJMenuBar(Tools.aboutMenu(new JMenuBar(), MainWindow.this));
		
		try {
			InputStream imgIS = getClass().getResourceAsStream("/szachy.png");
			add(new JLabel(new ImageIcon(ImageIO.read(imgIS))), BorderLayout.CENTER);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
