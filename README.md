---

# Backend Huerto Hogar

API REST desarrollada en **Spring Boot 3**, utilizada por la aplicación móvil “Huerto Hogar”.
Este backend permite gestionar productos, usuarios, roles, autenticación con JWT, carrito, ventas y stock.

---

## Integrantes del equipo

* **Moira Guzmán**
* **Briam Djesus**

---

## Descripción del proyecto

Este proyecto corresponde al **backend** de la aplicación móvil Huerto Hogar, orientada a un público general que desea comprar insumos, frutas, verduras, lácteos y productos orgánicos.

La API ofrece:

* Gestión completa de productos
* Carrito de compras persistente
* Sistema de ventas
* Control de stock
* Autenticación y autorización mediante JWT
* Roles de usuario (ADMIN / USER)

El backend sirve como proveedor de datos para la aplicación móvil Android desarrollada en Kotlin + Jetpack Compose.

---

# Arquitectura del proyecto

## Tecnologías principales

* Java 17
* Spring Boot 3
* Maven
* JPA / Hibernate
* MySQL
* Spring Security 6 (JWT)
* Lombok

## Estructura de paquetes (clean architecture por capas)

```
cl.huertohogar.backend
├── config          # Configuración global (CORS, Security)
├── controller      # Controladores REST
├── dto             # Objetos de transferencia (requests/responses)
├── model           # Entidades JPA (Producto, Carrito, Venta, Usuario, etc.)
├── repository      # Interfaces JpaRepository
├── security        # JWT, filtros y configuración de seguridad
└── service         # Lógica de negocio
```

---

# Entidades principales

## Producto

* id_producto
* nombre
* descripcion
* precio
* stock
* categoria
* imageUrl

## Carrito

Carrito único del sistema:

* items (ItemCarrito)
* total

## ItemCarrito

* producto
* cantidad
* subtotal

## Venta

* id_venta
* fecha
* total
* detalles (DetalleVenta)

## DetalleVenta

* producto
* cantidad
* subtotal

## Usuario

* correo
* nombre
* rol
* claveHash

---

# Seguridad (JWT)

El backend utiliza autenticación basada en **JWT**, con:

* Login
* Registro
* Filtro JwtAuthenticationFilter
* Authorities según rol
* Control de accesos (ADMIN / USER)

### Endpoints públicos:

* `/auth/**`
* `GET /api/productos`

### Endpoints protegidos:

* `/api/carrito/**`
* `/api/ventas/**` (solo ADMIN)
* Modificación de productos (ADMIN)

---

# Funcionalidades implementadas

## Gestión de productos

* Crear producto
* Editar producto
* Actualizar stock
* Eliminar producto
* Listar productos

## Carrito de compras

* Agregar item
* Eliminar item
* Vaciar carrito
* Listar carrito

## Venta real

* Finalizar compra
* Crear venta
* Descontar stock
* Crear detalle_venta
* Vaciar carrito
* Devolver ID de venta

## Usuarios y roles

* Registro
* Login
* Obtener usuario autenticado
* Rol ADMIN con permisos para administrar catálogo y ventas

---

# Endpoints principales (resumen)

## Autenticación

```
POST /auth/login
POST /auth/register
```

## Productos

```
GET    /api/productos
POST   /api/productos                (ADMIN)
PUT    /api/productos/{id}           (ADMIN)
PATCH  /api/productos/{id}/stock     (ADMIN)
DELETE /api/productos/{id}           (ADMIN)
```

## Carrito

```
GET    /api/carrito
POST   /api/carrito/add?idProducto=1&cantidad=1
DELETE /api/carrito/item/{idItem}
DELETE /api/carrito/clear
POST   /api/carrito/checkout
```

## Ventas (ADMIN)

```
GET /api/ventas
```

---

# Cómo ejecutar el backend

## 1. Clonar el repositorio

```
git clone https://github.com/tuRepo/Backend-HuertoHogar
```

## 2. Configurar base de datos

Actualizar el archivo `application.properties` con el usuario y contraseña de MySQL.

## 3. Ejecutar con Maven

```
mvn spring-boot:run
```

El servidor correrá en:

```
http://localhost:9090
```

---

# Capturas (pendiente agregar)

Se recomienda incluir capturas de:

* Pruebas en Postman
* Endpoints de productos
* Carrito
* Checkout
* Base de datos MySQL

---

# Repositorio de la App Android

(Agregar enlace una vez disponible)

---

# Estado del proyecto

Backend funcional y completamente integrado con la App Android Huerto Hogar.

---
