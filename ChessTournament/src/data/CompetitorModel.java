//przechowuje dane użytkowników

package data;

public class CompetitorModel {

    private String name;
    private String surname;
    private int age;
    private int chessCategory;
    private int id;

    public CompetitorModel(){}

    public CompetitorModel(int id, String name, String surname, int age, int chessCategory) {
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.chessCategory = chessCategory;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getAge() {
        return age;
    }

    public int getChessCategory() {
        return chessCategory;
    }

    public void setChessCategory(int chessCategory) {
        this.chessCategory = chessCategory;
    }
}
