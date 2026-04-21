import React, { useState, useEffect, useRef } from 'react';
import { List, Button, Tag, Modal, Progress, Spin, message, Typography } from 'antd';
import { SoundOutlined } from '@ant-design/icons';
import { motion, AnimatePresence } from 'framer-motion';
import { poetryApi } from '../api';
import { useAuthStore } from '../store/authStore';

const STAGES = [
  { key: 'LISTEN',     label: '听古诗', icon: '👂', color: '#1890ff' },
  { key: 'READ',       label: '读古诗', icon: '📖', color: '#52c41a' },
  { key: 'UNDERSTAND', label: '了解',   icon: '🎬', color: '#722ed1' },
  { key: 'ANALYZE',    label: '分析',   icon: '🗺️', color: '#fa8c16' },
  { key: 'MEMORIZE',   label: '背古诗', icon: '🎤', color: '#eb2f96' },
];

const LEVELS        = ['童生', '秀才', '举人', '贡士', '进士'];
const LEVEL_THR     = [0, 10, 30, 50, 70];

function getLevelIdx(count) {
  return LEVEL_THR.filter((t) => t <= count).length - 1;
}

/* ── 晋级诏书 ──────────────────────────────────────────── */
function ImperialDecree({ visible, level, count, onClose }) {
  const texts = {
    秀才: `朕观汝已习诗 ${count} 首，文采初显，特赐秀才称号，望勤勉向学！`,
    举人: `朕闻汝博览诗书，已通 ${count} 首，才学见长，特封举人之位！`,
    贡士: `朕览汝诗学心得，${count} 首皆通，堪称贡士，期望再接再厉！`,
    进士: `朕大悦！汝已习古诗 ${count} 首，高中进士，诗才卓绝！赐金匾以彰！`,
  };
  return (
    <Modal open={visible} footer={null} closable={false} centered width={320}
      styles={{ content: { background: 'transparent', padding: 0, boxShadow: 'none' } }}>
      <AnimatePresence>
        {visible && (
          <motion.div
            className="imperial-decree"
            initial={{ scale: 0.5, opacity: 0, rotate: -5 }}
            animate={{ scale: 1, opacity: 1, rotate: 0 }}
            exit={{ scale: 0.5, opacity: 0 }}
            transition={{ type: 'spring', bounce: 0.4 }}
          >
            <div style={{ fontSize: 40, marginBottom: 8 }}>📜</div>
            <div className="decree-title">皇帝诏书</div>
            <div className="decree-body">{texts[level] || `恭喜晋升${level}！`}</div>
            <div style={{ marginTop: 16, fontSize: 20 }}>🎊 晋升 {level} 🎊</div>
            <Button
              type="primary"
              style={{ marginTop: 16, background: '#8B1A1A', border: 'none' }}
              onClick={onClose}
            >
              领旨谢恩
            </Button>
          </motion.div>
        )}
      </AnimatePresence>
    </Modal>
  );
}

