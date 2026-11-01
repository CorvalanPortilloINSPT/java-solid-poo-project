-- =====================================================================
-- FastFood · base completa: estructura + datos de ejemplo
-- ATENCIÓN: borra la base fastfood si existe y la crea de nuevo.
-- Ejecutar completo en MySQL Workbench (o HeidiSQL). Carga:
-- 7 empleados, 22 ingredientes,
-- 13 productos con receta y 10 pedidos (8 despachados, 2 pendientes).
--
-- Usuarios (usuario / contraseña):
--   sysadmin  / sysadmin123  SYS_ADMIN Administrador Sistema
--   admin     / admin123     GERENTE   Lucía Fernández
--   rperez    / vendedor123  VENDEDOR  Ratón Pérez
--   tazul     / cocinero123  COCINERO  El Tiburón Azul
--   mgomez    / vendedor123  VENDEDOR  Martina Gómez
--   bsosa     / cocinero123  COCINERO  Bruno Sosa
--   jlopez    / vendedor123  VENDEDOR  Julián López  (dado de baja)
-- =====================================================================

DROP DATABASE IF EXISTS fastfood;
CREATE DATABASE fastfood CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE fastfood;

-- ---------------------------------------------------------------------
-- Estructura (tipos compatibles con Hibernate: ddl-auto=validate)
-- ---------------------------------------------------------------------
-- Empleados. Herencia SINGLE_TABLE: Persona (abstracta) + Empleado.
CREATE TABLE persona (
  DTYPE        VARCHAR(31)  NOT NULL,
  ID           VARCHAR(255) NOT NULL,
  DNI          VARCHAR(255),
  nombre       VARCHAR(255),
  apellido     VARCHAR(255),
  usuario      VARCHAR(255),
  `contraseña` VARCHAR(255),
  rol          ENUM('VENDEDOR','COCINERO','GERENTE','SYS_ADMIN'),
  activo       BIT(1),
  PRIMARY KEY (ID),
  UNIQUE KEY uk_persona_dni (DNI),
  UNIQUE KEY uk_persona_usuario (usuario)
) ENGINE=InnoDB;

CREATE TABLE ingrediente (
  ID          VARCHAR(255) NOT NULL,
  descripcion VARCHAR(255),
  precio      DOUBLE       NOT NULL,
  activo      BIT(1)       NOT NULL,
  PRIMARY KEY (ID)
) ENGINE=InnoDB;

CREATE TABLE producto (
  ID          VARCHAR(255) NOT NULL,
  descripcion VARCHAR(255),
  precio      DOUBLE       NOT NULL,
  disponible  BIT(1)       NOT NULL,
  activo      BIT(1)       NOT NULL,
  PRIMARY KEY (ID)
) ENGINE=InnoDB;

-- Receta: clave compuesta (producto, ingrediente).
CREATE TABLE productoingrediente (
  producto_ID    VARCHAR(255) NOT NULL,
  ingrediente_ID VARCHAR(255) NOT NULL,
  obligatorio    BIT(1)       NOT NULL,
  PRIMARY KEY (producto_ID, ingrediente_ID),
  CONSTRAINT fk_receta_producto    FOREIGN KEY (producto_ID)    REFERENCES producto (ID),
  CONSTRAINT fk_receta_ingrediente FOREIGN KEY (ingrediente_ID) REFERENCES ingrediente (ID)
) ENGINE=InnoDB;

CREATE TABLE pedido (
  ID                  VARCHAR(255) NOT NULL,
  fecha               DATE,
  estado              ENUM('PENDIENTE','DESPACHADO'),
  empleadoVendedor_ID VARCHAR(255),
  empleadoCocinero_ID VARCHAR(255),
  PRIMARY KEY (ID),
  CONSTRAINT fk_pedido_vendedor FOREIGN KEY (empleadoVendedor_ID) REFERENCES persona (ID),
  CONSTRAINT fk_pedido_cocinero FOREIGN KEY (empleadoCocinero_ID) REFERENCES persona (ID)
) ENGINE=InnoDB;

-- Ítems del pedido: copian descripción y precio del producto al vender.
CREATE TABLE item (
  ID                  VARCHAR(255) NOT NULL,
  pedido_ID           VARCHAR(255),
  producto_ID         VARCHAR(255),
  descripcionProducto VARCHAR(255),
  cantidad            INT          NOT NULL,
  precioVenta         DOUBLE       NOT NULL,
  PRIMARY KEY (ID),
  CONSTRAINT fk_item_pedido   FOREIGN KEY (pedido_ID)   REFERENCES pedido (ID),
  CONSTRAINT fk_item_producto FOREIGN KEY (producto_ID) REFERENCES producto (ID)
) ENGINE=InnoDB;

