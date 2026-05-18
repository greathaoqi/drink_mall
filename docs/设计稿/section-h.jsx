// section-h.jsx — Common states & overlays

// ============================================================
// H1. 加载中
// ============================================================
const ScreenLoading = () => (
  <Phone>
    <div style={{ background: '#fff' }}>
      <StatusBar theme="light"/>
      <NavBar title="加载中..." theme="on-light"/>
    </div>
    <div style={{ padding: 16, background: 'var(--bg-app)', height: 'calc(100% - 88px)' }}>
      {/* Skeleton items */}
      {[0, 1, 2].map(i => (
        <div key={i} style={{ background: '#fff', borderRadius: 12, padding: 14, marginBottom: 12, display: 'flex', gap: 12 }}>
          <div style={{
            width: 80, height: 80, borderRadius: 8,
            background: 'linear-gradient(90deg, #ECE5D6 25%, #F8F4EC 50%, #ECE5D6 75%)',
            backgroundSize: '200% 100%',
            animation: 'shimmer 1.5s infinite',
          }}/>
          <div style={{ flex: 1, paddingTop: 4 }}>
            <div style={{ height: 14, width: '80%', borderRadius: 4, background: 'linear-gradient(90deg, #ECE5D6 25%, #F8F4EC 50%, #ECE5D6 75%)', backgroundSize: '200% 100%', animation: 'shimmer 1.5s infinite' }}/>
            <div style={{ height: 12, width: '60%', borderRadius: 4, background: 'linear-gradient(90deg, #ECE5D6 25%, #F8F4EC 50%, #ECE5D6 75%)', backgroundSize: '200% 100%', animation: 'shimmer 1.5s infinite', marginTop: 10 }}/>
            <div style={{ height: 12, width: '40%', borderRadius: 4, background: 'linear-gradient(90deg, #ECE5D6 25%, #F8F4EC 50%, #ECE5D6 75%)', backgroundSize: '200% 100%', animation: 'shimmer 1.5s infinite', marginTop: 10 }}/>
          </div>
        </div>
      ))}

      <style>{`
        @keyframes shimmer { 0%{background-position:200% 0}100%{background-position:-200% 0} }
        @keyframes spin { from{transform:rotate(0)} to{transform:rotate(360deg)} }
      `}</style>

      <div style={{ position: 'absolute', left: '50%', top: '60%', transform: 'translate(-50%, -50%)', textAlign: 'center' }}>
        <div style={{
          width: 56, height: 56, borderRadius: '50%',
          background: 'rgba(0,0,0,0.7)',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          margin: '0 auto',
        }}>
          <div style={{
            width: 28, height: 28, borderRadius: '50%',
            border: '3px solid rgba(255,214,133,0.25)',
            borderTopColor: '#FFD646',
            animation: 'spin 0.9s linear infinite',
          }}/>
        </div>
        <div style={{ fontSize: 13, color: 'var(--text-2)', marginTop: 12 }}>加载中...</div>
      </div>
    </div>
    <HomeIndicator/>
  </Phone>
);

