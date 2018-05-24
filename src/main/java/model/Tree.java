package model;

import java.io.Serializable;
import java.util.ArrayList;

public class Tree<Elem> implements Serializable{
    private Node<Elem> root;

    public Tree(){
        root = null;
    }

    public Node<Elem> getRoot() {
        return root;
    }

    public void setRoot(Node<Elem> root) {
        this.root = root;
    }

    public ArrayList<Node> inorder(){
        ArrayList<Node> inorderTree = new ArrayList<>();


        return inorderTree;
    }
}
