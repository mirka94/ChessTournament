package chessTournament;

//klasa odpowiada za uruchomienie programu
public class MainProgram {
  Window window = new Window();
  Database DB;

  public static void main (String[]args){
      MainProgram main = new MainProgram();
      main.window.windowMain();
      
      /*
      Database DB = new Database();
      
      Competitor cm = new Competitor(null, "Mirkaa", "Pelc", 21, 1, false);
      Competitor cm2 = new Competitor(null, "Pietrek", "Jab�o�ski", 20, 1, false);
      Competitor cm3 = new Competitor(null, "Maryjusz", "Lorek", 99, 0, false);
      DB.insertOrUpdateCompetitor(cm, 0);
      DB.insertOrUpdateCompetitor(cm2, 0);
      DB.insertOrUpdateCompetitor(cm3, 1);
      */
      /*for(Competitor c2 : DB.getCompetitors(1)) {
      	c2.setAge(c2.getAge()-1);
      	DB.insertOrUpdateCompetitor(c2, 1);
      	System.out.println(c2.getId());
          System.out.println(c2.getAge());
          System.out.println(c2.getName());
          System.out.println(c2.getSurname());
          System.out.println(c2.getChessCategory());
      }        
      DB.close();*/
  }
}