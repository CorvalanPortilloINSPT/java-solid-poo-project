<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <%@ include file="/WEB-INF/jsp/fragmentos/head.jspf" %>
    <title>Receta de <c:out value="${producto.descripcion}"/></title>
</head>
<body>
<%@ include file="/WEB-INF/jsp/fragmentos/encabezado.jspf" %>
<main class="contenido">

<h1>Receta de <c:out value="${producto.descripcion}"/></h1>
<p><a class="volver" href="${ctx}/productos">Volver a productos</a></p>

<c:if test="${not empty exito}">
    <p class="exito"><c:out value="${exito}"/></p>
</c:if>
<c:if test="${not empty error}">
    <p class="error"><c:out value="${error}"/></p>
</c:if>

<p>
    <strong>Producto:</strong> <c:out value="${producto.id}"/> -
    <c:out value="${producto.descripcion}"/> -
    $<fmt:formatNumber value="${producto.precio}" pattern="#,##0.00"/>
</p>
<p>
    <strong>Disponible para la venta:</strong>
    <c:choose>
        <c:when test="${producto.disponible}">Sí</c:when>
        <c:otherwise>No (necesita al menos un ingrediente obligatorio)</c:otherwise>
    </c:choose>
</p>

<%-- Ingredientes de la receta --%>
<h2>Ingredientes</h2>

<c:choose>
    <c:when test="${empty receta}">
        <p>La receta todavía no tiene ingredientes.</p>
    </c:when>
    <c:otherwise>
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Ingrediente</th>
                    <th>Obligatorio</th>
                    <th class="acciones">Acciones</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="ri" items="${receta}">
                    <tr>
                        <td data-label="ID"><c:out value="${ri.idIngrediente}"/></td>
                        <td data-label="Ingrediente"><c:out value="${ri.descripcionIngrediente}"/></td>
                        <td data-label="Obligatorio">${ri.obligatorio ? 'Sí' : 'No'}</td>
                        <td class="acciones">
                            <c:choose>
                                <c:when test="${ri.obligatorio}">
                                    <span>No se puede quitar</span>
                                </c:when>
                                <c:otherwise>
                                    <form method="post"
                                          action="${ctx}/productos/${producto.id}/receta/${ri.idIngrediente}/quitar">
                                        <button type="submit">Quitar</button>
                                    </form>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:otherwise>
</c:choose>

<%-- Agregar ingrediente --%>
<h2>Agregar ingrediente</h2>

<c:choose>
    <c:when test="${empty ingredientesParaAgregar}">
        <p>No hay más ingredientes activos para agregar.</p>
    </c:when>
    <c:otherwise>
        <form class="en-linea" method="post" action="${ctx}/productos/${producto.id}/receta">
            <label>Ingrediente
                <select name="idIngrediente" required>
                    <c:forEach var="ingrediente" items="${ingredientesParaAgregar}">
                        <option value="${ingrediente.id}">
                            <c:out value="${ingrediente.id}"/> - <c:out value="${ingrediente.descripcion}"/>
                        </option>
                    </c:forEach>
                </select>
            </label>
            <label>
                <input type="checkbox" name="obligatorio" value="true">
                Obligatorio
            </label>
            <button type="submit">Agregar</button>
        </form>
        <p>Un ingrediente obligatorio no se puede quitar después ni de la receta ni de un pedido.</p>
    </c:otherwise>
</c:choose>

</main>
</body>
</html>