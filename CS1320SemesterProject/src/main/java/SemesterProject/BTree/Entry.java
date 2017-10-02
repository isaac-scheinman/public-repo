package SemesterProject.BTree;

import java.util.ArrayList;
import java.util.List;

/**
 * An Entry class, which creates an Entry object
 * with a given key and value to store in a B-Tree
 *
 * @author Yitzie Scheinman
 * @version 4.7.2017
 */

public class Entry<Key extends Comparable<Key>, Value>
{
    private Key key;
    private Value value;
    private List<Value> values = new ArrayList<Value>();

    public Entry(Key key, Value val, boolean isExternal)
    {
        //If the Entry is in an external node, create an array of values
        //to allow for storage of multiple values in the same key
        this.key = key;
        if(isExternal) {
            if(val != null) {
                values.add(val);
            }
        }
        //If the Entry is in an internal node, leave its value as one
        //to accommodate the storage of a child node as a value
        else {
            this.value = val;
        }
    }

    /**
     * @return The key of the Entry
     */
    public Key getKey()
    {
        return this.key;
    }

    /**
     * @return The single value of the Entry
     */
    public Value getValue()
    {
        return value;
    }

    /**
     * @return The array of values of the Entry
     */
    public List<Value> getValues()
    {
        return values;
    }

    /**
     * For an Entry in an external node, add another
     * value for the Entry's key
     *
     * @param val The additional value
     */
    public void addValue(Value val)
    {
        values.add(val);
    }

    /**
     * Remove all of the values from the Entry
     */
    public void removeValues()
    {
        values = new ArrayList<Value>();
    }
}