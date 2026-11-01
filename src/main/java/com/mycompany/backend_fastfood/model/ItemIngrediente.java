package com.mycompany.backend_fastfood.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Modificación de un ingrediente en un ítem del pedido.
 *
 * Reglas:
 * - La cantidad es POR UNIDAD del ítem: "cheddar x1" en un ítem de
 *   3 hamburguesas significa 3 cheddar (uno por hamburguesa).
 * - El precio unitario del ingrediente se congela al momento de la
 *   venta, igual que el precio del producto en Item.
 * - Solo los AGREGADO suman al precio; los QUITADO no descuentan.
 */
@Entity
@Table(name = "itemingrediente")
public class ItemIngrediente {

    @Id
    private String ID;

    @ManyToOne
    @JoinColumn(name = "item_ID")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "ingrediente_ID")
    private Ingrediente ingrediente;

    @Enumerated(EnumType.STRING)
    private TipoModificacion tipoModificacion;

    /** Cantidad por unidad del ítem. */
    private int cantidad;

    /** Precio del ingrediente al momento de la venta. */
    private double precioUnitario;

    protected ItemIngrediente() {
        // Constructor requerido por JPA
    }

    /**
     * Crea la modificación y congela el precio actual del ingrediente.
     */
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

    public Item getItem() {
        return item;
    }

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