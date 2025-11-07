package com.example.ConsultaInventario1.Sesion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SesionDatos implements Serializable {
    private int idUsuario;
    private String Usuario;
    private String clave;
    private boolean autenticado;
    private String cargo;
}