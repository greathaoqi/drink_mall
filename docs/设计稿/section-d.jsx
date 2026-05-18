// section-d.jsx — Orders

// ============================================================
// D1. 我的订单 (with tabs)
// ============================================================
const ScreenOrders = () => (
  <Phone>
    <div style={{ background: '#fff' }}>
      <StatusBar theme="light"/>
      <NavBar title="我的订单" theme="on-light"/>
      <div style={{ display: 'flex', borderBottom: '1px solid var(--line-soft)', padding: '0 8px' }}>
        {[
          { l: '全部' },
          { l: '待付款', n: 1, a: true },
          { l: '待发货', n: 2 },
          { l: '待收货' },
          { l: '已完成' },
          { l: '售后' },
        ].map(t => (
          <div key={t.l} style={{
            flex: 1, textAlign: 'center', padding: '12px 0',
            fontSize: 13, fontWeight: t.a ? 600 : 400,
            color: t.a ? 'var(--gold-500)' : 'var(--text-2)',
            position: 'relative',
          }}>
            {t.l}{t.n ? <sup style={{ fontSize: 9, color: '#D6453B', marginLeft: 2 }}>{t.n}</sup> : ''}
            {t.a && <div style={{ position: 'absolute', bottom: 0, left: '50%', transform: 'translateX(-50%)', width: 22, height: 2, background: 'var(--grad-gold)', borderRadius: 2 }}/>}
          </div>
        ))}
      </div>
    </div>

    <div style={{ padding: '12px 16px 30px', overflow: 'auto', height: 'calc(100% - 88px - 44px)' }}>
      {[
        {
          id: 'CPH202605161641892', state: '待付款', stateColor: '#D6453B',
          items: [
            { n: '茅台飞天 53度 500ml', p: '2,980', q: 2, k: 'maotai' },
            { n: '五粮液普五 52度 500ml', p: '1,380', q: 1, k: 'wuliang' },
          ],
          total: '7,027.20', count: 3,
          actions: [['取消订单', false], ['立即支付', true]],
          time: '14:59 后取消',
        },
        {
          id: 'CPH202605151235012', state: '待发货', stateColor: '#D38A00',
          items: [
            { n: '法国波尔多干红 750ml', p: '298', q: 2, k: 'redwine' },
          ],
          total: '596.00', count: 2,
          actions: [['提醒发货', false]],
          time: '已付款 · 备货中',
        },
        {
          id: 'CPH202605141156234', state: '待收货', stateColor: '#3A6FD1',
          items: [
            { n: '青花郎20 53度 500ml', p: '1,188', q: 1, k: 'redwine' },
          ],
          total: '1,188.00', count: 1,
          actions: [['查看物流', false], ['确认收货', true]],
          time: '已发货 · 顺丰快递',
        },
        {
          id: 'CPH202605101015487', state: '已完成', stateColor: '#9B8E7C',
          items: [
            { n: '威士忌 12年 700ml', p: '688', q: 1, k: 'whisky' },
          ],
          total: '688.00', count: 1,
          actions: [['再次购买', false], ['申请售后', false]],
          time: '佣金已到账 ¥137.60',
        },
      ].map((o, i) => (
        <div key={i} style={{ background: '#fff', borderRadius: 12, padding: '14px 14px', marginBottom: 12, boxShadow: 'var(--shadow-card)' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div style={{ fontSize: 12, color: 'var(--text-3)' }}>订单号 {o.id.substring(0, 17)}…</div>
            <div style={{ fontSize: 13, fontWeight: 600, color: o.stateColor }}>{o.state}</div>
          </div>
          <div style={{ marginTop: 12 }}>
            {o.items.map((p, j) => (
              <div key={j} style={{ display: 'flex', gap: 10, marginBottom: 8 }}>
                <div style={{ width: 56, height: 56, borderRadius: 6, overflow: 'hidden', flexShrink: 0 }}><ProdImg kind={p.k}/></div>
                <div style={{ flex: 1, minWidth: 0 }}>
                  <div style={{ fontSize: 13, fontWeight: 500, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{p.n}</div>
                  <div style={{ fontSize: 11, color: 'var(--text-3)', marginTop: 2 }}>500ml 单瓶</div>
                </div>
                <div style={{ textAlign: 'right', flexShrink: 0 }}>
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
    </div>
    <HomeIndicator/>
  </Phone>
);

// ============================================================
// D2. 订单详情
// ============================================================
const ScreenOrderDetail = () => (
  <Phone>
    <div style={{ background: 'var(--grad-brown)', color: '#F6E7C2', paddingBottom: 30 }}>
      <StatusBar theme="dark"/>
      <NavBar title="订单详情" theme="on-brown"/>
      <div style={{ padding: '8px 20px 0' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
          <div style={{ width: 44, height: 44, borderRadius: '50%', background: 'rgba(228,165,22,0.18)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <Icon name="truck" size={24} color="#FFD685"/>
          </div>
          <div>
            <div style={{ fontSize: 18, fontWeight: 700, color: '#FFE0A0' }}>已发货 · 配送中</div>
            <div style={{ fontSize: 12, color: 'rgba(246,231,194,0.7)', marginTop: 2 }}>预计 5 月 18 日送达</div>
          </div>
        </div>
      </div>
    </div>

    <div style={{ overflow: 'auto', height: 'calc(100% - 130px - 70px)', padding: '12px 16px 14px' }}>
      {/* Address */}
      <div style={{ background: '#fff', borderRadius: 12, padding: 14, display: 'flex', alignItems: 'flex-start', gap: 10, marginTop: -8 }}>
        <Icon name="pin" size={20} color="#D38A00"/>
        <div style={{ flex: 1 }}>
          <div style={{ fontSize: 14, fontWeight: 600 }}>李明远 138 **** 8888</div>
          <div style={{ fontSize: 13, color: 'var(--text-2)', marginTop: 4 }}>北京市朝阳区建国门外大街1号国贸大厦A座 28层2801室</div>
        </div>
      </div>

      {/* Items */}
      <div style={{ background: '#fff', borderRadius: 12, padding: '12px 14px', marginTop: 10 }}>
        {[
          { n: '茅台飞天 53度 500ml', p: '2,980', q: 2, k: 'maotai' },
          { n: '五粮液普五 52度 500ml', p: '1,380', q: 1, k: 'wuliang' },
        ].map((p, i) => (
          <div key={i} style={{ display: 'flex', gap: 10, padding: '8px 0', borderTop: i > 0 ? '1px solid var(--line-soft)' : 'none' }}>
            <div style={{ width: 60, height: 60, borderRadius: 6, overflow: 'hidden' }}><ProdImg kind={p.k}/></div>
            <div style={{ flex: 1 }}>
              <div style={{ fontSize: 13, fontWeight: 500 }}>{p.n}</div>
              <div style={{ fontSize: 11, color: 'var(--text-3)', marginTop: 2 }}>500ml 单瓶</div>
            </div>
            <div style={{ textAlign: 'right' }}>
              <div className="price font-num" style={{ fontSize: 14 }}><span className="sym">¥</span>{p.p}</div>
              <div style={{ fontSize: 11, color: 'var(--text-3)' }}>×{p.q}</div>
            </div>
          </div>
        ))}
      </div>

      {/* Price */}
      <div style={{ background: '#fff', borderRadius: 12, padding: 14, marginTop: 10, fontSize: 13 }}>
        {[
          ['商品总额', '¥7,340.00'],
          ['运费', '免运费'],
          ['优惠券', '-¥300.00'],
          ['积分抵扣', '-¥12.80'],
          ['实付金额', <span className="price font-num" style={{ fontSize: 18 }}><span className="sym">¥</span>7,027.20</span>],
        ].map(([k, v], i) => (
          <div key={i} style={{ display: 'flex', justifyContent: 'space-between', padding: '4px 0' }}>
            <div className="text-2">{k}</div>
            <div className={typeof v === 'string' ? 'font-num' : ''}>{v}</div>
          </div>
        ))}
      </div>

      {/* Meta */}
      <div style={{ background: '#fff', borderRadius: 12, padding: 14, marginTop: 10, fontSize: 13 }}>
        {[
          ['订单编号', 'CPH202605141156234', '复制'],
          ['下单时间', '2026-05-14 11:56:23'],
          ['付款时间', '2026-05-14 11:57:01'],
          ['发货时间', '2026-05-15 09:23:48'],
          ['配送方式', '顺丰速运'],
          ['运单号', 'SF1238495027341', '复制'],
        ].map(([k, v, action], i) => (
          <div key={i} style={{ display: 'flex', padding: '5px 0', color: 'var(--text-2)' }}>
            <div style={{ width: 90, color: 'var(--text-3)' }}>{k}</div>
            <div className="font-num" style={{ flex: 1, color: 'var(--text-1)' }}>{v}</div>
            {action && <div style={{ color: 'var(--gold-500)', fontSize: 12 }}>{action}</div>}
          </div>
        ))}
      </div>

      {/* Commission */}
      <div style={{ marginTop: 10, padding: '12px 14px', background: 'var(--gold-50)', borderRadius: 10, fontSize: 12, color: '#7A5610' }}>
        <Icon name="coin" size={16} color="#D38A00" style={{ verticalAlign: 'middle', marginRight: 4 }}/>
        预估佣金 <span style={{ fontWeight: 700, color: '#D38A00' }}>¥1,405.44</span> · 确认收货后 7 天到账
      </div>
    </div>

    <div style={{ position: 'absolute', left: 0, right: 0, bottom: 0, background: '#fff', borderTop: '1px solid var(--line-soft)', padding: '12px 14px 26px', display: 'flex', gap: 10, justifyContent: 'flex-end' }}>
      <button className="btn-gold-outline" style={{ height: 38, fontSize: 13, padding: '0 16px' }}>联系客服</button>
      <button className="btn-gold-outline" style={{ height: 38, fontSize: 13, padding: '0 16px' }}>查看物流</button>
      <button className="btn-gold" style={{ height: 38, fontSize: 13, padding: '0 18px' }}>确认收货</button>
    </div>
  </Phone>
);

// ============================================================
// D3. 物流详情
// ============================================================
const ScreenLogistics = () => (
  <Phone>
    <div style={{ background: '#fff' }}>
      <StatusBar theme="light"/>
      <NavBar title="物流详情" theme="on-light"/>
    </div>
    <div style={{ background: 'var(--bg-app)', padding: 16, overflow: 'auto', height: 'calc(100% - 88px)' }}>
      {/* Logistics card */}
      <div style={{ background: '#fff', borderRadius: 12, padding: 14, display: 'flex', alignItems: 'center', gap: 12 }}>
        <div style={{ width: 56, height: 56, borderRadius: 8, background: 'var(--cream-100)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <Icon name="truck" size={28} color="#D38A00"/>
        </div>
        <div style={{ flex: 1 }}>
          <div style={{ fontSize: 14, fontWeight: 600 }}>顺丰速运 <span className="font-num" style={{ fontWeight: 400, fontSize: 12, color: 'var(--text-3)' }}>SF1238495027341</span></div>
          <div style={{ fontSize: 12, color: 'var(--text-3)', marginTop: 4 }}>3 件商品 · 预计明日送达</div>
        </div>
        <div style={{ fontSize: 12, color: 'var(--gold-500)' }}>复制</div>
      </div>

      {/* Timeline */}
      <div style={{ background: '#fff', borderRadius: 12, padding: '14px 16px', marginTop: 10 }}>
        {[
          { d: '今日', t: '14:23', txt: '【北京朝阳】快件已到达 朝阳建国门营业点 (派件员: 张师傅 137****8821)', active: true },
          { d: '今日', t: '08:42', txt: '【北京朝阳】快件已到达 朝阳分拣中心' },
          { d: '昨日', t: '23:18', txt: '【北京顺义】快件已离开 北京顺义中转场' },
          { d: '昨日', t: '19:05', txt: '【北京顺义】快件已到达 北京顺义中转场' },
          { d: '5/15', t: '15:32', txt: '【贵州茅台】快件已离开 茅台镇营业点' },
          { d: '5/15', t: '09:23', txt: '【贵州茅台】醇品汇仓库已发货' },
        ].map((s, i) => (
          <div key={i} style={{ display: 'flex', gap: 10, paddingBottom: 14, position: 'relative' }}>
            <div style={{ width: 50, flexShrink: 0, fontSize: 11, color: 'var(--text-3)', textAlign: 'right', paddingTop: 1 }}>
              <div>{s.d}</div>
              <div>{s.t}</div>
            </div>
            <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', flexShrink: 0 }}>
              <div style={{
                width: s.active ? 14 : 8, height: s.active ? 14 : 8, borderRadius: '50%',
                background: s.active ? 'var(--grad-gold)' : 'var(--line)',
                boxShadow: s.active ? '0 0 0 4px rgba(228,165,22,0.18)' : 'none',
                marginTop: 4,
              }}/>
              <div style={{ flex: 1, width: 1, background: 'var(--line)', minHeight: 16 }}/>
            </div>
            <div style={{
              fontSize: 13, color: s.active ? 'var(--text-1)' : 'var(--text-2)',
              fontWeight: s.active ? 600 : 400, lineHeight: 1.5, paddingTop: 1, flex: 1,
            }}>
              {s.txt}
            </div>
          </div>
        ))}
      </div>

      <button className="btn-gold-outline" style={{ width: '100%', marginTop: 12 }}>
        <Icon name="headset" size={18} color="#D38A00" style={{ marginRight: 4 }}/>联系快递员
      </button>
    </div>
    <HomeIndicator/>
  </Phone>
);

// ============================================================
// D4. 申请售后
// ============================================================
const ScreenAfterSale = () => (
  <Phone>
    <div style={{ background: '#fff' }}>
      <StatusBar theme="light"/>
      <NavBar title="申请售后" theme="on-light"/>
    </div>
    <div style={{ background: 'var(--bg-app)', overflow: 'auto', height: 'calc(100% - 88px - 80px)', padding: '12px 0' }}>
      {/* Item */}
      <div style={{ background: '#fff', padding: '12px 16px', display: 'flex', gap: 10 }}>
        <div style={{ width: 60, height: 60, borderRadius: 6, overflow: 'hidden' }}><ProdImg kind="whisky"/></div>
        <div style={{ flex: 1 }}>
          <div style={{ fontSize: 13, fontWeight: 500 }}>单一麦芽威士忌 12年 700ml</div>
          <div style={{ fontSize: 11, color: 'var(--text-3)', marginTop: 2 }}>700ml 单瓶 ×1</div>
          <div className="price font-num" style={{ fontSize: 15, marginTop: 4 }}><span className="sym">¥</span>688</div>
        </div>
      </div>

      {/* Type */}
      <div style={{ background: '#fff', marginTop: 10 }}>
        <div className="form-row">
          <div className="label">售后类型</div>
          <div className="field" style={{ color: 'var(--text-1)' }}>仅退款（未收到货）</div>
          <Icon name="chevron" size={14} color="#C9BFA9"/>
        </div>
        <div className="form-row">
          <div className="label">退款金额</div>
          <div className="field price font-num" style={{ fontSize: 16 }}><span className="sym">¥</span>688.00</div>
        </div>
        <div className="form-row" style={{ borderBottom: 'none' }}>
          <div className="label">退款原因</div>
          <div className="field" style={{ color: 'var(--text-3)' }}>请选择</div>
          <Icon name="chevron" size={14} color="#C9BFA9"/>
        </div>
      </div>

      <div style={{ background: '#fff', marginTop: 10, padding: '14px 16px' }}>
        <div style={{ fontSize: 13, fontWeight: 600, marginBottom: 8 }}>问题描述</div>
        <textarea rows="4" placeholder="请详细描述遇到的问题，便于客服更快为您处理…" style={{
          width: '100%', resize: 'none', border: 'none', outline: 'none',
          background: 'var(--bg-app)', borderRadius: 8, padding: 10, fontSize: 13, fontFamily: 'inherit',
        }}/>
        <div style={{ fontSize: 11, color: 'var(--text-3)', textAlign: 'right' }}>0/200</div>

        <div style={{ fontSize: 13, fontWeight: 600, margin: '10px 0 8px' }}>上传凭证</div>
        <div style={{ display: 'flex', gap: 10 }}>
          {[0, 1].map(i => (
            <div key={i} style={{ width: 70, height: 70, borderRadius: 8, background: 'var(--bg-app)', position: 'relative' }}>
              <div style={{ width: '100%', height: '100%', background: 'linear-gradient(135deg, #C4322B, #8B1410)', borderRadius: 8 }}/>
              <div style={{ position: 'absolute', top: -6, right: -6, width: 18, height: 18, borderRadius: '50%', background: '#1A1006', color: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <Icon name="close" size={10} color="#fff" strokeWidth={2.5}/>
              </div>
            </div>
          ))}
          <div style={{
            width: 70, height: 70, borderRadius: 8, border: '1.5px dashed var(--line)',
            display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center',
            color: 'var(--text-3)', fontSize: 11, gap: 2,
          }}>
            <Icon name="plus" size={20} color="#9B8E7C"/>
            <div>添加 (2/5)</div>
          </div>
        </div>
      </div>
    </div>
    <div style={{ position: 'absolute', left: 16, right: 16, bottom: 30 }}>
      <button className="btn-gold" style={{ width: '100%' }}>提交申请</button>
    </div>
    <HomeIndicator/>
  </Phone>
);

// ============================================================
// D5. 售后详情
// ============================================================
const ScreenAfterSaleDetail = () => (
  <Phone>
    <div style={{ background: 'var(--grad-brown)', color: '#F6E7C2', paddingBottom: 24 }}>
      <StatusBar theme="dark"/>
      <NavBar title="售后详情" theme="on-brown"/>
      <div style={{ padding: '4px 20px' }}>
        <div style={{ fontSize: 18, fontWeight: 700, color: '#FFE0A0' }}>退款处理中</div>
        <div style={{ fontSize: 12, color: 'rgba(246,231,194,0.7)', marginTop: 4 }}>客服正在审核您的售后申请，预计 24 小时内回复</div>
      </div>
    </div>
    <div style={{ padding: '0 16px 16px', overflow: 'auto', height: 'calc(100% - 130px)' }}>
      {/* Progress */}
      <div style={{ background: '#fff', borderRadius: 12, padding: '16px 16px', marginTop: -6, boxShadow: 'var(--shadow-card)' }}>
        <div style={{ display: 'flex', alignItems: 'flex-start' }}>
          {[
            { l: '提交申请', t: '5/16 14:20', a: true },
            { l: '商家审核', t: '审核中…', a: true, current: true },
            { l: '退款到账', t: '待处理' },
          ].map((s, i, arr) => (
            <div key={i} style={{ flex: 1, textAlign: 'center', position: 'relative' }}>
              {i < arr.length - 1 && <div style={{ position: 'absolute', top: 12, left: '60%', right: '-40%', height: 2, background: s.a ? 'var(--gold-500)' : 'var(--line)' }}/>}
              <div style={{
                width: 26, height: 26, borderRadius: '50%',
                background: s.current ? 'var(--grad-gold)' : s.a ? 'var(--gold-500)' : 'var(--line)',
                margin: '0 auto', display: 'flex', alignItems: 'center', justifyContent: 'center',
                color: '#fff', position: 'relative',
                boxShadow: s.current ? '0 0 0 5px rgba(228,165,22,0.18)' : 'none',
              }}>
                {s.a ? <Icon name="check" size={14} color="#fff" strokeWidth={3}/> : null}
              </div>
              <div style={{ fontSize: 12, marginTop: 6, color: s.a ? 'var(--text-1)' : 'var(--text-3)', fontWeight: s.current ? 600 : 400 }}>{s.l}</div>
              <div style={{ fontSize: 10, color: 'var(--text-3)', marginTop: 2 }}>{s.t}</div>
            </div>
          ))}
        </div>
      </div>

      <div style={{ background: '#fff', borderRadius: 12, padding: 14, marginTop: 10, display: 'flex', gap: 10 }}>
        <div style={{ width: 60, height: 60, borderRadius: 6, overflow: 'hidden' }}><ProdImg kind="whisky"/></div>
        <div style={{ flex: 1 }}>
          <div style={{ fontSize: 13, fontWeight: 500 }}>单一麦芽威士忌 12年 700ml</div>
          <div style={{ fontSize: 11, color: 'var(--text-3)', marginTop: 2 }}>700ml 单瓶 ×1</div>
        </div>
        <div className="price font-num" style={{ fontSize: 14 }}><span className="sym">¥</span>688</div>
      </div>

      <div style={{ background: '#fff', borderRadius: 12, padding: 14, marginTop: 10, fontSize: 13 }}>
        {[
          ['服务单号', 'SH202605161420029'],
          ['售后类型', '仅退款 (未收到货)'],
          ['退款金额', '¥688.00'],
          ['申请时间', '2026-05-16 14:20:12'],
          ['退款原因', '物流长时间未更新'],
        ].map(([k, v]) => (
          <div key={k} style={{ display: 'flex', padding: '5px 0' }}>
            <div style={{ width: 90, color: 'var(--text-3)' }}>{k}</div>
            <div style={{ flex: 1 }} className="font-num">{v}</div>
          </div>
        ))}
        <div style={{ padding: '8px 0', color: 'var(--text-3)' }}>问题描述</div>
        <div style={{ padding: '8px 12px', background: 'var(--bg-app)', borderRadius: 6, color: 'var(--text-2)', fontSize: 12, lineHeight: 1.6 }}>
          下单已 6 天，物流轨迹一直停留在「贵州茅台」未更新，联系快递无果，请协助处理退款。
        </div>
      </div>

      <div style={{ display: 'flex', gap: 10, marginTop: 14 }}>
        <button className="btn-gold-outline" style={{ flex: 1 }}>撤销申请</button>
        <button className="btn-gold" style={{ flex: 1 }}>
          <Icon name="headset" size={16} color="#3A1A00" style={{ marginRight: 4 }}/>联系客服
        </button>
      </div>
    </div>
    <HomeIndicator/>
  </Phone>
);

Object.assign(window, { ScreenOrders, ScreenOrderDetail, ScreenLogistics, ScreenAfterSale, ScreenAfterSaleDetail });
