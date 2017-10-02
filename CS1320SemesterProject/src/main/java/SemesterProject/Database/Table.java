package SemesterProject.Database;

import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.*;
import SemesterProject.BTree.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A list of Rows representing a table, with the ability
 * to insert, update, and delete rows, or create an index on
 * a specific column
 *
 *
 * *** NOTE ***
 * The structure of the table is an ArrayList of Rows.  The table
 * has a Column[] field containing all of the tables columns, and
 * each Row is created to parallel that Column[], with each
 * DataEntry[i] in the Row assigned a Column from Column[i], which
 * is used to check the information being put in that spot (ie type,
 * unique/not null, etc) - in other words, all of the values in the
 * column at a given index in the table's Column[] will be in that
 * same index in their Row (which is a DataEntry[])
 *
 *
 * @author Yitzie Scheinman
 * @version 4.27.2017
 */

public class Table
{
    private String name;
    private ArrayList<Row> rows = new ArrayList<Row>();
    private Column[] columns;

    public Table()
    {

    }

    /**
     * A constructor for a one-cell table containing just the word true or
     * a two-cell table containing the word false and a comment
     *
     * @param trueOrFalse Which type of table to make
     */
    public Table(boolean trueOrFalse)
    {
        Row newRow;
        if(trueOrFalse) {
            //Create a one-column row containing the word true
            newRow = new Row(1);
            newRow.getEntries()[0] = new DataEntry<Boolean>(trueOrFalse);
        }
        else {
            //Create a two-column row containing the word false and a comment
            newRow = new Row(2);
            newRow.getEntries()[0] = new DataEntry<Boolean>(trueOrFalse);
            newRow.getEntries()[1] = new DataEntry<String>("referenced nonexistent table or column");
        }
        //Add the new row to the list of rows comprising the table
        rows.add(newRow);
    }

    /**
     * A full constructor that takes a CreateTable query and creates all of the columns
     * for the table
     *
     * @param query The CreateTableQuery used to construct the table
     */
    public Table(CreateTableQuery query)
    {
        name = query.getTableName();
        List<Column> columnList = new ArrayList<Column>();
        //Creates each column with the appropriate type and qualities ie Unique, Not Null, and Default Value
        for(ColumnDescription column : query.getColumnDescriptions()) {
            Column newColumn = null;
            if(column.getColumnType().equals(ColumnDescription.DataType.INT)) {
                newColumn = new Column<Integer>(column);
                if(newColumn.getHasDefault()) {
                    try {
                        newColumn.setDefaultVal(Integer.valueOf(column.getDefaultValue()));
                    }
                    catch(NumberFormatException nfe) {
                        throw new IllegalArgumentException("The default value " + column.getDefaultValue() + " is not of type INT");
                    }
                }
            }
            else if(column.getColumnType().equals(ColumnDescription.DataType.VARCHAR)) {
                newColumn = new Column<String>(column);
                if(newColumn.getHasDefault()) {
                    if(column.getDefaultValue().startsWith("'") && column.getDefaultValue().endsWith("'")) {
                        newColumn.setDefaultVal(column.getDefaultValue().substring(1, column.getDefaultValue().length() - 1));
                    }
                    else {
                        throw new IllegalArgumentException("The default value " + column.getDefaultValue() + " is not of type VARCHAR");
                    }
                }
            }
            else if(column.getColumnType().equals(ColumnDescription.DataType.DECIMAL)) {
                newColumn = new Column<Double>(column);
                if(newColumn.getHasDefault()) {
                    try {
                        newColumn.setDefaultVal(Double.valueOf(column.getDefaultValue()));
                    }
                    catch(NumberFormatException nfe) {
                        throw new IllegalArgumentException("The default value " + column.getDefaultValue() + " is not of type DECIMAL");
                    }
                }
            }
            else if(column.getColumnType().equals(ColumnDescription.DataType.BOOLEAN)) {
                newColumn = new Column<Boolean>(column);
                if(newColumn.getHasDefault()) {
                    if(column.getDefaultValue().equalsIgnoreCase("true") || column.getDefaultValue().equalsIgnoreCase("false")) {
                        newColumn.setDefaultVal(Boolean.valueOf(column.getDefaultValue()));
                    }
                    else {
                        throw new IllegalArgumentException("The default value " + column.getDefaultValue() + " is not of type BOOLEAN");
                    }
                }
            }
            if(newColumn != null) {
                if(!columnList.isEmpty()) {
                    for (Column col : columnList) {
                        if (col.getName().equalsIgnoreCase(newColumn.getName())) {
                            throw new IllegalArgumentException("You cannot create multiple columns with the same name");
                        }
                    }
                }
                columnList.add(newColumn);
            }
        }
        columns = columnList.toArray(new Column[columnList.size()]);
        for(Column column : columns) {
            //Set the primary key column (automatically becomes Unique and Not Null)
            if(column.getName().equalsIgnoreCase(query.getPrimaryKeyColumn().getColumnName())) {
                column.setAsPrimaryKey();
                createIndex(column);
            }
        }
    }

