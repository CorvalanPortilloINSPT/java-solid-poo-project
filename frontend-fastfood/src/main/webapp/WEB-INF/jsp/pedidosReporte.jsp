<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <%@ include file="/WEB-INF/jsp/fragmentos/head.jspf" %>
    <title>Reporte de ventas</title>
</head>
<body>
<%@ include file="/WEB-INF/jsp/fragmentos/encabezado.jspf" %>
<main class="contenido">

<h1>Reporte de ventas</h1>

<%-- ============================================================ --%>
<%-- Filtros (GET: quedan en la URL)                               --%>
<%-- ============================================================ --%>
<form class="en-linea" method="get" action="${ctx}/pedidos/reporte">
    <label>Desde
        <input type="date" name="desde" value="${fn:escapeXml(desde)}">
    </label>
    <label>Hasta
        <input type="date" name="hasta" value="${fn:escapeXml(hasta)}">
    </label>
    <label>Vendedor
        <select name="idVendedor">
            <option value="">Todos</option>
            <c:forEach var="empleado" items="${empleados}">
                <option value="${empleado.id}" ${empleado.id == idVendedor ? 'selected' : ''}>
                    <c:out value="${empleado.apellido}, ${empleado.nombre} (${empleado.id})"/>
                    <c:if test="${not empleado.activo}"> - dado de baja</c:if>
                </option>
            </c:forEach>
        </select>
    </label>
    <div class="barra-acciones">
        <button type="submit">Consultar</button>
        <a class="volver" href="${ctx}/pedidos/reporte">Limpiar filtros</a>
    </div>
</form>

<p>Incluye solo pedidos despachados. Los filtros vacíos no se aplican.</p>

<c:if test="${not empty error}">
    <p class="error"><c:out value="${error}"/></p>
</c:if>

<%-- ============================================================ --%>
<%-- Resultado                                                     --%>
<%-- ============================================================ --%>
<c:if test="${not empty reporte}">

    <h2>Resumen</h2>
    <p>
        <strong>Período:</strong>
        <c:choose>
            <c:when test="${empty reporte.desde and empty reporte.hasta}">todas las fechas</c:when>
            <c:otherwise>
                <c:out value="${empty reporte.desde ? 'el inicio' : reporte.desde}"/>
                a
                <c:out value="${empty reporte.hasta ? 'hoy' : reporte.hasta}"/>
            </c:otherwise>
        </c:choose>
    </p>
    <p>
        <strong>Vendedor:</strong>
        <c:choose>
            <c:when test="${empty reporte.idVendedor}">todos</c:when>
            <c:otherwise>
                <c:out value="${nombresEmpleados[reporte.idVendedor]} (${reporte.idVendedor})"/>
            </c:otherwise>
        </c:choose>
    </p>
    <p><strong>Cantidad de ventas:</strong> ${reporte.cantidadVentas}</p>
    <p><strong>Total vendido:</strong>
        $<fmt:formatNumber value="${reporte.totalVentas}" pattern="#,##0.00"/></p>

    <h2>Pedidos</h2>
    <c:choose>
        <c:when test="${empty reporte.pedidos}">
            <p>No hay ventas con esos filtros.</p>
        </c:when>
        <c:otherwise>
            <table>
                <thead>
                    <tr>
                        <th>Pedido</th>
                        <th>Fecha</th>
                        <th>Vendedor</th>
                        <th>Cocinero</th>
                        <th>Total</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="pedido" items="${reporte.pedidos}">
                        <tr>
                            <td data-label="Pedido">
                                <a class="codigo" href="${ctx}/pedidos/${pedido.id}"><c:out value="${pedido.id}"/></a>
                            </td>
                            <td data-label="Fecha"><c:out value="${pedido.fecha}"/></td>
                            <td data-label="Vendedor">
                                <c:out value="${nombresEmpleados[pedido.empleadoVendedor]}"/>
                                (<c:out value="${pedido.empleadoVendedor}"/>)
                            </td>
                            <td data-label="Cocinero">
                                <c:out value="${nombresEmpleados[pedido.empleadoCocinero]}"/>
                                (<c:out value="${pedido.empleadoCocinero}"/>)
                            </td>
                            <td data-label="Total">$<fmt:formatNumber value="${pedido.total}" pattern="#,##0.00"/></td>
                        </tr>
                    </c:forEach>
                </tbody>
                <tfoot>
                    <tr>
                        <th colspan="4">Total</th>
                        <th>$<fmt:formatNumber value="${reporte.totalVentas}" pattern="#,##0.00"/></th>
                    </tr>
                </tfoot>
            </table>
        </c:otherwise>
    </c:choose>
</c:if>

</main>
</body>
</html>