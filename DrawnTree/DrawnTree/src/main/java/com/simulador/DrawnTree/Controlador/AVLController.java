package com.simulador.DrawnTree.Controlador;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.simulador.DrawnTree.Modelo.AVLTree.AVLNode;
import com.simulador.DrawnTree.Modelo.AVLTree.AVLTree;

@RestController
@RequestMapping("/avl")
@CrossOrigin(origins = "*")
public class AVLController {

  private final AVLTree arbol = new AVLTree();

  // Insertar un valor al árbol AVL
  @PostMapping("/insertar")
  public void insertar(@RequestParam int valor) {
    arbol.insert(valor);
  }

  // Eliminar un valor del árbol AVL
  @PostMapping("/eliminar")
  public void eliminar(@RequestParam int valor) {
    arbol.delete(valor);
  }

  // Obtener la estructura actual del árbol en formato JSON
@GetMapping("/estructura")
public ResponseEntity<Object> obtenerEstructura() {
  AVLNode nodo = arbol.getRoot();
  if (nodo == null) {
    return ResponseEntity.ok(new HashMap<>()); // ← devuelve {} en vez de null
  }
  return ResponseEntity.ok(arbol.nodeToMap(nodo));
}
@GetMapping("/buscar")
public ResponseEntity<Map<String, Object>> buscar(@RequestParam int valor) {
    boolean encontrado = arbol.buscar(valor);
    Map<String, Object> response = new HashMap<>();
    response.put("encontrado", encontrado);
    return ResponseEntity.ok(response);
}

  // Reiniciar el árbol (eliminar todos los nodos)
@PostMapping("/reiniciar")
public ResponseEntity<Map<String, String>> reiniciarArbol() {
  arbol.reset(); // 👈 Llama al método sobre la instancia
  Map<String, String> response = new HashMap<>();
  response.put("mensaje", "Árbol reiniciado correctamente");
  return ResponseEntity.ok(response);
}
}