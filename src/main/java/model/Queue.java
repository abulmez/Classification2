package model;


public class Queue<E> extends DataStructure<E>{
    public Queue(){
        super();
    }


    /**
     * Adds an element to the back of the queue
     */
    @Override
    public void push(E elem){
        super.container.addLast(elem);
    }


    /**
     * Removes and returns the first element of the queue
     */
    @Override
    public E pop(){
        return container.remove();
    }

    @Override
    public E peek() {
        //todo
        return null;
    }

}
