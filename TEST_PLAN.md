# Plan: 诗韵童学 全流程验证与测试

## TL;DR
项目后端（7个微服务）和前端（6个页面）代码均已完成，但从未实际构建运行过。本计划按照"构建 → 启动 → 接口验证 → 功能验收"四个阶段，逐步验证整个系统是否可正常运行。

## 阶段一：构建验证（可并行）

1. **后端编译检查** — 在 `backend/` 目录执行 `mvn clean package -DskipTests`，验证所有 8 个模块无编译错误
2. **前端依赖安装** — 在 `frontend/` 目录执行 `npm install --legacy-peer-deps`
3. **前端构建检查** — 执行 `npm run build`，验证 React 编译无错误（*依赖步骤 2*）

## 阶段二：容器启动验证

4. **Docker Compose 启动** — `docker compose up -d --build`，等待所有容器进入 healthy 状态
5. **健康检查验证** — `docker compose ps` 确认 9 个容器全部 Up
6. **Nacos 注册验证** — 访问 `http://localhost:8848/nacos`，确认 6 个微服务均已注册

## 阶段三：接口验证（按业务流顺序）

7. **注册流程** — `POST /api/auth/register`；SMS_ENABLED=false 时验证码在 auth-service 日志中
8. **登录流程** — `POST /api/auth/login/password`（13800000000 / Admin@123）
9. **用户信息** — `GET /api/user/profile`（附 Authorization 头）
10. **教材查询** — `GET /api/poetry/textbooks`，验证返回 4 个教材体系
11. **诗词查询** — `GET /api/poetry/poems/1`，验证返回《静夜思》
12. **学习进度** — `POST /api/poetry/library/unit/1` → `POST /api/poetry/progress/1/stage?stage=LISTEN`
13. **游戏提交** — `POST /api/game/submit`（poemInput="静夜思", categoryType="KEYWORD", categoryValue="月"）
14. **商城接口** — `GET /api/shop/items`
15. **管理接口** — `GET /api/admin/users`（需 admin token）

## 阶段四：前端功能验收

16. **登录页** — 访问 `http://localhost:3000`，测试 SMS / 密码两种登录
17. **首次登录引导** — 新用户跳转 /profile-setup，完成教材选择后进入学习页
18. **学习页** — 走完 听→读→了解→分析→背 5 个阶段弹窗
19. **晋级诏书** — 背完足够诗词后验证诏书弹窗触发
20. **游戏页** — 飞花令选词→输入诗名→验证提交记录
21. **商城页** — 商品展示→加购→下单流程
22. **个人页** — 头像上传、昵称修改、已背诗列表
23. **管理后台** — 测试新增诗词、商品管理、订单状态更新

## 验证命令速查

```bash
# 获取管理员 token
curl -X POST http://localhost:8080/api/auth/login/password \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800000000","password":"Admin@123"}'

# 查看验证码（SMS 关闭时）
docker compose logs auth-service | grep "验证码"

# 测试教材接口
curl http://localhost:8080/api/poetry/textbooks \
  -H "Authorization: Bearer <TOKEN>"