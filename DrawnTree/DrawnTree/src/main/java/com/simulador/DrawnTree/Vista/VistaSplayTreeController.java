package com.simulador.DrawnTree.Vista;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class VistaSplayTreeController {
     @GetMapping("/Splay-Tree")
  public String mostrarPaginaBTree() {
    return "Splay-Tree"; // busca btree.html en /templates
  }
}
