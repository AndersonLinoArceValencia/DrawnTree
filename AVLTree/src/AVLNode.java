public class AVLNode {
    public int value;
    public AVLNode left;
    public AVLNode right;
    public int height; // Altura del nodo para el balanceo AVL

    public AVLNode(int value) {
        this.value = value;
        this.left = null;
        this.right = null;
        this.height = 1; // Un nuevo nodo tiene altura 1
    }
}
