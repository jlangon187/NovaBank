# NovaBank - Plataforma de Microservicios (Modulo 4)

NovaBank es un backend bancario migrado desde una arquitectura monolitica hacia una arquitectura de microservicios sincronos, con seguridad centralizada, descubrimiento de servicios y configuracion externalizada.

## 1. Contexto de migracion

- Modulo 1: CLI + estructuras en memoria.
- Modulo 2: CLI + JDBC + PostgreSQL.
- Modulo 3: Monolito REST con Spring Boot.
- Modulo 4 (actual): sistema distribuido con servicios independientes, API Gateway, Service Discovery y patrones de resiliencia.

## 2. Topologia de servicios

| Servicio | Puerto | Rol | Responsabilidad | Propiedad de datos |
|---|---:|---|---|---|
| <img src="https://img.shields.io/badge/Service-Eureka_Server-00A1E0?style=flat-square&logo=spring&logoColor=white" alt="Eureka Server" /> | 8761 | Infraestructura | Registro y descubrimiento de servicios | N/A |
| <img src="https://img.shields.io/badge/Service-Config_Server-4A5568?style=flat-square&logo=spring&logoColor=white" alt="Config Server" /> | 8888 | Infraestructura | Configuracion centralizada por entorno | N/A |
| <img src="https://img.shields.io/badge/Service-API_Gateway-111827?style=flat-square&logo=spring&logoColor=white" alt="API Gateway" /> | 8080 | Infraestructura | Punto unico de entrada, enrutado y filtro JWT | N/A |
| <img src="https://img.shields.io/badge/Service-Auth_Service-1F2937?style=flat-square&logo=springsecurity&logoColor=white" alt="Auth Service" /> | 8084 | Seguridad | Autenticacion, emision y validacion de token | `novabank_usuarios` |
| <img src="https://img.shields.io/badge/Service-Cliente_Service-0EA5E9?style=flat-square&logo=springboot&logoColor=white" alt="Cliente Service" /> | 8081 | Negocio | Gestion del ciclo de vida de clientes | `novabank_clientes` |
| <img src="https://img.shields.io/badge/Service-Cuenta_Service-22C55E?style=flat-square&logo=springboot&logoColor=white" alt="Cuenta Service" /> | 8082 | Negocio | Gestion de cuentas y saldo | `novabank_cuentas` |
| <img src="https://img.shields.io/badge/Service-Operacion_Service-F59E0B?style=flat-square&logo=springboot&logoColor=white" alt="Operacion Service" /> | 8083 | Negocio | Depositos, retiros, transferencias y movimientos | `novabank_operaciones` |

## 3. Stack tecnologico

| Capa | Tecnologia + version | Notas |
|---|---|---|
| Lenguaje | <img src="https://img.shields.io/badge/Java-17-007396?style=flat-square&logo=openjdk&logoColor=white" alt="Java 17" /> | Baseline del proyecto |
| Framework core | <img src="https://img.shields.io/badge/Spring_Boot-4.0.6-6DB33F?style=flat-square&logo=springboot&logoColor=white" alt="Spring Boot 4.0.6" /> | Parent BOM |
| Ecosistema cloud | <img src="https://img.shields.io/badge/Spring_Cloud-2025.1.1-0A66C2?style=flat-square&logo=spring&logoColor=white" alt="Spring Cloud 2025.1.1" /> | Discovery, Config, Gateway, OpenFeign |
| Descubrimiento | <img src="https://img.shields.io/badge/Netflix_Eureka-via_Spring_Cloud-00A1E0?style=flat-square&logo=spring&logoColor=white" alt="Netflix Eureka" /> | Resolucion dinamica de endpoints |
| Configuracion centralizada | <img src="https://img.shields.io/badge/Config_Server-via_Spring_Cloud-4A5568?style=flat-square&logo=spring&logoColor=white" alt="Config Server" /> | Config remota desde Git |
| Edge/API | <img src="https://img.shields.io/badge/Spring_Cloud_Gateway-via_Spring_Cloud-111827?style=flat-square&logo=spring&logoColor=white" alt="Spring Cloud Gateway" /> | Ruteo y filtro de autenticacion |
| Comunicacion entre servicios | <img src="https://img.shields.io/badge/OpenFeign-via_Spring_Cloud-EF4444?style=flat-square&logo=openfeign&logoColor=white" alt="OpenFeign" /> | Clientes HTTP declarativos |
| Resiliencia | <img src="https://img.shields.io/badge/Resilience4j-CircuitBreaker_+_Retry-2B2D42?style=flat-square" alt="Resilience4j" /> | Tolerancia a fallos y reintentos |
| Seguridad | <img src="https://img.shields.io/badge/Spring_Security_+_jjwt-jjwt_0.12.x-1F2937?style=flat-square&logo=springsecurity&logoColor=white" alt="Spring Security y jjwt" /> | JWT stateless |
| Persistencia | <img src="https://img.shields.io/badge/Spring_Data_JPA_+_Hibernate-via_Spring_Boot-59666C?style=flat-square&logo=hibernate&logoColor=white" alt="JPA Hibernate" /> | Una BD por servicio |
| Base de datos runtime | <img src="https://img.shields.io/badge/PostgreSQL-Production-336791?style=flat-square&logo=postgresql&logoColor=white" alt="PostgreSQL" /> | Aislamiento por contexto |
| Base de datos testing | <img src="https://img.shields.io/badge/H2-Testing-003545?style=flat-square" alt="H2" /> | Pruebas en memoria |
| Testing | <img src="https://img.shields.io/badge/JUnit5_+_Mockito_+_MockMvc_+_WireMock-Testing-25A162?style=flat-square&logo=junit5&logoColor=white" alt="Testing stack" /> | Unit, slice, integracion y contrato |

