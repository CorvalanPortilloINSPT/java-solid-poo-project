<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <%@ include file="/WEB-INF/jsp/fragmentos/head.jspf" %>
    <title>Empleados</title>
</head>
<body>
<%@ include file="/WEB-INF/jsp/fragmentos/encabezado.jspf" %>
<main class="contenido">

<h1>Empleados</h1>

<c:if test="${not empty exito}">
    <p class="exito"><c:out value="${exito}"/></p>
</c:if>
<c:if test="${not empty error}">
    <p class="error"><c:out value="${error}"/></p>
</c:if>

<p><a class="boton" href="${ctx}/empleados/nuevo">Nuevo empleado</a></p>

<c:choose>
    <c:when test="${empty empleados}">
        <p>No hay empleados para gestionar.</p>
    </c:when>
    <c:otherwise>
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>DNI</th>
                    <th>Apellido</th>
                    <th>Nombre</th>
                    <th>Usuario</th>
                    <th>Rol</th>
                    <th>Estado</th>
                    <th class="acciones">Acciones</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="empleado" items="${empleados}">
                    <tr>
                        <td data-label="ID"><c:out value="${empleado.id}"/></td>
                        <td data-label="DNI"><c:out value="${empleado.dni}"/></td>
                        <td data-label="Apellido"><c:out value="${empleado.apellido}"/></td>
                        <td data-label="Nombre"><c:out value="${empleado.nombre}"/></td>
                        <td data-label="Usuario"><c:out value="${empleado.usuario}"/></td>
                        <td data-label="Rol"><c:out value="${empleado.rol}"/></td>
                        <td data-label="Estado">${empleado.activo ? 'Activo' : 'Dado de baja'}</td>
                        <td class="acciones">
                            <c:choose>
                                <c:when test="${not empleado.activo}">
                                    <span>Sin acciones</span>
                                </c:when>
                                <c:otherwise>

                                    <%-- Rol del día: solo gerente, vendedor <-> cocinero --%>
                                    <c:if test="${puedeAsignarRolDelDia and empleado.rol == 'VENDEDOR'}">
                                        <form method="post" action="${ctx}/empleados/${empleado.id}/rol">
                                            <input type="hidden" name="rol" value="COCINERO">
                                            <button type="submit">Pasar a cocinero</button>
                                        </form>
                                    </c:if>
                                    <c:if test="${puedeAsignarRolDelDia and empleado.rol == 'COCINERO'}">
                                        <form method="post" action="${ctx}/empleados/${empleado.id}/rol">
                                            <input type="hidden" name="rol" value="VENDEDOR">
                                            <button type="submit">Pasar a vendedor</button>
                                        </form>
                                    </c:if>

                                    <a href="${ctx}/empleados/${empleado.id}/editar">Modificar</a>

                                    <%-- Nadie se da de baja a sí mismo --%>
                                    <c:if test="${empleado.id != idUsuarioActual}">
                                        <details class="confirmar">
                                            <summary>Dar de baja</summary>
                                            <form class="panel-confirmar" method="post"
                                                  action="${ctx}/empleados/${empleado.id}/baja">
                                                <span class="pregunta">
                                                    ¿Dar de baja a <c:out value="${empleado.nombre} ${empleado.apellido}"/>?
                                                    No va a poder iniciar sesión.
                                                </span>
                                                <button type="submit">Confirmar</button>
                                            </form>
                                        </details>
                                    </c:if>

                                </c:otherwise>
                            </c:choose>
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