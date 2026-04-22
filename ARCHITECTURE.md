# 诗韵童学 · 系统架构图

## 一、整体架构

```
┌──────────────────────────────────────────────────────────────────────┐
│                           用户终端                                    │
│              浏览器 / 移动端浏览器 (React SPA)                         │
│                        http://localhost:3000                          │
└───────────────────────────────┬──────────────────────────────────────┘
                                │ HTTP  /api/*
                                ▼
┌──────────────────────────────────────────────────────────────────────┐
│                      Frontend  (Nginx)                                │
│   React 18 · Ant Design 5 · Zustand · Framer Motion · React Router 6 │
│                                                                        │
│   ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐  │
│   │ LoginPage│ │Learning  │ │ GamePage │ │ ShopPage │ │ Profile  │  │
│   │          │ │  Page    │ │          │ │          │ │  Page    │  │
│   └──────────┘ └──────────┘ └──────────┘ └──────────┘ └──────────┘  │
│                                      ┌──────────┐                    │
│                                      │AdminPage │  (ADMIN role only) │
│                                      └──────────┘                    │
│   状态管理: authStore (Zustand + localStorage)                        │
│   HTTP 层:  axios (/api/*  →  proxy → Gateway:8080)                  │
└───────────────────────────────┬──────────────────────────────────────┘
                                │ proxy_pass http://gateway:8080
                                ▼
┌──────────────────────────────────────────────────────────────────────┐
│                  API Gateway  :8080                                   │
│         Spring Cloud Gateway · 路由 · 限流 · 熔断                     │
│                                                                        │
│  路由规则 (lb:// 负载均衡 via Nacos):                                  │
│  /api/auth/**      →  auth-service                                    │
│  /api/user/**      →  user-service                                    │
│  /api/poetry/**    →  poetry-service                                  │
│  /api/game/**      →  game-service                                    │
│  /api/shop/**      →  shop-service                                    │
│  /api/admin/**     →  admin-service                                   │
│                                                                        │
│  过滤器: JWT 鉴权 (GatewayAuthFilter) · RequestRateLimiter(Redis) · CircuitBreaker│
└──┬──────┬──────┬──────┬──────┬──────┬────────────────────────────────┘
   │      │      │      │      │      │  (Spring Cloud LoadBalancer)
   ▼      ▼      ▼      ▼      ▼      ▼
```

## 二、微服务层

```
┌────────────┐  ┌────────────┐  ┌───────────────┐  ┌────────────┐  ┌────────────┐  ┌────────────┐
│auth-service│  │user-service│  │poetry-service │  │game-service│  │shop-service│  │admin-service│
│  :8081     │  │  :8082     │  │  :8083        │  │  :8084     │  │  :8085     │  │  :8086     │
│────────────│  │────────────│  │───────────────│  │────────────│  │────────────│  │────────────│
│发送短信验证│  │用户资料     │  │教材/年级/单元  │  │飞花令/节气 │  │商品/购物车 │  │用户/内容   │
│码 (SMS/log)│  │昵称/头像   │  │古诗 CRUD      │  │题材/作者   │  │订单管理    │  │商城管理    │
│注册 / 登录 │  │积分/学习时长│  │学习进度五阶段 │  │提交诗句    │  │元宝结算    │  │仅限 ADMIN  │
│JWT 签发    │  │            │  │录音上传       │  │排行计分    │  │            │  │角色校验    │
│AdminInit   │  │            │  │               │  │            │  │            │  │文件上传    │
│Runner      │  │            │  │               │  │            │  │            │  │            │
└─────┬──────┘  └─────┬──────┘  └──────┬────────┘  └─────┬──────┘  └─────┬──────┘  └─────┬──────┘
      │               │                │                  │               │               │
      └───────────────┴────────────────┴──────────────────┴───────────────┴───────────────┘
                                       │  MyBatis (mybatis-spring-boot-starter 3.0.3)
                                       ▼
```

## 三、基础设施层

```
┌─────────────────────────────────────────────────────────────────┐
│                      基础设施                                    │
│                                                                  │
│  ┌──────────────────────────┐   ┌──────────────────────────┐    │
│  │  Nacos  :8848 / :9848    │   │   MySQL 8.0  :3306        │    │
│  │  standalone (Derby)      │   │   database: poetry_app    │    │
│  │  - 服务注册与发现         │   │   charset: utf8mb4        │    │
│  │  - 配置中心               │   │   初始化: database/init.sql│   │
│  └──────────────────────────┘   │   持久化卷: mysql_data     │    │
│                                  └──────────────────────────┘    │
│  ┌──────────────────────────┐                                     │
│  │  Redis 7  :6379          │                                     │
│  │  - Gateway 限流令牌桶     │                                     │
│  │  (RequestRateLimiter)    │                                     │
│  └──────────────────────────┘                                     │
│  ┌──────────────────────────────────────────────────────────┐    │
│  │  共享文件卷  ./uploads                                    │    │
│  │  auth / user / poetry / admin 服务均挂载此目录            │    │
│  │  存储: 头像、朗读音频、动画视频、学生录音                  │    │
│  └──────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
```

## 四、数据库 Schema

