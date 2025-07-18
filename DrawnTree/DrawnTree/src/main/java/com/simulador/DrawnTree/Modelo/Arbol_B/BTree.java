package com.simulador.DrawnTree.Modelo.Arbol_B;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BTree {
    private BTreeNode root;
    private int degree;

    public BTree(int degree) {
        this.root = new BTreeNode(true);
        this.degree = degree;
    }

    public boolean search(int key) {
        BTreeNode curr = this.root;
        while (!curr.leaf) {
            int i = 0;
            while (i < curr.keys.size()) {
                if (key < curr.keys.get(i)) {
                    break;
                }
                i += 1;
            }
            curr = curr.values.get(i);
        }
        int i = 0;
        while (i < curr.keys.size()) {
            if (curr.keys.get(i) == key) {
                return true;
            }
            i += 1;
        }
        return false;
    }

    public void insert(int key) {
       BTreeNode curr = this.root;
        if (curr.keys.size() == 2 * this.degree) {
           BTreeNode newRoot = new BTreeNode(false);
            this.root = newRoot;
            newRoot.values.add(curr);
            this.split(newRoot, 0, curr);
            this.insertNonFull(newRoot, key);
        } else {
            this.insertNonFull(curr, key);
        }
    }

    private void insertNonFull(BTreeNode curr, int key) {
        int i = 0;
        while (i < curr.keys.size()) {
            if (key < curr.keys.get(i)) {
                break;
            }
            i += 1;
        }
        if (curr.leaf) {
            curr.keys.add(i, key);
        } else {
            if (curr.values.get(i).keys.size() == 2 * this.degree) {
                this.split(curr, i, curr.values.get(i));
                if (key > curr.keys.get(i)) {
                    i += 1;
                }
            }
            this.insertNonFull(curr.values.get(i), key);
        }
    }

    
    private void split(BTreeNode parent, int index, BTreeNode node) {
    BTreeNode new_node = new BTreeNode(node.leaf);
    parent.values.add(index + 1, new_node);
    parent.keys.add(index, node.keys.get(this.degree - 1));

    new_node.keys.addAll(node.keys.subList(this.degree, node.keys.size()));
    node.keys.subList(this.degree - 1, node.keys.size()).clear();

    if (!node.leaf) {
        new_node.values.addAll(node.values.subList(this.degree, node.values.size()));
        node.values.subList(this.degree, node.values.size()).clear();
    }
}


    private void stealFromLeft(BTreeNode parent, int i) {
        BTreeNode node = parent.values.get(i);
        BTreeNode leftSibling = parent.values.get(i - 1);
        node.keys.add(0, parent.keys.get(i - 1));
        parent.keys.set(i - 1, leftSibling.keys.remove(leftSibling.keys.size() - 1));
        if (!node.leaf) {
            node.values.add(0, leftSibling.values.remove(leftSibling.values.size() - 1));
        }
    }

    private void stealFromRight(BTreeNode parent, int i) {
        BTreeNode node = parent.values.get(i);
        BTreeNode rightSibling = parent.values.get(i + 1);
        node.keys.add(parent.keys.get(i));
        parent.keys.set(i, rightSibling.keys.remove(0));
        if (!node.leaf) {
            node.values.add(rightSibling.values.remove(0));
        }
    }

    public void delete(int key) {
        BTreeNode curr = this.root;
        boolean found = false;
        int i = 0;
        while (i < curr.keys.size()) {
            if (key == curr.keys.get(i)) {
                found = true;
                break;
            } else if (key < curr.keys.get(i)) {
                break;
            }
            i += 1;
        }
        if (found) {
            if (curr.leaf) {
                curr.keys.remove(i);
            } else {
                BTreeNode pred = curr.values.get(i);
                if (pred.keys.size() >= this.degree) {
                    int predKey = this.getMaxKey(pred);
                    curr.keys.set(i, predKey);
                    this.deleteFromLeaf(predKey, pred);
                } else {
                   BTreeNode succ = curr.values.get(i + 1);
                    if (succ.keys.size() >= this.degree) {
                        int succKey = this.getMinKey(succ);
                        curr.keys.set(i, succKey);
                        this.deleteFromLeaf(succKey, succ);
                    } else {
                        this.merge(curr, i, pred, succ);
                        this.deleteFromLeaf(key, pred);
                    }
                }

                if (curr == this.root && curr.keys.size() == 0) {
                    this.root = curr.values.get(0);
                }
            }
        } else {
            if (curr.leaf) {
                return;
            } else {
                if (curr.values.get(i).keys.size() < this.degree) {
                    if (i != 0 && curr.values.get(i - 1).keys.size() >= this.degree) {
                        this.stealFromLeft(curr, i);
                    } else if (i != curr.keys.size() && curr.values.get(i + 1).keys.size() >= this.degree) {
                        this.stealFromRight(curr, i);
                    } else {
                        if (i == curr.keys.size()) {
                            i -= 1;
                        }
                        this.merge(curr, i, curr.values.get(i), curr.values.get(i + 1));
                    }
                }

                this.delete(key);
            }
        }
    }

    private void deleteFromLeaf(int key, BTreeNode leaf) {
        leaf.keys.remove(Integer.valueOf(key));

        if (leaf == this.root || leaf.keys.size() >= Math.floor(this.degree / 2)) {
            return;
        }

       BTreeNode parent = this.findParent(leaf);
        int i = parent.values.indexOf(leaf);

        if (i > 0 && parent.values.get(i - 1).keys.size() > Math.floor(this.degree / 2)) {
            this.rotateRight(parent, i);
        } else if (i < parent.keys.size() && parent.values.get(i + 1).keys.size() > Math.floor(this.degree / 2)) {
            this.rotateLeft(parent, i);
        } else {
            if (i == parent.keys.size()) {
                i -= 1;
            }
            this.merge(parent, i, parent.values.get(i), parent.values.get(i + 1));
        }
    }

    private int getMinKey(BTreeNode node) {
        while (!node.leaf) {
            node = node.values.get(0);
        }
        return node.keys.get(0);
    }

    private int getMaxKey(BTreeNode node) {
        while (!node.leaf) {
            node = node.values.get(node.values.size() - 1);
        }
        return node.keys.get(node.keys.size() - 1);
    }

    private BTreeNode findParent(BTreeNode child) {
        BTreeNode curr = this.root;
        while (!curr.leaf) {
            int i = 0;
            while (i < curr.values.size()) {
                if (child == curr.values.get(i)) {
                    return curr;
                } else if (child.keys.get(0) < curr.values.get(i).keys.get(0)) {
                    break;
                }
                i += 1;
            }
            curr = curr.values.get(i);
        }
        return null;
    }

    private void merge(BTreeNode parent, int i, BTreeNode pred, BTreeNode succ) {
        pred.keys.addAll(succ.keys);
        pred.values.addAll(succ.values);
        parent.values.remove(i + 1);
        parent.keys.remove(i);

        if (parent == this.root && parent.keys.size() == 0) {
            this.root = pred;
        }
    }

    private void rotateRight(BTreeNode parent, int i) {
        BTreeNode node = parent.values.get(i);
        BTreeNode prev = parent.values.get(i - 1);
        node.keys.add(0, parent.keys.get(i - 1));
        parent.keys.set(i - 1, prev.keys.remove(prev.keys.size() - 1));
        if (!node.leaf) {
            node.values.add(0, prev.values.remove(prev.values.size() - 1));
        }
    }

    private void rotateLeft(BTreeNode parent, int i) {
        BTreeNode node = parent.values.get(i);
        BTreeNode next = parent.values.get(i + 1);
        node.keys.add(parent.keys.get(i));
        parent.keys.set(i, next.keys.remove(0));
        if (!node.leaf) {
            node.values.add(next.values.remove(0));
        }
    }
public List<Map<String, Object>> toLista() {
  List<Map<String, Object>> lista = new ArrayList<>();
  toLista(root, null, lista);
  return lista;
}

private void toLista(BTreeNode nodo, String padreId, List<Map<String, Object>> lista) {
  if (nodo == null) return;

  for (int i = 0; i < nodo.keys.size(); i++) {
    Map<String, Object> map = new HashMap<>();
    String id = nodo.hashCode() + "_" + i;

    map.put("id", id);
    map.put("valor", nodo.keys.get(i));
    map.put("padre", padreId);

    lista.add(map);
  }

  // Llamada recursiva a los hijos
  for (int i = 0; i < nodo.values.size(); i++) {
    toLista(nodo.values.get(i), nodo.hashCode() + "_H" + i, lista);
  }
}
public void clear() {
  root = null;
}

}
