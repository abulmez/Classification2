package model;

public class Stack<E> extends DataStructure<E> {

    public Stack(){
        super();
    }


    /**
     * Adds an element to the front of the stack
     */
    @Override
    public void push(E elem) {
        super.container.push(elem);
    }

    /**
     * Removes and returns the first element of the stack
     */
    @Override
    public E pop() {
        return super.container.pop();
    }

    @Override
    public E peek(){return super.container.peekFirst();}
}
