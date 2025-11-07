package com.example.ConsultaInventario1.Service;

import com.example.ConsultaInventario1.DTO.VentaDTO;
import com.example.ConsultaInventario1.DTO.nuevoProductoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Service
public class ProcesoService {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    private SesionService sesionService;
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public nuevoProductoDTO registrarProducto(nuevoProductoDTO bean) throws SQLException {
        sesionService.validarsesionyrol("Administrador");
        // Validaciones
        validarCategoria(bean.getIdCategoria());
        validarPrecio(bean.getPrecio());
        validarStock(bean.getStock());

        // Generar código según categoría
        String codigo = generarCodigo(bean.getIdCategoria());
        bean.setCodigo(codigo);
        // Registrar en la base de datos
        registrarNuevoProducto(bean);
        return bean;
    }

    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    public void registrarNuevoProducto(nuevoProductoDTO bean) {
        String sql = """
            INSERT INTO PRODUCTO(nombre, codigo, categoria_id, precio, stock_actual, stock_minimo)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        Object[] datos = {
                bean.getNombProducto(),
                bean.getCodigo(),
                bean.getIdCategoria(),
                bean.getPrecio(),
                bean.getStock(),
                bean.getStockMinimo()
        };
        jdbcTemplate.update(sql, datos);
    }

    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    public void validarStock(int stock) throws SQLException {
        if (stock <= 0) {
            throw new SQLException("Cantidad inválida.");
        }
    }

    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    public void validarPrecio(double precio) throws SQLException {
        if (precio <= 0) {
            throw new SQLException("Precio inválido.");
        }
    }

    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    public String generarCodigo(int idCategoria) {
        // Contar cuántos productos existen en esa categoría
        String sql = "SELECT COUNT(1) FROM PRODUCTO WHERE categoria_id = ?";
        int contador = jdbcTemplate.queryForObject(sql, Integer.class, idCategoria) + 2;

        // Generar letra según el idCategoria → 1=A, 2=B, 3=C, ...
        char letra = (char) ('A' + (idCategoria - 1));

        // Formatear tipo A-001, B-002, etc.
        String codigo = String.format("%c-%03d", letra, contador);
        return codigo;
    }

    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    public void validarCategoria(int idCategoria) throws SQLException {
        String sql = "SELECT COUNT(1) FROM CATEGORIA WHERE id_categoria = ?";
        int existe = jdbcTemplate.queryForObject(sql, Integer.class, idCategoria);
        if (existe == 0) {
            throw new SQLException("Categoría no existente.");
        }
    }
    //-------------------------------------------------------
    //PROCESO 2
    //-------------------------------------------------------
    private static final double IGV = 0.18; // 18% de impuesto
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void registrarVenta(List<VentaDTO> venta) throws SQLException {
        sesionService.estaAutenticado();
        // Validar stock y obtener total
        double total = validarStockycalcularTotal(venta);

        // Calcular impuesto y total
        double impuesto = calcularImpuesto(total);
        double subtotal=total-impuesto;

        int idVenta = insertarVenta(sesionService.getSesion().getIdUsuario(), subtotal, impuesto, total);

        //  Insertar detalle y actualizar stock
        registrarDetalleYActualizarStock(idVenta, venta);
    }

    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    public double validarStockycalcularTotal(List<VentaDTO> venta) throws SQLException {
        double total = 0.0;
        for (VentaDTO d : venta) {
            Map<String, Object> producto = jdbcTemplate.queryForMap(
                    "SELECT precio, stock_actual FROM PRODUCTO WHERE id_producto = ?",
                    d.getIdProducto()
            );

            double precioUnitario = Double.parseDouble(producto.get("precio").toString());
            int stockActual = Integer.parseInt(producto.get("stock_actual").toString());
            validarStockVenta(stockActual,d.getCantidad(), d.getIdProducto());
            total += precioUnitario * d.getCantidad();
        }
        return total;
    }
    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    public double calcularImpuesto(double subtotal) {
        return subtotal * IGV;
    }
    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    public int insertarVenta(int idUsuario, double subtotal, double impuesto, double total) {
        /*
        String sql = """
            INSERT INTO VENTA(usuario_id, total, subtotal, impuesto)
            OUTPUT INSERTED.id_venta
            VALUES (?, ?, ?, ?)
        """;*/
        String sql= """
                            INSERT INTO VENTA(usuario_id,fecha, total)
                            OUTPUT INSERTED.id_venta
                            VALUES (?, GETDATE(), ?)
                    """;
        /*return jdbcTemplate.queryForObject(sql, new Object[]{idUsuario, total, subtotal, impuesto}, Integer.class);*/
        return jdbcTemplate.queryForObject(sql, new Object[]{idUsuario, total}, Integer.class);
    }
    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    public void registrarDetalleYActualizarStock(int idVenta, List<VentaDTO> detalles) {
        for (VentaDTO d : detalles) {
            String sqlprecio="SELECT precio FROM PRODUCTO WHERE id_producto= ?";
            double precioUnitario = jdbcTemplate.queryForObject(sqlprecio,Double.class,d.getIdProducto());

            // Insertar detalle
            String sqlventadetalle="INSERT INTO VENTA_DETALLE(venta_id, producto_id, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(sqlventadetalle,idVenta, d.getIdProducto(), d.getCantidad(), precioUnitario
            );

            // Actualizar stock
            String sqlactualizarstock="UPDATE PRODUCTO SET stock_actual = stock_actual - ? WHERE id_producto = ?";
            jdbcTemplate.update(sqlactualizarstock,d.getCantidad(), d.getIdProducto()
            );
        }
    }
    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    public void validarStockVenta(int stockActual, int cantidad, int idProducto) throws SQLException {
        if (stockActual <= 0) {
            throw new SQLException("Cantidad no válida");
        }
        if (stockActual < cantidad) {
            throw new SQLException("Stock insuficiente para el producto con ID " + idProducto);
        }
    }
}