/* ── 五阶段学习弹窗 ─────────────────────────────────────── */
function LearningModal({ poem, visible, onClose, onComplete }) {
  const [stageIdx,  setStageIdx]  = useState(0);
  const [recording, setRecording] = useState(false);
  const mediaRecorder = useRef(null);
  const chunks        = useRef([]);
  const audioRef      = useRef(null);

  const stageKey = STAGES[stageIdx]?.key;

  const startRecord = async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      chunks.current = [];
      mediaRecorder.current = new MediaRecorder(stream);
      mediaRecorder.current.ondataavailable = (e) => chunks.current.push(e.data);
      mediaRecorder.current.onstop = async () => {
        const blob = new Blob(chunks.current, { type: 'audio/webm' });
        const form = new FormData();
        form.append('file', blob, 'recording.webm');
        try { await poetryApi.uploadRecording(poem.id, form); } catch { /* best-effort */ }
        stream.getTracks().forEach((t) => t.stop());
      };
      mediaRecorder.current.start();
      setRecording(true);
    } catch { message.error('无法访问麦克风，请检查权限'); }
  };

  const stopRecord = () => {
    if (mediaRecorder.current?.state === 'recording') mediaRecorder.current.stop();
    setRecording(false);
  };

  const handleNext = async () => {
    const nextIdx = stageIdx + 1;
    if (nextIdx < STAGES.length) {
      setStageIdx(nextIdx);
      await poetryApi.updateStage(poem.id, STAGES[nextIdx].key).catch(() => {});
    } else {
      const res = await poetryApi.updateStage(poem.id, 'COMPLETED').catch(() => ({}));
      onComplete && onComplete(res?.data);
    }
  };

  if (!poem) return null;

  return (
    <Modal
      open={visible}
      title={null}
      footer={null}
      onCancel={onClose}
      width="95%"
      style={{ maxWidth: 500, top: 20 }}
      styles={{ content: { background: '#FDFAF2', border: '2px solid rgba(201,168,76,0.4)', borderRadius: 16 } }}
    >
      <div style={{ textAlign: 'center', padding: '8px 0' }}>
        {/* 标题 */}
        <div style={{ fontFamily: "'ZCOOL XiaoWei', serif", fontSize: 20, color: '#8B1A1A', letterSpacing: 4 }}>
          {poem.title}
        </div>
        <div style={{ color: '#888', fontSize: 13, marginBottom: 12 }}>
          {poem.dynasty} · {poem.author}
        </div>

        {/* 阶段指示器 */}
        <div style={{ display: 'flex', justifyContent: 'center', gap: 6, marginBottom: 16, flexWrap: 'wrap' }}>
          {STAGES.map((s, i) => (
            <Tag
              key={s.key}
              color={i === stageIdx ? s.color : i < stageIdx ? 'success' : 'default'}
              style={{ fontSize: 11 }}
            >
              {i < stageIdx ? '✓' : s.icon} {s.label}
            </Tag>
          ))}
        </div>

        {/* 诗文 */}
        <div className="poem-text" style={{ marginBottom: 16, fontSize: 20 }}>
          {poem.content}
        </div>

        {/* 各阶段内容 */}
        {stageKey === 'LISTEN' && (
          <div>
            <audio ref={audioRef} src={poem.audioUrl} />
            <Button
              icon={<SoundOutlined />}
              size="large"
              onClick={() => audioRef.current?.play()}
              style={{ background: '#1890ff', color: 'white', border: 'none', marginBottom: 8 }}
            >
              播放朗读音频
            </Button>
            {!poem.audioUrl && <div style={{ color: '#aaa', fontSize: 12 }}>（音频待管理员上传）</div>}
          </div>
        )}

        {(stageKey === 'READ' || stageKey === 'MEMORIZE') && (
          <div>
            <div style={{ marginBottom: 10, color: stageKey === 'READ' ? '#52c41a' : '#eb2f96', fontSize: 13 }}>
              {stageKey === 'READ' ? '跟着读一读，按住话筒开始录音' : '根据提示背诵，按住话筒录制'}
            </div>
            <div style={{ display: 'flex', justifyContent: 'center' }}>
              <button
                className={`mic-button ${recording ? 'recording' : ''}`}
                onMouseDown={startRecord}
                onMouseUp={stopRecord}
                onTouchStart={startRecord}
                onTouchEnd={stopRecord}
              >
                {recording ? '🔴' : '🎙️'}
              </button>
            </div>
            {recording && (
              <div style={{ color: '#eb2f96', marginTop: 8, animation: 'blink 1s infinite', fontSize: 13 }}>
                ● 录音中...
              </div>
            )}
          </div>
        )}

        {stageKey === 'UNDERSTAND' && (
          <div style={{ background: '#f6f0ff', borderRadius: 8, padding: 14, textAlign: 'left', marginBottom: 8 }}>
            <div style={{ fontWeight: 'bold', color: '#722ed1', marginBottom: 6 }}>✍️ 诗人介绍</div>
            <div style={{ fontSize: 13, lineHeight: 1.8 }}>{poem.authorIntro || '暂无介绍'}</div>
            <div style={{ fontWeight: 'bold', color: '#722ed1', margin: '10px 0 6px' }}>📜 写作背景</div>
            <div style={{ fontSize: 13, lineHeight: 1.8 }}>{poem.background || '暂无背景介绍'}</div>
            {poem.animationUrl && (
              <video src={poem.animationUrl} controls
                style={{ width: '100%', borderRadius: 8, marginTop: 10, maxHeight: 180 }} />
            )}
          </div>
        )}

        {stageKey === 'ANALYZE' && (
          <div style={{ background: '#fff7e6', borderRadius: 8, padding: 14, textAlign: 'left', marginBottom: 8 }}>
            <div style={{ fontWeight: 'bold', color: '#fa8c16', marginBottom: 6 }}>🗺️ 诗意理解</div>
            <div style={{ fontSize: 13, lineHeight: 1.8 }}>{poem.translation || '暂无译文'}</div>
          </div>
        )}

        <Button
          type="primary"
          block
          size="large"
          style={{ marginTop: 14, background: '#8B1A1A', border: 'none', height: 46, letterSpacing: 3 }}
          onClick={handleNext}
        >
          {stageIdx < STAGES.length - 1
            ? `下一步：${STAGES[stageIdx + 1]?.label}`
            : '完成学习 🎉'}
        </Button>
      </div>
    </Modal>
  );
}

