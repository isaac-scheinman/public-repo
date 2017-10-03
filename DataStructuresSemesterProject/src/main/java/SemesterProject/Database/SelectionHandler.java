package SemesterProject.Database;

import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.*;
import java.util.*;

/**
 * Takes a SelectQuery and a particular table, and formats a new
 * table of the selected rows and columns from the original table
 *
 * @author Yitzie Scheinman
 * @version 5.15.2017
 */

public class SelectionHandler
{
    private ArrayList<Row> rows;
    private Column[] columns;

    public SelectionHandler(Table table)
    {
        rows = table.getRows();
        columns = table.getColumns();
    }

    /**
     * Analyze the SelectQuery and select the appropriate
     * columns and rows from the table
     *
     * @param query The SelectQuery used to make the selections
     * @return A table of the selected rows and columns
     */
    public Table select(SelectQuery query)
    {
        //Create a new table to hold the select results
        Table selectResult = new Table();
        //If it is a SELECT * query, add all columns
        if(query.getSelectedColumnNames().length == 1 && query.getSelectedColumnNames()[0].getColumnName().equals("*")) {
            selectResult.setColumns(columns);
        }
        //If it is not a SELECT * query, add the selected columns
        else {
            List<Column> columnList = new ArrayList<Column>();
            List<String> columnListNames = new ArrayList<String>();
            for(ColumnID column : query.getSelectedColumnNames()) {
                for(int i = 0; i < columns.length; i++) {
                    if(columns[i].getName().equalsIgnoreCase(column.getColumnName())) {
                        columnList.add(columns[i]);
                        columnListNames.add(columns[i].getName());
                    }
                }
                if(!columnListNames.contains(column.getColumnName())) {
                    throw new IllegalArgumentException("There is no column with the name " + column.getColumnName() + " in that table");
                }
            }
            selectResult.setColumns(columnList.toArray(new Column[columnList.size()]));
        }
        //If there is a WHERE condition, create a ConditionParser
        //to return only the rows that fit the condition(s)
        if(query.getWhereCondition() != null) {
            ConditionParser parse = new ConditionParser(columns);
            rows = (ArrayList<Row>)parse.checkCondition(query.getWhereCondition(), rows);
        }
        //Check if the query is selecting functions or columns
        //(and ensure that it isn't selecting both)
        if(!query.getFunctionMap().isEmpty()) {
            for(ColumnID column : query.getSelectedColumnNames()) {
                if(!query.getFunctionMap().containsKey(column)) {
                    throw new IllegalArgumentException("You cannot select columns and functions in the same query");
                }
            }
            //If the query is selecting functions
            List<Object> functionSelections = new ArrayList<Object>();
            for(ColumnID column : query.getFunctionMap().keySet()) {
                Integer columnNumber = null;
                for(int i = 0; i < columns.length; i++) {
                    if(columns[i].getName().equalsIgnoreCase(column.getColumnName())) {
                        columnNumber = i;
                    }
                }
                //Collect the entries from the given column to perform the function
                Collection<DataEntry> entriesForFunction;
                if(query.getFunctionMap().get(column).isDistinct) {
                    //If the function is DISTINCT, use a HashSet to avoid
                    //duplicate entries
                    entriesForFunction = new HashSet<DataEntry>();
                }
                else {
                    entriesForFunction = new ArrayList<DataEntry>();
                }
                for(Row row : rows) {
                    entriesForFunction.add(row.getEntries()[columnNumber]);
                }
                //For any function other than COUNT, ensure that it is only on number columns (int or decimal)
                if(!query.getFunctionMap().get(column).function.equals(SelectQuery.FunctionName.COUNT)) {
                    if(columns[columnNumber].getType().equalsIgnoreCase("boolean") || columns[columnNumber].getType().equalsIgnoreCase("varchar")) {
                        throw new IllegalArgumentException("The function " + query.getFunctionMap().get(column).function.toString() + " does not work for a column of type " + columns[columnNumber].getType());
                    }
                }
                //Perform the appropriate function
                if(query.getFunctionMap().get(column).function.equals(SelectQuery.FunctionName.AVG)) {
                    Double total = 0.0;
                    Integer count = 0;
                    for(DataEntry entry : entriesForFunction) {
                        if(entry.getData() != null) {
                            if(columns[columnNumber].getType().equalsIgnoreCase("int")) {
                                total += (Integer)entry.getData();
                            }
                            else {
                                total += (Double)entry.getData();
                            }
                            count++;
                        }
                    }
                    if(columns[columnNumber].getType().equalsIgnoreCase("int")) {
                        functionSelections.add(total.intValue() / count);
                    }
                    else {
                        //Cuts off the decimal average at the correct amount of decimal places
                        if(((Double)(total / count)).toString().substring(((Double)(total / count)).toString().indexOf("."), ((Double)(total / count)).toString().length()).length() > columns[columnNumber].getFractionalLength()) {
                            functionSelections.add(Double.valueOf(((Double)(total / count)).toString().substring(0, ((Double)(total / count)).toString().indexOf(".") + 1 + columns[columnNumber].getFractionalLength())));
                        }
                        else {
                            functionSelections.add(total / count);
                        }
                    }
                }
                else if(query.getFunctionMap().get(column).function.equals(SelectQuery.FunctionName.COUNT)) {
                    Integer count = 0;
                    for(DataEntry entry : entriesForFunction) {
                        if(entry.getData() != null) {
                            count++;
                        }
                    }
                    functionSelections.add(count);
                }
                else if(query.getFunctionMap().get(column).function.equals(SelectQuery.FunctionName.MAX)) {
                    //Use index's getMaxKey() method, if index exists
                    if(columns[columnNumber].getHasIndex()) {
                        functionSelections.add(columns[columnNumber].getIndex().getMaxKey());
                    }
                    else {
                        Comparable max = ((DataEntry)entriesForFunction.toArray()[0]).getData();
                        for(DataEntry entry : entriesForFunction) {
                            if(entry.getData() != null) {
                                if(entry.getData().compareTo(max) > 0) {
                                    max = entry.getData();
                                }
                            }
                        }
                        functionSelections.add(max);
                    }
                }
                else if(query.getFunctionMap().get(column).function.equals(SelectQuery.FunctionName.MIN)) {
                    //Use index's getMinKey() method, if index exists
                    if(columns[columnNumber].getHasIndex()) {
                        functionSelections.add(columns[columnNumber].getIndex().getMinKey());
                    }
                    else {
                        Comparable min = ((DataEntry)entriesForFunction.toArray()[0]).getData();
                        for(DataEntry entry : entriesForFunction) {
                            if(entry.getData() != null) {
                                if(entry.getData().compareTo(min) < 0) {
                                    min = entry.getData();
                                }
                            }
                        }
                        functionSelections.add(min);
                    }
                }
                else if(query.getFunctionMap().get(column).function.equals(SelectQuery.FunctionName.SUM)) {
                    Double total = 0.0;
                    for(DataEntry entry : entriesForFunction) {
                        if(entry.getData() != null) {
                            if(columns[columnNumber].getType().equalsIgnoreCase("int")) {
                                total += (Integer)entry.getData();
                            }
                            else {
                                total += (Double)entry.getData();
                            }
                        }
                    }
                    if(columns[columnNumber].getType().equalsIgnoreCase("int")) {
                        functionSelections.add(total.intValue());
                    }
                    else {
                        functionSelections.add(total);
                    }
                }
                //Change Column "names" and "types" for ResultSet
                //ie Name(Type) = COUNT(col1)
                for(int i = 0; i < selectResult.getColumns().length; i++) {
                    if(selectResult.getColumns()[i].getName().equalsIgnoreCase(column.getColumnName())) {
                        selectResult.getColumns()[i] = new Column(query.getFunctionMap().get(column).function.toString(), column.getColumnName());
                    }
                }
            }
            //Create a single row of just the completed functions for
            //the selection result
            Row newRow = new Row(functionSelections.size());
            for(int i = 0; i < functionSelections.size(); i++) {
                if(functionSelections.toArray()[i] instanceof Integer) {
                    newRow.getEntries()[i] = new DataEntry<Integer>((Integer)functionSelections.toArray()[i]);
                }
                else {
                    newRow.getEntries()[i] = new DataEntry<Double>((Double)functionSelections.toArray()[i]);
                }
            }
            selectResult.addRow(newRow);
        }
        else {
            //If the query is selecting columns
            Collection<Row> selections;
            if(query.isDistinct()) {
                //If the function is DISTINCT, use a HashSet to avoid
                //duplicate entries
                selections = new HashSet<Row>();
            }
            else {
                selections = new ArrayList<Row>();
            }
            //If it's not a SELECT * query, add only the data from chosen
            //columns for each selected row
            if(!(query.getSelectedColumnNames().length == 1 && query.getSelectedColumnNames()[0].getColumnName().equals("*"))) {
                for(Row row : rows) {
                    Row newRow = new Row(selectResult.getColumns().length);
                    for(int i = 0; i < query.getSelectedColumnNames().length; i++) {
                        Integer columnNumber = null;
                        for(int j = 0; j < columns.length; j++) {
                            if(columns[j].getName().equalsIgnoreCase(query.getSelectedColumnNames()[i].getColumnName())) {
                                columnNumber = j;
                            }
                        }
                        newRow.getEntries()[i] = row.getEntries()[columnNumber];
                    }
                    selections.add(newRow);
                }
            }
            //If it's a SELECT * query, add the entire row
            //for each selected row
            else {
                for(Row row : rows) {
                    selections.add(row);
                }
            }
            //If the query contains order bys, pass through the selected
            //rows and columns to the order method, which gives the rows
            //back in the appropriate order
            if(!Arrays.asList(query.getOrderBys()).isEmpty()) {
                for(Row row : order(selections, selectResult.getColumns(), query.getOrderBys())) {
                    selectResult.addRow(row);
                }
            }
            else {
                for(Row row : selections) {
                    selectResult.addRow(row);
                }
            }
        }
        return selectResult;
    }

