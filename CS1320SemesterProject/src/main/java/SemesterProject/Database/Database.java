package SemesterProject.Database;

import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.*;
import net.sf.jsqlparser.JSQLParserException;
import java.util.List;
import java.util.ArrayList;

/**
 * Takes an SQL Query String and executes the proper command
 *
 *
 * *** NOTE ***
 * Only returns a false ResultSet if query attempts
 * to reference a nonexistent table or column - any other mistake
 * in the query throws an IllegalArgumentException with an
 * explanatory message
 *
 *
 * @author Yitzie Scheinman
 * @version 4.25.2017
 */

public class Database
{
    private SQLParser parser = new SQLParser();
    private List<Table> tables = new ArrayList<Table>();

    public Database()
    {
        
    }

    /**
     * Takes any SQL Query String and identifies and then calls the proper command
     *
     * @param SQL The SQL Query
     * @return A ResultSet for the query
     * @throws JSQLParserException
     */
    public ResultSet execute(String SQL) throws JSQLParserException
    {
        SQLQuery query = parser.parse(SQL);
        if(query instanceof CreateTableQuery) {
            return createTable((CreateTableQuery)query);
        }
        else if(query instanceof CreateIndexQuery) {
            return createIndex((CreateIndexQuery)query);
        }
        else if(query instanceof SelectQuery) {
            return select((SelectQuery)query);
        }
        else if(query instanceof InsertQuery) {
            return insert((InsertQuery)query);
        }
        else if(query instanceof UpdateQuery) {
            return update((UpdateQuery)query);
        }
        else if(query instanceof DeleteQuery) {
            return delete((DeleteQuery)query);
        }
        return null;
    }

    /**
     * Asserts that this is a new table and creates it
     *
     * @param query The CreateTableQuery used to construct the table
     * @return A ResultSet containing the empty new table
     */
    private ResultSet createTable(CreateTableQuery query)
    {
        for(Table table : tables) {
            if(table.getName().equalsIgnoreCase(query.getTableName())) {
                throw new IllegalArgumentException("A table with that name already exists");
            }
        }
        Table newTable = new Table(query);
        //Keep track of tables in DB
        tables.add(newTable);
        //Wrap new table in a ResultSet
        return new ResultSet(newTable);
    }

    /**
     * Identifies the proper table and tells that table to create the index
     *
     * @param query The CreateIndexQuery to pass through to the table
     * @return A ResultSet containing a one- or two-cell table stating whether or not the query was successful
     */
    private ResultSet createIndex(CreateIndexQuery query)
    {
        Table tableToIndex = null;
        boolean indexed = false;
        for(Table table : tables) {
            if(table.getName().equalsIgnoreCase(query.getTableName())) {
                tableToIndex = table;
            }
        }
        if(tableToIndex != null) {
            indexed = tableToIndex.createIndex(query);
        }
        //Create a trueOrFalse table of one or two cells
        Table newTable;
        if(indexed) {
            newTable = new Table(true);
        }
        else {
            newTable = new Table(false);
        }
        //Wrap trueOrFalse table in a ResultSet
        return new ResultSet(newTable);
    }

    /**
     * Identifies the proper table and creates a SelectionHandler to make the selection
     *
     * @param query The SelectQuery to pass through to the SelectionHandler
     * @return A ResultSet containing a table of the rows and colums selected by the query
     */
    private ResultSet select(SelectQuery query)
    {
        Table tableForSelect = null;
        for(Table table : tables) {
            if(table.getName().equalsIgnoreCase(query.getFromTableNames()[0])) {
                tableForSelect = table;
            }
        }
        if(tableForSelect == null) {
            throw new IllegalArgumentException("There is no table with that name");
        }
        else {
            //Copy this table to a SelectionHandler object
            SelectionHandler handler = new SelectionHandler(tableForSelect);
            //Wrap table of selections in a ResultSet
            return new ResultSet(handler.select(query));
        }
    }

    /**
     * Identifies the proper table and tells that table to insert the row
     *
     * @param query The InsertQuery to pass through to the table
     * @return A ResultSet containing a one- or two-cell table stating whether or not the query was successful
     */
    private ResultSet insert(InsertQuery query)
    {
        Table tableForInsert = null;
        boolean inserted = false;
        for(Table table : tables) {
            if(table.getName().equalsIgnoreCase(query.getTableName())) {
                tableForInsert = table;
            }
        }
        if(tableForInsert != null) {
            inserted = tableForInsert.insert(query);
        }
        //Create a trueOrFalse table of one or two cells
        Table newTable;
        if(inserted) {
            newTable = new Table(true);
        }
        else {
            newTable = new Table(false);
        }
        //Wrap trueOrFalse table in a ResultSet
        return new ResultSet(newTable);
    }

    /**
     * Identifies the proper table and tells that table to update the proper rows
     *
     * @param query The UpdateQuery to pass through to the table
     * @return A ResultSet containing a one- or two-cell table stating whether or not the query was successful
     */
    private ResultSet update(UpdateQuery query)
    {
        Table tableForUpdate = null;
        boolean updated = false;
        for(Table table : tables) {
            if(table.getName().equalsIgnoreCase(query.getTableName())) {
                tableForUpdate = table;
            }
        }
        if(tableForUpdate != null) {
            updated = tableForUpdate.update(query);
        }
        //Create a trueOrFalse table of one or two cells
        Table newTable;
        if(updated) {
            newTable = new Table(true);
        }
        else {
            newTable = new Table(false);
        }
        //Wrap trueOrFalse table in a ResultSet
        return new ResultSet(newTable);
    }

    /**
     * Identifies the proper table and tells that table to delete the proper rows
     *
     * @param query The DeleteQuery to pass through to the table
     * @return A ResultSet containing a one- or two-cell table stating whether or not the query was successful
     */
    private ResultSet delete(DeleteQuery query)
    {
        Table tableForDelete = null;
        boolean deleted = false;
        for(Table table : tables) {
            if(table.getName().equalsIgnoreCase(query.getTableName())) {
                tableForDelete = table;
            }
        }
        if(tableForDelete != null) {
            deleted = tableForDelete.delete(query);
        }
        //Create a trueOrFalse table of one or two cells
        Table newTable;
        if(deleted) {
            newTable = new Table(true);
        }
        else {
            newTable = new Table(false);
        }
        //Wrap trueOrFalse table in a ResultSet
        return new ResultSet(newTable);
    }
}