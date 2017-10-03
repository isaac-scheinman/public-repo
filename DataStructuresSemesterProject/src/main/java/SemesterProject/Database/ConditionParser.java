package SemesterProject.Database;

import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Takes a table's columns, a list of rows, and a possibly complex
 * condition, and check the condition(s) on those rows, returning only
 * the rows that fit the condition(s)
 *
 * @author Yitzie Scheinman
 * @version 4.15.2017
 */

public class ConditionParser
{
    private Column[] columns;

    public ConditionParser(Column[] columns)
    {
        this.columns = columns;
    }

    /**
     * Takes a list of rows and a condition, and checks the condition
     * on those rows
     *
     * @param condition The condition to be checked
     * @param rows The rows to be checked
     * @return The rows that fit the condition
     */
    public List<Row> checkCondition(Condition condition, List<Row> rows)
    {
        List<Row> rowsWithCondition = new ArrayList<Row>();
        //If condition contains only one condition, check that condition
        if(!(condition.getLeftOperand() instanceof Condition)) {
            //Left Operand = column to check
            Integer columnToCheck = null;
            for(int i = 0; i < columns.length; i++) {
                if(condition.getLeftOperand() instanceof ColumnID) {
                    if(columns[i].getName().equalsIgnoreCase(((ColumnID)condition.getLeftOperand()).getColumnName())) {
                        columnToCheck = i;
                    }
                }
                else {
                    if(columns[i].getName().equalsIgnoreCase((String)condition.getLeftOperand())) {
                        columnToCheck = i;
                    }
                }
            }
            if(columnToCheck == null) {
                throw new IllegalArgumentException("Your condition '" + condition + "' is invalid");
            }
            //Right Operand = value to check for
            else {
                //Check that the right Operand is the correct type for the column
                Object rightOperand = null;
                //The right Operand could be null
                if(!((String)condition.getRightOperand()).equalsIgnoreCase("null")) {
                    if(columns[columnToCheck].getType().equalsIgnoreCase("int")) {
                        try {
                            rightOperand = Integer.valueOf((String)condition.getRightOperand());
                        }
                        catch(NumberFormatException nfe) {
                            throw new IllegalArgumentException(condition.getRightOperand() + " is not the correct type for column " + columns[columnToCheck].getName());
                        }
                    }
                    else if(columns[columnToCheck].getType().equalsIgnoreCase("varchar")) {
                        if(((String)condition.getRightOperand()).startsWith("'") && ((String)condition.getRightOperand()).endsWith("'")) {
                            rightOperand = ((String)condition.getRightOperand()).substring(1, ((String)condition.getRightOperand()).length() - 1);
                        }
                        else {
                            throw new IllegalArgumentException(condition.getRightOperand() + " is not the correct type for column " + columns[columnToCheck].getName());
                        }
                    }
                    else if(columns[columnToCheck].getType().equalsIgnoreCase("decimal")) {
                        try {
                            rightOperand = Double.valueOf((String)condition.getRightOperand());
                        }
                        catch(NumberFormatException nfe) {
                            throw new IllegalArgumentException(condition.getRightOperand() + " is not the correct type for column " + columns[columnToCheck].getName());
                        }
                    }
                    else if(columns[columnToCheck].getType().equalsIgnoreCase("boolean")) {
                        if((((String)condition.getRightOperand()).equalsIgnoreCase("true")) || (((String)condition.getRightOperand()).equalsIgnoreCase("false"))) {
                            rightOperand = Boolean.valueOf((String) condition.getRightOperand());
                        }
                        else {
                            throw new IllegalArgumentException(condition.getRightOperand() + " is not the correct type for column " + columns[columnToCheck].getName());
                        }
                    }
                }
                //Check the appropriate operator
                if(condition.getOperator().equals(Condition.Operator.EQUALS)) {
                    if(rightOperand != null) {
                        //If the column has an index, use the index's get method
                        if(columns[columnToCheck].getHasIndex()) {
                            rowsWithCondition.addAll(columns[columnToCheck].getIndex().get((Comparable) rightOperand));
                            rowsWithCondition.retainAll(rows);
                        }
                        else {
                            for(Row row : rows) {
                                if(row.getEntries()[columnToCheck].getData() != null && row.getEntries()[columnToCheck].getData().equals(rightOperand)) {
                                    rowsWithCondition.add(row);
                                }
                            }
                        }
                    }
                    //If condition is checking for null values
                    //Don't use index
                    else {
                        for(Row row : rows) {
                            if(row.getEntries()[columnToCheck].getData() == null) {
                                rowsWithCondition.add(row);
                            }
                        }
                    }
                }
                else if(condition.getOperator().equals(Condition.Operator.NOT_EQUALS)) {
                    if(rightOperand != null) {
                        for(Row row : rows) {
                            if(row.getEntries()[columnToCheck].getData() == null || !(row.getEntries()[columnToCheck].getData().equals(rightOperand))) {
                                rowsWithCondition.add(row);
                            }
                        }
                    }
                    //If condition is checking for null values
                    else {
                        for(Row row : rows) {
                            if(row.getEntries()[columnToCheck].getData() != null) {
                                rowsWithCondition.add(row);
                            }
                        }
                    }
                }
                else if(condition.getOperator().equals(Condition.Operator.LESS_THAN)) {
                    if(rightOperand != null) {
                        //If the column has an index, use the index's getLessThan method
                        if(columns[columnToCheck].getHasIndex()) {
                            rowsWithCondition.addAll(columns[columnToCheck].getIndex().getLessThan((Comparable) rightOperand, false));
                            rowsWithCondition.retainAll(rows);
                        }
                        else {
                            for(Row row : rows) {
                                if(row.getEntries()[columnToCheck].getData() != null && row.getEntries()[columnToCheck].getData().compareTo(rightOperand) < 0) {
                                    rowsWithCondition.add(row);
                                }
                            }
                        }
                    }
                    //Operator cannot check for null values
                    else {
                        throw new IllegalArgumentException("Your condition '" + condition + "' is invalid");
                    }
                }
                else if(condition.getOperator().equals(Condition.Operator.lESS_THAN_OR_EQUALS)) {
                    if(rightOperand != null) {
                        //If the column has an index, use the index's getLessThan method
                        //with the parameter orEquals
                        if(columns[columnToCheck].getHasIndex()) {
                            rowsWithCondition.addAll(columns[columnToCheck].getIndex().getLessThan((Comparable) rightOperand, true));
                            rowsWithCondition.retainAll(rows);
                        }
                        else {
                            for(Row row : rows) {
                                if(row.getEntries()[columnToCheck].getData() != null && row.getEntries()[columnToCheck].getData().compareTo(rightOperand) <= 0) {
                                    rowsWithCondition.add(row);
                                }
                            }
                        }
                    }
                    //Operator cannot check for null values
                    else {
                        throw new IllegalArgumentException("Your condition '" + condition + "' is invalid");
                    }
                }
                else if(condition.getOperator().equals(Condition.Operator.GREATER_THAN)) {
                    if(rightOperand != null) {
                        //If the column has an index, use the index's getMoreThan method
                        if(columns[columnToCheck].getHasIndex()) {
                            rowsWithCondition.addAll(columns[columnToCheck].getIndex().getMoreThan((Comparable) rightOperand, false));
                            rowsWithCondition.retainAll(rows);
                        }
                        else {
                            for(Row row : rows) {
                                if(row.getEntries()[columnToCheck].getData() != null && row.getEntries()[columnToCheck].getData().compareTo(rightOperand) > 0) {
                                    rowsWithCondition.add(row);
                                }
                            }
                        }
                    }
                    //Operator cannot check for null values
                    else {
                        throw new IllegalArgumentException("Your condition '" + condition + "' is invalid");
                    }
                }
                else if(condition.getOperator().equals(Condition.Operator.GREATER_THAN_OR_EQUALS)) {
                    if(rightOperand != null) {
                        //If the column has an index, use the index's getMoreThan method
                        //with the parameter orEquals
                        if(columns[columnToCheck].getHasIndex()) {
                            rowsWithCondition.addAll(columns[columnToCheck].getIndex().getMoreThan((Comparable) rightOperand, true));
                            rowsWithCondition.retainAll(rows);
                        }
                        else {
                            for(Row row : rows) {
                                if(row.getEntries()[columnToCheck].getData() != null && row.getEntries()[columnToCheck].getData().compareTo(rightOperand) >= 0) {
                                    rowsWithCondition.add(row);
                                }
                            }
                        }
                    }
                    //Operator cannot check for null values
                    else {
                        throw new IllegalArgumentException("Your condition '" + condition + "' is invalid");
                    }
                }
                return rowsWithCondition;
            }
        }
        //If the condition is complex, recursively check each condition
        else {
            //If the operator between conditions is AND, check the right condition
            //only on the rows returned from checking the left condition
            if(condition.getOperator().equals(Condition.Operator.AND)) {
                rowsWithCondition.addAll(checkCondition((Condition)condition.getRightOperand(), checkCondition((Condition)condition.getLeftOperand(), rows)));
            }
            //If the operator between conditions is OR, return any rows
            //that fit either condition
            else {
                rowsWithCondition.addAll(checkCondition((Condition)condition.getLeftOperand(), rows));
                //Account for the overlap ie the rows that fit both conditions
                rowsWithCondition.removeAll(checkCondition((Condition)condition.getRightOperand(), rows));
                rowsWithCondition.addAll(checkCondition((Condition)condition.getRightOperand(), rows));
            }
            return rowsWithCondition;
        }
    }
}