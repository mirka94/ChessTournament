//przechowuje dane użytkowników

package model;

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

    public Tournament(Integer id, String name, String year, int boards, int rounds, int roundsCompleted) {
    	this.id 			= id;
        this.name 			= name;
        this.year 			= year;
        this.boards 		= boards;
        this.rounds 		= rounds;
        this.roundsCompleted = roundsCompleted;
    }

    public Integer getId() {
        return id;
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
}
