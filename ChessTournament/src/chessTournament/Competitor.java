//przechowuje dane użytkowników

package chessTournament;

public class Competitor {

    private String name;
    private String surname;
    private int age;
    private int chessCategory;
    private Integer id;
    private boolean isDisqualified;

    public Competitor(Integer id, String name, String surname, int age, int chessCategory, boolean isDisqualified) {
    	this.id 			= id;
        this.name 			= name;
        this.surname 		= surname;
        this.age 			= age;
        this.chessCategory 	= chessCategory;
        this.isDisqualified = isDisqualified;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws ValidatorException {
    	if(name.length()<3) throw new ValidatorException("Imię za krótkie");
    	if(name.length()>50) throw new ValidatorException("Imię za długie");
    	if(!name.matches("[a-zA-ZżółćęśąźńŻÓŁĆĘŚĄŹŃ\\- ]+")) throw new ValidatorException("Imię zawiera niedozwolone znaki");
        this.name = name;
    }

    public void setAge(int age) throws ValidatorException {
    	if(age<0) throw new ValidatorException("Wiek nie może być ujemny");
        this.age = age;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) throws ValidatorException  {
    	if(name.length()<2) throw new ValidatorException("Nazwisko za krótkie");
    	if(name.length()>50) throw new ValidatorException("Nazwisko za długie");
    	if(!name.matches("[a-zA-ZżółćęśąźńŻÓŁĆĘŚĄŹŃ\\- ]+")) throw new ValidatorException("Nazwisko zawiera niedozwolone znaki");
        this.surname = surname;
    }

    public int getAge() {
        return age;
    }

    public int getChessCategory() {
        return chessCategory;
    }

    public void setChessCategory(int chessCategory) throws ValidatorException  {
    	// TODO throw ValidatorException
        this.chessCategory = chessCategory;
    }
    
    public boolean getIsDisqualified() {
        return this.isDisqualified;
    }
    
    public void setIsDisqualified(boolean isDisqualified) {
        this.isDisqualified = isDisqualified;
    }
}
