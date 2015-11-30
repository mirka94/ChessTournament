package tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.Competitor;

public class Simulator {
	private static BufferedReader imieReader;
	private static BufferedReader nazwiskoReader;
	static void rozgrywek_finaly(int zawodnikow, int izg) {
		System.out.print("Finały: zawodnikow - "+zawodnikow+", "
				+izg*(izg-1)+" rozgrywek odbylo sie juz w fazie eliminacji, pozostalo rozgrywek "+
				(zawodnikow*(zawodnikow-1)-(zawodnikow/izg)*izg*(izg-1)));
	}
	public static int rozgrywek_eliminacje(int zawodnikow, int grup) {
		List<Integer> grupy = new ArrayList<Integer>();
		while(zawodnikow>0) {
			int t = zawodnikow/grup;
			grupy.add(t);
			zawodnikow-=t;
			grup--;
		}
		int rozgrywek = 0;
		for(Integer i : grupy) {
			//System.out.print("Grupa: zawodnikow - "+i+", rozgrywek - "+(i*(i-1)/2)+"\n");
			rozgrywek+=i*(i-1)/2;
		}
		//System.out.print("Łącznie rozgrywek: "+rozgrywek+"\n");
		return rozgrywek;
	}
	
	public static void roundRobinTable(int g) {
		int gp = (g%2==1) ? g+1 : g;
		for(int i=1; i<gp; ++i) {
			if(i%2==1) {
				System.out.print((1+i/2)+"-"+gp);
				for(int j=2; j<=gp/2; ++j) {
					System.out.print("\t"+(j+i/2)+"-"+((gp-j+i/2)%(gp-1)+1));
				}
			}
			else {
				System.out.print(gp+"-"+(gp/2+i/2));
				for(int j=2; j<=gp/2; ++j) {
					System.out.print("\t"+((gp/2+j+i/2-2)%(gp-1)+1)+"-"+((gp/2-j+i/2)%(gp-1)+1));
				}
			}
			System.out.print("\n");
		}
	}
	
	static String pad(String toPad) {
		return (toPad+"                    ").substring(0, 20);
	}
	public static Competitor RandomPlayer() throws IOException {
		Random rn = new Random();
		int a = 10+rn.nextInt(10)+rn.nextInt(10);
		int c = rn.nextInt(6)+1;
		
		int randomInt = rn.nextInt(300);
		String imie = null, nazwisko = null;
		
        imieReader = new BufferedReader(new FileReader("imiona.txt"));
        nazwiskoReader = new BufferedReader(new FileReader("nazwiska.txt"));
        
        do {
        	for (int i = 0; i < randomInt + 1; i++) {
        			imie = imieReader.readLine();
        			nazwisko = nazwiskoReader.readLine();
        	}
		} while(imie.endsWith("a") && nazwisko.endsWith("ki"));
		return new Competitor(null, imie, nazwisko, a, c, false, null); //dodać 0
	}
}