-- Modificaciones de cada ítem (por unidad).
CREATE TABLE itemingrediente (
  ID               VARCHAR(255) NOT NULL,
  item_ID          VARCHAR(255),
  ingrediente_ID   VARCHAR(255),
  tipoModificacion ENUM('AGREGADO','QUITADO'),
  cantidad         INT          NOT NULL,
  precioUnitario   DOUBLE       NOT NULL,
  PRIMARY KEY (ID),
  CONSTRAINT fk_mod_item        FOREIGN KEY (item_ID)        REFERENCES item (ID),
  CONSTRAINT fk_mod_ingrediente FOREIGN KEY (ingrediente_ID) REFERENCES ingrediente (ID)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Datos de ejemplo
-- ---------------------------------------------------------------------

-- Empleados (herencia SINGLE_TABLE: DTYPE = 'Empleado'; contraseñas en BCrypt)
INSERT INTO persona (DTYPE, ID, DNI, nombre, apellido, usuario, `contraseña`, rol, activo) VALUES
  ('Empleado', 'E0001', '20111222', 'Administrador', 'Sistema', 'sysadmin', '{bcrypt}$2a$10$N9Zo0mz9Ja3sqUcQ6XHkJOojg9ypd8l0gD4jbzKmbBv1LOj.5E.JW', 'SYS_ADMIN', 1),
  ('Empleado', 'E0002', '27333444', 'Lucía', 'Fernández', 'admin', '{bcrypt}$2a$10$usDMu95pFIo4mYdkzC3DbuLq6lttP6kITs2lSoD2qK4caf554geGO', 'GERENTE', 1),
  ('Empleado', 'E0003', '30555666', 'Ratón', 'Pérez', 'rperez', '{bcrypt}$2a$10$r1QrwwlsrKuh3yGj4P2QzOZCPv2GgH5pL8UprP7Y8Pu1gdro.7Pa2', 'VENDEDOR', 1),
  ('Empleado', 'E0004', '31777888', 'El Tiburón', 'Azul', 'tazul', '{bcrypt}$2a$10$yjCBaeW6jSk2kQDhIAp2s.VH7zFF2y4Qj03qof8RMmrAeSowmzD/6', 'COCINERO', 1),
  ('Empleado', 'E0005', '35123456', 'Martina', 'Gómez', 'mgomez', '{bcrypt}$2a$10$r1QrwwlsrKuh3yGj4P2QzOZCPv2GgH5pL8UprP7Y8Pu1gdro.7Pa2', 'VENDEDOR', 1),
  ('Empleado', 'E0006', '36987654', 'Bruno', 'Sosa', 'bsosa', '{bcrypt}$2a$10$yjCBaeW6jSk2kQDhIAp2s.VH7zFF2y4Qj03qof8RMmrAeSowmzD/6', 'COCINERO', 1),
  ('Empleado', 'E0007', '33222111', 'Julián', 'López', 'jlopez', '{bcrypt}$2a$10$r1QrwwlsrKuh3yGj4P2QzOZCPv2GgH5pL8UprP7Y8Pu1gdro.7Pa2', 'VENDEDOR', 0);

-- Ingredientes (precio = costo de agregarlo como extra)
INSERT INTO ingrediente (ID, descripcion, precio, activo) VALUES
  ('ING01', 'Pan de hamburguesa', 350, 1),
  ('ING02', 'Medallón de carne', 900, 1),
  ('ING03', 'Queso cheddar', 300, 1),
  ('ING04', 'Lechuga', 150, 1),
  ('ING05', 'Tomate', 150, 1),
  ('ING06', 'Cebolla', 120, 1),
  ('ING07', 'Panceta', 450, 1),
  ('ING08', 'Pepinillos', 200, 1),
  ('ING09', 'Salsa especial', 150, 1),
  ('ING10', 'Pechuga de pollo crispy', 1100, 1),
  ('ING11', 'Pan de sándwich', 300, 1),
  ('ING12', 'Mayonesa', 100, 1),
  ('ING13', 'Papas fritas', 800, 1),
  ('ING14', 'Salsa cheddar', 350, 1),
  ('ING15', 'Presas de pollo frito', 2500, 1),
  ('ING16', 'Nuggets de pollo', 1500, 1),
  ('ING17', 'Salsa barbacoa', 150, 1),
  ('ING18', 'Gaseosa cola 500 ml', 900, 1),
  ('ING19', 'Agua mineral 500 ml', 700, 1),
  ('ING20', 'Helado de vainilla', 1000, 1),
  ('ING21', 'Salsa de chocolate', 250, 1),
  ('ING22', 'Salsa picante', 200, 0);

-- Productos (disponible = la receta tiene al menos un ingrediente obligatorio)
INSERT INTO producto (ID, descripcion, precio, disponible, activo) VALUES
  ('PR001', 'Hamburguesa clásica', 2500, 1, 1),
  ('PR002', 'Hamburguesa con queso', 2900, 1, 1),
  ('PR003', 'Hamburguesa con panceta', 3600, 1, 1),
  ('PR004', 'Sándwich de pollo crispy', 3100, 1, 1),
  ('PR005', 'Presas de pollo frito (3 u.)', 3500, 1, 1),
  ('PR006', 'Nuggets de pollo (6 u.)', 2200, 1, 1),
  ('PR007', 'Papas fritas medianas', 1500, 1, 1),
  ('PR008', 'Papas con cheddar y panceta', 2600, 1, 1),
  ('PR009', 'Gaseosa cola 500 ml', 1200, 1, 1),
  ('PR010', 'Agua mineral 500 ml', 1000, 1, 1),
  ('PR011', 'Sundae de chocolate', 1800, 1, 1),
  ('PR012', 'Wrap de pollo (próximamente)', 3000, 0, 1),
  ('PR013', 'Hamburguesa vegetariana', 3000, 1, 0);

-- Recetas
INSERT INTO productoingrediente (producto_ID, ingrediente_ID, obligatorio) VALUES
  ('PR001', 'ING01', 1),
  ('PR001', 'ING02', 1),
  ('PR001', 'ING04', 0),
  ('PR001', 'ING05', 0),
  ('PR001', 'ING09', 0),
  ('PR002', 'ING01', 1),
  ('PR002', 'ING02', 1),
  ('PR002', 'ING03', 1),
  ('PR002', 'ING08', 0),
  ('PR002', 'ING06', 0),
  ('PR003', 'ING01', 1),
  ('PR003', 'ING02', 1),
  ('PR003', 'ING03', 1),
  ('PR003', 'ING07', 1),
  ('PR003', 'ING17', 0),
  ('PR003', 'ING06', 0),
  ('PR004', 'ING11', 1),
  ('PR004', 'ING10', 1),
  ('PR004', 'ING04', 0),
  ('PR004', 'ING05', 0),
  ('PR004', 'ING12', 0),
  ('PR005', 'ING15', 1),
  ('PR006', 'ING16', 1),
  ('PR006', 'ING17', 0),
  ('PR007', 'ING13', 1),
  ('PR008', 'ING13', 1),
  ('PR008', 'ING14', 1),
  ('PR008', 'ING07', 0),
  ('PR009', 'ING18', 1),
  ('PR010', 'ING19', 1),
  ('PR011', 'ING20', 1),
  ('PR011', 'ING21', 0),
  ('PR013', 'ING01', 1),
  ('PR013', 'ING04', 0),
  ('PR013', 'ING05', 0);

-- Pedidos (el cocinero se asigna al despachar)
INSERT INTO pedido (ID, fecha, estado, empleadoVendedor_ID, empleadoCocinero_ID) VALUES
  ('PE001', '2026-10-19', 'DESPACHADO', 'E0003', 'E0004'),
  ('PE002', '2026-10-19', 'DESPACHADO', 'E0005', 'E0006'),
  ('PE003', '2026-10-20', 'DESPACHADO', 'E0003', 'E0004'),
  ('PE004', '2026-10-21', 'DESPACHADO', 'E0003', 'E0006'),
  ('PE005', '2026-10-22', 'DESPACHADO', 'E0005', 'E0004'),
  ('PE006', '2026-10-24', 'DESPACHADO', 'E0005', 'E0006'),
  ('PE007', '2026-10-26', 'DESPACHADO', 'E0003', 'E0004'),
  ('PE008', '2026-10-28', 'DESPACHADO', 'E0005', 'E0004'),
  ('PE009', '2026-10-31', 'PENDIENTE', 'E0003', NULL),
  ('PE010', '2026-10-31', 'PENDIENTE', 'E0005', NULL);

-- Ítems (descripción y precio copiados del producto al momento de la venta)
INSERT INTO item (ID, pedido_ID, producto_ID, descripcionProducto, cantidad, precioVenta) VALUES
  ('IT001', 'PE001', 'PR002', 'Hamburguesa con queso', 2, 2900),
  ('IT002', 'PE001', 'PR007', 'Papas fritas medianas', 2, 1500),
  ('IT003', 'PE001', 'PR009', 'Gaseosa cola 500 ml', 2, 1200),
  ('IT004', 'PE002', 'PR003', 'Hamburguesa con panceta', 1, 3600),
  ('IT005', 'PE002', 'PR009', 'Gaseosa cola 500 ml', 1, 1200),
  ('IT006', 'PE003', 'PR004', 'Sándwich de pollo crispy', 1, 3100),
  ('IT007', 'PE003', 'PR010', 'Agua mineral 500 ml', 1, 1000),
  ('IT008', 'PE004', 'PR005', 'Presas de pollo frito (3 u.)', 2, 3500),
  ('IT009', 'PE004', 'PR007', 'Papas fritas medianas', 1, 1500),
  ('IT010', 'PE004', 'PR009', 'Gaseosa cola 500 ml', 2, 1200),
  ('IT011', 'PE005', 'PR001', 'Hamburguesa clásica', 1, 2500),
  ('IT012', 'PE005', 'PR011', 'Sundae de chocolate', 1, 1800),
  ('IT013', 'PE006', 'PR006', 'Nuggets de pollo (6 u.)', 1, 2200),
  ('IT014', 'PE006', 'PR008', 'Papas con cheddar y panceta', 1, 2600),
  ('IT015', 'PE007', 'PR002', 'Hamburguesa con queso', 3, 2900),
  ('IT016', 'PE007', 'PR009', 'Gaseosa cola 500 ml', 3, 1200),
  ('IT017', 'PE008', 'PR003', 'Hamburguesa con panceta', 1, 3600),
  ('IT018', 'PE008', 'PR011', 'Sundae de chocolate', 2, 1800),
  ('IT019', 'PE009', 'PR001', 'Hamburguesa clásica', 2, 2500),
  ('IT020', 'PE009', 'PR007', 'Papas fritas medianas', 2, 1500),
  ('IT021', 'PE010', 'PR004', 'Sándwich de pollo crispy', 1, 3100),
  ('IT022', 'PE010', 'PR010', 'Agua mineral 500 ml', 1, 1000);

-- Modificaciones por unidad (solo AGREGADO suma; QUITADO no descuenta)
INSERT INTO itemingrediente (ID, item_ID, ingrediente_ID, tipoModificacion, cantidad, precioUnitario) VALUES
  ('II0001', 'IT004', 'ING03', 'AGREGADO', 1, 300),
  ('II0002', 'IT006', 'ING05', 'QUITADO', 1, 150),
  ('II0003', 'IT011', 'ING04', 'QUITADO', 1, 150),
  ('II0004', 'IT011', 'ING07', 'AGREGADO', 1, 450),
  ('II0005', 'IT013', 'ING17', 'AGREGADO', 1, 150),
  ('II0006', 'IT015', 'ING03', 'AGREGADO', 1, 300),
  ('II0007', 'IT018', 'ING21', 'QUITADO', 1, 250),
  ('II0008', 'IT019', 'ING03', 'AGREGADO', 1, 300),
  ('II0009', 'IT021', 'ING12', 'QUITADO', 1, 100);

-- Totales esperados (los calcula el sistema):
--   PE001  2026-10-19  DESPACHADO  $11.200
--   PE002  2026-10-19  DESPACHADO  $5.100
--   PE003  2026-10-20  DESPACHADO  $4.100
--   PE004  2026-10-21  DESPACHADO  $10.900
--   PE005  2026-10-22  DESPACHADO  $4.750
--   PE006  2026-10-24  DESPACHADO  $4.950
--   PE007  2026-10-26  DESPACHADO  $13.200
--   PE008  2026-10-28  DESPACHADO  $7.200
--   PE009  2026-10-31  PENDIENTE   $8.600
--   PE010  2026-10-31  PENDIENTE   $4.100
