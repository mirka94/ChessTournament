package tools;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import res.Strings;

public  class MyPlainDocument extends PlainDocument {
	private static final long serialVersionUID = -489930074061735703L;

	@Override
  	public void insertString(int offs, String str, AttributeSet a)
  			throws BadLocationException {
  		if(str.length()>50-offs) str = str.substring(0, 50-offs);
  		str = str.replaceAll(Strings.forbiddenCharsRegExp, "");
  		super.insertString(offs, str, a);
  	}
}