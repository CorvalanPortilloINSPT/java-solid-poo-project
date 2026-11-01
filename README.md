# FastFood – Sistema de gestión para un local de comidas rápidas

Trabajo Práctico Final de **Programación II** – UTN INSPT · Comisión 2603 · Grupo B · 2026
Profesor: Miguel Silva

**Integrantes**
- Corvalán Portillo, Carla Sofía
- García, Pablo Gustavo
- Ciancio, Vicente Reynaldo

---

## Descripción

Sistema web para gestionar un local de comidas rápidas. Permite administrar empleados, ingredientes y productos (con sus recetas), registrar pedidos con ingredientes agregados o quitados, despacharlos desde la cocina y consultar un reporte de ventas.

Cada empleado ingresa con su usuario y ve solo las opciones de su rol:

| Rol | Qué puede hacer |
|---|---|
| **SYS_ADMIN** | Alta, baja y modificación de gerentes y administradores. |
| **GERENTE** | Gestiona vendedores, cocineros, ingredientes, productos y recetas. Consulta pedidos y el reporte de ventas. |
| **VENDEDOR** | Arma pedidos (agrega o quita ingredientes por ítem) y consulta sus pedidos. |
| **COCINERO** | Ve los pedidos pendientes y los despacha. |

En la primera entrega el sistema era una aplicación de consola con persistencia en archivos de texto. Esta versión lo migra a una arquitectura web y conserva sus reglas de negocio. Los archivos de esa entrega están en [`primera-entrega/`](primera-entrega).

## Arquitectura

```
Navegador  ──HTTP──▶  frontend-fastfood  ──REST + JSON──▶  backend-fastfood  ──JPA──▶  MySQL
                      (puerto 8081)        (HTTP Basic)      (puerto 8080)              (base fastfood)
```

- **backend-fastfood**: API REST. Concentra las reglas de negocio, la seguridad y el acceso a la base de datos.
- **frontend-fastfood**: aplicación web que muestra las pantallas de cada rol y consume la API. No usa JavaScript.

## Tecnologías

| Parte | Tecnologías |
|---|---|
| Backend | Java 25, Spring Boot 4, Spring Web, Spring Data JPA (Hibernate), Spring Security (HTTP Basic + BCrypt), Maven |
| Frontend | Java 25, Spring Boot 4, Spring MVC, JSP + JSTL, CSS, Maven |
| Base de datos | MySQL 8 |

## Estructura del repositorio

| Carpeta | Contenido |
|---|---|
| [`backend-fastfood/`](backend-fastfood) | Código fuente de la API REST |
| [`frontend-fastfood/`](frontend-fastfood) | Código fuente de la aplicación web |
| [`database/`](database) | `fastfood.sql`: crea la base con datos de ejemplo |
| [`docs/`](docs) | Informe final y diagramas UML ([`docs/uml/`](docs/uml)) |
| [`ejecutables/`](ejecutables) | Scripts `.bat` para iniciar el sistema |
| [`primera-entrega/`](primera-entrega) | Archivos de la primera entrega |

Los ejecutables (`.jar` y `.war`) se descargan desde **[Releases](../../releases)**.

---

## Cómo ejecutarlo

### Requisitos

- **Java 25** o superior (`java -version` para verificarlo)
- **MySQL 8** en el puerto 3306. Por defecto el backend se conecta con el usuario `root` sin contraseña.

### 1. Crear la base de datos

Abrí [`database/fastfood.sql`](database/fastfood.sql) en MySQL Workbench y ejecutalo completo (botón del rayo). También se puede ejecutar desde la consola:

```
mysql -u root -p < database/fastfood.sql
```

El script **borra y vuelve a crear** la base `fastfood` con las 7 tablas y los datos de ejemplo.

### 2. Iniciar el sistema

**Opción A: con los ejecutables (no requiere Maven)**

