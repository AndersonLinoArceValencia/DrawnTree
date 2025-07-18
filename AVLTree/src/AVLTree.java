public class AVLTree {
    private AVLNode root;

    public AVLTree() {
        this.root = null;
    }

    // Método auxiliar para obtener la altura de un nodo
    private int height(AVLNode node) {
        return (node == null) ? 0 : node.height;
    }

    // Método auxiliar para actualizar la altura de un nodo
    private void updateHeight(AVLNode node) {
        if (node != null) {
            node.height = 1 + Math.max(height(node.left), height(node.right));
        }
    }

    // Método auxiliar para obtener el factor de balance de un nodo
    private int getBalance(AVLNode node) {
        return (node == null) ? 0 : height(node.left) - height(node.right);
    }

    // Rotación a la derecha
    private AVLNode rotateRight(AVLNode y) {
        AVLNode x = y.left;
        AVLNode T2 = x.right;

        x.right = y;
        y.left = T2;

        updateHeight(y);
        updateHeight(x);

        return x;
    }

    // Rotación a la izquierda
    private AVLNode rotateLeft(AVLNode x) {
        AVLNode y = x.right;
        AVLNode T2 = y.left;

        y.left = x;
        x.right = T2;

        updateHeight(x);
        updateHeight(y);

        return y;
    }

    // Inserción de un valor en el árbol AVL
    public void insert(int value) {
        root = insertNode(root, value);
    }

    private AVLNode insertNode(AVLNode node, int value) {
        // 1. Realizar la inserción normal de BST
        if (node == null) {
            return new AVLNode(value);
        }

        if (value < node.value) {
            node.left = insertNode(node.left, value);
        } else if (value > node.value) {
            node.right = insertNode(node.right, value);
        } else {
            // Valor duplicado, no se permite en este AVL
            return node;
        }

        // 2. Actualizar la altura del nodo actual
        updateHeight(node);

        // 3. Obtener el factor de balance de este nodo para verificar si se desequilibró
        int balance = getBalance(node);

        // 4. Si el nodo se desequilibró, hay 4 casos:

        // Caso Izquierda-Izquierda (LL)
        if (balance > 1 && value < node.left.value) {
            return rotateRight(node);
        }

        // Caso Derecha-Derecha (RR)
        if (balance < -1 && value > node.right.value) {
            return rotateLeft(node);
        }

        // Caso Izquierda-Derecha (LR)
        if (balance > 1 && value > node.left.value) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        // Caso Derecha-Izquierda (RL)
        if (balance < -1 && value < node.right.value) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        // Si no está desequilibrado, devuelve el nodo sin cambios
        return node;
    }

    // Eliminación de un valor del árbol AVL
    public void delete(int value) {
        root = deleteNode(root, value);
    }

    private AVLNode deleteNode(AVLNode node, int value) {
        // 1. Realizar la eliminación normal de BST
        if (node == null) {
            return null;
        }

        if (value < node.value) {
            node.left = deleteNode(node.left, value);
        } else if (value > node.value) {
            node.right = deleteNode(node.right, value);
        } else {
            // Nodo a eliminar encontrado

            // Caso 1: Nodo con 0 o 1 hijo
            if (node.left == null || node.right == null) {
                node = (node.left == null) ? node.right : node.left;
            } else {
                // Caso 2: Nodo con 2 hijos
                // Encontrar el sucesor inorden (el menor en el subárbol derecho)
                AVLNode temp = minValueNode(node.right);
                node.value = temp.value; // Copiar el valor del sucesor a este nodo
                node.right = deleteNode(node.right, temp.value); // Eliminar el sucesor inorden
            }
        }

        // Si el árbol tenía solo un nodo, entonces regresa null
        if (node == null) {
            return node;
        }

        // 2. Actualizar la altura del nodo actual
        updateHeight(node);

        // 3. Obtener el factor de balance de este nodo para verificar si se desequilibró
        int balance = getBalance(node);

        // 4. Si el nodo se desequilibró, hay 4 casos:

        // Caso Izquierda-Izquierda (LL)
        if (balance > 1 && getBalance(node.left) >= 0) {
            return rotateRight(node);
        }

        // Caso Izquierda-Derecha (LR)
        if (balance > 1 && getBalance(node.left) < 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        // Caso Derecha-Derecha (RR)
        if (balance < -1 && getBalance(node.right) <= 0) {
            return rotateLeft(node);
        }

        // Caso Derecha-Izquierda (RL)
        if (balance < -1 && getBalance(node.right) > 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    // Método auxiliar para encontrar el nodo con el valor mínimo en un subárbol
    private AVLNode minValueNode(AVLNode node) {
        AVLNode current = node;
        while (current.left != null) {
            current = current.left;
        }
        return current;
    }

    // Método para obtener la raíz del árbol (para serialización JSON)
    public AVLNode getRoot() {
        return root;
    }

    // Método para reiniciar el árbol
    public void reset() {
        this.root = null;
    }
}

