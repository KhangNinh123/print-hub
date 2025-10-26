-- printshop_mariadb.sql
-- Schema for "Web giới thiệu & bán máy in / máy scan" (MariaDB)
-- Engine: InnoDB, Charset: utf8mb4
-- Updated to match current Entity classes

-- 0) Create database
CREATE DATABASE IF NOT EXISTS printshop
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
USE printshop;

-- 1) Roles & Users
CREATE TABLE IF NOT EXISTS roles (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(30) NOT NULL UNIQUE -- 'ROLE_ADMIN', 'ROLE_CUSTOMER'
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(120) NOT NULL UNIQUE,
  password_hash VARCHAR(200) NOT NULL,
  full_name VARCHAR(120) NOT NULL,
  phone VARCHAR(20),
  default_address VARCHAR(255),
  is_active TINYINT(1) NOT NULL DEFAULT 1,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS user_roles (
  user_id INT NOT NULL,
  role_id INT NOT NULL,
  PRIMARY KEY (user_id, role_id),
  CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- 2) Catalog (Brands, Categories, Products, Product Images)
<<<<<<< HEAD
-- Updated Brand table with description
CREATE TABLE IF NOT EXISTS brands (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(80) NOT NULL UNIQUE,
  description TEXT -- Added description field
) ENGINE=InnoDB;

-- Updated Category table with description
CREATE TABLE IF NOT EXISTS categories (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(80) NOT NULL UNIQUE,
  description TEXT -- Added description field
) ENGINE=InnoDB;

-- Updated Product table to match Entity
CREATE TABLE IF NOT EXISTS products (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(200) NOT NULL UNIQUE,
  description TEXT, -- Added description field
  price DECIMAL(12,2) NOT NULL,
  stock_quantity INT NOT NULL DEFAULT 0, -- Changed from 'stock' to 'stock_quantity'
  image_url VARCHAR(255), -- Changed from 'thumbnail_url' to 'image_url'
  category_id INT NOT NULL,
  brand_id INT NOT NULL, -- Changed to NOT NULL to match Entity
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_products_brand FOREIGN KEY (brand_id) REFERENCES brands(id) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT chk_products_price CHECK (price >= 0),
  CONSTRAINT chk_products_stock CHECK (stock_quantity >= 0)
=======
CREATE TABLE IF NOT EXISTS brands (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(80) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS categories (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(80) NOT NULL UNIQUE -- 'Máy in', 'Máy scan', ...
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS products (
  id INT AUTO_INCREMENT PRIMARY KEY,
  sku VARCHAR(50) UNIQUE,
  name VARCHAR(200) NOT NULL,
  category_id INT NOT NULL,
  brand_id INT,
  price DECIMAL(12,2) NOT NULL,
  stock INT NOT NULL DEFAULT 0,
  warranty_months INT DEFAULT 12,
  specs TEXT,
  thumbnail_url VARCHAR(255),
  is_active TINYINT(1) NOT NULL DEFAULT 1,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_products_brand FOREIGN KEY (brand_id) REFERENCES brands(id) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT chk_products_price CHECK (price >= 0),
  CONSTRAINT chk_products_stock CHECK (stock >= 0)
>>>>>>> 2e14c26e8786d752acc647e5941f392c7cc2c4d9
) ENGINE=InnoDB;

CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_brand ON products(brand_id);
CREATE INDEX idx_products_price ON products(price);
CREATE FULLTEXT INDEX ftx_products_name ON products(name);
<<<<<<< HEAD
CREATE FULLTEXT INDEX ftx_products_description ON products(description);

-- Product Images table (keeping original structure)
=======

>>>>>>> 2e14c26e8786d752acc647e5941f392c7cc2c4d9
CREATE TABLE IF NOT EXISTS product_images (
  id INT AUTO_INCREMENT PRIMARY KEY,
  product_id INT NOT NULL,
  url VARCHAR(255) NOT NULL,
  is_primary TINYINT(1) NOT NULL DEFAULT 0,
  CONSTRAINT fk_product_images_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

<<<<<<< HEAD
-- 3) Orders (keeping original structure)
=======
-- 3) Orders
>>>>>>> 2e14c26e8786d752acc647e5941f392c7cc2c4d9
CREATE TABLE IF NOT EXISTS orders (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(20) NOT NULL UNIQUE, -- e.g., PS-2025-000123
  user_id INT NULL,
  full_name VARCHAR(120) NOT NULL,
  phone VARCHAR(20) NOT NULL,
  shipping_address VARCHAR(255) NOT NULL,
  status VARCHAR(30) NOT NULL DEFAULT 'PENDING',       -- PENDING/CONFIRMED/PAID/SHIPPING/COMPLETED/CANCELLED
  payment_method VARCHAR(20) NOT NULL,                  -- COD/ONLINE
  payment_status VARCHAR(20) NOT NULL DEFAULT 'UNPAID', -- UNPAID/PAID/FAILED
  subtotal DECIMAL(12,2) NOT NULL,
  shipping_fee DECIMAL(12,2) NOT NULL DEFAULT 0,
  total DECIMAL(12,2) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_orders_user ON orders(user_id);
CREATE INDEX idx_orders_code ON orders(code);

CREATE TABLE IF NOT EXISTS order_items (
  order_id BIGINT NOT NULL,
  product_id INT NOT NULL,
  price DECIMAL(12,2) NOT NULL,
  quantity INT NOT NULL,
  PRIMARY KEY (order_id, product_id),
  CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT chk_order_items_qty CHECK (quantity > 0),
  CONSTRAINT chk_order_items_price CHECK (price >= 0)
) ENGINE=InnoDB;

-- 3b) Optional: Order status history
CREATE TABLE IF NOT EXISTS order_status_history (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_id BIGINT NOT NULL,
  status VARCHAR(30) NOT NULL,
  note VARCHAR(255),
  changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_osh_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- 3c) Optional: Payments (for later online gateways)
CREATE TABLE IF NOT EXISTS payments (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_id BIGINT NOT NULL,
  provider VARCHAR(40), -- VNPay/MoMo/etc.
  txn_ref VARCHAR(100),
  amount DECIMAL(12,2) NOT NULL,
  status VARCHAR(20) NOT NULL, -- SUCCESS/FAIL/PENDING
  paid_at DATETIME NULL,
  CONSTRAINT fk_payments_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- 4) Seed data
INSERT INTO roles(name) VALUES ('ROLE_ADMIN'), ('ROLE_CUSTOMER')
  ON DUPLICATE KEY UPDATE name = VALUES(name);

<<<<<<< HEAD
-- Updated brand data with descriptions
INSERT INTO brands(name, description) VALUES 
  ('HP', 'Hewlett-Packard - Thương hiệu máy in và máy scan hàng đầu thế giới'),
  ('Canon', 'Canon Inc. - Chuyên về máy in, máy scan và thiết bị văn phòng'),
  ('Epson', 'Seiko Epson Corporation - Nổi tiếng với công nghệ in phun và máy scan')
  ON DUPLICATE KEY UPDATE description = VALUES(description);

-- Updated category data with descriptions
INSERT INTO categories(name, description) VALUES 
  ('Máy in', 'Các loại máy in laser, phun màu, đa chức năng cho văn phòng và gia đình'),
  ('Máy scan', 'Máy quét tài liệu, hình ảnh với độ phân giải cao')
  ON DUPLICATE KEY UPDATE description = VALUES(description);

-- Updated sample products with descriptions and new field names
INSERT INTO products(name, description, price, stock_quantity, image_url, category_id, brand_id)
VALUES
  ('HP LaserJet 1100', 'Máy in laser đen trắng A4, tốc độ in nhanh, tiết kiệm mực', 2900000, 20, NULL, (SELECT id FROM categories WHERE name='Máy in'), (SELECT id FROM brands WHERE name='HP')),
  ('HP Ink Tank 415', 'Máy in phun màu với bình mực lớn, kết nối WiFi, tiết kiệm chi phí', 3500000, 15, NULL, (SELECT id FROM categories WHERE name='Máy in'), (SELECT id FROM brands WHERE name='HP')),
  ('Canon PIXMA G2010', 'Máy in phun màu đa chức năng, bình mực tích hợp, chất lượng cao', 2800000, 25, NULL, (SELECT id FROM categories WHERE name='Máy in'), (SELECT id FROM brands WHERE name='Canon')),
  ('Canon LBP 2900', 'Máy in laser đen trắng nhỏ gọn, phù hợp văn phòng nhỏ', 2100000, 12, NULL, (SELECT id FROM categories WHERE name='Máy in'), (SELECT id FROM brands WHERE name='Canon')),
  ('Epson EcoTank L3110', 'Máy in phun màu với hệ thống bình mực EcoTank, tiết kiệm tối đa', 3300000, 18, NULL, (SELECT id FROM categories WHERE name='Máy in'), (SELECT id FROM brands WHERE name='Epson')),
  ('Epson Perfection V39', 'Máy scan phẳng độ phân giải cao 4800 dpi, phù hợp scan tài liệu', 1900000, 10, NULL, (SELECT id FROM categories WHERE name='Máy scan'), (SELECT id FROM brands WHERE name='Epson')),
  ('Canon CanoScan LiDE 300', 'Máy scan phẳng nhỏ gọn, kết nối USB, dễ sử dụng', 1600000, 14, NULL, (SELECT id FROM categories WHERE name='Máy scan'), (SELECT id FROM brands WHERE name='Canon')),
  ('HP ScanJet 200', 'Máy scan phẳng đa chức năng, độ phân giải 2400 dpi', 1750000, 8, NULL, (SELECT id FROM categories WHERE name='Máy scan'), (SELECT id FROM brands WHERE name='HP')),
  ('HP Laser 107w', 'Máy in laser đen trắng có WiFi, thiết kế nhỏ gọn', 2400000, 16, NULL, (SELECT id FROM categories WHERE name='Máy in'), (SELECT id FROM brands WHERE name='HP')),
  ('Epson DS-1630', 'Máy scan đa chức năng với ADF, phù hợp văn phòng lớn', 5200000, 5, NULL, (SELECT id FROM categories WHERE name='Máy scan'), (SELECT id FROM brands WHERE name='Epson'))
ON DUPLICATE KEY UPDATE 
  description = VALUES(description), 
  price = VALUES(price), 
  stock_quantity = VALUES(stock_quantity);
=======
INSERT INTO brands(name) VALUES ('HP'), ('Canon'), ('Epson')
  ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO categories(name) VALUES ('Máy in'), ('Máy scan')
  ON DUPLICATE KEY UPDATE name = VALUES(name);

-- Sample products (10 items)
INSERT INTO products(sku, name, category_id, brand_id, price, stock, warranty_months, specs, thumbnail_url)
VALUES
  ('HP-LJ-1100',  'HP LaserJet 1100',       (SELECT id FROM categories WHERE name='Máy in'),  (SELECT id FROM brands WHERE name='HP'),    2900000, 20, 12, 'Laser, A4, USB', NULL),
  ('HP-INK-415',  'HP Ink Tank 415',        (SELECT id FROM categories WHERE name='Máy in'),  (SELECT id FROM brands WHERE name='HP'),    3500000, 15, 12, 'Phun màu, WiFi, A4', NULL),
  ('CANON-G2010', 'Canon PIXMA G2010',      (SELECT id FROM categories WHERE name='Máy in'),  (SELECT id FROM brands WHERE name='Canon'), 2800000, 25, 12, 'Phun màu, A4, USB', NULL),
  ('CANON-LBP2900','Canon LBP 2900',        (SELECT id FROM categories WHERE name='Máy in'),  (SELECT id FROM brands WHERE name='Canon'), 2100000, 12, 12, 'Laser, A4, USB', NULL),
  ('EPSON-L3110','Epson EcoTank L3110',     (SELECT id FROM categories WHERE name='Máy in'),  (SELECT id FROM brands WHERE name='Epson'), 3300000, 18, 12, 'Phun màu, A4', NULL),
  ('EPSON-V39',   'Epson Perfection V39',   (SELECT id FROM categories WHERE name='Máy scan'),(SELECT id FROM brands WHERE name='Epson'), 1900000, 10, 12, 'Flatbed, 4800 dpi', NULL),
  ('CANON-LiDE300','Canon CanoScan LiDE 300',(SELECT id FROM categories WHERE name='Máy scan'),(SELECT id FROM brands WHERE name='Canon'), 1600000, 14, 12, 'Flatbed, 2400 dpi', NULL),
  ('HP-SCAN-200', 'HP ScanJet 200',         (SELECT id FROM categories WHERE name='Máy scan'),(SELECT id FROM brands WHERE name='HP'),    1750000, 8,  12, 'Flatbed, 2400 dpi', NULL),
  ('HP-LJ-107w',  'HP Laser 107w',          (SELECT id FROM categories WHERE name='Máy in'),  (SELECT id FROM brands WHERE name='HP'),    2400000, 16, 12, 'Laser, WiFi, A4', NULL),
  ('EPSON-DS-1630','Epson DS-1630',         (SELECT id FROM categories WHERE name='Máy scan'),(SELECT id FROM brands WHERE name='Epson'), 5200000, 5,  12, 'Flatbed + ADF', NULL)
ON DUPLICATE KEY UPDATE price = VALUES(price), stock = VALUES(stock);
>>>>>>> 2e14c26e8786d752acc647e5941f392c7cc2c4d9

-- Helpful views (optional)
-- CREATE VIEW v_order_summary AS
--   SELECT o.id, o.code, o.user_id, o.status, o.payment_status, o.total, o.created_at
--   FROM orders o;

-- Notes:
-- - Seed user/admin should be created via application (register + role assign) to ensure password hashing (BCrypt).
-- - Consider adding triggers for stock deduction upon status change if you handle stock at DB-level (else, do it in service layer).
<<<<<<< HEAD
-- - Updated schema to match current Entity classes with description fields and renamed columns.
=======
>>>>>>> 2e14c26e8786d752acc647e5941f392c7cc2c4d9
