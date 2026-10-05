# Mjengo CMS Backend

Backend services for **Mjengo CMS**, a construction-site operations and inventory management platform. The system helps construction teams manage materials, warehouses, suppliers, stock movement, daily site activity, reporting, and user access from a set of focused Spring Boot services.

## What the platform provides

- User authentication, roles, and permissions
- Construction and site-activity management
- Daily activity logs and material-consumption tracking
- Material, category, warehouse, and supplier management
- Receiving, issuing, returning, damaging, and losing stock
- Material movement ledgers and audit history
- Inventory dashboards and operational reports
- Scheduled low-stock alerts and daily reports
- Service discovery and API routing through Eureka and Spring Cloud Gateway

## Architecture

```text
   API Gateway :8090
          │
          ├── Authentication Service :8081
          ├── Storekeeper Service    :8082
          └── Site Operations Service:8084

        Eureka Server :8761
        Shared DTOs   (Maven module)
```

The services register with Eureka and are reached through the API Gateway. Each business service owns its domain and its database. RabbitMQ is used where asynchronous messaging is required, while REST clients support service-to-service requests.

## Services

| Module | Application name | Port | Responsibility |
| --- | --- | ---: | --- |
| `eurekaserverservice` | `eureka-server-service` | `8761` | Service registration and discovery |
| `apigatewayservice/ApiGateway-Service` | `api-gateway-service` | `8090` | Client entry point and request routing |
| `authenticationservice` | `authentication-service` | `8081` | Authentication, users, roles, and permissions |
| `storekeeperservice/storekeeperservice` | `store-keeper-service` | `8082` | Inventory, suppliers, warehouses, and stock movement |
| `siteoperationsservice` | `site-operations-service` | `8084` | Construction sites, daily logs, dashboards, and reports |
| `mjengoshareddtos` | `mjengoshareddtos` | — | Shared DTOs and contracts |

The repository also contains an `authservice` module with a similar authentication package. Confirm which authentication module is intended for your deployment before starting both, because the two modules use the same application name and port.

## Technology stack

- Java and Spring Boot
- Spring Cloud Netflix Eureka
- Spring Cloud Gateway
- Spring Data JPA and Hibernate
- Spring Security and JWT
- MySQL
- Redis
- RabbitMQ
- Thymeleaf and PDF reporting
- Cloudinary file storage
- Maven

## Prerequisites

Install and run:

- JDK compatible with the Spring Boot version used by the target service
- MySQL
- Redis
- RabbitMQ
- Git

The repository includes a Maven wrapper in each service, so a system-wide Maven installation is optional.

## Local setup

### 1. Clone the repository

```bash
git clone https://github.com/sololemons/MjengoCMS.git
cd MjengoCMS
```

### 2. Create the MySQL databases

The checked-in configuration uses these local database names:

```sql
CREATE DATABASE mjengoauthenticationservice;
CREATE DATABASE mjengostorekeeperservice;
CREATE DATABASE mjengositeoperationsservice;
```

Update the JDBC URLs, credentials, and other environment-specific settings before running the services. Do not commit production passwords or tokens.

### 3. Start infrastructure

Start MySQL, Redis, and RabbitMQ using their local or managed installations. The default local settings currently expect:

| Dependency | Default host | Default port |
| --- | --- | ---: |
| MySQL | `localhost` | `3306` |
| Redis | `localhost` | `6379` |
| RabbitMQ | `localhost` | `5672` |

### 4. Start the services

Start Eureka first, then the gateway and business services:

```bash
cd eurekaserverservice
./mvnw spring-boot:run
```

In separate terminals:

```bash
cd apigatewayservice/ApiGateway-Service
./mvnw spring-boot:run
```

```bash
cd authenticationservice
./mvnw spring-boot:run
```

```bash
cd storekeeperservice/storekeeperservice
./mvnw spring-boot:run
```

```bash
cd siteoperationsservice
./mvnw spring-boot:run
```

The gateway is available at `http://localhost:8090`. Confirm the route definitions in the gateway configuration before integrating an API client.

## Building and testing

Run service-level tests from the relevant module:

```bash
cd authenticationservice
./mvnw test
```

```bash
cd storekeeperservice/storekeeperservice
./mvnw test
```

```bash
cd siteoperationsservice
./mvnw test
```

Build an individual service with:

```bash
./mvnw clean package
```

Before opening a pull request, verify the service builds, tests pass, and the application starts with the documented dependencies available.

## Performance considerations

Dashboard reporting uses database-side filtering and aggregation where appropriate. For example, material-usage reports can filter by construction and date, join daily logs with consumed materials, group by material name, calculate totals, sort by usage, and return only the requested page.

Interface-based projections are used for summary results so the application does not load complete entities when it only needs fields such as:

- Material name
- Total quantity consumed

This reduces data transfer, object creation, memory use, and application-side processing.

## Security and configuration

- Keep database, JWT, RabbitMQ, email, and Cloudinary credentials outside source control.
- Use environment variables or a secrets manager for deployment values.
- Review CORS and gateway rules before exposing the API publicly.
- Protect administrative and inventory-changing endpoints with authentication and permissions.
- Validate uploaded files, request payloads, and query parameters.
- Do not use development credentials in production.

The repository contains local development configuration files. Treat them as templates and replace machine-specific values when deploying.

## Repository structure

```text
MjengoCMS/
├── apigatewayservice/
│   └── ApiGateway-Service/
├── authenticationservice/
├── authservice/
├── eurekaserverservice/
├── mjengoshareddtos/
├── siteoperationsservice/
├── storekeeperservice/
│   └── storekeeperservice/
├── google-java-formatter.xml
└── README.md
```

## Contribution workflow

1. Create a feature branch from `main`.
2. Keep changes scoped to the relevant service.
3. Add or update tests for changed behaviour.
4. Run the affected service's test and package commands.
5. Document new configuration, endpoints, or dependencies.
6. Open a pull request with a clear summary and validation steps.

## Author

**Solomon Ndimu Ngandu**

- GitHub: [@sololemons](https://github.com/sololemons)
- LinkedIn: [Solomon Ndimu](https://linkedin.com/in/solomondimu)

## License

No license file is currently included in this repository. Add an explicit license before distributing the project or accepting external contributions.
