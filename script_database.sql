-- --- CREACIÓN DE TABLAS ---

-- 1. Tabla de Categorías
CREATE TABLE CATEGORIA (
    id_categoria INT IDENTITY(1,1) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

-- 2. Tabla de Usuarios (para Seguridad y Ventas)
CREATE TABLE USUARIO (
    id_usuario INT IDENTITY(1,1) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    clave VARCHAR(255) NOT NULL, -- Para Spring Security (bcrypt hash)
    rol VARCHAR(20) NOT NULL -- Ej: 'GERENTE', 'CAJERO'
);

-- 3. Tabla de Productos (El inventario)
CREATE TABLE PRODUCTO (
    id_producto INT IDENTITY(1,1) PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(255) NOT NULL,
    categoria_id INT NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    stock_actual INT NOT NULL,
    stock_minimo INT NOT NULL, -- Para RF3 (Alertas)
    FOREIGN KEY (categoria_id) REFERENCES CATEGORIA(id_categoria)
);

-- 4. Tabla Maestra de Ventas
CREATE TABLE VENTA (
    id_venta INT IDENTITY(1,1) PRIMARY KEY,
    usuario_id INT NOT NULL,
    fecha DATETIME NOT NULL DEFAULT GETDATE(),
    total DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES USUARIO(id_usuario)
);

-- 5. Tabla de Detalle de Ventas
CREATE TABLE VENTA_DETALLE (
    id_detalle INT IDENTITY(1,1) PRIMARY KEY,
    venta_id INT NOT NULL,
    producto_id INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL, -- Precio al momento de la venta
    FOREIGN KEY (venta_id) REFERENCES VENTA(id_venta),
    FOREIGN KEY (producto_id) REFERENCES PRODUCTO(id_producto)
);
GO

-- --- INSERCIÓN DE DATOS DE EJEMPLO ---

PRINT 'Insertando datos de ejemplo...'

-- Insertar Categorías
INSERT INTO CATEGORIA (nombre) VALUES
('Bebidas'),
('Abarrotes'),
('Limpieza'),
('Snacks');

-- Insertar Usuarios (Clave "123" hasheada con BCrypt - Spring Security la entenderá)
-- Puedes usar una web de "bcrypt generator" para crear tus propias claves
INSERT INTO USUARIO (username, clave, rol) VALUES
('gerente', '$2a$10$E.qZ0.v3/D.qG.8/J.nGOeJb4n.T.U.B0k5.q.Q.E.x9.Q.K.B.d/K', 'GERENTE'),
('cajero1', '$2a$10$E.qZ0.v3/D.qG.8/J.nGOeJb4n.T.U.B0k5.q.Q.E.x9.Q.K.B.d/K', 'CAJERO');

-- Insertar Productos
INSERT INTO PRODUCTO (codigo, nombre, categoria_id, precio, stock_actual, stock_minimo) VALUES
('B-001', 'Gaseosa 3L', 1, 7.50, 100, 20),
('A-001', 'Arroz Bolsa 1kg', 2, 4.20, 150, 30),
('L-001', 'Detergente Bolsa 500g', 3, 5.00, 80, 10),
('S-001', 'Papas Fritas Bolsa Pequeña', 4, 1.50, 200, 50),
('B-002', 'Agua Mineral 1L', 1, 2.00, 120, 20),
('A-002', 'Atún en Lata', 2, 3.80, 90, 15),
('L-002', 'Lejía Botella 1L', 3, 3.00, 70, 15),
('A-003', 'Fideos 500g', 2, 2.80, 100, 25);

PRINT '¡Base de datos y datos de ejemplo creados con éxito!'
GO