public  class Main {
    public static void main(String[] args) {
        SplayTree tree = new SplayTree();
        tree.insert(10);
        tree.insert(5);
        tree.insert(15);
        tree.printTree();
        System.out.println("Buscar 5: " + (tree.search(5) != null ? "Encontrado" : "No existe"));
        System.out.println("Raíz actual: " + tree.getRootKey());
        tree.delete(10);
        System.out.println("Raíz después de eliminar 10: " + tree.getRootKey());
    }
} 
