package com.example.ConsultaInventario1.Rest;

import com.example.ConsultaInventario1.DTO.VentaDTO;
import com.example.ConsultaInventario1.DTO.nuevoProductoDTO;
import com.example.ConsultaInventario1.Service.ProcesoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/Tienda/api/Procesos")
public class registrarRest {

        @Autowired
        private ProcesoService service;
        @PostMapping("/Registrar/Producto")
        public ResponseEntity<?> registrarProducto(@RequestBody nuevoProductoDTO bean){
            try {
                return ResponseEntity.ok(service.registrarProducto(bean));
            } catch (SQLException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ERROR: " +  e.getMessage() );
            }catch(RuntimeException e){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("ERROR: "+e.getMessage());
            }catch(Exception e){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hubo un problema al intentar matricular ,Contacte a soporte."+e.getMessage());
            }
        }
        @PostMapping("/Registrar/Venta")
    public ResponseEntity<?> registrarVenta(@RequestBody List<VentaDTO> bean){
            try{
                service.registrarVenta(bean);
                return ResponseEntity.ok("Se registro la venta con exito");
            }catch(SQLException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ERROR: " +  e.getMessage() );
            }catch(RuntimeException e){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("ERROR: "+e.getMessage());
            }catch(Exception e){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hubo un problema al intentar registrar la venta.Contacte a soporte.");
            }
        }
    }
