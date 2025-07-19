package com.simulador.DrawnTree.Modelo.Arbol_B;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class BTree {
    private BTreeNode root;
    private final int t; // Grado mínimo del árbol

    public BTree(int t) {
        if (t < 2) {
            throw new IllegalArgumentException("El grado del árbol B debe ser al menos 2");
        }
        this.root = null;
        this.t = t;
    }

    public static class BTreeNode {
        List<Integer> keys;
        List<BTreeNode> children;
        boolean leaf;
        BTreeNode next; // Para recorrido secuencial en hojas

        public BTreeNode(boolean leaf) {
            this.keys = new ArrayList<>();
            this.children = new ArrayList<>();
            this.leaf = leaf;
            this.next = null;
        }

        public boolean isLeaf() {
            return leaf;
        }
    }

    // Operaciones básicas
    public boolean insert(int key) {
        if (root == null) {
            root = new BTreeNode(true);
            root.keys.add(key);
            return true;
        }

        if (root.keys.size() == 2 * t - 1) {
            BTreeNode newRoot = new BTreeNode(false);
            newRoot.children.add(root);
            splitChild(newRoot, 0);
            root = newRoot;
        }

        return insertNonFull(root, key);
    }

    public boolean search(int key) {
        return search(root, key);
    }

    public boolean delete(int key) {
        if (root == null) return false;

        boolean deleted = delete(root, key);

        // Si la raíz queda vacía y no es hoja, hacer su único hijo la nueva raíz
        if (root.keys.isEmpty() && !root.isLeaf()) {
            root = root.children.get(0);
        }

        return deleted;
    }

    public void clear() {
        root = null;
    }

    // Métodos de información
    public int size() {
        return size(root);
    }

    public int height() {
        return height(root);
    }

    public int getDegree() {
        return t;
    }

    public boolean isEmpty() {
        return root == null || root.keys.isEmpty();
    }

    // Métodos para serialización
    public Map<String, Object> toMap() {
        Map<String, Object> map = new TreeMap<>();
        if (root != null) {
            map.put("root", nodeToMap(root));
            map.put("grado", t);
            map.put("size", size());
            map.put("height", height());
        }
        return map;
    }

    // Métodos privados de implementación
    private boolean search(BTreeNode node, int key) {
        if (node == null) return false;

        int i = 0;
        while (i < node.keys.size() && key > node.keys.get(i)) {
            i++;
        }

        if (i < node.keys.size() && key == node.keys.get(i)) {
            return true;
        }

        return node.isLeaf() ? false : search(node.children.get(i), key);
    }

    private boolean insertNonFull(BTreeNode node, int key) {
        int i = node.keys.size() - 1;

        if (node.isLeaf()) {
            // Insertar en hoja
            while (i >= 0 && key < node.keys.get(i)) {
                i--;
            }
            // Evitar duplicados
            if (i >= 0 && key == node.keys.get(i)) {
                return false;
            }
            node.keys.add(i + 1, key);
            return true;
        } else {
            // Encontrar hijo adecuado
            while (i >= 0 && key < node.keys.get(i)) {
                i--;
            }
            i++;

            // Verificar si el hijo está lleno
            if (node.children.get(i).keys.size() == 2 * t - 1) {
                splitChild(node, i);
                if (key > node.keys.get(i)) {
                    i++;
                }
            }

            return insertNonFull(node.children.get(i), key);
        }
    }

    private void splitChild(BTreeNode parent, int childIndex) {
        BTreeNode child = parent.children.get(childIndex);
        BTreeNode newChild = new BTreeNode(child.isLeaf());

        // Mover la clave media al padre
        parent.keys.add(childIndex, child.keys.get(t - 1));

        // Mover las claves mayores al nuevo nodo
        newChild.keys.addAll(child.keys.subList(t, 2 * t - 1));
        child.keys.subList(t - 1, 2 * t - 1).clear();

        // Mover los hijos si no es hoja
        if (!child.isLeaf()) {
            newChild.children.addAll(child.children.subList(t, 2 * t));
            child.children.subList(t, 2 * t).clear();
        }

        // Enlazar hojas para recorrido secuencial
        if (child.isLeaf()) {
            newChild.next = child.next;
            child.next = newChild;
        }

        parent.children.add(childIndex + 1, newChild);
    }

    private boolean delete(BTreeNode node, int key) {
        int idx = findKeyIndex(node, key);

        // Caso 1: La clave está en este nodo
        if (idx < node.keys.size() && node.keys.get(idx) == key) {
            if (node.isLeaf()) {
                node.keys.remove(idx); // Caso simple: eliminación de hoja
                return true;
            } else {
                return deleteFromInternalNode(node, idx); // Caso complejo
            }
        } else {
            // Caso 2: La clave no está en este nodo
            if (node.isLeaf()) {
                return false; // Clave no encontrada
            }

            // Asegurar que el hijo tiene al menos t claves
            boolean isLastChild = (idx == node.keys.size());
            if (node.children.get(idx).keys.size() < t) {
                fillChild(node, idx);
            }

            // Si el último hijo fue fusionado, el índice pudo cambiar
            if (isLastChild && idx > node.keys.size()) {
                return delete(node.children.get(idx - 1), key);
            } else {
                return delete(node.children.get(idx), key);
            }
        }
    }

    private boolean deleteFromInternalNode(BTreeNode node, int idx) {
        int key = node.keys.get(idx);

        // Caso 3a: Hijo anterior tiene al menos t claves
        if (node.children.get(idx).keys.size() >= t) {
            int predecessor = getPredecessor(node, idx);
            node.keys.set(idx, predecessor);
            return delete(node.children.get(idx), predecessor);
        }
        // Caso 3b: Hijo siguiente tiene al menos t claves
        else if (node.children.get(idx + 1).keys.size() >= t) {
            int successor = getSuccessor(node, idx);
            node.keys.set(idx, successor);
            return delete(node.children.get(idx + 1), successor);
        }
        // Caso 3c: Fusionar hijos
        else {
            mergeChildren(node, idx);
            return delete(node.children.get(idx), key);
        }
    }

    private int getPredecessor(BTreeNode node, int idx) {
        BTreeNode curr = node.children.get(idx);
        while (!curr.isLeaf()) {
            curr = curr.children.get(curr.keys.size());
        }
        return curr.keys.get(curr.keys.size() - 1);
    }

    private int getSuccessor(BTreeNode node, int idx) {
        BTreeNode curr = node.children.get(idx + 1);
        while (!curr.isLeaf()) {
            curr = curr.children.get(0);
        }
        return curr.keys.get(0);
    }

    private void fillChild(BTreeNode node, int idx) {
        // Intentar tomar prestado del hermano izquierdo
        if (idx != 0 && node.children.get(idx - 1).keys.size() >= t) {
            borrowFromLeft(node, idx);
        }
        // Intentar tomar prestado del hermano derecho
        else if (idx != node.keys.size() && node.children.get(idx + 1).keys.size() >= t) {
            borrowFromRight(node, idx);
        }
        // Fusionar con un hermano
        else {
            if (idx != node.keys.size()) {
                mergeChildren(node, idx);
            } else {
                mergeChildren(node, idx - 1);
            }
        }
    }

    private void borrowFromLeft(BTreeNode node, int idx) {
        BTreeNode child = node.children.get(idx);
        BTreeNode leftSibling = node.children.get(idx - 1);

        // Mover clave del padre al hijo
        child.keys.add(0, node.keys.get(idx - 1));
        
        // Mover última clave del hermano izquierdo al padre
        node.keys.set(idx - 1, leftSibling.keys.remove(leftSibling.keys.size() - 1));
        
        // Mover hijo si no es hoja
        if (!child.isLeaf()) {
            child.children.add(0, leftSibling.children.remove(leftSibling.children.size() - 1));
        }
    }

    private void borrowFromRight(BTreeNode node, int idx) {
        BTreeNode child = node.children.get(idx);
        BTreeNode rightSibling = node.children.get(idx + 1);

        // Mover clave del padre al hijo
        child.keys.add(node.keys.get(idx));
        
        // Mover primera clave del hermano derecho al padre
        node.keys.set(idx, rightSibling.keys.remove(0));
        
        // Mover hijo si no es hoja
        if (!child.isLeaf()) {
            child.children.add(rightSibling.children.remove(0));
        }
    }

    private void mergeChildren(BTreeNode node, int idx) {
        BTreeNode child = node.children.get(idx);
        BTreeNode rightSibling = node.children.get(idx + 1);

        // Mover clave del padre al hijo
        child.keys.add(node.keys.remove(idx));
        
        // Mover claves del hermano
        child.keys.addAll(rightSibling.keys);
        
        // Mover hijos si no es hoja
        if (!child.isLeaf()) {
            child.children.addAll(rightSibling.children);
        }
        
        // Actualizar enlace de hojas
        if (child.isLeaf()) {
            child.next = rightSibling.next;
        }
        
        // Eliminar hermano fusionado
        node.children.remove(idx + 1);
    }

    private int findKeyIndex(BTreeNode node, int key) {
        int idx = 0;
        while (idx < node.keys.size() && key > node.keys.get(idx)) {
            idx++;
        }
        return idx;
    }

    private int size(BTreeNode node) {
        if (node == null) return 0;
        
        int count = node.keys.size();
        for (BTreeNode child : node.children) {
            count += size(child);
        }
        return count;
    }

    private int height(BTreeNode node) {
        if (node == null) return -1;
        if (node.isLeaf()) return 0;
        
        int maxHeight = 0;
        for (BTreeNode child : node.children) {
            maxHeight = Math.max(maxHeight, height(child));
        }
        return 1 + maxHeight;
    }

    private Map<String, Object> nodeToMap(BTreeNode node) {
        Map<String, Object> nodeMap = new TreeMap<>();
        nodeMap.put("claves", new ArrayList<>(node.keys));
        nodeMap.put("hoja", node.isLeaf());

        if (!node.isLeaf()) {
            List<Map<String, Object>> childrenList = new ArrayList<>();
            for (BTreeNode child : node.children) {
                childrenList.add(nodeToMap(child));
            }
            nodeMap.put("hijos", childrenList);
        }

        return nodeMap;
    }
}