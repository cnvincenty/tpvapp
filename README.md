# TPVAPP

**TPVAPP** (Terminal Punto de Venta APP) es un sistema **full stack** que incluye:
- **Backend**: Java 21 + Spring Boot (API REST)
- **Frontend**: Angular 20 (interfaz web)
- **Base de datos**: MySQL

---

## Grupo 2

- Ing. Joaquin Gonzales Mosquera
- Ing. Edson Mancilla Rodriguez
- Ing. Juan Jose Miranda Mendoza
- Ing. Milena Mollinedo Franco
- Ing. César Nilton Vincenty Funes

---

## Estructura del Proyecto

```
backend/ # Backend Java Spring Boot
├── src/...
├── pom.xml
└── recursos/...
frontend/ # Frontend Angular 20
├── src/...
├── angular.json
└── package.json
```

---

## Requisitos Técnicos

- Java 21
- Maven 3.8+
- Node.js 20+
- Angular CLI 20+
- Git
- MySQL 8+

**Servidor de Producción:**
- Java 21
- MySQL 8+
- Nginx (para servir el frontend)
- Acceso a internet para instalar dependencias

---

### Requisito previo
Crear un carpeta recursos

c:\despliegue\backend\recursos

### Base de Datos credenciales

Usuario: tpvappusuario
Clave: Cl4v3S3cR3t4
Puerto: 3306
Host: localhost
Base: tpvapp

### Backend – Variables de Entorno

Editar backend/src/main/resources/application.properties:

```
server.port=9595

spring.datasource.hikari.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/tpvapp?createDatabaseIfNotExist=true
spring.datasource.username=tpvappusuario
spring.datasource.password=Cl4v3S3cR3t4
```

### Frontend – Configuración de API

En frontend/src/environments/environment.prod.ts:

```
export const environment = {
  production: true,
  apiUrl: 'http://<backend-host>:9595'
};
```

## Compilación y Empaquetado

### Backend (Spring Boot)

```
$ cd backend
$ mvn clean install package
```

### Frontend (Angular)

```
$ cd frontend
$ npm install
$ npm run build
```
