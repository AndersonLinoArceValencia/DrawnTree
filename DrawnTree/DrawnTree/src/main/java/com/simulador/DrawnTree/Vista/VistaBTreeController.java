package com.simulador.DrawnTree.Vista;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VistaBTreeController {

  @GetMapping("/btree")
  public String mostrarPaginaBTree() {
    return "btree"; // busca btree.html en /templates
  }
}