    /**
     * Identifies the proper column and passes it through to the
     * private createIndex method
     *
     * @param query The CreateIndexQuery used to identify the column
     * @return Whether or not the index was successfully created
     */
    public boolean createIndex(CreateIndexQuery query)
    {
        Column columnToIndex = null;
        for(Column column : columns) {
            if(column.getName().equals(query.getColumnName())) {
                columnToIndex = column;
            }
        }
        if(columnToIndex != null) {
            if(columnToIndex.getHasIndex()) {
                throw new IllegalArgumentException("The column " + columnToIndex.getName() + " already has an index");
            }
            else {
                return createIndex(columnToIndex);
            }
        }
        return false;
    }

    /**
     * Creates an index for the given column
     *
     * @param columnToIndex The column being indexed
     * @return Whether or not the index was successfully created
     */
    private boolean createIndex(Column columnToIndex)
    {
        Integer columnNumber = null;
        for(int i = 0; i < columns.length; i++) {
            if(columnToIndex.getName().equalsIgnoreCase(columns[i].getName())) {
                columnNumber = i;
            }
        }
        if(columns[columnNumber].getHasIndex()) {
            throw new IllegalArgumentException("The column " + columns[columnNumber].getName() + " already has an index");
        }
        else {
            //Create a B-Tree that takes keys of the column type
            BTree index = null;
            if(columnToIndex.getType().equalsIgnoreCase("int")) {
                index = new BTree<Integer, Row>(Integer.MIN_VALUE);
            }
            else if(columnToIndex.getType().equalsIgnoreCase("varchar")) {
                index = new BTree<String, Row>("");
            }
            else if(columnToIndex.getType().equalsIgnoreCase("decimal")) {
                index = new BTree<Double, Row>(Double.NEGATIVE_INFINITY);
            }
            else if(columnToIndex.getType().equalsIgnoreCase("boolean")) {
                index = new BTree<Boolean, Row>(false);
            }
            if(index != null && columnNumber != null) {
                for(Row row : rows) {
                    //If the column already contains data, put it into the B-Tree
                    if(row.getEntries()[columnNumber].getData() != null) {
                        index.put(row.getEntries()[columnNumber].getData(), row);
                    }
                }
                //Assign the B-Tree to the column
                columns[columnNumber].createIndex(index);
                return true;
            }
        }
        //If the query didn't match any columns in the table, return false
        return false;
    }

