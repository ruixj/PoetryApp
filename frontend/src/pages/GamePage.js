import React, { useState, useEffect, useRef } from 'react';
import { Button, Input, List, Avatar, Tag, Select, message, Empty, Tooltip } from 'antd';
import { SendOutlined, AudioOutlined } from '@ant-design/icons';
import { motion, AnimatePresence } from 'framer-motion';
import { gameApi, poetryApi } from '../api';
import { useAuthStore } from '../store/authStore';

const SEASON_VALUES = [
  '春分','清明','谷雨','立夏','小满','芒种',
  '夏至','大暑','立秋','白露','寒露','立冬',
  '大雪','冬至','小寒','大寒',
];

const CATEGORY_TYPES = [
  { type: 'KEYWORD', label: '飞花令', icon: '🌺', fixed: false },
  { type: 'SEASON',  label: '节气',   icon: '🌸', fixed: true  },
  { type: 'THEME',   label: '题材',   icon: '🎨', fixed: false },
  { type: 'AUTHOR',  label: '诗人',   icon: '✍️', fixed: false },
];

export default function GamePage() {
  const user       = useAuthStore((s) => s.user);
  const updateUser = useAuthStore((s) => s.updateUser);

  const [activeType,   setActiveType]   = useState('KEYWORD');
  const [catValue,     setCatValue]     = useState('');
  const [catOptions,   setCatOptions]   = useState([]);
  const [submissions,  setSubmissions]  = useState([]);
  const [inputText,    setInputText]    = useState('');
  const [sending,      setSending]      = useState(false);
  const [loadingOpts,  setLoadingOpts]  = useState(false);
  const listRef = useRef(null);

  const currentDef = CATEGORY_TYPES.find((c) => c.type === activeType);

  useEffect(() => {
    setCatValue('');
    setSubmissions([]);
    if (!currentDef?.fixed) {
      setLoadingOpts(true);
      poetryApi.getCategoryValues(activeType)
        .then((r) => setCatOptions(r.data || []))
        .finally(() => setLoadingOpts(false));
    }
  }, [activeType]);

  useEffect(() => {
    if (catValue) loadSubmissions();
  }, [catValue]);

  const loadSubmissions = async () => {
    try {
      const r = await gameApi.getSubmissions(activeType, catValue);
      setSubmissions(r.data || []);
    } catch {}
  };

  const handleSend = async () => {
    if (!inputText.trim()) return message.warning('请输入古诗名称或内容');
    if (!catValue)         return message.warning('请先选择关键词');
    setSending(true);
    try {
      await gameApi.submit({ poemInput: inputText.trim(), categoryType: activeType, categoryValue: catValue });
      setInputText('');
      updateUser({ yuanbaoPoints: (user?.yuanbaoPoints || 0) + 5 });
      message.success('🎉 提交成功！+5元宝');
      loadSubmissions();
    } catch (e) {
      message.error(e.message || '提交失败，请检查诗名是否正确');
    } finally { setSending(false); }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: 'calc(100vh - 120px)' }}>
      {/* 分类 Tab */}
      <div style={{ background: 'linear-gradient(135deg, #2C1810, #5C2B1A)', padding: '12px 16px 0' }}>
        <div style={{ display: 'flex', gap: 8, overflowX: 'auto', paddingBottom: 10 }}>
          {CATEGORY_TYPES.map((c) => (
            <div
              key={c.type}
              onClick={() => setActiveType(c.type)}
              style={{
                cursor: 'pointer', padding: '8px 16px', fontSize: 14,
                flexShrink: 0, borderRadius: 20,
                color:       activeType === c.type ? '#2C1810'                       : '#F5EDD6',
                background:  activeType === c.type ? '#C9A84C'                       : 'transparent',
                border:      activeType === c.type ? 'none' : '1px solid rgba(245,237,214,0.3)',
                fontFamily: "'ZCOOL XiaoWei', serif",
              }}
            >
              {c.icon} {c.label}
            </div>
          ))}
        </div>

        {/* 关键词选择 */}
        <div style={{ paddingBottom: 12 }}>
          {currentDef?.fixed ? (
            <div style={{ display: 'flex', gap: 6, flexWrap: 'wrap' }}>
              {SEASON_VALUES.map((v) => (
                <Tag
                  key={v}
                  onClick={() => setCatValue(v)}
                  style={{
                    cursor: 'pointer', borderRadius: 12,
                    color:      catValue === v ? 'white'        : '#F5EDD6',
                    background: catValue === v ? '#8B1A1A'      : 'transparent',
                    border:     '1px solid rgba(245,237,214,0.3)',
                  }}
                >
                  {v}
                </Tag>
              ))}
            </div>
          ) : (
            <Select
              showSearch
              style={{ width: '100%' }}
              placeholder={`选择${currentDef?.label}关键词`}
              value={catValue || undefined}
              onChange={setCatValue}
              loading={loadingOpts}
              optionFilterProp="label"
              options={catOptions.map((v) => ({ value: v, label: v }))}
            />
          )}
        </div>
      </div>

      {/* 分类标题 */}
      {catValue && (
        <div style={{
          textAlign: 'center', padding: '8px',
          background: 'rgba(201,168,76,0.1)',
          fontFamily: "'ZCOOL XiaoWei', serif",
          color: '#8B1A1A', fontSize: 17, letterSpacing: 3,
        }}>
          {activeType === 'SEASON'  && `🌸 ${catValue} · 节气诗`}
          {activeType === 'KEYWORD' && `🌺 含「${catValue}」字的古诗`}
          {activeType === 'THEME'   && `🎨 ${catValue}题材`}
          {activeType === 'AUTHOR'  && `✍️ ${catValue}的诗作`}
        </div>
      )}

      {/* 提交列表 */}
      <div style={{ flex: 1, overflowY: 'auto', padding: '8px 12px' }} ref={listRef}>
        {!catValue ? (
          <Empty description={<span style={{ color: '#888' }}>请先选择分类和关键词</span>}
            style={{ padding: 60 }} />
        ) : submissions.length === 0 ? (
          <div style={{ textAlign: 'center', padding: 40, color: '#888' }}>
            还没有人提交，你来第一个！
          </div>
        ) : (
          <AnimatePresence>
            {submissions.map((sub, idx) => (
              <motion.div
                key={sub.id}
                initial={{ opacity: 0, y: 16, scale: 0.96 }}
                animate={{ opacity: 1, y: 0, scale: 1 }}
                transition={{ delay: idx * 0.04 }}
                style={{ marginBottom: 10 }}
              >
                <div className="classical-card" style={{ padding: 12 }}>
                  <div style={{ display: 'flex', gap: 10, alignItems: 'flex-start' }}>
                    <Avatar src={sub.userAvatar} size={36}
                      style={{ flexShrink: 0, border: '2px solid #C9A84C' }}>
                      {sub.userNickname?.charAt(0)}
                    </Avatar>
                    <div style={{ flex: 1 }}>
                      <div style={{ fontSize: 12, color: '#888', marginBottom: 4 }}>{sub.userNickname}</div>
                      <div style={{ fontFamily: "'ZCOOL XiaoWei', serif", fontSize: 16, color: '#2C1810', letterSpacing: 2 }}>
                        《{sub.poemTitle}》
                      </div>
                      <div style={{ fontSize: 13, color: '#666', marginTop: 4, letterSpacing: 1, lineHeight: 1.8 }}>
                        {sub.poemContent}
                      </div>
                      <div style={{ fontSize: 12, color: '#888', marginTop: 4 }}>
                        {sub.poemDynasty} · {sub.poemAuthor}
                      </div>
                    </div>
                  </div>
                </div>
              </motion.div>
            ))}
          </AnimatePresence>
        )}
      </div>

      {/* 底部输入框 */}
      <div style={{
        padding: '10px 14px',
        background: '#FDFAF2',
        borderTop: '1px solid rgba(201,168,76,0.3)',
        display: 'flex', gap: 8, alignItems: 'center',
      }}>
        <Input
          value={inputText}
          onChange={(e) => setInputText(e.target.value)}
          placeholder="输入古诗名称或内容..."
          onPressEnter={handleSend}
          size="large"
          style={{ borderRadius: 24, fontFamily: "'ZCOOL XiaoWei', serif" }}
          suffix={
            <Tooltip title="语音输入">
              <AudioOutlined style={{ color: '#8B1A1A', cursor: 'pointer', fontSize: 18 }} />
            </Tooltip>
          }
        />
        <Button
          type="primary"
          shape="circle"
          size="large"
          icon={<SendOutlined />}
          loading={sending}
          onClick={handleSend}
          style={{ background: '#8B1A1A', border: 'none', flexShrink: 0 }}
        />
      </div>
    </div>
  );
}
