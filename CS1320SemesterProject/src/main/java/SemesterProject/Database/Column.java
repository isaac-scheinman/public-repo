package SemesterProject.Database;

import SemesterProject.BTree.*;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.*;

/**
 * Keeps track of the qualities of a given column in a table
 *
 * @author Yitzie Scheinman
 * @version 4.30.2017
 */

public class Column<Type extends Comparable<Type>>
{
    private String name;
    private String type;
    private boolean unique = false;
    private boolean notNull = false;
    private boolean hasDefault = false;
    private Type defaultVal;
    private int wholeNumberLength;
    private int fractionalLength;
    private int varcharLength;
    private boolean hasIndex = false;
    private BTree<Type, Row> index;

    public Column(ColumnDescription column)
    {
        name = column.getColumnName();
        type = column.getColumnType().toString().toLowerCase();
        unique = column.isUnique();
        notNull = column.isNotNull();
        hasDefault = column.getHasDefault();
        wholeNumberLength = column.getWholeNumberLength();
        fractionalLength = column.getFractionLength();
        varcharLength = column.getVarCharLength();
    }

    public Column(String name, String type)
    {
        this.name = name;
        this.type = type;
    }

    /**
     * @return The name of the column
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @return A String of the data type of the column
     */
    public String getType()
    {
        return this.type;
    }

    /**
     * @return Whether or not the column needs unique values
     */
    public boolean isUnique()
    {
        return this.unique;
    }

    /**
     * @return Whether or not the column can contain null values
     */
    public boolean isNotNull()
    {
        return this.notNull;
    }

    /**
     * @return Whether or not the column has a default value
     */
    public boolean getHasDefault()
    {
        return this.hasDefault;
    }

    /**
     * @return The default value of the column
     */
    public Type getDefaultVal()
    {
        return this.defaultVal;
    }

    /**
     * @param val The default value of the column
     */
    public void setDefaultVal(Type val)
    {
        this.defaultVal = val;
    }

    /**
     * @return The maximum amount of digits before a decimal, if the column is of type DECIMAL
     */
    public int getWholeNumberLength()
    {
        return this.wholeNumberLength;
    }

    /**
     * @return The maximum amount of digits after a decimal, if the column is of type DECIMAL
     */
    public int getFractionalLength()
    {
        return this.fractionalLength;
    }

    /**
     * @return The maximum amount of characters, if the column is of type VARCHAR
     */
    public int getVarcharLength()
    {
        return this.varcharLength;
    }

    /**
     * @return Whether or not the column has an index
     */
    public boolean getHasIndex()
    {
        return this.hasIndex;
    }

    /**
     * @return The columns index (B-Tree)
     */
    public BTree getIndex()
    {
        return this.index;
    }

    /**
     * Assigns the column an index
     *
     * @param index The created index
     */
    public void createIndex(BTree index)
    {
        hasIndex = true;
        this.index = index;
    }

    /**
     * Make the column the Primary Key column, which must be Unique and Not Null
     */
    public void setAsPrimaryKey()
    {
        unique = true;
        notNull = true;
        if(hasDefault) {
            throw new IllegalArgumentException("A primary key column cannot have a default value");
        }
    }
}