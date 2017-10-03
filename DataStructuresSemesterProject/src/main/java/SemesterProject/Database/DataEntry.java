package SemesterProject.Database;

/**
 * Holds one "cell" of a table, an entry associated with
 * a particular column and stored in a particular row
 *
 * @author Yitzie Scheinman
 * @version 4.27.2017
 */

public class DataEntry<Type extends Comparable<Type>>
{
    private Type data = null;
    private Column column;

    public DataEntry(Type data)
    {
        if(data != null) {
            this.data = data;
        }
    }

    /**
     * A constructor that associates the entry with a
     * specific column
     *
     * @param column The column associated with the entry
     */
    public DataEntry(Column column)
    {
        this.column = column;
    }

    /**
     * @param data The data to put in the entry
     */
    public void addData(Type data)
    {
        this.data = data;
    }

    /**
     * @return The entry's data
     */
    public Type getData()
    {
        return this.data;
    }

    /**
     * @return The column associated with the entry
     */
    public Column getColumn()
    {
        return this.column;
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o) {
            return true;
        }
        if(!(o instanceof DataEntry)) {
            return false;
        }
        DataEntry<?> dataEntry = (DataEntry<?>)o;
        if(getData() != null ? !getData().equals(dataEntry.getData()) : dataEntry.getData() != null) {
            return false;
        }
        return getColumn() != null ? getColumn().equals(dataEntry.getColumn()) : dataEntry.getColumn() == null;
    }

    @Override
    public int hashCode()
    {
        int result = getData() != null ? getData().hashCode() : 0;
        result = 31 * result + (getColumn() != null ? getColumn().hashCode() : 0);
        return result;
    }
}