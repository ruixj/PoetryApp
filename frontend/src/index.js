import React from 'react';
import ReactDOM from 'react-dom/client';
import { ConfigProvider } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import App from './App';
import './styles/global.css';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <ConfigProvider
    locale={zhCN}
    theme={{
      token: {
        colorPrimary: '#8B1A1A',
        colorLink: '#8B1A1A',
        borderRadius: 8,
        fontFamily: "'ZCOOL XiaoWei', 'Noto Serif SC', 'SimSun', serif",
      },
    }}
  >
    <App />
  </ConfigProvider>
);
