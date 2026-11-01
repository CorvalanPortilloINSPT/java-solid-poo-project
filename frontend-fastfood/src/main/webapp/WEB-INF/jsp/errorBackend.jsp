<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <%@ include file="/WEB-INF/jsp/fragmentos/head.jspf" %>
    <title>FastFood - Error</title>
</head>
<body>
<%@ include file="/WEB-INF/jsp/fragmentos/encabezado.jspf" %>
<main class="contenido">

<h1>No se pudo completar la operación</h1>

<p class="error"><c:out value="${mensaje}"/></p>

<c:if test="${sinConexion}">
    <p>Verifique que el backend esté iniciado y vuelva a intentar.</p>
</c:if>

<p><a class="volver" href="${ctx}/inicio">Volver al menú</a></p>

</main>
</body>
</html>