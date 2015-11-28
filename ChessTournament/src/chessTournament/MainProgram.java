package chessTournament;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import Window.MainWindow;


//klasa odpowiada za uruchomienie programu
public class MainProgram {  

  public static void main (String[]args){
	  new MainWindow().setVisible(true);
  }
  
  public static class MyPlainDocument extends PlainDocument {
	private static final long serialVersionUID = 6266677720804666216L;
	@Override
  	public void insertString(int offs, String str, AttributeSet a)
  			throws BadLocationException {
  		if(str.length()>50-offs) str = str.substring(0, 50-offs);
  		str = str.replaceAll("[^a-zA-Z0-9żółćęśąźńŻÓŁĆĘŚĄŹŃ\\- ]+", "");
  		super.insertString(offs, str, a);
  	}
  }
}