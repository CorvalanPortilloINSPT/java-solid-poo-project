<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <%@ include file="/WEB-INF/jsp/fragmentos/head.jspf" %>
    <title>FastFood - Pedidos pendientes</title>
</head>
<body>
<%@ include file="/WEB-INF/jsp/fragmentos/encabezado.jspf" %>
<main class="contenido">

<h1>Pedidos pendientes</h1>

<%-- Mensajes de la última acción (flash attributes) --%>
<c:if test="${not empty exito}">
    <p class="exito"><c:out value="${exito}"/></p>
</c:if>
<c:if test="${not empty error}">
    <p class="error"><c:out value="${error}"/></p>
</c:if>

<c:choose>
    <c:when test="${empty pedidos}">
        <p>No hay pedidos pendientes.</p>
    </c:when>

    <c:otherwise>
        <c:forEach var="pedido" items="${pedidos}">
            <section class="tarjeta">
                <h2>Pedido <c:out value="${pedido.id}"/></h2>
                <p>
                    <strong>Fecha:</strong> <c:out value="${pedido.fecha}"/> ·
                    <strong>Vendedor:</strong> <c:out value="${pedido.nombreVendedor}"/>
                </p>

                <%@ include file="/WEB-INF/jsp/fragmentos/detallePedido.jspf" %>

                <c:if test="${puedeDespachar}">
                    <form class="ancho-completo" method="post"
                          action="${ctx}/pedidos/${pedido.id}/despachar">
                        <button type="submit">Despachar pedido <c:out value="${pedido.id}"/></button>
                    </form>
                </c:if>
            </section>
        </c:forEach>
    </c:otherwise>
</c:choose>

</main>
</body>
</html>