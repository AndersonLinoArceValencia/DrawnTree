package com.simulador.DrawnTree.Modelo.SplayTree;

public class Splaytree {
    private SplayTreeNode root;

    // Rotación a la izquierda (para splaying)
    private void leftRotate(SplayTreeNode x) {
        SplayTreeNode y = x.right;
        x.right = y.left;
        if (y.left != null) y.left.parent = x;
        y.parent = x.parent;
        if (x.parent == null) root = y;
        else if (x == x.parent.left) x.parent.left = y;
        else x.parent.right = y;
        y.left = x;
        x.parent = y;
    }

    // Rotación a la derecha (para splaying)
    private void rightRotate(SplayTreeNode x) {
        SplayTreeNode y = x.left;
        x.left = y.right;
        if (y.right != null) y.right.parent = x;
        y.parent = x.parent;
        if (x.parent == null) root = y;
        else if (x == x.parent.right) x.parent.right = y;
        else x.parent.left = y;
        y.right = x;
        x.parent = y;
    }

    // Operación Splay: mueve el nodo a la raíz
    private void splay(SplayTreeNode node) {
        while (node.parent != null) {
            if (node.parent.parent == null) { // Caso 1: Zig
                if (node == node.parent.left) rightRotate(node.parent);
                else leftRotate(node.parent);
            } else if (node == node.parent.left && node.parent == node.parent.parent.left) { // Zig-Zig (derecha)
                rightRotate(node.parent.parent);
                rightRotate(node.parent);
            } else if (node == node.parent.right && node.parent == node.parent.parent.right) { // Zig-Zig (izquierda)
                leftRotate(node.parent.parent);
                leftRotate(node.parent);
            } else if (node == node.parent.right && node.parent == node.parent.parent.left) { // Zig-Zag
                leftRotate(node.parent);
                rightRotate(node.parent);
            } else { // Zig-Zag inverso
                rightRotate(node.parent);
                leftRotate(node.parent);
            }
        }
    }

    // Insertar un nodo
    public void insert(int key) {
        SplayTreeNode node = new SplayTreeNode(key);
        SplayTreeNode y = null;
        SplayTreeNode x = root;

        while (x != null) {
            y = x;
            if (node.key < x.key) x = x.left;
            else x = x.right;
        }

        node.parent = y;
        if (y == null) root = node;
        else if (node.key < y.key) y.left = node;
        else y.right = node;

        splay(node); // Ajustar el árbol
    }

    // Buscar un nodo
    public SplayTreeNode search(int key) {
        SplayTreeNode node = root;
        while (node != null) {
            if (node.key == key) {
                splay(node); // Ajustar el árbol
                return node;
            } else if (key < node.key) node = node.left;
            else node = node.right;
        }
        return null;
    }

    // Eliminar un nodo
    public void delete(int key) {
        SplayTreeNode node = search(key); // Busca y hace splay
        if (node == null) return;

        if (node.left == null) replace(node, node.right);
        else if (node.right == null) replace(node, node.left);
        else {
            SplayTreeNode min = node.right;
            while (min.left != null) min = min.left;
            if (min.parent != node) {
                replace(min, min.right);
                min.right = node.right;
                min.right.parent = min;
            }
            replace(node, min);
            min.left = node.left;
            min.left.parent = min;
        }
    }

    private void replace(SplayTreeNode u, SplayTreeNode v) {
        if (u.parent == null) root = v;
        else if (u == u.parent.left) u.parent.left = v;
        else u.parent.right = v;
        if (v != null) v.parent = u.parent;
    }

    public void printTree() {
        printTree(root, 0);
    }

    private void printTree(SplayTreeNode node, int indent) {
        if (node == null) return;
        printTree(node.right, indent + 4);
        for (int i = 0; i < indent; i++) System.out.print(" ");
        System.out.println(node.key);
        printTree(node.left, indent + 4);
    }

    
    public int getRootKey() {
        return root != null ? root.key : -1;  // Retorna -1 si el árbol está vacío.
    }
}