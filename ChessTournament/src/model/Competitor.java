//przechowuje dane użytkowników

package model;

import java.io.Serializable;
import java.text.Collator;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Locale;

import chessTournament.ValidatorException;
import res.Strings;

/**
 * Przechowuje dane o uczestniku
 */
public class Competitor implements Serializable, Comparable {
	private static final long serialVersionUID = -8356380635352407432L;
	private String name;
    private String surname;
    private int age;
    private int chessCategory;
    private Integer id;
    private boolean isDisqualified;
    private Integer group;
    public static EnumMap<SortOption, Comparator<Competitor>> comparators;

    static {
    	comparators = new EnumMap<SortOption, Comparator<Competitor>>(SortOption.class);
    	Collator collator = Collator.getInstance(new Locale(Strings.locale));
	    comparators.put(SortOption.AGE_ASC, 
	    		(c1, c2) -> c1.getAge().compareTo(c2.getAge()));
		comparators.put(SortOption.AGE_DESC, 
				(c2, c1) -> c1.getAge().compareTo(c2.getAge()));
		comparators.put(SortOption.CHESSCATEGORY_ASC, 
				(c1, c2) -> c1.getChessCategory().compareTo(c2.getChessCategory()));
		comparators.put(SortOption.CHESSCATEGORY_DESC, 
				(c2, c1) -> c1.getChessCategory().compareTo(c2.getChessCategory()));
		comparators.put(SortOption.NAME_ASC, 
				(c1, c2) -> collator.compare(c1.getName(), c2.getName()));
		comparators.put(SortOption.NAME_DESC, 
				(c2, c1) -> collator.compare(c1.getName(), c2.getName()));
		comparators.put(SortOption.SURNAME_ASC, 
				(c1, c2) -> c1.getSurname().compareTo(c2.getSurname()));
		comparators.put(SortOption.SURNAME_DESC, 
				(c2, c1) -> c1.getSurname().compareTo(c2.getSurname()));
    }
    
    public Competitor(Integer id, String name, String surname, int age, int chessCategory, boolean isDisqualified, Integer group) {
    	this.id 			= id;
        this.name 			= name;
        this.surname 		= surname;
        this.age 			= age;
        this.chessCategory 	= chessCategory;
        this.isDisqualified = isDisqualified;
        this.group			= group;
    }
    
    public void setId(Integer id) {
    	this.id = id;
    }
    
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws ValidatorException {
    	if(name.length()>50) throw new ValidatorException(Strings.nameTooLong);
    	if(!name.matches(Strings.allowedCharsRegExp)) throw new ValidatorException(Strings.forbiddenCharsName);
        this.name = name;
    }

    public void setAge(int age) throws ValidatorException {
    	if(age<0) throw new ValidatorException(Strings.negativeAge);
        this.age = age;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) throws ValidatorException  {
    	if(name.length()>50) throw new ValidatorException(Strings.surnameTooLong);
    	if(!name.matches(Strings.allowedCharsRegExp)) throw new ValidatorException(Strings.forbiddenCharsSurName);
        this.surname = surname;
    }

    public Integer getAge() {
        return age;
    }

    public Integer getChessCategory() {
        return chessCategory;
    }

    public void setChessCategory(int chessCategory) {
        this.chessCategory = chessCategory;
    }
    
    public Boolean getIsDisqualified() {
        return this.isDisqualified;
    }
    
    public void setIsDisqualified(boolean isDisqualified) {
        this.isDisqualified = isDisqualified;
    }
    
    public Integer getRawGroup() {
        return group;
    }
    
    public Integer getGroup() {
    	if(group==null) return null;
        return group%100;
    }
    
    public void setGroup(Integer group) {
    	this.group = group;
    }
    
    public void setGoesFinal(boolean goes) {
    	group%=100;
    	if(goes) group+=100;
    }
    
    public boolean getGoesFinal() {
    	return group>=100;
    }
    
    public enum SortOption {
		NAME_ASC, NAME_DESC, 
		SURNAME_ASC, SURNAME_DESC, 
		AGE_ASC, AGE_DESC, 
		CHESSCATEGORY_ASC, CHESSCATEGORY_DESC
	}
    
    @Override
    public String toString() {
    	return surname+" "+name;
    }
    
    @Override
    public boolean equals(Object obj) {
    	if(obj instanceof Competitor) return this.getId() == ((Competitor) obj).getId();
    	return false;
    }

	@Override
	public int compareTo(Object obj) {
		if(!(obj instanceof Competitor)) return -1;
		Competitor c = (Competitor) obj;
		if(c.getId()-getId()!=0) return c.getId()-getId();
		return toString().compareTo(c.toString());
	}
}
