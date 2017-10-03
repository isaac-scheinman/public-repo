package SemesterProject.BTree;

/**
 * A Node class, which creates a Node object with a
 * specific height, entry count, and an array of Entry
 * objects as part of a B-Tree
 *
 * @author Yitzie Scheinman
 * @version 4.7.2017
 */

public class Node
{
    private int height;
    private Entry[] entry;
    private int entryCount;
    private Node nextNode = null;
    private Node previousNode = null;

    public Node(int height, int nodeSize, int entryCount)
    {
        this.height = height;
        entry = new Entry[nodeSize];
        this.entryCount = entryCount;
    }

    /**
     * @return The height of the Node
     */
    public int getHeight()
    {
        return this.height;
    }

    /**
     * @return The array of the Node's entries
     */
    public Entry[] getEntries()
    {
        return entry;
    }

    /**
     * @return The number of entries in the Node
     */
    public int getEntryCount()
    {
        return this.entryCount;
    }

    /**
     * Increase the entryCount by 1
     */
    public void incrementEntryCount()
    {
        this.entryCount++;
    }

    /**
     * Set the number of visible entries in the Node
     *
     * @param newCount The number of entries
     */
    public void setEntryCount(int newCount)
    {
        this.entryCount = newCount;
    }

    /**
     * @return The next leaf node
     */
    public Node getNextNode()
    {
        return this.nextNode;
    }

    /**
     * Set a reference to the next leaf node
     *
     * @param nextNode The next leaf node
     */
    public void setNextNode(Node nextNode)
    {
        this.nextNode = nextNode;
    }

    /**
     * @return The previous leaf node
     */
    public Node getPreviousNode()
    {
        return this.previousNode;
    }

    /**
     * Set a reference to the previous leaf node
     *
     * @param previousNode The previous leaf node
     */
    public void setPreviousNode(Node previousNode)
    {
        this.previousNode = previousNode;
    }
}