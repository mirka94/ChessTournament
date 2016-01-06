package chessTournament;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import res.Strings;
import window.MainWindow;


//klasa odpowiada za uruchomienie programu
public class MainProgram {  

  public static void main (String[]args){
	  new MainWindow().setVisible(true);
  }
  
  public static class MyPlainDocument extends PlainDocument {
	@Override
  	public void insertString(int offs, String str, AttributeSet a)
  			throws BadLocationException {
  		if(str.length()>50-offs) str = str.substring(0, 50-offs);
  		str = str.replaceAll(Strings.forbiddenCharsRegExp, "");
  		super.insertString(offs, str, a);
  	}
  }
}