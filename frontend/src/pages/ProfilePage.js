import React, { useState, useEffect } from 'react';
import { Avatar, Button, List, Tag, Modal, Input, Upload, message, Spin } from 'antd';
import { EditOutlined, SoundOutlined, UploadOutlined } from '@ant-design/icons';
import { motion } from 'framer-motion';
import { userApi, poetryApi } from '../api';
import { useAuthStore } from '../store/authStore';

const LEVELS    = ['童生', '秀才', '举人', '贡士', '进士'];
const LEVEL_THR = [0, 10, 30, 50, 70];

function getLevelIdx(count) {
  return LEVEL_THR.filter((t) => t <= count).length - 1;
}

export default function ProfilePage() {
  const user       = useAuthStore((s) => s.user);
  const updateUser = useAuthStore((s) => s.updateUser);

  const [completed,      setCompleted]      = useState([]);
  const [loading,        setLoading]        = useState(true);
  const [nicknameModal,  setNicknameModal]  = useState(false);
  const [nickname,       setNickname]       = useState(user?.nickname || '');
  const [savingNick,     setSavingNick]     = useState(false);
  const [audioModal,     setAudioModal]     = useState(null); // poem object
  const [uploading,      setUploading]      = useState(false);

  useEffect(() => {
    poetryApi.getCompleted()
      .then((r) => setCompleted(r.data || []))
      .finally(() => setLoading(false));
  }, []);

  const lvlIdx      = getLevelIdx(completed.length);
  const hours       = Math.floor((user?.totalStudyMinutes || 0) / 60);
  const mins        = (user?.totalStudyMinutes || 0) % 60;

  const handleSaveNickname = async () => {
    if (!nickname.trim()) return message.warning('昵称不能为空');
    setSavingNick(true);
    try {
      await userApi.updateProfile({ nickname: nickname.trim() });
      updateUser({ nickname: nickname.trim() });
      message.success('昵称已更新');
      setNicknameModal(false);
    } catch { message.error('修改失败'); }
    finally { setSavingNick(false); }
  };

  const handleAvatarUpload = async ({ file }) => {
    setUploading(true);
    const form = new FormData();
    form.append('file', file);
    try {
      const r = await userApi.uploadAvatar(form);
      updateUser({ avatarUrl: r.data?.avatarUrl });
      message.success('头像已更新');
    } catch { message.error('上传失败'); }
    finally { setUploading(false); }
    return false;
  };

  return (
    <div style={{ paddingBottom: 80 }}>
      {/* 个人信息头部 */}
      <div style={{
        background: 'linear-gradient(135deg, #2C1810 0%, #8B1A1A 60%, #C9512C 100%)',
        padding: '24px 20px 30px',
        color: 'white',
      }}>
        <div style={{ display: 'flex', gap: 16, alignItems: 'center' }}>
          <Upload showUploadList={false} beforeUpload={(f) => { handleAvatarUpload({ file: f }); return false; }}>
            <Avatar
              size={72}
              src={user?.avatarUrl}
              style={{ border: '3px solid #C9A84C', cursor: 'pointer', fontSize: 30 }}
            >
              {!user?.avatarUrl && user?.nickname?.charAt(0)}
            </Avatar>
          </Upload>
          <div style={{ flex: 1 }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
              <span style={{ fontFamily: "'ZCOOL XiaoWei', serif", fontSize: 20, letterSpacing: 2 }}>
                {user?.nickname || '用户'}
              </span>
              <EditOutlined
                style={{ cursor: 'pointer', color: 'rgba(255,255,255,0.7)' }}
                onClick={() => { setNickname(user?.nickname || ''); setNicknameModal(true); }}
              />
            </div>
            <Tag color="gold" style={{ marginTop: 6, fontFamily: "'ZCOOL XiaoWei', serif", letterSpacing: 2 }}>
              🏛️ {LEVELS[lvlIdx]}
            </Tag>
            {user?.phone && (
              <div style={{ fontSize: 12, color: 'rgba(255,255,255,0.6)', marginTop: 4 }}>{user.phone}</div>
            )}
          </div>
          {uploading && <Spin />}
        </div>

        {/* 数据统计 */}
        <div style={{
          display: 'flex', marginTop: 20, borderTop: '1px solid rgba(255,255,255,0.2)',
          paddingTop: 16, gap: 0,
        }}>
          {[
            { label: '元宝', value: `🪙 ${user?.yuanbaoPoints || 0}` },
            { label: '已背诗数', value: `📜 ${completed.length}` },
            { label: '学习时长', value: `⏱ ${hours}h${mins}m` },
          ].map((stat) => (
            <div key={stat.label} style={{ flex: 1, textAlign: 'center', borderRight: '1px solid rgba(255,255,255,0.15)' }}>
              <div style={{ fontSize: 18, fontWeight: 'bold' }}>{stat.value}</div>
              <div style={{ fontSize: 12, color: 'rgba(255,255,255,0.7)', marginTop: 2 }}>{stat.label}</div>
            </div>
          ))}
        </div>
      </div>

      {/* 已背古诗 */}
      <div style={{ padding: '16px 12px' }}>
        <div style={{
          fontFamily: "'ZCOOL XiaoWei', serif", fontSize: 18, color: '#8B1A1A',
          marginBottom: 12, letterSpacing: 2,
        }}>
          📚 已背古诗（{completed.length} 首）
        </div>

        {loading ? (
          <div style={{ textAlign: 'center', padding: 40 }}><Spin /></div>
        ) : completed.length === 0 ? (
          <div style={{ textAlign: 'center', padding: 40, color: '#888' }}>
            还没有背过古诗，快去学习吧！
          </div>
        ) : (
          <List
            dataSource={completed}
            renderItem={(item, idx) => (
              <motion.div
                key={item.poemId}
                initial={{ opacity: 0, x: -16 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ delay: idx * 0.04 }}
              >
                <div className="classical-card" style={{ padding: 14, marginBottom: 10 }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                    <div>
                      <div style={{ fontFamily: "'ZCOOL XiaoWei', serif", fontSize: 16, color: '#8B1A1A', letterSpacing: 3 }}>
                        {item.poemTitle}
                      </div>
                      <div style={{ fontSize: 12, color: '#888', marginTop: 4 }}>
                        {item.dynasty} · {item.author}
                      </div>
                    </div>
                    {item.recordingUrl && (
                      <Button
                        icon={<SoundOutlined />}
                        size="small"
                        onClick={() => setAudioModal(item)}
                        style={{ color: '#8B1A1A', borderColor: '#8B1A1A', flexShrink: 0 }}
                      >
                        我的录音
                      </Button>
                    )}
                  </div>
                  {item.poemContent && (
                    <div className="poem-text" style={{ marginTop: 8, fontSize: 14 }}>
                      {item.poemContent?.substring(0, 32)}…
                    </div>
                  )}
                </div>
              </motion.div>
            )}
          />
        )}
      </div>

      {/* 修改昵称 Modal */}
      <Modal title="修改昵称" open={nicknameModal} onCancel={() => setNicknameModal(false)}
        footer={null} centered>
        <Input
          value={nickname}
          onChange={(e) => setNickname(e.target.value)}
          placeholder="请输入新昵称"
          maxLength={20}
          size="large"
          onPressEnter={handleSaveNickname}
          style={{ marginBottom: 16 }}
        />
        <Button type="primary" block size="large" loading={savingNick}
          style={{ background: '#8B1A1A', border: 'none' }}
          onClick={handleSaveNickname}>
          保存
        </Button>
      </Modal>

      {/* 录音播放 Modal */}
      <Modal title={`🎵 ${audioModal?.poemTitle} · 我的录音`}
        open={!!audioModal} onCancel={() => setAudioModal(null)} footer={null} centered>
        {audioModal?.recordingUrl && (
          <audio src={audioModal.recordingUrl} controls style={{ width: '100%' }} />
        )}
      </Modal>
    </div>
  );
}
