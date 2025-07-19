package com.simulador.DrawnTree.Modelo.SplayTree;

public class SplayTreeNode {
           SplayTreeNode left, right, parent;
 int key;
        public SplayTreeNode(int key) {
            this.key = key;
            this.left = null;
            this.right = null;
            this.parent = null;
        }
    }
