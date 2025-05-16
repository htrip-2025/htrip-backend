/*
  Revised DDL matching the ERD:
  - snake_case naming
  - AUTO_INCREMENT on PKs
  - composite PKs for join tables
  - all FKs declared
  - cascade delete on child tables
  - users.role defined as ENUM for site-level roles
*/

CREATE DATABASE IF NOT EXISTS `htrip`;
USE `htrip`;

-- 계획 멤버 역할 정의
CREATE TABLE member_roles (
  role_no INT NOT NULL AUTO_INCREMENT,
  role_name VARCHAR(20) NOT NULL UNIQUE COMMENT '계획 내 역할 (leader, editor, viewer)',
  can_edit BOOLEAN NOT NULL DEFAULT FALSE COMMENT '편집 가능 여부',
  can_delete BOOLEAN NOT NULL DEFAULT FALSE COMMENT '삭제 가능 여부',
  description VARCHAR(100) NULL COMMENT '역할 설명',
  PRIMARY KEY (role_no)
) ENGINE=InnoDB;

INSERT INTO member_roles(role_name, can_edit, can_delete, description) VALUES
  ('LEADER', TRUE, TRUE, '계획 작성자 (전체 권한)'),
  ('EDITOR', TRUE, FALSE, '공동 편집자 (편집만 가능)'),
  ('VIEWER', FALSE, FALSE, '읽기 전용 사용자');

-- 사용자 (site-level role as ENUM)
CREATE TABLE users (
  user_id INT NOT NULL AUTO_INCREMENT,
  email VARCHAR(255) NOT NULL,
  oauth_provider ENUM('KAKAO','NAVER','GOOGLE') NOT NULL,
  oauth_id VARCHAR(100) NOT NULL,
  name VARCHAR(100) NOT NULL,
  nickname VARCHAR(100) NOT NULL,
  profile_img_url TEXT,
  regist_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_login_at DATETIME,
  role ENUM('USER','ADMIN') NOT NULL DEFAULT 'USER' COMMENT '사이트 역할',
  PRIMARY KEY (user_id)
) ENGINE=InnoDB;

-- 게시판 카테고리
CREATE TABLE board_category (
  category_no INT NOT NULL AUTO_INCREMENT,
  category_name VARCHAR(20) NOT NULL,
  PRIMARY KEY (category_no)
) ENGINE=InnoDB;

-- 게시판 글
CREATE TABLE board (
  board_no BIGINT NOT NULL AUTO_INCREMENT,
  category_no INT NOT NULL,
  content TEXT NOT NULL,
  user_id INT NOT NULL,
  write_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  views INT NOT NULL DEFAULT 0,
  likes INT NOT NULL DEFAULT 0,
  PRIMARY KEY (board_no),
  INDEX idx_board_category (category_no),
  INDEX idx_board_user (user_id),
  CONSTRAINT fk_board_category FOREIGN KEY (category_no) REFERENCES board_category(category_no),
  CONSTRAINT fk_board_user FOREIGN KEY (user_id) REFERENCES users(user_id)
) ENGINE=InnoDB;

-- 댓글
CREATE TABLE comment (
  comment_id BIGINT NOT NULL AUTO_INCREMENT,
  board_no BIGINT NOT NULL,
  user_id INT NOT NULL,
  comment TEXT NOT NULL,
  write_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (comment_id),
  INDEX idx_comment_board (board_no),
  INDEX idx_comment_user (user_id),
  CONSTRAINT fk_comment_board FOREIGN KEY (board_no) REFERENCES board(board_no) ON DELETE CASCADE,
  CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES users(user_id)
) ENGINE=InnoDB;

