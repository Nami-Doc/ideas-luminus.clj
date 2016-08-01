CREATE TABLE implementations (
  id SERIAL PRIMARY KEY,
  idea_id integer NOT NULL REFERENCES ideas,
  user_id integer NOT NULL REFERENCES users,
  repo_url varchar(255),
  demo_url varchar(255),
  comment text
);
--;;
CREATE TABLE screenshots (
  id SERIAL PRIMARY KEY,
  implementation_id integer NOT NULL REFERENCES implementations
);
--;;
CREATE TABLE comments (
  id SERIAL PRIMARY KEY,
  parent_type varchar(50),
  parent_id integer NOT NULL
);
--;;
COMMENT ON COLUMN comments.parent_type IS 'Name of the table this comment belongs to';
--;;
COMMENT ON COLUMN comments.parent_id IS 'ID of the referenced element, from the table ${parent_type}';
