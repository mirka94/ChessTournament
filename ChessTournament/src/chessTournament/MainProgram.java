package chessTournament;


//klasa odpowiada za uruchomienie programu
public class MainProgram {  

  public static void main (String[]args){
	  new CompetitorTabbedPane();
      System.out.print(Simulator.Simulate(13, 5));
  }
}