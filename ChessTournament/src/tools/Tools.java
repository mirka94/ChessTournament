package tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import model.Competitor;
import model.SingleGame;

public class Tools {
	public static void checkGroups(int groups, List<Competitor> competitors) {
		competitors.forEach(c -> { 
			if(c.getGroup()!=null && c.getGroup()+1>groups) c.setGroup(null); 
		});
	}
	
	
	/**
	 * @param competitors lista uczestników
	 * @return lista grup, każda zawierająca listę uczestników
	 */
	public static TreeMap<Integer, List<Competitor>> groupsList(List<Competitor> competitors) {
		TreeMap<Integer, List<Competitor>> groupsList = new TreeMap<>();
		for(Competitor c : competitors) {
			int g = c.getGroup();
			if(!groupsList.containsKey(g)) 
				groupsList.put(g, new ArrayList<>());
			groupsList.get(g).add(c);
		}
		return groupsList;
	}
	
	public static List<SingleGame> generateSingleGames(TreeMap<Integer, List<Competitor>> groupsList) {
		List<SingleGame> result = new ArrayList<>();
		for(Integer i : groupsList.keySet()) {
			List<Competitor> l = groupsList.get(i);
			Collections.shuffle(l);
			for(SGInfo sgi : roundRobinGamesInfo(l.size())) {
				result.add(
					new SingleGame(l.get(sgi.c1-1), l.get(sgi.c2-1), sgi.r-1)
				);
			}
		}
		
		result.sort((c1,c2) -> {
			return c1.getRound()-c2.getRound();
		});
		
		return result;
	}
	
	public static List<SGInfo> roundRobinGamesInfo(int g) {
		LinkedList<SGInfo> result = new LinkedList<>();
		int gp = (g%2==1) ? g+1 : g;
		for(int i=1; i<gp; ++i) {
			if(i%2==1) {
				if(g==gp) result.add(new SGInfo(1+i/2,gp,i));
				for(int j=2; j<=gp/2; ++j)
					result.add(new SGInfo((j+i/2),(gp-j+i/2)%(gp-1)+1,i));
			}
			else {
				if(g==gp) result.add(new SGInfo(gp,(gp/2+i/2),i));
				for(int j=2; j<=gp/2; ++j)
					result.add(new SGInfo((gp/2+j+i/2-2)%(gp-1)+1,(gp/2-j+i/2)%(gp-1)+1,i));
			}
		}
		return result;
	}
	
	static class SGInfo {
		int c1, c2, r;
		public SGInfo(int c1, int c2, int r) {
			this.c1 = c1;
			this.c2 = c2;
			this.r = r;
		}
	}
}