```
poetry_app
│
├── users                # 用户 (phone, nickname, role, yuanbao_points, total_study_minutes)
├── sms_codes            # 短信验证码 (phone, code, expires_at, is_used)
├── login_records        # 登录记录 (user_id, login_time, logout_time, duration_minutes)
│
├── textbook_systems     # 教材 (name, description, order_num)
├── grades               # 年级 (name, textbook_id, level, grade_number, order_num)
├── units                # 单元 (name, grade_id, order_num)
├── poems                # 古诗 (title, dynasty, author, content, pinyin, translation,
│                        #        author_intro, background, audio_url, animation_url,
│                        #        mindmap_data, difficulty_words)
├── poem_categories      # 古诗分类 (poem_id, category_type, category_value)
│                        #  category_type: KEYWORD / SEASON / THEME / AUTHOR
├── unit_poems           # 单元-古诗关联 (unit_id, poem_id, order_num)
│
├── user_poem_library    # 用户诗库 (user_id, poem_id, added_at)
├── user_poem_progress   # 学习进度 (user_id, poem_id, stage, is_completed,
│                        #            recording_url, completed_at)
│                        #  stage: LISTEN → READ → UNDERSTAND → ANALYZE → MEMORIZE → COMPLETED
│
├── game_submissions     # 游戏提交 (user_id, poem_id, category_type, category_value)
│
├── shop_items           # 商品 (name, description, points_cost, stock, image_url, status)
│                        #  status: ON_SHELF / OFF_SHELF
├── cart_items           # 购物车 (user_id, item_id, quantity)
├── orders               # 订单 (user_id, order_no, total_points, shipping_name,
│                        #        shipping_phone, shipping_address, status)
│                        #  status: PENDING → SHIPPED → COMPLETED
└── order_items          # 订单明细 (order_id, item_id, item_name, points_cost, quantity)
```

## 五、认证与鉴权流程

```
客户端                   Gateway               auth-service              MySQL
   │                        │                       │                       │
   │── POST /api/auth/login ─►                       │                       │
   │                        │── 路由 ──────────────►│                       │
   │                        │                       │── 验证手机+密码/SMS ──►│
   │                        │                       │◄── user 记录 ─────────│
   │                        │                       │── 签发 JWT (HS256) ───┐│
   │◄── { token, user } ────│◄──────────────────────│                       │
   │                        │                       │                       │
   │── 后续请求              │                       │                       │
   │  Authorization:         │                       │                       │
   │  Bearer <JWT>  ─────────►                       │                       │
   │                        │ GatewayAuthFilter      │                       │
   │                        │ 验证 JWT 签名 + 提取    │                       │
   │                        │ userId/role → Header   │                       │
   │                        │── 转发 + X-User-Id ──►下游服务                 │
   │                        │                                                │
   │                     白名单 (无需 JWT):                                   │
   │                     /api/auth/login, /api/auth/register, /api/auth/sms │
```

## 六、学习进度五阶段流程

```
用户打开古诗卡片
        │
        ▼
   ┌─────────┐     ┌─────────┐     ┌─────────────┐     ┌──────────┐     ┌──────────┐
   │  LISTEN │ ──► │  READ   │ ──► │  UNDERSTAND │ ──► │ ANALYZE  │ ──► │ MEMORIZE │
   │ 听音频  │     │ 录音跟读│     │ 诗人介绍+   │     │ 诗意译文 │     │ 背诵录音 │
   │         │     │(MediaRec│     │ 写作背景+   │     │          │     │ 上传保存 │
   └─────────┘     │ order)  │     │ 动画视频    │     └──────────┘     └────┬─────┘
                   └─────────┘     └─────────────┘                          │
                                                                             ▼
                                                                       COMPLETED
                                                                   learning_progress
                                                                   +1 元宝 (store)
                                                                   检测等级晋升
                                                                   (晋升 → 诏书动画)
```

## 七、等级体系

| 等级 | 称号 | 需完成古诗数 |
|------|------|------------|
| 1    | 童生  | 0 首起      |
| 2    | 秀才  | 10 首       |
| 3    | 举人  | 30 首       |
| 4    | 贡士  | 50 首       |
| 5    | 进士  | 70 首       |

晋级时前端触发 `ImperialDecree` 弹窗（Framer Motion spring 动画）。

## 八、部署拓扑（Docker Compose）

```
Docker Network (默认 bridge)
│
├── poetry-mysql       :3306   ← volume: mysql_data
├── poetry-nacos       :8848, :9848
│
├── poetry-gateway     :8080   ← depends_on: nacos
├── poetry-auth        :8081   ← depends_on: nacos, mysql
├── poetry-user        :8082   ← depends_on: nacos, mysql
├── poetry-poetry      :8083   ← depends_on: nacos, mysql
├── poetry-game        :8084   ← depends_on: nacos, mysql
├── poetry-shop        :8085   ← depends_on: nacos, mysql
├── poetry-admin       :8086   ← depends_on: nacos, mysql
│
└── poetry-frontend    :3000   ← nginx, depends_on: gateway
                                  /api/* → proxy_pass gateway:8080

共享卷:  ./uploads  (挂载至 auth/user/poetry/admin 服务)
日志卷:  ./logs/<service>/
```

## 九、技术栈总览

| 层次 | 技术 | 版本 |
|------|------|------|
| 前端框架 | React | 18.2.0 |
| UI 组件库 | Ant Design | 5.15.0 |
| 状态管理 | Zustand (persist) | 4.5.2 |
| 动画 | Framer Motion | 11.0.8 |
| 路由 | React Router | 6.22.0 |
| HTTP 客户端 | Axios | — |
| 后端框架 | Spring Boot | 3.1.9 |
| 微服务 | Spring Cloud | 2022.0.4 |
| 服务发现/配置 | Spring Cloud Alibaba / Nacos | 2022.0.0.0 / 2.3.2 |
| 网关 | Spring Cloud Gateway | — |
| ORM | MyBatis (mybatis-spring-boot-starter) | 3.0.3 |
| 认证 | jjwt (HS256) | 0.12.5 |
| 数据库 | MySQL | 8.0 |
| 容器编排 | Docker Compose | 2.20+ |
| 前端服务 | Nginx | alpine |
| 构建工具 | Maven (多模块) | 3.8+ |
| Java | OpenJDK | 17 |
