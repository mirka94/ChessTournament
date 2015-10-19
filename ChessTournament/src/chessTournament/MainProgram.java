package chessTournament;

//klasa odpowiada za uruchomienie programu

//import layout.Window;

import data.CompetitorModel;
import layout.Window;
//import xml.CompetitorReadFile;
//import xml.CompetitorToFile;

public class MainProgram {

    Window window = new Window();

    public static void main (String[]args){

        MainProgram main = new MainProgram();

        main.window.windowMain();

//        CompetitorModel cm = new CompetitorModel();


        /*cm.setName("Mirkaa");
        cm.setSurname("Pelc");
        cm.setAge(21);
        cm.setChessCategory(1);
        cm.setId(1);

        String path="C:/Users/Mrrusia/Desktop/program/ddd.xml";

        new CompetitorToFile().toXML(cm,path);

        CompetitorModel c2;

        c2 = new CompetitorReadFile().competitor(path);

        System.out.println(c2.getId());
        System.out.println(c2.getAge());
        System.out.println(c2.getName());
        System.out.println(c2.getSurname());
        System.out.println(c2.getChessCategory());
        */

    }

}
