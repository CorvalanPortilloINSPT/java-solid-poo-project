package com.mycompany.backend_fastfood.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import java.util.Collections;
import java.util.List;

/**
 * Pedido tomado por un vendedor y despachado por un cocinero.
 *
 * Reglas:
 * - Se crea con la fecha del día, estado PENDIENTE y sin cocinero.
 * - El único cambio de estado posible es PENDIENTE -> DESPACHADO,
 *   y en ese momento queda asignado el cocinero.
 * - Total = suma de los subtotales de sus ítems.
 */
@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
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
     */
    @OneToMany(
            mappedBy = "pedido",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Item> items = new ArrayList<>();

    protected Pedido() {
        // Constructor requerido por JPA
    }

    /** Pedido nuevo: PENDIENTE y sin cocinero. */
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

    public Empleado getEmpleadoVendedor() {
        return empleadoVendedor;
    }

    public Empleado getEmpleadoCocinero() {
        return empleadoCocinero;
    }

    /**
     * Lista de solo lectura. Para agregar, usar agregarItem().
     * @JsonIgnore evita un bucle infinito al convertir a JSON
     * (Pedido -> items -> pedido -> items...).
     */
    @JsonIgnore
    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void agregarItem(Item item) {
        items.add(item);
    }

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

    public double calcularTotal() {
        return items.stream()
                .mapToDouble(Item::calcularSubtotal)
                .sum();
    }
}