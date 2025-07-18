package com.simulador.arbol.controller;

import com.simulador.arbol.model.SplayTree;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/splay-tree")
public class SplayTreeController {

    private final SplayTree splayTree = new SplayTree();

    // Insertar un nodo
    @PostMapping("/insert/{key}")
    public String insert(@PathVariable int key) {
        splayTree.insert(key);
        return String.format("Nodo %d insertado. Raíz actual: %d", key, splayTree.getRootKey());
    }

    // Buscar un nodo
    @GetMapping("/search/{key}")
    public String search(@PathVariable int key) {
        boolean found = splayTree.search(key) != null;
        return found 
            ? String.format("Nodo %d encontrado. Raíz actual: %d", key, splayTree.getRootKey())
            : "Nodo no encontrado";
    }

    // Eliminar un nodo
    @DeleteMapping("/delete/{key}")
    public String delete(@PathVariable int key) {
        splayTree.delete(key);
        return String.format("Nodo %d eliminado. Raíz actual: %d", key, splayTree.getRootKey());
    }

    // Obtener estructura del árbol (para depuración)
    @GetMapping("/structure")
    public String getStructure() {
        StringBuilder sb = new StringBuilder();
        splayTree.printTree();
        return "Ver estructura en consola del servidor";
    }
}