package model;

import java.io.Serializable;

public class Node<Elem> implements Serializable{
    private Elem data;
    private Node<Elem> leftChild;
    private Node<Elem> rightChild;
    private Node<Elem> parent;

    public Node(Elem data){
        this.data = data;
    }

    public Elem getData() {
        return data;
    }

    public void setData(Elem data) {
        this.data = data;
    }

    public Node<Elem> getLeftChild() {
        return leftChild;
    }

    public void setLeftChild(Node<Elem> leftChild) {
        this.leftChild = leftChild;
    }

    public Node<Elem> getRightChild() {
        return rightChild;
    }

    public void setRightChild(Node<Elem> rightChild) {
        this.rightChild = rightChild;
    }

    public Node<Elem> getParent() {
        return parent;
    }

    public void setParent(Node<Elem> parent) {
        this.parent = parent;
    }
}