1. Descargá `backend-fastfood-0.0.1-SNAPSHOT.jar` y `frontend-fastfood-0.0.1-SNAPSHOT.war` desde [Releases](../../releases).
2. Copialos en la carpeta `ejecutables/`, junto a los `.bat`.
3. Ejecutá `1-iniciar-backend.bat` y esperá a que la consola muestre `Started BackendFastfoodApplication`.
4. Ejecutá `2-iniciar-frontend.bat`.

También se pueden iniciar a mano:

```
java -jar backend-fastfood-0.0.1-SNAPSHOT.jar
java -jar frontend-fastfood-0.0.1-SNAPSHOT.war
```

**Opción B: desde el código fuente**

En dos consolas distintas, primero el backend y después el frontend:

```
cd backend-fastfood
mvnw spring-boot:run
```

```
cd frontend-fastfood
mvnw spring-boot:run
```

### 3. Entrar

Abrí **http://localhost:8081** en el navegador.

### Si MySQL tiene contraseña o el sistema corre en varias PCs

Los valores de `application.properties` se pueden cambiar al iniciar, sin recompilar:

```
java -jar backend-fastfood-0.0.1-SNAPSHOT.jar --spring.datasource.password=TU_CLAVE
java -jar backend-fastfood-0.0.1-SNAPSHOT.jar --spring.datasource.url=jdbc:mysql://IP_DE_LA_BASE:3306/fastfood
java -jar frontend-fastfood-0.0.1-SNAPSHOT.war --backend.url=http://IP_DEL_BACKEND:8080
```

Desde otra PC o un celular de la misma red, se entra con `http://IP_DEL_FRONTEND:8081`. El firewall de Windows tiene que permitir los puertos que se usen: 3306 en la PC de la base, 8080 en la del backend y 8081 en la del frontend.

---

## Usuarios de prueba

| Usuario | Contraseña | Rol | Empleado |
|---|---|---|---|
| `sysadmin` | `sysadmin123` | SYS_ADMIN | Administrador Sistema |
| `admin` | `admin123` | GERENTE | Lucía Fernández |
| `rperez` | `vendedor123` | VENDEDOR | Ratón Pérez |
| `mgomez` | `vendedor123` | VENDEDOR | Martina Gómez |
| `tazul` | `cocinero123` | COCINERO | El Tiburón Azul |
| `bsosa` | `cocinero123` | COCINERO | Bruno Sosa |
| `jlopez` | `vendedor123` | VENDEDOR | Julián López (dado de baja: no puede ingresar) |

> ⚠️ Son usuarios y contraseñas **ficticios, solo para probar el sistema en forma local** con la base de ejemplo. En la base se guardan cifradas con BCrypt, nunca en texto plano. En un uso real hay que cambiar todas las contraseñas desde «Cambiar contraseña» y no publicar credenciales.

Al iniciar, si no hay ningún SYS_ADMIN o GERENTE activo, el backend crea `sysadmin` / `sysadmin123` o `admin` / `admin123`, para que el sistema siempre tenga quien dé de alta empleados.

La base de ejemplo también incluye 22 ingredientes, 13 productos con sus recetas y 10 pedidos (8 despachados y 2 pendientes). El producto *Wrap de pollo* no tiene receta, por eso aparece como no disponible.

---

## Documentación

- 📄 [Informe final](docs/Informe_TP_FastFood_Grupo_B.pdf)
- 📐 Diagramas de clases: [sistema completo](docs/uml/FastFood_UML.pdf) · [backend](docs/uml/FastFood_UML_backend.pdf) · [frontend](docs/uml/FastFood_UML_frontend.pdf) · [backend editable (.drawio)](docs/uml/FastFood_backend_UML.drawio)
- 🧩 [Casos de uso](docs/uml/casos_de_uso.png) · [Entidad-relación](docs/uml/entidad_relacion.png) · [Arquitectura](docs/uml/arquitectura.png)
