package scheduling;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: mjain
 * Date: 1/28/15
 * Time: 11:12 AM
 * To change this template use File | Settings | File Templates.
 */
public class Weekend
{
    private GameDate saturday;
    private GameDate sunday;
    private ArrayList<Team> teams = new ArrayList<Team>();

    public Weekend(GameDate saturday, GameDate sunday) {
        this.saturday = saturday;
        this.sunday = sunday;
    }

    public GameDate getSaturday() {
        return saturday;
    }

    public GameDate getSunday() {
        return sunday;
    }

    public ArrayList<Team> getTeams() {
        return teams;
    }

    public boolean isSuitableForBothTeams(Match match)
    {
        if(teams.contains(match.getHostTeam()) || teams.contains(match.getGuestTeam())) return false;

        return true;  //To change body of created methods use File | Settings | File Templates.
    }
}