    /**
     * Creates a new row and adds it to the table
     *
     * @param query The InsertQuery used to create the row
     * @return Whether or not the row was successfully created
     */
    public boolean insert(InsertQuery query)
    {
        List<String> columnNames = new ArrayList<String>();
        for(Column column : columns) {
            columnNames.add(column.getName());
        }
        //Make sure all of the columns in the query exist, otherwise return false
        for(ColumnValuePair cvp : query.getColumnValuePairs()) {
            if(!columnNames.contains(cvp.getColumnID().getColumnName())) {
                return false;
            }
        }
        //Create a new Row the size of the table's Column[]
        Row newRow = new Row(columns.length);
        //In each index of the Row, create a DataEntry of that column's type
        //and assign the column of that index to the DataEntry
        for(int i = 0; i < columns.length; i++) {
            DataEntry entry = null;
            if(columns[i].getType().equalsIgnoreCase("int")) {
                entry = new DataEntry<Integer>(columns[i]);
            }
            else if(columns[i].getType().equalsIgnoreCase("varchar")) {
                entry = new DataEntry<String>(columns[i]);
            }
            else if(columns[i].getType().equalsIgnoreCase("decimal")) {
                entry = new DataEntry<Double>(columns[i]);
            }
            else if(columns[i].getType().equalsIgnoreCase("boolean")) {
                entry = new DataEntry<Boolean>(columns[i]);
            }
            if(entry != null) {
                newRow.getEntries()[i] = entry;
            }
        }
        for(int i = 0; i < newRow.getEntries().length; i++) {
            DataEntry entry = newRow.getEntries()[i];
            //Identify the proper column for each value to be inserted and call the private
            //insert method to add that value to the DataEntry at that index
            for(ColumnValuePair cvp : query.getColumnValuePairs()) {
                if(cvp.getColumnID().getColumnName().equalsIgnoreCase(entry.getColumn().getName())) {
                    insert(cvp.getValue(), entry, i);
                }
            }
            //If any column was not specifically given a value
            if(entry.getData() == null) {
                //If that column has a default value, insert it, otherwise make the value null
                if(entry.getColumn().getHasDefault()) {
                    entry.addData(entry.getColumn().getDefaultVal());
                }
                //If the column doesn't have a default value and is not null, throw an exception
                else if(entry.getColumn().isNotNull()) {
                    throw new IllegalArgumentException("The column " + entry.getColumn().getName() + " cannot have any null values");
                }
            }
        }
        //If any column is indexed, add the new row to that column's
        //index with that column's value as the key
        for(int i = 0; i < newRow.getEntries().length; i++) {
            DataEntry entry = newRow.getEntries()[i];
            if(entry.getColumn().getHasIndex()) {
                entry.getColumn().getIndex().put(entry.getData(), newRow);
            }
        }
        //Add the newly created Row to the table
        rows.add(newRow);
        return true;
    }

