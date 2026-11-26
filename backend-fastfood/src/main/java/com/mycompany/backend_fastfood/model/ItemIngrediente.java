package com.mycompany.backend_fastfood.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * Modificación de un ingrediente en un ítem del pedido.
 *
 * Reglas:
 * - La cantidad es POR UNIDAD del ítem: "cheddar x1" en un ítem de
 *   3 hamburguesas significa 3 cheddar (uno por hamburguesa).
 * - El precio unitario del ingrediente se congela al momento de la
 *   venta, igual que el precio del producto en Item.
 * - Solo los AGREGADO suman al precio; los QUITADO no descuentan.
 *
 * JSON (lo que devuelve la API):
 * { "id": "II0031", "idIngrediente": "ING02",
 *   "descripcionIngrediente": "Queso cheddar", "tipo": "AGREGADO",
 *   "cantidadPorUnidad": 1, "cantidadTotal": 3, "precioUnitario": 300.0 }
 *
 * JSON de una modificación al registrar un pedido:
 * { "idIngrediente": "ING02", "tipo": "AGREGADO", "cantidad": 1 }
 */
@Entity
@Table(name = "itemingrediente")
public class ItemIngrediente {

    @Id
    @JsonProperty("id")
    private String ID;

    @ManyToOne
    @JoinColumn(name = "item_ID")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "ingrediente_ID")
    private Ingrediente ingrediente;

    /** En el JSON se llama "tipo" (al recibir y al devolver). */
    @Enumerated(EnumType.STRING)
    @JsonProperty("tipo")
    private TipoModificacion tipoModificacion;

    /**
     * Cantidad por unidad del ítem.
     * En la respuesta se llama "cantidadPorUnidad", para distinguirla de
     * "cantidadTotal"; al registrar un pedido llega como "cantidad"
     * (@JsonAlias acepta ese otro nombre al leer el JSON).
     */
    @JsonProperty("cantidadPorUnidad")
    @JsonAlias("cantidad")
    private int cantidad;

    /** Precio del ingrediente al momento de la venta. */
    private double precioUnitario;

    /**
     * Solo para RECIBIR el JSON de un pedido nuevo, que trae el ID del
     * ingrediente. @Transient: no es una columna de la tabla.
     * El service busca el ingrediente real con este ID.
     */
    @Transient
    @JsonProperty("idIngrediente")
    private String idIngrediente;

    protected ItemIngrediente() {
        // Constructor requerido por JPA (y por Jackson al leer un JSON)
    }

    /**
     * Crea la modificación y congela el precio actual del ingrediente.
     *
     * Jackson NO usa este constructor (Mode.DISABLED): al leer un JSON usa
     * el constructor vacío y carga los atributos uno por uno. Si lo usara,
     * le pasaría ingrediente = null y fallaría en ingrediente.getPrecio().
     */
    @JsonCreator(mode = JsonCreator.Mode.DISABLED)
    public ItemIngrediente(
            String ID,
            Item item,
            Ingrediente ingrediente,
            TipoModificacion tipoModificacion,
            int cantidad) {

        this.ID = ID;
        this.item = item;
        this.ingrediente = ingrediente;
        this.tipoModificacion = tipoModificacion;
        this.cantidad = cantidad;
        this.precioUnitario = ingrediente.getPrecio();
    }

    public String getID() {
        return ID;
    }

    /** @JsonIgnore corta el ciclo Item → ItemIngrediente → Item en el JSON. */
    @JsonIgnore
    public Item getItem() {
        return item;
    }

    /** En el JSON se muestran solo el ID y la descripción (ver abajo). */
    @JsonIgnore
    public Ingrediente getIngrediente() {
        return ingrediente;
    }

    public TipoModificacion getTipoModificacion() {
        return tipoModificacion;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    /**
     * Si la modificación ya está guardada, devuelve el ID del ingrediente;
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

    /**
     * Lo que tiene que preparar el cocinero en total:
     * cantidad por unidad × cantidad del ítem.
     * Sale en el JSON como "cantidadTotal".
     */
    @JsonProperty("cantidadTotal")
    public int calcularCantidadTotal() {
        if (item == null) {
            return cantidad;
        }
        return cantidad * item.getCantidad();
    }

    /**
     * Costo adicional de esta modificación para UNA unidad del ítem.
     * AGREGADO: precio congelado × cantidad. QUITADO: 0.
     */
    public double calcularCostoPorUnidad() {
        if (tipoModificacion == TipoModificacion.AGREGADO) {
            return precioUnitario * cantidad;
        }

        return 0;
    }
}