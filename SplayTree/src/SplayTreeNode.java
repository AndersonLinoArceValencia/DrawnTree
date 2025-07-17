public class SplayTreeNode {
    int key;
    SplayTreeNode left, right, parent;

    public SplayTreeNode(int key) {
        this.key = key;
        this.left = this.right = this.parent = null;
    }
}