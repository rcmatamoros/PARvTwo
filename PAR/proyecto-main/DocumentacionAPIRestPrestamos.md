## Analisis de La APIRest Prestamos


# Controladores
El llamado a los metodos.

# DTOS
Cada entidad tendra su propio dto, el dto sera el objeto que viaje con la informacion, este se expone al cliente ocultando el modelo original.
# Enums
Tipos de prestamos son constantes.
# Modelos (Entidad)
- Entidad Clientes.
  Metodos
  -- Crear clientes (recibe un json). 
  -- Buscar Cliente (por Id).
  -- Obtener Todos (retorna informacion de tabla cliente,   tabla direccion y tabla prestamos) para lo cual funciona definir las relaciones. Retorna una lista.
- Entidad Prestamos.
  -- Crear Prestamo( recibe un json).
  -- Buscar Prestamo (por Id).
  -- Asociar Prestamo a Cliente (relaciones ).
  -- Obtener Saldo pendiente (Suma todas las cuotas en estado P) retorna el campo saldo de la tabla de amortizacion.
  -- Pagar cuota (cambiar el campo estado estado(update))

Las demas entidades solo tendran los campos de tablas
# Repositorios
Cada entidad tendra su propio repositorio el cual debera tener la anotacion @Repository y heredara de JpaRepository.
# Servicio

Se genera toda la logica de los metodos.
# Singleton
Patron de diseño que usara ModelMapper para crear instancia del objeto.
## Base de Datos en MySQL Workbench

# Tabla Direccion
# Tabla Cliente
# Tabla Cliente_Prestamos
# Tabla Prestamos
# Tabla Tabla_Amortizaciones


Un cliente no puede tener mas de dos direcciones,la tasa de interes cambia segun el tipon de prestamo

***Las tasas de interés para cada tipo de préstamos estarán en el archivo de propiedades
(application.properties) una para cada tipo. Estas tasas las deberá leer desde este archivo.***
La relacion de M:M entre cliente y prestamos

## Swagger 
Se usa la dependecia Springdoc
y se inicializa o verifica en 
http://localhost:8080/swagger-ui.html.
