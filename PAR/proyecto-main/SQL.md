## Copia de la Tabla

use proyecto;

CREATE TABLE Cliente (
	dni VARCHAR(20) PRIMARY KEY,
    nombre VARCHAR(20),
    apellido VARCHAR(20),
    telefono VARCHAR(20),
    correo VARCHAR(20),
    sueldo DECIMAL(14,2)
);

CREATE TABLE Direccion(
	idDireccion INT PRIMARY KEY AUTO_INCREMENT,
    pais VARCHAR(20),
    departamento VARCHAR(20),
    ciudad VARCHAR(20),
    colonia VARCHAR(20),
    referencia VARCHAR(20),
    dni VARCHAR(20),
    foreign key(dni) references Cliente(dni)
);

CREATE TABLE Prestamos (
	idPrestamo INT PRIMARY KEY AUTO_INCREMENT,
    monto DECIMAL(14,2),
    plazo INT,
    tasa_interes DECIMAL(14,2),
    cuota DECIMAL(14,2),
    estado CHAR,
    tipoPrestamo char
);

CREATE TABLE tabla_amortizacion(
	id INT PRIMARY KEY AUTO_INCREMENT,
	idPrestamo INT,
    numeroCuota INT,
    interes DECIMAL(14,2),
    capital DECIMAL(14,2),
    saldo DECIMAL(14,2),
    estado CHAR,
    fechaVencimiento date,
    foreign key(idPrestamo) references Prestamos(idPrestamo)
);

select * from direccion;
select * from cliente;