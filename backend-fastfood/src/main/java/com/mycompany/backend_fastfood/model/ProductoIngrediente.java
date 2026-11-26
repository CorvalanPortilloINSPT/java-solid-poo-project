package com.mycompany.backend_fastfood.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * Receta de un producto: qué ingredientes lleva y cuáles son obligatorios.
 * La clave es el par (producto, ingrediente), así que no puede repetirse.
 *
 * JSON (lo que devuelve la API, una fila de la receta):
 * { "idProducto": "PR001", "descripcionProducto": "Hamburguesa",
 *   "idIngrediente": "ING02", "descripcionIngrediente": "Queso cheddar",
 *   "obligatorio": false }
 *
 * JSON para agregar un ingrediente a la receta
 * (POST /api/productos/{id}/ingredientes):
 * { "idIngrediente": "ING02", "obligatorio": false }
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

    @JsonProperty("obligatorio")
    private boolean obligatorio;

    /**
     * Solo para RECIBIR el JSON de "agregar a la receta", que trae el ID
     * del ingrediente. @Transient: no es una columna de la tabla.
     * El service busca el ingrediente real con este ID.
     */
    @Transient
    @JsonProperty("idIngrediente")
    private String idIngrediente;

    protected ProductoIngrediente() {
        // Constructor requerido por JPA (y por Jackson al leer un JSON)
    }

    /**
     * Relación nueva entre un producto y un ingrediente de su receta.
     *
     * Jackson NO usa este constructor (Mode.DISABLED): al leer un JSON usa
     * el constructor vacío y carga "idIngrediente" y "obligatorio". La
     * relación que se guarda la arma ProductoService con este constructor.
     */
    @JsonCreator(mode = JsonCreator.Mode.DISABLED)
    public ProductoIngrediente(
            Producto producto,
            Ingrediente ingrediente,
            boolean obligatorio) {

        this.producto = producto;
        this.ingrediente = ingrediente;
        this.obligatorio = obligatorio;
    }

    /** En el JSON se muestran solo el ID y la descripción (ver abajo). */
    @JsonIgnore
    public Producto getProducto() {
        return producto;
    }

    @JsonIgnore
    public Ingrediente getIngrediente() {
        return ingrediente;
    }

    public boolean isObligatorio() {
        return obligatorio;
    }

    // ------------------------------------------------------------------
    // Datos que se muestran en el JSON
    // ------------------------------------------------------------------

    @JsonProperty("idProducto")
    public String getIdProducto() {
        if (producto == null) {
            return null;
        }
        return producto.getID();
    }

    @JsonProperty("descripcionProducto")
    public String getDescripcionProducto() {
        if (producto == null) {
            return null;
        }
        return producto.getDescripcion();
    }

    /**
     * Si la relación ya está guardada, devuelve el ID del ingrediente;
     * si viene de un JSON recibido, el ID que llegó.
     */
    @JsonProperty("idIngrediente")
    public String getIdIngrediente() {
        if (ingrediente == null) {
            return idIngrediente;
        }
        return ingrediente.getID();
    }

    @JsonProperty("descripcionIngrediente")
    public String getDescripcionIngrediente() {
        if (ingrediente == null) {
            return null;
        }
        return ingrediente.getDescripcion();
    }
}