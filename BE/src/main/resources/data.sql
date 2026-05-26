-- Seed data for local testing (PostgreSQL)
-- Notes:
-- - Entity mappings use BOOLEAN for status columns, so use true/false (not 0/1)
-- - mstuser.username is VARCHAR(8) in current entity mapping



-- mstuser
INSERT INTO mstuser (psn_cd, user_id, username, password, role, status, createtime, create_psn_cd, updatetime, update_psn_cd)
VALUES
(1, 'admin01', 'admin01', '$2a$10$hashedpassword1', 1, false, NOW(), 1, NOW(), 1),
(2, 'nv00001', 'nguyenva', '$2a$10$hashedpassword2', 2, false, NOW(), 1, NOW(), 1),
(3, 'nv00002', 'tranthi', '$2a$10$hashedpassword3', 2, false, NOW(), 1, NOW(), 1)
ON CONFLICT (psn_cd) DO NOTHING;

-- mstproducttype
INSERT INTO mstproducttype (producttype_id, name, status, createtime, create_user, updatetime, update_user)
VALUES
(1, 'Tivi', false, NOW(), 1, NOW(), 1),
(2, 'Tủ lạnh', false, NOW(), 1, NOW(), 1),
(3, 'Máy giặt', false, NOW(), 1, NOW(), 1),
(4, 'Điều hòa', false, NOW(), 1, NOW(), 1),
(5, 'Điện thoại', false, NOW(), 1, NOW(), 1)
ON CONFLICT (producttype_id) DO NOTHING;

-- mstproduct (20 rows)
INSERT INTO mstproduct (product_id, product_name, status, description, product_img, product_amount, price, producttype_id, createtime, create_user, updatetime, update_user)
VALUES
(1,  'Samsung Smart TV 55"',           false, 'Tivi Samsung 4K 55 inch',            '/img/product/tv_samsung_55.jpg',      15, 15990000, 1, NOW(), 1, NOW(), 1),
(2,  'LG OLED TV 65"',                 false, 'Tivi LG OLED 4K 65 inch',            '/img/product/tv_lg_65.jpg',           10, 32990000, 1, NOW(), 1, NOW(), 1),
(3,  'Sony Bravia 50"',                false, 'Tivi Sony 4K 50 inch',               '/img/product/tv_sony_50.jpg',           8, 18990000, 1, NOW(), 1, NOW(), 1),
(4,  'Panasonic TV 43"',               false, 'Tivi Panasonic Full HD 43 inch',     '/img/product/tv_panasonic_43.jpg',     20,  8990000, 1, NOW(), 1, NOW(), 1),
(5,  'Samsung Tủ lạnh Side By Side',   false, 'Tủ lạnh Samsung 617L',               '/img/product/tl_samsung_617.jpg',       7, 25990000, 2, NOW(), 1, NOW(), 1),
(6,  'LG Tủ lạnh French Door',         false, 'Tủ lạnh LG 635L inverter',           '/img/product/tl_lg_635.jpg',            5, 38990000, 2, NOW(), 1, NOW(), 1),
(7,  'Panasonic Tủ lạnh 380L',         false, 'Tủ lạnh Panasonic inverter 380L',    '/img/product/tl_panasonic_380.jpg',     12, 12990000, 2, NOW(), 1, NOW(), 1),
(8,  'Aqua Tủ lạnh 320L',              false, 'Tủ lạnh Aqua 2 cánh 320L',           '/img/product/tl_aqua_320.jpg',          18,  9490000, 2, NOW(), 1, NOW(), 1),
(9,  'Samsung Máy giặt 10kg',          false, 'Máy giặt Samsung cửa trước 10kg',    '/img/product/mg_samsung_10.jpg',        10, 11990000, 3, NOW(), 1, NOW(), 1),
(10, 'LG Máy giặt 9kg',                false, 'Máy giặt LG inverter 9kg',           '/img/product/mg_lg_9.jpg',              14,  9990000, 3, NOW(), 1, NOW(), 1),
(11, 'Electrolux Máy giặt 8kg',        false, 'Máy giặt Electrolux cửa ngang 8kg',  '/img/product/mg_electrolux_8.jpg',      20,  7490000, 3, NOW(), 1, NOW(), 1),
(12, 'Toshiba Máy giặt 7kg',           false, 'Máy giặt Toshiba cửa trên 7kg',      '/img/product/mg_toshiba_7.jpg',          9,  5990000, 3, NOW(), 1, NOW(), 1),
(13, 'Daikin Điều hòa 1.5HP',          false, 'Điều hòa Daikin inverter 1.5HP',     '/img/product/dh_daikin_15.jpg',         25, 13990000, 4, NOW(), 1, NOW(), 1),
(14, 'Mitsubishi Điều hòa 2HP',        false, 'Điều hòa Mitsubishi inverter 2HP',   '/img/product/dh_mitsu_2.jpg',           11, 18990000, 4, NOW(), 1, NOW(), 1),
(15, 'Panasonic Điều hòa 1HP',         false, 'Điều hòa Panasonic inverter 1HP',    '/img/product/dh_panasonic_1.jpg',       30,  9990000, 4, NOW(), 1, NOW(), 1),
(16, 'Samsung Điều hòa 1.5HP',         false, 'Điều hòa Samsung Wind-Free 1.5HP',   '/img/product/dh_samsung_15.jpg',        16, 15490000, 4, NOW(), 1, NOW(), 1),
(17, 'iPhone 15 Pro Max 256GB',        false, 'Apple iPhone 15 Pro Max 256GB',      '/img/product/dt_iphone15pm.jpg',         8, 34990000, 5, NOW(), 1, NOW(), 1),
(18, 'Samsung Galaxy S24 Ultra',       false, 'Samsung Galaxy S24 Ultra 256GB',     '/img/product/dt_s24ultra.jpg',          10, 31990000, 5, NOW(), 1, NOW(), 1),
(19, 'Xiaomi 14 Pro',                  false, 'Xiaomi 14 Pro 512GB',                '/img/product/dt_xiaomi14pro.jpg',       13, 19990000, 5, NOW(), 1, NOW(), 1),
(20, 'OPPO Find X7',                   false, 'OPPO Find X7 256GB',                 '/img/product/dt_oppofindx7.jpg',        17, 17990000, 5, NOW(), 1, NOW(), 1)
ON CONFLICT (product_id) DO NOTHING;

