
package com.mycompany.backend_fastfood.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "producto")
public class Producto {

    @Id
    private String ID;

    private String descripcion;

    private double precio;

    private boolean disponible;

    @Column(nullable = false)
    private boolean activo = true;

    protected Producto() {
    }

    public Producto(
            String ID,
            String descripcion,
            double precio,
            boolean disponible) {
        this.ID = ID;
        this.descripcion = descripcion;
        this.precio = precio;
        this.disponible = disponible;
        this.activo = true;
    }

    public String getID() {
        return ID;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
