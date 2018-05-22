package scheduling;

/**
 * Created with IntelliJ IDEA.
 * User: mjain
 * Date: 1/28/15
 * Time: 10:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class Team
{
    private String name;
    private Ground homeGround;
    private int numberOfUmpiringAssignments = 0;

    public Team(String name, Ground homeGround) {
        this.name = name;
        this.homeGround = homeGround;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Ground getHomeGround() {
        return homeGround;
    }

    public int getNumberOfUmpiringAssignments() {
        return numberOfUmpiringAssignments;
    }

    public void incrementNumberOfUmpiringAssignments()
    {
        this.numberOfUmpiringAssignments++;
    }
}
