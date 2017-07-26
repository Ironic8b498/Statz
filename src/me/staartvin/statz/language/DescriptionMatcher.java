package me.staartvin.statz.language;

import me.staartvin.statz.database.datatype.Query;
import me.staartvin.statz.datamanager.PlayerStat;
import me.staartvin.statz.datamanager.player.PlayerInfo;
import me.staartvin.statz.util.StatzUtil;

/**
 * This class is used to match descriptions to statistics, in varying detail.
 * <p>
 * Created by s150149 on 27-5-2017.
 */
public class DescriptionMatcher {

    public String getTotalDescription(PlayerInfo info, PlayerStat statType) {

        StatisticDescription descriptionEnum = StatisticDescription.valueOf(statType.toString());

        String description = "";

        if (statType == statType.TIME_PLAYED) {
            description = descriptionEnum.getTotalDescription(StatzUtil.timeToString((int) info.getTotalValue(), StatzUtil.Time.MINUTES));
        } else {
            description = descriptionEnum.getTotalDescription((int) info.getTotalValue());
        }


        return description;
    }

    public String getHighDetailDescription(Query query, PlayerStat statType) {
        String description = "";

        StatisticDescription descriptionEnum = StatisticDescription.valueOf(statType.toString());

        if (descriptionEnum == null) {
            return null;
        }

        switch (statType) {
            case JOINS:
            case VOTES:
                description = descriptionEnum.getHighDetailDescription(query.getValue());
                break;
            case ARROWS_SHOT:
                description = descriptionEnum.getHighDetailDescription((int) query.getValue(),
                        StatzUtil.roundDouble(Double.parseDouble(query.getValue("forceShot").toString()), 2),
                        query.getValue("world"));
                break;
            case BLOCKS_BROKEN:
            case BLOCKS_PLACED:
                description = descriptionEnum.getHighDetailDescription((int) query.getValue(),
                        query.getIntValue("typeid"), query.getIntValue("datavalue"),
                        query.getValue("world"));
                break;
            case BUCKETS_EMPTIED:
            case BUCKETS_FILLED:
            case DEATHS:
            case EGGS_THROWN:
            case ENTERED_BEDS:
            case TIMES_SHORN:
            case XP_GAINED:
                description = descriptionEnum.getHighDetailDescription((int) query.getValue(),
                        query.getValue("world"));
                break;
            case COMMANDS_PERFORMED:
                description = descriptionEnum.getHighDetailDescription(query.getValue("command").toString()
                                + " " + query.getValue("arguments").toString(),
                        (int) query.getValue(), query.getValue("world"));
                break;
            case DAMAGE_TAKEN:
                description = descriptionEnum.getHighDetailDescription((int) query.getValue(),
                        query.getValue("cause").toString(), query.getValue("world"));
                break;
            case DISTANCE_TRAVELLED:
                description = descriptionEnum.getHighDetailDescription(StatzUtil.roundDouble(query.getValue(), 2),
                        query.getValue("world"), query.getValue("moveType"));
                break;
            case FOOD_EATEN:
                description = descriptionEnum.getHighDetailDescription(query.getValue("foodEaten"),
                        (int) query.getValue(), query.getValue("world"));
                break;
            case ITEMS_CAUGHT:
                description = descriptionEnum.getHighDetailDescription(query.getValue("caught"),
                        (int) query.getValue(), query.getValue("world"));
                break;
            case ITEMS_CRAFTED:
            case ITEMS_DROPPED:
            case ITEMS_PICKED_UP:
            case TOOLS_BROKEN:
                description = descriptionEnum.getHighDetailDescription(query.getValue("item"),
                        (int) query.getValue(), query.getValue("world"));
                break;
            case KILLS_MOBS:
                description = descriptionEnum.getHighDetailDescription((int) query.getValue(),
                        query.getValue("mob"), query.getValue("world"));
                break;
            case KILLS_PLAYERS:
                description = descriptionEnum.getHighDetailDescription(query.getValue("playerKilled"),
                        (int) query.getValue(), query.getValue("world"));
                break;
            case TELEPORTS:
                description = descriptionEnum.getHighDetailDescription(query.getValue("world"),
                        query.getValue("destWorld"), (int) query.getValue(),
                        query.getValue("cause"));
                break;
            case TIME_PLAYED:
                description = descriptionEnum.getHighDetailDescription(StatzUtil.timeToString((int) query.getValue(),
                        StatzUtil.Time.MINUTES), query.getValue("world"));
                break;
            case TIMES_KICKED:
                description = descriptionEnum.getHighDetailDescription((int) query.getValue(),
                        query.getValue("world"), query.getValue("reason"));
                break;
            case VILLAGER_TRADES:
                description = descriptionEnum.getHighDetailDescription((int) query.getValue(),
                        query.getValue("world"), query.getValue("trade"));
                break;
            case WORLDS_CHANGED:
                description = descriptionEnum.getHighDetailDescription( query.getValue("world"),
                        query.getValue("destWorld"), (int) query.getValue());
                break;
        }

        return description;

    }

}