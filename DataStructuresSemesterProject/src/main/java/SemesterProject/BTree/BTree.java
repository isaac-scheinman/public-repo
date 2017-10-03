package SemesterProject.BTree;

import java.util.List;
import java.util.ArrayList;

/**
 * A B-Tree implementation, which allows you to store
 * Entry objects in a B-tree using Node objects and
 * search for or delete the values of those entries
 *
 * @author Yitzie Scheinman
 * @version 4.7.2017
 */

public class BTree<Key extends Comparable<Key>, Value> {
    private static final int MAX_SIZE = 6;
    private Node root = new Node(0, MAX_SIZE, 0);
    private Node leftmost;
    private Node rightmost;

    public BTree(Key min) {
        //Create sentinel
        root.getEntries()[0] = new Entry<Key, Value>(min, null, true);
        root.setEntryCount(1);
        //Keep reference to leftmost external Node
        leftmost = root;
        //Keep reference to rightmost external Node, to be updated
        rightmost = root;
    }

    /**
     * The public get method, which searches the B-Tree
     * for a specific key using private methods and returns its value(s)
     *
     * @param key The key being searched for
     * @return The value of the searched key, if it exists
     */
    public List<Value> get(Key key) {
        //Calls private get method beginning with the root of the tree
        return get(root, key, root.getHeight());
    }

    /**
     * The private get method, which recursively locates the proper node
     * to be searched and then searches it for the given key, returning
     * its value(s)
     *
     * @param currentNode The node being searched
     * @param key         The key being searched for in the node
     * @param height      The height of the given node
     * @return The value of the searched key, if it exists
     */
    private List<Value> get(Node currentNode, Key key, int height) {
        List<Value> val = null;
        //If the node is external, search the node for the given key
        if (height == 0) {
            for (int i = 0; i < currentNode.getEntryCount(); i++) {
                if (key.compareTo((Key) currentNode.getEntries()[i].getKey()) == 0) {
                    val = (List<Value>) currentNode.getEntries()[i].getValues();
                }
            }
        }
        //If the node is internal, search the node for the proper child node
        //and recursively call get with that node
        else {
            for (int i = 0; i < currentNode.getEntryCount(); i++) {
                if ((i < currentNode.getEntryCount() - 1 && key.compareTo((Key) currentNode.getEntries()[i + 1].getKey()) < 0) || i == currentNode.getEntryCount() - 1) {
                    //Recursive call
                    val = get((Node) currentNode.getEntries()[i].getValue(), key, height - 1);
                    break;
                }
            }
        }
        return val;
    }

    /**
     * A get method which returns a list of all values
     * less than (or less than or equals to) a given key
     *
     * @param key      The key being searched for
     * @param orEquals Whether or not the key is included
     * @return The list of all lesser values
     */
    public List<Value> getLessThan(Key key, boolean orEquals) {
        List<Value> values = new ArrayList<Value>();
        Node node = leftmost;
        //Add everything until and including the key
        while (node != null && !(node.getEntries()[0].getKey().compareTo(key) > 0)) {
            for (int i = 0; i < node.getEntryCount(); i++) {
                List<Value> vals = node.getEntries()[i].getValues();
                values.addAll(vals);
                if (node.getEntries()[i].getKey().compareTo(key) == 0) {
                    break;
                }
                //If we've gone too far
                if (node.getEntries()[i].getKey().compareTo(key) > 0) {
                    values.removeAll(vals);
                    break;
                }
            }
            node = node.getNextNode();
        }
        //If the key exists is not included, remove it
        if (!orEquals) {
            if (get(key) != null) {
                values.removeAll(get(key));
            }
        }
        return values;
    }

    /**
     * A get method which returns a list of all values
     * greater than (or greater than or equal to) a given key
     *
     * @param key      The key being searched for
     * @param orEquals Whether or not the key is included
     * @return The list of all greater values
     */
    public List<Value> getMoreThan(Key key, boolean orEquals) {
        List<Value> values = new ArrayList<Value>();
        Node node = rightmost;
        //Moving backwards from the end, add everything
        //until and including the key
        while (node != null && !(node.getEntries()[node.getEntryCount() - 1].getKey().compareTo(key) < 0)) {
            for (int i = node.getEntryCount() - 1; i >= 0; i--) {
                List<Value> vals = node.getEntries()[i].getValues();
                values.addAll(vals);
                if (node.getEntries()[i].getKey().compareTo(key) == 0) {
                    break;
                }
                //If we've gone too far
                if (node.getEntries()[i].getKey().compareTo(key) < 0) {
                    values.removeAll(vals);
                    break;
                }
            }
            node = node.getPreviousNode();
        }
        //If the key exists and is not included, remove it
        if (!orEquals) {
            if (get(key) != null) {
                values.removeAll(get(key));
            }
        }
        return values;
    }

    /**
     * A get method which returns the lowest non-sentinel
     * key in the B-Tree
     *
     * @return The lowest non-sentinel key
     */
    public Key getMinKey() {
        return (Key) leftmost.getEntries()[1].getKey();
    }

    /**
     * A get method which returns the largest key in the B-Tree
     *
     * @return The largest key
     */
    public Key getMaxKey() {
        return (Key) rightmost.getEntries()[rightmost.getEntryCount() - 1].getKey();
    }

