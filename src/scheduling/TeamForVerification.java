package scheduling;

public class TeamForVerification
{
    String teamName;
    int weekendNumberOfGames;
    int weekendNumberOfUmpiringAssignments;
    String matchDate;
    String umpringDate;

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public int getWeekendNumberOfGames() {
        return weekendNumberOfGames;
    }

    public void setWeekendNumberOfGames(int weekendNumberOfGames) {
        this.weekendNumberOfGames = weekendNumberOfGames;
    }

    public int getWeekendNumberOfUmpiringAssignments() {
        return weekendNumberOfUmpiringAssignments;
    }

    public void setWeekendNumberOfUmpiringAssignments(int weekendNumberOfUmpiringAssignments) {
        this.weekendNumberOfUmpiringAssignments = weekendNumberOfUmpiringAssignments;
    }

    public String getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(String matchDate) {
        this.matchDate = matchDate;
    }

    public String getUmpringDate() {
        return umpringDate;
    }

    public void setUmpringDate(String umpringDate) {
        this.umpringDate = umpringDate;
    }

    public void incrementNumberOfGames()
    {
        weekendNumberOfGames++;
    }

    public void incrementNumberOfUmpiringAssignments()
    {
        weekendNumberOfUmpiringAssignments++;
    }
}
