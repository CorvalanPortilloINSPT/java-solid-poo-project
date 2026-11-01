<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<%-- Sin id: alta. Con id: modificación. --%>
<c:choose>
    <c:when test="${empty id}">
        <c:set var="titulo" value="Nuevo ingrediente"/>
        <c:set var="accion" value="${ctx}/ingredientes/nuevo"/>
    </c:when>
    <c:otherwise>
        <c:set var="titulo" value="Modificar ingrediente ${id}"/>
        <c:set var="accion" value="${ctx}/ingredientes/${id}/editar"/>
    </c:otherwise>
</c:choose>
<!DOCTYPE html>
<html lang="es">
<head>
    <%@ include file="/WEB-INF/jsp/fragmentos/head.jspf" %>
    <title><c:out value="${titulo}"/></title>
</head>
<body>
<%@ include file="/WEB-INF/jsp/fragmentos/encabezado.jspf" %>
<main class="contenido">

<h1><c:out value="${titulo}"/></h1>
<p><a class="volver" href="${ctx}/ingredientes">Volver a ingredientes</a></p>

<c:if test="${not empty error}">
    <p class="error"><c:out value="${error}"/></p>
</c:if>

<form class="formulario" method="post" action="${accion}">
    <p>
        <label>Descripción
            <input type="text" name="descripcion" value="${fn:escapeXml(descripcion)}" required>
        </label>
    </p>
    <p>
        <label>Precio
            <input type="text" name="precio" value="${fn:escapeXml(precio)}"
                   inputmode="decimal" required>
        </label>
    </p>
    <button type="submit">Guardar</button>
</form>

</main>
</body>
</html>