    /**
     * The public put method, which inserts a given key and
     * value as an Entry object into the B-Tree
     *
     * @param key The key of the entry being inserted
     * @param val The value of the entry being inserted
     */
    public void put(Key key, Value val) {
        //Calls private put method beginning with the root of the tree
        Node newNode = put(root, key, val, root.getHeight());
        //If the insertion results in a split in the root and returns
        //a node, a new root is created with references to the two
        //halves of the split original root and an increased height
        if (newNode != null) {
            Node oldRoot = root;
            root = new Node(oldRoot.getHeight() + 1, MAX_SIZE, 2);
            root.getEntries()[0] = new Entry<Key, Node>((Key) oldRoot.getEntries()[0].getKey(), oldRoot, false);
            root.getEntries()[1] = new Entry<Key, Node>((Key) newNode.getEntries()[0].getKey(), newNode, false);
        }
    }

    /**
     * A private put method, which recursively locates the proper
     * node for the insertion of a given key and value and
     * calls another put method for the insertion
     *
     * @param currentNode The node being assessed
     * @param key         The key of the entry being inserted
     * @param val         The value of the entry being inserted
     * @param height      The height of the given node
     * @return The newly created node in the case of a split
     */
    private Node put(Node currentNode, Key key, Value val, int height) {
        //If the node is external, find the proper place in
        //the node for the insertion
        if (height == 0) {
            if (key != null) {
                Entry newEntry = new Entry<Key, Value>(key, val, true);
                for (int i = 0; i < currentNode.getEntryCount(); i++) {
                    //If the key is less than the next key, insert it
                    //before that entry
                    if (key.compareTo((Key) currentNode.getEntries()[i].getKey()) < 0) {
                        return put(currentNode, newEntry, i);
                    }
                    //If the key equals an already existing key, add the value to that entry
                    else if (key.compareTo((Key) currentNode.getEntries()[i].getKey()) == 0) {
                        if (val != null) {
                            currentNode.getEntries()[i].addValue(val);
                        } else {
                            currentNode.getEntries()[i].removeValues();
                        }
                        return null;
                    }
                }
                return put(currentNode, newEntry, currentNode.getEntryCount());
            } else {
                return null;
            }
        }
        //If the node is internal, find the proper child node for the insertion
        //and recursively call put with that node
        else {
            for (int i = 0; i < currentNode.getEntryCount(); i++) {
                if ((i < currentNode.getEntryCount() - 1 && key.compareTo((Key) currentNode.getEntries()[i + 1].getKey()) < 0) || i == currentNode.getEntryCount() - 1) {
                    //Recursive call
                    Node newNode = put((Node) currentNode.getEntries()[i].getValue(), key, val, height - 1);
                    //If the put call on the external node results in a split, a new
                    //entry referencing the new node must be inserted into the parent
                    //node
                    if (newNode != null) {
                        Entry newEntry = new Entry<Key, Node>((Key) newNode.getEntries()[0].getKey(), newNode, false);
                        return put(currentNode, newEntry, i + 1);
                    } else {
                        return null;
                    }
                }
            }
            return null;
        }
    }

    /**
     * The put method for a specific node, which inserts an entry
     * into a node and splits it if necessary
     *
     * @param currentNode The node into which the entry is being inserted
     * @param entry       The entry being inserted
     * @param entryIndex  The location in the node for the entry
     * @return The newly created node in the case of a split
     */
    private Node put(Node currentNode, Entry entry, int entryIndex) {
        if (entryIndex != currentNode.getEntryCount()) {
            for (int j = currentNode.getEntryCount(); j > entryIndex; j--) {
                currentNode.getEntries()[j] = currentNode.getEntries()[j - 1];
            }
        }
        currentNode.getEntries()[entryIndex] = entry;
        currentNode.incrementEntryCount();
        if (currentNode.getEntryCount() < MAX_SIZE) {
            return null;
        }
        //If the node is full, it splits and returns the newly
        //created node to be referenced in the parent node
        else {
            return split(currentNode);
        }
    }


    /**
     * The split method, which splits a full node into two
     * half-full nodes and returns the new node (the second half)
     *
     * @param currentNode The full node being split
     * @return The new node created
     */
    private Node split(Node currentNode) {
        Node newNode = new Node(currentNode.getHeight(), MAX_SIZE, MAX_SIZE / 2);
        for (int i = MAX_SIZE / 2; i < MAX_SIZE; i++) {
            newNode.getEntries()[i - (MAX_SIZE / 2)] = currentNode.getEntries()[i];
        }
        currentNode.setEntryCount(MAX_SIZE / 2);
        //Keep reference to Node order
        newNode.setNextNode(currentNode.getNextNode());
        currentNode.setNextNode(newNode);
        newNode.setPreviousNode(currentNode);
        //Update the rightmost Node reference if necessary
        if (newNode.getNextNode() == null) {
            rightmost = newNode;
        }
        return newNode;
    }

    /**
     * The delete method, which deletes all values in the B-Tree
     * for a given key
     *
     * @param key The key to be deleted
     */
    public void delete(Key key) {
        //If the key has a value in the tree, replace that
        //key's value with null
        if (get(key) != null) {
            put(key, null);
        }
    }

    /**
     * The more specific delete method which deletes a particular
     * value in the B-Tree from its key
     *
     * @param key The key where you are deleting
     * @param val The value you are deleting from that key
     */
    public void delete(Key key, Value val) {
        if (get(key) != null) {
            if (get(key).contains(val)) {
                get(key).remove(val);
            }
        }
    }
}