package com.mycompany.backend_fastfood.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Producto del menú.
 *
 * JSON (lo que devuelve la API):
 * { "id": "PR001", "descripcion": "Hamburguesa", "precio": 4500.0,
 *   "disponible": true, "activo": true }
 *
 * Para el alta y la modificación se usan solo "descripcion" y "precio":
 * el ID y "disponible" los calcula el sistema.
 */
@Entity
@Table(name = "producto")
public class Producto {

    @Id
    @JsonProperty("id")
    private String ID;

    private String descripcion;
    private double precio;
    private boolean disponible;

    @Column(nullable = false)
    private boolean activo = true;

    protected Producto() {
    }

    /**
     * Producto nuevo, siempre activo.
     *
     * Jackson NO usa este constructor (Mode.DISABLED): al leer un JSON usa
     * el constructor vacío y carga la descripción y el precio con los
     * setters. El producto que se guarda lo arma ProductoService.
     */
    @JsonCreator(mode = JsonCreator.Mode.DISABLED)
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