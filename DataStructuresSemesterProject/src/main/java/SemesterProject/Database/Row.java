package SemesterProject.Database;

import java.util.Arrays;

/**
 * An array holding a row of information for a table, each
 * as a DataEntry object and associated with a particular
 * column
 *
 * @author Yitzie Scheinman
 * @version 4.25.2017
 */

public class Row
{
    private DataEntry[] entries;

    public Row(int columns)
    {
        entries = new DataEntry[columns];
    }

    /**
     * @return The array of DataEntry objects
     */
    public DataEntry[] getEntries()
    {
        return this.entries;
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o) {
            return true;
        }
        if(!(o instanceof Row)) {
            return false;
        }
        Row row = (Row) o;
        return Arrays.equals(getEntries(), row.getEntries());
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(getEntries());
    }
}