USE Mundial2026;
GO

-- Insertar Usuario Administrador (contraseña: admin123)
INSERT INTO Usuario (nombre_usuario, contrasena_hash, tipo_usuario) VALUES
('admin', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'Administrador');
GO

-- Insertar Confederaciones
INSERT INTO Confederacion (nombre, siglas) VALUES
('Unión de Federaciones Europeas de Fútbol', 'UEFA'),
('Confederación Sudamericana de Fútbol', 'CONMEBOL'),
('Confederación de Fútbol de Norte, Centroamérica y el Caribe', 'CONCACAF'),
('Confederación Africana de Fútbol', 'CAF'),
('Confederación Asiática de Fútbol', 'AFC'),
('Confederación de Fútbol de Oceanía', 'OFC');
GO

-- Insertar Países Anfitriones
INSERT INTO PaisAnfitrion (nombre) VALUES
('México'),
('Estados Unidos'),
('Canadá');
GO

-- Insertar Grupos (12 grupos)
INSERT INTO Grupo (nombre_grupo) VALUES
('A'), ('B'), ('C'), ('D'), ('E'), ('F'),
('G'), ('H'), ('I'), ('J'), ('K'), ('L');
GO

-- Insertar Ciudades
INSERT INTO Ciudad (nombre, id_pais_anfitrion) VALUES
('Ciudad de México', 1),
('Guadalajara', 1),
('Monterrey', 1),
('Los Ángeles', 2),
('Nueva York', 2),
('Miami', 2),
('Toronto', 3),
('Vancouver', 3),
('Montreal', 3);
GO

-- Insertar Estadios
INSERT INTO Estadio (nombre, capacidad, id_ciudad) VALUES
('Estadio Azteca', 87523, 1),
('Estadio Akron', 49850, 2),
('Estadio BBVA', 53500, 3),
('Rose Bowl', 92542, 4),
('MetLife Stadium', 82500, 5),
('Hard Rock Stadium', 65326, 6),
('BMO Field', 30991, 7),
('BC Place', 54500, 8),
('Stade Olympique', 56040, 9);
GO

-- Insertar Equipos
INSERT INTO Equipo (nombre, pais, valor_total_equipo, id_confederacion) VALUES
('Brasil', 'Brasil', 850000000, 2),
('Argentina', 'Argentina', 780000000, 2),
('Francia', 'Francia', 920000000, 1),
('España', 'España', 750000000, 1),
('México', 'México', 280000000, 3),
('Estados Unidos', 'Estados Unidos', 320000000, 3),
('Canadá', 'Canadá', 180000000, 3),
('Japón', 'Japón', 150000000, 5);
GO

-- Insertar Directores Técnicos
INSERT INTO DirectorTecnico (nombre, nacionalidad, fecha_nacimiento, id_equipo) VALUES
('Tite', 'Brasileña', '1961-05-25', 1),
('Lionel Scaloni', 'Argentina', '1978-05-16', 2),
('Didier Deschamps', 'Francesa', '1968-10-15', 3),
('Luis de la Fuente', 'Española', '1961-06-21', 4),
('Jaime Lozano', 'Mexicana', '1978-09-29', 5),
('Gregg Berhalter', 'Estadounidense', '1973-08-01', 6),
('John Herdman', 'Inglesa', '1975-07-19', 7),
('Hajime Moriyasu', 'Japonesa', '1968-08-23', 8);
GO

-- Insertar Jugadores
INSERT INTO Jugador (nombre, fecha_nacimiento, posicion, peso, estatura, valor_mercado, id_equipo) VALUES
('Neymar Jr', '1992-02-05', 'Delantero', 68.5, 1.75, 90000000, 1),
('Vinicius Jr', '2000-07-12', 'Delantero', 73.0, 1.76, 150000000, 1),
('Lionel Messi', '1987-06-24', 'Delantero', 72.0, 1.70, 50000000, 2),
('Enzo Fernández', '2001-01-17', 'Centrocampista', 78.0, 1.78, 75000000, 2),
('Kylian Mbappé', '1998-12-20', 'Delantero', 73.0, 1.78, 180000000, 3),
('Eduardo Camavinga', '2002-11-10', 'Centrocampista', 68.0, 1.82, 85000000, 3),
('Pedri', '2002-11-25', 'Centrocampista', 60.0, 1.74, 90000000, 4),
('Gavi', '2004-08-05', 'Centrocampista', 70.0, 1.73, 80000000, 4),
('Santiago Giménez', '2001-04-18', 'Delantero', 76.0, 1.82, 40000000, 5),
('Edson Álvarez', '1997-10-24', 'Defensa', 75.0, 1.87, 35000000, 5),
('Christian Pulisic', '1998-09-18', 'Delantero', 69.0, 1.78, 45000000, 6),
('Weston McKennie', '1998-08-28', 'Centrocampista', 81.0, 1.85, 25000000, 6),
('Alphonso Davies', '2000-11-02', 'Defensa', 75.0, 1.83, 70000000, 7),
('Jonathan David', '2000-01-14', 'Delantero', 70.0, 1.75, 45000000, 7),
('Takefusa Kubo', '2001-06-04', 'Centrocampista', 67.0, 1.73, 30000000, 8),
('Ritsu Doan', '1998-06-16', 'Centrocampista', 70.0, 1.72, 20000000, 8);
GO

-- Asignar equipos a grupos
INSERT INTO Equipo_Grupo (id_equipo, id_grupo) VALUES
(1, 1), (2, 1), (3, 2), (4, 2),
(5, 3), (6, 3), (7, 4), (8, 4);
GO

-- Insertar Partidos
INSERT INTO Partido (fecha_hora, id_estadio, id_grupo, id_equipo_local, id_equipo_visitante) VALUES
('2026-06-14 15:00:00', 1, 1, 1, 2),
('2026-06-15 18:00:00', 4, 2, 3, 4),
('2026-06-16 20:00:00', 7, 3, 5, 6),
('2026-06-17 14:00:00', 2, 4, 7, 8);
GO