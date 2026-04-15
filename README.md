# NovaBank

## Descripción del proyecto

NovaBank es una aplicación de consola (CLI) para la gestión bancaria, construida con una arquitectura modular y escalable. Este sistema permite a los usuarios administrar clientes, gestionar cuentas bancarias, realizar operaciones financieras (depósitos, retiros y transferencias) y consultar historiales de movimientos mediante rangos de fechas.

La aplicación utiliza PostgreSQL para la persistencia de datos, garantizando la integridad y consistencia de la información bancaria. Se han implementado validaciones estrictas para las entradas del usuario (DNI, IBAN, emails, fechas) en la capa de servicio, asegurando la calidad de los datos.

---

## Tecnologías utilizadas
- Java 17  
- Maven  
- JUnit 5  
- Mockito  
- Git  
- PostgreSQL (Base de datos relacional)

---

## Requisitos del sistema
Para poder ejecutar este proyecto en un entorno local, es necesario contar con:

- Java Development Kit (JDK) versión 17  
- Apache Maven instalado y configurado en las variables de entorno del sistema  
- PostgreSQL instalado y en ejecución (versión 12 o superior recomendada)

---

## Configuración de la base de datos

### Crear la base de datos `novabank_db`

Primero, asegúrate de que tu servidor PostgreSQL esté en ejecución. Luego, crea la base de datos `novabank_db`. Puedes hacerlo a través de la consola de `psql` o una herramienta gráfica como pgAdmin:

```sql
CREATE DATABASE novabank_db;
```

### Ejecutar el esquema `schema.sql`

Una vez creada la base de datos, debes ejecutar el script `schema.sql` para crear las tablas necesarias (clientes, cuentas, movimientos).

Puedes hacerlo desde la línea de comandos:

```bash
psql -d novabank_db -U <tu_usuario_postgres> -f src/main/resources/schema.sql
```
Reemplaza `<tu_usuario_postgres>` con tu nombre de usuario de PostgreSQL.

---

## Variables de conexión a la base de datos

Las credenciales de conexión a la base de datos se configuran en el archivo `src/main/java/com/jlanzasg/novabank/config/DatabaseConnectionManager.java`.

Asegúrate de actualizar las siguientes constantes con los valores correctos para tu entorno PostgreSQL:

```java
private static final String URL = "jdbc:postgresql://localhost:5432/novabank_db"; // URL de conexión
private static final String USER = "postgres"; // Tu usuario de PostgreSQL
private static final String PASSWORD = "root"; // Tu contraseña de PostgreSQL
```

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

---

### Cómo ejecutar los tests
Para ejecutar la batería de pruebas unitarias:

```bash
mvn test
```
**Nota:** Los tests de la capa de repositorio (`com.jlanzasg.novabank.repository.Impl.*Test`) requieren que la base de datos PostgreSQL esté activa y configurada correctamente para su ejecución.

---

## Estructura del proyecto (Arquitectura por Capas)

El código fuente se organiza siguiendo una arquitectura por capas dentro de:

```
src/main/java/com/jlanzasg/novabank/
```

La aplicación se estructura en cuatro capas principales, promoviendo la separación de preocupaciones y la modularidad:

```
+---------------------+
|   Capa de Presentación  |
|      (view)         |
+---------------------+
           |
           v
+---------------------+
|    Capa de Servicio     |
|     (service)       |
+---------------------+
           |
           v
+---------------------+
|    Capa de Repositorio  |
|    (repository)     |
+---------------------+
           |
           v
+---------------------+
|   Capa de Origen de Datos   |
|       (config)      |
+---------------------+
```

### Componentes principales

-   **`Aplicacion.java`**  
    Clase principal y punto de entrada del sistema.
-   **`view/`**  
    Contiene las clases responsables de la interfaz de usuario en la consola (CLI), gestionando la interacción con el usuario y la presentación de datos. Ejemplos: `Menu`, `MenuCliente`, `MenuCuentas`, `MenuOperaciones`, `MenuConsultas`.
-   **`service/`**  
    Define la lógica de negocio y la coordinación entre los repositorios. Aquí se implementan las reglas de negocio y las validaciones. Ejemplos: `ClienteService`, `CuentaService`, `MovimientoService`, `OperacionService` y sus implementaciones en `service/Impl/`.
-   **`repository/`**  
    Contiene las interfaces que definen los contratos para el acceso a datos.
-   **`repository/Impl/`**  
    Implementaciones concretas de los repositorios que interactúan directamente con la base de datos PostgreSQL. Ejemplos: `ClienteRepositoryImpl`, `CuentaRepositoryImpl`, `MovimientoRepositoryImpl`.
-   **`model/`**  
    Clases POJO (Plain Old Java Objects) que representan las entidades del dominio: `Cliente`, `Cuenta`, `Movimiento`, `TipoMovimiento`.
-   **`config/`**  
    Clases de configuración del sistema, como `DatabaseConnectionManager.java`, que gestiona las conexiones a la base de datos.
-   **`utils/`**  
    Clases de utilidad para funciones auxiliares, como `Validacion.java` para la validación de datos.

---

## Patrones de diseño aplicados

El proyecto NovaBank integra varios patrones de diseño para mejorar la modularidad, la mantenibilidad y la escalabilidad:

-   **Singleton Pattern**:  
    Aplicado en `DatabaseConnectionManager.java` para asegurar que solo exista una instancia de la clase que gestiona las conexiones a la base de datos, controlando el acceso a un recurso compartido.
-   **Builder Pattern**:  
    Utilizado en las clases `Cliente.java` y `Cuenta.java` (a través de Lombok) y en `MovimientoFactory.java` para la construcción de objetos complejos paso a paso, lo que mejora la legibilidad y flexibilidad en la creación de instancias.
-   **Factory Method Pattern**:  
    Implementado en `MovimientoFactory.java`, que proporciona métodos estáticos para crear diferentes tipos de objetos `Movimiento` (depósito, retiro, transferencia). Esto centraliza la lógica de creación de objetos y desacopla el código cliente de las clases de movimiento concretas.
-   **Repository Pattern**:  
    Evidente en las interfaces del paquete `repository` y sus implementaciones en `repository/Impl/`. Este patrón aísla el dominio de la lógica de acceso a datos, permitiendo un cambio fácil de la tecnología de persistencia sin afectar el resto de la aplicación.

---

## Recursos adicionales

-   `src/main/resources/schema.sql`  
    Script SQL para la creación de la base de datos y sus tablas en PostgreSQL.
-   `src/test/java/com/jlanzasg/novabank/`  
    Pruebas unitarias para las distintas capas de la aplicación.

---

## Enlace al repositorio

El código fuente y el historial de versiones están disponibles en GitHub: [Repositorio de NovaBank](https://github.com/jlangon187/NovaBank.git)
