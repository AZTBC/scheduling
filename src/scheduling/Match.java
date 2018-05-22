package scheduling;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: mjain
 * Date: 1/28/15
 * Time: 10:46 AM
 * To change this template use File | Settings | File Templates.
 */
public class Match
{
    private Team guestTeam;
    private Team hostTeam;
    private Date matchDate;
    private Team umpiringTeam;


    public Match(Team guestTeam, Team hostTeam) {
        this.guestTeam = guestTeam;
        this.hostTeam = hostTeam;
    }

    public Team getGuestTeam() {
        return guestTeam;
    }

    public Team getHostTeam() {
        return hostTeam;
    }

    public Date getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(Date matchDate) {
        this.matchDate = matchDate;
    }

    public Team getUmpiringTeam() {
        return umpiringTeam;
    }

    public void setUmpiringTeam(Team umpiringTeam) {
        this.umpiringTeam = umpiringTeam;
    }
}

