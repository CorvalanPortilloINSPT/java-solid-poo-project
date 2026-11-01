<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <%@ include file="/WEB-INF/jsp/fragmentos/head.jspf" %>
    <title>Productos</title>
</head>
<body>
<%@ include file="/WEB-INF/jsp/fragmentos/encabezado.jspf" %>
<main class="contenido">

<h1>Productos</h1>

<c:if test="${not empty exito}">
    <p class="exito"><c:out value="${exito}"/></p>
</c:if>
<c:if test="${not empty error}">
    <p class="error"><c:out value="${error}"/></p>
</c:if>

<p><a class="boton" href="${ctx}/productos/nuevo">Nuevo producto</a></p>

<c:choose>
    <c:when test="${empty productos}">
        <p>No hay productos activos.</p>
    </c:when>
    <c:otherwise>
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Descripción</th>
                    <th>Precio</th>
                    <th>Disponible</th>
                    <th class="acciones">Acciones</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="producto" items="${productos}">
                    <tr>
                        <td data-label="ID"><c:out value="${producto.id}"/></td>
                        <td data-label="Descripción"><c:out value="${producto.descripcion}"/></td>
                        <td data-label="Precio">$<fmt:formatNumber value="${producto.precio}" pattern="#,##0.00"/></td>
                        <td data-label="Disponible">${producto.disponible ? 'Sí' : 'No'}</td>
                        <td class="acciones">
                            <a href="${ctx}/productos/${producto.id}/receta">Receta</a>
                            <a href="${ctx}/productos/${producto.id}/editar">Modificar</a>
                            <details class="confirmar">
                                <summary>Dar de baja</summary>
                                <form class="panel-confirmar" method="post"
                                      action="${ctx}/productos/${producto.id}/baja">
                                    <span class="pregunta">
                                        ¿Dar de baja <c:out value="${producto.descripcion}"/>?
                                    </span>
                                    <button type="submit">Confirmar</button>
                                </form>
                            </details>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <p>Un producto está disponible para la venta cuando su receta tiene al menos un ingrediente obligatorio.</p>
    </c:otherwise>
</c:choose>

</main>
</body>
</html>