package com.example.ConsultaInventario1.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ConsultaService {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private SesionService sesionService;

    public List<Map<String,Object>> consultarInventario(String palabra_Clave) {
        sesionService.estaAutenticado();
        String sql= """
                SELECT
                	c.nombre as Categoria,
                    p.id_producto,
                    p.nombre,
                    p.precio,
                    p.stock_actual
                FROM PRODUCTO p
                INNER JOIN CATEGORIA c ON p.categoria_id = c.id_categoria
                WHERE p.nombre LIKE ?
                   OR c.nombre LIKE ?
                """;
        String filtro="%"+palabra_Clave+"%";
        List<Map<String,Object>> lista= jdbcTemplate.queryForList(sql,filtro,filtro);
        return lista;
    }
}
