package scheduling;

/**
 * Created with IntelliJ IDEA.
 * User: mjain
 * Date: 1/28/15
 * Time: 10:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class Ground
{
    private String name;
    private String region;
    private int numberOfTeams = 0; //number of teams using this ground as their home ground
    private int totalNumberOfGames = 0;
    private int numberOfUnscheduledGames = 0;

    public Ground(String name, String region) {
        this.name = name;
        this.region = region;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegion() {
        return region;
    }

    public void incrementNumberOfTeams()
    {
        this.numberOfTeams++;
    }

    public int getNumberOfUnscheduledGames() {
        return numberOfUnscheduledGames;
    }

    public void incrementTotalNumberOfGames()
    {
        this.totalNumberOfGames++;
    }

    public void incrementNumberOfUnscheduledGames()
    {
        this.numberOfUnscheduledGames++;
    }

    public void decreaseNumberOfUnscheduledGames()
    {
        this.numberOfUnscheduledGames--;
    }
}
