package com.example.ConsultaInventario1.Rest;

import com.example.ConsultaInventario1.Service.ConsultaService;
import com.example.ConsultaInventario1.Service.SesionService;
import com.example.ConsultaInventario1.Sesion.SesionDatos;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Tienda/api")
public class ConsultaRest {
    @Autowired
    private ConsultaService consultaService;

    @Autowired
    private SesionService sesionService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody SesionDatos log) {
        boolean ok = sesionService.login(log.getUsuario(), log.getClave());
        if (ok) {
            SesionDatos sesion = sesionService.getSesion();
            return ResponseEntity.ok("Inicio de sesión exitoso. Bienvenido " + sesion.getUsuario() + "!");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Sesión cerrada correctamente");
    }

    @GetMapping("/inventario/{palabra_Clave}")
    public ResponseEntity<?> listaInventario(@PathVariable String palabra_Clave){
        try{
            List<Map<String,Object>> lista=consultaService.consultarInventario(palabra_Clave);
            return ResponseEntity.ok(lista);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hubo un problema al intentar registrar la venta.Contacte a soporte.");
        }
    }
 }
