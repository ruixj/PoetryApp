import React, { useState } from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { Drawer, Avatar, Button, Divider, message } from 'antd';
import { UserOutlined, CloseOutlined, LogoutOutlined } from '@ant-design/icons';
import { useAuthStore } from '../store/authStore';
import { authApi } from '../api';

const NAV_ITEMS = [
  { key: 'learning', label: '学习', icon: '📚', path: '/learning' },
  { key: 'game',     label: '游戏', icon: '🎮', path: '/game' },
  { key: 'shop',     label: '商城', icon: '🏪', path: '/shop' },
  { key: 'profile',  label: '我的', icon: '👤', path: '/profile' },
];

export default function AppLayout() {
  const navigate    = useNavigate();
  const location    = useLocation();
  const user        = useAuthStore((s) => s.user);
  const logoutStore = useAuthStore((s) => s.logout);
  const [drawerOpen, setDrawerOpen]   = useState(false);
  const [loggingOut, setLoggingOut]   = useState(false);

  const activeKey = NAV_ITEMS.find((n) => location.pathname.startsWith(n.path))?.key;

  const handleLogout = async () => {
    setLoggingOut(true);
    try { await authApi.logout(); } catch { /* ignore */ }
    logoutStore();
    message.success('已退出，下次再来哦！');
    navigate('/login', { replace: true });
    setLoggingOut(false);
  };

  const MENU_LINKS = [
    { icon: '📚', label: '学习',     path: '/learning' },
    { icon: '🎮', label: '游戏',     path: '/game' },
    { icon: '🏪', label: '积分商城', path: '/shop' },
    { icon: '👤', label: '个人中心', path: '/profile' },
    ...(user?.role === 'ADMIN' ? [{ icon: '⚙️', label: '系统管理', path: '/admin' }] : []),
  ];

  return (
    <div style={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
      {/* 顶部标题栏 */}
      <header className="scroll-header">
        <h1>诗韵童学</h1>
        <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
          <span className="yuanbao-badge">🪙 {user?.yuanbaoPoints ?? 0} 元宝</span>
          <Avatar
            src={user?.avatarUrl}
            icon={<UserOutlined />}
            style={{ cursor: 'pointer', border: '2px solid #C9A84C' }}
            onClick={() => setDrawerOpen(true)}
          />
        </div>
      </header>

      {/* 主内容区 */}
      <main style={{ flex: 1, paddingBottom: 72, overflowY: 'auto' }}>
        <Outlet />
      </main>

      {/* 底部导航 */}
      <nav className="bottom-nav">
        {NAV_ITEMS.map((item) => (
          <div
            key={item.key}
            className={`bottom-nav-item ${activeKey === item.key ? 'active' : ''}`}
            onClick={() => navigate(item.path)}
          >
            <span className="nav-icon">{item.icon}</span>
            <span>{item.label}</span>
          </div>
        ))}
      </nav>

      {/* 用户信息侧边栏 */}
      <Drawer
        title={null}
        placement="right"
        open={drawerOpen}
        onClose={() => setDrawerOpen(false)}
        width={280}
        styles={{ body: { padding: 0 } }}
        closeIcon={null}
      >
        {/* 用户信息头部 */}
        <div style={{
          background: 'linear-gradient(135deg, #8B1A1A, #C9512C)',
          padding: '24px 20px',
          color: '#F5EDD6',
          position: 'relative',
        }}>
          <Button
            type="text"
            icon={<CloseOutlined />}
            style={{ color: '#F5EDD6', position: 'absolute', top: 12, right: 12 }}
            onClick={() => setDrawerOpen(false)}
          />
          <Avatar
            size={64}
            src={user?.avatarUrl}
            icon={<UserOutlined />}
            style={{ border: '3px solid #C9A84C', marginBottom: 12 }}
          />
          <div style={{ fontSize: 18, fontWeight: 'bold', letterSpacing: 2 }}>
            {user?.nickname}
          </div>
          <div style={{ opacity: 0.8, fontSize: 12, marginTop: 4 }}>
            🪙 {user?.yuanbaoPoints ?? 0} 元宝
          </div>
          <div style={{ opacity: 0.8, fontSize: 12 }}>
            ⏰ 学习 {Math.round((user?.totalStudyMinutes || 0) / 60)} 小时
          </div>
        </div>

        {/* 导航链接 */}
        <div style={{ padding: '12px 0' }}>
          {MENU_LINKS
            .filter((item) => !location.pathname.startsWith(item.path))
            .map((item) => (
              <div
                key={item.path}
                style={{
                  display: 'flex', alignItems: 'center', gap: 12,
                  padding: '14px 20px', cursor: 'pointer',
                  borderBottom: '1px solid rgba(201,168,76,0.15)',
                  fontFamily: "'ZCOOL XiaoWei', serif", fontSize: 16,
                }}
                onClick={() => { navigate(item.path); setDrawerOpen(false); }}
              >
                <span style={{ fontSize: 22 }}>{item.icon}</span>
                <span>{item.label}</span>
              </div>
            ))}

          <Divider style={{ margin: '8px 0' }} />

          <div
            style={{
              display: 'flex', alignItems: 'center', gap: 12,
              padding: '14px 20px', cursor: 'pointer',
              color: '#8B1A1A',
              fontFamily: "'ZCOOL XiaoWei', serif", fontSize: 16,
            }}
            onClick={handleLogout}
          >
            <LogoutOutlined style={{ fontSize: 20 }} />
            <span>{loggingOut ? '退出中...' : '退出系统'}</span>
          </div>
        </div>
      </Drawer>
    </div>
  );
}
