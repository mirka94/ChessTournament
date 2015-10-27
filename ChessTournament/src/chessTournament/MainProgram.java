package chessTournament;


//klasa odpowiada za uruchomienie programu
public class MainProgram {
  MenuBarForWindow window = new MenuBarForWindow();
  Database DB = new Database();

  public static void main (String[]args){
      
	 // new NaRazieNiepotrzebne();
	  
	  MainProgram main = new MainProgram();
      main.window.windowMain();
      
  }
}