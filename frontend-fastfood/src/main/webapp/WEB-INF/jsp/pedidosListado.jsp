<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <%@ include file="/WEB-INF/jsp/fragmentos/head.jspf" %>
    <title>FastFood - Pedidos</title>
</head>
<body>
<%@ include file="/WEB-INF/jsp/fragmentos/encabezado.jspf" %>
<main class="contenido">

<h1>Pedidos</h1>

<c:if test="${not empty exito}">
    <p class="exito"><c:out value="${exito}"/></p>
</c:if>
<c:if test="${not empty error}">
    <p class="error"><c:out value="${error}"/></p>
</c:if>

<c:choose>
    <c:when test="${empty pedidos}">
        <p>No hay pedidos registrados.</p>
    </c:when>

    <c:otherwise>
        <table>
            <thead>
                <tr>
                    <th>Pedido</th>
                    <th>Fecha</th>
                    <th>Estado</th>
                    <th>Vendedor</th>
                    <th>Cocinero</th>
                    <th>Total</th>
                    <th class="acciones">Acciones</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="pedido" items="${pedidos}">
                    <tr>
                        <td data-label="Pedido"><c:out value="${pedido.id}"/></td>
                        <td data-label="Fecha"><c:out value="${pedido.fecha}"/></td>
                        <td data-label="Estado"><c:out value="${pedido.estado}"/></td>
                        <td data-label="Vendedor"><c:out value="${pedido.empleadoVendedor}"/></td>
                        <td data-label="Cocinero"><c:out value="${empty pedido.empleadoCocinero ? 'Sin despachar' : pedido.empleadoCocinero}"/></td>
                        <td data-label="Total">$<fmt:formatNumber value="${pedido.total}" pattern="#,##0.00"/></td>
                        <td class="acciones">
                            <%-- Eliminar primero: así "Ver detalle" queda siempre contra el borde --%>
                            <c:if test="${puedeEliminar and pedido.pendiente}">
                                <details class="confirmar">
                                    <summary>Eliminar</summary>
                                    <form class="panel-confirmar" method="post"
                                          action="${ctx}/pedidos/${pedido.id}/eliminar">
                                        <span class="pregunta">
                                            ¿Eliminar el pedido <c:out value="${pedido.id}"/>? No se puede deshacer.
                                        </span>
                                        <button type="submit">Confirmar</button>
                                    </form>
                                </details>
                            </c:if>

                            <a href="${ctx}/pedidos/${pedido.id}">Ver detalle</a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:otherwise>
</c:choose>

</main>
</body>
</html>