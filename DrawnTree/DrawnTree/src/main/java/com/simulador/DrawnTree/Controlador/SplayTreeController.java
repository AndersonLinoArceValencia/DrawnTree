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

import com.simulador.DrawnTree.Modelo.SplayTree.Splaytree;

@RestController
@RequestMapping("/api/splaytree")
@CrossOrigin(origins = "*")
public class SplayTreeController {

    private final Splaytree splayTree = new Splaytree();

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
            
            splayTree.insert(valor);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Valor insertado correctamente",
                "raiz", splayTree.getRootKey()
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
            
            splayTree.delete(valor);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Valor eliminado correctamente",
                "raiz", splayTree.getRootKey()
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
            
            boolean encontrado = splayTree.search(valor) != null;
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "encontrado", encontrado,
                "raiz", splayTree.getRootKey(),
                "message", encontrado ? "Valor encontrado" : "Valor no encontrado"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
                ));
        }
    }

    @GetMapping("/raiz")
    public ResponseEntity<?> obtenerRaiz() {
        try {
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "raiz", splayTree.getRootKey()
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
        splayTree.clear(); // Asegúrate de que este método existe en tu clase Splaytree
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Árbol vaciado correctamente",
            "arbol", splayTree.toMap() // Devuelve la estructura vacía
        ));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
    }
}

    @GetMapping("/visualizar")
public ResponseEntity<?> visualizarArbol() {
    try {
        Map<String, Object> estructura = splayTree.toMap(); // Debe incluir la estructura completa
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "arbol", estructura
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