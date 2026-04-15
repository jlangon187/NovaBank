# NovaBank

## Descripción del proyecto

NovaBank es una aplicación de consola (CLI) para la gestión bancaria. Este módulo implementa la lógica central del sistema, permitiendo administrar clientes, gestionar cuentas bancarias, realizar operaciones financieras (depósitos, retiros y transferencias) y consultar historiales de movimientos mediante rangos de fechas.

Actualmente, el sistema almacena los datos en memoria mediante colecciones, validando estrictamente las entradas del usuario (DNI, IBAN, emails, fechas).

---

## Tecnologías utilizadas
- Java 17  
- Maven  
- JUnit 5  
- Mockito  
- Git  
- SQL (Esquema de base de datos preparado en `schema.sql`)

---

## Requisitos del sistema
Para poder ejecutar este proyecto en un entorno local, es necesario contar con:

- Java Development Kit (JDK) versión 17  
- Apache Maven instalado y configurado en las variables de entorno del sistema  

---

## Instrucciones de ejecución

### Cómo compilar
Para limpiar construcciones previas y compilar el código fuente, ejecuta:

```bash
mvn clean compile
```

### Cómo ejecutar
El proyecto está configurado con `exec-maven-plugin`. Para iniciar la aplicación:

```bash
mvn exec:java
```

### Cómo ejecutar los tests
Para ejecutar la batería de pruebas unitarias:

```bash
mvn test
```

---

## Estructura del proyecto

El código fuente se organiza siguiendo una arquitectura por capas dentro de:

```
src/main/java/com/jlanzasg/novabank/
```

### Componentes principales

- **Aplicacion.java**  
  Clase principal y punto de entrada del sistema  

- **modelo/**  
  Clases POJO del dominio:  
  - Cliente  
  - CuentaBancaria  
  - Movimiento  
  - TipoMovimiento  

- **negocio/**  
  Lógica principal y almacenamiento en memoria:  
  - Banco  

- **validaciones/**  
  Validaciones de datos introducidos por el usuario:  
  - Validacion  

- **vista/**  
  Interfaz CLI:  
  - Menu  
  - MenuCliente  
  - MenuCuentas  
  - MenuOperaciones  
  - MenuConsultas  

---

## Recursos adicionales

- `src/main/resources/schema.sql`  
  Estructura relacional preparada para futura persistencia en base de datos  

- `src/test/java/com/jlanzasg/novabank/`  
  Pruebas unitarias de las distintas capas  

---

## Enlace al repositorio

El código fuente y el historial de versiones están disponibles en GitHub: [Repositorio de NovaBank](https://github.com/jlangon187/NovaBank.git)
