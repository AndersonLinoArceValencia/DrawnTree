package com.simulador.DrawnTree.Vista;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class VistaAVLTreeController {
     @GetMapping("/avl")
  public String mostrarPaginaAVL() {
    return "avl"; // busca btree.html en /templates
  }
}
