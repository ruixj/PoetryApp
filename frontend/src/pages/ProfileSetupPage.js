import React, { useState, useEffect } from 'react';
import { Button, Select, Checkbox, Tag, message, Steps, Spin } from 'antd';
import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { poetryApi } from '../api';
import { useAuthStore } from '../store/authStore';

export default function ProfileSetupPage() {
  const navigate    = useNavigate();
  const updateUser  = useAuthStore((s) => s.updateUser);

  const [step,            setStep]            = useState(0);
  const [textbooks,       setTextbooks]       = useState([]);
  const [grades,          setGrades]          = useState([]);
  const [units,           setUnits]           = useState([]);
  const [selectedTextbook, setSelectedTextbook] = useState(null);
  const [selectedGrade,   setSelectedGrade]   = useState(null);
  const [selectedUnitIds, setSelectedUnitIds] = useState([]);
  const [loadingGrades,   setLoadingGrades]   = useState(false);
  const [loadingUnits,    setLoadingUnits]    = useState(false);
  const [saving,          setSaving]          = useState(false);

  useEffect(() => {
    poetryApi.listTextbooks().then((r) => setTextbooks(r.data || []));
  }, []);

  const handleSelectTextbook = async (id) => {
    setSelectedTextbook(id);
    setSelectedGrade(null);
    setUnits([]);
    setSelectedUnitIds([]);
    setLoadingGrades(true);
    try {
      const r = await poetryApi.listGrades(id);
      setGrades(r.data || []);
      setStep(1);
    } finally { setLoadingGrades(false); }
  };

  const handleSelectGrade = async (id) => {
    setSelectedGrade(id);
    setUnits([]);
    setSelectedUnitIds([]);
    setLoadingUnits(true);
    try {
      const r = await poetryApi.listUnits(id);
      setUnits(r.data || []);
      setStep(2);
    } finally { setLoadingUnits(false); }
  };

  const handleStart = async () => {
    setSaving(true);
    try {
      const unitIds = selectedUnitIds.length > 0
        ? selectedUnitIds
        : units.map((u) => u.id);
      for (const uid of unitIds) {
        await poetryApi.addUnitToLibrary(uid);
      }
      updateUser({ isFirstLogin: false, textbookId: selectedTextbook, gradeId: selectedGrade });
      message.success('学习库已建立，开始你的古诗之旅！');
      navigate('/learning', { replace: true });
    } catch (e) {
      message.error(e.message || '操作失败');
    } finally { setSaving(false); }
  };

  return (
    <div style={{
      minHeight: '100vh',
      background: 'linear-gradient(160deg, #2C1810, #5C2B1A, #8B1A1A)',
      display: 'flex', alignItems: 'center', justifyContent: 'center',
      padding: 20,
    }}>
      <motion.div
        className="classical-card"
        style={{ width: '100%', maxWidth: 480, padding: 28 }}
        initial={{ opacity: 0, scale: 0.95 }}
        animate={{ opacity: 1, scale: 1 }}
      >
        <div style={{ textAlign: 'center', marginBottom: 24 }}>
          <div style={{ fontSize: 40 }}>📖</div>
          <h2 style={{
            fontFamily: "'ZCOOL XiaoWei', serif",
            color: '#8B1A1A', margin: '8px 0', letterSpacing: 4,
          }}>
            建立学习库
          </h2>
          <p style={{ color: '#888', fontSize: 13 }}>选择教材体系和年级，开启古诗学习之旅</p>
        </div>

        <Steps
          current={step}
          size="small"
          style={{ marginBottom: 24 }}
          items={[{ title: '选教材' }, { title: '选年级' }, { title: '选诗篇' }]}
        />

        {/* 步骤0：选教材 */}
        <div style={{ marginBottom: 16 }}>
          <div style={{ marginBottom: 8, color: '#8B1A1A', fontWeight: 'bold' }}>选择教材体系</div>
          <Select
            style={{ width: '100%' }}
            placeholder="请选择教材"
            value={selectedTextbook}
            onChange={handleSelectTextbook}
            size="large"
            loading={loadingGrades}
            options={textbooks.map((t) => ({ value: t.id, label: t.name }))}
          />
        </div>

        {/* 步骤1：选年级 */}
        {grades.length > 0 && (
          <div style={{ marginBottom: 16 }}>
            <div style={{ marginBottom: 8, color: '#8B1A1A', fontWeight: 'bold' }}>选择年级</div>
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8 }}>
              {grades.map((g) => (
                <Tag
                  key={g.id}
                  color={selectedGrade === g.id ? '#8B1A1A' : 'default'}
                  style={{ cursor: 'pointer', padding: '6px 14px', fontSize: 14, borderRadius: 8 }}
                  onClick={() => handleSelectGrade(g.id)}
                >
                  {g.name}
                </Tag>
              ))}
            </div>
          </div>
        )}

        {/* 步骤2：选单元 */}
        {step >= 2 && (
          <div style={{ marginBottom: 20 }}>
            <div style={{ marginBottom: 8, color: '#8B1A1A', fontWeight: 'bold' }}>
              选择学习单元（不选则加载全部）
            </div>
            {loadingUnits ? <Spin /> : (
              <Checkbox.Group
                value={selectedUnitIds}
                onChange={setSelectedUnitIds}
                style={{ display: 'flex', flexDirection: 'column', gap: 8 }}
              >
                {units.map((u) => (
                  <Checkbox key={u.id} value={u.id}>{u.name}</Checkbox>
                ))}
              </Checkbox.Group>
            )}
          </div>
        )}

        {step >= 2 && (
          <Button
            type="primary"
            block
            size="large"
            loading={saving}
            onClick={handleStart}
            style={{
              background: 'linear-gradient(135deg, #8B1A1A, #C9512C)',
              border: 'none', height: 48, fontSize: 16, letterSpacing: 4,
            }}
          >
            开始学习 📚
          </Button>
        )}
      </motion.div>
    </div>
  );
}
