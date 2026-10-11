-- =====================================================================
--  PrintXpress - sample data (SQLite)
--  Runs once, right after schema.sql. The demo customer (user_id 1:
--  demo@printxpress.lk / Demo@123) is inserted by AppDatabase.java first,
--  because its password hash is calculated in Java.
-- =====================================================================

INSERT INTO addresses (user_id, label, address_line, city, postal_code, is_default) VALUES
(1, 'Home',   'No. 25, Temple Road, Nugegoda', 'Colombo', '10250', 1),
(1, 'Office', '4th Floor, 120 Galle Road',     'Colombo', '00300', 0);

INSERT INTO categories (name, description, icon_name) VALUES
('Business Cards', 'Premium cards that make a great first impression', 'ic_cat_cards'),
('Flyers',         'Eye-catching flyers and leaflets for promotions',  'ic_cat_flyers'),
('Posters',        'High quality posters in many sizes',               'ic_cat_posters'),
('Banners',        'Indoor and outdoor banners for events and shops',  'ic_cat_banners'),
('Stickers',       'Custom stickers and labels in any shape',          'ic_cat_stickers'),
('Mugs',           'Personalised ceramic mugs for gifts and branding',  'ic_cat_mugs'),
('T-Shirts',       'Custom printed t-shirts for teams and events',     'ic_cat_tshirts');

INSERT INTO products (category_id, name, description, base_price, min_quantity, max_quantity, turnaround_days) VALUES
(1, 'Standard Business Cards', 'Classic 300gsm cards with full colour print on both sides.',          12.00, 50,  5000, 2),
(1, 'Premium Matte Cards',     'Thick 400gsm matte laminated cards with a soft-touch finish.',        25.00, 50,  5000, 3),
(2, 'A5 Flyers',               'Full colour A5 flyers, ideal for handouts and events.',               18.00, 25, 10000, 2),
(2, 'Folded Brochures',        'Tri-fold A4 brochures to tell your full story.',                      55.00, 25,  5000, 3),
(3, 'Photo Posters',           'Vivid posters printed on photo-quality paper.',                      450.00,  1,   500, 1),
(3, 'Event Posters',           'Large posters for concerts, sales and announcements.',               650.00,  1,   500, 2),
(4, 'Vinyl Banner',            'Weather-proof outdoor vinyl banner with eyelets.',                  2500.00,  1,    50, 3),
(4, 'Roll-up Banner',          'Portable roll-up stand banner for exhibitions.',                    8500.00,  1,    20, 4),
(5, 'Die-cut Stickers',        'Stickers cut to the shape of your design.',                           20.00, 50, 10000, 2),
(5, 'Product Labels',          'Rectangular labels for bottles, jars and boxes.',                      8.00, 100, 20000, 2),
(6, 'Classic White Mug',       '11oz ceramic mug with a wrap-around print.',                         950.00,  1,   500, 3),
(6, 'Magic Colour Mug',        'Black mug that reveals your design when filled with hot water.',    1650.00,  1,   200, 4),
(7, 'Cotton T-Shirt',          '100% cotton round-neck t-shirt with front print.',                  1450.00,  1,   500, 4),
(7, 'Polo T-Shirt',            'Smart polo shirt with embroidered-look print.',                     2250.00,  1,   300, 5);

INSERT INTO product_options (product_id, option_type, option_name, extra_price) VALUES
-- 1 Standard Business Cards
(1,'SIZE','Standard (89 x 51 mm)',0),(1,'SIZE','Square (65 x 65 mm)',3),
(1,'MATERIAL','300gsm Art Card',0),(1,'MATERIAL','300gsm Gloss Laminated',4),
-- 2 Premium Matte Cards
(2,'SIZE','Standard (89 x 51 mm)',0),(2,'SIZE','Square (65 x 65 mm)',4),
(2,'MATERIAL','400gsm Soft-touch Matte',0),(2,'MATERIAL','400gsm Spot UV',10),
-- 3 A5 Flyers
(3,'SIZE','A5 (148 x 210 mm)',0),(3,'SIZE','A4 (210 x 297 mm)',12),
(3,'MATERIAL','130gsm Gloss',0),(3,'MATERIAL','170gsm Matte',5),
-- 4 Folded Brochures
(4,'SIZE','A4 Tri-fold',0),(4,'SIZE','A3 Bi-fold',35),
(4,'MATERIAL','150gsm Gloss',0),(4,'MATERIAL','200gsm Matte',15),
-- 5 Photo Posters
(5,'SIZE','A3 (297 x 420 mm)',0),(5,'SIZE','A2 (420 x 594 mm)',350),(5,'SIZE','A1 (594 x 841 mm)',900),
(5,'MATERIAL','Photo Gloss',0),(5,'MATERIAL','Photo Satin',100),
-- 6 Event Posters
(6,'SIZE','A2 (420 x 594 mm)',0),(6,'SIZE','A1 (594 x 841 mm)',550),
(6,'MATERIAL','150gsm Poster Paper',0),(6,'MATERIAL','Synthetic Waterproof',300),
-- 7 Vinyl Banner
(7,'SIZE','3 x 2 ft',0),(7,'SIZE','6 x 3 ft',2000),(7,'SIZE','10 x 4 ft',5500),
(7,'MATERIAL','Standard Flex',0),(7,'MATERIAL','Heavy Duty Flex',800),
-- 8 Roll-up Banner
(8,'SIZE','2.5 x 6.5 ft',0),(8,'SIZE','3 x 7 ft',1500),
(8,'MATERIAL','Standard Stand',0),(8,'MATERIAL','Premium Aluminium Stand',2500),
-- 9 Die-cut Stickers
(9,'SIZE','5 x 5 cm',0),(9,'SIZE','7.5 x 7.5 cm',8),(9,'SIZE','10 x 10 cm',15),
(9,'MATERIAL','Gloss Vinyl',0),(9,'MATERIAL','Waterproof Matte Vinyl',6),
-- 10 Product Labels
(10,'SIZE','5 x 3 cm',0),(10,'SIZE','8 x 5 cm',4),
(10,'MATERIAL','Paper Label',0),(10,'MATERIAL','Transparent Vinyl',5),
-- 11 Classic White Mug
(11,'SIZE','11 oz',0),(11,'SIZE','15 oz',250),
(11,'MATERIAL','Gloss Ceramic',0),
-- 12 Magic Colour Mug
(12,'SIZE','11 oz',0),
(12,'MATERIAL','Black Magic Coating',0),(12,'MATERIAL','Red Magic Coating',150),
-- 13 Cotton T-Shirt
(13,'SIZE','S',0),(13,'SIZE','M',0),(13,'SIZE','L',0),(13,'SIZE','XL',100),(13,'SIZE','XXL',200),
(13,'MATERIAL','White Cotton',0),(13,'MATERIAL','Black Cotton',150),
-- 14 Polo T-Shirt
(14,'SIZE','S',0),(14,'SIZE','M',0),(14,'SIZE','L',0),(14,'SIZE','XL',150),
(14,'MATERIAL','Pique Cotton',0),(14,'MATERIAL','Dri-fit Polyester',300);

