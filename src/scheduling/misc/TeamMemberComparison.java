package scheduling.misc;

//Purpose: check and report on which member is part of more than one club


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TeamMemberComparison
{
    private static Map<String, String> teamNamesAndURLs = new HashMap<String, String>(); //teamName, url
    private static Map<String, ArrayList<String>> teamNamesAndPlayers = new HashMap<String, ArrayList<String>>();

    public static void main(String [] args) throws IOException, InterruptedException {

        findTeamNamesAndURLs();

        //findPlayersNamesForTeam("AZ Royals", "https://aztbc.com/team/81cbfb651ce960e5c50556db");

        findPlayersForAllTeams();

        findDuplicates();

        findPlayersWithFirstNamesOnly();
    }

    private static void findPlayersWithFirstNamesOnly()
    {
        StringBuilder sb=new StringBuilder("Players with only first names: \n");

        for(String teamName : teamNamesAndPlayers.keySet())
        {
            sb.append(teamName + ": \n");

            ArrayList<String> playerNames = teamNamesAndPlayers.get(teamName);

            for(String playerName : playerNames) if(!playerName.trim().contains(" ")) sb.append("    " + playerName + "\n");
        }

        System.out.println(sb);

    }

    private static void findTeamNamesAndURLs() throws InterruptedException {
        WebDriver driver = new ChromeDriver();

        driver.get("https://aztbc.com/teams");

        TimeUnit.SECONDS.sleep(7);

        WebElement table_element = driver.findElement(By.className("table-bordered"));

        List<WebElement> tr_collection=table_element.findElements(By.xpath("tbody/tr"));

        System.out.println("Number of Teams = "+tr_collection.size());

        for(WebElement rowElement : tr_collection)
        {
            List<WebElement> td_collection=rowElement.findElements(By.xpath("td"));

            String teamName = td_collection.get(0).getText();
            String url = td_collection.get(0).findElement(By.xpath("a")).getAttribute("href");

            System.out.println(teamName + "    " + url);

            teamNamesAndURLs.put(teamName.trim(), url.trim());
        }

        driver.close();
    }

    private static void findPlayersForAllTeams() throws InterruptedException
    {
        StringBuilder sb = new StringBuilder("Reading Players For Teams: ");

        for(String teamName : teamNamesAndURLs.keySet()) findPlayersNamesForTeam(teamName, teamNamesAndURLs.get(teamName), sb);

        System.out.println(sb);

        for(String teamName : teamNamesAndURLs.keySet()) System.out.println(teamName + ": " + teamNamesAndPlayers.get(teamName));
    }

    private static void findDuplicates()
    {
        for(String teamName : teamNamesAndPlayers.keySet())
        {
            System.out.println("Finding Duplicates for: " + teamName);

            ArrayList<String> playerNames = teamNamesAndPlayers.get(teamName);

            for(String playerName : playerNames)
            {
                for(String teamNameForComparison : teamNamesAndPlayers.keySet())
                {
                    if(!teamName.equalsIgnoreCase(teamNameForComparison))
                    {
                        ArrayList<String> playerNamesToCompare = teamNamesAndPlayers.get(teamNameForComparison);

                        for(String playerNameForComparison : playerNamesToCompare)
                        {
                            if(playerName.equalsIgnoreCase(playerNameForComparison)) System.out.println(playerName + "," + teamName + "," + teamNameForComparison);
                        }
                    }
                }
            }
        }

    }

    private static void findPlayersNamesForTeam(String teamName, String url, StringBuilder sb) throws InterruptedException {
        WebDriver driver = new ChromeDriver();

        driver.get(url);

        TimeUnit.SECONDS.sleep(5);

        //WebElement table_element = driver.findElement(By.className("table-bordered"));

        List<WebElement> playerNameElementsCollection=driver.findElements(By.className("media-heading"));

        sb.append("    Team Name:" + teamName + ": "+ playerNameElementsCollection.size());

        teamNamesAndPlayers.put(teamName, new ArrayList<String>());

        for(WebElement nameElement : playerNameElementsCollection)
            teamNamesAndPlayers.get(teamName).add(nameElement.getText().trim());

        //System.out.println(teamsPlayers);

        driver.close();
    }

}
