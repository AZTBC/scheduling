package scheduling;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.util.Date;

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

public class VerifySchedule
{
    //private static String fileName = "/Users/mjain/Temp/Cricket/Winter League 2017-18/Scheduling/Winter 2017-18 Schedule v6.xlsx";
    private static String fileName = "/Users/mjain/Temp/Cricket/Summer 2018/Schedule/2018 Summer - Schedule - v2.xlsx";
    private static OPCPackage pkg;
    private static XSSFWorkbook wb;
    private static Sheet sheet;

    private static int dayColumnNumber = 0;
    private static int dateColumnNumber = 1;
    private static int hostTeamColumnNumber = 2;
    private static int guestTeamColumnNumber = 3;
    private static int groundColumnNumber = 4;
    private static int umpiringColumnNumber = 5;

    private static int regularLeagueScheduleSheetNumber = 0;
    private static int warmupsScheduleSheetNumber = 1;



    private static Map<String, TeamForVerification> weekend = new HashMap<String, TeamForVerification>();

    public static void main(String [] args) throws IOException, InvalidFormatException {
        openFile();

        performTeamVerification();

        performGroundVerification();

        closeFile();
    }

    private static void openFile() throws InvalidFormatException, IOException {

        pkg = OPCPackage.open(new File(fileName));
        wb = new XSSFWorkbook(pkg);
        sheet = wb.getSheetAt(regularLeagueScheduleSheetNumber);
        //sheet = wb.getSheetAt(warmupsScheduleSheetNumber);

        System.out.println("Opened File");
    }

    private static void performTeamVerification()
    {
        System.out.println(sheet.getLastRowNum());

        System.out.println("Performing Team Verification");

        for(Row row : sheet)
        {
            if(row.getRowNum() == 0) continue;

            if(row.getCell(0) == null || row.getCell(0).toString().trim().isEmpty())
            {
                //todo: write verification logic
                verifyWeekend();

                weekend.clear();

                continue;
            }

            String date = row.getCell(dateColumnNumber).toString().trim();
            String guestTeamName = row.getCell(guestTeamColumnNumber).toString().trim();
            String hostTeamName = row.getCell(hostTeamColumnNumber).toString().trim();

            //logic required for warmups where we don't assign umpiring teams
            String umpringTeamName = null;
            if(row.getCell(umpiringColumnNumber) != null) umpringTeamName = row.getCell(umpiringColumnNumber).toString().trim();

            addPlayingTeam(guestTeamName, date);
            addPlayingTeam(hostTeamName, date);
            if(umpringTeamName != null) addUmpiringTeam(umpringTeamName, date);
        }

        System.out.println("Team Verification Completed");
    }

    private static void verifyWeekend() {
        for(String teamName : weekend.keySet())
        {
            TeamForVerification team = weekend.get(teamName);

            //if team is scheduled for more than one game on a weekend
            if(team.getWeekendNumberOfGames() > 1)
            {
                System.out.println("-- Weekend Number of games Issue: " + team.getTeamName() + ", " + team.getMatchDate() + ", " + team.getWeekendNumberOfGames());
                continue;
            }

            //if team is scheduled for a match and more than one umpring assignments on a weekend
            if(team.getWeekendNumberOfGames() > 0 && team.getWeekendNumberOfUmpiringAssignments() > 1)
            {
                System.out.println("-- Games + More than one umpiring assignment: " + team.getTeamName() + ", " + team.getMatchDate());
                continue;
            }

            //if team has a match and umpiring on the same day
            if(team.getWeekendNumberOfGames() > 0 && team.getWeekendNumberOfUmpiringAssignments() > 0 && team.getMatchDate().equalsIgnoreCase(team.getUmpringDate()))
            {
                System.out.println("-- Games and umpiring on same day " + team.getTeamName() + ", " + team.getMatchDate());
                continue;
            }
        }
    }

    private static void performGroundVerification() {
        System.out.println("Performing Ground Verification");

        Map<String, ArrayList<String>> datesAndGrounds = new HashMap<String, ArrayList<String>>();

        for(Row row : sheet) {

            if (row.getRowNum() == 0 || row.getCell(0) == null || row.getCell(0).toString().trim().isEmpty()) continue;

            String date = row.getCell(dateColumnNumber).toString().trim();
            String ground = row.getCell(groundColumnNumber).toString().trim();

            if(datesAndGrounds.containsKey(date))
            {
                ArrayList<String> grounds = datesAndGrounds.get(date);

                if(grounds.contains(ground)) System.out.println("--" + ground + " used multiple times on: " + date);
                else grounds.add(ground);
            }
            else
            {
                ArrayList<String> grounds = new ArrayList<String>();
                grounds.add(ground);

                datesAndGrounds.put(date, grounds);
            }
        }

        System.out.println("Ground Verification Completed");
    }

    private static void addUmpiringTeam(String umpringTeamName, String date)
    {
        if(weekend.containsKey(umpringTeamName))
        {
            weekend.get(umpringTeamName).incrementNumberOfUmpiringAssignments();
            weekend.get(umpringTeamName).setUmpringDate(date);
        }
        else
        {
            TeamForVerification team = new TeamForVerification();
            team.setTeamName(umpringTeamName);
            team.setUmpringDate(date);
            team.setWeekendNumberOfUmpiringAssignments(1);
            weekend.put(umpringTeamName, team);
        }
    }

    private static void addPlayingTeam(String teamName, String date)
    {
        if(weekend.containsKey(teamName))
        {
            weekend.get(teamName).incrementNumberOfGames();
            weekend.get(teamName).setMatchDate(date);
        }
        else
        {
            TeamForVerification team = new TeamForVerification();
            team.setTeamName(teamName);
            team.setMatchDate(date);
            team.setWeekendNumberOfGames(1);
            weekend.put(teamName, team);
        }
    }

    private static void closeFile() throws IOException {
        pkg.close();

        System.out.println("Closed File");
    }



}

