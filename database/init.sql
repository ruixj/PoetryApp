-- ============================================================
-- 诗韵童学 - Poetry Learning App Database Schema
-- MySQL 8.0+ | utf8mb4_unicode_ci
-- ============================================================

CREATE DATABASE IF NOT EXISTS poetry_app CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE poetry_app;

-- ===========================
-- User Management
-- ===========================

CREATE TABLE users (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    phone       VARCHAR(11)  NOT NULL COMMENT '手机号',
    password    VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
    nickname    VARCHAR(50)  COMMENT '昵称',
    avatar_url  VARCHAR(500) COMMENT '头像URL',
    yuanbao_points INT DEFAULT 0 COMMENT '元宝积分',
    total_study_minutes INT DEFAULT 0 COMMENT '学习时长（分钟）',
    role        VARCHAR(20) DEFAULT 'USER' COMMENT '角色：USER, ADMIN',
    is_first_login BOOLEAN DEFAULT TRUE COMMENT '是否首次登录',
    textbook_id BIGINT COMMENT '当前选择的教材体系',
    grade_id    BIGINT COMMENT '当前选择的年级',
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_phone (phone),
    INDEX idx_role (role)
) COMMENT='用户表';

CREATE TABLE login_records (
    id               BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    user_id          BIGINT NOT NULL COMMENT '用户ID',
    login_time       DATETIME NOT NULL COMMENT '登录时间',
    logout_time      DATETIME COMMENT '退出时间',
    duration_minutes INT DEFAULT 0 COMMENT '本次登录时长（分钟）',
    INDEX idx_user_id (user_id),
    INDEX idx_login_time (login_time)
) COMMENT='登录记录表';

CREATE TABLE sms_codes (
    id         BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    phone      VARCHAR(11) NOT NULL COMMENT '手机号',
    code       VARCHAR(6)  NOT NULL COMMENT '验证码',
    expires_at DATETIME NOT NULL COMMENT '过期时间',
    is_used    BOOLEAN DEFAULT FALSE COMMENT '是否已使用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_phone (phone),
    INDEX idx_expires_at (expires_at)
) COMMENT='短信验证码表';

-- ===========================
-- Content Management
-- ===========================

