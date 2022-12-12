package org.cy.java.jdbc;

import org.cy.java.jdbc.modelo.Producto;
import org.cy.java.jdbc.repositorio.ProductoRepositorioImpl;
import org.cy.java.jdbc.repositorio.Repositorio;
import org.cy.java.jdbc.util.ConexionBasedeDatos;

import java.sql.Connection;
import java.sql.SQLException;

public class EjemploJdbcDelete {
    public static void main(String[] args) {

            Repositorio<Producto>repositorio = new ProductoRepositorioImpl();

            System.out.println("==================== Listar ====================");
            repositorio.listar().forEach(System.out::println);// lista la completa

            System.out.println("==================== Obtener por Id ====================");
            System.out.println(repositorio.porId(2L));// lista solo el Id 2

            System.out.println("==================== Eliminar Producto ====================");

            repositorio.eliminar(3L);
            System.out.println("Producto eliminado con éxito");
            repositorio.listar().forEach(System.out::println);// lista la completa

    }
}

