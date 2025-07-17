package com.simulador.DrawnTree.Modelo;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArbolBinario {
  public Nodo raiz;

  public void insertar(int valor) {
    raiz = insertarRec(raiz, valor);
  }

  private Nodo insertarRec(Nodo nodo, int valor) {
    if (nodo == null) return new Nodo(valor);
    if (valor < nodo.valor) nodo.izq = insertarRec(nodo.izq, valor);
    else if (valor > nodo.valor) nodo.der = insertarRec(nodo.der, valor);
    return nodo;
  }

  public void eliminar(int valor) {
    raiz = eliminarRec(raiz, valor);
  }

  private Nodo eliminarRec(Nodo nodo, int valor) {
    if (nodo == null) return null;

    if (valor < nodo.valor) {
      nodo.izq = eliminarRec(nodo.izq, valor);
    } else if (valor > nodo.valor) {
      nodo.der = eliminarRec(nodo.der, valor);
    } else {
      if (nodo.izq == null) return nodo.der;
      if (nodo.der == null) return nodo.izq;

      nodo.valor = minimo(nodo.der);
      nodo.der = eliminarRec(nodo.der, nodo.valor);
    }

    return nodo;
  }

  private int minimo(Nodo nodo) {
    while (nodo.izq != null) {
      nodo = nodo.izq;
    }
    return nodo.valor;
  }

  public List<Map<String, Object>> toLista() {
    List<Map<String, Object>> lista = new ArrayList<>();
    toListaRec(raiz, null, lista);
    return lista;
  }

  private void toListaRec(Nodo nodo, Integer padre, List<Map<String, Object>> lista) {
    if (nodo == null) return;

    Map<String, Object> map = new HashMap<>();
    map.put("id", nodo.valor);
    map.put("valor", nodo.valor);
    map.put("padre", padre);
    lista.add(map);

    toListaRec(nodo.izq, nodo.valor, lista);
    toListaRec(nodo.der, nodo.valor, lista);
  }

  public List<int[]> obtenerConexiones() {
    List<int[]> conexiones = new ArrayList<>();
    recorrer(raiz, conexiones);
    return conexiones;
  }

  private void recorrer(Nodo nodo, List<int[]> conexiones) {
    if (nodo == null) return;
    if (nodo.izq != null) {
      conexiones.add(new int[]{nodo.valor, nodo.izq.valor});
      recorrer(nodo.izq, conexiones);
    }
    if (nodo.der != null) {
      conexiones.add(new int[]{nodo.valor, nodo.der.valor});
      recorrer(nodo.der, conexiones);
    }
  }
public boolean buscar(int valor) {
  return buscarRec(raiz, valor);
}
private boolean buscarRec(Nodo nodo, int valor) {
  if (nodo == null) return false;
  if (nodo.valor == valor) return true;
  if (valor < nodo.valor) return buscarRec(nodo.izq, valor);
  else return buscarRec(nodo.der, valor);
}

public Map<String, Object> buscarConCamino(int valor) {
  List<Integer> camino = new ArrayList<>();
  Nodo actual = raiz;
  boolean encontrado = false;

  while (actual != null) {
    camino.add(actual.valor);

    if (valor == actual.valor) {
      encontrado = true;
      break;
    } else if (valor < actual.valor) {
      actual = actual.izq;
    } else {
      actual = actual.der;
    }
  }

  Map<String, Object> resultado = new HashMap<>();
  resultado.put("camino", camino);
  resultado.put("encontrado", encontrado);
  return resultado;
}


}
