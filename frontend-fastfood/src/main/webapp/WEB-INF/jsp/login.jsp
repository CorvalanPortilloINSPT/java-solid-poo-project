<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <%@ include file="/WEB-INF/jsp/fragmentos/head.jspf" %>
    <title>FastFood - Iniciar sesión</title>
</head>
<body>
<%@ include file="/WEB-INF/jsp/fragmentos/encabezado.jspf" %>
<main class="contenido">

<form class="formulario centrado" method="post" action="${pageContext.request.contextPath}/login">

    <h1>Iniciar sesión</h1>

    <%-- Mensaje después de cerrar sesión (/login?salir) --%>
    <c:if test="${param.salir != null}">
        <p class="exito">Sesión cerrada.</p>
    </c:if>

    <%-- Mensaje de error que manda AuthController --%>
    <c:if test="${not empty error}">
        <p class="error"><c:out value="${error}"/></p>
    </c:if>

    <p>
        <label for="usuario">Usuario</label>
        <input type="text" id="usuario" name="usuario"
               value="${fn:escapeXml(usuarioIngresado)}"
               pattern="[^&lt;&gt;\{\}]*"
               title="No se permiten los caracteres &lt; &gt; { }"
               autocomplete="username" required autofocus>
    </p>
    <p>
        <label for="password">Contraseña</label>
        <input type="password" id="password" name="password"
               pattern="[^&lt;&gt;\{\}]*"
               title="No se permiten los caracteres &lt; &gt; { }"
               autocomplete="current-password" required>
    </p>
    <button type="submit">Ingresar</button>
</form>

</main>
</body>
</html>