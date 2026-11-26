package com.mycompany.backend_fastfood.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

/**
 * Línea de un pedido: un producto, una cantidad y sus modificaciones.
 *
 * Reglas:
 * - La descripción y el precio del producto se copian al momento de la
 *   venta (si después cambia el producto, el historial no cambia).
 * - Las modificaciones se aplican a CADA unidad del ítem.
 * - Subtotal = cantidad × (precioVenta + costo de agregados por unidad).
 *
 * JSON (lo que devuelve la API):
 * { "id": "IT020", "idProducto": "PR001",
 *   "descripcionProducto": "Hamburguesa con queso", "cantidad": 3,
 *   "precioVenta": 1500.0, "modificaciones": [ ... ], "subtotal": 5400.0 }
 *
 * JSON de un ítem al registrar un pedido:
 * { "idProducto": "PR001", "cantidad": 3, "modificaciones": [ ... ] }
 */
@Entity
@Table(name = "item")
public class Item {

    @Id
    @JsonProperty("id")
    private String ID;

    @ManyToOne
    @JoinColumn(name = "pedido_ID")
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "producto_ID")
    private Producto producto;

    @Column(name = "descripcionProducto")
    private String descripcionProducto;

    @JsonProperty("cantidad")
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
    @JsonProperty("modificaciones")
    private List<ItemIngrediente> modificaciones = new ArrayList<>();

    /**
     * Solo para RECIBIR el JSON de un pedido nuevo, que trae el ID del
     * producto. @Transient: no es una columna de la tabla.
     * El service busca el producto real con este ID.
     */
    @Transient
    @JsonProperty("idProducto")
    private String idProducto;

    protected Item() {
        // Constructor requerido por JPA (y por Jackson al leer un JSON)
    }

    /**
     * Crea el ítem copiando la descripción y el precio actuales
     * del producto (precio congelado).
     *
     * Jackson NO usa este constructor (Mode.DISABLED): al leer un JSON usa
     * el constructor vacío y carga los atributos uno por uno. Si lo usara,
     * le pasaría producto = null y fallaría en producto.getDescripcion().
     */
    @JsonCreator(mode = JsonCreator.Mode.DISABLED)
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

    /** @JsonIgnore corta el ciclo Pedido → Item → Pedido en el JSON. */
    @JsonIgnore
    public Pedido getPedido() {
        return pedido;
    }

    /**
     * En el JSON se muestra solo el ID del producto: la descripción y el
     * precio que importan son los congelados al momento de la venta.
     */
    @JsonIgnore
    public Producto getProducto() {
        return producto;
    }

    /**
     * Si el ítem ya está guardado, devuelve el ID del producto;
     * si viene de un JSON recibido, el ID que llegó.
     */
    @JsonProperty("idProducto")
    public String getIdProducto() {
        if (producto == null) {
            return idProducto;
        }
        return producto.getID();
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
     * Devuelve una COPIA de la lista (copia defensiva): quien la reciba
     * puede recorrerla, pero si la modifica no cambia el ítem.
     * Para agregar una modificación se usa agregarModificacion().
     * Sale en el JSON; el ciclo Item → ItemIngrediente → Item
     * se corta con el @JsonIgnore de ItemIngrediente.getItem().
     */
    public List<ItemIngrediente> getModificaciones() {
        if (modificaciones == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(modificaciones);
    }

    public void agregarModificacion(ItemIngrediente modificacion) {
        modificaciones.add(modificacion);
    }

    /** Costo de los ingredientes agregados para UNA unidad. */
    public double calcularCostoAgregadosPorUnidad() {
        double costo = 0;

        for (ItemIngrediente modificacion : modificaciones) {
            costo += modificacion.calcularCostoPorUnidad();
        }

        return costo;
    }

    /**
     * cantidad × (precio del producto + agregados por unidad).
     * Sale en el JSON como "subtotal".
     */
    @JsonProperty("subtotal")
    public double calcularSubtotal() {
        return cantidad * (precioVenta + calcularCostoAgregadosPorUnidad());
    }
}