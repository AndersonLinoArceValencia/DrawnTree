package com.simulador.DrawnTree.Vista;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VistaIndexController {
    
      @GetMapping("/index")
  public String mostrarPaginaBTree() {
    return "index"; // busca index.html en /templates
  }
}
