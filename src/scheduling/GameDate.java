package scheduling;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: mjain
 * Date: 1/28/15
 * Time: 11:09 AM
 * To change this template use File | Settings | File Templates.
 */
public class GameDate
{
    private Date gameDate;
    private int numberOfMatchesScheduled;
    private ArrayList<Ground> occupiedGrounds = new ArrayList<Ground>();
    private ArrayList<Team> teams = new ArrayList<Team>();

    public GameDate(Date date) {
        gameDate = date;
    }

    public Date getGameDate() {
        return gameDate;
    }

    public int getNumberOfMatchesScheduled() {
        return numberOfMatchesScheduled;
    }

    public void incrementNumberOfMatchesScheduled()
    {
        this.numberOfMatchesScheduled++;
    }

    public ArrayList<Ground> getOccupiedGrounds() {
        return occupiedGrounds;
    }

    public ArrayList<Team> getTeams() {
        return teams;
    }

    public boolean suits(int maxNumberOfMatchesPerDay, Ground ground)
    {
        if(this.getNumberOfMatchesScheduled() < maxNumberOfMatchesPerDay && !occupiedGrounds.contains(ground)) return true;

        return false;
    }
}
