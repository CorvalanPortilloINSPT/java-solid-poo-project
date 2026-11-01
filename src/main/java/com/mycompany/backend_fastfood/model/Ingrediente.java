package com.mycompany.backend_fastfood.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ingrediente")
public class Ingrediente {

    @Id
    private String ID;

    private String descripcion;
    private double precio;
    private boolean activo = true;

    public Ingrediente() {
    }
    
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