package model;

import java.util.LinkedList;

public abstract class DataStructure <E>{
    protected LinkedList<E> container;
    public DataStructure(){
        container = new LinkedList<>();
    }

    /**
     * Adds an element to the data structure
     */
    public abstract void push(E elem);

    /**
     * Removes and returns an element from the data structure
     */
    public abstract E pop();

    /**
     * Returns true if the data structure is empty or false otherwise
     */

    public abstract E peek();
    public Boolean isEmpty(){
        return container.isEmpty();
    }
}
