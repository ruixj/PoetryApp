# 诗韵童学 · 部署步骤

## 一、环境要求

| 工具 | 版本要求 | 说明 |
|------|----------|------|
| Java | 17+ | 后端编译和运行 |
| Maven | 3.8+ | 后端构建 |
| Node.js | 18+ | 前端构建 |
| Docker | 24+ | 容器运行 |
| Docker Compose | 2.20+ | 服务编排 |

验证环境：
```bash
java -version
mvn -version
node -v
docker -v
docker compose version
```

---

## 二、目录结构

```
poetryApp/
├── backend/              # Spring Cloud 后端（8 个模块）
│   ├── common/
│   ├── gateway/          → 端口 8080
│   ├── auth-service/     → 端口 8081
│   ├── user-service/     → 端口 8082
│   ├── poetry-service/   → 端口 8083
│   ├── game-service/     → 端口 8084
│   ├── shop-service/     → 端口 8085
│   └── admin-service/    → 端口 8086
├── frontend/             # React 前端 → 端口 3000
├── database/
│   └── init.sql          # 数据库初始化脚本（自动执行）
└── docker-compose.yml
```

---

## 三、一键部署（推荐）

### 第 1 步：构建后端 JAR

```bash
cd poetryApp/backend
mvn clean package -DskipTests
```

构建成功后每个服务的 `target/` 目录下会生成对应 JAR：
- `gateway/target/gateway-1.0.0.jar`
- `auth-service/target/auth-service-1.0.0.jar`
- 以此类推...

### 第 2 步：启动所有服务

```bash
cd poetryApp
docker compose up -d --build
```

启动顺序由 `depends_on` 自动控制：
```
MySQL → Nacos → 各微服务 → 前端
```

### 第 3 步：检查服务状态

```bash
docker compose ps
```

所有服务应为 `Up` 状态。若有服务未启动，查看日志：
```bash
docker compose logs <服务名>
# 例：
docker compose logs gateway
docker compose logs auth-service
```

### 第 4 步：访问应用

| 地址 | 说明 |
|------|------|
| http://localhost:3000 | 前端应用（学生入口） |
| http://localhost:8080 | API 网关 |
| http://localhost:8848/nacos | Nacos 控制台（账号 nacos/nacos） |

---

## 四、首次使用配置

### 4.1 管理员账号

系统启动后 auth-service 会自动将管理员密码初始化为 `Admin@123`。

登录入口：前端应用 → 密码登录 Tab → 手机号 `13800000000`，密码 `Admin@123`

### 4.2 在管理后台添加内容

登录管理员账号后进入「系统管理」页面：

1. **内容管理** → 新增古诗（填写标题、作者、朝代、诗文、译文，可上传朗读音频和动画视频）
2. **商城管理** → 新增商品（填写商品名、元宝价格、库存，上传图片后点击上架）

> 数据库 `init.sql` 已预置 5 首示例古诗（静夜思、春晓等）供测试。

---

## 五、分步手动部署（不使用 Docker）

如需在已有服务器上直接部署，可跳过 Docker。

### 5.1 启动 MySQL

确保 MySQL 8.0 运行在 3306 端口，执行初始化脚本：

```bash
mysql -u root -p < database/init.sql
```

### 5.2 启动 Nacos

```bash
# 下载 Nacos 2.3.2
wget https://github.com/alibaba/nacos/releases/download/2.3.2/nacos-server-2.3.2.tar.gz
tar -xzf nacos-server-2.3.2.tar.gz
cd nacos/bin
./startup.sh -m standalone
```

### 5.3 构建并启动后端服务

```bash
cd poetryApp/backend
mvn clean package -DskipTests

# 依次启动（或使用 supervisor / systemd 管理）
java -jar gateway/target/gateway-1.0.0.jar \
     --spring.cloud.nacos.server-addr=127.0.0.1:8848 &

java -jar auth-service/target/auth-service-1.0.0.jar \
     --spring.cloud.nacos.server-addr=127.0.0.1:8848 \
     --spring.datasource.url=jdbc:mysql://127.0.0.1:3306/poetry_app \
     --spring.datasource.password=root@123 &

# 其余服务同上，替换 JAR 名称和端口即可
```

### 5.4 构建并部署前端

```bash
cd poetryApp/frontend
npm install --legacy-peer-deps
npm run build
# build/ 目录即为静态产物，用 nginx 或其他静态服务器托管
```

Nginx 配置参考 `frontend/nginx.conf`，注意将 `proxy_pass` 改为实际网关地址。

---

## 六、环境变量说明

各服务支持以下环境变量覆盖默认配置：

| 环境变量 | 默认值 | 说明 |
|----------|--------|------|
| `NACOS_SERVER_ADDR` | `nacos:8848` | Nacos 地址 |
| `MYSQL_HOST` | `mysql` | MySQL 主机名 |
| `MYSQL_PASSWORD` | `root@123` | MySQL root 密码 |
| `JWT_SECRET` | 内置 Base64 密钥 | JWT 签名密钥，生产环境务必修改 |
| `SMS_ENABLED` | `false` | 是否启用阿里云真实短信（`true`/`false`） |
| `SMS_ACCESS_KEY` | — | 阿里云 AccessKey ID |
| `SMS_SECRET_KEY` | — | 阿里云 AccessKey Secret |
| `SMS_SIGN_NAME` | — | 短信签名名称 |
| `SMS_TEMPLATE_CODE` | — | 短信模板 Code |

在 `docker-compose.yml` 中对应服务的 `environment` 节添加即可：
```yaml
auth-service:
  environment:
    JWT_SECRET: 你的生产密钥Base64
    SMS_ENABLED: "true"
    SMS_ACCESS_KEY: 你的AK
    SMS_SECRET_KEY: 你的SK
    SMS_SIGN_NAME: 诗韵童学
    SMS_TEMPLATE_CODE: SMS_XXXXXXX
```

---

## 七、停止与清理

```bash
# 停止所有服务（保留数据）
docker compose down

# 停止并清除所有数据（包括数据库卷）
docker compose down -v

# 查看实时日志
docker compose logs -f

# 重启单个服务
docker compose restart auth-service
```

---

## 八、常见问题

### Q: 后端服务启动失败，报 Nacos 连接错误
**A:** Nacos 启动较慢，等待约 30 秒后再重试：
```bash
docker compose restart auth-service user-service poetry-service game-service shop-service admin-service
```

### Q: 前端页面空白 / API 请求 404
**A:** 检查 nginx.conf 中的 `proxy_pass` 是否与实际网关地址一致。Docker 环境中应为 `http://gateway:8080`，本机开发应为 `http://localhost:8080`。

### Q: 短信验证码不发送
**A:** 默认 `SMS_ENABLED=false`，验证码会打印到 auth-service 日志中：
```bash
docker compose logs auth-service | grep "验证码"
```

### Q: Maven 构建失败，找不到 common 模块
**A:** 必须在 `backend/` 目录下执行 `mvn package`，不能进入子模块单独构建：
```bash
cd poetryApp/backend
mvn clean package -DskipTests
```

### Q: 端口被占用
**A:** 修改 `docker-compose.yml` 中对应服务的 `ports` 映射，例如将 `"3000:80"` 改为 `"8090:80"`。
