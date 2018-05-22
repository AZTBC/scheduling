package scheduling;

import j2html.tags.Tag;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static j2html.TagCreator.*;

public class NewsLetter
{
    private static String fileName = "stats.xlsx";
    private static String delimeter = "|";
    private static OPCPackage pkg;
    private static XSSFWorkbook wb;
    private static List<String> bowlers = new ArrayList<String>();
    private static List<String> batsmen = new ArrayList<String>();
    private static List<String> pointsTable = new ArrayList<String>();


    public static void main(String [] args) throws IOException, InvalidFormatException {
        readExcelSheet();
        printH1();

        printDiv("Best Bowlers - Week 5");
        printNewLine();
        buildBowlersTable();
        printNewLine();

        printDiv("Best Batsmen - Week 5");
        printNewLine();
        buildBatsmenTable();
        printNewLine();

        printDiv("Points Table after Week 5");
        printNewLine();
        buildPointsTable();
        printNewLine();

        System.out.print("Full Points Table: ");
        System.out.println(a().withText("https://aztbc.com/standingStats").withHref("https://aztbc.com/standingStats").withTarget("_blank").render());

        //Full Points Table: <a href="https://aztbc.com/standingStats" target="_blank">https://aztbc.com/standingStats</a>
    }

    private static void readExcelSheet() throws IOException, InvalidFormatException {
        openFile();

        readBowlersStats();

        readBatsmenStats();

        readPointsTable();

        closeFile();
    }

    private static void readBowlersStats() {

        readStats(0, bowlers, "Bowler");

        //System.out.println(bowlers);
    }

    private static void readBatsmenStats() {

        readStats(1, batsmen, "Batsman");

       // System.out.println(batsmen);

    }

    private static void readStats(int sheetNumber, List<String> list, String type)
    {
        Sheet sheet = wb.getSheetAt(sheetNumber);

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue;
            if (row.getCell(0) == null || row.getCell(0).toString() == "") break;

            String playerName = row.getCell(0).toString().trim();
            String teamName = row.getCell(1).toString().trim();
            String runsOrOvers = "";

            if(type.equalsIgnoreCase("Batsman"))
                runsOrOvers = String.valueOf((int) (row.getCell(2).getNumericCellValue()));
            else runsOrOvers = String.valueOf((row.getCell(2).getNumericCellValue()));

            String ballsOrMaidens = String.valueOf((int)(row.getCell(3).getNumericCellValue()));
            String foursOrRuns = String.valueOf((int)(row.getCell(4).getNumericCellValue()));
            String sixesOrWickets = String.valueOf((int)(row.getCell(5).getNumericCellValue()));
            String teamAgainst = row.getCell(6).toString().trim();

            String rowData = playerName + delimeter + teamName + delimeter + runsOrOvers + delimeter + ballsOrMaidens + delimeter + foursOrRuns + delimeter + sixesOrWickets + delimeter + teamAgainst;
            list.add(rowData);
        }

    }

    private static void readPointsTable() {

        Sheet sheet = wb.getSheetAt(2);

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue;
            if (row.getCell(0) == null || row.getCell(0).toString() == "") break;

            String pos = String.valueOf((int)(row.getCell(0).getNumericCellValue())) + ".";
            String teamName = row.getCell(1).toString().trim();
            String played = String.valueOf((int)(row.getCell(2).getNumericCellValue()));
            String won = String.valueOf((int)(row.getCell(3).getNumericCellValue()));
            String lost = String.valueOf((int)(row.getCell(4).getNumericCellValue()));
            String tied = String.valueOf((int)(row.getCell(5).getNumericCellValue()));
            String noResult = String.valueOf((int)(row.getCell(6).getNumericCellValue()));
            String points = String.valueOf((int)(row.getCell(7).getNumericCellValue()));
            String nrr = String.valueOf((row.getCell(8).getNumericCellValue()));

            String rowData = pos + delimeter + teamName + delimeter + played + delimeter + won + delimeter + lost + delimeter + tied + delimeter + noResult + delimeter + points + delimeter + nrr;
            pointsTable.add(rowData);
        }
    }

    private static void printH1() {
        System.out.println(h1("Performers of the Week").withClass("mc-toc-title").withStyle("text-align: center;").render() + "\n");
    }

    private static void printDiv(String toPrint) {
        System.out.println(
                div(
                        strong(toPrint)
                ).withStyle("text-align: center;").render() +  "\n");

    }

    private static void printNewLine() {
        System.out.println(br().render());
    }

    private static void buildBowlersTable() {
        System.out.println(
                table(
                        buildThread("O", "M", "R", "W"),
                        buildBody(bowlers)

                ).withStyle("width: 800px").render()
        );
    }

    private static void buildBatsmenTable() {
        System.out.println(
                table(
                        buildThread("R", "B", "4s", "6s"),
                        buildBody(batsmen)

                ).withStyle("width: 800px").render()
        );
    }

    private static Tag buildThread(String third, String fourth, String fifth, String sixth) {
        return thead(
                tr(
                        th("Player").withStyle("text-align: left; width: 25%"),
                        th("Team").withStyle("text-align: left; width: 20%"),
                        th(third).withStyle("text-align: left; width: 7%"),
                        th(fourth).withStyle("text-align: left; width: 7%"),
                        th(fifth).withStyle("text-align: left; width: 7%"),
                        th(sixth).withStyle("text-align: left; width: 7%"),
                        th("Against").withStyle("text-align: left; width: 27%")
                )
        );
    }

    private static Tag buildBody(List<String> elements) {
        return tbody(
                each(elements, element ->
                        buildRow(element)
                )
        );
    }

    private static Tag buildRow(String row) {

        String[] elements = row.split("\\|");
        List<String> elementsList = new ArrayList<>();

        for(String element : elements) elementsList.add(element);


        return tr(
                each(elementsList, element ->
                        td(element)
                )
        );
    }

    private static void buildPointsTable() {
        System.out.println(
                table(
                        buildPTThread(),
                        buildBody(pointsTable)

                ).withStyle("width: 800px").render()
        );
    }

    private static Tag buildPTThread() {
        return thead(
                tr(
                        th("Position").withStyle("text-align: left; width: 10%"),
                        th("Team").withStyle("text-align: left; width: 20%"),
                        th("Played").withStyle("text-align: left; width: 10%"),
                        th("Won").withStyle("text-align: left; width: 10%"),
                        th("Lost").withStyle("text-align: left; width: 10%"),
                        th("Tied").withStyle("text-align: left; width: 10%"),
                        th("N/D").withStyle("text-align: left; width: 10%"),
                        th("Points").withStyle("text-align: left; width: 10%"),
                        th("NRR").withStyle("text-align: left; width: 10%")
                )
        );
    }

    private static void openFile() throws InvalidFormatException, IOException {

        pkg = OPCPackage.open(new File(fileName));
        wb = new XSSFWorkbook(pkg);

        //System.out.println("File Opened: " + fileName);

    }

    private static void closeFile() throws IOException {
        pkg.close();

        //System.out.println("Closed File: " + fileName);
    }
}
