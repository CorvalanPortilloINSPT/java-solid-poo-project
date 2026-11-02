package com.mycompany.backend_fastfood.model;

import java.io.Serializable;
import java.util.Objects;

public class ProductoIngredienteId implements Serializable {

    private String producto;
    private String ingrediente;

    public ProductoIngredienteId() {
    }

    public ProductoIngredienteId(String producto, String ingrediente) {
        this.producto = producto;
        this.ingrediente = ingrediente;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getIngrediente() {
        return ingrediente;
    }

    public void setIngrediente(String ingrediente) {
        this.ingrediente = ingrediente;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ProductoIngredienteId)) {
            return false;
        }

        ProductoIngredienteId that = (ProductoIngredienteId) o;

        return Objects.equals(producto, that.producto)
                && Objects.equals(ingrediente, that.ingrediente);
    }

    @Override
    public int hashCode() {
        return Objects.hash(producto, ingrediente);
    }
}