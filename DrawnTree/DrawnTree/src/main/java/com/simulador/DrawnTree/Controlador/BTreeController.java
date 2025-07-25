package com.simulador.DrawnTree.Controlador;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.simulador.DrawnTree.Modelo.Arbol_B.BTree;

@RestController
@RequestMapping("/api/btree")
@CrossOrigin(origins = "*")
public class BTreeController {

    private final BTree btree = new BTree(3); // Árbol B de grado 3

    @PostMapping("/insertar")
    public ResponseEntity<?> insertar(@RequestBody Map<String, Integer> request) {
        try {
            Integer valor = request.get("valor");
            if (valor == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "El campo 'valor' es requerido"
                ));
            }
            
            btree.insert(valor);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Valor insertado correctamente",
                "tree", btree.toMap(),
                "grado", btree.getDegree()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
                ));
        }
    }

    @DeleteMapping("/eliminar/{valor}")
    public ResponseEntity<?> eliminar(@PathVariable Integer valor) {
        try {
            if (valor == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "El parámetro 'valor' es requerido"
                ));
            }
            
            boolean eliminado = btree.delete(valor);
            if (!eliminado) {
                return ResponseEntity.ok(Map.of(
                    "status", "warning",
                    "message", "El valor no existe en el árbol",
                    "tree", btree.toMap()
                ));
            }
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Valor eliminado correctamente",
                "tree", btree.toMap(),
                "grado", btree.getDegree()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
                ));
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<?> listar() {
        try {
            Map<String, Object> treeData = btree.toMap();
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "tree", treeData,
                "grado", btree.getDegree(),
                "size", btree.size(),
                "height", btree.height()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
                ));
        }
    }

    @DeleteMapping("/vaciar")
    public ResponseEntity<?> vaciarArbol() {
        try {
            btree.clear();
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Árbol vaciado correctamente",
                "tree", btree.toMap(),
                "grado", btree.getDegree()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
                ));
        }
    }

    @GetMapping("/buscar/{valor}")
    public ResponseEntity<?> buscar(@PathVariable Integer valor) {
        try {
            if (valor == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "El parámetro 'valor' es requerido"
                ));
            }
            
            boolean encontrado = btree.search(valor);
            Map<String, Object> treeData = btree.toMap();
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "encontrado", encontrado,
                "tree", treeData,
                "valorBuscado", valor
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
                ));
        }
    }
}