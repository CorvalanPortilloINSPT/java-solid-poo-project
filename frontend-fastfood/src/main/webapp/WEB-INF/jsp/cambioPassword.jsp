<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <%@ include file="/WEB-INF/jsp/fragmentos/head.jspf" %>
    <title>Cambiar contraseña</title>
</head>
<body>
<%@ include file="/WEB-INF/jsp/fragmentos/encabezado.jspf" %>
<main class="contenido">

<h1>Cambiar contraseña</h1>

<c:if test="${not empty exito}">
    <p class="exito"><c:out value="${exito}"/></p>
</c:if>
<c:if test="${not empty error}">
    <p class="error"><c:out value="${error}"/></p>
</c:if>

<form class="formulario" method="post" action="${ctx}/password">
    <p>
        <label>Contraseña actual
            <input type="password" name="passwordActual"
                   autocomplete="current-password" required>
        </label>
    </p>
    <p>
        <label>Contraseña nueva
            <input type="password" name="passwordNueva"
                   pattern="[^&lt;&gt;\{\}]*" title="No se permiten los caracteres &lt; &gt; { }"
                   autocomplete="new-password" required>
        </label>
    </p>
    <p>
        <label>Repetir contraseña nueva
            <input type="password" name="passwordRepetida"
                   pattern="[^&lt;&gt;\{\}]*" title="No se permiten los caracteres &lt; &gt; { }"
                   autocomplete="new-password" required>
        </label>
    </p>
    <button type="submit">Cambiar contraseña</button>
</form>

</main>
</body>
</html>