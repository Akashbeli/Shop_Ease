-- =====================================================================
--  ShopEase  |  MySQL 8 database setup
--  Run this WHOLE file once.
--    MySQL Workbench:  File > Open SQL Script... > click the lightning bolt
--    Command line:     mysql -u root -p < shopease.sql
--  Careful: it drops and recreates the "shopease" database,
--  so running it again resets all products, users and orders.
-- =====================================================================

DROP DATABASE IF EXISTS shopease;
CREATE DATABASE shopease CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE shopease;

CREATE TABLE users (
  id            INT AUTO_INCREMENT PRIMARY KEY,
  name          VARCHAR(80)  NOT NULL,
  email         VARCHAR(120) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  role          VARCHAR(10)  NOT NULL DEFAULT 'CUSTOMER'
);

CREATE TABLE products (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  name        VARCHAR(120)  NOT NULL,
  description TEXT,
  category    VARCHAR(50)   NOT NULL,
  price       DECIMAL(10,2) NOT NULL,
  stock       INT           NOT NULL DEFAULT 0,
  rating      DECIMAL(2,1)  NOT NULL DEFAULT 4.0,
  icon        VARCHAR(30)   NOT NULL DEFAULT 'bag',
  image_url   VARCHAR(255),
  INDEX idx_products_category (category)
);

CREATE TABLE orders (
  id             INT AUTO_INCREMENT PRIMARY KEY,
  user_id        INT           NOT NULL,
  total          DECIMAL(10,2) NOT NULL,
  status         VARCHAR(20)   NOT NULL DEFAULT 'PLACED',
  payment_method VARCHAR(20)   NOT NULL,
  ship_name      VARCHAR(80)   NOT NULL,
  ship_phone     VARCHAR(20)   NOT NULL,
  ship_address   VARCHAR(255)  NOT NULL,
  created_at     DATETIME      NOT NULL,
  CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users (id)
);

-- Name, icon and price are copied into each line, so old orders stay correct
-- even if a product is edited or deleted later.
CREATE TABLE order_items (
  id           INT AUTO_INCREMENT PRIMARY KEY,
  order_id     INT           NOT NULL,
  product_id   INT           NULL,
  product_name VARCHAR(120)  NOT NULL,
  icon         VARCHAR(30)   NOT NULL DEFAULT 'bag',
  unit_price   DECIMAL(10,2) NOT NULL,
  quantity     INT           NOT NULL,
  CONSTRAINT fk_items_order   FOREIGN KEY (order_id)   REFERENCES orders (id)   ON DELETE CASCADE,
  CONSTRAINT fk_items_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE SET NULL
);

-- ---------------------------------------------------------------------
--  Sample data
-- ---------------------------------------------------------------------

-- Admin login:  admin@shopease.com  /  admin123   (stored as a salted PBKDF2 hash)
INSERT INTO users (name, email, password_hash, role) VALUES
  ('Store Admin', 'admin@shopease.com', '65536:jMDWaSGeALBrghh2ZQy3/w==:FY2G4ECvTA4omFoX7fQq/HjjQ2tACVOfjLM6JLqTYAw=', 'ADMIN');

INSERT INTO products (name, description, category, price, stock, rating, icon) VALUES
  ('Wireless Noise-Cancelling Headphones', 'Over-ear Bluetooth headphones with active noise cancellation and a 30-hour battery.', 'Electronics', 4999, 25, 4.6, 'headphones'),
  ('Smart Fitness Watch', 'AMOLED display with heart-rate and SpO2 tracking. The battery lasts up to 7 days.', 'Electronics', 3499, 40, 4.4, 'watch'),
  ('Portable Bluetooth Speaker', 'Waterproof pocket speaker with deep bass and 12 hours of playtime.', 'Electronics', 1999, 60, 4.3, 'speaker'),
  ('Mechanical Keyboard', 'Hot-swappable keyboard with RGB lighting and tactile switches.', 'Electronics', 2799, 18, 4.5, 'keyboard'),
  ('Ultralight Laptop 14-inch', 'Full HD screen, 16 GB RAM, 512 GB SSD and a battery that lasts the whole day.', 'Electronics', 54999, 8, 4.7, 'laptop'),
  ('Classic Cotton T-Shirt', 'Soft 100% cotton crew neck in a relaxed everyday fit.', 'Fashion', 599, 120, 4.2, 'shirt'),
  ('Running Sneakers', 'Cushioned and breathable, built for daily runs and long walks.', 'Fashion', 2499, 45, 4.5, 'shoes'),
  ('Everyday Backpack 25L', 'Water-resistant backpack with a padded laptop sleeve and two bottle pockets.', 'Fashion', 1299, 70, 4.4, 'backpack'),
  ('UV-Protect Sunglasses', 'Lightweight frames with polarised lenses and 100% UV protection.', 'Fashion', 899, 4, 4.1, 'sunglasses'),
  ('Ceramic Coffee Mug Set', 'Set of four glazed 300 ml mugs. Microwave and dishwasher safe.', 'Home', 799, 80, 4.3, 'coffee'),
  ('Minimal Desk Lamp', 'Dimmable LED lamp with three colour temperatures and a USB charging port.', 'Home', 1199, 35, 4.4, 'lamp'),
  ('Accent Armchair', 'Comfortable fabric armchair with solid wood legs. Easy to assemble.', 'Home', 8999, 5, 4.5, 'sofa'),
  ('Indoor Plant with Pot', 'Low-maintenance indoor plant in a ceramic pot. Does well in indirect light.', 'Home', 449, 90, 4.6, 'plant'),
  ('Clean Code', 'A handbook of agile software craftsmanship by Robert C. Martin.', 'Books', 749, 30, 4.7, 'book'),
  ('Effective Java', 'Practical best practices for the Java platform by Joshua Bloch.', 'Books', 899, 22, 4.8, 'book'),
  ('Atomic Habits', 'An easy way to build good habits and break bad ones, by James Clear.', 'Books', 499, 65, 4.7, 'notebook'),
  ('The Pragmatic Programmer', 'Tips and techniques for becoming a better developer, by Andrew Hunt and David Thomas.', 'Books', 799, 15, 4.6, 'book'),
  ('Kashmir Willow Cricket Bat', 'Full-size bat with a comfortable grip, great for leather-ball and tennis-ball cricket.', 'Sports', 1799, 15, 4.2, 'cricket'),
  ('Yoga Mat 6 mm', 'Non-slip, cushioned mat with a carry strap.', 'Sports', 699, 55, 4.4, 'yoga'),
  ('Adjustable Dumbbell Set', 'Pair of adjustable dumbbells, up to 20 kg in total, with a storage tray.', 'Sports', 2999, 20, 4.3, 'dumbbell'),
  ('Trail Bicycle 21-speed', 'Lightweight alloy frame, front suspension and disc brakes for mixed terrain.', 'Sports', 14999, 6, 4.5, 'bicycle');
