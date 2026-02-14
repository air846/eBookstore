-- 为现有 user 表增加 union_id（小程序唯一标识）
ALTER TABLE user
  ADD COLUMN union_id VARCHAR(128) NULL UNIQUE AFTER password;
