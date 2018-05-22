package scheduling;

import java.util.Comparator;

public class TeamComaparatorForUmpiring implements Comparator<Team>
{
    @Override
    public int compare(Team team1, Team team2) {

        return team1.getNumberOfUmpiringAssignments() - team2.getNumberOfUmpiringAssignments();

    }
}