// section-extras-1.jsx — Order state tabs, cancel order, system update

// Shared compact order list builder
const OrderListShell = ({ activeTab, orders, emptyText, summary }) => (
  <Phone>
    <div style={{ background: '#fff' }}>
      <StatusBar theme="light"/>
      <NavBar title="我的订单" theme="on-light"/>
      <div style={{ display: 'flex', borderBottom: '1px solid var(--line-soft)', padding: '0 8px' }}>
        {[
          { l: '全部', k: 'all' },
          { l: '待付款', k: 'unpaid', n: 1 },
          { l: '待发货', k: 'ship', n: 2 },
          { l: '待收货', k: 'receive' },
          { l: '已完成', k: 'done' },
          { l: '售后', k: 'after' },
        ].map(t => (
          <div key={t.k} style={{
            flex: 1, textAlign: 'center', padding: '12px 0',
            fontSize: 13, fontWeight: t.k === activeTab ? 600 : 400,
            color: t.k === activeTab ? 'var(--gold-500)' : 'var(--text-2)',
            position: 'relative',
          }}>
            {t.l}{t.n ? <sup style={{ fontSize: 9, color: '#D6453B', marginLeft: 2 }}>{t.n}</sup> : ''}
            {t.k === activeTab && <div style={{ position: 'absolute', bottom: 0, left: '50%', transform: 'translateX(-50%)', width: 22, height: 2, background: 'var(--grad-gold)', borderRadius: 2 }}/>}
          </div>
        ))}
      </div>
    </div>

    {summary}

    <div style={{ padding: '12px 16px 30px', overflow: 'auto', height: `calc(100% - 88px - 44px - ${summary ? 70 : 0}px)` }}>
      {orders.map((o, i) => (
        <div key={i} style={{ background: '#fff', borderRadius: 12, padding: '14px', marginBottom: 12, boxShadow: 'var(--shadow-card)' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div style={{ fontSize: 12, color: 'var(--text-3)' }}>订单号 {o.id}</div>
            <div style={{ fontSize: 13, fontWeight: 600, color: o.stateColor }}>{o.state}</div>
          </div>
          <div style={{ marginTop: 12 }}>
            {o.items.map((p, j) => (
              <div key={j} style={{ display: 'flex', gap: 10, padding: '4px 0' }}>
                <div style={{ width: 56, height: 56, borderRadius: 6, overflow: 'hidden', flexShrink: 0 }}><ProdImg kind={p.k}/></div>
                <div style={{ flex: 1, minWidth: 0 }}>
                  <div style={{ fontSize: 13, fontWeight: 500, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{p.n}</div>
                  <div style={{ fontSize: 11, color: 'var(--text-3)', marginTop: 2 }}>500ml 单瓶</div>
                </div>
                <div style={{ textAlign: 'right' }}>
                  <div className="price font-num" style={{ fontSize: 14 }}><span className="sym">¥</span>{p.p}</div>
                  <div style={{ fontSize: 11, color: 'var(--text-3)' }}>×{p.q}</div>
                </div>
              </div>
            ))}
          </div>
          <div style={{ display: 'flex', alignItems: 'center', marginTop: 8, paddingTop: 8, borderTop: '1px solid var(--line-soft)' }}>
            <div style={{ fontSize: 11, color: 'var(--text-3)' }}>{o.time}</div>
            <div style={{ marginLeft: 'auto', fontSize: 12, color: 'var(--text-2)' }}>
              共 {o.count} 件 · 实付 <span className="font-num" style={{ color: 'var(--gold-500)', fontWeight: 700, fontSize: 15 }}>¥{o.total}</span>
            </div>
          </div>
          <div style={{ display: 'flex', gap: 8, marginTop: 12, justifyContent: 'flex-end' }}>
            {o.actions.map(([label, primary], k) => (
              <button key={k} style={{
                padding: '6px 16px', borderRadius: 999, border: 'none', cursor: 'pointer', fontSize: 12, fontWeight: 600,
                background: primary ? 'var(--grad-gold)' : '#fff',
                color: primary ? '#3A1A00' : 'var(--text-2)',
                boxShadow: primary ? '0 4px 10px rgba(211,138,0,0.25)' : 'inset 0 0 0 1px var(--line)',
              }}>{label}</button>
            ))}
          </div>
        </div>
      ))}
      {emptyText && orders.length < 2 && (
        <div style={{ textAlign: 'center', padding: '20px 0', fontSize: 12, color: 'var(--text-3)' }}>— {emptyText} —</div>
      )}
    </div>
    <HomeIndicator/>
  </Phone>
);

// ============================================================
// 待发货
// ============================================================
const ScreenOrdersWaitShip = () => (
  <OrderListShell
    activeTab="ship"
    summary={
      <div style={{ background: '#FFF6DA', padding: '10px 16px', display: 'flex', alignItems: 'center', gap: 8, fontSize: 12, color: '#7A5610' }}>
        <Icon name="box" size={16} color="#D38A00"/>
        2 个订单等待发货 · 平台承诺 48 小时内出库
      </div>
    }
    orders={[
      {
        id: 'CPH202605151235012', state: '待发货', stateColor: '#D38A00',
        items: [{ n: '法国波尔多干红 750ml', p: '298', q: 2, k: 'redwine' }],
        total: '596.00', count: 2,
        actions: [['联系客服', false], ['提醒发货', true]],
        time: '已付款 · 备货中 · 5/15 12:35',
      },
      {
        id: 'CPH202605141156234', state: '待发货', stateColor: '#D38A00',
        items: [
          { n: '茅台飞天 53度 500ml', p: '2,980', q: 1, k: 'maotai' },
          { n: '五粮液普五 52度 500ml', p: '1,380', q: 1, k: 'wuliang' },
        ],
        total: '4,060.00', count: 2,
        actions: [['联系客服', false], ['提醒发货', true]],
        time: '已付款 · 仓库打包中 · 5/14 11:56',
      },
    ]}
  />
);

// ============================================================
// 待收货
// ============================================================
const ScreenOrdersWaitReceive = () => (
  <OrderListShell
    activeTab="receive"
    summary={
      <div style={{ background: '#E8EFFD', padding: '10px 16px', display: 'flex', alignItems: 'center', gap: 8, fontSize: 12, color: '#1F4B9A' }}>
        <Icon name="truck" size={16} color="#3A6FD1"/>
        2 个订单运输中 · 长按订单可一键确认收货
      </div>
    }
    orders={[
      {
        id: 'CPH202605101015487', state: '配送中', stateColor: '#3A6FD1',
        items: [{ n: '青花郎20 53度 500ml', p: '1,188', q: 1, k: 'redwine' }],
        total: '1,188.00', count: 1,
        actions: [['查看物流', false], ['确认收货', true]],
        time: '顺丰速运 · 预计今日送达',
      },
      {
        id: 'CPH202605081842329', state: '已发货', stateColor: '#3A6FD1',
        items: [{ n: '威士忌 12年 700ml', p: '688', q: 1, k: 'whisky' }],
        total: '688.00', count: 1,
        actions: [['查看物流', false], ['确认收货', true]],
        time: '京东物流 · 预计 5/17 送达',
      },
    ]}
  />
);

// ============================================================
// 已完成
// ============================================================
const ScreenOrdersDone = () => (
  <OrderListShell
    activeTab="done"
    summary={
      <div style={{ background: '#E6F5EB', padding: '10px 16px', display: 'flex', alignItems: 'center', gap: 8, fontSize: 12, color: '#1F6B40' }}>
        <Icon name="check-circle" size={16} color="#2F9E5C"/>
        本月已完成 12 单 · 累计获得佣金 <span style={{ fontWeight: 700 }}>¥1,892.40</span>
      </div>
    }
    orders={[
      {
        id: 'CPH202605051015487', state: '已完成', stateColor: '#9B8E7C',
        items: [
          { n: '茅台飞天 53度 500ml', p: '2,980', q: 1, k: 'maotai' },
          { n: '国窖1573经典装 500ml', p: '980', q: 1, k: 'luzhou' },
        ],
        total: '3,960.00', count: 2,
        actions: [['再次购买', false], ['评价晒单', false], ['申请售后', false]],
        time: '佣金 ¥792 已到账 · 5/05 完成',
      },
      {
        id: 'CPH202604281542023', state: '已完成', stateColor: '#9B8E7C',
        items: [{ n: '法国波尔多干红 750ml', p: '298', q: 2, k: 'redwine' }],
        total: '596.00', count: 2,
        actions: [['再次购买', false], ['申请售后', false]],
        time: '已评价 · 佣金 ¥89.40 已到账',
      },
      {
        id: 'CPH202604221015487', state: '已完成', stateColor: '#9B8E7C',
        items: [{ n: '威士忌 12年 700ml', p: '688', q: 1, k: 'whisky' }],
        total: '688.00', count: 1,
        actions: [['再次购买', false], ['申请售后', false]],
        time: '已评价 · 佣金 ¥137.60 已到账',
      },
    ]}
    emptyText="只显示近 3 个月，更多请前往交易记录"
  />
);

// ============================================================
// 取消订单原因选择
// ============================================================
const ScreenCancelOrder = () => (
  <Phone>
    {/* background — faint order detail */}
    <div style={{ background: '#fff' }}>
      <StatusBar theme="light"/>
      <NavBar title="订单详情" theme="on-light"/>
    </div>
    <div style={{ padding: '12px 16px', background: 'var(--bg-app)', height: 'calc(100% - 88px)' }}>
      <div style={{ background: '#fff', borderRadius: 12, padding: 14 }}>
        <div style={{ fontSize: 13, color: 'var(--text-3)' }}>订单状态：待付款</div>
        <div style={{ fontSize: 14, fontWeight: 600, marginTop: 8 }}>茅台飞天 53度 500ml ×2</div>
        <div className="price font-num" style={{ fontSize: 22, marginTop: 8 }}><span className="sym">¥</span>5,960</div>
      </div>
    </div>

    {/* Mask */}
    <div className="sheet-mask"/>
    {/* Bottom sheet — cancel reason picker */}
    <div className="sheet" style={{ padding: '18px 16px 28px' }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 14 }}>
        <div style={{ fontSize: 16, fontWeight: 700 }}>请选择取消原因</div>
        <Icon name="close" size={20} color="#999"/>
      </div>
      <div style={{ fontSize: 12, color: 'var(--text-3)', marginBottom: 8 }}>取消后将立即退款 · 已使用的优惠券与积分原路返还</div>

      {[
        { l: '不想买了 / 暂时不需要', a: true },
        { l: '商品规格 / 数量选错' },
        { l: '其他平台价格更优惠' },
        { l: '收货地址填错了' },
        { l: '希望更换支付方式' },
        { l: '商品发货太慢' },
        { l: '重复下单' },
        { l: '其他原因' },
      ].map((o, i) => (
        <div key={o.l} style={{
          display: 'flex', alignItems: 'center', gap: 10,
          padding: '14px 12px', borderBottom: i < 7 ? '1px solid var(--line-soft)' : 'none',
        }}>
          <div style={{
            width: 18, height: 18, borderRadius: '50%',
            background: o.a ? 'var(--grad-gold)' : '#fff',
            border: o.a ? 'none' : '1.5px solid var(--line)',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            flexShrink: 0,
          }}>
            {o.a && <Icon name="check" size={12} color="#fff" strokeWidth={3}/>}
          </div>
          <div style={{ fontSize: 14, color: o.a ? 'var(--text-1)' : 'var(--text-2)', fontWeight: o.a ? 600 : 400 }}>{o.l}</div>
        </div>
      ))}

      <div style={{ display: 'flex', gap: 10, marginTop: 18 }}>
        <button className="btn-gold-outline" style={{ flex: 1 }}>我再想想</button>
        <button className="btn-gold" style={{ flex: 1.4 }}>提交取消</button>
      </div>
      <HomeIndicator/>
    </div>
  </Phone>
);

// ============================================================
// 系统更新弹窗
// ============================================================
const ScreenSystemUpdate = () => (
  <Phone>
    {/* faint home behind */}
    <div style={{ background: 'var(--grad-brown)', height: 380, opacity: 0.85 }}>
      <StatusBar theme="dark"/>
      <div style={{ padding: '8px 16px', color: '#F6E7C2', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <div style={{ fontSize: 22, fontWeight: 700, color: '#FFE0A0' }}>醇品汇</div>
      </div>
    </div>
    <div style={{ position: 'absolute', inset: 0, background: 'rgba(0,0,0,0.6)' }}/>

    {/* Update card */}
    <div className="modal-mask">
      <div style={{ width: 320, background: '#fff', borderRadius: 16, overflow: 'hidden', position: 'relative' }}>
        {/* Header banner */}
        <div style={{
          height: 144, background: 'linear-gradient(135deg, #2A0E00 0%, #4A1E08 100%)',
          padding: '20px 22px', position: 'relative', overflow: 'hidden',
        }}>
          <div style={{ position: 'absolute', top: -20, right: -20, width: 120, height: 120, borderRadius: '50%', background: 'radial-gradient(circle, rgba(228,165,22,0.32), transparent 60%)' }}/>
          <div style={{ position: 'absolute', top: 18, right: 22, background: 'var(--grad-gold)', color: '#3A1A00', fontSize: 11, fontWeight: 700, padding: '3px 8px', borderRadius: 4 }}>NEW</div>
          <div style={{ fontSize: 10, color: 'rgba(246,231,194,0.55)', letterSpacing: 3, marginTop: 8 }}>VERSION 2.0</div>
          <div style={{ fontSize: 22, fontWeight: 700, color: '#FFE0A0', marginTop: 6 }}>发现新版本</div>
          <div style={{ fontSize: 12, color: 'rgba(246,231,194,0.7)', marginTop: 4 }}>v2.0.0 · 35.6 MB</div>
        </div>

        {/* Update content */}
        <div style={{ padding: '18px 22px 4px' }}>
          <div style={{ fontSize: 13, fontWeight: 700, marginBottom: 10, color: 'var(--text-1)' }}>本次更新内容</div>
          {[
            ['新增', '酒豆资产体系，下单即可获得酒豆奖励'],
            ['新增', 'DF 余额团队内赠送功能，更便捷流转'],
            ['优化', '商品详情页加载速度，分享体验更顺滑'],
            ['修复', '部分机型订单状态显示异常的问题'],
          ].map(([tag, txt], i) => (
            <div key={i} style={{ display: 'flex', gap: 8, padding: '4px 0', fontSize: 12, color: 'var(--text-2)', lineHeight: 1.6 }}>
              <span style={{
                flexShrink: 0, padding: '1px 6px', borderRadius: 4,
                background: tag === '新增' ? 'var(--gold-50)' : tag === '优化' ? '#E8EFFD' : '#E6F5EB',
                color: tag === '新增' ? 'var(--gold-600)' : tag === '优化' ? '#3A6FD1' : '#1F6B40',
                fontSize: 10, fontWeight: 700, height: 18, lineHeight: '16px',
              }}>{tag}</span>
              <span>{txt}</span>
            </div>
          ))}
        </div>

        {/* Actions */}
        <div style={{ padding: '16px 22px 22px', display: 'flex', flexDirection: 'column', gap: 10 }}>
          <button className="btn-gold" style={{ width: '100%' }}>立即更新</button>
          <div style={{ textAlign: 'center', fontSize: 13, color: 'var(--text-3)' }}>稍后再说</div>
        </div>

        {/* Force update toggle */}
        <div style={{ borderTop: '1px solid var(--line-soft)', padding: '10px 22px', display: 'flex', alignItems: 'center', gap: 8, fontSize: 11, color: 'var(--text-3)' }}>
          <div style={{ width: 14, height: 14, borderRadius: 3, border: '1.5px solid var(--line)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}/>
          7 天内不再提示
        </div>
      </div>
    </div>
    <HomeIndicator dark/>
  </Phone>
);

Object.assign(window, {
  ScreenOrdersWaitShip, ScreenOrdersWaitReceive, ScreenOrdersDone,
  ScreenCancelOrder, ScreenSystemUpdate,
});
