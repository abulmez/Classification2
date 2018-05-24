package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;


public class Chromosome implements Serializable {
    private Tree<String> tree;

    public Chromosome(Tree<String> tree){
        this.tree = tree;
    }

    public Tree<String> getTree() {
        return tree;
    }

    public void setTree(Tree<String> tree) {
        this.tree = tree;
    }
}