-- trproductorder (Sample Data)
INSERT INTO trproductorder (id, custom_name, order_product_id, order_product_amount, unit_price, total_price, order_status, order_delivery_address, order_delivery_date, createtime, create_user, updatetime, update_user)
VALUES
(1, 'Khách hàng 1', 1, 2,  15990000, 31980000, 'DELIVERED', 'Hà Nội', NOW(), NOW(), 1, NOW(), 1),
(2, 'Khách hàng 2', 5, 1,  25990000, 25990000, 'DELIVERED', 'Hà Nội', NOW(), NOW(), 1, NOW(), 1),
(3, 'Khách hàng 3', 17, 5, 34990000, 174950000, 'DELIVERED', 'Hồ Chí Minh', NOW(), NOW(), 1, NOW(), 1),
(4, 'Khách hàng 4', 1, 3,  15990000, 47970000, 'DELIVERED', 'Hà Nội', NOW(), NOW(), 1, NOW(), 1),
(5, 'Khách hàng 5', 2, 4,  32990000, 131960000, 'DELIVERED', 'Hà Nội', NOW(), NOW(), 1, NOW(), 1)
ON CONFLICT (id) DO NOTHING;

-- advance sequences to avoid duplicate key when inserting new rows later
SELECT setval('mstuser_psn_cd_seq', (SELECT MAX(psn_cd) FROM mstuser));
SELECT setval('mstproducttype_producttype_id_seq', (SELECT MAX(producttype_id) FROM mstproducttype));
SELECT setval('mstproduct_product_id_seq', (SELECT MAX(product_id) FROM mstproduct));
