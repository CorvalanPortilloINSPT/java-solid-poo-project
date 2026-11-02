package com.mycompany.backend_fastfood.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Línea de un pedido: un producto, una cantidad y sus modificaciones.
 *
 * Reglas:
 * - La descripción y el precio del producto se copian al momento de la
 *   venta (si después cambia el producto, el historial no cambia).
 * - Las modificaciones se aplican a CADA unidad del ítem.
 * - Subtotal = cantidad × (precioVenta + costo de agregados por unidad).
 */
@Entity
@Table(name = "item")
public class Item {

    @Id
    private String ID;

    @ManyToOne
    @JoinColumn(name = "pedido_ID")
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "producto_ID")
    private Producto producto;

    @Column(name = "descripcionProducto")
    private String descripcionProducto;

    private int cantidad;

    private double precioVenta;

    /**
     * Las modificaciones se guardan y se borran junto con el ítem.
     * Se usa cuando se elimina un pedido pendiente.
     */
    @OneToMany(
            mappedBy = "item",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<ItemIngrediente> modificaciones = new ArrayList<>();

    protected Item() {
        // Constructor requerido por JPA
    }

    /**
     * Crea el ítem copiando la descripción y el precio actuales
     * del producto (precio congelado).
     */
    public Item(String ID, Pedido pedido, Producto producto, int cantidad) {
        this.ID = ID;
        this.pedido = pedido;
        this.producto = producto;
        this.descripcionProducto = producto.getDescripcion();
        this.precioVenta = producto.getPrecio();
        this.cantidad = cantidad;
    }

    public String getID() {
        return ID;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public Producto getProducto() {
        return producto;
    }

    public String getDescripcionProducto() {
        return descripcionProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getPrecioVenta() {
        return precioVenta;
    }

    /**
     * Lista de solo lectura. Para agregar, usar agregarModificacion().
     * @JsonIgnore evita un bucle infinito al convertir a JSON
     * (Item -> modificaciones -> item -> modificaciones...).
     */
    @JsonIgnore
    public List<ItemIngrediente> getModificaciones() {
        return Collections.unmodifiableList(modificaciones);
    }

    public void agregarModificacion(ItemIngrediente modificacion) {
        modificaciones.add(modificacion);
    }

    /** Costo de los ingredientes agregados para UNA unidad. */
    public double calcularCostoAgregadosPorUnidad() {
        return modificaciones.stream()
                .mapToDouble(ItemIngrediente::calcularCostoPorUnidad)
                .sum();
    }

    /** cantidad × (precio del producto + agregados por unidad). */
    public double calcularSubtotal() {
        return cantidad * (precioVenta + calcularCostoAgregadosPorUnidad());
    }
}