-- 게시글 추천 이력
CREATE TABLE board_likes (
  likes_no INT NOT NULL AUTO_INCREMENT,
  board_no BIGINT NOT NULL,
  liked_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (likes_no),
  INDEX idx_bl_board (board_no),
  CONSTRAINT fk_bl_board FOREIGN KEY (board_no) REFERENCES board(board_no) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 사용자⇄추천 이력 연결
CREATE TABLE user_board_likes (
  user_id INT NOT NULL,
  likes_no INT NOT NULL,
  PRIMARY KEY (user_id, likes_no),
  INDEX idx_ubl_user (user_id),
  INDEX idx_ubl_likes (likes_no),
  CONSTRAINT fk_ubl_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
  CONSTRAINT fk_ubl_likes FOREIGN KEY (likes_no) REFERENCES board_likes(likes_no) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 지역 코드
CREATE TABLE area (
  code INT NOT NULL,
  name VARCHAR(50) NOT NULL,
  PRIMARY KEY (code)
) ENGINE=InnoDB;

CREATE TABLE sigungu (
  code INT NOT NULL,
  name VARCHAR(50) NOT NULL,
  PRIMARY KEY (code)
) ENGINE=InnoDB;

-- 관광지 정보
CREATE TABLE attraction (
  place_id INT NOT NULL AUTO_INCREMENT,
  content_id VARCHAR(50) NULL COMMENT '콘텐츠 ID',
  content_type_id VARCHAR(10) NULL COMMENT '콘텐츠 타입 ID',
  title VARCHAR(255) NOT NULL,
  created_time VARCHAR(14) NULL COMMENT '등록일시(YYYYMMDDHHMMSS)',
  modified_time VARCHAR(14) NULL COMMENT '수정일시(YYYYMMDDHHMMSS)',
  telephone VARCHAR(50) NULL,
  address1 VARCHAR(255) NULL,
  address2 VARCHAR(100) NULL,
  zip_code VARCHAR(10) NULL,
  category1 VARCHAR(10) NULL,
  category2 VARCHAR(10) NULL,
  category3 VARCHAR(10) NULL,
  latitude DOUBLE NOT NULL,
  longitude DOUBLE NOT NULL,
  map_level VARCHAR(5) NULL,
  first_image_url VARCHAR(255) NULL,
  first_image_thumbnail_url VARCHAR(255) NULL,
  copyright_division_code VARCHAR(10) NULL,
  booktour_info VARCHAR(10) NULL,
  area_code INT NOT NULL,
  sigungu_code INT NOT NULL,
  PRIMARY KEY (place_id),
  INDEX idx_attr_area (area_code),
  INDEX idx_attr_sigungu (sigungu_code),
  CONSTRAINT fk_attr_area FOREIGN KEY (area_code) REFERENCES area(code),
  CONSTRAINT fk_attr_sigungu FOREIGN KEY (sigungu_code) REFERENCES sigungu(code)
) ENGINE=InnoDB;

-- 즐겨찾기
CREATE TABLE favorite (
  favorite_no INT NOT NULL AUTO_INCREMENT,
  user_id INT NOT NULL,
  create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  memo TEXT NULL,
  tag VARCHAR(255) NULL,
  place_id INT NOT NULL,
  PRIMARY KEY (favorite_no),
  INDEX idx_fav_user (user_id),
  INDEX idx_fav_place (place_id),
  CONSTRAINT fk_fav_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
  CONSTRAINT fk_fav_place FOREIGN KEY (place_id) REFERENCES attraction(place_id)
) ENGINE=InnoDB;

-- 여행 계획
CREATE TABLE trip_plan (
  plan_id INT NOT NULL AUTO_INCREMENT,
  user_id INT NOT NULL,
  title VARCHAR(30) NULL,
  create_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  start_date DATE NULL,
  end_date DATE NULL,
  is_public BOOLEAN NOT NULL DEFAULT TRUE,
  PRIMARY KEY (plan_id),
  INDEX idx_tp_user (user_id),
  CONSTRAINT fk_tp_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 계획 멤버 (여행 계획↔사용자 N:M)
CREATE TABLE plan_member (
  plan_id INT NOT NULL,
  user_id INT NOT NULL,
  role_no INT NOT NULL,
  nickname VARCHAR(20),
  PRIMARY KEY (plan_id, user_id),
  CONSTRAINT fk_pm_plan FOREIGN KEY (plan_id) REFERENCES trip_plan(plan_id) ON DELETE CASCADE,
  CONSTRAINT fk_pm_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
  CONSTRAINT fk_pm_role FOREIGN KEY (role_no) REFERENCES member_roles(role_no)
) ENGINE=InnoDB;

-- 여행 일자
CREATE TABLE trip_days (
  day_id INT NOT NULL AUTO_INCREMENT,
  plan_id INT NOT NULL,
  day_date DATE NULL,
  field VARCHAR(255) NULL,
  PRIMARY KEY (day_id),
  INDEX idx_td_plan (plan_id),
  CONSTRAINT fk_td_plan FOREIGN KEY (plan_id) REFERENCES trip_plan(plan_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 여행 세부 일정
CREATE TABLE trip_items (
  item_id INT NOT NULL AUTO_INCREMENT,
  day_id INT NOT NULL,
  place_id INT NOT NULL,
  sequence INT NOT NULL,
  start_time TIME NULL,
  end_time TIME NULL,
  memo TEXT NULL,
  PRIMARY KEY (item_id),
  INDEX idx_ti_day (day_id),
  INDEX idx_ti_place (place_id),
  CONSTRAINT fk_ti_day FOREIGN KEY (day_id) REFERENCES trip_days(day_id) ON DELETE CASCADE,
  CONSTRAINT fk_ti_place FOREIGN KEY (place_id) REFERENCES attraction(place_id)
) ENGINE=InnoDB;
