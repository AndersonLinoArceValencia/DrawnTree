package com.simulador.DrawnTree.Controlador;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.simulador.DrawnTree.Modelo.ArbolBinario;

@RestController
@RequestMapping("/api/arbol")
public class ArbolController {

  // Instancia única del árbol
  private final ArbolBinario arbol = new ArbolBinario();

  public ArbolController() {
    // Puedes inicializar con un valor fijo o dejarlo vacío
    arbol.insertar(50); // Este es opcional
  }

  // Endpoint para insertar un nodo
  @PostMapping("/insertar/{valor}")
  public Map<String, Object> insertar(@PathVariable int valor) {
    arbol.insertar(valor);
    return Map.of("nodos", arbol.toLista());
  }

  // Endpoint para eliminar un nodo
  @DeleteMapping("/eliminar/{valor}")
  public Map<String, Object> eliminar(@PathVariable int valor) {
    arbol.eliminar(valor);
    return Map.of("nodos", arbol.toLista());
  }

  // Endpoint para listar todos los nodos del árbol
  @GetMapping("/listar")
  public Map<String, Object> listar() {
    return Map.of("nodos", arbol.toLista());
  }

  // Endpoint para buscar un nodo y obtener el camino hacia él
@GetMapping("/buscar/{valor}")
@ResponseBody
public Map<String, Object> buscar(@PathVariable int valor) {
  Map<String, Object> resultado = arbol.buscarConCamino(valor);

  List<Integer> camino = (List<Integer>) resultado.get("camino");
  boolean encontrado = (boolean) resultado.get("encontrado");

  Map<String, Object> respuesta = new HashMap<>();
  respuesta.put("camino", camino);
  respuesta.put("encontrado", encontrado);
  respuesta.put("nodos", arbol.toLista());

  return respuesta;
}

@DeleteMapping("/vaciar")
public Map<String, Object> vaciar() {
    arbol.raiz = null;
    return Map.of("nodos", arbol.toLista());
}


}