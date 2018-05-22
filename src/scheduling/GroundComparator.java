package scheduling;

import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: mjain
 * Date: 1/29/15
 * Time: 2:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class GroundComparator implements Comparator<Ground>
{
    @Override
    public int compare(Ground ground1, Ground ground2) {

        return ground2.getNumberOfUnscheduledGames() - ground1.getNumberOfUnscheduledGames();

    }
}
