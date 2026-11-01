<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<c:choose>
    <c:when test="${esAlta}">
        <c:set var="titulo" value="Nuevo empleado"/>
        <c:set var="accion" value="${ctx}/empleados/nuevo"/>
    </c:when>
    <c:otherwise>
        <c:set var="titulo" value="Modificar empleado ${empleado.id}"/>
        <c:set var="accion" value="${ctx}/empleados/${empleado.id}/editar"/>
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
<p><a class="volver" href="${ctx}/empleados">Volver a empleados</a></p>

<c:if test="${not empty error}">
    <p class="error"><c:out value="${error}"/></p>
</c:if>

<form class="formulario" method="post" action="${accion}">

    <%-- DNI: se carga en el alta; en la modificación solo se muestra --%>
    <p>
        <c:choose>
            <c:when test="${esAlta}">
                <label>DNI
                    <input type="text" name="dni" value="${fn:escapeXml(empleado.dni)}"
                           inputmode="numeric" required>
                </label>
            </c:when>
            <c:otherwise>
                <strong>DNI:</strong> <c:out value="${empleado.dni}"/> (no se modifica)
            </c:otherwise>
        </c:choose>
    </p>

    <p>
        <label>Nombre
            <input type="text" name="nombre" value="${fn:escapeXml(empleado.nombre)}"
                   pattern="[^&lt;&gt;\{\}]*" title="No se permiten los caracteres &lt; &gt; { }" required>
        </label>
    </p>
    <p>
        <label>Apellido
            <input type="text" name="apellido" value="${fn:escapeXml(empleado.apellido)}"
                   pattern="[^&lt;&gt;\{\}]*" title="No se permiten los caracteres &lt; &gt; { }" required>
        </label>
    </p>
    <p>
        <label>Usuario
            <input type="text" name="usuario" value="${fn:escapeXml(empleado.usuario)}"
                   pattern="[^&lt;&gt;\{\}]*" title="No se permiten los caracteres &lt; &gt; { }"
                   autocomplete="off" required>
        </label>
    </p>

    <%-- Contraseña: obligatoria en el alta, opcional en la modificación --%>
    <p>
        <c:choose>
            <c:when test="${esAlta}">
                <label>Contraseña
                    <input type="password" name="password" autocomplete="new-password"
                           pattern="[^&lt;&gt;\{\}]*" title="No se permiten los caracteres &lt; &gt; { }" required>
                </label>
            </c:when>
            <c:otherwise>
                <label>Contraseña nueva
                    <input type="password" name="password" autocomplete="new-password"
                           pattern="[^&lt;&gt;\{\}]*" title="No se permiten los caracteres &lt; &gt; { }">
                </label>
                <span class="aclaracion">Dejar vacía para mantener la actual.</span>
            </c:otherwise>
        </c:choose>
    </p>

    <%-- Rol: se elige en el alta; en la modificación solo se muestra --%>
    <p>
        <c:choose>
            <c:when test="${esAlta}">
                <label>Rol
                    <select name="rol" required>
                        <c:forEach var="rolOpcion" items="${roles}">
                            <option value="${rolOpcion}" ${rolOpcion == empleado.rol ? 'selected' : ''}>
                                <c:out value="${rolOpcion}"/>
                            </option>
                        </c:forEach>
                    </select>
                </label>
            </c:when>
            <c:otherwise>
                <strong>Rol:</strong> <c:out value="${empleado.rol}"/>
                <span class="aclaracion">El rol del día se cambia desde el listado.</span>
            </c:otherwise>
        </c:choose>
    </p>

    <button type="submit">Guardar</button>
</form>

</main>
</body>
</html>