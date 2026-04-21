import React, { useState, useEffect } from 'react';
import { Button, Col, Row, Badge, Drawer, List, Form, Input,
         Empty, Tag, Spin, message, Modal, Divider } from 'antd';
import { ShoppingCartOutlined } from '@ant-design/icons';
import { motion } from 'framer-motion';
import { shopApi } from '../api';
import { useAuthStore } from '../store/authStore';

export default function ShopPage() {
  const user       = useAuthStore((s) => s.user);
  const updateUser = useAuthStore((s) => s.updateUser);

  const [items,      setItems]      = useState([]);
  const [loading,    setLoading]    = useState(false);
  const [cartItems,  setCartItems]  = useState([]);
  const [cartOpen,   setCartOpen]   = useState(false);
  const [orderOpen,  setOrderOpen]  = useState(false);
  const [orders,     setOrders]     = useState([]);
  const [ordersOpen, setOrdersOpen] = useState(false);
  const [placing,    setPlacing]    = useState(false);
  const [form] = Form.useForm();

  useEffect(() => { loadItems(); loadCart(); }, []);

  const loadItems = async () => {
    setLoading(true);
    try {
      const r = await shopApi.listItems(0, 20);
      setItems(r.data?.content || []);
    } finally { setLoading(false); }
  };

  const loadCart = async () => {
    try {
      const r = await shopApi.getCart();
      setCartItems(r.data || []);
    } catch {}
  };

  const handleAddToCart = async (itemId) => {
    try {
      await shopApi.addToCart(itemId, 1);
      message.success('已加入购物车');
      loadCart();
    } catch (e) { message.error(e.message || '添加失败'); }
  };

  const totalPoints = cartItems.reduce((s, ci) => s + ci.pointsCost * ci.quantity, 0);

  const handlePlaceOrder = async (values) => {
    setPlacing(true);
    try {
      await shopApi.placeOrder(values);
      message.success('🎉 下单成功！元宝已扣除');
      updateUser({ yuanbaoPoints: Math.max(0, (user?.yuanbaoPoints || 0) - totalPoints) });
      setOrderOpen(false);
      setCartOpen(false);
      loadCart();
      form.resetFields();
    } catch (e) { message.error(e.message || '下单失败'); }
    finally { setPlacing(false); }
  };

  const loadOrders = async () => {
    const r = await shopApi.getOrders();
    setOrders(r.data || []);
    setOrdersOpen(true);
  };

  return (
    <div style={{ padding: 12, paddingBottom: 80 }}>
      {/* 顶部工具栏 */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 14 }}>
        <div style={{ fontFamily: "'ZCOOL XiaoWei', serif", fontSize: 20, color: '#8B1A1A', letterSpacing: 3 }}>
          积分兑换商城
        </div>
        <div style={{ display: 'flex', gap: 10, alignItems: 'center' }}>
          <span className="yuanbao-badge">🪙 {user?.yuanbaoPoints || 0}</span>
          <Badge count={cartItems.length} showZero={false}>
            <Button
              icon={<ShoppingCartOutlined />}
              shape="circle"
              size="large"
              style={{ background: '#8B1A1A', color: 'white', border: 'none' }}
              onClick={() => setCartOpen(true)}
            />
          </Badge>
          <Button size="small" onClick={loadOrders}>我的订单</Button>
        </div>
      </div>

      {/* 商品列表 */}
      {loading ? (
        <div style={{ textAlign: 'center', padding: 60 }}><Spin size="large" /></div>
      ) : (
        <Row gutter={[10, 10]}>
          {items.map((item, idx) => (
            <Col span={12} key={item.id}>
              <motion.div
                initial={{ opacity: 0, y: 16 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: idx * 0.05 }}
              >
                <div className="classical-card" style={{ padding: 0, overflow: 'hidden' }}>
                  <div style={{
                    height: 120, backgroundColor: '#f5e6c8',
                    background: item.imageUrl ? `url(${item.imageUrl}) center/cover` : '#f5e6c8',
                    display: 'flex', alignItems: 'center', justifyContent: 'center',
                  }}>
                    {!item.imageUrl && <span style={{ fontSize: 44 }}>🏮</span>}
                  </div>
                  <div style={{ padding: '10px 12px' }}>
                    <div style={{
                      fontFamily: "'ZCOOL XiaoWei', serif", fontSize: 14, color: '#2C1810',
                      marginBottom: 4, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap',
                    }}>
                      {item.name}
                    </div>
                    <div style={{ fontSize: 11, color: '#888', marginBottom: 8, height: 30, overflow: 'hidden' }}>
                      {item.description}
                    </div>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                      <span style={{ color: '#C9512C', fontWeight: 'bold', fontSize: 14 }}>
                        🪙 {item.pointsCost}
                      </span>
                      <Tag color={item.stock > 0 ? 'success' : 'error'} style={{ fontSize: 10 }}>
                        {item.stock > 0 ? `库存${item.stock}` : '已售罄'}
                      </Tag>
                    </div>
                    <Button
                      type="primary" block size="small"
                      disabled={item.stock === 0}
                      onClick={() => handleAddToCart(item.id)}
                      style={{ marginTop: 8, height: 30, background: item.stock > 0 ? '#8B1A1A' : undefined, border: 'none' }}
                    >
                      加入购物车
                    </Button>
                  </div>
                </div>
              </motion.div>
            </Col>
          ))}
          {items.length === 0 && <Col span={24}><Empty description="暂无商品" /></Col>}
        </Row>
      )}

      {/* 购物车 Drawer */}
      <Drawer
        title={<span style={{ fontFamily: "'ZCOOL XiaoWei', serif", letterSpacing: 3 }}>🛒 购物车</span>}
        placement="bottom"
        height={420}
        open={cartOpen}
        onClose={() => setCartOpen(false)}
      >
        {cartItems.length === 0 ? <Empty description="购物车是空的" /> : (
          <>
            <List
              dataSource={cartItems}
              renderItem={(ci) => (
                <List.Item style={{ padding: '8px 0' }}>
                  <div style={{ display: 'flex', gap: 12, width: '100%', alignItems: 'center' }}>
                    <div style={{
                      width: 48, height: 48, borderRadius: 8, flexShrink: 0,
                      background: ci.itemImageUrl ? `url(${ci.itemImageUrl}) center/cover` : '#f5e6c8',
                      display: 'flex', alignItems: 'center', justifyContent: 'center',
                    }}>
                      {!ci.itemImageUrl && '🏮'}
                    </div>
                    <div style={{ flex: 1 }}>
                      <div style={{ fontSize: 14 }}>{ci.itemName}</div>
                      <div style={{ color: '#C9512C', fontSize: 13 }}>🪙 {ci.pointsCost} × {ci.quantity}</div>
                    </div>
                  </div>
                </List.Item>
              )}
            />
            <Divider />
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <span style={{ fontSize: 16 }}>
                合计：<strong style={{ color: '#C9512C' }}>🪙 {totalPoints}</strong>
              </span>
              <Button
                type="primary" size="large"
                style={{ background: '#8B1A1A', border: 'none', padding: '0 32px' }}
                disabled={totalPoints > (user?.yuanbaoPoints || 0)}
                onClick={() => { setCartOpen(false); setOrderOpen(true); }}
              >
                {totalPoints > (user?.yuanbaoPoints || 0) ? '元宝不足' : '去结算'}
              </Button>
            </div>
          </>
        )}
      </Drawer>

      {/* 填写收货信息 */}
      <Modal title="填写收货信息" open={orderOpen} onCancel={() => setOrderOpen(false)} footer={null}>
        <Form form={form} layout="vertical" onFinish={handlePlaceOrder}>
          <Form.Item name="receiverName" label="收货人" rules={[{ required: true }]}>
            <Input placeholder="姓名" />
          </Form.Item>
          <Form.Item name="receiverPhone" label="手机号"
            rules={[{ required: true, pattern: /^1\d{10}$/, message: '请输入正确手机号' }]}>
            <Input placeholder="11位手机号" />
          </Form.Item>
          <Form.Item name="receiverAddress" label="收货地址" rules={[{ required: true }]}>
            <Input.TextArea placeholder="省市区详细地址" rows={3} />
          </Form.Item>
          <div style={{ marginBottom: 14, color: '#888' }}>
            将消耗 <strong style={{ color: '#C9512C' }}>🪙 {totalPoints}</strong> 元宝
            （余额：{user?.yuanbaoPoints || 0}）
          </div>
          <Button type="primary" htmlType="submit" block size="large" loading={placing}
            style={{ background: '#8B1A1A', border: 'none' }}>
            确认下单
          </Button>
        </Form>
      </Modal>

      {/* 我的订单 */}
      <Drawer
        title="我的订单" placement="bottom" height="60%"
        open={ordersOpen} onClose={() => setOrdersOpen(false)}
      >
        <List
          dataSource={orders}
          locale={{ emptyText: '暂无订单' }}
          renderItem={(order) => (
            <div className="classical-card" style={{ padding: 12, marginBottom: 10 }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 6 }}>
                <span style={{ fontSize: 12, color: '#888' }}>订单 #{order.id}</span>
                <Tag color={order.status === 'PENDING' ? 'gold' : order.status === 'SHIPPED' ? 'blue' : 'success'}>
                  {order.status === 'PENDING' ? '待发货' : order.status === 'SHIPPED' ? '已发货' : '已完成'}
                </Tag>
              </div>
              <div style={{ fontSize: 13, color: '#666' }}>{order.receiverName} · {order.receiverPhone}</div>
              <div style={{ fontSize: 13, color: '#666', marginTop: 2 }}>{order.receiverAddress}</div>
              <div style={{ color: '#C9512C', marginTop: 6, fontWeight: 'bold' }}>🪙 {order.totalPoints} 元宝</div>
            </div>
          )}
        />
      </Drawer>
    </div>
  );
}
