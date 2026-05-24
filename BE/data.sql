-- mstuser
INSERT INTO mstuser (psn_cd, user_id, username, password, role, status, createtime, create_psn_cd, updatetime, update_psn_cd)
VALUES
(1, 'admin001', 'admin001', '$2a$10$hashedpassword1', 1, 0, NOW(), 1, NOW(), 1),
(2, 'nv000001', 'nguyenvana', '$2a$10$hashedpassword2', 2, 0, NOW(), 1, NOW(), 1),
(3, 'nv000002', 'tranthib', '$2a$10$hashedpassword3', 2, 0, NOW(), 1, NOW(), 1);

-- mstproducttype
INSERT INTO mstproducttype (producttype_id, name, status, createtime, create_user, updatetime, update_user)
VALUES
(1, 'Tivi', 0, NOW(), 1, NOW(), 1),
(2, 'Tủ lạnh', 0, NOW(), 1, NOW(), 1),
(3, 'Máy giặt', 0, NOW(), 1, NOW(), 1),
(4, 'Điều hòa', 0, NOW(), 1, NOW(), 1),
(5, 'Điện thoại', 0, NOW(), 1, NOW(), 1);

-- mstproduct (20 dòng)
INSERT INTO mstproduct (product_id, product_name, status, description, product_img, product_amount, price, producttype_id, createtime, create_user, updatetime, update_user)
VALUES
(1,  'Samsung Smart TV 55"',      0, 'Tivi Samsung 4K 55 inch',         '/img/product/tv_samsung_55.jpg',      15, 15990000, 1, NOW(), 1, NOW(), 1),
(2,  'LG OLED TV 65"',            0, 'Tivi LG OLED 4K 65 inch',         '/img/product/tv_lg_65.jpg',           10, 32990000, 1, NOW(), 1, NOW(), 1),
(3,  'Sony Bravia 50"',           0, 'Tivi Sony 4K 50 inch',            '/img/product/tv_sony_50.jpg',          8, 18990000, 1, NOW(), 1, NOW(), 1),
(4,  'Panasonic TV 43"',          0, 'Tivi Panasonic Full HD 43 inch',  '/img/product/tv_panasonic_43.jpg',    20, 8990000,  1, NOW(), 1, NOW(), 1),
(5,  'Samsung Tủ lạnh Side By Side', 0, 'Tủ lạnh Samsung 617L',        '/img/product/tl_samsung_617.jpg',      7, 25990000, 2, NOW(), 1, NOW(), 1),
(6,  'LG Tủ lạnh French Door',    0, 'Tủ lạnh LG 635L inverter',        '/img/product/tl_lg_635.jpg',           5, 38990000, 2, NOW(), 1, NOW(), 1),
(7,  'Panasonic Tủ lạnh 380L',    0, 'Tủ lạnh Panasonic inverter 380L', '/img/product/tl_panasonic_380.jpg',  12, 12990000, 2, NOW(), 1, NOW(), 1),
(8,  'Aqua Tủ lạnh 320L',         0, 'Tủ lạnh Aqua 2 cánh 320L',       '/img/product/tl_aqua_320.jpg',        18, 9490000,  2, NOW(), 1, NOW(), 1),
(9,  'Samsung Máy giặt 10kg',     0, 'Máy giặt Samsung cửa trước 10kg', '/img/product/mg_samsung_10.jpg',     10, 11990000, 3, NOW(), 1, NOW(), 1),
(10, 'LG Máy giặt 9kg',           0, 'Máy giặt LG inverter 9kg',        '/img/product/mg_lg_9.jpg',           14, 9990000,  3, NOW(), 1, NOW(), 1),
(11, 'Electrolux Máy giặt 8kg',   0, 'Máy giặt Electrolux cửa ngang 8kg','/img/product/mg_electrolux_8.jpg', 20, 7490000,  3, NOW(), 1, NOW(), 1),
(12, 'Toshiba Máy giặt 7kg',      0, 'Máy giặt Toshiba cửa trên 7kg',  '/img/product/mg_toshiba_7.jpg',       9, 5990000,  3, NOW(), 1, NOW(), 1),
(13, 'Daikin Điều hòa 1.5HP',     0, 'Điều hòa Daikin inverter 1.5HP', '/img/product/dh_daikin_15.jpg',      25, 13990000, 4, NOW(), 1, NOW(), 1),
(14, 'Mitsubishi Điều hòa 2HP',   0, 'Điều hòa Mitsubishi inverter 2HP','/img/product/dh_mitsu_2.jpg',       11, 18990000, 4, NOW(), 1, NOW(), 1),
(15, 'Panasonic Điều hòa 1HP',    0, 'Điều hòa Panasonic inverter 1HP', '/img/product/dh_panasonic_1.jpg',   30, 9990000,  4, NOW(), 1, NOW(), 1),
(16, 'Samsung Điều hòa 1.5HP',    0, 'Điều hòa Samsung Wind-Free 1.5HP','/img/product/dh_samsung_15.jpg',    16, 15490000, 4, NOW(), 1, NOW(), 1),
(17, 'iPhone 15 Pro Max 256GB',   0, 'Apple iPhone 15 Pro Max 256GB',   '/img/product/dt_iphone15pm.jpg',      8, 34990000, 5, NOW(), 1, NOW(), 1),
(18, 'Samsung Galaxy S24 Ultra',  0, 'Samsung Galaxy S24 Ultra 256GB',  '/img/product/dt_s24ultra.jpg',       10, 31990000, 5, NOW(), 1, NOW(), 1),
(19, 'Xiaomi 14 Pro',             0, 'Xiaomi 14 Pro 512GB',             '/img/product/dt_xiaomi14pro.jpg',    13, 19990000, 5, NOW(), 1, NOW(), 1),
(20, 'OPPO Find X7',              0, 'OPPO Find X7 256GB',              '/img/product/dt_oppofindx7.jpg',     17, 17990000, 5, NOW(), 1, NOW(), 1);