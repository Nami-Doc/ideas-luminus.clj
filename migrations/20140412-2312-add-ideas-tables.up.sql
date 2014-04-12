CREATE TABLE categories (
  id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL
);

CREATE TABLE ideas (
  id int PRIMARY KEY NOT NULL AUTO_INCREMENT,
  name varchar(255),
  description TEXT NOT NULL
);

CREATE TABLE idea_category (
  category_id INT NOT NULL,
  idea_id INT NOT NULL
);