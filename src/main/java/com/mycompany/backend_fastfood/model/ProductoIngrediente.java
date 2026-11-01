package com.mycompany.backend_fastfood.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Receta de un producto: qué ingredientes lleva y cuáles son obligatorios.
 * La clave es el par (producto, ingrediente), así que no puede repetirse.
 */
@Entity
@Table(name = "productoingrediente")
@IdClass(ProductoIngredienteId.class)
public class ProductoIngrediente {

    @Id
    @ManyToOne
    @JoinColumn(name = "producto_ID")
    private Producto producto;

    @Id
    @ManyToOne
    @JoinColumn(name = "ingrediente_ID")
    private Ingrediente ingrediente;

    private boolean obligatorio;

    protected ProductoIngrediente() {
        // Constructor requerido por JPA
    }

    public ProductoIngrediente(
            Producto producto,
            Ingrediente ingrediente,
            boolean obligatorio) {

        this.producto = producto;
        this.ingrediente = ingrediente;
        this.obligatorio = obligatorio;
    }

    public Producto getProducto() {
        return producto;
    }

    public Ingrediente getIngrediente() {
        return ingrediente;
    }

    public boolean isObligatorio() {
        return obligatorio;
    }

    public String getIDProducto() {
        return producto != null ? producto.getID() : null;
    }

    public String getIDIngrediente() {
        return ingrediente != null ? ingrediente.getID() : null;
    }
}