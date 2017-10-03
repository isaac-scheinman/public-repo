package SemesterProject.Database;

import java.util.ArrayList;

/**
 * Wraps a table and returns it with only the ability
 * to get its rows and columns, as well as a toString()
 * method for printing
 *
 * @author Yitzie Scheinman
 * @version 4.26.2017
 */

public class ResultSet
{
    private Table table;

    public ResultSet(Table table)
    {
        this.table = table;
    }

    /**
     * @return The table's columns
     */
    public Column[] getColumns()
    {
        return table.getColumns();
    }

    /**
     * @return The table's rows
     */
    public ArrayList<Row> getRows()
    {
        return table.getRows();
    }

    @Override
    public String toString()
    {
        String returnString = "";
        if(getColumns() != null) {
            returnString += "Columns: ";
            for(int i = 0; i < getColumns().length; i++) {
                returnString += getColumns()[i].getName() + "(" + getColumns()[i].getType().toString() + ")";
                if(i != getColumns().length - 1) {
                    returnString += ", ";
                }
                else {
                    returnString += "\n";
                }
            }
        }
        if(!getRows().isEmpty()) {
            for(Row row : getRows()) {
                returnString += "Row: ";
                for(int i = 0; i < row.getEntries().length; i++) {
                    returnString += row.getEntries()[i].getData();
                    if(i != row.getEntries().length - 1) {
                        returnString += ", ";
                    }
                    else {
                        returnString += "\n";
                    }
                }
            }
        }
        else {
            returnString += "No rows\n";
        }
        return returnString;
    }
}