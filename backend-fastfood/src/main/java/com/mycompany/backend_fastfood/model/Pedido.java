package com.mycompany.backend_fastfood.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Pedido tomado por un vendedor y despachado por un cocinero.
 *
 * Reglas:
 * - Se crea con la fecha del día, estado PENDIENTE y sin cocinero.
 * - El único cambio de estado posible es PENDIENTE a DESPACHADO,
 *   y en ese momento queda asignado el cocinero.
 * - Total = suma de los subtotales de sus ítems. No se guarda en la
 *   base: lo calcula el backend y sale en el JSON como "total".
 *
 * JSON (lo que devuelve la API):
 * {
 *   "id": "PE016", "fecha": "2026-11-08", "estado": "PENDIENTE",
 *   "idVendedor": "E0003", "nombreVendedor": "Ratón Pérez",
 *   "idCocinero": null, "nombreCocinero": null,
 *   "items": [ ... ver Item ... ],
 *   "total": 5400.0
 * }
 *
 * JSON para registrar un pedido (POST /api/pedidos): solo "items".
 * El ID, la fecha, el estado y el vendedor los define el sistema.
 */
@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @JsonProperty("id")
    private String ID;

    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    private EstadoPedido estado;

    @ManyToOne
    @JoinColumn(name = "empleadoVendedor_ID")
    private Empleado empleadoVendedor;

    @ManyToOne
    @JoinColumn(name = "empleadoCocinero_ID")
    private Empleado empleadoCocinero;

    /**
     * Los ítems (y sus modificaciones) se guardan y se borran
     * junto con el pedido, en una sola operación.
     *
     * @JsonProperty en el atributo: al registrar un pedido, Jackson
     * carga acá los ítems que llegan en el JSON.
     */
    @OneToMany(
            mappedBy = "pedido",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JsonProperty("items")
    private List<Item> items = new ArrayList<>();

    protected Pedido() {
        // Constructor requerido por JPA (y por Jackson al leer un JSON)
    }

    /**
     * Pedido nuevo: PENDIENTE y sin cocinero.
     *
     * Jackson NO usa este constructor (Mode.DISABLED): al leer un JSON usa
     * el constructor vacío y carga solo los ítems. El pedido que se guarda
     * lo arma PedidoService con este constructor, con el vendedor real.
     */
    @JsonCreator(mode = JsonCreator.Mode.DISABLED)
    public Pedido(String ID, LocalDate fecha, Empleado empleadoVendedor) {
        this.ID = ID;
        this.fecha = fecha;
        this.empleadoVendedor = empleadoVendedor;
        this.estado = EstadoPedido.PENDIENTE;
        this.empleadoCocinero = null;
    }

    public String getID() {
        return ID;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    /** En el JSON se muestran solo el ID y el nombre (ver abajo). */
    @JsonIgnore
    public Empleado getEmpleadoVendedor() {
        return empleadoVendedor;
    }

    @JsonIgnore
    public Empleado getEmpleadoCocinero() {
        return empleadoCocinero;
    }

    /**
     * Devuelve una COPIA de la lista (copia defensiva): quien la reciba
     * puede recorrerla, pero si la modifica no cambia el pedido.
     * Para agregar un ítem se usa agregarItem().
     * Sale en el JSON con sus ítems; el ciclo Pedido → Item → Pedido
     * se corta con el @JsonIgnore de Item.getPedido().
     */
    public List<Item> getItems() {
        if (items == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(items);
    }

    public void agregarItem(Item item) {
        items.add(item);
    }

    @JsonIgnore
    public boolean isPendiente() {
        return estado == EstadoPedido.PENDIENTE;
    }

    /**
     * Pasa el pedido a DESPACHADO y asigna el cocinero.
     * El service valida antes con mensajes de negocio; esta
     * comprobación protege la entidad si alguien la usa sin pasar
     * por el service.
     */
    public void despachar(Empleado cocinero) {
        if (!isPendiente()) {
            throw new IllegalStateException(
                    "Solo se puede despachar un pedido PENDIENTE");
        }

        this.estado = EstadoPedido.DESPACHADO;
        this.empleadoCocinero = cocinero;
    }

    /** Suma de los subtotales de los ítems. Sale en el JSON como "total". */
    @JsonProperty("total")
    public double calcularTotal() {
        double total = 0;

        for (Item item : items) {
            total += item.calcularSubtotal();
        }

        return total;
    }

    // ------------------------------------------------------------------
    // Datos de los empleados que se muestran en el JSON
    // ------------------------------------------------------------------

    @JsonProperty("idVendedor")
    public String getIdVendedor() {
        return idDe(empleadoVendedor);
    }

    @JsonProperty("nombreVendedor")
    public String getNombreVendedor() {
        return nombreDe(empleadoVendedor);
    }

    @JsonProperty("idCocinero")
    public String getIdCocinero() {
        return idDe(empleadoCocinero);
    }

    @JsonProperty("nombreCocinero")
    public String getNombreCocinero() {
        return nombreDe(empleadoCocinero);
    }

    private static String idDe(Empleado empleado) {
        if (empleado == null) {
            return null;
        }
        return empleado.getID();
    }

    private static String nombreDe(Empleado empleado) {
        if (empleado == null) {
            return null;
        }
        return empleado.getNombre() + " " + empleado.getApellido();
    }
}