    /**
     * Checks all of the relevant qualities of a value and attempts to insert
     * it into a particular DataEntry
     *
     * @param value The value being inserted
     * @param entry The DataEntry being inserted into
     * @param columnNumber The column identifier for that DataEntry
     */
    private void insert(String value, DataEntry entry, int columnNumber)
    {
        //Check the type of that DataEntry's column
        Column column = entry.getColumn();
        if(column.getType().equalsIgnoreCase("int")) {
            try {
                //Turn the String value into an Integer, if possible
                Integer val = Integer.valueOf(value);
                //If the column is Unique, check for unique values
                if(column.isUnique()) {
                    for(Row row : rows) {
                        if(row.getEntries()[columnNumber].getData().equals(val)) {
                            throw new IllegalArgumentException("The column " + column.getName() + " must have unique values");
                        }
                    }
                }
                //Add the value to the DataEntry
                entry.addData(val);
            }
            //If the value is not an int, throw an exception
            catch(NumberFormatException nfe) {
                throw new IllegalArgumentException(value + " is not of type INT");
            }
        }
        else if(column.getType().equalsIgnoreCase("varchar")) {
            //If the value is not a String (which would be given surrounded by single quotes), throw an exception
            if(value.startsWith("'") && value.endsWith("'")) {
                //If the column is Unique, check for unique values
                if(column.isUnique()) {
                    for(Row row : rows) {
                        if(row.getEntries()[columnNumber].getData().equals(value)) {
                            throw new IllegalArgumentException("The column " + column.getName() + " must have unique values");
                        }
                    }
                }
                //Check that the String value isn't too long
                if(value.length() > column.getVarcharLength()) {
                    throw new IllegalArgumentException("A string in the column " + column.getName() + " must contain " + column.getVarcharLength() + " characters or less");
                }
                //Remove the single quotes and add the value to the DataEntry
                entry.addData(value.substring(1, value.length() - 1));
            }
            else {
                throw new IllegalArgumentException(value + " is not of type VARCHAR");
            }
        }
        else if(column.getType().equalsIgnoreCase("decimal")) {
            try {
                //Turn the String value into a Double, if possible
                Double val = Double.valueOf(value);
                //If the column is Unique, check for unique values
                if(column.isUnique()) {
                    for(Row row : rows) {
                        if(row.getEntries()[columnNumber].getData().equals(val)) {
                            throw new IllegalArgumentException("The column " + column.getName() + " must have unique values");
                        }
                    }
                }
                //Using substrings (removing the first charachter if the double is negative), check that each side of the decimal point does not have too many digits
                if((val >= 0.0 && (val.toString().substring(0, val.toString().indexOf(".")).length()) > column.getWholeNumberLength()) || (val < 0.0 && (val.toString().substring(1, val.toString().indexOf(".")).length()) > column.getWholeNumberLength())) {
                    throw new IllegalArgumentException("A decimal value in the column " + column.getName() + " must contain " + column.getWholeNumberLength() + " digits or less to the left of the decimal");
                }
                if(val.toString().substring(val.toString().indexOf('.') + 1).length() > column.getFractionalLength()) {
                    throw new IllegalArgumentException("A decimal value in the column " + column.getName() + " must contain " + column.getFractionalLength() + " digits or less to the right of the decimal");
                }
                //Add the value to the DataEntry
                entry.addData(val);
            }
            //If the value is not a double, throw an exception
            catch(NumberFormatException nfe) {
                throw new IllegalArgumentException(value + " is not of type DECIMAL");
            }
        }
        else if(column.getType().equalsIgnoreCase("boolean")) {
            //If the value is not a boolean (either true or false), throw an exception
            if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                //Turn the String value into a Boolean
                Boolean val = Boolean.valueOf(value);
                //If the column is Unique, check for unique values
                if(column.isUnique()) {
                    for(Row row : rows) {
                        if(row.getEntries()[columnNumber].getData().equals(val)) {
                            throw new IllegalArgumentException("The column " + column.getName() + " must have unique values");
                        }
                    }
                }
                //Add the value to the DataEntry
                entry.addData(val);
            }
            else {
                throw new IllegalArgumentException(value + " is not of type BOOLEAN");
            }
        }
    }

    /**
     * Identifies the proper rows to update and passes them through to
     * the private update method
     *
     * @param query The UpdateQuery used to update rows
     * @return Whether or not the update was successful
     */
    public boolean update(UpdateQuery query) {
        //If there is a WHERE, create a ConditionParser
        if(query.getWhereCondition() != null) {
            Condition condition = query.getWhereCondition();
            ConditionParser parse = new ConditionParser(columns);
            //Using the List<Row> returned by the ConditionParser,
            //call the private update method
            List<Row> rowsToUpdate = parse.checkCondition(condition, rows);
            return update(query, rowsToUpdate);
        }
        //If there is no WHERE, call the private update method
        //using all of the tables rows
        else {
            return update(query, rows);
        }
    }

    /**
     * Updates a particular column's value in a given list of rows
     *
     * @param query The UpdateQuery used to update rows
     * @param rowsToUpdate The particular list of rows being updated
     * @return Whether or not the update was successful
     */
    private boolean update(UpdateQuery query, List<Row> rowsToUpdate)
    {
        for(ColumnValuePair cvp : query.getColumnValuePairs()) {
            //Identify the proper column to be updated
            Integer columnNumber = null;
            for(int i = 0; i < columns.length; i++) {
                if(columns[i].getName().equalsIgnoreCase(cvp.getColumnID().getColumnName())) {
                    columnNumber = i;
                }
            }
            //If the column to be updated doesn't exist, return false
            if(columnNumber == null) {
                return false;
            }
            else {
                //If the column is Unique, check that you are not updating them to non-unique values
                if(columns[columnNumber].isUnique()) {
                    for(Row row : rows) {
                        if(row.getEntries()[columnNumber].getData().toString().equalsIgnoreCase(cvp.getValue())) {
                            throw new IllegalArgumentException("The column " + columns[columnNumber].getName() + " must have unique values");
                        }
                    }
                    //If the update is supposed to affect more than one row, that updated value will automatically
                    //not be unique
                    if(rowsToUpdate.size() > 1) {
                        throw new IllegalArgumentException("The column " + columns[columnNumber].getName() + " must have unique values");
                    }
                }
                //Check that the new value is of the right type for its column and replace it
                //If the column is indexed, update the B-Tree
                if(columns[columnNumber].getType().equalsIgnoreCase("int")) {
                    for(Row row : rowsToUpdate) {
                        Row oldRow = row;
                        if(cvp.getValue().equalsIgnoreCase("null")) {
                            if(columns[columnNumber].isNotNull()) {
                                throw new IllegalArgumentException("The column " + columns[columnNumber].getName() + " cannot have any null values");
                            }
                            else {
                                row.getEntries()[columnNumber] = new DataEntry<Integer>(columns[columnNumber]);
                            }
                        }
                        else {
                            try {
                                row.getEntries()[columnNumber] = new DataEntry<Integer>(Integer.valueOf(cvp.getValue()));
                            }
                            catch(NumberFormatException nfe) {
                                throw new IllegalArgumentException(cvp.getValue() + " is not the right type for column " + columns[columnNumber].getName());
                            }
                        }
                        if(columns[columnNumber].getHasIndex()) {
                            //Update the B-Tree by deleting the original row and inserting the newly
                            //updated row
                            if(oldRow.getEntries()[columnNumber].getData() != null) {
                                columns[columnNumber].getIndex().delete(oldRow.getEntries()[columnNumber].getData(), oldRow);
                            }
                            columns[columnNumber].getIndex().put(row.getEntries()[columnNumber].getData(), row);
                        }
                    }
                }
                else if(columns[columnNumber].getType().equalsIgnoreCase("varchar")) {
                    if((cvp.getValue().startsWith("'") && cvp.getValue().endsWith("'")) || cvp.getValue().equalsIgnoreCase("null")) {
                        if(cvp.getValue().length() > columns[columnNumber].getVarcharLength()) {
                            throw new IllegalArgumentException("A string in the column " + columns[columnNumber].getName() + " must contain " + columns[columnNumber].getVarcharLength() + " characters or less");
                        }
                        for(Row row : rowsToUpdate) {
                            Row oldRow = row;
                            if(cvp.getValue().equalsIgnoreCase("null")) {
                                if(columns[columnNumber].isNotNull()) {
                                    throw new IllegalArgumentException("The column " + columns[columnNumber].getName() + " cannot have any null values");
                                }
                                else {
                                    row.getEntries()[columnNumber] = new DataEntry<String>(columns[columnNumber]);
                                }
                            }
                            else {
                                row.getEntries()[columnNumber] = new DataEntry<String>(cvp.getValue().substring(1, cvp.getValue().length() - 1));
                            }
                            if(columns[columnNumber].getHasIndex()) {
                                if(oldRow.getEntries()[columnNumber].getData() != null) {
                                    columns[columnNumber].getIndex().delete(oldRow.getEntries()[columnNumber].getData(), oldRow);
                                }
                                columns[columnNumber].getIndex().put(row.getEntries()[columnNumber].getData(), row);
                            }
                        }
                    }
                    else {
                        throw new IllegalArgumentException(cvp.getValue() + " is not the right type for column " + columns[columnNumber].getName());
                    }
                }
                else if(columns[columnNumber].getType().equalsIgnoreCase("decimal")) {
                    for(Row row : rowsToUpdate) {
                        Row oldRow = row;
                        if(cvp.getValue().equalsIgnoreCase("null")) {
                            if(columns[columnNumber].isNotNull()) {
                                throw new IllegalArgumentException("The column " + columns[columnNumber].getName() + " cannot have any null values");
                            }
                            else {
                                row.getEntries()[columnNumber] = new DataEntry<Double>(columns[columnNumber]);
                            }
                        }
                        else {
                            try {
                                Double val = Double.valueOf(cvp.getValue());
                                if((val >= 0.0 && (val.toString().substring(0, val.toString().indexOf(".")).length()) > columns[columnNumber].getWholeNumberLength()) || (val < 0.0 && (val.toString().substring(1, val.toString().indexOf(".")).length()) > columns[columnNumber].getWholeNumberLength())) {
                                    throw new IllegalArgumentException("A decimal value in the column " + columns[columnNumber].getName() + " must contain " + columns[columnNumber].getWholeNumberLength() + " digits or less to the left of the decimal");
                                }
                                if(val.toString().substring(val.toString().indexOf('.') + 1).length() > columns[columnNumber].getFractionalLength()) {
                                    throw new IllegalArgumentException("A decimal value in the column " + columns[columnNumber].getName() + " must contain " + columns[columnNumber].getFractionalLength() + " digits or less to the right of the decimal");
                                }
                                row.getEntries()[columnNumber] = new DataEntry<Double>(val);
                            }
                            catch(NumberFormatException nfe) {
                                throw new IllegalArgumentException(cvp.getValue() + " is not of type DECIMAL");
                            }
                        }
                        if(columns[columnNumber].getHasIndex()) {
                            if(oldRow.getEntries()[columnNumber].getData() != null) {
                                columns[columnNumber].getIndex().delete(oldRow.getEntries()[columnNumber].getData(), oldRow);
                            }
                            columns[columnNumber].getIndex().put(row.getEntries()[columnNumber].getData(), row);
                        }
                    }
                }
                else if(columns[columnNumber].getType().equalsIgnoreCase("boolean")) {
                    if(cvp.getValue().equalsIgnoreCase("true") || cvp.getValue().equalsIgnoreCase("false") || cvp.getValue().equalsIgnoreCase("null")) {
                        for(Row row : rowsToUpdate) {
                            Row oldRow = row;
                            if(cvp.getValue().equalsIgnoreCase("null")) {
                                if(columns[columnNumber].isNotNull()) {
                                    throw new IllegalArgumentException("The column " + columns[columnNumber].getName() + " cannot have any null values");
                                }
                                else {
                                    row.getEntries()[columnNumber] = new DataEntry<Boolean>(columns[columnNumber]);
                                }
                            }
                            else {
                                row.getEntries()[columnNumber] = new DataEntry<Boolean>(Boolean.valueOf(cvp.getValue()));
                            }
                            if(columns[columnNumber].getHasIndex()) {
                                if(oldRow.getEntries()[columnNumber].getData() != null) {
                                    columns[columnNumber].getIndex().delete(oldRow.getEntries()[columnNumber].getData(), oldRow);
                                }
                                columns[columnNumber].getIndex().put(row.getEntries()[columnNumber].getData(), row);
                            }
                        }
                    }
                    else {
                        throw new IllegalArgumentException(cvp.getValue() + " is not the right type for column " + columns[columnNumber].getName());
                    }
                }
            }
        }
        return true;
    }

    /**
     * Identifies which rows to delete and removes them from the table
     *
     * @param query The DeleteQuery used to delete rows
     * @return Whether or not the rows were successfully deleted
     */
    public boolean delete(DeleteQuery query)
    {
        //If there is a WHERE, create a ConditionParser
        if(query.getWhereCondition() != null) {
            Condition condition = query.getWhereCondition();
            ConditionParser parse = new ConditionParser(columns);
            //Identify the rows fulfilling the condition
            List<Row> rowsToDelete = parse.checkCondition(condition, rows);
            for(int i = 0; i < columns.length; i++) {
                //If any column has an index, remove this row from it
                if(columns[i].getHasIndex()) {
                    for(Row row : rowsToDelete) {
                        columns[i].getIndex().delete(row.getEntries()[i].getData(), row);
                    }
                }
            }
            //Remove the appropriate rows from the Table
            rows.removeAll(rowsToDelete);
        }
        //If there's no WHERE
        else {
            for(int i = 0; i < columns.length; i++) {
                //If any column has an index, remove this row from it
                if(columns[i].getHasIndex()) {
                    for(Row row : rows) {
                        columns[i].getIndex().delete(row.getEntries()[i].getData(), row);
                    }
                }
            }
            rows = new ArrayList<Row>();
        }
        return true;
    }

    /**
     * @return The name of the table
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @return The list of rows comprising the table
     */
    public ArrayList<Row> getRows()
    {
        return this.rows;
    }

    /**
     * @param row The row to be added to the table
     */
    public void addRow(Row row)
    {
        rows.add(row);
    }

    /**
     * @return The array of columns comprising the table
     */
    public Column[] getColumns()
    {
        return this.columns;
    }

    /**
     * @param columns The array of columns to be assigned to the table
     */
    public void setColumns(Column[] columns)
    {
        this.columns = columns;
    }
}