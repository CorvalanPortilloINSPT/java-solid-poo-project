<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <%@ include file="/WEB-INF/jsp/fragmentos/head.jspf" %>
    <title>Ingredientes</title>
</head>
<body>
<%@ include file="/WEB-INF/jsp/fragmentos/encabezado.jspf" %>
<main class="contenido">

<h1>Ingredientes</h1>

<c:if test="${not empty exito}">
    <p class="exito"><c:out value="${exito}"/></p>
</c:if>
<c:if test="${not empty error}">
    <p class="error"><c:out value="${error}"/></p>
</c:if>

<p><a class="boton" href="${ctx}/ingredientes/nuevo">Nuevo ingrediente</a></p>

<c:choose>
    <c:when test="${empty ingredientes}">
        <p>No hay ingredientes activos.</p>
    </c:when>
    <c:otherwise>
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Descripción</th>
                    <th>Precio</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="ingrediente" items="${ingredientes}">
                    <tr>
                        <td data-label="ID"><c:out value="${ingrediente.id}"/></td>
                        <td data-label="Descripción"><c:out value="${ingrediente.descripcion}"/></td>
                        <td data-label="Precio">$<fmt:formatNumber value="${ingrediente.precio}" pattern="#,##0.00"/></td>
                        <td class="acciones">
                            <a href="${ctx}/ingredientes/${ingrediente.id}/editar">Modificar</a>
                            <details class="confirmar">
                                <summary>Dar de baja</summary>
                                <form class="panel-confirmar" method="post"
                                      action="${ctx}/ingredientes/${ingrediente.id}/baja">
                                    <span class="pregunta">
                                        ¿Dar de baja <c:out value="${ingrediente.descripcion}"/>?
                                    </span>
                                    <button type="submit">Confirmar</button>
                                </form>
                            </details>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:otherwise>
</c:choose>

</main>
</body>
</html>