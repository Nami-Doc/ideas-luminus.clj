CREATE TABLE categories (
  id SERIAL,
  name VARCHAR(255) NOT NULL
);

CREATE TABLE ideas (
  id SERIAL,
  name varchar(255),
  description TEXT NOT NULL
);

CREATE TABLE idea_category (
  category_id INT NOT NULL,
  idea_id INT NOT NULL
);