import React, { useState, useEffect, useRef } from 'react';
import { Tabs, Form, Input, Button, message } from 'antd';
import { MobileOutlined, LockOutlined, SafetyOutlined } from '@ant-design/icons';
import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { authApi } from '../api';
import { useAuthStore } from '../store/authStore';
import '../styles/login.css';

const DECO_CHARS = ['诗', '词', '赋', '韵', '雅', '墨', '竹', '梅', '兰', '菊', '春', '秋'];

function DecoChar({ char, style }) {
  return (
    <div
      className="login-deco"
      style={{ fontSize: `${Math.random() * 40 + 20}px`, ...style }}
    >
      {char}
    </div>
  );
}

export default function LoginPage() {
  const navigate  = useNavigate();
  const setAuth   = useAuthStore((s) => s.setAuth);
  const [smsForm] = Form.useForm();
  const [pwdForm] = Form.useForm();
  const [regForm] = Form.useForm();
  const [loading, setLoading]     = useState(false);
  const [countdown, setCountdown] = useState(0);
  const timerRef = useRef(null);

  // 倒计时
  useEffect(() => {
    return () => clearInterval(timerRef.current);
  }, []);

  const startCountdown = () => {
    setCountdown(60);
    timerRef.current = setInterval(() => {
      setCountdown((c) => {
        if (c <= 1) { clearInterval(timerRef.current); return 0; }
        return c - 1;
      });
    }, 1000);
  };

  const handleSendSms = async (phone) => {
    if (!phone || !/^1\d{10}$/.test(phone)) {
      return message.warning('请输入正确的手机号');
    }
    try {
      await authApi.sendSms(phone);
      message.success('验证码已发送（开发模式：查看服务日志）');
      startCountdown();
    } catch (e) {
      message.error(e.message || '发送失败');
    }
  };

  const afterLogin = (data) => {
    setAuth(data.token, data.user);
    if (data.user?.isFirstLogin) {
      navigate('/profile-setup', { replace: true });
    } else {
      navigate('/learning', { replace: true });
    }
  };

  const handleSmsLogin = async (values) => {
    setLoading(true);
    try {
      const res = await authApi.loginSms({ phone: values.phone, code: values.code });
      afterLogin(res.data);
    } catch (e) {
      message.error(e.message || '登录失败');
    } finally { setLoading(false); }
  };

  const handlePwdLogin = async (values) => {
    setLoading(true);
    try {
      const res = await authApi.loginPassword({ phone: values.phone, password: values.password });
      afterLogin(res.data);
    } catch (e) {
      message.error(e.message || '手机号或密码错误');
    } finally { setLoading(false); }
  };

  const handleRegister = async (values) => {
    setLoading(true);
    try {
      const res = await authApi.register({
        phone: values.phone,
        code:  values.code,
        password: values.password,
        nickname: values.nickname || '',
      });
      afterLogin(res.data);
    } catch (e) {
      message.error(e.message || '注册失败');
    } finally { setLoading(false); }
  };

  const SmsCodeInput = ({ form }) => (
    <Form.Item name="code" rules={[{ required: true, message: '请输入验证码' }]}>
      <Input
        prefix={<SafetyOutlined />}
        placeholder="验证码"
        maxLength={6}
        addonAfter={
          <Button
            size="small"
            className="sms-btn"
            disabled={countdown > 0}
            onClick={() => handleSendSms(form.getFieldValue('phone'))}
          >
            {countdown > 0 ? `${countdown}s` : '发送验证码'}
          </Button>
        }
      />
    </Form.Item>
  );

  const tabItems = [
    {
      key: 'sms',
      label: '验证码登录',
      children: (
        <Form form={smsForm} onFinish={handleSmsLogin} size="large">
          <Form.Item name="phone" rules={[{ required: true, pattern: /^1\d{10}$/, message: '请输入正确手机号' }]}>
            <Input prefix={<MobileOutlined />} placeholder="手机号" maxLength={11} />
          </Form.Item>
          <SmsCodeInput form={smsForm} />
          <Form.Item>
            <Button type="primary" htmlType="submit" block loading={loading} className="login-btn">
              登 录
            </Button>
          </Form.Item>
        </Form>
      ),
    },
    {
      key: 'pwd',
      label: '密码登录',
      children: (
        <Form form={pwdForm} onFinish={handlePwdLogin} size="large">
          <Form.Item name="phone" rules={[{ required: true, pattern: /^1\d{10}$/, message: '请输入正确手机号' }]}>
            <Input prefix={<MobileOutlined />} placeholder="手机号" maxLength={11} />
          </Form.Item>
          <Form.Item name="password" rules={[{ required: true, message: '请输入密码' }]}>
            <Input.Password prefix={<LockOutlined />} placeholder="密码" />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" block loading={loading} className="login-btn">
              登 录
            </Button>
          </Form.Item>
        </Form>
      ),
    },
    {
      key: 'register',
      label: '注册',
      children: (
        <Form form={regForm} onFinish={handleRegister} size="large">
          <Form.Item name="phone" rules={[{ required: true, pattern: /^1\d{10}$/, message: '请输入正确手机号' }]}>
            <Input prefix={<MobileOutlined />} placeholder="手机号" maxLength={11} />
          </Form.Item>
          <SmsCodeInput form={regForm} />
          <Form.Item name="password" rules={[{ required: true, min: 6, message: '密码至少6位' }]}>
            <Input.Password prefix={<LockOutlined />} placeholder="设置密码（至少6位）" />
          </Form.Item>
          <Form.Item name="nickname">
            <Input placeholder="昵称（选填，不填将随机生成）" maxLength={20} />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" block loading={loading} className="login-btn">
              注 册
            </Button>
          </Form.Item>
        </Form>
      ),
    },
  ];

  return (
    <div className="login-page">
      {/* 浮动汉字装饰 */}
      {DECO_CHARS.map((char, i) => (
        <DecoChar
          key={i}
          char={char}
          style={{
            left:              `${(i * 37 + 5) % 90}%`,
            animationDuration: `${8 + (i % 5) * 3}s`,
            animationDelay:    `${(i * 1.3) % 6}s`,
          }}
        />
      ))}

      <motion.div
        className="login-card"
        initial={{ opacity: 0, y: 30 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
      >
        <div className="login-logo">
          <span className="app-icon">📜</span>
          <div className="app-name">诗韵童学</div>
          <div className="app-slogan">让古诗学习变得有趣</div>
        </div>

        <Tabs defaultActiveKey="sms" centered items={tabItems} />
      </motion.div>
    </div>
  );
}
