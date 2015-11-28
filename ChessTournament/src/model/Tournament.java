//przechowuje dane użytkowników

package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Przechowuje dane o turnieju
 */
public class Tournament {
	private Integer id;
    private String name;
    private String year;
	private int boards;
    private int rounds;
    private int roundsCompleted;
    private Type type;
    private List<TypeChangeListener> TClisteners = new ArrayList<TypeChangeListener>();
    
    public enum Type {
    	SWISS,
    	GROUP_ELIMINATIONS
    }

    public Tournament(Integer id, String name, String year, int boards, int rounds, int roundsCompleted, Type type) {
    	this.id 			= id;
        this.name 			= name;
        this.year 			= year;
        this.boards 		= boards;
        this.rounds 		= rounds;
        this.roundsCompleted = roundsCompleted;
        this.type			= type;
    }
    
    @FunctionalInterface
    public interface TypeChangeListener {
    	public void onTypeChanged(Type type);
    }
   
    public void addTypeChangeListener(TypeChangeListener listener) {
    	TClisteners.add(listener);
    }
    
    public void removeTypeChangeListener(TypeChangeListener listener) {
    	TClisteners.remove(listener);
    }

    public Integer getId() {
        return id;
    }
    
    public void setId(int newId) {
        id = newId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public int getBoards() {
		return boards;
	}

	public void setBoards(int boards) {
		this.boards = boards;
	}

	public int getRounds() {
		return rounds;
	}

	public void setRounds(int rounds) {
		this.rounds = rounds;
	}

	public int getRoundsCompleted() {
		return roundsCompleted;
	}

	public void setRoundsCompleted(int roundsCompleted) {
		this.roundsCompleted = roundsCompleted;
	}
	
	public Type getType() {
		return type;
	}
	
	public boolean isSwiss() {
		return type==Type.SWISS;
	}
	
	public void setType(Type type) {
		this.type = type;
		for (TypeChangeListener listener : TClisteners) {
		    listener.onTypeChanged(type);
		}
	}	
}
