insert into productcategory (id, name, image_filename, root_id, parent_id, lft, rgt) values (1, 'Root Category', 'doge01.jpg', null, null, 1, 14);
insert into productcategory (id, name, image_filename, root_id, parent_id, lft, rgt) values (2, 'Sub Category 1', 'doge02.jpg', 1, 1, 2, 3);
insert into productcategory (id, name, image_filename, root_id, parent_id, lft, rgt) values (3, 'Sub Category 2', 'doge03.jpg', 1, 1, 4, 11);
insert into productcategory (id, name, image_filename, root_id, parent_id, lft, rgt) values (4, 'Sub Sub Category 1', 'doge04.jpg', 1, 3, 5, 6);
insert into productcategory (id, name, image_filename, root_id, parent_id, lft, rgt) values (5, 'Sub Sub Category 2', 'doge05.jpg', 1, 3, 7, 8);
insert into productcategory (id, name, image_filename, root_id, parent_id, lft, rgt) values (6, 'Sub Sub Category 2', 'doge06.jpg', 1, 3, 9, 10);
insert into productcategory (id, name, image_filename, root_id, parent_id, lft, rgt) values (7, 'Sub Category 3', 'doge07.jpg', 1, 1, 12, 13);

insert into product (id, name, digital, published, default_price) values (1, 'Test Product 1', true, false, 199);
insert into product (id, name, digital, published, default_price) values (2, 'Test Product 2', false, true, 299);
insert into product (id, name, digital, published, default_price) values (3, 'Test Product 3', true, false, 399);
insert into product (id, name, digital, published, default_price) values (4, 'Test Product 4', false, true, 499);
insert into product (id, name, digital, published, default_price) values (5, 'Test Product 5', true, false, 599);
insert into product (id, name, digital, published, default_price) values (6, 'Test Product 6', false, true, 699);
insert into product (id, name, digital, published, default_price) values (7, 'Test Product 7', true, false, 799);
insert into product (id, name, digital, published, default_price) values (8, 'Test Product 8', false, true, 899);
insert into product (id, name, digital, published, default_price) values (9, 'Test Product 9', true, false, 999);
insert into product (id, name, digital, published, default_price) values (10, 'Test Product 10', false, true, 1099);
insert into product (id, name, digital, published, default_price) values (11, 'Test Product 11', false, true, 1199);
insert into product (id, name, digital, published, default_price) values (12, 'Test Product 12', false, true, 1299);
insert into product (id, name, digital, published, default_price) values (13, 'Test Product 13', false, true, 1399);
insert into product (id, name, digital, published, default_price) values (14, 'Test Product 14', false, true, 1499);
insert into product (id, name, digital, published, default_price) values (15, 'Test Product 15', false, true, 1599);

insert into shippingzone (id, name) values (1, 'Some Shipping Zone 1');
insert into shippingzone (id, name) values (2, 'Some Shipping Zone 2');

insert into shippingcountry (id, code) values (1, 'UM');
insert into shippingcountry (id, code, zone_id) values (2, 'US', 1);
insert into shippingcountry (id, code, zone_id) values (3, 'UY', 2);

insert into shippingmethod (id, name, min_value, max_value, rate, type, zone_id) values (1, 'Some Shipping Method 1', 350, 850, 0, 'Price', 1);
insert into shippingmethod (id, name, min_value, max_value, rate, type, zone_id) values (2, 'Some Shipping Method 2', 500, 1000, 0, 'Price', 1);
insert into shippingmethod (id, name, min_value, max_value, rate, type, zone_id) values (3, 'Some Shipping Method 3', 5000, 10000, 0, 'Price', 1);
insert into shippingmethod (id, name, min_value, max_value, rate, type, zone_id) values (4, 'Some Shipping Method 4', 0, 6000, 0, 'Weight', 1);
insert into shippingmethod (id, name, min_value, max_value, rate, type, zone_id) values (5, 'Some Shipping Method 5', 6000, 15000, 0, 'Weight', 1);
