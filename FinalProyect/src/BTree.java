public class BTree {
    private int t;
    private BTreeNode root;

    public BTree(int t) {
        this.t = t;
        root = new BTreeNode(t, true);
    }

    public BTreeNode getRoot() {
        return root;
    }

    

    public void insert(int key) {
        if (root.n == 2 * t - 1) {
            BTreeNode s = new BTreeNode(t, false);
            s.children[0] = root;
            s.splitChild(0, root);
            int i = 0;
            if (s.keys[0] < key) {
                i++;
            }
            s.children[i].insertNonFull(key);
            root = s;
        } else {
            root.insertNonFull(key);
        }
    }
}