INSERT INTO promotions (title, description, promo_code, discount_percent, min_quantity, start_date, end_date) VALUES
('Bulk Order Saver',   'Get 15% off when you order 500 or more items.',               'BULK15',   15, 500, '2026-01-01', '2027-12-31'),
('Avurudu Festive Deal','20% off festive designs for the Sinhala & Tamil New Year.',   'AVURUDU20', 20, 1,  '2026-03-15', '2027-04-30'),
('Christmas Special',  '10% off on mugs, t-shirts and posters this festive season.',  'XMAS10',   10, 1,  '2026-11-01', '2026-12-31'),
('Welcome Offer',      'New customers get 5% off their first order.',                 'WELCOME5',  5, 1,  '2026-01-01', '2027-12-31');

INSERT INTO notifications (user_id, title, message, type) VALUES
(1, 'Welcome to PrintXpress!', 'Use code WELCOME5 to get 5% off your first order.', 'PROMOTION'),
(1, 'Bulk Order Saver',        'Ordering 500+ items? Use code BULK15 for 15% off.', 'PROMOTION');

INSERT INTO sample_designs (category_id, title, description, style_color) VALUES
(1, 'Minimal Corporate',  'Clean white card with a bold orange accent line.',         '#FF7A00'),
(1, 'Dark Executive',     'Elegant black card with gold foil style text.',            '#3A3A3A'),
(2, 'Grand Opening Sale', 'Bright flyer layout with a big discount badge.',           '#E53935'),
(2, 'Restaurant Menu',    'Food flyer with photo grid and price list.',               '#8D6E63'),
(3, 'Music Night',        'Concert poster with neon gradient background.',            '#7B1FA2'),
(3, 'Motivational Quote', 'Typography poster for offices and classrooms.',            '#00897B'),
(4, 'Shop Front Banner',  'Large banner with logo, contact number and services.',     '#1E88E5'),
(5, 'Round Logo Sticker', 'Circular die-cut sticker for brand packaging.',            '#43A047'),
(6, 'Birthday Mug',       'Colourful birthday wishes with a photo frame.',            '#F4511E'),
(7, 'Team Jersey',        'Sports team t-shirt with name and number on the back.',    '#FB8C00');

INSERT INTO guidelines (title, content, sort_order) VALUES
('Accepted File Types',
 'Upload your artwork as PDF, PNG or JPG. PDF is recommended because it keeps fonts and vector graphics sharp. Maximum file size is 10 MB.', 1),
('Resolution',
 'Images should be at least 300 DPI at the final print size. Images taken from websites (72 DPI) will look blurry when printed.', 2),
('Colour Format (CMYK vs RGB)',
 'Printers use CMYK (Cyan, Magenta, Yellow, Black) ink. Screens use RGB. Design in CMYK where possible - bright RGB colours such as neon green may look duller when printed.', 3),
('Bleed Margins',
 'Add a 3 mm bleed on every side. Extend background colours and images past the trim line so no white edges appear after cutting.', 4),
('Safe Zone',
 'Keep all important text and logos at least 5 mm inside the trim line so they are not cut off.', 5),
('Fonts',
 'Convert text to outlines or embed fonts in your PDF. Use a minimum font size of 7 pt for small items such as business cards.', 6);

INSERT INTO faqs (question, answer, sort_order) VALUES
('How long does printing take?', 'Most products are ready in 2 to 4 working days. The exact turnaround time is shown on every product page.', 1),
('Can I cancel or reschedule my order?', 'Yes. You can cancel or change the date while the order is Pending or Processing. Once printing has started it cannot be changed.', 2),
('Do you deliver island-wide?', 'Yes, we deliver anywhere in Sri Lanka for a flat fee of LKR 350. You can also pick up for free from our Colombo branch.', 3),
('What if I do not have a design?', 'You can type your text when placing the order and our team will create a simple layout for you. Browse the Gallery for ideas.', 4),
('Which payment methods are accepted?', 'Cash on delivery / pickup and bank transfer. Online card payments are coming soon.', 5),
('Why does my print look different from my screen?', 'Screens show RGB colours while printers use CMYK ink. Read the Colour Format guideline for tips.', 6);
