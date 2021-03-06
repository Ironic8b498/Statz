package me.staartvin.statz.datamanager.player;

import me.staartvin.statz.database.datatype.Query;
import me.staartvin.statz.database.datatype.RowRequirement;
import me.staartvin.statz.util.StatzUtil;

import java.util.*;

/**
 * Statistics of a player recorded by Statz
 * <br>
 * <br>
 * Statistics of a player are stored in (key, value) pairs where a key is a PlayerStat object and a value is a list
 * of queries that represent the data retrieved from the database. Each {@link Query} object represents one row in
 * the database.
 * <p>
 * Date created: 15:07:07
 * 17 apr. 2016
 *
 * @author "Staartvin"
 */
public class PlayerInfo {

    private UUID uuid;

    private Map<PlayerStat, List<Query>> statistics = new HashMap<>();

    public PlayerInfo(final UUID uuid) {
        this.setUUID(uuid);
    }

    /**
     * Get UUID of the player this PlayerInfo represents.
     *
     * @return uuid of the player
     */
    public UUID getUUID() {
        return uuid;
    }

    public void setUUID(final UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * Checks whether this PlayerInfo is valid. PlayerInfo is considered valid
     * when all of these requirements are met:
     * <ul>
     * <li>The data is not corrupt.
     * <li>The query was valid and did not give any errors.
     * <li>The requested data is stored about this player
     * </ul>
     * Note that the notion of validity is deprecated and this method will always return true.
     *
     * @return true if valid info, false otherwise.
     */
    @Deprecated
    public boolean isValid() {
        return true;
    }

    @Deprecated
    public void setValid(final boolean isValid) {
        // Do nothing.
    }

    /**
     * Get data for a certain statistic of a player. It returns a list of {@link Query} objects that represent the
     * data in the database. Note that each Query object represents one row in the database.
     *
     * @param statType Type of statistic to get data for.
     * @return A list of {@link Query} objects that represent the rows in the database. If there is no data for the
     * given type of statistic, an empty list will be returned.
     */
    public List<Query> getDataOfPlayerStat(PlayerStat statType) {
        if (!this.hasDataOfPlayerStat(statType)) {
            return new ArrayList<>();
        }

        return statistics.get(statType);
    }

    /**
     * Get a row of a specific dataset.
     * See {@link #getDataOfPlayerStat(PlayerStat)} for more information.
     *
     * @param statType  Type of statistic to get data for.
     * @param rowNumber Row number to get query of.
     * @return a Query that corresponds to this row, or null if it doesn't exist.
     */
    public Query getRow(PlayerStat statType, int rowNumber) {
        List<Query> rows = this.getDataOfPlayerStat(statType);

        if (rowNumber < 0 || rowNumber >= rows.size()) {
            return null;
        }

        return rows.get(rowNumber);
    }

    /**
     * Get the total number of rows in this PlayerInfo object.
     *
     * @return total number of rows.
     */
    public int getTotalNumberOfRows() {
        int totalSize = 0;

        for (PlayerStat statType : PlayerStat.values()) {
            totalSize += getNumberOfRows(statType);
        }

        return totalSize;
    }

    /**
     * Get the number of rows that are stored for a statistic.
     *
     * @param statType Type of statistic
     * @return number of rows that are stored. If no rows are stored, zero is returned.
     */
    public int getNumberOfRows(PlayerStat statType) {
        List<Query> rows = this.getDataOfPlayerStat(statType);

        return rows.size();
    }

    /**
     * Get the number of different statistics are stored in this PlayerInfo object.
     *
     * @return number of statistics stored.
     */
    public int getNumberOfStatistics() {
        return statistics.size();
    }

    /**
     * Check whether data for a given statistic is available.
     *
     * @param statType Type of statistic to check.
     * @return true if the data is available, false otherwise.
     */
    public boolean hasDataOfPlayerStat(PlayerStat statType) {
        return statistics.containsKey(statType) && statistics.get(statType) != null;
    }

    /**
     * Get the value of a given column of a given row.
     *
     * @param statType   Type of statistic to get data for.
     * @param rowNumber  Row number to get the query of.
     * @param columnName Name of the column to get info out of the row.
     * @return a value that corresponds to the given value in the returned results or null if nothing was found.
     */
    public Object getValue(PlayerStat statType, int rowNumber, String columnName) {
        Query row = this.getRow(statType, rowNumber);

        if (row == null) return null;

        return row.getValue(columnName);
    }

    /**
     * Get the sum of all values in the 'value' column of each row that meets the given RowRequirements.
     * See the {@link RowRequirement} class for more info about requirements and some examples.
     *
     * @param statType Type of statistics to get data for.
     * @param reqs     A list of requirements that need to be met before adding the value to the sum.
     * @return the sum of the values in the rows that meet the given requirement or 0 if results were invalid or
     * non-existent.
     */
    public double getTotalValue(PlayerStat statType, RowRequirement... reqs) {
        // Check if we have any requirements - if not, just return double value.
        if (reqs == null || reqs.length == 0) {
            return this.getTotalValue(statType);
        }

        double value = 0;

        List<Query> rows = this.getDataOfPlayerStat(statType);

        if (rows.isEmpty())
            return value;

        for (Query row : rows) {
            if (!row.meetsAllRequirements(Arrays.asList(reqs))) {
                continue;
            }

            // All conditions were met, so we add this value.
            value += row.getDoubleValue("value");
        }

        return value;
    }

    /**
     * Set the data for a specific statistic.
     *
     * @param statType Type of statistic
     * @param rows     Data to set
     * @throws IllegalArgumentException if statType or rows is invalid.
     */
    public void setData(PlayerStat statType, List<Query> rows) throws IllegalArgumentException {

        if (statType == null) {
            throw new IllegalArgumentException("Stat cannot be null.");
        }

        if (rows == null) {
            throw new IllegalArgumentException("Given rows cannot be null");
        }

        statistics.put(statType, rows);
    }

    /**
     * Add a row of data of a specific statistic.
     *
     * @param statType Type of statistic.
     * @param row      Row to add.
     * @throws IllegalArgumentException if statistic is null or the row is null.
     */
    public void addRow(PlayerStat statType, Query row) throws IllegalArgumentException {

        if (statType == null) {
            throw new IllegalArgumentException("Stat cannot be null.");
        }

        if (row == null) {
            throw new IllegalArgumentException("Row cannot be null");
        }

        List<Query> rows = this.getDataOfPlayerStat(statType);

        rows.add(row);

        this.setData(statType, rows);
    }

    /**
     * Remove a row from data of a specific statistic.
     *
     * @param statType Type of statistic.
     * @param row      Row to remove.
     * @throws IllegalArgumentException if statistic is null or row is null.
     */
    public void removeRow(PlayerStat statType, Query row) throws IllegalArgumentException {
        if (statType == null) {
            throw new IllegalArgumentException("Stat cannot be null");
        }

        if (row == null) {
            throw new IllegalArgumentException("Row cannot be null");
        }

        // No row to be removed
        if (!this.hasDataOfPlayerStat(statType)) {
            return;
        }

        List<Query> rows = this.getDataOfPlayerStat(statType);

        rows.remove(row);

        this.setData(statType, rows);
    }

    /**
     * Get the total value of the 'value' column. This method sums up all the values from the 'value' column in each
     * row.
     *
     * @return the sum of the values of each row.
     */
    public double getTotalValue(PlayerStat statType) {
        double value = 0;

        for (Query q : this.getDataOfPlayerStat(statType)) {
            value += q.getValue();
        }

        return value;
    }

    /**
     * Get the total value but round to given decimal places.
     *
     * @param roundedDecimals How many decimal places to round to.
     * @return the same as {@link #getTotalValue(PlayerStat)}, but rounded to given decimal places.
     */
    public double getTotalValue(PlayerStat statType, int roundedDecimals) {
        double value = getTotalValue(statType);

        return StatzUtil.roundDouble(value, roundedDecimals);
    }

    @Override
    public String toString() {
        StringBuilder endString = new StringBuilder("PlayerInfo of " + this.getUUID() + ": {");

        StringBuilder queryString;

        for (Map.Entry<PlayerStat, List<Query>> entry : statistics.entrySet()) {
            PlayerStat statType = entry.getKey();
            List<Query> queries = entry.getValue();

            queryString = new StringBuilder(statType + ": {");

            for (Query q : queries) {
                queryString.append(q.toString()).append(", ");
            }

            int lastComma = queryString.lastIndexOf(",");

            if (lastComma >= 0) {
                queryString.deleteCharAt(lastComma);
            }

            queryString.append("}, ");
            endString.append(queryString.toString().trim());
        }

        endString = new StringBuilder(endString.toString().trim());

        endString.append("}");

        return endString.toString();
    }

    /**
     * Get a PlayerInfo object from this PlayerInfo object that will not conflict with the given comparePlayerInfo
     * object.
     *
     * @param comparePlayerInfo Given PlayerInfo object
     * @return non conflicting PlayerInfo object that contains all data from this PlayerInfo object and the given
     * PlayerInfo object.
     * @throws IllegalArgumentException if given PlayerInfo object is null.
     */
    public PlayerInfo resolveConflicts(PlayerInfo comparePlayerInfo) throws IllegalArgumentException {

        if (comparePlayerInfo == null) {
            throw new IllegalArgumentException("PlayerInfo object cannot be null!");
        }

        PlayerInfo nonConflictingPlayerInfo = new PlayerInfo(this.getUUID());

        for (PlayerStat statType : PlayerStat.values()) {

            if (!this.hasDataOfPlayerStat(statType) && !comparePlayerInfo.hasDataOfPlayerStat(statType)) {
                // If both PlayerInfo objects do not store data about this stat type, ignore it.
                continue;
            }

            List<Query> rows = this.getDataOfPlayerStat(statType);

            List<Query> comparedRows = comparePlayerInfo.getDataOfPlayerStat(statType);

            List<Query> conflictingQueries = new ArrayList<>();

            List<Query> nonConflictingQueries = new ArrayList<>();

            // If one of the lists is empty, the other will never conflict and so we can safely add all queries.
            if (comparedRows.isEmpty()) {
                nonConflictingQueries.addAll(rows);
            } else if (rows.isEmpty()) {
                nonConflictingQueries.addAll(comparedRows);
            } else {
                for (Query row : rows) {
                    for (Query comparedRow : comparedRows) {
                        // If rows conflict, add their non conflicting counterpart and put both rows on a list of
                        // conflicting queries.
                        if (row.conflicts(comparedRow)) {
                            nonConflictingQueries.add(row.resolveConflict(comparedRow));

                            // We store all queries that conflict.
                            conflictingQueries.add(row);
                            conflictingQueries.add(comparedRow);
                        }
                    }
                }


                // Now, we add all queries that have not conflicted in some way. This means that we add the negation of
                // conflictingQueries.

                // Loop over both lists and add queries that do not conflict.
                for (Query row : rows) {
                    if (!conflictingQueries.contains(row)) {
                        nonConflictingQueries.add(row);
                    }
                }

                for (Query comparedRow : comparedRows) {
                    if (!conflictingQueries.contains(comparedRow)) {
                        nonConflictingQueries.add(comparedRow);
                    }
                }
            }

            // If the list is empty, we do not need to add it.
            if (nonConflictingQueries.isEmpty()) {
                //continue;
            }

            // We've built up all queries that are non-conflicting. Hence, we should add this to the PlayerInfo object.
            nonConflictingPlayerInfo.setData(statType, nonConflictingQueries);
        }

        return nonConflictingPlayerInfo;
    }

    /**
     * Get all statistics that are stored in this PlayerInfo object.
     *
     * @return a list of statistics
     */
    public List<PlayerStat> getStatistics() {
        List<PlayerStat> statTypes = new ArrayList<>();

        for (PlayerStat statType : this.statistics.keySet()) {
            statTypes.add(statType);
        }

        return statTypes;
    }

    /**
     * Get all rows in this PlayerInfo object for the given statistic.
     *
     * @param statType Type of statistic to get the rows for
     * @return a list of rows or an empty list if this object does not store info about the requested statistic.
     */
    public List<Query> getRows(PlayerStat statType) {
        if (!this.hasDataOfPlayerStat(statType)) {
            return new ArrayList<>();
        }

        return this.statistics.get(statType);
    }

    /**
     * Get all rows of this PlayerInfo object. The rows are not given in any particular order.
     *
     * @return a list of rows in the PlayerInfo object.
     */
    public List<Query> getRows() {
        List<Query> rows = new ArrayList<>();

        for (PlayerStat statType : PlayerStat.values()) {
            rows.addAll(this.getRows(statType));
        }

        return rows;
    }

    /**
     * Get every query in this PlayerInfo object per stored statistic.
     * This method returns a map where the key is the statistic and the associated value is a list of queries that
     * are stored in this object.
     *
     * @return all queries stored per statistic type.
     */
    public Map<PlayerStat, List<Query>> getRowsPerStatistic() {
        Map<PlayerStat, List<Query>> map = new HashMap<>();

        for (Map.Entry<PlayerStat, List<Query>> entry : this.statistics.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }

            map.put(entry.getKey(), entry.getValue());
        }

        return map;
    }
}
