-- 初始化数据库结构与基础表
CREATE DATABASE IF NOT EXISTS ebookstore DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE ebookstore;

DROP TABLE IF EXISTS visit_log;
DROP TABLE IF EXISTS carousel;
DROP TABLE IF EXISTS read_history;
DROP TABLE IF EXISTS favorite;
DROP TABLE IF EXISTS book_chapter;
DROP TABLE IF EXISTS comment_reaction;
DROP TABLE IF EXISTS comment_hide;
DROP TABLE IF EXISTS book_comment;
DROP TABLE IF EXISTS book_urge;
DROP TABLE IF EXISTS comment_notice;
DROP TABLE IF EXISTS book;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS user;

CREATE TABLE user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  union_id VARCHAR(128) UNIQUE,
  nickname VARCHAR(100) NOT NULL,
  avatar VARCHAR(255),
  email VARCHAR(100),
  role TINYINT NOT NULL DEFAULT 0 COMMENT '0普通用户 1管理员',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '0禁用 1启用',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE category (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  parent_id BIGINT NOT NULL DEFAULT 0,
  sort_order INT NOT NULL DEFAULT 0
);

CREATE TABLE book (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(200) NOT NULL,
  author VARCHAR(100) NOT NULL,
  publisher VARCHAR(100),
  isbn VARCHAR(50),
  category_id BIGINT NOT NULL,
  cover_url VARCHAR(500),
  description TEXT,
  file_url VARCHAR(500) NOT NULL,
  file_type VARCHAR(20) NOT NULL,
  status TINYINT NOT NULL DEFAULT 1 COMMENT '0下架 1上架',
  visit_count BIGINT NOT NULL DEFAULT 0,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE book_chapter (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  book_id BIGINT NOT NULL,
  title VARCHAR(255) NOT NULL,
  content LONGTEXT NOT NULL,
  sort_order INT NOT NULL DEFAULT 0,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_book_sort(book_id, sort_order)
);

CREATE TABLE book_comment (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  book_id BIGINT NOT NULL,
  chapter_id BIGINT NOT NULL,
  paragraph_index INT NOT NULL,
  user_id BIGINT NOT NULL,
  parent_id BIGINT DEFAULT NULL,
  content TEXT NOT NULL,
  status TINYINT NOT NULL DEFAULT 1 COMMENT '0 pending 1 approved 2 rejected',
  like_count INT NOT NULL DEFAULT 0,
  dislike_count INT NOT NULL DEFAULT 0,
  deleted TINYINT NOT NULL DEFAULT 0,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_comment_chapter(book_id, chapter_id, paragraph_index),
  KEY idx_comment_parent(parent_id)
);

CREATE TABLE comment_reaction (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  comment_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  value TINYINT NOT NULL COMMENT '1 like -1 dislike',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_comment_user(comment_id, user_id)
);

CREATE TABLE comment_hide (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  comment_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_hide_user(comment_id, user_id)
);

CREATE TABLE book_urge (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  book_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_urge_user(book_id, user_id)
);

CREATE TABLE comment_notice (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  comment_id BIGINT,
  book_id BIGINT NOT NULL,
  chapter_id BIGINT NOT NULL,
  paragraph_index INT NOT NULL,
  type TINYINT NOT NULL COMMENT '1 like 2 dislike',
  from_user_id BIGINT NOT NULL,
  read_flag TINYINT NOT NULL DEFAULT 0,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_notice_user(user_id, read_flag, create_time)
);

CREATE TABLE favorite (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  book_id BIGINT NOT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_user_book(user_id, book_id)
);

CREATE TABLE read_history (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  book_id BIGINT NOT NULL,
  chapter VARCHAR(255),
  progress VARCHAR(100),
  read_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_user_book(user_id, book_id)
);

CREATE TABLE carousel (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  image_url VARCHAR(500) NOT NULL,
  link VARCHAR(500),
  sort_order INT NOT NULL DEFAULT 0,
  status TINYINT NOT NULL DEFAULT 1
);

CREATE TABLE visit_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT,
  ip VARCHAR(64),
  url VARCHAR(255),
  visit_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 演示密码均为 123456
INSERT INTO user (username, password, nickname, email, role, status) VALUES
('admin', '$2b$10$ht/XnFFYH.n942kdEfMASua6D/pZsGPtxxkqvUFQXIBqc5loh8oN6', '系统管理员', 'admin@bookstore.com', 1, 1),
('reader', '$2b$10$ht/XnFFYH.n942kdEfMASua6D/pZsGPtxxkqvUFQXIBqc5loh8oN6', '普通读者', 'reader@bookstore.com', 0, 1);

INSERT INTO category (name, parent_id, sort_order) VALUES
('文学', 0, 1),
('小说', 1, 1),
('科幻', 2, 1),
('计算机', 0, 2),
('编程语言', 4, 1);

INSERT INTO book (title, author, publisher, isbn, category_id, cover_url, description, file_url, file_type, status, visit_count) VALUES
('三体', '刘慈欣', '重庆出版社', '9787536692930', 3, 'https://example.com/covers/santi.jpg', '中国科幻经典作品。', 'https://example.com/files/santi.epub', 'EPUB', 1, 128),
('Java 核心技术', 'Cay S. Horstmann', '机械工业出版社', '9787111641247', 5, 'https://example.com/covers/java.jpg', 'Java 入门与进阶图书。', 'https://example.com/files/java.pdf', 'PDF', 1, 86);

INSERT INTO book_chapter (book_id, title, content, sort_order) VALUES
(1, '第一章', '示例章节内容：第一章。', 1),
(1, '第二章', '示例章节内容：第二章。', 2),
(2, 'Chapter 1', 'Sample chapter content for Java book.', 1);

INSERT INTO carousel (image_url, link, sort_order, status) VALUES
('https://example.com/carousel/1.jpg', '/book/1', 1, 1),
('https://example.com/carousel/2.jpg', '/book/2', 2, 1);
