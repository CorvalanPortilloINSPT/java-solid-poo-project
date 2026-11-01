<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <%@ include file="/WEB-INF/jsp/fragmentos/head.jspf" %>
    <title>FastFood - Pedido <c:out value="${pedido.id}"/></title>
</head>
<body>
<%@ include file="/WEB-INF/jsp/fragmentos/encabezado.jspf" %>
<main class="contenido">

<h1>Pedido <c:out value="${pedido.id}"/></h1>
<p><a class="volver" href="${ctx}/pedidos">Volver a pedidos</a></p>

<c:if test="${not empty exito}">
    <p class="exito"><c:out value="${exito}"/></p>
</c:if>
<c:if test="${not empty error}">
    <p class="error"><c:out value="${error}"/></p>
</c:if>

<dl class="datos">
    <dt>Fecha</dt>
    <dd><c:out value="${pedido.fecha}"/></dd>

    <dt>Estado</dt>
    <dd><c:out value="${pedido.estado}"/></dd>

    <dt>Vendedor</dt>
    <dd><c:out value="${pedido.nombreVendedor}"/> (<c:out value="${pedido.idVendedor}"/>)</dd>

    <dt>Cocinero</dt>
    <dd>
        <c:choose>
            <c:when test="${empty pedido.idCocinero}">Sin despachar</c:when>
            <c:otherwise>
                <c:out value="${pedido.nombreCocinero}"/> (<c:out value="${pedido.idCocinero}"/>)
            </c:otherwise>
        </c:choose>
    </dd>
</dl>

<h2>Detalle</h2>
<%@ include file="/WEB-INF/jsp/fragmentos/detallePedido.jspf" %>

<c:if test="${puedeEliminar and pedido.estado == 'PENDIENTE'}">
    <details class="confirmar ancho-completo">
        <summary>Eliminar pedido</summary>
        <form class="panel-confirmar" method="post" action="${ctx}/pedidos/${pedido.id}/eliminar">
            <span class="pregunta">
                ¿Eliminar el pedido <c:out value="${pedido.id}"/>? No se puede deshacer.
            </span>
            <button type="submit">Confirmar</button>
        </form>
    </details>
</c:if>

</main>
</body>
</html>