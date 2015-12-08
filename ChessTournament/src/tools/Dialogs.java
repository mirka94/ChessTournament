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
	
	public static void autorzy() {
		JOptionPane.showMessageDialog(
			null,
			"Autorzy: \n"+
			"Piotr Jabłoński\n"+
			"Mirosława Pelc\n"+
			"Mariusz Lorek",
			"Autorzy", JOptionPane.UNDEFINED_CONDITION);
	}
	
	public static void opis() {
		JOptionPane.showMessageDialog(
			null,
			"Program powstał w ramach zaliczenia Zespołowych Przedsięwzięć Inżynierskich"+
			"na Państwowej Wyższej Szkole Zawodowej w Nowym Sączu.\nProwadzący przedmiot: "+
			"dr Antoni Ligęza\nProgram obsługuje turniej szachowy odbywający się podczas "+
			"Małopolskiej Nocy Naukowców.",
			"Opis", JOptionPane.UNDEFINED_CONDITION);
	}
	
}