CREATE TABLE textbook_systems (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '教材体系ID',
    name        VARCHAR(100) NOT NULL COMMENT '名称',
    description VARCHAR(500) COMMENT '描述',
    order_num   INT DEFAULT 0 COMMENT '排序',
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT='教材体系表';

CREATE TABLE grades (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '年级ID',
    textbook_id  BIGINT NOT NULL COMMENT '教材体系ID',
    name         VARCHAR(100) NOT NULL COMMENT '年级名称',
    level        VARCHAR(20)  NOT NULL COMMENT '学段：PRIMARY（小学），MIDDLE（初中）',
    grade_number INT NOT NULL COMMENT '年级数字（1-6小学，7-9初中）',
    order_num    INT DEFAULT 0 COMMENT '排序',
    INDEX idx_textbook_id (textbook_id)
) COMMENT='年级表';

CREATE TABLE units (
    id        BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '单元ID',
    grade_id  BIGINT NOT NULL COMMENT '年级ID',
    name      VARCHAR(200) NOT NULL COMMENT '单元名称',
    order_num INT DEFAULT 0 COMMENT '排序',
    INDEX idx_grade_id (grade_id)
) COMMENT='单元表';

CREATE TABLE poems (
    id                 BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '古诗ID',
    title              VARCHAR(200) NOT NULL COMMENT '标题',
    author             VARCHAR(100) COMMENT '作者',
    dynasty            VARCHAR(50)  COMMENT '朝代',
    content            TEXT NOT NULL COMMENT '诗文内容',
    pinyin             TEXT COMMENT '拼音标注（JSON数组，每行每字）',
    translation        TEXT COMMENT '译文',
    background         TEXT COMMENT '写作背景',
    author_intro       TEXT COMMENT '作者介绍',
    animation_url      VARCHAR(500) COMMENT '故事动画视频URL',
    mindmap_data       TEXT COMMENT '思维导图数据（JSON）',
    audio_url          VARCHAR(500) COMMENT '朗读音频URL',
    difficulty_words   TEXT COMMENT '难点字（JSON数组）',
    created_at         DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at         DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT='古诗表';

CREATE TABLE unit_poems (
    unit_id   BIGINT NOT NULL COMMENT '单元ID',
    poem_id   BIGINT NOT NULL COMMENT '古诗ID',
    order_num INT DEFAULT 0 COMMENT '排序',
    PRIMARY KEY (unit_id, poem_id),
    INDEX idx_poem_id (poem_id)
) COMMENT='单元古诗关联表';

CREATE TABLE poem_categories (
    id             BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    poem_id        BIGINT NOT NULL COMMENT '古诗ID',
    category_type  VARCHAR(50)  NOT NULL COMMENT '类型：SOLAR_TERM节气,THEME题材,POET诗人,FLYING_FLOWER飞花令',
    category_value VARCHAR(100) NOT NULL COMMENT '值',
    INDEX idx_poem_id (poem_id),
    INDEX idx_category (category_type, category_value)
) COMMENT='古诗分类标签表';

-- ===========================
-- Learning Progress
-- ===========================

CREATE TABLE user_poem_library (
    id       BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    user_id  BIGINT NOT NULL COMMENT '用户ID',
    poem_id  BIGINT NOT NULL COMMENT '古诗ID',
    added_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
    UNIQUE KEY uk_user_poem (user_id, poem_id),
    INDEX idx_user_id (user_id)
) COMMENT='用户学习库（已选择要学习的古诗）';

CREATE TABLE user_poem_progress (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    user_id       BIGINT NOT NULL COMMENT '用户ID',
    poem_id       BIGINT NOT NULL COMMENT '古诗ID',
    current_stage VARCHAR(30) DEFAULT 'LISTEN' COMMENT '当前阶段：LISTEN,READ,UNDERSTAND,ANALYZE,MEMORIZE,COMPLETED',
    is_completed  BOOLEAN DEFAULT FALSE COMMENT '是否完成',
    completed_at  DATETIME COMMENT '完成时间',
    recording_url VARCHAR(500) COMMENT '背诵录音URL',
    UNIQUE KEY uk_user_poem_progress (user_id, poem_id),
    INDEX idx_user_id (user_id),
    INDEX idx_is_completed (is_completed)
) COMMENT='用户古诗学习进度表';

-- ===========================
-- Game
-- ===========================

CREATE TABLE game_submissions (
    id                    BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '提交ID',
    user_id               BIGINT NOT NULL COMMENT '用户ID',
    poem_id               BIGINT NOT NULL COMMENT '古诗ID',
    game_category_type    VARCHAR(50)  COMMENT '游戏类别类型',
    game_category_value   VARCHAR(100) COMMENT '游戏类别值',
    input_text            TEXT COMMENT '输入文本',
    is_valid              BOOLEAN DEFAULT FALSE COMMENT '是否有效',
    points_earned         INT DEFAULT 0 COMMENT '获得积分',
    created_at            DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    INDEX idx_user_id (user_id),
    INDEX idx_category (game_category_type, game_category_value),
    INDEX idx_created_at (created_at)
) COMMENT='游戏提交记录表';

-- ===========================
-- Shop
-- ===========================

CREATE TABLE shop_items (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '商品ID',
    name        VARCHAR(200) NOT NULL COMMENT '商品名称',
    description TEXT COMMENT '商品描述',
    image_url   VARCHAR(500) COMMENT '商品图片URL',
    points_cost INT NOT NULL COMMENT '所需元宝积分',
    stock       INT DEFAULT 0 COMMENT '库存',
    status      VARCHAR(20) DEFAULT 'ON_SHELF' COMMENT '状态：ON_SHELF上架,OFF_SHELF下架',
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_status (status)
) COMMENT='商品表';

CREATE TABLE cart_items (
    id         BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '购物车项目ID',
    user_id    BIGINT NOT NULL COMMENT '用户ID',
    item_id    BIGINT NOT NULL COMMENT '商品ID',
    quantity   INT DEFAULT 1 COMMENT '数量',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_item (user_id, item_id),
    INDEX idx_user_id (user_id)
) COMMENT='购物车表';

CREATE TABLE orders (
    id               BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
    order_no         VARCHAR(50) NOT NULL COMMENT '订单号',
    user_id          BIGINT NOT NULL COMMENT '用户ID',
    total_points     INT NOT NULL COMMENT '总积分',
    shipping_name    VARCHAR(100) COMMENT '收件人姓名',
    shipping_phone   VARCHAR(11)  COMMENT '收件人电话',
    shipping_address TEXT COMMENT '收货地址',
    status           VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态：PENDING,PROCESSING,SHIPPED,COMPLETED,CANCELLED',
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_order_no (order_no),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
) COMMENT='订单表';

CREATE TABLE order_items (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单项目ID',
    order_id    BIGINT NOT NULL COMMENT '订单ID',
    item_id     BIGINT NOT NULL COMMENT '商品ID',
    item_name   VARCHAR(200) COMMENT '商品名称（快照）',
    item_image  VARCHAR(500) COMMENT '商品图片URL（快照）',
    quantity    INT NOT NULL COMMENT '数量',
    points_cost INT NOT NULL COMMENT '单价积分（快照）',
    INDEX idx_order_id (order_id)
) COMMENT='订单项目表';

-- ===========================
-- Initial Data
-- ===========================

INSERT INTO textbook_systems (name, description, order_num) VALUES
('人教版',   '人民教育出版社教材体系', 1),
('北师大版', '北京师范大学出版社教材体系', 2),
('苏教版',   '江苏教育出版社教材体系', 3),
('粤教版',   '广东教育出版社教材体系', 4);

-- 人教版年级（textbook_id=1）
INSERT INTO grades (textbook_id, name, level, grade_number, order_num) VALUES
(1, '小学一年级', 'PRIMARY', 1, 1),
(1, '小学二年级', 'PRIMARY', 2, 2),
(1, '小学三年级', 'PRIMARY', 3, 3),
(1, '小学四年级', 'PRIMARY', 4, 4),
(1, '小学五年级', 'PRIMARY', 5, 5),
(1, '小学六年级', 'PRIMARY', 6, 6),
(1, '初中七年级', 'MIDDLE',  7, 7),
(1, '初中八年级', 'MIDDLE',  8, 8),
(1, '初中九年级', 'MIDDLE',  9, 9);

-- 北师大版年级（textbook_id=2）
INSERT INTO grades (textbook_id, name, level, grade_number, order_num) VALUES
(2, '小学一年级', 'PRIMARY', 1, 1),
(2, '小学二年级', 'PRIMARY', 2, 2),
(2, '小学三年级', 'PRIMARY', 3, 3),
(2, '小学四年级', 'PRIMARY', 4, 4),
(2, '小学五年级', 'PRIMARY', 5, 5),
(2, '小学六年级', 'PRIMARY', 6, 6),
(2, '初中七年级', 'MIDDLE',  7, 7),
(2, '初中八年级', 'MIDDLE',  8, 8),
(2, '初中九年级', 'MIDDLE',  9, 9);

-- 人教版一年级单元
INSERT INTO units (grade_id, name, order_num) VALUES
(1, '第一单元', 1),
(1, '第二单元', 2),
(1, '第三单元', 3);

-- 经典古诗 (示例)
INSERT INTO poems (title, author, dynasty, content, pinyin, translation, background, author_intro, difficulty_words) VALUES
('静夜思', '李白', '唐',
 '床前明月光，疑是地上霜。举头望明月，低头思故乡。',
 '[{"line":"床前明月光","pinyin":"chuáng qián míng yuè guāng"},{"line":"疑是地上霜","pinyin":"yí shì dì shàng shuāng"},{"line":"举头望明月","pinyin":"jǔ tóu wàng míng yuè"},{"line":"低头思故乡","pinyin":"dī tóu sī gù xiāng"}]',
 '皎洁的月光洒在床前，好像地上泛起了一层白霜。我禁不住抬起头来，看那天窗外空中的一轮明月，不由得低头沉思，想起远方的家乡。',
 '这首诗写于唐玄宗开元十五年（727年），李白旅居扬州，夜不成寐，见月思乡，写下了这首千古传诵的名诗。',
 '李白（701年—762年）字太白，号青莲居士，又号"谪仙人"，唐代伟大的浪漫主义诗人，被后人誉为"诗仙"。',
 '["疑","举","故"]'),

('春晓', '孟浩然', '唐',
 '春眠不觉晓，处处闻啼鸟。夜来风雨声，花落知多少。',
 '[{"line":"春眠不觉晓","pinyin":"chūn mián bù jué xiǎo"},{"line":"处处闻啼鸟","pinyin":"chù chù wén tí niǎo"},{"line":"夜来风雨声","pinyin":"yè lái fēng yǔ shēng"},{"line":"花落知多少","pinyin":"huā luò zhī duō shǎo"}]',
 '春天的夜晚一直睡到天亮，醒来只听见到处有鸟儿啼叫。想起昨夜风声雨声，不知道又有多少花朵被打落了。',
 '这是一首格调轻快的山水诗，写春天早晨的景色，赞美春光的美好，流露出诗人喜爱春天的心情。',
 '孟浩然（689年—740年），名浩，字浩然，号孟山人，唐代著名的山水田园派诗人。',
 '["晓","啼","处"]'),

('望庐山瀑布', '李白', '唐',
 '日照香炉生紫烟，遥看瀑布挂前川。飞流直下三千尺，疑是银河落九天。',
 '[{"line":"日照香炉生紫烟","pinyin":"rì zhào xiāng lú shēng zǐ yān"},{"line":"遥看瀑布挂前川","pinyin":"yáo kàn pù bù guà qián chuān"},{"line":"飞流直下三千尺","pinyin":"fēi liú zhí xià sān qiān chǐ"},{"line":"疑是银河落九天","pinyin":"yí shì yín hé luò jiǔ tiān"}]',
 '太阳照耀香炉峰，升起一片紫色烟雾。远远望见瀑布像一条白链高挂山前。那飞流而下的瀑布有三千尺之长，真好像是银河从天际坠落下来。',
 '这首诗是诗人李白游历庐山时写的，描写了庐山瀑布的壮美景色。',
 '李白（701年—762年）字太白，号青莲居士，唐代伟大的浪漫主义诗人。',
 '["炉","遥","挂","疑"]'),

('悯农', '李绅', '唐',
 '锄禾日当午，汗滴禾下土。谁知盘中餐，粒粒皆辛苦。',
 '[{"line":"锄禾日当午","pinyin":"chú hé rì dāng wǔ"},{"line":"汗滴禾下土","pinyin":"hàn dī hé xià tǔ"},{"line":"谁知盘中餐","pinyin":"shuí zhī pán zhōng cān"},{"line":"粒粒皆辛苦","pinyin":"lì lì jiē xīn kǔ"}]',
 '农民在正午烈日的暴晒下锄禾，汗水从身上滴落在禾苗生长的土地上。又有谁知道盘中的饭食，每颗每粒都是农民用辛勤的劳动换来的呢？',
 '这首诗描绘了农民的艰辛，表达了对劳动人民的深切同情，告诫人们要爱惜粮食。',
 '李绅（772年—846年），字公垂，唐代著名诗人。',
 '["锄","禾","粒","皆"]'),

('登鹳雀楼', '王之涣', '唐',
 '白日依山尽，黄河入海流。欲穷千里目，更上一层楼。',
 '[{"line":"白日依山尽","pinyin":"bái rì yī shān jìn"},{"line":"黄河入海流","pinyin":"huáng hé rù hǎi liú"},{"line":"欲穷千里目","pinyin":"yù qióng qiān lǐ mù"},{"line":"更上一层楼","pinyin":"gèng shàng yī céng lóu"}]',
 '夕阳依傍着西山慢慢地沉没，滔滔黄河朝着东海汹涌奔流。若想把千里的风光景物看够，那就要登上更高的一层城楼。',
 '诗人登上鹳雀楼时，描写了登楼时看到的景象，并由此得出了要不断进取的哲理。',
 '王之涣（688年—742年），字季凌，唐代著名诗人。',
 '["依","欲","穷","更"]');

-- 关联古诗到单元
INSERT INTO unit_poems (unit_id, poem_id, order_num) VALUES
(1, 1, 1),
(1, 2, 2),
(2, 3, 1),
(2, 4, 2),
(3, 5, 1);

-- 古诗分类标签
INSERT INTO poem_categories (poem_id, category_type, category_value) VALUES
-- 静夜思
(1, 'THEME',         '思乡'),
(1, 'POET',          '李白'),
(1, 'FLYING_FLOWER', '月'),
-- 春晓
(2, 'THEME',         '写景'),
(2, 'POET',          '孟浩然'),
(2, 'FLYING_FLOWER', '花'),
(2, 'SOLAR_TERM',    '春分'),
-- 望庐山瀑布
(3, 'THEME',         '写景'),
(3, 'POET',          '李白'),
(3, 'FLYING_FLOWER', '烟'),
-- 悯农
(4, 'THEME',         '叙事'),
(4, 'FLYING_FLOWER', '日'),
-- 登鹳雀楼
(5, 'THEME',         '写景'),
(5, 'POET',          '王之涣'),
(5, 'FLYING_FLOWER', '日');

-- Admin user (password: Admin@123 — will be set by AdminInitRunner on startup)
-- Role ADMIN with is_first_login=FALSE
INSERT INTO users (phone, password, nickname, role, is_first_login) VALUES
('13800000000', 'PENDING_INIT', '诗韵管理员', 'ADMIN', FALSE);