// ============================================================
// H2. 空状态 (订单空)
// ============================================================
const ScreenEmpty = () => (
  <Phone>
    <div style={{ background: '#fff' }}>
      <StatusBar theme="light"/>
      <NavBar title="我的订单" theme="on-light"/>
      <div style={{ display: 'flex', borderBottom: '1px solid var(--line-soft)', padding: '0 8px' }}>
        {['全部', '待付款', '待发货', '待收货', '已完成', '售后'].map((t, i) => (
          <div key={t} style={{
            flex: 1, textAlign: 'center', padding: '12px 0',
            fontSize: 13, fontWeight: i === 0 ? 600 : 400,
            color: i === 0 ? 'var(--gold-500)' : 'var(--text-2)',
            position: 'relative',
          }}>
            {t}
            {i === 0 && <div style={{ position: 'absolute', bottom: 0, left: '50%', transform: 'translateX(-50%)', width: 22, height: 2, background: 'var(--grad-gold)' }}/>}
          </div>
        ))}
      </div>
    </div>

    <div className="empty" style={{ paddingTop: 100, background: 'var(--bg-app)', height: 'calc(100% - 88px - 44px)' }}>
      <div style={{
        width: 160, height: 160,
        background: 'var(--cream-100)', borderRadius: '50%',
        display: 'flex', alignItems: 'center', justifyContent: 'center',
        marginBottom: 18,
      }}>
        <svg viewBox="0 0 100 100" width="90" height="90" fill="none" stroke="#E4A516" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <rect x="20" y="30" width="60" height="48" rx="4"/>
          <path d="M20 42 H80"/>
          <path d="M35 22 V35 M65 22 V35"/>
          <path d="M30 58 H50 M30 66 H42" stroke="#E4A516" opacity="0.4"/>
        </svg>
      </div>
      <div style={{ fontSize: 15, fontWeight: 600, color: 'var(--text-1)', marginBottom: 8 }}>暂无订单</div>
      <div style={{ fontSize: 13, color: 'var(--text-3)', marginBottom: 22 }}>下单后您可以在这里查看订单详情和物流</div>
      <button className="btn-gold">去逛逛</button>
    </div>
    <HomeIndicator/>
  </Phone>
);

