package com.simulador.DrawnTree.Controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {

  @GetMapping("/")
  public String inicio() {
    return "index"; // devuelve el HTML ubicado en templates/index.html
  }
}