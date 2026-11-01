<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <%@ include file="/WEB-INF/jsp/fragmentos/head.jspf" %>
    <title>FastFood - Inicio</title>
</head>
<body>
<%@ include file="/WEB-INF/jsp/fragmentos/encabezado.jspf" %>
<main class="contenido">

<h1>Bienvenido/a, <c:out value="${usuario.nombre}"/></h1>

<h2>Menú</h2>

<c:choose>
    <c:when test="${usuario.rol == 'VENDEDOR'}">
        <ul class="menu">
            <li><a href="${ctx}/pedidos/nuevo">Registrar pedido</a></li>
            <li><a href="${ctx}/pedidos">Consultar pedidos</a></li>
        </ul>
    </c:when>

    <c:when test="${usuario.rol == 'COCINERO'}">
        <ul class="menu">
            <li><a href="${ctx}/pedidos/pendientes">Pedidos pendientes</a></li>
        </ul>
    </c:when>

    <c:when test="${usuario.rol == 'GERENTE'}">
        <ul class="menu">
            <li><a href="${ctx}/empleados">Vendedores y cocineros (rol del día)</a></li>
            <li><a href="${ctx}/productos">Productos y recetas</a></li>
            <li><a href="${ctx}/ingredientes">Ingredientes</a></li>
            <li><a href="${ctx}/pedidos">Pedidos</a></li>
            <li><a href="${ctx}/pedidos/reporte">Reporte de ventas</a></li>
        </ul>
    </c:when>

    <c:when test="${usuario.rol == 'SYS_ADMIN'}">
        <ul class="menu">
            <li><a href="${ctx}/empleados">Gerentes y administradores</a></li>
        </ul>
    </c:when>

    <c:otherwise>
        <p>Su rol no tiene opciones disponibles.</p>
    </c:otherwise>
</c:choose>

<h2>Mi cuenta</h2>
<ul class="menu">
    <li><a href="${ctx}/password">Cambiar contraseña</a></li>
</ul>

</main>
</body>
</html>