// ============================================================
// H3. 网络错误
// ============================================================
const ScreenNetError = () => (
  <Phone>
    <div style={{ background: '#fff' }}>
      <StatusBar theme="light"/>
      <NavBar title="" theme="on-light"/>
    </div>
    <div style={{ background: 'var(--bg-app)', height: 'calc(100% - 88px)', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', padding: '0 30px' }}>
      <div style={{
        width: 160, height: 160, borderRadius: '50%',
        background: '#FFF1EF',
        display: 'flex', alignItems: 'center', justifyContent: 'center',
      }}>
        <svg viewBox="0 0 100 100" width="100" height="100" fill="none" stroke="#D6453B" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <path d="M10 40 a40 40 0 0 1 80 0" opacity="0.3"/>
          <path d="M22 50 a30 30 0 0 1 56 0" opacity="0.5"/>
          <path d="M34 60 a18 18 0 0 1 32 0" opacity="0.7"/>
          <circle cx="50" cy="72" r="3.5" fill="#D6453B"/>
          <path d="M30 28 L70 78 M70 28 L30 78" stroke="#D6453B" strokeWidth="2.5"/>
        </svg>
      </div>
      <div style={{ fontSize: 18, fontWeight: 700, marginTop: 22 }}>网络连接异常</div>
      <div style={{ fontSize: 13, color: 'var(--text-3)', marginTop: 8, textAlign: 'center', maxWidth: 250 }}>
        请检查您的网络设置或稍后再试，醇品汇离不开你
      </div>
      <button className="btn-gold" style={{ marginTop: 28, padding: '0 36px' }}>
        <Icon name="reload" size={18} color="#3A1A00" style={{ marginRight: 6 }}/>重新加载
      </button>
      <div style={{ fontSize: 12, color: 'var(--text-3)', marginTop: 14 }}>错误代码: NET_ERR_CONNECTION_TIMEOUT</div>
    </div>
    <HomeIndicator/>
  </Phone>
);

// ============================================================
// H4. 未登录拦截
// ============================================================
const ScreenAuthGate = () => (
  <Phone>
    {/* Faint home behind */}
    <div style={{ background: 'var(--grad-brown)', height: 280, opacity: 0.85 }}>
      <StatusBar theme="dark"/>
      <div style={{ padding: '8px 16px', color: '#F6E7C2', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <div style={{ fontSize: 22, fontWeight: 700, color: '#FFE0A0' }}>醇品汇</div>
        <div style={{ display: 'flex', gap: 10 }}>
          <Icon name="cart" size={22} color="#FFD685"/>
        </div>
      </div>
    </div>
    <div style={{ position: 'absolute', inset: 0, background: 'rgba(0,0,0,0.55)' }}/>

    {/* Modal */}
    <div className="modal-mask">
      <div className="modal" style={{ padding: '24px 20px 0', textAlign: 'center' }}>
        <BrandMark size={56}/>
        <div style={{ fontSize: 17, fontWeight: 700, marginTop: 14 }}>请先登录</div>
        <div style={{ fontSize: 13, color: 'var(--text-2)', marginTop: 10, lineHeight: 1.7 }}>
          登录后可使用购买、分享获佣、提现等完整功能
        </div>
        <button className="btn-wechat" style={{ width: '100%', marginTop: 22 }}>
          <Icon name="wechat" size={20}/> 微信一键登录
        </button>
        <button className="btn-gold-outline" style={{ width: '100%', marginTop: 10, background: 'var(--cream-50)' }}>
          继续浏览
        </button>
        <div style={{ padding: '18px 0 14px', color: 'var(--text-3)', fontSize: 12 }}>
          已阅读并同意 <span style={{ color: 'var(--gold-500)' }}>《用户协议》</span> <span style={{ color: 'var(--gold-500)' }}>《隐私协议》</span>
        </div>
      </div>
    </div>
    <HomeIndicator dark/>
  </Phone>
);

// ============================================================
// H5. Toast (操作反馈)
// ============================================================
const ScreenToast = () => (
  <Phone>
    {/* Show cart behind */}
    <div style={{ background: '#fff' }}>
      <StatusBar theme="light"/>
      <div style={{ padding: '4px 16px 12px', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <div style={{ fontSize: 17, fontWeight: 600 }}>购物车 <span style={{ color: 'var(--text-3)', fontSize: 13, fontWeight: 400 }}>(3)</span></div>
        <div style={{ fontSize: 13, color: 'var(--text-2)' }}>管理</div>
      </div>
    </div>
    <div style={{ padding: '12px 16px' }}>
      {[0, 1].map(i => (
        <div key={i} style={{ background: '#fff', borderRadius: 12, padding: 12, marginBottom: 10, display: 'flex', gap: 10 }}>
          <div style={{ width: 80, height: 80, borderRadius: 8, overflow: 'hidden' }}>
            <ProdImg kind={i === 0 ? 'maotai' : 'wuliang'}/>
          </div>
          <div style={{ flex: 1, paddingTop: 6 }}>
            <div style={{ fontSize: 13, fontWeight: 500 }}>{i === 0 ? '茅台飞天 53度 500ml' : '五粮液普五 52度 500ml'}</div>
            <div style={{ fontSize: 11, color: 'var(--text-3)', marginTop: 6 }}>500ml 单瓶</div>
            <div className="price font-num" style={{ fontSize: 17, marginTop: 6 }}><span className="sym">¥</span>{i === 0 ? '2,980' : '1,380'}</div>
          </div>
        </div>
      ))}
    </div>

    {/* Toast samples in stack */}
    <div style={{ position: 'absolute', top: '38%', left: '50%', transform: 'translate(-50%, -50%)', display: 'flex', flexDirection: 'column', gap: 14, alignItems: 'center' }}>
      {/* success */}
      <div style={{
        background: 'rgba(20,15,5,0.85)', color: '#fff',
        borderRadius: 12, padding: '20px 28px',
        display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 10,
        backdropFilter: 'blur(10px)',
        minWidth: 130,
      }}>
        <div style={{ width: 44, height: 44, borderRadius: '50%', background: 'var(--grad-gold)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <Icon name="check" size={28} color="#fff" strokeWidth={3}/>
        </div>
        <div style={{ fontSize: 13 }}>已加入购物车</div>
      </div>

      {/* warn / info */}
      <div style={{
        background: 'rgba(20,15,5,0.85)', color: '#fff',
        borderRadius: 8, padding: '12px 18px',
        display: 'inline-flex', alignItems: 'center', gap: 8,
        fontSize: 13,
      }}>
        <Icon name="info" size={18} color="#FFD685"/>
        库存仅剩 3 件
      </div>

      {/* error */}
      <div style={{
        background: 'rgba(20,15,5,0.85)', color: '#fff',
        borderRadius: 8, padding: '12px 18px',
        display: 'inline-flex', alignItems: 'center', gap: 8,
        fontSize: 13,
      }}>
        <Icon name="close" size={16} color="#FF6A5C" strokeWidth={2.5}/>
        操作失败，请稍后再试
      </div>
    </div>

    <HomeIndicator/>
  </Phone>
);

// ============================================================
// H6. 确认弹窗
// ============================================================
const ScreenConfirm = () => (
  <Phone>
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

    <div className="modal-mask">
      <div className="modal">
        <div style={{ fontSize: 17, fontWeight: 700, marginBottom: 10 }}>确认取消订单？</div>
        <div style={{ fontSize: 13, color: 'var(--text-2)', lineHeight: 1.6 }}>
          取消后无法恢复，已使用的优惠券将原路退回，积分抵扣将立即返还。
        </div>
        <div className="modal-actions">
          <button>再想想</button>
          <button>确认取消</button>
        </div>
      </div>
    </div>
    <HomeIndicator dark/>
  </Phone>
);

// ============================================================
// H7. 操作面板 / Action Sheet
// ============================================================
const ScreenActionSheet = () => (
  <Phone>
    <div style={{ background: '#fff' }}>
      <StatusBar theme="light"/>
      <NavBar title="商品详情" theme="on-light" right={<Icon name="share" size={20}/>}/>
    </div>
    <div style={{ height: 280, background: '#0E0805' }}><ProdImg kind="maotai"/></div>
    <div style={{ position: 'absolute', inset: 0, background: 'rgba(0,0,0,0.45)' }}/>

    {/* Action sheet */}
    <div style={{
      position: 'absolute', left: 0, right: 0, bottom: 0,
      background: '#fff', borderRadius: '20px 20px 0 0',
      padding: '8px 0 calc(8px + var(--safe-bottom))',
    }}>
      {/* Header */}
      <div style={{ textAlign: 'center', padding: '12px 16px 14px', fontSize: 13, color: 'var(--text-3)' }}>
        请选择操作
      </div>

      {[
        { l: '加入购物车', i: 'cart', c: '#D38A00' },
        { l: '立即购买', i: 'card', c: '#D38A00' },
        { l: '分享获佣', i: 'share', c: '#D1467A', sub: '预估佣金 ¥596' },
        { l: '生成商品海报', i: 'qr', c: '#3A6FD1' },
        { l: '收藏商品', i: 'heart', c: '#9B8E7C' },
        { l: '举报商品', i: 'warn', c: '#D6453B', danger: true },
      ].map((a, i, arr) => (
        <div key={a.l} style={{ display: 'flex', alignItems: 'center', gap: 14, padding: '14px 20px', borderBottom: i < arr.length - 1 ? '1px solid var(--line-soft)' : 'none' }}>
          <div style={{ width: 36, height: 36, borderRadius: '50%', background: a.c + '18', color: a.c, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <Icon name={a.i} size={20} color={a.c}/>
          </div>
          <div style={{ flex: 1 }}>
            <div style={{ fontSize: 15, fontWeight: 500, color: a.danger ? '#D6453B' : 'var(--text-1)' }}>{a.l}</div>
            {a.sub && <div style={{ fontSize: 11, color: 'var(--gold-500)', marginTop: 2 }}>{a.sub}</div>}
          </div>
          <Icon name="chevron" size={16} color="#C9BFA9"/>
        </div>
      ))}

      <div style={{ height: 8, background: 'var(--bg-app)' }}/>
      <div style={{ textAlign: 'center', padding: '16px 0 4px', fontSize: 16, fontWeight: 500, color: 'var(--text-2)' }}>取消</div>
      <HomeIndicator/>
    </div>
  </Phone>
);

Object.assign(window, {
  ScreenLoading, ScreenEmpty, ScreenNetError,
  ScreenAuthGate, ScreenToast, ScreenConfirm, ScreenActionSheet,
});
