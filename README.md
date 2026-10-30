# E-Commerce Backend (Spring Boot)

REST API for an e-commerce store: authentication (JWT), product catalogue, cart, orders, reviews, wishlist and an admin dashboard. Payment gateway integration is planned (see [Roadmap](#roadmap)).

## Tech stack

| Area | Technology |
|---|---|
| Language / Build | Java 21, Maven |
| Framework | Spring Boot 3.5.6 (Web, Data JPA, Validation, Security) |
| Database | MySQL (Hibernate `ddl-auto=update`) |
| Auth | Stateless JWT (JJWT 0.13.0), BCrypt password hashing |
| Other | Lombok, hand-written DTO mappers |

## Project structure

```
src/main/java/com/ecommerce
├── Security/     JWT filter, JwtUtil, SecurityConfig, UserPrincipal, CustomUserDetailsService
├── controller/   REST controllers (thin, no business logic)
├── service/      Business logic and transactions
├── repository/   Spring Data JPA repositories
├── entity/       JPA entities
├── dto/          request/ and response/ objects (entities are never returned directly)
├── mapper/       Entity <-> DTO mappers
├── enums/        Role, OrderStatus, PaymentStatus, AddressType, DiscountType
└── exception/    Custom exceptions + GlobalExceptionHandling
```

## Getting started

### Prerequisites
- JDK 21
- Maven 3.9+
- MySQL 8+
### 1. Create the database
```sql
CREATE DATABASE ecommerce_db;
```

### 2. Set environment variables
`application.properties` reads secrets from the environment, so nothing sensitive is committed.

| Variable | Description |
|---|---|
| `DB_PASSWORD` | Password of the MySQL `root` user (change the username in `application.properties` if needed) |
| `JWT_SECRET` | Secret used to sign tokens. Use a long random string (at least 32 characters) |

```bash
# Linux / macOS
export DB_PASSWORD=your_db_password
export JWT_SECRET=$(openssl rand -base64 48)
 
# Windows (PowerShell)
$env:DB_PASSWORD="your_db_password"
$env:JWT_SECRET="a-long-random-secret-string-at-least-32-chars"
```

### 3. Run
```bash
mvn spring-boot:run
```
The API starts on `http://localhost:8080`. Tables are created automatically on first run.

### 4. Create an admin user
Registration always creates a `CUSTOMER`. Register normally, then promote the user in MySQL:
```sql
UPDATE users SET role = 'ADMIN' WHERE email = 'you@example.com';
```
Log in again afterwards so the new token carries the `ADMIN` role.

## Authentication

1. `POST /api/v1/auth/register` to create an account.
2. `POST /api/v1/auth/login` returns a token.
3. Send it on every protected request:
```
   Authorization: Bearer <token>
```

Tokens expire after 24 hours (`jwt.expiration-ms`). Deactivated users cannot log in and their existing tokens stop working.

| Status | Meaning |
|---|---|
| 401 | Missing, invalid or expired token |
| 403 | Authenticated but not allowed (for example a customer calling an admin endpoint) |

## API endpoints

Access: **Public** = no token, **User** = any logged-in user, **Admin** = `ROLE_ADMIN`.
All list endpoints accept `?page=0&size=10&sort=field,asc|desc` and return a page object:
`{ "content": [...], "page": { "size", "number", "totalElements", "totalPages" } }`.

### Auth
| Method | Path | Access | Description |
|---|---|---|---|
| POST | `/api/v1/auth/register` | Public | Register a new customer |
| POST | `/api/v1/auth/login` | Public | Login, returns JWT |

### Users
| Method | Path | Access | Description |
|---|---|---|---|
| GET | `/api/v1/users/me` | User | My profile |
| PUT | `/api/v1/users/me` | User | Update name / phone |
| PATCH | `/api/v1/users/me/password` | User | Change password (old + new) |
| DELETE | `/api/v1/users/me` | User | Deactivate my account |
| GET | `/api/v1/users` | Admin | List all users |
| GET | `/api/v1/users/{id}` | Admin | Get a user |
| DELETE | `/api/v1/users/{id}` | Admin | Deactivate a user |
| PATCH | `/api/v1/users/{id}/activate` | Admin | Reactivate a user |

### Addresses
| Method | Path | Access | Description |
|---|---|---|---|
| POST | `/api/v1/addresses` | User | Add address (first one becomes default) |
| GET | `/api/v1/addresses` | User | My addresses |
| PUT | `/api/v1/addresses/{id}` | User | Update address |
| PATCH | `/api/v1/addresses/{id}/default` | User | Mark as default |
| DELETE | `/api/v1/addresses/{id}` | User | Delete address |

### Categories
| Method | Path | Access | Description |
|---|---|---|---|
| GET | `/api/v1/categories` | Public | List active categories |
| GET | `/api/v1/categories/tree` | Public | Category tree with children |
| GET | `/api/v1/categories/{id}` | Public | Get one category |
| POST | `/api/v1/categories` | Admin | Create (optional `parentId`) |
| PUT | `/api/v1/categories/{id}` | Admin | Update |
| DELETE | `/api/v1/categories/{id}` | Admin | Deactivate (blocked while it has active sub-categories) |

### Products
| Method | Path | Access | Description |
|---|---|---|---|
| GET | `/api/v1/products` | Public | List and search active products: `keyword`, `categoryId`, `brand`, `minPrice`, `maxPrice` + paging/sort. Without filters it returns all active products (thumbnail only, no image list) |
| GET | `/api/v1/products/getAll` | Public | All active products (with images), paged |
| GET | `/api/v1/products/{id}` | Public | Product details with images |
| POST | `/api/v1/products` | Admin | Create product |
| PUT | `/api/v1/products/{id}` | Admin | Update product (only the fields you send change) |
| DELETE | `/api/v1/products/{id}` | Admin | Deactivate product (soft delete) |
| PATCH | `/api/v1/products/{id}/activate` | Admin | Reactivate product |
| PATCH | `/api/v1/products/{id}/stock` | Admin | Add to stock `{stockQuantity}` |
| POST | `/api/v1/products/{id}/images` | Admin | Add image |
| PATCH | `/api/v1/products/{productId}/images/{imageId}/primary` | Admin | Mark image as primary |
| DELETE | `/api/v1/products/{productId}/images/{imageId}` | Admin | Delete image |

### Cart
| Method | Path | Access | Description |
|---|---|---|---|
| GET | `/api/v1/cart` | User | My cart (created on first use) |
| POST | `/api/v1/cart/items` | User | Add item `{productId, quantity}` (same product merges quantity) |
| PUT | `/api/v1/cart/items/{itemId}` | User | Set item quantity. Body is a plain number, e.g. `3` |
| DELETE | `/api/v1/cart/{itemId}` | User | Remove one item |
| DELETE | `/api/v1/cart` | User | Delete the whole cart |

### Orders
| Method | Path | Access | Description |
|---|---|---|---|
| POST | `/api/v1/orders` | User | Place order from cart `{addressId}` |
| GET | `/api/v1/orders/my` | User | My orders |
| GET | `/api/v1/orders/{id}` | Owner / Admin | Order details |
| PATCH | `/api/v1/orders/{id}/cancel` | Owner / Admin | Cancel (only `PENDING` / `CONFIRMED`), stock is restored |
| GET | `/api/v1/orders`| Admin | All orders|
| PATCH | `/api/v1/orders/{id}/status` | Admin | Move to the next status |

### Reviews
| Method | Path | Access | Description |
|---|---|---|---|
| POST | `/api/v1/review` | User | Review a product `{productId, rating, comment}`. Allowed only after that product's order is `DELIVERED`, one review per product |
| GET | `/api/v1/review/{productId}/reviews` | Public | Reviews of a product |
| PUT | `/api/v1/review/{id}` | Owner | Edit my review |
| DELETE | `/api/v1/review/{reviewId}` | Owner / Admin | Delete review |

The product's `averageRating` is recalculated on every create, edit and delete.

### Wishlist
| Method | Path | Access | Description |
|---|---|---|---|
| POST | `/api/v1/wishlist` | User | Add product `{productId}` |
| GET | `/api/v1/wishlist` | User | My wishlist |
| DELETE | `/api/v1/wishlist/{wishlistItemId}` | User | Remove item |

### Admin dashboard
| Method | Path | Access | Description |
|---|---|---|---|
| GET | `/api/v1/dashboard/summary` | Admin | Users, products, orders, pending orders, revenue |
| GET | `/api/v1/dashboard/low-stock?threshold=10` | Admin | Products at or below the threshold |
| GET | `/api/v1/dashboard/top-products?limit=5` | Admin | Best sellers by quantity |

## Data model

```
User 1 ──── * Address
User 1 ──── 1 Cart ──── * CartItem * ──── 1 Product
User 1 ──── 1 WishList ──── * WishlistItem * ──── 1 Product
User 1 ──── * Order ──── * OrderItem * ──── 1 Product
User 1 ──── * Review * ──── 1 Product        
Category 1 ──── * Product
Category 1 ──── * Category                       (parent / children)
Product 1 ──── * ProductImage
```

Notes:
- Parent-to-child collections use `mappedBy` with `cascade = ALL` and `orphanRemoval = true`; all `@ManyToOne` / `@OneToOne` are `LAZY`.
- Unique constraints: `cart_item(cart_id, product_id)`, `review(user_id, product_id)`, `wishlist_item(user_id, product_id)`, `product.sku`, `users.email`, `orders.orderNumber`.
- Users, products, categories use soft delete through an `active` flag.
- An order stores a **snapshot** of the shipping address and product name and price, so later edits never change old orders.
## Business rules

- **Place order:** cart must not be empty, address must belong to the user, every product must be active. Stock is reduced atomically (`adjustStock`), so overselling is not possible. Cart is cleared afterwards.
- **Pricing:** price is the discount price if present, otherwise the normal price. Shipping is free for a subtotal of 500 or more, otherwise 50.
- **Order status flow:** `PENDING → CONFIRMED → PACKED → SHIPPED → DELIVERED`. Admin can only move one step forward. `CANCELLED` is possible only from `PENDING` or `CONFIRMED`, through the cancel endpoint.
- **Reviews:** only for a product the user received, one per product.
## Error responses

| Status | When |
|---|---|
| 400 | Validation failed, bad input, invalid status change |
| 401 | Not authenticated or wrong login credentials |
| 403 | Not allowed |
| 404 | Resource not found (also used for other users' orders and addresses) |
| 409 | Duplicate (email, SKU, category name, review, wishlist item) |
| 422 | Insufficient stock |

## Roadmap

- [ ] Payment gateway integration (Payment entity, payment method, webhook, refunds). Orders already carry a `paymentStatus`
- [ ] Coupons
- [ ] Standard JSON error body with field-level validation errors
- [ ] Forgot / reset password, email verification
- [ ] Swagger / OpenAPI docs
- [ ] Unit and integration tests

## Notes

- Tested manually with Postman. There is no automated test suite yet.
 