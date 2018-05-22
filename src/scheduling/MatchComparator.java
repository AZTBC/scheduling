package scheduling;

import java.util.Comparator;

public class MatchComparator implements Comparator<Match>
{
    @Override
    public int compare(Match match1, Match match2) {

        return match1.getMatchDate().compareTo(match2.getMatchDate());

    }
}
