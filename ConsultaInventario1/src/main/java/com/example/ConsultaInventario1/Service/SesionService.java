package com.example.ConsultaInventario1.Service;

import com.example.ConsultaInventario1.Sesion.SesionDatos;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Service
public class SesionService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private HttpSession httpSession;

    public boolean login(String usuario, String clave) {
        try {
            String sql = "SELECT id_usuario, rol FROM usuario WHERE username = ? AND clave = ?";
            Map<String, Object> rec = jdbcTemplate.queryForMap(sql, usuario, clave);

            // Si se encontró el usuario
            SesionDatos sesion = new SesionDatos();
            sesion.setIdUsuario(Integer.parseInt(rec.get("id_usuario").toString()));
            sesion.setUsuario(usuario);
            sesion.setCargo(rec.get("rol").toString());
            sesion.setAutenticado(true);

            // Guardamos en la sesión HTTP
            httpSession.setAttribute("usuarioSesion", sesion);

            return true;
        } catch (Exception e) {
            // Si no se encuentra el usuario o hay error
            return false;
        }
    }

    public void logout() {
        httpSession.invalidate(); // Elimina toda la sesión
    }

    public SesionDatos getSesion() {
        return (SesionDatos) httpSession.getAttribute("usuarioSesion");
    }

    public void estaAutenticado() {
        SesionDatos sesion = (SesionDatos) httpSession.getAttribute("usuarioSesion");
        if (sesion == null || !sesion.isAutenticado()) {
            throw new RuntimeException("Debe iniciar sesión para acceder a este recurso");
        }
    }
    public void validarsesionyrol(String rolRequerido) throws SQLException {
        SesionDatos sesion = getSesion();

        // Verifica si hay sesión
        if (sesion == null || !sesion.isAutenticado()) {
            throw new RuntimeException("Debe iniciar sesión para acceder a este recurso");
        }

        // Verifica si el rol coincide (si se especificó uno)
        if (rolRequerido != null && !sesion.getCargo().equals(rolRequerido) && !sesion.getCargo().equals("GERENTE")) {
            throw new RuntimeException("No tiene permisos suficientes para esta acción");
        }
    }
}
