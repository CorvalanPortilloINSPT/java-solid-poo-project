package com.mycompany.backend_fastfood.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Ingrediente con su precio actual.
 *
 * JSON (lo que devuelve la API):
 * { "id": "ING01", "descripcion": "Pan", "precio": 300.0, "activo": true }
 *
 * Para el alta y la modificación se usan solo "descripcion" y "precio":
 * el ID lo genera el sistema y "activo" cambia solo con la baja lógica.
 */
@Entity
@Table(name = "ingrediente")
public class Ingrediente {

    @Id
    @JsonProperty("id")
    private String ID;

    private String descripcion;
    private double precio;
    private boolean activo = true;

    public Ingrediente() {
    }

    /**
     * Ingrediente nuevo, siempre activo.
     *
     * Jackson NO usa este constructor (Mode.DISABLED): al leer un JSON usa
     * el constructor vacío y carga la descripción y el precio con los
     * setters. El ingrediente que se guarda lo arma IngredienteService.
     */
    @JsonCreator(mode = JsonCreator.Mode.DISABLED)
    public Ingrediente(String ID, String descripcion, double precio) {
        this.ID = ID;
        this.descripcion = descripcion;
        this.precio = precio;
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

    public boolean isActivo() {
        return activo;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}