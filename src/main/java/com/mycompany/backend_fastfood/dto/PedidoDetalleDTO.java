package com.mycompany.backend_fastfood.dto;

import com.mycompany.backend_fastfood.model.Empleado;
import com.mycompany.backend_fastfood.model.Item;
import com.mycompany.backend_fastfood.model.ItemIngrediente;
import com.mycompany.backend_fastfood.model.Pedido;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Detalle completo de un pedido: ítems, modificaciones y totales.
 * Es lo que ve el cocinero al preparar y el vendedor al consultar.
 *
 * Ejemplo (3 hamburguesas con cheddar extra):
 * {
 *   "id": "PE016",
 *   "estado": "PENDIENTE",
 *   "items": [{
 *     "descripcionProducto": "Hamburguesa con queso",
 *     "cantidad": 3,
 *     "precioVenta": 1500.0,
 *     "modificaciones": [{
 *       "descripcionIngrediente": "Queso cheddar",
 *       "tipo": "AGREGADO",
 *       "cantidadPorUnidad": 1,
 *       "cantidadTotal": 3,
 *       "precioUnitario": 300.0
 *     }],
 *     "subtotal": 5400.0
 *   }],
 *   "total": 5400.0
 * }
 */
@Getter
@AllArgsConstructor
public class PedidoDetalleDTO {

    private String id;
    private LocalDate fecha;
    private String estado;
    private String idVendedor;
    private String nombreVendedor;
    private String idCocinero;
    private String nombreCocinero;
    private List<ItemDetalle> items;
    private double total;

    public static PedidoDetalleDTO desde(Pedido pedido) {
        return new PedidoDetalleDTO(
                pedido.getID(),
                pedido.getFecha(),
                pedido.getEstado() != null
                        ? pedido.getEstado().name()
                        : null,
                idDe(pedido.getEmpleadoVendedor()),
                nombreDe(pedido.getEmpleadoVendedor()),
                idDe(pedido.getEmpleadoCocinero()),
                nombreDe(pedido.getEmpleadoCocinero()),
                pedido.getItems().stream()
                        .map(ItemDetalle::desde)
                        .toList(),
                pedido.calcularTotal()
        );
    }

    private static String idDe(Empleado empleado) {
        return empleado != null ? empleado.getID() : null;
    }

    private static String nombreDe(Empleado empleado) {
        return empleado != null
                ? empleado.getNombre() + " " + empleado.getApellido()
                : null;
    }

    /** Un producto del pedido. */
    @Getter
    @AllArgsConstructor
    public static class ItemDetalle {

        private String id;
        private String idProducto;
        private String descripcionProducto;
        private int cantidad;
        private double precioVenta;
        private List<ModificacionDetalle> modificaciones;
        private double subtotal;

        static ItemDetalle desde(Item item) {
            return new ItemDetalle(
                    item.getID(),
                    item.getProducto() != null
                            ? item.getProducto().getID()
                            : null,
                    item.getDescripcionProducto(),
                    item.getCantidad(),
                    item.getPrecioVenta(),
                    item.getModificaciones().stream()
                            .map(mod -> ModificacionDetalle.desde(
                                    mod, item.getCantidad()))
                            .toList(),
                    item.calcularSubtotal()
            );
        }
    }

    /** Un ingrediente agregado o quitado. */
    @Getter
    @AllArgsConstructor
    public static class ModificacionDetalle {

        private String idIngrediente;
        private String descripcionIngrediente;
        private String tipo;

        /** Lo que se cargó: por cada unidad del ítem. */
        private int cantidadPorUnidad;

        /** Lo que tiene que preparar el cocinero: por unidad × cantidad del ítem. */
        private int cantidadTotal;

        /** Precio congelado al momento de la venta. */
        private double precioUnitario;

        static ModificacionDetalle desde(ItemIngrediente mod, int cantidadItem) {
            return new ModificacionDetalle(
                    mod.getIngrediente() != null
                            ? mod.getIngrediente().getID()
                            : null,
                    mod.getIngrediente() != null
                            ? mod.getIngrediente().getDescripcion()
                            : null,
                    mod.getTipoModificacion() != null
                            ? mod.getTipoModificacion().name()
                            : null,
                    mod.getCantidad(),
                    mod.getCantidad() * cantidadItem,
                    mod.getPrecioUnitario()
            );
        }
    }
}