import React, { useState, useEffect } from 'react';
import { Tabs, Table, Button, Modal, Form, Input, Upload,
         Select, Switch, Tag, message, InputNumber, Space } from 'antd';
import { PlusOutlined, UploadOutlined } from '@ant-design/icons';
import { adminApi } from '../api';

const { TabPane } = Tabs;

/* ── 用户管理 ─────────────────────────────────────────── */
function UserTab() {
  const [users,   setUsers]   = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    setLoading(true);
    adminApi.listUsers()
      .then((r) => setUsers(r.data || []))
      .finally(() => setLoading(false));
  }, []);

  const cols = [
    { title: 'ID',   dataIndex: 'id',    width: 60 },
    { title: '昵称', dataIndex: 'nickname' },
    { title: '手机', dataIndex: 'phone' },
    { title: '元宝', dataIndex: 'yuanbaoPoints', width: 80 },
    { title: '学习(min)', dataIndex: 'totalStudyMinutes', width: 100 },
    { title: '角色', dataIndex: 'role',
      render: (r) => <Tag color={r === 'ADMIN' ? 'red' : 'blue'}>{r}</Tag> },
  ];

  return (
    <Table
      columns={cols} dataSource={users} loading={loading}
      rowKey="id" scroll={{ x: 600 }} size="small"
      pagination={{ pageSize: 10 }}
    />
  );
}

/* ── 内容管理 ─────────────────────────────────────────── */
function ContentTab() {
  const [poems,    setPoems]    = useState([]);
  const [loading,  setLoading]  = useState(false);
  const [modal,    setModal]    = useState(false);
  const [saving,   setSaving]   = useState(false);
  const [audioFile, setAudioFile]     = useState(null);
  const [animFile,  setAnimFile]      = useState(null);
  const [form] = Form.useForm();

  const load = () => {
    setLoading(true);
    adminApi.listPoems()
      .then((r) => setPoems(r.data || []))
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, []);

  const handleCreate = async (values) => {
    setSaving(true);
    try {
      const formData = new FormData();
      Object.entries(values).forEach(([k, v]) => { if (v != null) formData.append(k, v); });
      if (audioFile) formData.append('audioFile', audioFile);
      if (animFile)  formData.append('animationFile', animFile);
      await adminApi.createPoem(formData);
      message.success('古诗已创建');
      setModal(false); form.resetFields(); setAudioFile(null); setAnimFile(null);
      load();
    } catch (e) { message.error(e.message || '创建失败'); }
    finally { setSaving(false); }
  };

  const handleDelete = (id) => {
    Modal.confirm({
      title: '确认删除？', content: '删除后无法恢复',
      okButtonProps: { style: { background: '#8B1A1A', border: 'none' } },
      onOk: async () => {
        await adminApi.deletePoem(id);
        message.success('已删除');
        load();
      },
    });
  };

  const cols = [
    { title: 'ID',   dataIndex: 'id',      width: 60 },
    { title: '标题', dataIndex: 'title' },
    { title: '朝代', dataIndex: 'dynasty', width: 80 },
    { title: '作者', dataIndex: 'author',  width: 90 },
    { title: '单元ID', dataIndex: 'unitId', width: 80 },
    { title: '操作', width: 80,
      render: (_, r) => <Button danger size="small" onClick={() => handleDelete(r.id)}>删除</Button> },
  ];

  return (
    <>
      <div style={{ marginBottom: 12, textAlign: 'right' }}>
        <Button type="primary" icon={<PlusOutlined />}
          style={{ background: '#8B1A1A', border: 'none' }}
          onClick={() => setModal(true)}>
          新增古诗
        </Button>
      </div>
      <Table columns={cols} dataSource={poems} loading={loading}
        rowKey="id" scroll={{ x: 600 }} size="small" pagination={{ pageSize: 10 }} />

      <Modal title="新增古诗" open={modal} onCancel={() => setModal(false)} footer={null} width={520}>
        <Form form={form} layout="vertical" onFinish={handleCreate}>
          <Form.Item name="title"    label="标题"  rules={[{ required: true }]}><Input /></Form.Item>
          <Form.Item name="dynasty"  label="朝代"  rules={[{ required: true }]}><Input /></Form.Item>
          <Form.Item name="author"   label="作者"  rules={[{ required: true }]}><Input /></Form.Item>
          <Form.Item name="content"  label="正文"  rules={[{ required: true }]}><Input.TextArea rows={3} /></Form.Item>
          <Form.Item name="translation"  label="译文"><Input.TextArea rows={2} /></Form.Item>
          <Form.Item name="authorIntro"  label="诗人介绍"><Input.TextArea rows={2} /></Form.Item>
          <Form.Item name="background"   label="写作背景"><Input.TextArea rows={2} /></Form.Item>
          <Form.Item name="unitId" label="单元ID" rules={[{ required: true }]}><InputNumber style={{ width: '100%' }} /></Form.Item>
          <Form.Item label="朗读音频">
            <Upload beforeUpload={(f) => { setAudioFile(f); return false; }} maxCount={1} accept="audio/*">
              <Button icon={<UploadOutlined />}>选择音频</Button>
            </Upload>
          </Form.Item>
          <Form.Item label="动画视频">
            <Upload beforeUpload={(f) => { setAnimFile(f); return false; }} maxCount={1} accept="video/*">
              <Button icon={<UploadOutlined />}>选择视频</Button>
            </Upload>
          </Form.Item>
          <Button type="primary" htmlType="submit" block size="large" loading={saving}
            style={{ background: '#8B1A1A', border: 'none' }}>创建</Button>
        </Form>
      </Modal>
    </>
  );
}

