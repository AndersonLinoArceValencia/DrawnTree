package com.simulador.DrawnTree.Vista;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class VistaBinaryTreeController {
     @GetMapping("/binarytree")
  public String mostrarPaginaBinaryTree() {
    return "binarytree"; // busca btree.html en /templates
  }
}
