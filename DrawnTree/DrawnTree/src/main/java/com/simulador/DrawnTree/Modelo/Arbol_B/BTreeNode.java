package com.simulador.DrawnTree.Modelo.Arbol_B;

import java.util.ArrayList;
import java.util.List;

public class BTreeNode {
List<Integer> keys;
    List< BTreeNode > values;
    boolean leaf;
    BTreeNode next;

    public BTreeNode(boolean leaf) {
        this.keys = new ArrayList<>();
        this.values = new ArrayList<>();
        this.leaf = leaf;
        this.next = null;

    }

   

}
