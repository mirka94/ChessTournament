package tools;

import javax.swing.JOptionPane;

/**
 * Definiuje okna błędów, ostrzeżeń oraz informacji
 */
public class Dialogs {
	public static void bladBazy() {
		JOptionPane.showMessageDialog(
			null, 
			"Błąd odczytu / zapisu", 
			"Błąd bazy", 
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
	
	/**
	 * @return Czy kontynuować mimo ostrzeżenia
	 */
	public static boolean niktZGrupyDoFinalow() {
		int r = JOptionPane.showConfirmDialog(
			null, 
			"Istnieje grupa, w której nie wybrano graczy przechodzących do finału. Kontynuować?",
			"Uwaga!",
			JOptionPane.OK_CANCEL_OPTION);
		return r==JOptionPane.OK_OPTION;
	}
	
}
