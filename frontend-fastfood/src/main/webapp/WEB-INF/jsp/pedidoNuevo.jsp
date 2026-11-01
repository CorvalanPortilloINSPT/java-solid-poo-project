<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <%@ include file="/WEB-INF/jsp/fragmentos/head.jspf" %>
    <title>Nuevo pedido</title>
</head>
<body>
<%@ include file="/WEB-INF/jsp/fragmentos/encabezado.jspf" %>
<main class="contenido">

<h1>Nuevo pedido</h1>

<%-- Mensajes (Post/Redirect/Get) --%>
<c:if test="${not empty exito}">
    <p class="exito"><c:out value="${exito}"/></p>
</c:if>
<c:if test="${not empty error}">
    <p class="error"><c:out value="${error}"/></p>
</c:if>

<%-- ============================================================ --%>
<%-- Agregar producto                                              --%>
<%-- ============================================================ --%>
<h2>Agregar producto</h2>

<c:choose>
    <c:when test="${empty productos}">
        <p>No hay productos disponibles.</p>
    </c:when>
    <c:otherwise>
        <form class="en-linea" method="post" action="${ctx}/pedidos/nuevo/items">
            <label>Producto
                <select name="idProducto" required>
                    <c:forEach var="producto" items="${productos}">
                        <option value="${producto.id}">
                            <c:out value="${producto.descripcion}"/>
                            - $<fmt:formatNumber value="${producto.precio}" pattern="#,##0.00"/>
                        </option>
                    </c:forEach>
                </select>
            </label>
            <label>Cantidad
                <input type="number" name="cantidad" value="1" min="1" required>
            </label>
            <button type="submit">Agregar</button>
        </form>
    </c:otherwise>
</c:choose>

<%-- ============================================================ --%>
<%-- Pedido en armado                                              --%>
<%-- ============================================================ --%>
<h2>Pedido en armado</h2>

<c:choose>
    <c:when test="${pedidoEnArmado.vacio}">
        <p>Todavía no agregaste productos.</p>
    </c:when>
    <c:otherwise>

        <c:forEach var="item" items="${pedidoEnArmado.items}" varStatus="estadoItem">
            <c:set var="numeroItem" value="${estadoItem.count}"/>

            <section class="tarjeta">
                <h3>
                    Ítem ${numeroItem}:
                    <c:out value="${item.descripcionProducto}"/>
                    x ${item.cantidad}
                    ($<fmt:formatNumber value="${item.precio}" pattern="#,##0.00"/> c/u)
                </h3>

                <%-- Receta: lo que trae el producto --%>
                <p><strong>Ingredientes del producto:</strong>
                    <c:choose>
                        <c:when test="${empty item.receta}">sin ingredientes cargados.</c:when>
                        <c:otherwise>
                            <c:forEach var="ri" items="${item.receta}" varStatus="estadoReceta">
                                <c:out value="${ri.descripcionIngrediente}"/><c:if test="${ri.obligatorio}"> (obligatorio)</c:if><c:if test="${not estadoReceta.last}">, </c:if>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </p>

                <%-- Modificaciones del ítem --%>
                <c:choose>
                    <c:when test="${empty item.modificaciones}">
                        <p>Sin modificaciones.</p>
                    </c:when>
                    <c:otherwise>
                        <table>
                            <thead>
                                <tr>
                                    <th>Tipo</th>
                                    <th>Ingrediente</th>
                                    <th>Cantidad por unidad</th>
                                    <th>Precio unitario</th>
                                    <th class="acciones">Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="modif" items="${item.modificaciones}" varStatus="estadoModif">
                                    <tr>
                                        <td data-label="Tipo"><c:out value="${modif.tipo}"/></td>
                                        <td data-label="Ingrediente"><c:out value="${modif.descripcionIngrediente}"/></td>
                                        <td data-label="Cantidad por unidad">${modif.cantidad}</td>
                                        <td data-label="Precio unitario">
                                            <c:choose>
                                                <c:when test="${modif.tipo == 'AGREGADO'}">
                                                    $<fmt:formatNumber value="${modif.precioUnitario}" pattern="#,##0.00"/>
                                                </c:when>
                                                <c:otherwise>sin cargo</c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="acciones">
                                            <form method="post"
                                                  action="${ctx}/pedidos/nuevo/items/${numeroItem}/modificaciones/${estadoModif.count}/quitar">
                                                <button type="submit">Quitar</button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>

                <%-- Agregar modificación --%>
                <form class="en-linea" method="post"
                      action="${ctx}/pedidos/nuevo/items/${numeroItem}/modificaciones">
                    <label>Acción
                        <select name="tipo" required>
                            <option value="AGREGADO">Agregar</option>
                            <option value="QUITADO">Quitar</option>
                        </select>
                    </label>
                    <label>Ingrediente
                        <select name="idIngrediente" required>
                            <c:forEach var="ingrediente" items="${ingredientes}">
                                <c:if test="${ingrediente.activo}">
                                    <option value="${ingrediente.id}">
                                        <c:out value="${ingrediente.descripcion}"/>
                                        (+$<fmt:formatNumber value="${ingrediente.precio}" pattern="#,##0.00"/>)
                                    </option>
                                </c:if>
                            </c:forEach>
                        </select>
                    </label>
                    <label>Cantidad por unidad
                        <input type="number" name="cantidad" value="1" min="1" required>
                    </label>
                    <button type="submit">Agregar modificación</button>
                </form>

                <p><strong>Subtotal estimado:</strong>
                    $<fmt:formatNumber value="${item.subtotalEstimado}" pattern="#,##0.00"/></p>

                <form method="post" action="${ctx}/pedidos/nuevo/items/${numeroItem}/quitar">
                    <button type="submit">Quitar ítem</button>
                </form>
            </section>
        </c:forEach>

        <h2>Total estimado:
            $<fmt:formatNumber value="${pedidoEnArmado.totalEstimado}" pattern="#,##0.00"/></h2>
        <p>El total definitivo lo calcula el sistema al confirmar el pedido.</p>

        <div class="barra-acciones">
            <form method="post" action="${ctx}/pedidos/nuevo/confirmar">
                <button type="submit">Confirmar pedido</button>
            </form>

            <details class="confirmar">
                <summary>Descartar pedido</summary>
                <form class="panel-confirmar" method="post" action="${ctx}/pedidos/nuevo/cancelar">
                    <span class="pregunta">¿Descartar todo el pedido en armado?</span>
                    <button type="submit">Confirmar</button>
                </form>
            </details>
        </div>
    </c:otherwise>
</c:choose>

</main>
</body>
</html>