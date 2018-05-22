package scheduling.misc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PrintWeekends
{
    private static SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

    private static String leagueStartDateString = "04/28/2018";
    private static int numberWeekends = 18;

    public static void main(String [] args) throws ParseException {
        Date currentDate = sdf.parse(leagueStartDateString);

        for(int i = 0; i < numberWeekends; i++)
        {
            System.out.println("Sat," + sdf.format(currentDate));
            System.out.println("Sat," + sdf.format(currentDate));
            System.out.println("Sat," + sdf.format(currentDate));
            System.out.println("Sun," + sdf.format(getDate(currentDate, 1)));
            System.out.println("Sun," + sdf.format(getDate(currentDate, 1)));
            System.out.println("Sun," + sdf.format(getDate(currentDate, 1)));
            System.out.println();
            currentDate = getDate(currentDate, 7);
        }

    }

    private static Date getDate(Date saturday, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(saturday);
        cal.add(Calendar.DATE, i);

        return cal.getTime();
    }
}
