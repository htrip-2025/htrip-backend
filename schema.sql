/*
  Revised DDL matching the ERD:
  - snake_case naming
  - AUTO_INCREMENT on PKs
  - composite PKs for join tables
  - all FKs declared
  - duplicate memberroles table dropped (use `roles` for all)
*/

CREATE DATABASE IF NOT EXISTS `htrip`;
USE `htrip`;

-- 권한(roles) 정의
CREATE TABLE roles (
  role_no INT NOT NULL AUTO_INCREMENT,
  role_name VARCHAR(50) NOT NULL,
  PRIMARY KEY (role_no)
) ENGINE=InnoDB;

-- 회원(사용자)
CREATE TABLE users (
  user_id INT NOT NULL AUTO_INCREMENT,
  email VARCHAR(255) NOT NULL,
  oauth_provider ENUM('kakao','naver','google') NOT NULL,
  oauth_id VARCHAR(100) NOT NULL,
  name VARCHAR(100) NOT NULL,
  nickname VARCHAR(100) NOT NULL,
  profile_img_url TEXT,
  regist_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_login_at DATETIME,
  role_no INT NOT NULL,
  PRIMARY KEY (user_id),
  INDEX idx_users_role (role_no),
  CONSTRAINT fk_users_role FOREIGN KEY (role_no) REFERENCES roles (role_no)
) ENGINE=InnoDB;

-- 게시판 카테고리
CREATE TABLE board_category (
  category_no INT NOT NULL AUTO_INCREMENT,
  category_name VARCHAR(20) NOT NULL,
  PRIMARY KEY (category_no)
) ENGINE=InnoDB;

-- 게시판 글
CREATE TABLE board (
  board_no INT NOT NULL AUTO_INCREMENT,
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
  CONSTRAINT fk_board_category FOREIGN KEY (category_no) REFERENCES board_category (category_no),
  CONSTRAINT fk_board_user FOREIGN KEY (user_id) REFERENCES users (user_id)
) ENGINE=InnoDB;

-- 댓글
CREATE TABLE comment (
  comment_id BIGINT NOT NULL AUTO_INCREMENT,
  board_no INT NOT NULL,
  user_id INT NOT NULL,
  comment TEXT NOT NULL,
  write_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (comment_id),
  INDEX idx_comment_board (board_no),
  INDEX idx_comment_user (user_id),
  CONSTRAINT fk_comment_board FOREIGN KEY (board_no) REFERENCES board (board_no),
  CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES users (user_id)
) ENGINE=InnoDB;

-- 게시글 추천 이력
CREATE TABLE board_likes (
  likes_no INT NOT NULL AUTO_INCREMENT,
  board_no INT NOT NULL,
  liked_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (likes_no),
  INDEX idx_bl_board (board_no),
  CONSTRAINT fk_bl_board FOREIGN KEY (board_no) REFERENCES board (board_no)
) ENGINE=InnoDB;

-- 사용자⇄추천 이력 연결
CREATE TABLE user_board_likes (
  user_id INT NOT NULL,
  likes_no INT NOT NULL,
  PRIMARY KEY (user_id, likes_no),
  INDEX idx_ubl_user (user_id),
  INDEX idx_ubl_likes (likes_no),
  CONSTRAINT fk_ubl_user FOREIGN KEY (user_id) REFERENCES users (user_id),
  CONSTRAINT fk_ubl_likes FOREIGN KEY (likes_no) REFERENCES board_likes (likes_no)
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
  content_id VARCHAR(50) NULL COMMENT '콘텐츠 ID (Primary Key)',
  content_type_id VARCHAR(10) NULL COMMENT '콘텐츠 타입 ID/우리 DB에 없음',
  title VARCHAR(255) NOT NULL COMMENT '콘텐츠 제목',
  created_time VARCHAR(14) NULL COMMENT '등록일시 (YYYYMMDDHHMMSS)',
  modified_time VARCHAR(14) NULL COMMENT '수정일시 (YYYYMMDDHHMMSS)',
  telephone VARCHAR(50) NULL,
  address1 VARCHAR(255) NULL,
  address2 VARCHAR(100) NULL,
  zip_code VARCHAR(10) NULL,
  category1 VARCHAR(10) NULL,
  category2 VARCHAR(10) NULL,
  category3 VARCHAR(10) NULL,
  latitude DECIMAL(11,8) NOT NULL,
  longitude DECIMAL(11,8) NOT NULL,
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
  CONSTRAINT fk_attr_area FOREIGN KEY (area_code) REFERENCES area (code),
  CONSTRAINT fk_attr_sigungu FOREIGN KEY (sigungu_code) REFERENCES sigungu (code)
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
  CONSTRAINT fk_fav_user FOREIGN KEY (user_id) REFERENCES users (user_id),
  CONSTRAINT fk_fav_place FOREIGN KEY (place_id) REFERENCES attraction (place_id)
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
  CONSTRAINT fk_tp_user FOREIGN KEY (user_id) REFERENCES users (user_id)
) ENGINE=InnoDB;

-- 계획 멤버: 역할 정의
CREATE TABLE plan_member (
  plan_id INT NOT NULL,
  role_no INT NOT NULL,
  nickname VARCHAR(20) NULL,
  PRIMARY KEY (plan_id, role_no),
  INDEX idx_pm_plan (plan_id),
  INDEX idx_pm_role (role_no),
  CONSTRAINT fk_pm_plan FOREIGN KEY (plan_id) REFERENCES travel_plan (plan_id),
  CONSTRAINT fk_pm_role FOREIGN KEY (role_no) REFERENCES roles (role_no)
) ENGINE=InnoDB;

-- 사용자⇄여행 계획 멤버 연결
CREATE TABLE user_member_connect (
  plan_id INT NOT NULL,
  user_id INT NOT NULL,
  PRIMARY KEY (plan_id, user_id),
  INDEX idx_umc_plan (plan_id),
  INDEX idx_umc_user (user_id),
  CONSTRAINT fk_umc_plan FOREIGN KEY (plan_id) REFERENCES travel_plan (plan_id),
  CONSTRAINT fk_umc_user FOREIGN KEY (user_id) REFERENCES users (user_id)
) ENGINE=InnoDB;

-- 여행 일자
CREATE TABLE plan_days (
  day_id INT NOT NULL AUTO_INCREMENT,
  plan_id INT NOT NULL,
  day_date DATE NULL,
  field VARCHAR(255) NULL,
  PRIMARY KEY (day_id),
  INDEX idx_td_plan (plan_id),
  CONSTRAINT fk_td_plan FOREIGN KEY (plan_id) REFERENCES travel_plan (plan_id)
) ENGINE=InnoDB;

-- 여행 세부 일정
CREATE TABLE plan_items (
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
  CONSTRAINT fk_ti_day FOREIGN KEY (day_id) REFERENCES plan_days (day_id),
  CONSTRAINT fk_ti_place FOREIGN KEY (place_id) REFERENCES attraction (place_id)
) ENGINE=InnoDB;