    /**
     * Takes a list of rows and an array of columns and sorts the rows based
     * on an arbitrary amount of order bys on the columns (ascending or descending)
     *
     * @param rows The list of rows to be sorted
     * @param columns The columns being used to sort them
     * @param orderBys The order by commands dictating the sort
     * @return The list of rows in the appropriate order
     */
    private Collection<Row> order(Collection<Row> rows, Column[] columns, SelectQuery.OrderBy[] orderBys)
    {
        //Only need to apply the order by(s) if there
        //is more than one selected row
        if(rows.size() > 1) {
            Row[] rowArray = rows.toArray(new Row[rows.size()]);
            Integer columnNumber = null;
            //Keep track of the last column that was ordered, in
            //order to support order bys within order bys
            Integer prevColumnNumber = null;
            for(int i = 0; i < orderBys.length; i++) {
                for(int j = 0; j < columns.length; j++) {
                    if(columns[j].getName().equalsIgnoreCase(orderBys[i].getColumnID().getColumnName())) {
                        prevColumnNumber = columnNumber;
                        columnNumber = j;
                    }
                }
                if(columnNumber == null) {
                    throw new IllegalArgumentException("The column " + orderBys[i].getColumnID().getColumnName() + " is not in the select query");
                }
                else {
                    if(i == 0) {
                        //If it's the first order by, do an insertion sort on the
                        //entire list of rows, either ascending or descending
                        //Any null values will be automatically sorted to the beginning
                        for(int j = 1; j < rowArray.length; j++) {
                            Row row = rowArray[j];
                            if(orderBys[0].isAscending()) {
                                int k;
                                for(k = j; k > 0 && rowArray[k - 1].getEntries()[columnNumber].getData() != null && (row.getEntries()[columnNumber].getData() == null || (rowArray[k - 1].getEntries()[columnNumber].getData().compareTo(row.getEntries()[columnNumber].getData()) > 0)); k--) {
                                    rowArray[k] = rowArray[k - 1];
                                }
                                rowArray[k] = row;
                            }
                            else {
                                int k;
                                for(k = j; k > 0 && rowArray[k - 1].getEntries()[columnNumber].getData() != null && (row.getEntries()[columnNumber].getData() == null || rowArray[k - 1].getEntries()[columnNumber].getData().compareTo(row.getEntries()[columnNumber].getData()) < 0); k--) {
                                    rowArray[k] = rowArray[k - 1];
                                }
                                rowArray[k] = row;
                            }
                        }
                    }
                    else {
                        //If it's a subsequent order by, only do the sort within
                        //any rows with the same value in the last sorted column
                        for(int j = 1; j < rowArray.length; j++) {
                            Row row = rowArray[j];
                            if((row.getEntries()[prevColumnNumber].getData() == null && rowArray[j - 1].getEntries()[prevColumnNumber].getData() == null) || (rowArray[j - 1].getEntries()[prevColumnNumber].getData() != null && row.getEntries()[prevColumnNumber].getData().compareTo(rowArray[j - 1].getEntries()[prevColumnNumber].getData()) == 0)) {
                                if(orderBys[i].isAscending()) {
                                    int k;
                                    for(k = j; k > 0 && ((row.getEntries()[prevColumnNumber].getData() == null && rowArray[j - 1].getEntries()[prevColumnNumber].getData() == null) || rowArray[k].getEntries()[prevColumnNumber].getData().compareTo(rowArray[k - 1].getEntries()[prevColumnNumber].getData()) == 0) && rowArray[k - 1].getEntries()[columnNumber].getData() != null && (row.getEntries()[columnNumber].getData() == null || rowArray[k - 1].getEntries()[columnNumber].getData().compareTo(row.getEntries()[columnNumber].getData()) > 0); k--) {
                                        rowArray[k] = rowArray[k - 1];
                                    }
                                    rowArray[k] = row;
                                }
                                else {
                                    int k;
                                    for(k = j; k > 0 && ((row.getEntries()[prevColumnNumber].getData() == null && rowArray[j - 1].getEntries()[prevColumnNumber].getData() == null) || rowArray[k].getEntries()[prevColumnNumber].getData().compareTo(rowArray[k - 1].getEntries()[prevColumnNumber].getData()) == 0) && rowArray[k - 1].getEntries()[columnNumber].getData() != null && (row.getEntries()[columnNumber].getData() == null || rowArray[k - 1].getEntries()[columnNumber].getData().compareTo(row.getEntries()[columnNumber].getData()) < 0); k--) {
                                        rowArray[k] = rowArray[k - 1];
                                    }
                                    rowArray[k] = row;
                                }
                            }
                        }
                    }
                }
            }
            return new ArrayList<Row>(Arrays.asList(rowArray));
        }
        //If there is only one selected row, return that row
        //without having to sort (obviously)
        return rows;
    }
}