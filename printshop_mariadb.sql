-- =========================
-- PrintShop - Minimal Schema
-- MariaDB / InnoDB / utf8mb4
-- =========================
CREATE DATABASE IF NOT EXISTS printshop
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE printshop;

-- 1) Auth
CREATE TABLE roles (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(30) NOT NULL UNIQUE          -- ROLE_ADMIN / ROLE_USER
) ENGINE=InnoDB;

CREATE TABLE users (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       email VARCHAR(120) NOT NULL UNIQUE,
                       password_hash VARCHAR(200) NOT NULL,
                       full_name VARCHAR(120) NOT NULL,
                       phone VARCHAR(20),
                       default_address VARCHAR(255),
                       is_active TINYINT(1) NOT NULL DEFAULT 1,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE user_roles (
                            user_id INT NOT NULL,
                            role_id INT NOT NULL,
                            PRIMARY KEY (user_id, role_id),
                            CONSTRAINT fk_ur_u FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                            CONSTRAINT fk_ur_r FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 2) Catalog (Product / Category / Brand)
CREATE TABLE categories (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(80) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE brands (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(80) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE products (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          sku VARCHAR(50) UNIQUE,
                          name VARCHAR(200) NOT NULL,
                          category_id INT NOT NULL,
                          brand_id INT,
                          price DECIMAL(12,2) NOT NULL CHECK (price >= 0),
                          stock INT NOT NULL DEFAULT 0 CHECK (stock >= 0),
                          warranty_months INT DEFAULT 12,
                          is_active TINYINT(1) NOT NULL DEFAULT 1,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          CONSTRAINT fk_p_c FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT,
                          CONSTRAINT fk_p_b FOREIGN KEY (brand_id) REFERENCES brands(id) ON DELETE SET NULL
) ENGINE=InnoDB;
CREATE INDEX idx_p_cat ON products(category_id);
CREATE INDEX idx_p_price ON products(price);

-- 3) Cart
CREATE TABLE carts (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       user_id INT NULL,                  -- null = guest
                       cart_key CHAR(36) NULL,            -- UUID guest (cookie/localStorage)
                       status ENUM('ACTIVE','ORDERED','ABANDONED') NOT NULL DEFAULT 'ACTIVE',
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
                       CONSTRAINT uq_cart_key UNIQUE (cart_key)
) ENGINE=InnoDB;

CREATE TABLE cart_items (
                            cart_id BIGINT NOT NULL,
                            product_id INT NOT NULL,
                            quantity INT NOT NULL CHECK (quantity > 0),
                            PRIMARY KEY (cart_id, product_id),
                            CONSTRAINT fk_ci_cart FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
                            CONSTRAINT fk_ci_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 4) Orders
CREATE TABLE orders (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        code VARCHAR(20) NOT NULL UNIQUE,          -- ví dụ: PS-2025-000001
                        user_id INT NULL,                          -- có thể null nếu guest
                        full_name VARCHAR(120) NOT NULL,
                        phone VARCHAR(20) NOT NULL,
                        shipping_address VARCHAR(255) NOT NULL,
                        status VARCHAR(30) NOT NULL DEFAULT 'PENDING',         -- PENDING/CONFIRMED/PAID/SHIPPING/COMPLETED/CANCELLED
                        payment_method VARCHAR(20) NOT NULL,                   -- COD/ONLINE (chưa cần bảng payment)
                        payment_status VARCHAR(20) NOT NULL DEFAULT 'UNPAID',  -- UNPAID/PAID/FAILED
                        subtotal DECIMAL(12,2) NOT NULL,
                        shipping_fee DECIMAL(12,2) NOT NULL DEFAULT 0,
                        total DECIMAL(12,2) NOT NULL,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        CONSTRAINT fk_o_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;
CREATE INDEX idx_o_user ON orders(user_id);

CREATE TABLE order_details (
                               order_id BIGINT NOT NULL,
                               product_id INT NOT NULL,
                               price DECIMAL(12,2) NOT NULL CHECK (price >= 0),  -- snapshot giá lúc mua
                               quantity INT NOT NULL CHECK (quantity > 0),
                               PRIMARY KEY (order_id, product_id),
                               CONSTRAINT fk_od_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
                               CONSTRAINT fk_od_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 5) Reviews
CREATE TABLE reviews (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         product_id INT NOT NULL,
                         user_id INT NOT NULL,
                         rating TINYINT NOT NULL CHECK (rating BETWEEN 1 AND 5),
                         title VARCHAR(120),
                         content TEXT,
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         CONSTRAINT fk_r_p FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
                         CONSTRAINT fk_r_u FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                         CONSTRAINT uq_review_once UNIQUE (product_id, user_id)  -- 1 user/1 sp: 1 review
) ENGINE=InnoDB;

-- Roles & Users
INSERT INTO roles(name) VALUES ('ROLE_ADMIN'), ('ROLE_USER')
    ON DUPLICATE KEY UPDATE name=VALUES(name);

INSERT INTO users(email,password_hash,full_name,phone,default_address,is_active) VALUES
                                                                                     ('admin@shop.local','Hoilamchi3@','Quản trị viên','0900000001','123 Q1, HCM',1),
                                                                                     ('user@shop.local',  'Hoilamchi2!','Nguyễn Văn A','0900000002','45 Q10, HCM',1)
    ON DUPLICATE KEY UPDATE full_name=VALUES(full_name);

INSERT IGNORE INTO user_roles(user_id,role_id)
SELECT u.id,r.id FROM users u JOIN roles r
WHERE (u.email='admin@shop.local' AND r.name='ROLE_ADMIN')
   OR (u.email='user@shop.local' AND r.name='ROLE_USER');

-- Catalog
INSERT INTO categories(name) VALUES ('Máy in'),('Máy scan')
    ON DUPLICATE KEY UPDATE name=VALUES(name);
INSERT INTO brands(name) VALUES ('HP'),('Canon'),('Epson')
    ON DUPLICATE KEY UPDATE name=VALUES(name);

INSERT INTO products(sku,name,category_id,brand_id,price,stock,warranty_months,is_active) VALUES
                                                                                              ('HP-LJ-1100','HP LaserJet 1100',(SELECT id FROM categories WHERE name='Máy in'),(SELECT id FROM brands WHERE name='HP'),2900000,20,12,1),
                                                                                              ('CANON-LBP2900','Canon LBP 2900',(SELECT id FROM categories WHERE name='Máy in'),(SELECT id FROM brands WHERE name='Canon'),2100000,12,12,1),
                                                                                              ('EPSON-V39','Epson Perfection V39',(SELECT id FROM categories WHERE name='Máy scan'),(SELECT id FROM brands WHERE name='Epson'),1900000,10,12,1)
    ON DUPLICATE KEY UPDATE price=VALUES(price), stock=VALUES(stock);

-- Cart demo (user@shop.local có 1 giỏ 2 sp)
SET @uid := (SELECT id FROM users WHERE email='user@shop.local');
INSERT INTO carts(user_id,status) SELECT @uid,'ACTIVE'
    WHERE NOT EXISTS(SELECT 1 FROM carts WHERE user_id=@uid AND status='ACTIVE');
SET @cid := (SELECT id FROM carts WHERE user_id=@uid AND status='ACTIVE' LIMIT 1);
SET @p1 := (SELECT id FROM products WHERE sku='HP-LJ-1100');
SET @p2 := (SELECT id FROM products WHERE sku='EPSON-V39');

INSERT INTO cart_items(cart_id,product_id,quantity) VALUES
    (@cid,@p1,1) ON DUPLICATE KEY UPDATE quantity=VALUES(quantity);
INSERT INTO cart_items(cart_id,product_id,quantity) VALUES
    (@cid,@p2,2) ON DUPLICATE KEY UPDATE quantity=VALUES(quantity);

-- Tạo 1 order từ giỏ (mẫu)
SET @sub := (SELECT SUM(ci.quantity * p.price) FROM cart_items ci JOIN products p ON p.id=ci.product_id WHERE ci.cart_id=@cid);
SET @ship := 30000;
DELETE FROM order_details WHERE order_id IN (SELECT id FROM orders WHERE code='PS-000001');
DELETE FROM orders WHERE code='PS-000001';
INSERT INTO orders(code,user_id,full_name,phone,shipping_address,status,payment_method,payment_status,subtotal,shipping_fee,total)
VALUES ('PS-000001',@uid,'Nguyễn Văn A','0900000002','45 Q10, HCM','CONFIRMED','COD','UNPAID',IFNULL(@sub,0),@ship,IFNULL(@sub,0)+@ship);
SET @oid := LAST_INSERT_ID();

INSERT INTO order_details(order_id,product_id,price,quantity)
SELECT @oid, ci.product_id, p.price, ci.quantity
FROM cart_items ci JOIN products p ON p.id=ci.product_id
WHERE ci.cart_id=@cid;

-- Reviews mẫu
INSERT INTO reviews(product_id,user_id,rating,title,content) VALUES
                                                                 ((SELECT id FROM products WHERE sku='HP-LJ-1100'), @uid, 5,'Tốt','In nhanh, rõ.'),
                                                                 ((SELECT id FROM products WHERE sku='EPSON-V39'),  @uid, 4,'Ổn','Scan đủ dùng.')
    ON DUPLICATE KEY UPDATE rating=VALUES(rating), title=VALUES(title), content=VALUES(content);