## 4. Diagramas de arquitectura

### 4.1 Arquitectura global

```mermaid
flowchart LR
    U[Clientes\nPostman / Frontend] --> GW[API Gateway\n:8080]
    GW --> AU[auth-service\n:8084]
    GW --> CL[cliente-service\n:8081]
    GW --> CU[cuenta-service\n:8082]
    GW --> OP[operacion-service\n:8083]

    CL --> DB1[(novabank_clientes)]
    CU --> DB2[(novabank_cuentas)]
    OP --> DB3[(novabank_operaciones)]
    AU --> DB4[(novabank_usuarios)]

    GW -. service discovery .-> EU[Eureka\n:8761]
    AU -. registro .-> EU
    CL -. registro .-> EU
    CU -. registro .-> EU
    OP -. registro .-> EU

    CFG[Config Server\n:8888] --> GW
    CFG --> AU
    CFG --> CL
    CFG --> CU
    CFG --> OP
    GIT[(Config Git Repo)] --> CFG
```

### 4.2 Flujo de peticion autenticada

```mermaid
sequenceDiagram
    participant C as Cliente
    participant G as API Gateway
    participant A as auth-service
    participant O as operacion-service
    participant Q as cuenta-service

    C->>G: GET /operaciones/saldo/{iban} + Bearer JWT
    G->>A: /api/auth/validate?token=...
    A-->>G: true
    G->>O: Reenvio de peticion
    O->>Q: Feign getCuentaByIban(iban)
    Q-->>O: CuentaResponseDTO
    O-->>G: 200 saldo DTO
    G-->>C: 200 OK
```

### 4.3 Dependencias entre servicios (sincrono)

```mermaid
flowchart LR
    CU[cuenta-service] -->|Feign: ClienteClient| CL[cliente-service]
    OP[operacion-service] -->|Feign: CuentaClient| CU
```

### 4.4 Modelo ER (database-per-service)

```mermaid
erDiagram
    CLIENTES {
      BIGINT id PK
      STRING dni UK
      STRING nombre
      STRING apellidos
      STRING email UK
      STRING telefono UK
      DATETIME fecha_creacion
    }

    CUENTAS {
      BIGINT id PK
      STRING iban UK
      DOUBLE balance
      BIGINT cliente_id
      DATETIME fecha_creacion
      BIGINT version
    }

    MOVIMIENTOS {
      BIGINT id PK
      STRING cuenta_iban
      STRING tipo
      DOUBLE cantidad
      DATETIME fecha
    }

    USUARIOS {
      BIGINT id PK
      STRING email UK
      STRING password
    }
```

## 5. API Gateway y seguridad

Las rutas se centralizan en `C:\novabank-config-repo\api-gateway.yml`.

| Route ID | Path Predicate | Target URI | Filtro |
|---|---|---|---|
| `auth-service-route` | `/api/auth/**` | `lb://auth-service` | ninguno |
| `cliente-service-route` | `/clientes`, `/clientes/**` | `lb://cliente-service` | `AuthenticationFilter` |
| `cuenta-service-route` | `/cuentas`, `/cuentas/**` | `lb://cuenta-service` | `AuthenticationFilter` |
| `operacion-service-route` | `/operaciones/**`, `/consultas/**` | `lb://operacion-service` | `AuthenticationFilter` |

Flujo JWT:

1. El cliente obtiene token con `POST /api/auth/login`.
2. El cliente envia `Authorization: Bearer <jwt>` al Gateway.
3. El Gateway valida el token contra `auth-service /api/auth/validate`.
4. Si es valido, enruta al microservicio destino; si no, responde `401`.

## 6. Estrategia de resiliencia

Configuracion actual (desde Config Server):

- `cuenta-service` protege llamadas a `cliente-service` con Circuit Breaker + Retry.
- `operacion-service` protege llamadas a `cuenta-service` con Circuit Breaker + Retry.
- Los Feign clients incorporan fallback para degradacion controlada.

Evidencia esperada en la entrega:

1. Respuesta normal con dependencia activa.
2. Respuesta de fallback con dependencia detenida.
3. Recuperacion automatica tras reactivar la dependencia.

## 7. Testing y cobertura actual

Tipos de pruebas implementadas:

- Unitarias (servicios) con Mockito.
- Repositorio con `@DataJpaTest` + H2.
- Controlador con `@WebMvcTest` + MockMvc.
- Integracion con `@SpringBootTest`.
- Contrato Feign con WireMock.

Comando usado en este entorno:

```bash
mvn -pl cliente-service,cuenta-service,operacion-service -DforkCount=0 test
```

Resultado validado:

- `cliente-service`: 22 tests
- `cuenta-service`: 22 tests
- `operacion-service`: 24 tests
- Total: 68 tests (`BUILD SUCCESS`)

## 8. Guia de ejecucion local

Orden recomendado de arranque:

1. `eureka-server`
2. `config-server`
3. `auth-server`
4. `cliente-service`
5. `cuenta-service`
6. `operacion-service`
7. `api-gateway`

Notas:

- Config Server espera el repo en `C:/novabank-config-repo/`.
- Cada servicio consume configuracion remota y se registra en Eureka.
- El acceso externo debe realizarse por `api-gateway`.

NovaBank Modulo 4 consolida una migracion realista hacia microservicios: separacion por contexto, infraestructura compartida, contratos entre servicios y resiliencia ante fallos parciales.
