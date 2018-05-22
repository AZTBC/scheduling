package scheduling;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CartesianProduct
{
    private static List<String> matches = new ArrayList<String>();

    public static void main(String[] args)
    {
        String teams = "Boosters 11,Panthers,Raging Bulls,Raiders,The SuperNovas,Warriors,Arizona Royals,AZ Kings,Chargers,Clairvoyant Batters,Desert Jumbos,Desert Storm,Desi Devils,Dropouts,Gladiators,Indus,Rodeos,Super Kings,Tempest,Terminators";

        String [] teamsArray = teams.split(",");

        generateCartesianProduct(teamsArray);

        for(String match : matches) System.out.println(match);
    }

    private static void generateCartesianProduct(String[] teamsArray)
    {
        for(String homeTeam : teamsArray)
        {
            for(String awayTeam : teamsArray)
            {
                if(homeTeam.equals(awayTeam)) continue;

                String match1 = homeTeam + "," + awayTeam;
                String match2 = awayTeam + "," + homeTeam;

                if(!matches.contains(match1) && !matches.contains(match2)) matches.add(match1);
            }
        }
    }
}