/* ── 商城管理 ─────────────────────────────────────────── */
function ShopTab() {
  const [items,   setItems]   = useState([]);
  const [orders,  setOrders]  = useState([]);
  const [loading, setLoading] = useState(false);
  const [ordersLoading, setOrdersLoading] = useState(false);
  const [itemModal,  setItemModal]  = useState(false);
  const [ordersModal, setOrdersModal] = useState(false);
  const [saving,  setSaving]  = useState(false);
  const [imgFile, setImgFile] = useState(null);
  const [form]    = Form.useForm();

  const loadItems = () => {
    setLoading(true);
    adminApi.listShopItems()
      .then((r) => setItems(r.data || []))
      .finally(() => setLoading(false));
  };

  const loadOrders = () => {
    setOrdersLoading(true);
    adminApi.listOrders()
      .then((r) => setOrders(r.data || []))
      .finally(() => setOrdersLoading(false));
  };

  useEffect(() => { loadItems(); }, []);

  const handleCreate = async (values) => {
    setSaving(true);
    try {
      const formData = new FormData();
      Object.entries(values).forEach(([k, v]) => { if (v != null) formData.append(k, v); });
      if (imgFile) formData.append('imageFile', imgFile);
      await adminApi.createShopItem(formData);
      message.success('商品已创建');
      setItemModal(false); form.resetFields(); setImgFile(null);
      loadItems();
    } catch (e) { message.error(e.message || '创建失败'); }
    finally { setSaving(false); }
  };

  const handleShelf = async (item, shelf) => {
    try {
      if (shelf) await adminApi.onShelfItem(item.id);
      else       await adminApi.offShelfItem(item.id);
      message.success(shelf ? '已上架' : '已下架');
      loadItems();
    } catch { message.error('操作失败'); }
  };

  const handleOrderStatus = async (orderId, status) => {
    try {
      await adminApi.updateOrderStatus(orderId, status);
      message.success('状态已更新');
      loadOrders();
    } catch { message.error('更新失败'); }
  };

  const itemCols = [
    { title: 'ID',     dataIndex: 'id',          width: 60 },
    { title: '商品名', dataIndex: 'name' },
    { title: '积分',   dataIndex: 'pointsCost',  width: 80 },
    { title: '库存',   dataIndex: 'stock',       width: 80 },
    { title: '上架',   dataIndex: 'onShelf',     width: 80,
      render: (v, r) => (
        <Switch checked={v} onChange={(c) => handleShelf(r, c)} />
      ) },
  ];

  const orderCols = [
    { title: 'ID',     dataIndex: 'id',       width: 60 },
    { title: '用户',   dataIndex: 'userNickname' },
    { title: '收货人', dataIndex: 'receiverName' },
    { title: '积分',   dataIndex: 'totalPoints', width: 80 },
    { title: '状态',   dataIndex: 'status',     width: 110,
      render: (v, r) => (
        <Select
          value={v} size="small" style={{ width: 100 }}
          onChange={(s) => handleOrderStatus(r.id, s)}
          options={[
            { value: 'PENDING',   label: '待发货' },
            { value: 'SHIPPED',   label: '已发货' },
            { value: 'COMPLETED', label: '已完成' },
          ]}
        />
      ) },
  ];

  return (
    <>
      <Space style={{ marginBottom: 12 }}>
        <Button type="primary" icon={<PlusOutlined />}
          style={{ background: '#8B1A1A', border: 'none' }}
          onClick={() => setItemModal(true)}>
          新增商品
        </Button>
        <Button onClick={() => { loadOrders(); setOrdersModal(true); }}>查看订单</Button>
      </Space>
      <Table columns={itemCols} dataSource={items} loading={loading}
        rowKey="id" size="small" pagination={{ pageSize: 10 }} />

      {/* 新增商品 Modal */}
      <Modal title="新增商品" open={itemModal} onCancel={() => setItemModal(false)} footer={null}>
        <Form form={form} layout="vertical" onFinish={handleCreate}>
          <Form.Item name="name"        label="商品名" rules={[{ required: true }]}><Input /></Form.Item>
          <Form.Item name="description" label="描述"><Input.TextArea rows={2} /></Form.Item>
          <Form.Item name="pointsCost"  label="所需积分" rules={[{ required: true }]}><InputNumber min={1} style={{ width: '100%' }} /></Form.Item>
          <Form.Item name="stock"       label="库存"    rules={[{ required: true }]}><InputNumber min={0} style={{ width: '100%' }} /></Form.Item>
          <Form.Item label="商品图片">
            <Upload beforeUpload={(f) => { setImgFile(f); return false; }} maxCount={1} accept="image/*">
              <Button icon={<UploadOutlined />}>选择图片</Button>
            </Upload>
          </Form.Item>
          <Button type="primary" htmlType="submit" block size="large" loading={saving}
            style={{ background: '#8B1A1A', border: 'none' }}>创建</Button>
        </Form>
      </Modal>

      {/* 订单 Modal */}
      <Modal title="所有订单" open={ordersModal} onCancel={() => setOrdersModal(false)}
        footer={null} width={700}>
        <Table columns={orderCols} dataSource={orders} loading={ordersLoading}
          rowKey="id" size="small" scroll={{ x: 600 }} pagination={{ pageSize: 8 }} />
      </Modal>
    </>
  );
}

/* ── AdminPage ──────────────────────────────────────────── */
export default function AdminPage() {
  return (
    <div style={{ padding: '12px 16px', paddingBottom: 80 }}>
      <div style={{
        fontFamily: "'ZCOOL XiaoWei', serif", fontSize: 22,
        color: '#8B1A1A', letterSpacing: 4, marginBottom: 16,
      }}>
        管理后台
      </div>
      <Tabs defaultActiveKey="users" size="small">
        <TabPane tab="用户管理" key="users"><UserTab /></TabPane>
        <TabPane tab="内容管理" key="content"><ContentTab /></TabPane>
        <TabPane tab="商城管理" key="shop"><ShopTab /></TabPane>
      </Tabs>
    </div>
  );
}
