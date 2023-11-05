package rip.warzone.hub.scoreboard;

import net.frozenorb.qlib.scoreboard.ScoreboardConfiguration;
import net.frozenorb.qlib.scoreboard.TitleGetter;
import rip.warzone.hub.HubConstants;

public class HubScoreConfiguration {

    public static ScoreboardConfiguration create(){
        ScoreboardConfiguration configuration = new ScoreboardConfiguration();
        configuration.setScoreGetter(new HubScoreGetter());
        configuration.setTitleGetter(new TitleGetter(HubConstants.SCOREBOARD_TITLE.replace("&", "\u00a7")));
        return configuration;
    }

}
