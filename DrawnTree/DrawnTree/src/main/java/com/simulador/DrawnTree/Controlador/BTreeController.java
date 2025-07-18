package com.simulador.DrawnTree.Controlador;

import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.simulador.DrawnTree.Modelo.Arbol_B.BTree;

@RestController
@RequestMapping("/api/btree")
public class BTreeController {

    private final BTree btree = new BTree(3); // Grado mínimo, ajusta según tu necesidad

    // Insertar una clave
    @PostMapping("/insertar/{valor}")
    public Map<String, Object> insertar(@PathVariable int valor) {
        btree.insert(valor);
        return Map.of("nodos", btree.toLista());
    }

    // Eliminar una clave (debes implementar este método en tu clase BTree)
    @DeleteMapping("/eliminar/{valor}")
    public Map<String, Object> eliminar(@PathVariable int valor) {
        btree.delete(valor);
        return Map.of("nodos", btree.toLista());
    }

    // Listar todos los nodos del árbol B
    @GetMapping("/listar")
    public Map<String, Object> listar() {
        return Map.of("nodos", btree.toLista());
    }

    // Vaciar el árbol B
    @DeleteMapping("/vaciar")
    public Map<String, Object> vaciar() {
        btree.clear(); // Debes implementar este método para reiniciar el árbol
        return Map.of("nodos", btree.toLista());
    }
}