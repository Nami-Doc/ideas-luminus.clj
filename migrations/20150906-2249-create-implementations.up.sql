CREATE TABLE `implementations` (
  `id` SERIAL,
  `idea_id` integer NOT NULL REFERENCES `ideas`,
  `user_id` integer NOT NULL REFERENCES `users`,
  `repo_url` varchar(255),
  `demo_url` varchar(255),
  `coment` text,
);

CREATE TABLE `screenshots` (
  `id` SERIAL,
  `implementation_id` integer NOT NULL REFERENCES `implementations`
);

CREATE TABLE `comments` (
  `id` SERIAL,
  `parent_type` varchar(50),
  `parent_id` integer NOT NULL,
);
COMMENT ON COLUMN `comments`.`parent_type` IS 'Name of the table this comment comments';
COMMENT ON COLUMN `comments`.`parent_id` IS 'ID of the referenced element, from the table ${parent_type}';
