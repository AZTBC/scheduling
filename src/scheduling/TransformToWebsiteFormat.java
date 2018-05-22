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
import java.util.Date;

/**
 * Created by mjain on 9/23/16.
 */
public class TransformToWebsiteFormat
{
    private static String fileName = "/Users/mjain/Temp/Cricket/Winter League 2017-18/Scheduling/Winter 2017-18 Schedule v6.xlsx";
    private static OPCPackage pkg;
    private static XSSFWorkbook wb;
    private static SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

    private static int dayColumnNumber = 0;
    private static int dateColumnNumber = 1;
    private static int hostTeamColumnNumber = 2;
    private static int guestTeamColumnNumber = 3;
    private static int groundColumnNumber = 4;
    private static int umpiringColumnNumber = 5;

    private static int regularLeagueScheduleSheetNumber = 0;
    private static int warmupsScheduleSheetNumber = 1;

    public static void main(String [] args) throws IOException, InvalidFormatException, ParseException {
        transformToWebsiteFormat();
    }

    public static void transformToWebsiteFormat() throws IOException, InvalidFormatException, ParseException {
        openFile();

        readAndTransform();

        closeFile();
    }

    private static void openFile() throws InvalidFormatException, IOException {

        pkg = OPCPackage.open(new File(fileName));
        wb = new XSSFWorkbook(pkg);

        System.out.println("File Opened: " + fileName);

    }

    private static void readAndTransform() throws ParseException {
        Sheet sheet = wb.getSheetAt(regularLeagueScheduleSheetNumber);

        System.out.println("Home Team," + "Visiting Team," + "Division," + "Play Off," + "Date," + "Venue," + "Umpire1," + "Umpire2," + "Placeholder Text");

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue;

            if(row.getCell(0) == null || row.getCell(0).toString().trim().equalsIgnoreCase("")) continue;

            String homeTeam = row.getCell(hostTeamColumnNumber).toString().trim();
            String awayTeam = row.getCell(guestTeamColumnNumber).toString().trim();

            String dateString = sdf.format(row.getCell(dateColumnNumber).getDateCellValue());
            Date date = sdf.parse(dateString);

            if(date.before(sdf.parse("11/1/2017"))) dateString += " 8:00";
            else dateString += " 9:00";


            String venue = row.getCell(groundColumnNumber).toString().trim();
            String umpiringTeam = "Umpire " + row.getCell(umpiringColumnNumber).toString().trim();

            //for warmups
            //String umpiringTeam = "";

            System.out.println(homeTeam + "," + awayTeam + "," + "A" + "," + "No" + "," + dateString + "," + venue + "," + umpiringTeam + "," + umpiringTeam);
        }
    }


    private static void closeFile() throws IOException {
        pkg.close();

        System.out.println("Closed File: " + fileName);
    }
}
