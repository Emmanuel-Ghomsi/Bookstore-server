DELETE FROM book WHERE book_id is not null ;
ALTER TABLE book AUTO_INCREMENT = 1001;

DELETE FROM category WHERE category_id is not null;
ALTER TABLE category AUTO_INCREMENT = 1001;

INSERT INTO `category` (`name`) VALUES ('Crime'),('Adventure'),('Comedy'),('Suspense');

INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('Murder!', 'Arnold Bennett', '', 699, 0, TRUE, FALSE, 1001);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('The Speckled Band', 'Sir Arthur Conan Doyle', '', 799, 0, TRUE, FALSE, 1001);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('The Five Orange Pips', 'Arthur Conan Doyle', '', 599, 0, FALSE, FALSE, 1001);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('Death on the Air', 'Ngaio Marsh', '', 499, 0, FALSE, FALSE, 1001);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('An Occurrence at Owl Creek Bridge One of the Missing', 'Ambrose Bierce', '', 399, 0, TRUE, FALSE, 1002);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('From a View to a Kill', 'Ian Fleming', '', 299, 0, TRUE, FALSE, 1002);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('The Hostage', 'C. S. Forester', '', 199, 0, FALSE, FALSE, 1002);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('The Traveller''s Story of a Terribly Strange Bed', 'Wilkie Collins', '', 99, 0, FALSE, FALSE, 1002);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('Goodbye to all Cats', 'P. G. Wodehouse', '', 899, 0, TRUE, FALSE, 1003);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('On Guard, Bella Fleace Gave a Party', 'Evelyn Waugh', '', 999, 0, TRUE, FALSE, 1003);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('Sredni Vashtar, The Secret Sin Septimus Brope', 'Saki', '', 1099, 0, FALSE, FALSE, 1003);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('Here We Are A Telephone Call', 'Dorothy Parker', '', 1199, 0, FALSE, FALSE, 1003);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('The Pit and the Pendulum', 'Edgar Allan Poe', '', 1299, 0, TRUE, FALSE, 1004);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('The Vampyre', 'John Polidori', '', 1399, 0, TRUE, FALSE, 1004);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('The Signalman', 'Charles Dickens', '', 1499, 0, FALSE, FALSE, 1004);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('Country of the Blind', 'H.G. Wells', '', 1599, 0, FALSE, FALSE, 1004);