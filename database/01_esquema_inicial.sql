CREATE DATABASE Mundial2026;
GO

USE Mundial2026;
GO

-- Tabla de Usuarios
CREATE TABLE Usuario (
    id_usuario INT PRIMARY KEY IDENTITY(1,1),
    nombre_usuario VARCHAR(50) UNIQUE NOT NULL,
    contrasena_hash VARCHAR(255) NOT NULL,
    tipo_usuario VARCHAR(20) NOT NULL CHECK (tipo_usuario IN ('Administrador', 'Tradicional', 'Esporadico')),
    fecha_creacion DATETIME DEFAULT GETDATE()
);
GO

-- Tabla de Confederaciones
CREATE TABLE Confederacion (
    id_confederacion INT PRIMARY KEY IDENTITY(1,1),
    nombre VARCHAR(100) UNIQUE NOT NULL,
    siglas VARCHAR(10) NOT NULL
);
GO

-- Tabla de Países Anfitriones
CREATE TABLE PaisAnfitrion (
    id_pais_anfitrion INT PRIMARY KEY IDENTITY(1,1),
    nombre VARCHAR(100) UNIQUE NOT NULL
);
GO

-- Tabla de Grupos
CREATE TABLE Grupo (
    id_grupo INT PRIMARY KEY IDENTITY(1,1),
    nombre_grupo VARCHAR(1) NOT NULL,
    CONSTRAINT chk_grupo CHECK (nombre_grupo IN ('A','B','C','D','E','F','G','H','I','J','K','L'))
);
GO

-- Tabla de Equipos (depende de Confederacion)
CREATE TABLE Equipo (
    id_equipo INT PRIMARY KEY IDENTITY(1,1),
    nombre VARCHAR(100) UNIQUE NOT NULL,
    pais VARCHAR(100) NOT NULL,
    valor_total_equipo DECIMAL(15,2) DEFAULT 0,
    id_confederacion INT NOT NULL,
    FOREIGN KEY (id_confederacion) REFERENCES Confederacion(id_confederacion)
);
GO

-- Tabla de Directores Técnicos (depende de Equipo)
CREATE TABLE DirectorTecnico (
    id_dt INT PRIMARY KEY IDENTITY(1,1),
    nombre VARCHAR(200) NOT NULL,
    nacionalidad VARCHAR(100) NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    id_equipo INT UNIQUE NOT NULL,
    FOREIGN KEY (id_equipo) REFERENCES Equipo(id_equipo)
);
GO

-- Tabla de Jugadores (depende de Equipo)
CREATE TABLE Jugador (
    id_jugador INT PRIMARY KEY IDENTITY(1,1),
    nombre VARCHAR(200) NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    posicion VARCHAR(50) NOT NULL,
    peso DECIMAL(5,2) NOT NULL,
    estatura DECIMAL(3,2) NOT NULL,
    valor_mercado DECIMAL(15,2) NOT NULL,
    id_equipo INT NOT NULL,
    FOREIGN KEY (id_equipo) REFERENCES Equipo(id_equipo),
    CONSTRAINT chk_peso CHECK (peso > 0 AND peso < 200),
    CONSTRAINT chk_estatura CHECK (estatura > 0 AND estatura < 2.50),
    CONSTRAINT chk_valor CHECK (valor_mercado >= 0)
);
GO

-- Tabla de Ciudades (depende de PaisAnfitrion)
CREATE TABLE Ciudad (
    id_ciudad INT PRIMARY KEY IDENTITY(1,1),
    nombre VARCHAR(100) NOT NULL,
    id_pais_anfitrion INT NOT NULL,
    FOREIGN KEY (id_pais_anfitrion) REFERENCES PaisAnfitrion(id_pais_anfitrion),
    CONSTRAINT unique_ciudad_pais UNIQUE (nombre, id_pais_anfitrion)
);
GO

-- Tabla de Estadios (depende de Ciudad)
CREATE TABLE Estadio (
    id_estadio INT PRIMARY KEY IDENTITY(1,1),
    nombre VARCHAR(100) UNIQUE NOT NULL,
    capacidad INT NOT NULL,
    id_ciudad INT NOT NULL,
    FOREIGN KEY (id_ciudad) REFERENCES Ciudad(id_ciudad),
    CONSTRAINT chk_capacidad CHECK (capacidad > 0)
);
GO

-- Tabla intermedia Equipo-Grupo (relación N:M)
CREATE TABLE Equipo_Grupo (
    id_equipo_grupo INT PRIMARY KEY IDENTITY(1,1),
    id_equipo INT NOT NULL,
    id_grupo INT NOT NULL,
    FOREIGN KEY (id_equipo) REFERENCES Equipo(id_equipo),
    FOREIGN KEY (id_grupo) REFERENCES Grupo(id_grupo),
    CONSTRAINT unique_equipo_grupo UNIQUE (id_equipo, id_grupo)
);
GO

-- Tabla de Partidos (depende de Equipo, Estadio y Grupo)
CREATE TABLE Partido (
    id_partido INT PRIMARY KEY IDENTITY(1,1),
    fecha_hora DATETIME NOT NULL,
    id_estadio INT NOT NULL,
    id_grupo INT NOT NULL,
    id_equipo_local INT NOT NULL,
    id_equipo_visitante INT NOT NULL,
    goles_local INT DEFAULT 0,
    goles_visitante INT DEFAULT 0,
    FOREIGN KEY (id_estadio) REFERENCES Estadio(id_estadio),
    FOREIGN KEY (id_grupo) REFERENCES Grupo(id_grupo),
    FOREIGN KEY (id_equipo_local) REFERENCES Equipo(id_equipo),
    FOREIGN KEY (id_equipo_visitante) REFERENCES Equipo(id_equipo),
    CONSTRAINT chk_equipos_diferentes CHECK (id_equipo_local != id_equipo_visitante),
    CONSTRAINT chk_goles CHECK (goles_local >= 0 AND goles_visitante >= 0)
);
GO

-- Tabla de Bitácora (depende de Usuario)
CREATE TABLE Bitacora (
    id_registro INT PRIMARY KEY IDENTITY(1,1),
    id_usuario INT NOT NULL,
    fecha_hora_ingreso DATETIME DEFAULT GETDATE(),
    fecha_hora_salida DATETIME NULL,
    FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario)
);
GO

-- Índices para optimizar consultas
CREATE INDEX idx_jugador_equipo ON Jugador(id_equipo);
CREATE INDEX idx_jugador_valor ON Jugador(valor_mercado);
CREATE INDEX idx_jugador_edad ON Jugador(fecha_nacimiento);
CREATE INDEX idx_partido_fecha ON Partido(fecha_hora);
CREATE INDEX idx_partido_estadio ON Partido(id_estadio);
CREATE INDEX idx_bitacora_fechas ON Bitacora(fecha_hora_ingreso, fecha_hora_salida);
CREATE INDEX idx_bitacora_usuario ON Bitacora(id_usuario);
GO