🍽️ Tasty Town — Full-Stack Food Ordering Application
Tasty Town is a full-stack food ordering web application built with React + Vite on the frontend and Spring Boot + Spring Data JPA + PostgreSQL on the backend.
The application supports customer authentication, food discovery, categories, cart management, order placement, order history, image uploads, and an admin panel for managing foods, categories, and orders.
---
✨ Key Features
Customer
User registration and JWT-based login
Browse food items and categories
Search and filter foods by category
View food details and food images
Add, update, and remove cart items
Place food orders with contact and address information
View personal order history
Track order status
Admin
Admin authentication and role-based authorization
Add and delete food categories
Add, update, and delete food items
Upload food images
View recent orders
Update order status
Backend
RESTful APIs with Spring Boot
Spring Data JPA persistence
PostgreSQL database
JWT authentication with Spring Security
BCrypt password hashing
JPA auditing for selected entities
Swagger/OpenAPI documentation
Automatic startup creation of the configured default admin account
Seeded food/category data and image import support
---
🛠️ Tech Stack
Layer	Technologies
Frontend	React 19, Vite, React Router, Axios, Bootstrap 5, Bootstrap Icons, React Toastify, React Slick, Sass
Backend	Java 17, Spring Boot 4, Spring MVC, Spring Data JPA, Spring Security
Database	PostgreSQL
Authentication	JWT + BCrypt
API Documentation	Springdoc OpenAPI / Swagger UI
Build Tools	Maven, npm
Development	VS Code / IntelliJ IDEA / Eclipse
---
📁 Project Structure
```text
tasty-town-photos-added-main/
├── backend/
│   ├── src/main/java/com/tastytown/backend/
│   │   ├── audit/              # JPA auditing support
│   │   ├── config/             # Security, CORS, Swagger, data seeding
│   │   ├── constants/          # Roles and order status enums
│   │   ├── controller/         # REST controllers
│   │   ├── dto/                # Request/response DTOs
│   │   ├── exception/           # Exception handling
│   │   ├── mapper/              # Entity/DTO mappers
│   │   ├── model/               # JPA entities
│   │   ├── repository/          # Spring Data repositories
│   │   ├── security/jwt/        # JWT filter/utilities
│   │   └── service/             # Business logic
│   ├── src/main/resources/
│   │   └── application.yaml    # Database/server/JWT configuration
│   └── pom.xml
│
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   ├── context/             # Auth/cart/category/store state
│   │   ├── layout/
│   │   ├── pages/               # Customer and admin pages
│   │   ├── service/              # Axios API clients
│   │   └── router.jsx
│   ├── public/
│   ├── package.json
│   └── vite.config.js
│
└── README.md
```
---
🗄️ Database Design
The current JPA model contains these main database entities:
`users` — registered customers and administrators
`category` — food categories
`food` — food catalogue records
`cart` — one active cart per user
`cart_item` — food/quantity records inside a cart
`orders` — placed orders belonging to users
`order_item` — item snapshots stored inside an order
The application uses PostgreSQL and Hibernate with `ddl-auto: update`, so the schema is created/updated from the JPA entity mappings during development.
ER Diagram
The diagram below represents the relationships defined by the current Java entities.
```mermaid
erDiagram
    USERS ||--o| CART : owns
    CART ||--|{ CART_ITEM : contains
    FOOD ||--o{ CART_ITEM : added_to
    CATEGORY ||--o{ FOOD : classifies
    USERS ||--o{ ORDERS : places
    ORDERS ||--|{ ORDER_ITEM : contains

    USERS {
        string user_id PK
        string username
        string user_email UK
        string user_password
        string role
        datetime created_date
        string created_by
        datetime updated_date
        string updated_by
    }

    CART {
        string cart_id PK
        string user_id FK UK
        datetime created_date
        string created_by
        datetime updated_date
        string updated_by
    }

    CART_ITEM {
        string cart_item_id PK
        string cart_id FK
        string food_id FK
        int quantity
    }

    CATEGORY {
        string category_id PK
        string category_name
        datetime created_date
        string created_by
        datetime updated_date
        string updated_by
    }

    FOOD {
        string food_id PK
        string food_name
        string food_description
        double food_price
        string food_image
        string category_id FK
        datetime created_date
        string created_by
        datetime updated_date
        string updated_by
    }

    ORDERS {
        string order_id PK
        string user_id FK
        double total_amount
        datetime order_date
        string order_status
        string contact_info
        string address_info
        datetime created_date
        string created_by
        datetime updated_date
        string updated_by
    }

    ORDER_ITEM {
        string order_item_id PK
        string order_id FK
        string food_name
        double food_price
        int quantity
    }
```
Relationship Summary
Relationship	Cardinality	Meaning
`USERS → CART`	1 : 0..1	A user can have one active cart; a cart belongs to one user.
`CART → CART_ITEM`	1 : many	A cart contains multiple cart items.
`FOOD → CART_ITEM`	1 : many	A food can appear in many users' carts.
`CATEGORY → FOOD`	1 : many	One category contains many food items.
`USERS → ORDERS`	1 : many	A user can place multiple orders.
`ORDERS → ORDER_ITEM`	1 : many	An order contains one or more order items.
Important Order Design Note
`OrderItem` does not contain a `food_id` relationship to `Food`. Instead, the order stores `foodName` and `foodPrice` directly. This works as an order snapshot, preserving the item details used when the order was placed even if the catalogue price/name later changes.
---
🔐 Authentication & Authorization
The backend uses Spring Security + JWT.
Roles
```text
ROLE_USER
ROLE_ADMIN
```
Public APIs
User login
User registration
GET food APIs
GET category APIs
Swagger/OpenAPI resources
Protected APIs
Cart operations require authentication
Order operations require authentication
Food create/update/delete operations require admin access
Category create/update/delete operations require admin access
Admin registration endpoint requires an authenticated admin
Passwords are encoded with BCrypt before storage.
---
🌐 API Endpoints
The backend context path is:
```text
http://localhost:1200/tasty-town
```
Authentication
```text
POST /api/v1/auth/login
POST /api/v1/auth/register
POST /api/v1/auth/register-admin
```
Categories
```text
GET    /api/v1/categories
GET    /api/v1/categories/{catId}
POST   /api/v1/categories
PUT    /api/v1/categories
DELETE /api/v1/categories/{catId}
```
Foods
```text
GET    /api/v1/foods
GET    /api/v1/foods/{foodId}
GET    /api/v1/foods/{imageName}/image
GET    /api/v1/foods/paginated-foods
POST   /api/v1/foods
POST   /api/v1/foods/image/{foodId}/food
PUT    /api/v1/foods/{foodId}
DELETE /api/v1/foods/{foodId}
```
Cart
```text
POST   /api/v1/cart
GET    /api/v1/cart
PUT    /api/v1/cart
DELETE /api/v1/cart/{foodId}/food
DELETE /api/v1/cart
```
Orders
```text
POST   /api/v1/orders
GET    /api/v1/orders/user
GET    /api/v1/orders
PUT    /api/v1/orders/{orderId}/order/status
```
---
⚙️ Local Setup
1. Prerequisites
Install:
Java 17+
Maven 3.9+ (or use the included Maven Wrapper)
Node.js + npm
PostgreSQL
2. Create the PostgreSQL Database
Create a database named:
```sql
CREATE DATABASE "tasty-town";
```
Then configure the database credentials in:
```text
backend/src/main/resources/application.yaml
```
Current development configuration expects PostgreSQL on the local machine and database name `tasty-town`.
3. Start the Backend
Windows
```powershell
cd backend
./mvnw.cmd spring-boot:run
```
macOS / Linux
```bash
cd backend
./mvnw spring-boot:run
```
Backend URL:
```text
http://localhost:1200/tasty-town
```
Swagger UI:
```text
http://localhost:1200/tasty-town/swagger-ui/index.html
```
4. Start the Frontend
Open a second terminal:
```bash
cd frontend
npm install
npm run dev
```
Frontend URL:
```text
http://localhost:5173
```
The current frontend service files call the backend through:
```text
http://localhost:1200/tasty-town/api/v1
```
---
👤 Default Admin
At application startup, `AppConfig` checks the configured `DEFAULT.ADMIN.EMAIL` and creates the administrator account when it does not already exist.
The values are defined in `backend/src/main/resources/application.yaml`.
> **Security:** Do not use the development credentials or JWT secret from the repository for a real deployment. Move database passwords, JWT secrets, and admin credentials to environment variables or a secure secret manager before publishing or deploying the application.
---
📦 Food Image Storage
Food images are stored under the backend image directory configured by:
```yaml
upload:
  image:
    path: images
```
The backend exposes image retrieval through:
```text
GET /api/v1/foods/{imageName}/image
```
The project also contains seeded image assets used by the data seeding logic.
---
🔄 Application Flow
```text
Customer
   ↓
React + Vite Frontend
   ↓  Axios / REST API
Spring Boot Backend
   ↓
JWT + Spring Security
   ↓
Service Layer
   ↓
Spring Data JPA
   ↓
PostgreSQL
```
Typical ordering flow:
```text
Register/Login
    ↓
Browse Categories / Foods
    ↓
Add Food to Cart
    ↓
Update Cart Quantity
    ↓
Place Order
    ↓
Create Order + Order Items
    ↓
Admin Updates Order Status
    ↓
Customer Views Order History
```
---
🧩 Frontend Pages
Customer Pages
Home
Explore Foods
Food Details
Login
Register
Cart
Place Order
Orders
Admin Pages
Orders
Add Food
Add Category
Categories
Foods
The React application uses contexts for authentication, cart, category, and application/store state.
---
🧪 Build & Test
Backend
```bash
cd backend
./mvnw test
```
Frontend
```bash
cd frontend
npm run lint
npm run build
```
---
🚀 Production Checklist
Before deploying the application:
Replace hardcoded PostgreSQL credentials with environment variables.
Replace the development JWT secret with a strong secret stored outside source control.
Replace the default admin password and review the admin-registration flow.
Configure the frontend API base URL for the deployed HTTPS backend.
Configure production CORS origins instead of allowing only the local Vite origin.
Use database migration tooling such as Flyway or Liquibase for production schema management instead of relying only on `ddl-auto: update`.
Store uploaded food images in production object storage or a persistent volume.
Enable HTTPS for the deployed frontend and backend.
---
📌 Project Highlights
Tasty Town demonstrates practical full-stack development concepts including:
Component-based React UI development
REST API integration with Axios
JWT authentication and authorization
Role-based admin access
JPA entity relationships
PostgreSQL database design
Cart and order lifecycle management
Multipart image upload handling
DTO/entity mapping
Exception handling
API documentation with Swagger
Maven and npm based builds
---
📄 License
This project is intended for learning, portfolio, and demonstration purposes.
