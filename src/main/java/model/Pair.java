package model;

public class Pair<E1,E2> {
    private E1 first;
    private E2 second;
    public Pair(E1 first, E2 second){
        this.first = first;
        this.second = second;
    }


    /**
     * Returns the first element of a pair
     */
    public E1 getFirst() {
        return first;
    }

    /**
     * Sets the first element of a pair
     */
    public void setFirst(E1 first) {
        this.first = first;
    }


    /**
     * Returns the second element of a pair
     */
    public E2 getSecond() {
        return second;
    }

    /**
     * Sets the second element of a pair
     */
    public void setSecond(E2 second) {
        this.second = second;
    }
}