/* ── LearningPage ───────────────────────────────────────── */
export default function LearningPage() {
  const user        = useAuthStore((s) => s.user);
  const updateUser  = useAuthStore((s) => s.updateUser);

  const [textbooks,  setTextbooks]  = useState([]);
  const [grades,     setGrades]     = useState([]);
  const [units,      setUnits]      = useState([]);
  const [poems,      setPoems]      = useState([]);
  const [selTextbook, setSelTextbook] = useState(null);
  const [selGrade,   setSelGrade]   = useState(null);
  const [selUnit,    setSelUnit]    = useState(null);
  const [activePoem, setActivePoem] = useState(null);
  const [modalOpen,  setModalOpen]  = useState(false);
  const [completed,  setCompleted]  = useState([]);
  const [loading,    setLoading]    = useState(false);
  const [decree,     setDecree]     = useState({ visible: false, level: '', count: 0 });

  useEffect(() => {
    poetryApi.listTextbooks().then((r) => setTextbooks(r.data || []));
    poetryApi.getCompleted().then((r) => setCompleted(r.data || []));
  }, []);

  const onSelectTextbook = async (t) => {
    setSelTextbook(t); setGrades([]); setSelGrade(null); setUnits([]); setPoems([]);
    const r = await poetryApi.listGrades(t.id);
    setGrades(r.data || []);
  };

  const onSelectGrade = async (g) => {
    setSelGrade(g); setUnits([]); setPoems([]);
    const r = await poetryApi.listUnits(g.id);
    setUnits(r.data || []);
  };

  const onSelectUnit = async (u) => {
    setSelUnit(u); setLoading(true);
    const r = await poetryApi.getPoemsByUnit(u.id);
    setPoems(r.data || []);
    setLoading(false);
  };

  const onOpenPoem = (poem) => { setActivePoem(poem); setModalOpen(true); };

  const onStageComplete = (data) => {
    setModalOpen(false);
    if (!data?.justCompleted) return;
    updateUser({ yuanbaoPoints: (user?.yuanbaoPoints || 0) + 1 });
    const count     = data.totalCompleted;
    const prevIdx   = getLevelIdx(count - 1);
    const newIdx    = getLevelIdx(count);
    if (newIdx > prevIdx) {
      setTimeout(() => setDecree({ visible: true, level: LEVELS[newIdx], count }), 600);
    }
    message.success('🎉 完成学习！+1元宝');
    poetryApi.getCompleted().then((r) => setCompleted(r.data || []));
  };

  const completedCount = completed.length;
  const lvlIdx         = getLevelIdx(completedCount);
  const nextThr        = LEVEL_THR[lvlIdx + 1] ?? LEVEL_THR[lvlIdx];
  const pct = lvlIdx < LEVELS.length - 1
    ? Math.min(((completedCount - LEVEL_THR[lvlIdx]) / (nextThr - LEVEL_THR[lvlIdx])) * 100, 100)
    : 100;

  return (
    <div style={{ padding: '12px', paddingBottom: 80 }}>
      {/* 进度区 */}
      <div className="progress-section">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 6 }}>
          <span className="rank-badge">🏛️ {LEVELS[lvlIdx]}</span>
          <span style={{ fontSize: 14, color: '#888' }}>
            已背 <strong>{completedCount}</strong> 首
          </span>
        </div>
        <Progress
          percent={pct}
          strokeColor={{ '0%': '#8B1A1A', '100%': '#C9A84C' }}
          trailColor="rgba(139,26,26,0.1)"
          showInfo={false}
          strokeWidth={8}
        />
        <div style={{ fontSize: 12, color: '#888', marginTop: 4 }}>
          {lvlIdx < LEVELS.length - 1
            ? `再背 ${nextThr - completedCount} 首晋升 ${LEVELS[lvlIdx + 1]}`
            : '🎊 已达最高等级！'}
        </div>
      </div>

      {/* 教材 Tab */}
      <div style={{ display: 'flex', gap: 8, overflowX: 'auto', marginBottom: 10, paddingBottom: 4 }}>
        {textbooks.map((t) => (
          <Tag
            key={t.id}
            color={selTextbook?.id === t.id ? '#8B1A1A' : 'default'}
            style={{ cursor: 'pointer', padding: '6px 12px', fontSize: 13, flexShrink: 0, borderRadius: 8 }}
            onClick={() => onSelectTextbook(t)}
          >
            {t.name}
          </Tag>
        ))}
      </div>

      {grades.length > 0 && (
        <div style={{ display: 'flex', gap: 8, overflowX: 'auto', marginBottom: 10, paddingBottom: 4 }}>
          {grades.map((g) => (
            <Tag
              key={g.id}
              color={selGrade?.id === g.id ? '#C9512C' : 'default'}
              style={{ cursor: 'pointer', padding: '6px 12px', fontSize: 13, flexShrink: 0, borderRadius: 8 }}
              onClick={() => onSelectGrade(g)}
            >
              {g.name}
            </Tag>
          ))}
        </div>
      )}

      {units.length > 0 && (
        <div style={{ display: 'flex', gap: 8, overflowX: 'auto', marginBottom: 12, paddingBottom: 4 }}>
          {units.map((u) => (
            <Tag
              key={u.id}
              color={selUnit?.id === u.id ? '#4A7B5A' : 'default'}
              style={{ cursor: 'pointer', padding: '6px 12px', fontSize: 13, flexShrink: 0, borderRadius: 8 }}
              onClick={() => onSelectUnit(u)}
            >
              {u.name}
            </Tag>
          ))}
        </div>
      )}

      {/* 诗词列表 */}
      {loading ? (
        <div style={{ textAlign: 'center', padding: 40 }}><Spin size="large" /></div>
      ) : (
        <List
          dataSource={poems}
          locale={{ emptyText: selUnit ? '暂无古诗' : '👈 请先选择教材、年级和单元' }}
          renderItem={(poem, idx) => {
            const done = completed.some((p) => p.poemId === poem.id);
            return (
              <motion.div
                key={poem.id}
                initial={{ opacity: 0, x: -20 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ delay: idx * 0.04 }}
              >
                <div
                  className="classical-card"
                  style={{ padding: 16, marginBottom: 10, cursor: 'pointer' }}
                  onClick={() => onOpenPoem(poem)}
                >
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                    <div>
                      <div style={{ fontFamily: "'ZCOOL XiaoWei', serif", fontSize: 18, color: '#8B1A1A', letterSpacing: 3 }}>
                        {poem.title}
                      </div>
                      <div style={{ fontSize: 13, color: '#888', marginTop: 4 }}>
                        {poem.dynasty} · {poem.author}
                      </div>
                      <div style={{ fontSize: 13, color: '#666', marginTop: 6, letterSpacing: 1 }}>
                        {poem.content?.substring(0, 16)}…
                      </div>
                    </div>
                    <Tag color={done ? 'success' : 'gold'} style={{ flexShrink: 0, marginLeft: 8 }}>
                      {done ? '✓ 已背' : '开始学'}
                    </Tag>
                  </div>
                </div>
              </motion.div>
            );
          }}
        />
      )}

      <LearningModal
        poem={activePoem}
        visible={modalOpen}
        onClose={() => setModalOpen(false)}
        onComplete={onStageComplete}
      />

      <ImperialDecree
        visible={decree.visible}
        level={decree.level}
        count={decree.count}
        onClose={() => setDecree((d) => ({ ...d, visible: false }))}
      />
    </div>
  );
}
