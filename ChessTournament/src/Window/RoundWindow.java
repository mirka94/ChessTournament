package window;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import panel.RoundPanel;

@SuppressWarnings("serial")
public class RoundWindow extends JFrame{

	private RoundPanel roundPanel;
	
	public RoundWindow(){
		
		setVisible(true);
		setSize(700,500);
		setLayout(new BorderLayout());
		
		roundPanel = new RoundPanel();
		add(roundPanel, BorderLayout.CENTER);
		
	}
	
}
