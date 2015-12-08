package tools;

import javax.swing.JOptionPane;

public class Dialogs {
	public static void doZrobienia() {
		JOptionPane.showMessageDialog(
			null, 
			"Ta opcja jeszcze nie działa", 
			"Nie za duże chęci?", 
			JOptionPane.ERROR_MESSAGE);
	}
	
	public static void graczBezGrupy() {
		JOptionPane.showMessageDialog(
			null, 
			"Aby móc rozpocząć turniej, każdy gracz musi być przydzielony do grupy", 
			"Gracz bez grupy", 
			JOptionPane.ERROR_MESSAGE);
	}

	public static void nierownomiernyPodzial(int min, int max) {
		JOptionPane.showMessageDialog(
			null, 
			"Największa grupa: "+max+" uczestników, \n"+
			"Najmniejsza grupa: "+min+" uczestników \n"+
			"Różnica pomiędzy tymi wartościami nie może być większa od 1", 
			"Nierównomierny podział", 
			JOptionPane.ERROR_MESSAGE);
	}
	
	public static void gryBezWyniku() {
		JOptionPane.showMessageDialog(
			null, 
			"Aby zakończyć eliminacje, wszystkie gry tej fazy muszą być ukończone", 
			"Nieukończone rozgrywki", 
			JOptionPane.ERROR_MESSAGE);
	}
}
