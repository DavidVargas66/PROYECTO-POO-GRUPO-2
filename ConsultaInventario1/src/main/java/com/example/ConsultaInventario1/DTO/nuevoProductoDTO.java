package com.example.ConsultaInventario1.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class nuevoProductoDTO {
private int idCategoria;
private double precio;
private int stock;
private String codigo;
private String nombProducto;
private int stockMinimo;
}
