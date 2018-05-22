package scheduling;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: mjain
 * Date: 1/28/15
 * Time: 10:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class Scheduling
{
    private static String fileName = "matches-winter-2017-18.xlsx";
    private static OPCPackage pkg;
    private static XSSFWorkbook wb;
    private static ArrayList<Team> teams = new ArrayList<Team>();
    private static ArrayList<Ground> grounds = new ArrayList<Ground>();
    private static ArrayList<Date> offDates = new ArrayList<Date>();
    private static ArrayList<Match> matches = new ArrayList<Match>();
    private static ArrayList<Weekend> weekends = new ArrayList<Weekend>();

    private static final int maxUmpiringPerTeam = 4;
    private static final int maxMatchesPerDay = 3;
    private static int totalNumMatches;
    private static int numUnscheduledMatches;
    private static SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    private static SimpleDateFormat outputDateFormat = new SimpleDateFormat("E,MM-dd-yyyy");

    private static String leagueStartDateString = "04/28/2018";
    private static Date leagueStartDate;

    public static void main(String[] args) throws IOException, InvalidFormatException, ParseException, InterruptedException {
        performScheduling();

        //TransformToWebsiteFormat.transformToWebsiteFormat();

    }

    private static void performScheduling() throws IOException, ParseException, InvalidFormatException {
        leagueStartDate = sdf.parse(leagueStartDateString);
        openFile();

        readLeagueOffDates();
        readTeamsAndGrounds();
        readMatches();

        //sort grounds based on the total number of games to be played on the ground, higher to lower
        Collections.sort(grounds, new GroundComparator());


        //for(Ground ground : grounds) System.out.println(ground.getName() + ",\t #Teams: " + ground.getNumberOfTeams() + ",\t #Games: " + ground.getTotalNumberOfGames());

        scheduleMatches();

        scheduleUmpiringAssignments();

        //todo: umpiring assignments are not perfect, sometimes some teams have less than 7 assignments, check the logic
        //todo: some teams prefer sat over sun, some teams can't play on a certain date

        printData();

        closeFile();
    }

    private static void printData() {

        System.out.println("\nTeams and # of umpiring assignments: ");
        for(Team team : teams) System.out.println(team.getName() + "," + team.getNumberOfUmpiringAssignments());

        Collections.sort(matches, new MatchComparator());

        System.out.println();
        System.out.println("Date," + "Away Team," + "Home Team," + "Ground," + "Umpiring Team");

        for(Match match : matches)
        {
            String umpiringTeamName = "";

            if(match.getUmpiringTeam() != null) umpiringTeamName = match.getUmpiringTeam().getName();

            System.out.println(outputDateFormat.format(match.getMatchDate()) + "," + match.getGuestTeam().getName() + "," + match.getHostTeam().getName() + "," + match.getHostTeam().getHomeGround().getName() + "," + umpiringTeamName);
        }


        System.out.println("\n\n******Matches without umpiring assignments******");
        for(Match match : matches)
        {
            if(match.getUmpiringTeam() == null)
                System.out.println(outputDateFormat.format(match.getMatchDate()) + "," + match.getGuestTeam().getName() + "," + match.getHostTeam().getName() + "," + match.getHostTeam().getHomeGround().getName());
        }
    }

    private static void scheduleUmpiringAssignments()
    {
        for(Weekend weekend : weekends)
        {
            ArrayList<Match> saturdayMatchesThisWeekend = findMatchesOnThisDay(weekend.getSaturday());
            ArrayList<Match> sundayMatchesThisWeekend = findMatchesOnThisDay(weekend.getSunday());

            ArrayList<Team> teamsWithABye = findTeamsWithABye(weekend);
            ArrayList<Team> teamsPlayingOnSaturday = findTeamsPlayingOnThisDay(saturdayMatchesThisWeekend);
            ArrayList<Team> teamsPlayingOnSunday = findTeamsPlayingOnThisDay(sundayMatchesThisWeekend);

            //sort the teams from least to most umpirings done
            Collections.sort(teamsWithABye, new TeamComaparatorForUmpiring());
            Collections.sort(teamsPlayingOnSaturday, new TeamComaparatorForUmpiring());
            Collections.sort(teamsPlayingOnSunday, new TeamComaparatorForUmpiring());

            //System.out.println("#Teams with a bye: " + teamsWithABye.size());
            //System.out.println("# Saturday Matches: " + saturdayMatchesThisWeekend.size());
            //System.out.println("# Sunday Matches: " + sundayMatchesThisWeekend.size());

            //System.out.println();

            //teams with a bye can umpire in multiple matches on this weekend
            for (Team team : teamsWithABye)
            {

                for(Match match : saturdayMatchesThisWeekend)
                {
                    if(match.getUmpiringTeam() == null && team.getNumberOfUmpiringAssignments() < maxUmpiringPerTeam && match.getHostTeam().getHomeGround().getRegion().equalsIgnoreCase(team.getHomeGround().getRegion()))
                    {
                        match.setUmpiringTeam(team);
                        team.incrementNumberOfUmpiringAssignments();
                        //break;
                    }
                }

                for(Match match : sundayMatchesThisWeekend)
                {
                    if(match.getUmpiringTeam() == null && team.getNumberOfUmpiringAssignments() < maxUmpiringPerTeam && match.getHostTeam().getHomeGround().getRegion().equalsIgnoreCase(team.getHomeGround().getRegion()))
                    {
                        match.setUmpiringTeam(team);
                        team.incrementNumberOfUmpiringAssignments();
                        //break;
                    }
                }
            }

            ArrayList<Team> teamsUmpiringOnSaturday = findTeamsUmpiringOnThisDay(saturdayMatchesThisWeekend);
            ArrayList<Team> teamsUmpiringOnSunday = findTeamsUmpiringOnThisDay(sundayMatchesThisWeekend);

            scheduleUmpiringOnADay(saturdayMatchesThisWeekend, teamsPlayingOnSunday, teamsUmpiringOnSaturday);
            scheduleUmpiringOnADay(sundayMatchesThisWeekend, teamsPlayingOnSaturday, teamsUmpiringOnSunday);
        }
    }

    private static void scheduleUmpiringOnADay(ArrayList<Match> matchesForUmpiringAssignments, ArrayList<Team> teamsPlayingOnNextDay, ArrayList<Team> teamsUmpiringOnThisDay) //"next" goes either way, a day before or a day after
    {
        for(Match match : matchesForUmpiringAssignments)
        {
            if(match.getUmpiringTeam() == null)
            {
                for (Team team : teamsPlayingOnNextDay)
                {
                    if(team.getNumberOfUmpiringAssignments() < maxUmpiringPerTeam && match.getHostTeam().getHomeGround().getRegion().equalsIgnoreCase(team.getHomeGround().getRegion()) && !teamsUmpiringOnThisDay.contains(team))
                    {
                        match.setUmpiringTeam(team);
                        team.incrementNumberOfUmpiringAssignments();
                        teamsUmpiringOnThisDay.add(team);
                        break;
                    }
                }
            }
        }
    }

    private static ArrayList<Team> findTeamsPlayingOnThisDay(ArrayList<Match> matchesOnThisDay) {
        ArrayList<Team> teamsPlayingOnThisDay = new ArrayList<Team>();

        for(Match match : matchesOnThisDay)
        {
            teamsPlayingOnThisDay.add(match.getGuestTeam());
            teamsPlayingOnThisDay.add(match.getHostTeam());
        }

        return teamsPlayingOnThisDay;  //To change body of created methods use File | Settings | File Templates.
    }

    private static ArrayList<Team> findTeamsUmpiringOnThisDay(ArrayList<Match> matchesOnThisDay) {
        ArrayList<Team> teamsUmpiringOnThisDay = new ArrayList<Team>();

        for(Match match : matchesOnThisDay) if(match.getUmpiringTeam() != null) teamsUmpiringOnThisDay.add(match.getUmpiringTeam());

        return teamsUmpiringOnThisDay;  //To change body of created methods use File | Settings | File Templates.
    }

    private static ArrayList<Team> findTeamsWithABye(Weekend weekend)
    {
        ArrayList<Team> teamsWithABye = new ArrayList<Team>();

        for(Team team : teams) if(!weekend.getTeams().contains(team)) teamsWithABye.add(team);

        return teamsWithABye;
    }

    //todo: this method can be made more efficient, currently its iterating thru all the matches to find out matches being played on one day
    private static ArrayList<Match> findMatchesOnThisDay(GameDate gameDate)
    {
        ArrayList<Match> matchesOnThisDay = new ArrayList<Match>();

        for(Match match : matches) if(match.getMatchDate().equals(gameDate.getGameDate())) matchesOnThisDay.add(match);

        return matchesOnThisDay;  //To change body of created methods use File | Settings | File Templates.
    }

    private static void openFile() throws InvalidFormatException, IOException {

        pkg = OPCPackage.open(new File(fileName));
        wb = new XSSFWorkbook(pkg);

        System.out.println("Opened File");

    }

    public static void readTeamsAndGrounds() throws InvalidFormatException, IOException {

        Sheet sheet = wb.getSheetAt(1);

        for(Row row : sheet)
        {
            if(row.getRowNum() == 0) continue;

            if (row.getCell(0) != null)
            {
                String teamName = row.getCell(0).toString().trim();
                String homeGround = row.getCell(1).toString().trim();
                String region = row.getCell(2).toString().trim();

                Ground ground = returnOrCreateHomeGround(homeGround, region);

                Team newTeam = new Team(teamName, ground);

                teams.add(newTeam);
                ground.incrementNumberOfTeams();
            }
            else break;
        }

        //for(Team team : teams) System.out.println(team.getName() + ", " + team.getHomeGround().getName() + ", " + team.getHomeGround().getRegion());
        System.out.println("Read Teams and Ground, a total of: " + teams.size() + " teams and " + grounds.size() + " grounds");
        //for(Ground ground : grounds) System.out.println(ground.getName() + ", " + ground.getNumberOfTeams());

    }

    private static void readLeagueOffDates() {

        Sheet sheet = wb.getSheetAt(2);

        for(Row row : sheet)
        {
            Date date = row.getCell(0).getDateCellValue();
            offDates.add(date);

            //System.out.println(date);
        }

        System.out.println("Read League Off Dates, a total of: " + offDates.size());
    }

    private static void readMatches() {

        Sheet sheet = wb.getSheetAt(0);

        for(Row row : sheet)
        {
            if(row.getRowNum() == 0) continue;

            String guestTeamName = row.getCell(0).toString().trim();
            String hostTeamName = row.getCell(1).toString().trim();

            Team guestTeam = findTeamByName(guestTeamName);
            Team hostTeam = findTeamByName(hostTeamName);

            if(guestTeam == null || hostTeam == null)
            {
                System.out.println("Code Red...Exiting");
                System.exit(1);
            }

            matches.add(new Match(guestTeam, hostTeam));
            hostTeam.getHomeGround().incrementTotalNumberOfGames();
            hostTeam.getHomeGround().incrementNumberOfUnscheduledGames();
        }

        totalNumMatches = matches.size();
        numUnscheduledMatches = totalNumMatches;

        //for(Match match : matches) System.out.println(match.getGuestTeam().getName() + ", " + match.getHostTeam().getName());

        System.out.println("Read Matches, a total of: " + matches.size());
    }

    private static Team findTeamByName(String teamName) {

        for(Team team : teams) if(team.getName().equalsIgnoreCase(teamName)) return team;

        System.out.println("O_O Match sheet has a team name, " + teamName + " which was not found in the teams I read earlier. I am sad :(");
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    private static void scheduleMatches() {

        Random randomGenerator = new Random();

        while (numUnscheduledMatches > 0)
        {
            //pick a random match to schedule
            int i = randomGenerator.nextInt(totalNumMatches);

            Match match = findUnscheduledMatch(i);

            if(match != null && isMatchBeingPlayedAtTheBusiestGround(match.getHostTeam().getHomeGround()) && scheduleMatch(match))
            {
                //System.out.println("Scheduled on " + match.getMatchDate());
                numUnscheduledMatches--;
            }
        }
    }

    private static boolean isMatchBeingPlayedAtTheBusiestGround(Ground homeGround)
    {
        Collections.sort(grounds, new GroundComparator());

        for(Ground ground : grounds)
        {
            //System.out.println("This Ground: " + ground.getName());
            if (ground.getNumberOfUnscheduledGames() > 0)
            {
                if(ground.equals(homeGround)) return true;
                else return false;
            }
        }
        return true;
    }

    private static boolean scheduleMatch(Match match) {

        Weekend weekend = findFirstAvailableWeekend(match);

        GameDate schedulingDate = null;

        //this loop is to find out on which weekend there are no ground conflicts while making sure only n of games are scheduled per day
        while(true)
        {
            if(weekend.getSaturday().suits(maxMatchesPerDay, match.getHostTeam().getHomeGround()))
            {
                schedulingDate = weekend.getSaturday();
                break;
            }
            else if (weekend.getSunday().suits(maxMatchesPerDay, match.getHostTeam().getHomeGround()))
            {
                schedulingDate = weekend.getSunday();
                break;
            }

            weekend = findNextAvailableWeekend(weekend, match);
        }

        if(schedulingDate != null)
        {
            match.setMatchDate(schedulingDate.getGameDate());
            match.getHostTeam().getHomeGround().decreaseNumberOfUnscheduledGames();
            schedulingDate.incrementNumberOfMatchesScheduled();
            weekend.getTeams().add(match.getGuestTeam());
            weekend.getTeams().add(match.getHostTeam());
            schedulingDate.getTeams().add(match.getGuestTeam());
            schedulingDate.getTeams().add(match.getHostTeam());
            schedulingDate.getOccupiedGrounds().add(match.getHostTeam().getHomeGround());
            schedulingDate.getTeams().add(match.getGuestTeam());

            return true;
        }

        return false;  //To change body of created methods use File | Settings | File Templates.
    }

    private static Weekend findNextAvailableWeekend(Weekend weekend, Match match) {

        for(int i = weekends.indexOf(weekend)+1; i < weekends.size(); i++)
        {
            weekend = weekends.get(i);

            if(weekend.isSuitableForBothTeams(match)) return weekend;
        }

        return addAWeekend();
    }

    private static Weekend findFirstAvailableWeekend(Match match) {

        for(Weekend weekend : weekends) if(weekend.isSuitableForBothTeams(match)) return weekend;

        //if no weekend is suitable, we need to extend the schedule and add a weekend.
        return addAWeekend();
    }

    private static Weekend addAWeekend() {

        Date saturday;
        Date sunday;

        //if you are starting the schedule, add the first weekend based on the league start date
        if(weekends.size() == 0)
        {
            saturday = leagueStartDate;
            sunday = getDate(saturday, 1);

            //System.out.println("Adding first weekend, I am assuming the league starts on a Saturday: " + saturday + ", Sunday: " + sunday);
        }
        else
        {
            Weekend lastWeekend = weekends.get(weekends.size()-1);

            Date lastSaturday = lastWeekend.getSaturday().getGameDate();
            Date lastSunday = lastWeekend.getSunday().getGameDate();

            saturday = getDate(lastSaturday, 7);
            sunday = getDate(lastSunday, 7);

            //todo: think about how to handle only 1 off date over the weekend like for Charity Walk

            while(true)
            {
                if (offDates.contains(saturday) || offDates.contains(sunday))
                {
                    saturday = getDate(saturday, 7);
                    sunday = getDate(sunday, 7);
                }
                else
                    break;
            }

            //System.out.println("Adding new weekend, Saturday: " + saturday + ", Sunday: " + sunday);
        }

        Weekend weekend = new Weekend(new GameDate(saturday), new GameDate(sunday));

        weekends.add(weekend);

        return weekend;
    }

    private static Date getDate(Date saturday, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(saturday);
        cal.add(Calendar.DATE, i);

        return cal.getTime();
    }

    private static Match findUnscheduledMatch(int x) {

        Match match = matches.get(x);

        if(match.getMatchDate() == null) return match;

        for(int i = 0; i < totalNumMatches; i++)
        {
            if(x < matches.size()-i)
            {
                match = matches.get(x+i);
                if(match.getMatchDate() == null) return match;
            }

            if(x >= i)
            {
                match = matches.get(x-i);
                if(match.getMatchDate() == null) return match;
            }
        }

        return null;  //To change body of created methods use File | Settings | File Templates.
    }


    private static Ground returnOrCreateHomeGround(String groundName, String region)
    {
        for(Ground ground : grounds)
        {
            if(ground.getName().equalsIgnoreCase(groundName))
                  return ground;
        }

        Ground newGround = new Ground(groundName, region);

        grounds.add(newGround);

        return newGround;  //To change body of created methods use File | Settings | File Templates.
    }

    private static void closeFile() throws IOException {
        pkg.close();

        System.out.println("Closed File");
    }

}
