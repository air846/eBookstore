USE ebookstore;

-- 默认密码改为 123456（BCrypt）
UPDATE user
SET password = '$2b$10$ht/XnFFYH.n942kdEfMASua6D/pZsGPtxxkqvUFQXIBqc5loh8oN6'
WHERE username IN ('admin', 'reader');
