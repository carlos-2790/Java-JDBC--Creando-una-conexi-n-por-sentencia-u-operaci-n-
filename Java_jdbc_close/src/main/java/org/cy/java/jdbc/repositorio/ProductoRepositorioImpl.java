package org.cy.java.jdbc.repositorio;

import org.cy.java.jdbc.modelo.Categoria;
import org.cy.java.jdbc.modelo.Producto;
import org.cy.java.jdbc.util.ConexionBasedeDatos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoRepositorioImpl implements  Repositorio<Producto>{

    private Connection getConnection() throws SQLException {
        return ConexionBasedeDatos.getInstance();
    }


    @Override
    public List<Producto> listar() {

        List<Producto> productos = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT p.*, c.nombre AS categoria FROM productos AS p inner join categorias AS c ON (p.categoriaId = c.id)")) {
            while (rs.next()) {
                Producto p = crearPoducto(rs);
                productos.add(p);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return productos;
    }


    @Override
    public Producto porId(Long id) {
        Producto producto = null;
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT p.*, c.nombre AS categoria FROM productos AS p inner join categorias AS c ON (p.categoriaId = c.id) WHERE p.id = ?")) {

            stmt.setLong(1, id);   //asignamos el parametro del id
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    producto = crearPoducto(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return producto;
    }

    @Override
    public void guardar(Producto producto) {

        String sql;
        if (producto.getId() != null && producto.getId() > 0) {  // si el id es mayor a 0 es un update
            sql = "UPDATE productos SET nombre =?, precio=? , categoriaId=? WHERE id=?";
        } else {
            sql = "INSERT INTO productos(nombre,precio,categoriaId,fecha_registro)VALUES(?,?,?,?)";
        }
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, producto.getNombre());
            stmt.setLong(2, producto.getPrecio());
            stmt.setLong(3, producto.getCategoria().getId());

            if (producto.getId() != null && producto.getId() > 0) { // si el id es mayor a 0 es un update, por lo tanto setea el id sino hace un inserte de date en un nuevo registro
                stmt.setLong(4, producto.getId());
            } else {
                stmt.setDate(4, new Date(producto.getFechaRegistro().getTime()));
            }
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void eliminar(Long id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM productos WHERE id = ?")) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static Producto crearPoducto(ResultSet rs) throws SQLException {
        Producto p = new Producto();
        p.setId(rs.getLong("id"));
        p.setNombre(rs.getString("nombre"));
        p.setPrecio(rs.getInt("precio"));
        p.setFechaRegistro(rs.getDate("fecha_registro"));
        Categoria categoria = new Categoria();
        categoria.setId(rs.getLong("categoriaId"));
        categoria.setNombre(rs.getString("categoria"));
        p.setCategoria(categoria);
        return p;
    }
}
