<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <%@ include file="/WEB-INF/jsp/fragmentos/head.jspf" %>
    <title>FastFood - Prueba</title>
</head>
<body>
<%@ include file="/WEB-INF/jsp/fragmentos/encabezado.jspf" %>
<main class="contenido">

<h1>Página de prueba</h1>

<h2>1. Conexión con el backend</h2>
<c:choose>
    <c:when test="${conectado}">
        <p class="exito">Conectado: <c:out value="${mensaje}"/></p>
    </c:when>
    <c:otherwise>
        <p class="error">Sin conexión: <c:out value="${mensaje}"/></p>
    </c:otherwise>
</c:choose>

<h2>2. Error traducido (endpoint protegido sin credenciales)</h2>
<p><c:out value="${errorEsperado}"/></p>

</main>
</body>
</html>