# ShopEase: online shopping cart (Java Servlets + JSP + MySQL)

This copy is for **Apache Tomcat 9 (package javax.servlet)**.
Using Tomcat 10.1 or 11 instead? Use `ShopEase-Tomcat10-11.zip`. The two differ only in `javax` vs `jakarta`, and the wrong pair gives a 404 or "ClassNotFoundException".

## What you need
- JDK 17 or newer
- Eclipse IDE for Enterprise Java and Web Developers
- Apache Tomcat 9 (package javax.servlet)
- MySQL 8 (MySQL Workbench makes step 1 easy)

## Set up (about 10 minutes)
1. **Create the database.** In MySQL Workbench open `database/shopease.sql` and run it (lightning bolt). It creates the `shopease` database, the tables, 21 sample products and an admin user. Running it again resets everything.
2. **Tell the app your MySQL password.** Open `src/main/java/com/shopease/util/DBConnection.java` and change `USER` and `PASSWORD`.
3. **Import the project.** In Eclipse: File > Import > Maven > Existing Maven Projects > pick this folder > Finish. Wait until the progress bar at the bottom right finishes (Maven downloads the libraries).
4. **Add Tomcat to Eclipse (first time only).** Window > Preferences > Server > Runtime Environments > Add > Apache Tomcat 9 (package javax.servlet) > choose the folder where you unzipped Tomcat.
5. **Run.** Right-click the project > Run As > Run on Server > choose Tomcat > Finish. Open http://localhost:8080/ShopEase/

**Logins:** admin = `admin@shopease.com` / `admin123`. For a customer, click Sign up.

### No Maven? Import it by hand
1. File > New > Dynamic Web Project. Name it `ShopEase`, pick your Tomcat runtime, leave "Generate web.xml" unticked.
2. Copy this zip's `src/main/java/*` into the project's `src/main/java` and `src/main/webapp/*` into `src/main/webapp` (older Eclipse names them `src` and `WebContent`).
3. Download these jars and put them in `src/main/webapp/WEB-INF/lib`:
   - https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.4.0/mysql-connector-j-8.4.0.jar
   - https://repo1.maven.org/maven2/javax/servlet/jstl/1.2/jstl-1.2.jar
4. Project > Clean, then Run on Server as above.

## How it is organised (MVC, layered)
```
src/main/java/com/shopease
  model/       User, Product, Cart, CartItem, Order, OrderItem, Category, OrderStatus, Role
  dao/         Dao<T> (interface), ProductDao, UserDao, OrderDao   (JDBC + MySQL)
  service/     CartService, OrderService                           (business rules)
  payment/     PaymentMethod, AbstractPayment, CashOnDelivery, UpiPayment, CardPayment, PaymentFactory
  controller/  BaseServlet + one servlet per page                  (Controller)
  filter/      EncodingFilter (UTF-8), AuthFilter (login and admin checks)
  exception/   ShopException, OutOfStockException, DataAccessException
  util/        DBConnection, PasswordUtil (PBKDF2), Money
src/main/webapp
  WEB-INF/views/   the JSP pages (View). Not reachable directly from the browser.
  css/style.css    all styling       js/app.js   add-to-cart without reloading
database/shopease.sql
```

## OOP ideas to point out in your viva
| Concept | Where |
|---|---|
| Encapsulation | private fields with getters and setters in every `model` class |
| Inheritance | `User`, `Product`, `Order` extend `BaseEntity`; payments extend `AbstractPayment`; servlets extend `BaseServlet` |
| Abstraction | abstract `BaseEntity.describe()`, interfaces `Dao<T>` and `PaymentMethod` |
| Polymorphism | each `PaymentMethod` returns its own `getInitialStatus()`; every entity overrides `describe()` |
| Interfaces and generics | `Dao<T>` implemented by three DAOs |
| Exception handling | custom checked `ShopException` / `OutOfStockException`; unchecked `DataAccessException`; transaction rollback in `OrderDao.create` |
| Collections | `LinkedHashMap` inside `Cart`, `List` and `Collection` everywhere |
| Enums with data | `OrderStatus` (label and colour), `Role` |
| Design patterns | MVC, DAO, Factory (`PaymentFactory`), Strategy-style payments |

## Features
Search, category filter and sorting; product pages; cart with live stock and price checks and a free-delivery meter;
sign up and log in (salted PBKDF2 passwords); checkout with validation; order history; demo payments (nothing is charged);
admin pages to add, edit and delete products and to update order status. Add-to-cart uses fetch() and also works without JavaScript.

## If something goes wrong
| Symptom | Fix |
|---|---|
| `Communications link failure` or `Access denied` | MySQL is not running, or the password in `DBConnection.java` is wrong |
| `Unknown database 'shopease'` | Run `database/shopease.sql` (step 1) |
| HTTP 404 on every page, or `javax`/`jakarta` ClassNotFoundException | You used the zip for the other Tomcat version |
| `The absolute uri ... cannot be resolved` (JSTL) | Maven has not finished: right-click project > Maven > Update Project, or add the jars by hand |
| Admin login fails | You changed the data; run `shopease.sql` again |
| Port 8080 already in use | Stop the other program, or change the Tomcat port in Eclipse's Servers view |
