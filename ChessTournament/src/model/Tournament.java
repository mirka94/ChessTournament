//przechowuje dane użytkowników

package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Przechowuje dane o turnieju
 */
public class Tournament implements Serializable {
	private static final long serialVersionUID = 7913534186353148249L;
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
    
    public Tournament copy() {
    	return new Tournament(id, name, year, boards, rounds, roundsCompleted, type);
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

    public void setId(Integer id) {
    	this.id = id;
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

	public void setRoundsCompleted(int roundsCompleted, boolean overRide) {
		if(roundsCompleted > this.roundsCompleted || overRide)  
			this.roundsCompleted = roundsCompleted;
	}
	
	public void setRoundsCompleted(int roundsCompleted) {
		setRoundsCompleted(roundsCompleted, false);
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
	
	public boolean isPlayersEditAllowed() {
		return roundsCompleted<0;
	}
	public boolean isDisqualificationAllowed() {
		return roundsCompleted==0 || roundsCompleted==2;
	}
}
