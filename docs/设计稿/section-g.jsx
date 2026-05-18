// section-g.jsx — My / Profile

// ============================================================
// G1. 个人中心
// ============================================================
const ScreenProfile = () => (
  <Phone>
    {/* Brown header */}
    <div style={{
      background: 'var(--grad-brown)',
      backgroundImage: 'radial-gradient(ellipse 80% 60% at 30% 20%, rgba(228,165,22,0.18), transparent 60%), var(--grad-brown)',
      color: '#F6E7C2', paddingBottom: 30, position: 'relative',
    }}>
      <StatusBar theme="dark"/>
      <div style={{ display: 'flex', justifyContent: 'flex-end', padding: '4px 16px 0' }}>
        <Icon name="gear" size={22} color="#FFD685"/>
      </div>
      <div style={{ display: 'flex', alignItems: 'center', padding: '14px 20px 0', gap: 14 }}>
        <Avatar size={64} kind="boss" seed="李"/>
        <div>
          <div style={{ fontSize: 20, fontWeight: 700, color: '#FFE0A0' }}>李明远</div>
          <div style={{ fontSize: 12, color: 'rgba(246,231,194,0.7)', marginTop: 4 }}>138 **** 8888</div>
          <div style={{ marginTop: 6 }}>
            <div className="tag tag-gold-solid" style={{ fontSize: 11 }}>县级联营商 · 生态合伙人</div>
          </div>
        </div>
      </div>
    </div>

    {/* Stats card overlay */}
    <div style={{ margin: '0 16px', marginTop: -22, position: 'relative', zIndex: 2,
      background: '#fff', borderRadius: 14, padding: '16px 0',
      display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)',
      boxShadow: 'var(--shadow-card)',
    }}>
      {[
        { l: '可提现余额', v: '¥24,860', c: 'var(--gold-500)' },
        { l: '团队业绩', v: '¥58,400' },
        { l: '积分', v: '1,280' },
      ].map((s, i) => (
        <div key={s.l} style={{ textAlign: 'center', borderLeft: i > 0 ? '1px solid var(--line-soft)' : 'none' }}>
          <div className="font-num" style={{ fontSize: 18, fontWeight: 700, color: s.c || 'var(--text-1)' }}>{s.v}</div>
          <div style={{ fontSize: 11, color: 'var(--text-3)', marginTop: 4 }}>{s.l}</div>
        </div>
      ))}
    </div>

    <div style={{ overflow: 'auto', height: 'calc(100% - 200px - 76px - 30px)', padding: '14px 16px' }}>
      {/* My orders */}
      <div style={{ background: '#fff', borderRadius: 12, padding: 14, marginBottom: 12 }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 16 }}>
          <div style={{ fontSize: 15, fontWeight: 700 }}>我的订单</div>
          <div style={{ fontSize: 12, color: 'var(--gold-500)' }}>全部订单 ›</div>
        </div>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(5, 1fr)' }}>
          {[
            { l: '待付款', i: 'card', n: 1 },
            { l: '待发货', i: 'box', n: 2 },
            { l: '待收货', i: 'truck' },
            { l: '已完成', i: 'check-circle' },
            { l: '售后', i: 'headset' },
          ].map(s => (
            <div key={s.l} style={{ textAlign: 'center', position: 'relative' }}>
              <div style={{ display: 'flex', justifyContent: 'center' }}>
                <Icon name={s.i} size={28} color="#5E5142" strokeWidth={1.5}/>
                {s.n && <div style={{ position: 'absolute', top: -4, right: 'calc(50% - 22px)', background: '#D6453B', color: '#fff', fontSize: 10, padding: '0 5px', height: 16, borderRadius: 8, display: 'flex', alignItems: 'center', minWidth: 16, justifyContent: 'center' }}>{s.n}</div>}
              </div>
              <div style={{ fontSize: 11, marginTop: 6, color: 'var(--text-2)' }}>{s.l}</div>
            </div>
          ))}
        </div>
      </div>

      {/* My assets */}
      <div style={{ background: '#fff', borderRadius: 12, padding: 14, marginBottom: 12 }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 12 }}>
          <div style={{ fontSize: 15, fontWeight: 700 }}>我的资产</div>
          <div style={{ fontSize: 12, color: 'var(--gold-500)' }}>提现 ›</div>
        </div>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: 8 }}>
          {[
            { l: '可提现余额', v: '¥24,860', bg: '#FBE9B6', fg: '#7A5610' },
            { l: '冻结余额',   v: '¥3,200',  bg: '#EAE5D6', fg: '#7A6F58' },
            { l: 'DF 余额',    v: '¥1,500',  bg: '#DCE5FA', fg: '#3A6FD1' },
            { l: '酒豆',       v: '6,480',   bg: '#F6DDD0', fg: '#A33616' },
            { l: '积分',       v: '1,280',   bg: '#FCDDE6', fg: '#D1467A' },
            { l: '优惠券',     v: '3 张',    bg: '#E6F0DF', fg: '#3F7A2A' },
          ].map(a => (
            <div key={a.l} style={{ background: a.bg, borderRadius: 10, padding: '12px 12px' }}>
              <div className="font-num" style={{ fontSize: 16, fontWeight: 700, color: a.fg }}>{a.v}</div>
              <div style={{ fontSize: 11, color: a.fg, opacity: 0.85, marginTop: 2 }}>{a.l}</div>
            </div>
          ))}
        </div>
      </div>

      {/* Menu */}
      <div style={{ background: '#fff', borderRadius: 12 }}>
        <div className="row-link">
          <div className="row-icon" style={{ background: '#DCE5FA', color: '#3A6FD1' }}><Icon name="people" size={18} color="#3A6FD1"/></div>
          <div>团队管理</div>
          <Icon name="chevron" size={16} className="row-arrow" color="#C9BFA9"/>
        </div>
        <div className="row-link">
          <div className="row-icon"><Icon name="share" size={18} color="#D38A00"/></div>
          <div>分享邀请</div>
          <Icon name="chevron" size={16} className="row-arrow" color="#C9BFA9"/>
        </div>
        <div className="row-link">
          <div className="row-icon" style={{ background: '#FCDDE6', color: '#D1467A' }}><Icon name="ticket" size={18} color="#D1467A"/></div>
          <div>优惠券</div>
          <div className="row-val">3 张可用</div>
          <Icon name="chevron" size={16} color="#C9BFA9"/>
        </div>
        <div className="row-link" style={{ borderBottom: 'none' }}>
          <div className="row-icon" style={{ background: '#D9F0E0', color: '#2F9E5C' }}><Icon name="headset" size={18} color="#2F9E5C"/></div>
          <div>联系客服</div>
          <Icon name="chevron" size={16} className="row-arrow" color="#C9BFA9"/>
        </div>
      </div>
    </div>
    <TabBar active="me"/>
  </Phone>
);

// ============================================================
// G2. 我的资产
// ============================================================
const ScreenAssets = () => (
  <Phone>
    <div style={{ background: 'var(--grad-brown)', color: '#F6E7C2', paddingBottom: 30 }}>
      <StatusBar theme="dark"/>
      <NavBar title="我的资产" theme="on-brown" right={<span style={{ fontSize: 13, color: '#FFD685' }}>明细</span>}/>
      <div style={{ padding: '8px 24px 0' }}>
        <div style={{ fontSize: 12, color: 'rgba(246,231,194,0.6)' }}>账户总资产 (元)</div>
        <div className="font-num" style={{ fontSize: 38, fontWeight: 700, color: '#FFE0A0', marginTop: 4 }}>29,624.80</div>
        <div style={{ fontSize: 11, color: 'rgba(246,231,194,0.55)', marginTop: 4 }}>含可提现、冻结、DF、酒豆、积分等</div>
      </div>
    </div>

    <div style={{ padding: '0 16px 24px', overflow: 'auto', height: 'calc(100% - 180px)' }}>
      {/* Cards stack */}
      <div style={{ marginTop: -16, display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
        {[
          { l: '可提现余额', v: '24,860.00', unit: '¥', bg: 'linear-gradient(135deg, #FFD646, #D18A00)', fg: '#3A1A00', sub: '可立即提现' },
          { l: '冻结余额',   v: '3,200.00',  unit: '¥', bg: 'linear-gradient(135deg, #4A3F30, #2A2018)', fg: '#FFE0A0', sub: '待结算订单' },
          { l: 'DF 余额',    v: '1,500.00',  unit: '¥', bg: 'linear-gradient(135deg, #3A6FD1, #1F4B9A)', fg: '#fff', sub: '可消费 · 可赠送' },
          { l: '酒豆',       v: '6,480',     bg: 'linear-gradient(135deg, #C4322B, #771810)', fg: '#fff', sub: '100 豆 = ¥1' },
          { l: '积分',       v: '1,280',     bg: 'linear-gradient(135deg, #D1467A, #99294F)', fg: '#fff', sub: '可抵 ¥12.80' },
          { l: '优惠券',     v: '3',         unit: '', suffix: ' 张', bg: 'linear-gradient(135deg, #6BA45C, #3F7A2A)', fg: '#fff', sub: '其中 1 张即将过期' },
        ].map(a => (
          <div key={a.l} style={{ background: a.bg, borderRadius: 12, padding: 14, color: a.fg, position: 'relative', overflow: 'hidden' }}>
            <div style={{ position: 'absolute', top: -16, right: -16, width: 60, height: 60, borderRadius: '50%', background: 'rgba(255,255,255,0.15)' }}/>
            <div style={{ fontSize: 11, opacity: 0.85 }}>{a.l}</div>
            <div className="font-num" style={{ fontSize: 22, fontWeight: 700, marginTop: 6 }}>
              {a.unit !== undefined ? a.unit : ''}{a.v}{a.suffix || ''}
            </div>
            <div style={{ fontSize: 10, opacity: 0.7, marginTop: 4 }}>{a.sub}</div>
          </div>
        ))}
      </div>

      {/* Actions */}
      <div style={{ background: '#fff', borderRadius: 12, padding: '14px 0', marginTop: 12, display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', rowGap: 14 }}>
        {[
          { l: '提现', i: 'wallet', c: '#D38A00' },
          { l: '余额明细', i: 'coin', c: '#3A6FD1' },
          { l: '酒豆明细', i: 'gift', c: '#A33616' },
          { l: '积分明细', i: 'star', c: '#D1467A' },
          { l: 'DF 赠送', i: 'share', c: '#3A6FD1' },
          { l: 'DF 记录', i: 'people', c: '#3A6FD1' },
          { l: '充值',     i: 'plus', c: '#2F9E5C' },
          { l: '兑换商品', i: 'shop', c: '#A33616' },
        ].map(a => (
          <div key={a.l} style={{ textAlign: 'center' }}>
            <div style={{
              width: 40, height: 40, margin: '0 auto', borderRadius: '50%',
              background: a.c + '18', color: a.c,
              display: 'flex', alignItems: 'center', justifyContent: 'center',
            }}>
              <Icon name={a.i} size={20} color={a.c}/>
            </div>
            <div style={{ fontSize: 11, marginTop: 6 }}>{a.l}</div>
          </div>
        ))}
      </div>

      {/* Recent activity */}
      <div style={{ marginTop: 14 }}>
        <div style={{ fontSize: 14, fontWeight: 700, marginBottom: 10 }}>最近变动</div>
        <div style={{ background: '#fff', borderRadius: 12 }}>
          {[
            { t: '佣金到账 · 茅台飞天', amt: '+596.00', date: '今日 14:32', sub: '可提现余额' },
            { t: '提现到微信钱包', amt: '-10,000.00', date: '5/15 09:32', sub: '可提现余额', neg: true },
            { t: '招商奖励 · 王女士入驻', amt: '+1,200.00', date: '5/14 11:08', sub: '可提现余额' },
            { t: '订单使用积分抵扣', amt: '-128', date: '5/14 11:05', sub: '积分', neg: true },
          ].map((r, i) => (
            <div key={i} style={{ display: 'flex', alignItems: 'center', padding: '12px 16px', borderBottom: i < 3 ? '1px solid var(--line-soft)' : 'none' }}>
              <div style={{ flex: 1 }}>
                <div style={{ fontSize: 13, fontWeight: 500 }}>{r.t}</div>
                <div style={{ fontSize: 11, color: 'var(--text-3)', marginTop: 4 }}>{r.date} · {r.sub}</div>
              </div>
              <div className="font-num" style={{ fontSize: 15, fontWeight: 700, color: r.neg ? 'var(--text-1)' : 'var(--gold-500)' }}>{r.amt}</div>
            </div>
          ))}
        </div>
      </div>
    </div>
    <HomeIndicator/>
  </Phone>
);

// ============================================================
// G3. 余额明细
// ============================================================
const ScreenBalanceLog = () => (
  <Phone>
    <div style={{ background: '#fff' }}>
      <StatusBar theme="light"/>
      <NavBar title="余额明细" theme="on-light" right={<Icon name="filter" size={20}/>}/>
    </div>
    <div style={{ background: 'var(--grad-gold)', margin: '0 16px', borderRadius: 12, padding: '14px 18px', color: '#3A1A00' }}>
      <div style={{ fontSize: 12, color: '#7A4810' }}>可提现余额</div>
      <div className="font-num" style={{ fontSize: 30, fontWeight: 700, marginTop: 2 }}>¥24,860.00</div>
      <div style={{ display: 'flex', gap: 18, marginTop: 10, fontSize: 12 }}>
        <div>本月收入 <span className="font-num" style={{ fontWeight: 700 }}>+¥6,452</span></div>
        <div>本月支出 <span className="font-num" style={{ fontWeight: 700 }}>-¥10,000</span></div>
      </div>
    </div>

    <div style={{ padding: '14px 16px 4px', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
      <div style={{ display: 'flex', gap: 6 }}>
        {['全部', '收入', '支出'].map((c, i) => (
          <div key={c} style={{
            padding: '4px 12px', borderRadius: 999,
            background: i === 0 ? 'var(--gold-50)' : 'transparent',
            border: i === 0 ? '1px solid var(--gold-300)' : '1px solid var(--line)',
            color: i === 0 ? 'var(--gold-600)' : 'var(--text-2)',
            fontSize: 12, fontWeight: i === 0 ? 600 : 400,
          }}>{c}</div>
        ))}
      </div>
      <div style={{ fontSize: 12, color: 'var(--text-2)', display: 'flex', alignItems: 'center', gap: 4 }}>
        2026.05 <Icon name="chevron-down" size={14} color="#5E5142"/>
      </div>
    </div>

    <div style={{ overflow: 'auto', height: 'calc(100% - 88px - 110px - 50px)', padding: '6px 16px 24px' }}>
      <div style={{ fontSize: 12, color: 'var(--text-3)', margin: '8px 0' }}>今日</div>
      <div style={{ background: '#fff', borderRadius: 12 }}>
        {[
          { t: '佣金到账', sub: '茅台飞天 53度 · 20%', amt: '+596.00', date: '14:32' },
          { t: '佣金到账', sub: '五粮液普五 52度 · 20%', amt: '+276.00', date: '11:18' },
        ].map((r, i) => (
          <div key={i} style={{ display: 'flex', padding: '12px 16px', borderBottom: i < 1 ? '1px solid var(--line-soft)' : 'none' }}>
            <div style={{ flex: 1 }}>
              <div style={{ fontSize: 14, fontWeight: 500 }}>{r.t}</div>
              <div style={{ fontSize: 11, color: 'var(--text-3)', marginTop: 4 }}>{r.sub}</div>
            </div>
            <div style={{ textAlign: 'right' }}>
              <div className="font-num" style={{ fontSize: 15, fontWeight: 700, color: 'var(--gold-500)' }}>{r.amt}</div>
              <div style={{ fontSize: 11, color: 'var(--text-3)', marginTop: 4 }}>{r.date}</div>
            </div>
          </div>
        ))}
      </div>

      <div style={{ fontSize: 12, color: 'var(--text-3)', margin: '14px 0 8px' }}>昨日</div>
      <div style={{ background: '#fff', borderRadius: 12 }}>
        {[
          { t: '提现到微信', sub: '微信钱包 · 李明远', amt: '-10,000.00', date: '09:32', neg: true },
          { t: '招商奖励', sub: '王女士入驻 · 县级', amt: '+1,200.00', date: '昨日' },
          { t: '扶商奖励', sub: '团队成员业绩', amt: '+208.40', date: '昨日' },
        ].map((r, i, arr) => (
          <div key={i} style={{ display: 'flex', padding: '12px 16px', borderBottom: i < arr.length - 1 ? '1px solid var(--line-soft)' : 'none' }}>
            <div style={{ flex: 1 }}>
              <div style={{ fontSize: 14, fontWeight: 500 }}>{r.t}</div>
              <div style={{ fontSize: 11, color: 'var(--text-3)', marginTop: 4 }}>{r.sub}</div>
            </div>
            <div style={{ textAlign: 'right' }}>
              <div className="font-num" style={{ fontSize: 15, fontWeight: 700, color: r.neg ? 'var(--text-1)' : 'var(--gold-500)' }}>{r.amt}</div>
              <div style={{ fontSize: 11, color: 'var(--text-3)', marginTop: 4 }}>{r.date}</div>
            </div>
          </div>
        ))}
      </div>

      <div style={{ fontSize: 12, color: 'var(--text-3)', margin: '14px 0 8px' }}>5月14日</div>
      <div style={{ background: '#fff', borderRadius: 12 }}>
        {[
          { t: '佣金到账', sub: '红酒礼盒 ×2', amt: '+178.80', date: '19:42' },
          { t: '自购优惠返现', sub: '国窖1573 · 5折', amt: '+98.00', date: '09:12' },
        ].map((r, i) => (
          <div key={i} style={{ display: 'flex', padding: '12px 16px', borderBottom: i < 1 ? '1px solid var(--line-soft)' : 'none' }}>
            <div style={{ flex: 1 }}>
              <div style={{ fontSize: 14, fontWeight: 500 }}>{r.t}</div>
              <div style={{ fontSize: 11, color: 'var(--text-3)', marginTop: 4 }}>{r.sub}</div>
            </div>
            <div style={{ textAlign: 'right' }}>
              <div className="font-num" style={{ fontSize: 15, fontWeight: 700, color: 'var(--gold-500)' }}>{r.amt}</div>
              <div style={{ fontSize: 11, color: 'var(--text-3)', marginTop: 4 }}>{r.date}</div>
            </div>
          </div>
        ))}
      </div>
    </div>
    <HomeIndicator/>
  </Phone>
);

// ============================================================
// G4. 积分明细
// ============================================================
const ScreenPointsLog = () => (
  <Phone>
    <div style={{ background: '#fff' }}>
      <StatusBar theme="light"/>
      <NavBar title="积分明细" theme="on-light"/>
    </div>
    <div style={{
      background: 'linear-gradient(135deg, #D1467A, #99294F)',
      margin: '0 16px', borderRadius: 12, padding: '14px 18px', color: '#fff',
      position: 'relative', overflow: 'hidden',
    }}>
      <div style={{ position: 'absolute', top: -16, right: -16, width: 80, height: 80, borderRadius: '50%', background: 'rgba(255,255,255,0.15)' }}/>
      <div style={{ fontSize: 12, opacity: 0.85 }}>我的积分</div>
      <div className="font-num" style={{ fontSize: 30, fontWeight: 700, marginTop: 4 }}>1,280</div>
      <div style={{ fontSize: 11, opacity: 0.85, marginTop: 4 }}>可抵扣 ¥12.80 · 100 积分 = ¥1</div>
      <button style={{ marginTop: 12, height: 30, padding: '0 14px', borderRadius: 999, background: '#fff', color: '#D1467A', border: 'none', fontSize: 12, fontWeight: 600 }}>
        积分兑换 ›
      </button>
    </div>

    <div style={{ padding: '14px 16px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
      <div style={{ fontSize: 15, fontWeight: 700 }}>变动明细</div>
      <div style={{ display: 'flex', gap: 6 }}>
        {['全部', '获得', '使用'].map((c, i) => (
          <div key={c} style={{
            padding: '3px 10px', borderRadius: 999,
            background: i === 0 ? '#FCDDE6' : 'transparent',
            color: i === 0 ? '#D1467A' : 'var(--text-2)',
            fontSize: 11, fontWeight: i === 0 ? 600 : 400,
          }}>{c}</div>
        ))}
      </div>
    </div>

    <div style={{ overflow: 'auto', height: 'calc(100% - 88px - 110px - 50px)', padding: '0 16px 24px' }}>
      <div style={{ background: '#fff', borderRadius: 12 }}>
        {[
          { t: '订单返积分 · 茅台飞天', amt: '+298', date: '今日 14:32' },
          { t: '订单返积分 · 五粮液', amt: '+138', date: '今日 11:18' },
          { t: '订单使用积分', amt: '-128', date: '5/14 11:05', neg: true },
          { t: '签到奖励', amt: '+10', date: '5/14 09:00' },
          { t: '邀请新用户', amt: '+200', date: '5/12 14:30' },
          { t: '订单返积分 · 红酒礼盒', amt: '+88', date: '5/10 19:42' },
          { t: '积分抵扣订单', amt: '-200', date: '5/08 16:18', neg: true },
          { t: '签到奖励 · 连续 7 天', amt: '+50', date: '5/08 09:00' },
        ].map((r, i, arr) => (
          <div key={i} style={{ display: 'flex', alignItems: 'center', padding: '12px 16px', borderBottom: i < arr.length - 1 ? '1px solid var(--line-soft)' : 'none' }}>
            <div style={{ flex: 1 }}>
              <div style={{ fontSize: 14 }}>{r.t}</div>
              <div style={{ fontSize: 11, color: 'var(--text-3)', marginTop: 2 }}>{r.date}</div>
            </div>
            <div className="font-num" style={{ fontSize: 15, fontWeight: 700, color: r.neg ? 'var(--text-1)' : '#D1467A' }}>{r.amt}</div>
          </div>
        ))}
      </div>
    </div>
    <HomeIndicator/>
  </Phone>
);

// ============================================================
// G5. 优惠券
// ============================================================
const ScreenCoupons = () => (
  <Phone>
    <div style={{ background: '#fff' }}>
      <StatusBar theme="light"/>
      <NavBar title="我的优惠券" theme="on-light" right={<span style={{ fontSize: 13, color: 'var(--gold-500)' }}>领券中心</span>}/>
      <div style={{ display: 'flex', padding: '0 16px', borderBottom: '1px solid var(--line-soft)' }}>
        {[
          { l: '可用', n: 3, a: true },
          { l: '已使用', n: 12 },
          { l: '已过期', n: 8 },
        ].map(t => (
          <div key={t.l} style={{
            flex: 1, textAlign: 'center', padding: '12px 0',
            fontSize: 13, fontWeight: t.a ? 600 : 400,
            color: t.a ? 'var(--gold-500)' : 'var(--text-2)',
            position: 'relative',
          }}>
            {t.l} ({t.n})
            {t.a && <div style={{ position: 'absolute', bottom: 0, left: '50%', transform: 'translateX(-50%)', width: 22, height: 2, background: 'var(--grad-gold)' }}/>}
          </div>
        ))}
      </div>
    </div>

    <div style={{ background: 'var(--bg-app)', overflow: 'auto', height: 'calc(100% - 88px - 44px)', padding: '12px 16px 24px' }}>
      {[
        { amt: '300', cond: '满 ¥3,000 可用', t: '主产品专区通用券', d: '有效期至 2026-08-31', live: true },
        { amt: '100', cond: '满 ¥1,000 可用', t: '招商专区专属券', d: '有效期至 2026-06-30', live: true },
        { amt: '50',  cond: '满 ¥500 可用',   t: '新人专享券',     d: '有效期至 2026-05-31', live: true, soon: true },
      ].map((c, i) => (
        <div key={i} style={{
          display: 'flex', background: '#fff',
          borderRadius: 12, marginBottom: 10,
          boxShadow: 'var(--shadow-card)', overflow: 'hidden',
        }}>
          <div style={{
            width: 120, background: 'var(--grad-gold)',
            color: '#3A1A00', padding: '14px 12px', textAlign: 'center',
            position: 'relative',
          }}>
            <div style={{ fontSize: 11 }}>满减券</div>
            <div className="font-num" style={{ fontSize: 28, fontWeight: 800, lineHeight: 1.1, marginTop: 4 }}>
              <span style={{ fontSize: 16, fontWeight: 700 }}>¥</span>{c.amt}
            </div>
            <div style={{ fontSize: 10, marginTop: 4 }}>{c.cond}</div>
            {/* zigzag */}
            <div style={{ position: 'absolute', right: -6, top: 0, bottom: 0, width: 12, background:
              `radial-gradient(circle at 0 6px, #fff 6px, transparent 6px) 0 0/12px 12px` }}/>
          </div>
          <div style={{ flex: 1, padding: '12px 14px 12px 18px' }}>
            <div style={{ fontSize: 14, fontWeight: 600 }}>{c.t}</div>
            <div style={{ fontSize: 11, color: 'var(--text-3)', marginTop: 4 }}>{c.d}</div>
            <div style={{ fontSize: 11, color: 'var(--text-3)', marginTop: 4 }}>限主产品专区商品 · 不与其他优惠叠加</div>
            <div style={{ display: 'flex', alignItems: 'center', marginTop: 8 }}>
              {c.soon && <div className="tag" style={{ background: '#FFE9E6', color: '#D6453B' }}>即将过期</div>}
              <button style={{ marginLeft: 'auto', height: 28, padding: '0 14px', borderRadius: 999, background: 'var(--grad-gold)', color: '#3A1A00', border: 'none', fontSize: 12, fontWeight: 600 }}>立即使用</button>
            </div>
          </div>
        </div>
      ))}

      <div style={{ textAlign: 'center', padding: '20px 0', fontSize: 12, color: 'var(--text-3)' }}>
        — 没有更多了 —
      </div>
    </div>
    <HomeIndicator/>
  </Phone>
);

// ============================================================
// G6. 设置
// ============================================================
const ScreenSettings = () => (
  <Phone>
    <div style={{ background: '#fff' }}>
      <StatusBar theme="light"/>
      <NavBar title="设置" theme="on-light"/>
    </div>
    <div style={{ background: 'var(--bg-app)', overflow: 'auto', height: 'calc(100% - 88px)', padding: '12px 0 24px' }}>
      {/* Account */}
      <div style={{ fontSize: 12, color: 'var(--text-3)', padding: '0 16px 8px' }}>账号</div>
      <div style={{ background: '#fff' }}>
        <div className="row-link"><div>头像</div><div className="row-val"><Avatar size={32} kind="boss"/></div><Icon name="chevron" size={14} color="#C9BFA9"/></div>
        <div className="row-link"><div>昵称</div><div className="row-val">李明远</div><Icon name="chevron" size={14} color="#C9BFA9"/></div>
        <div className="row-link"><div>手机号</div><div className="row-val">138 **** 8888</div><Icon name="chevron" size={14} color="#C9BFA9"/></div>
        <div className="row-link"><div>实名认证</div><div className="row-val" style={{ color: 'var(--green-500)' }}>已认证</div><Icon name="chevron" size={14} color="#C9BFA9"/></div>
        <div className="row-link" style={{ borderBottom: 'none' }}><div>邀请人</div><div className="row-val">张敬山</div></div>
      </div>

      {/* Preferences */}
      <div style={{ fontSize: 12, color: 'var(--text-3)', padding: '16px 16px 8px' }}>偏好</div>
      <div style={{ background: '#fff' }}>
        <div className="row-link">
          <div>消息通知</div>
          <div style={{ marginLeft: 'auto', width: 40, height: 22, borderRadius: 999, background: 'var(--grad-gold)', position: 'relative' }}>
            <div style={{ position: 'absolute', top: 2, right: 2, width: 18, height: 18, background: '#fff', borderRadius: '50%' }}/>
          </div>
        </div>
        <div className="row-link">
          <div>声音提醒</div>
          <div style={{ marginLeft: 'auto', width: 40, height: 22, borderRadius: 999, background: '#E5DFD0', position: 'relative' }}>
            <div style={{ position: 'absolute', top: 2, left: 2, width: 18, height: 18, background: '#fff', borderRadius: '50%' }}/>
          </div>
        </div>
        <div className="row-link" style={{ borderBottom: 'none' }}><div>语言</div><div className="row-val">简体中文</div><Icon name="chevron" size={14} color="#C9BFA9"/></div>
      </div>

      <div style={{ fontSize: 12, color: 'var(--text-3)', padding: '16px 16px 8px' }}>其他</div>
      <div style={{ background: '#fff' }}>
        <div className="row-link"><div>清除缓存</div><div className="row-val">12.4 MB</div><Icon name="chevron" size={14} color="#C9BFA9"/></div>
        <div className="row-link"><div>用户协议</div><Icon name="chevron" size={14} className="row-arrow" color="#C9BFA9"/></div>
        <div className="row-link"><div>隐私协议</div><Icon name="chevron" size={14} className="row-arrow" color="#C9BFA9"/></div>
        <div className="row-link"><div>关于醇品汇</div><div className="row-val">v1.0.0</div><Icon name="chevron" size={14} color="#C9BFA9"/></div>
        <div className="row-link" style={{ borderBottom: 'none' }}><div>注销账号</div><Icon name="chevron" size={14} className="row-arrow" color="#C9BFA9"/></div>
      </div>

      <div style={{ padding: '24px 16px 0' }}>
        <button style={{ width: '100%', height: 46, borderRadius: 999, background: '#fff', color: '#D6453B', border: 'none', fontSize: 15, fontWeight: 600 }}>退出登录</button>
      </div>
    </div>
    <HomeIndicator/>
  </Phone>
);

// ============================================================
// G7. 消息中心
// ============================================================
const ScreenMessages = () => (
  <Phone>
    <div style={{ background: '#fff' }}>
      <StatusBar theme="light"/>
      <NavBar title="消息中心" theme="on-light" right={<span style={{ fontSize: 13, color: 'var(--text-2)' }}>全部已读</span>}/>
    </div>

    <div style={{ background: 'var(--bg-app)', overflow: 'auto', height: 'calc(100% - 88px)', padding: '12px 16px 24px' }}>
      {/* Categories */}
      <div style={{ background: '#fff', borderRadius: 12, padding: '14px 8px', display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 8, marginBottom: 12 }}>
        {[
          { l: '系统通知', i: 'bell', c: '#D38A00', n: 2 },
          { l: '订单消息', i: 'box', c: '#3A6FD1', n: 1 },
          { l: '佣金消息', i: 'coin', c: '#2F9E5C' },
          { l: '活动消息', i: 'gift', c: '#D1467A' },
        ].map(c => (
          <div key={c.l} style={{ textAlign: 'center', position: 'relative' }}>
            <div style={{ width: 46, height: 46, margin: '0 auto', borderRadius: '50%', background: c.c + '18', color: c.c, display: 'flex', alignItems: 'center', justifyContent: 'center', position: 'relative' }}>
              <Icon name={c.i} size={22} color={c.c}/>
              {c.n && <div style={{ position: 'absolute', top: -2, right: -4, background: '#D6453B', color: '#fff', fontSize: 10, padding: '0 5px', height: 16, borderRadius: 8, display: 'flex', alignItems: 'center' }}>{c.n}</div>}
            </div>
            <div style={{ fontSize: 11, marginTop: 6 }}>{c.l}</div>
          </div>
        ))}
      </div>

      {/* Messages */}
      <div style={{ background: '#fff', borderRadius: 12 }}>
        {[
          { i: 'coin', c: '#2F9E5C', t: '佣金到账提醒', s: '您的订单佣金 ¥596.00 已到账，可立即提现…', d: '今日 14:32', new: true },
          { i: 'box', c: '#3A6FD1', t: '订单发货通知', s: '您的订单 CPH202605141156234 已发货，预计 5月18日…', d: '今日 09:23' },
          { i: 'gift', c: '#D1467A', t: '招商奖励到账', s: '王女士成功入驻为县级联营商，您获得 ¥1,200 招商奖励…', d: '昨日 11:08' },
          { i: 'bell', c: '#D38A00', t: '秋季新品上市', s: '2026 秋季新品季 · 联营商福利活动今日 10:00 正式开启…', d: '昨日 10:00', new: true },
          { i: 'box', c: '#3A6FD1', t: '物流轨迹更新', s: '您的订单包裹已到达北京朝阳建国门营业点…', d: '5/14 16:23' },
          { i: 'bell', c: '#D38A00', t: '系统升级公告', s: '醇品汇小程序将于本周日凌晨进行升级维护…', d: '5/12 09:00' },
        ].map((m, i) => (
          <div key={i} style={{ display: 'flex', gap: 12, padding: '14px 16px', borderBottom: '1px solid var(--line-soft)', alignItems: 'flex-start' }}>
            <div style={{ width: 40, height: 40, borderRadius: '50%', background: m.c + '18', color: m.c, display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0, position: 'relative' }}>
              <Icon name={m.i} size={20} color={m.c}/>
              {m.new && <div style={{ position: 'absolute', top: 0, right: 0, width: 8, height: 8, borderRadius: '50%', background: '#D6453B', border: '1.5px solid #fff' }}/>}
            </div>
            <div style={{ flex: 1, minWidth: 0 }}>
              <div style={{ display: 'flex', alignItems: 'center' }}>
                <div style={{ fontSize: 14, fontWeight: 600 }}>{m.t}</div>
                <div style={{ marginLeft: 'auto', fontSize: 11, color: 'var(--text-3)' }}>{m.d}</div>
              </div>
              <div style={{ fontSize: 12, color: 'var(--text-2)', marginTop: 4, lineHeight: 1.5, display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical', overflow: 'hidden' }}>{m.s}</div>
            </div>
          </div>
        ))}
      </div>
    </div>
    <HomeIndicator/>
  </Phone>
);

// ============================================================
// G8. 客服 / 联系我们
// ============================================================
const ScreenCS = () => (
  <Phone>
    <div style={{ background: 'var(--grad-brown)', color: '#F6E7C2', paddingBottom: 30 }}>
      <StatusBar theme="dark"/>
      <NavBar title="客服中心" theme="on-brown"/>
      <div style={{ padding: '8px 24px', textAlign: 'center' }}>
        <Icon name="headset" size={48} color="#FFD685" style={{ margin: '0 auto' }}/>
        <div style={{ fontSize: 18, fontWeight: 700, color: '#FFE0A0', marginTop: 12 }}>你好，李明远</div>
        <div style={{ fontSize: 12, color: 'rgba(246,231,194,0.7)', marginTop: 6 }}>很高兴为您服务，可选择以下方式联系我们</div>
      </div>
    </div>

    <div style={{ padding: '14px 16px', overflow: 'auto', height: 'calc(100% - 200px)' }}>
      {/* Online + phone */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10, marginTop: -16, position: 'relative', zIndex: 2 }}>
        <div style={{ background: 'var(--grad-gold)', borderRadius: 14, padding: '18px 14px', color: '#3A1A00' }}>
          <Icon name="chat" size={28} color="#3A1A00"/>
          <div style={{ fontSize: 14, fontWeight: 700, marginTop: 8 }}>在线客服</div>
          <div style={{ fontSize: 11, opacity: 0.85, marginTop: 4 }}>09:00 - 22:00 在线</div>
        </div>
        <div style={{ background: '#fff', borderRadius: 14, padding: '18px 14px', border: '1px solid var(--line)' }}>
          <Icon name="headset" size={28} color="#D38A00"/>
          <div style={{ fontSize: 14, fontWeight: 700, marginTop: 8 }}>电话客服</div>
          <div className="font-num" style={{ fontSize: 13, color: 'var(--gold-500)', marginTop: 4, fontWeight: 600 }}>400-668-8821</div>
        </div>
      </div>

      {/* Topics */}
      <div style={{ marginTop: 14 }}>
        <div style={{ fontSize: 14, fontWeight: 700, marginBottom: 10 }}>常见问题</div>
        <div style={{ background: '#fff', borderRadius: 12 }}>
          {[
            { l: '如何成为联营商？', i: 'tree' },
            { l: '佣金如何计算与结算？', i: 'coin' },
            { l: '订单发货时间及物流查询', i: 'truck' },
            { l: '退换货政策与售后流程', i: 'reload' },
            { l: '提现规则与到账时间', i: 'wallet' },
            { l: '团队等级与升级规则', i: 'medal' },
          ].map((q, i, arr) => (
            <div key={i} className="row-link" style={{ borderBottom: i < arr.length - 1 ? '1px solid var(--line-soft)' : 'none' }}>
              <div className="row-icon"><Icon name={q.i} size={16} color="#D38A00"/></div>
              <div>{q.l}</div>
              <Icon name="chevron" size={14} className="row-arrow" color="#C9BFA9"/>
            </div>
          ))}
        </div>
      </div>

      <div style={{ marginTop: 14, padding: '12px 14px', background: 'var(--gold-50)', borderRadius: 10, fontSize: 12, color: '#7A5610', display: 'flex', alignItems: 'center', gap: 8 }}>
        <Icon name="info" size={16} color="#D38A00"/>
        反馈与建议请发送邮件至 service@chunpinhui.com
      </div>
    </div>
    <HomeIndicator/>
  </Phone>
);

// ============================================================
// G9. 酒豆明细
// ============================================================
const ScreenBeansLog = () => (
  <Phone>
    <div style={{ background: '#fff' }}>
      <StatusBar theme="light"/>
      <NavBar title="酒豆明细" theme="on-light" right={<span style={{ fontSize: 13, color: '#A33616' }}>规则</span>}/>
    </div>
    <div style={{
      background: 'linear-gradient(135deg, #C4322B 0%, #771810 100%)',
      margin: '0 16px', borderRadius: 14, padding: '16px 18px', color: '#fff',
      position: 'relative', overflow: 'hidden',
    }}>
      <div style={{ position: 'absolute', top: -16, right: -16, width: 96, height: 96, borderRadius: '50%', background: 'rgba(255,255,255,0.12)' }}/>
      <div style={{ position: 'absolute', bottom: -30, right: 20, opacity: 0.18 }}>
        <svg viewBox="0 0 60 60" width="80" height="80" fill="#FFD646"><ellipse cx="30" cy="30" rx="18" ry="22"/></svg>
      </div>
      <div style={{ position: 'relative' }}>
        <div style={{ fontSize: 12, opacity: 0.85 }}>我的酒豆</div>
        <div className="font-num" style={{ fontSize: 32, fontWeight: 700, marginTop: 4, letterSpacing: 1 }}>6,480</div>
        <div style={{ fontSize: 11, opacity: 0.85, marginTop: 4 }}>可抵扣 ¥64.80 · 100 豆 = ¥1</div>
        <div style={{ display: 'flex', gap: 8, marginTop: 14 }}>
          <button style={{ height: 30, padding: '0 14px', borderRadius: 999, background: '#fff', color: '#A33616', border: 'none', fontSize: 12, fontWeight: 700 }}>
            兑换商品 ›
          </button>
          <button style={{ height: 30, padding: '0 14px', borderRadius: 999, background: 'rgba(255,255,255,0.18)', color: '#fff', border: '1px solid rgba(255,255,255,0.35)', fontSize: 12, fontWeight: 600 }}>
            赚酒豆攻略
          </button>
        </div>
      </div>
    </div>

    {/* Stat summary */}
    <div style={{ background: '#fff', margin: '12px 16px 0', borderRadius: 12, padding: '12px 0', display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)' }}>
      {[
        { l: '本月获得', v: '+1,820', c: '#A33616' },
        { l: '本月使用', v: '-440', c: 'var(--text-1)' },
        { l: '即将到期', v: '300', c: '#D6453B', sub: '6月30日' },
      ].map((s, i) => (
        <div key={s.l} style={{ textAlign: 'center', borderLeft: i > 0 ? '1px solid var(--line-soft)' : 'none' }}>
          <div className="font-num" style={{ fontSize: 16, fontWeight: 700, color: s.c }}>{s.v}</div>
          <div style={{ fontSize: 11, color: 'var(--text-3)', marginTop: 4 }}>{s.l}{s.sub && ` · ${s.sub}`}</div>
        </div>
      ))}
    </div>

    {/* Filter */}
    <div style={{ padding: '14px 16px 6px', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
      <div style={{ display: 'flex', gap: 6 }}>
        {['全部', '获得', '使用', '过期'].map((c, i) => (
          <div key={c} style={{
            padding: '4px 12px', borderRadius: 999,
            background: i === 0 ? '#F6DDD0' : 'transparent',
            border: i === 0 ? '1px solid #D89478' : '1px solid var(--line)',
            color: i === 0 ? '#A33616' : 'var(--text-2)',
            fontSize: 12, fontWeight: i === 0 ? 600 : 400,
          }}>{c}</div>
        ))}
      </div>
      <div style={{ fontSize: 12, color: 'var(--text-2)', display: 'flex', alignItems: 'center', gap: 4 }}>
        2026.05 <Icon name="chevron-down" size={14} color="#5E5142"/>
      </div>
    </div>

    <div style={{ overflow: 'auto', height: 'calc(100% - 88px - 140px - 40px - 50px)', padding: '0 16px 24px' }}>
      <div style={{ background: '#fff', borderRadius: 12 }}>
        {[
          { t: '订单消费返豆', sub: '茅台飞天 · 比例 10%', amt: '+298', date: '今日 14:32' },
          { t: '订单消费返豆', sub: '五粮液普五 · 比例 10%', amt: '+138', date: '今日 11:18' },
          { t: '使用酒豆抵扣订单', sub: '酱香型礼盒 · 抵 ¥1.40', amt: '-140', date: '5/14 11:05', neg: true },
          { t: '邀请新人奖励', sub: '王女士首单完成', amt: '+500', date: '5/12 14:30' },
          { t: '签到 7 日连击', sub: '连续签到奖励', amt: '+200', date: '5/12 09:00' },
          { t: '订单消费返豆', sub: '红酒礼盒 · 比例 10%', amt: '+89', date: '5/10 19:42' },
          { t: '酒豆兑换商品', sub: '兑换 醇品汇定制酒杯', amt: '-2,000', date: '5/08 16:18', neg: true },
          { t: '酒豆到期', sub: '2025 年累计未使用', amt: '-150', date: '5/01 00:00', neg: true, exp: true },
        ].map((r, i, arr) => (
          <div key={i} style={{ display: 'flex', alignItems: 'center', padding: '12px 16px', borderBottom: i < arr.length - 1 ? '1px solid var(--line-soft)' : 'none' }}>
            <div style={{ width: 32, height: 32, borderRadius: '50%', background: r.exp ? '#FFE9E6' : r.neg ? '#F4EEDF' : '#F6DDD0', color: r.exp ? '#D6453B' : r.neg ? '#7A6F58' : '#A33616', display: 'flex', alignItems: 'center', justifyContent: 'center', marginRight: 12, flexShrink: 0 }}>
              <Icon name={r.exp ? 'warn' : r.neg ? 'shop' : 'gift'} size={16}/>
            </div>
            <div style={{ flex: 1, minWidth: 0 }}>
              <div style={{ fontSize: 14 }}>{r.t}</div>
              <div style={{ fontSize: 11, color: 'var(--text-3)', marginTop: 2 }}>{r.date} · {r.sub}</div>
            </div>
            <div className="font-num" style={{ fontSize: 15, fontWeight: 700, color: r.neg ? 'var(--text-1)' : '#A33616' }}>{r.amt}</div>
          </div>
        ))}
      </div>
      <div style={{ textAlign: 'center', padding: '20px 0 0', fontSize: 12, color: 'var(--text-3)' }}>— 上拉加载更多 —</div>
    </div>
    <HomeIndicator/>
  </Phone>
);

// ============================================================
// G10. DF 赠送
// ============================================================
const ScreenDFGift = () => (
  <Phone>
    <div style={{ background: 'linear-gradient(180deg, #3A6FD1 0%, #1F4B9A 100%)', color: '#fff', paddingBottom: 22 }}>
      <StatusBar theme="dark"/>
      <NavBar title="DF 赠送" theme="on-brown" right={<span style={{ fontSize: 13, color: '#fff' }}>赠送记录</span>}/>
      <div style={{ padding: '4px 24px 0' }}>
        <div style={{ fontSize: 12, opacity: 0.85 }}>当前 DF 余额</div>
        <div className="font-num" style={{ fontSize: 36, fontWeight: 700, marginTop: 4 }}>¥1,500.00</div>
        <div style={{ fontSize: 11, opacity: 0.7, marginTop: 4 }}>可赠送给团队成员或直推好友</div>
      </div>
    </div>

    <div style={{ background: 'var(--bg-app)', overflow: 'auto', height: 'calc(100% - 200px - 80px)', padding: '12px 0' }}>
      {/* Recipient selector */}
      <div style={{ background: '#fff', padding: '14px 16px' }}>
        <div style={{ fontSize: 13, fontWeight: 700, marginBottom: 10 }}>赠送对象</div>
        <div style={{ display: 'flex', alignItems: 'center', gap: 12, padding: '10px 12px', background: 'var(--cream-50)', borderRadius: 10, border: '1px solid var(--line)' }}>
          <Avatar size={40} kind="woman1" seed="周"/>
          <div style={{ flex: 1 }}>
            <div style={{ fontSize: 14, fontWeight: 600 }}>周文君</div>
            <div style={{ fontSize: 11, color: 'var(--text-3)', marginTop: 2 }}>139 **** 2046 · 直推团队</div>
          </div>
          <div style={{ fontSize: 12, color: 'var(--gold-500)' }}>更换 ›</div>
        </div>
        {/* Recent quick pick */}
        <div style={{ fontSize: 11, color: 'var(--text-3)', marginTop: 12, marginBottom: 8 }}>最近赠送</div>
        <div style={{ display: 'flex', gap: 12 }}>
          {[
            { n: '王晓明', k: 'man1' },
            { n: '陈丽华', k: 'woman1' },
            { n: '李志强', k: 'man2' },
            { n: '赵小婷', k: 'woman2' },
            { n: '更多', icon: true },
          ].map((m, i) => (
            <div key={i} style={{ textAlign: 'center' }}>
              {m.icon ? (
                <div style={{ width: 40, height: 40, borderRadius: '50%', background: 'var(--bg-app)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                  <Icon name="chevron" size={18} color="#9B8E7C"/>
                </div>
              ) : (
                <Avatar size={40} seed={m.n[0]} kind={m.k}/>
              )}
              <div style={{ fontSize: 10, marginTop: 4, color: 'var(--text-2)' }}>{m.n}</div>
            </div>
          ))}
        </div>
      </div>

      {/* Amount input */}
      <div style={{ background: '#fff', padding: '14px 16px', marginTop: 10 }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <div style={{ fontSize: 13, fontWeight: 700 }}>赠送金额</div>
          <div style={{ fontSize: 12, color: '#3A6FD1' }}>全部赠出</div>
        </div>
        <div style={{ display: 'flex', alignItems: 'baseline', marginTop: 14, paddingBottom: 12, borderBottom: '1px solid var(--line-soft)' }}>
          <div style={{ fontSize: 26, fontWeight: 700, color: 'var(--text-1)' }}>¥</div>
          <div className="font-num" style={{ fontSize: 36, fontWeight: 700, marginLeft: 4 }}>500.00</div>
        </div>
        <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 8, fontSize: 11, color: 'var(--text-3)' }}>
          <div>赠送上限：单次 ¥5,000 · 单日 ¥10,000</div>
          <div>余额 ¥1,500</div>
        </div>
        <div style={{ display: 'flex', gap: 8, marginTop: 14 }}>
          {['100', '300', '500', '1000', '1500'].map((a, i) => (
            <div key={a} style={{
              flex: 1, padding: '8px 0', textAlign: 'center', fontSize: 12,
              borderRadius: 8,
              background: i === 2 ? '#DCE5FA' : 'var(--bg-app)',
              color: i === 2 ? '#3A6FD1' : 'var(--text-2)',
              fontWeight: i === 2 ? 600 : 400,
              border: i === 2 ? '1px solid #3A6FD1' : '1px solid transparent',
            }}>¥{a}</div>
          ))}
        </div>
      </div>

      {/* Note */}
      <div style={{ background: '#fff', padding: '14px 16px', marginTop: 10 }}>
        <div style={{ fontSize: 13, fontWeight: 700, marginBottom: 10 }}>赠送祝福语 <span style={{ fontWeight: 400, color: 'var(--text-3)', fontSize: 11 }}>(选填)</span></div>
        <textarea rows="3" placeholder="对她/他说点什么吧…" defaultValue="恭喜本月业绩翻倍，继续加油！" style={{
          width: '100%', resize: 'none', border: 'none', outline: 'none',
          background: 'var(--bg-app)', borderRadius: 8, padding: 10, fontSize: 13, fontFamily: 'inherit',
        }}/>
        <div style={{ fontSize: 11, color: 'var(--text-3)', textAlign: 'right' }}>18/50</div>
      </div>

      <div style={{ margin: '10px 16px 0', padding: '12px 14px', background: '#E8EFFD', borderRadius: 10, fontSize: 12, color: '#1F4B9A', lineHeight: 1.7 }}>
        <div style={{ fontWeight: 600, marginBottom: 4 }}>赠送说明</div>
        · 仅可赠送给直推 / 次级团队成员<br/>
        · 赠送后 DF 即时到账，不可撤回<br/>
        · 当日赠送总额不超过 ¥10,000，单次 ¥5,000
      </div>
    </div>

    <div style={{ position: 'absolute', left: 0, right: 0, bottom: 0, background: '#fff', borderTop: '1px solid var(--line-soft)', padding: '12px 16px 26px', display: 'flex', alignItems: 'center' }}>
      <div>
        <div style={{ fontSize: 11, color: 'var(--text-3)' }}>赠送后剩余</div>
        <div className="font-num" style={{ fontSize: 18, fontWeight: 700 }}>¥1,000.00</div>
      </div>
      <button className="btn-gold" style={{ marginLeft: 'auto', height: 46, padding: '0 32px', background: 'linear-gradient(135deg, #3A6FD1, #1F4B9A)', color: '#fff', boxShadow: '0 6px 18px rgba(58,111,209,0.32)' }}>
        确认赠送
      </button>
    </div>
  </Phone>
);

// ============================================================
// G11. DF 赠送记录
// ============================================================
const ScreenDFGiftLog = () => (
  <Phone>
    <div style={{ background: '#fff' }}>
      <StatusBar theme="light"/>
      <NavBar title="DF 赠送记录" theme="on-light"/>
      <div style={{ display: 'flex', padding: '0 16px', borderBottom: '1px solid var(--line-soft)' }}>
        {[
          { l: '全部', a: true },
          { l: '我送出', n: 12 },
          { l: '我收到', n: 5 },
        ].map(t => (
          <div key={t.l} style={{
            flex: 1, textAlign: 'center', padding: '12px 0',
            fontSize: 13, fontWeight: t.a ? 600 : 400,
            color: t.a ? '#3A6FD1' : 'var(--text-2)',
            position: 'relative',
          }}>
            {t.l}{t.n ? ` (${t.n})` : ''}
            {t.a && <div style={{ position: 'absolute', bottom: 0, left: '50%', transform: 'translateX(-50%)', width: 22, height: 2, background: '#3A6FD1' }}/>}
          </div>
        ))}
      </div>
    </div>

    {/* Summary */}
    <div style={{ background: 'linear-gradient(135deg, #3A6FD1, #1F4B9A)', margin: '12px 16px 0', borderRadius: 12, padding: '14px 18px', color: '#fff' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between' }}>
        <div>
          <div style={{ fontSize: 11, opacity: 0.85 }}>累计送出</div>
          <div className="font-num" style={{ fontSize: 22, fontWeight: 700, marginTop: 4 }}>¥8,600</div>
        </div>
        <div style={{ width: 1, background: 'rgba(255,255,255,0.2)' }}/>
        <div>
          <div style={{ fontSize: 11, opacity: 0.85 }}>累计收到</div>
          <div className="font-num" style={{ fontSize: 22, fontWeight: 700, marginTop: 4 }}>¥2,400</div>
        </div>
        <div style={{ width: 1, background: 'rgba(255,255,255,0.2)' }}/>
        <div>
          <div style={{ fontSize: 11, opacity: 0.85 }}>本月送出</div>
          <div className="font-num" style={{ fontSize: 22, fontWeight: 700, marginTop: 4 }}>¥1,500</div>
        </div>
      </div>
    </div>

    <div style={{ overflow: 'auto', height: 'calc(100% - 88px - 44px - 90px)', padding: '12px 16px 24px' }}>
      {[
        { dir: 'out', n: '周文君', k: 'woman1', amt: '500.00', date: '今日 11:32', note: '恭喜本月业绩翻倍，继续加油！' },
        { dir: 'in',  n: '张敬山', k: 'boss',   amt: '1,000.00', date: '今日 09:18', note: '辛苦了，给你点小福利' },
        { dir: 'out', n: '王晓明', k: 'man1',   amt: '300.00', date: '昨日 16:42', note: '新人加油礼' },
        { dir: 'out', n: '陈丽华', k: 'woman1', amt: '500.00', date: '昨日 14:08', note: '欢迎加入团队' },
        { dir: 'out', n: '李志强', k: 'man2',   amt: '200.00', date: '5/12 10:05', note: '感谢推荐新客户' },
        { dir: 'in',  n: '张敬山', k: 'boss',   amt: '500.00', date: '5/10 19:42', note: '团队建设奖励' },
        { dir: 'out', n: '赵小婷', k: 'woman2', amt: '300.00', date: '5/06 12:18' },
        { dir: 'out', n: '周文君', k: 'woman1', amt: '800.00', date: '5/03 09:30', note: '周文君，你真棒' },
      ].map((r, i) => (
        <div key={i} style={{ background: '#fff', borderRadius: 12, padding: '12px 14px', marginBottom: 10, display: 'flex', gap: 12, alignItems: 'flex-start' }}>
          <Avatar size={40} seed={r.n[0]} kind={r.k}/>
          <div style={{ flex: 1, minWidth: 0 }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
              <div style={{ fontSize: 13, fontWeight: 600 }}>{r.dir === 'out' ? '送给 ' : '来自 '}{r.n}</div>
              <div className="tag" style={{
                background: r.dir === 'out' ? '#FFE9E6' : '#DCE5FA',
                color: r.dir === 'out' ? '#D6453B' : '#3A6FD1',
                fontSize: 10,
              }}>{r.dir === 'out' ? '送出' : '收到'}</div>
            </div>
            {r.note && (
              <div style={{
                fontSize: 11, color: 'var(--text-2)', marginTop: 6, padding: '6px 10px',
                background: r.dir === 'out' ? '#FFE9E6' : '#E8EFFD',
                borderRadius: 6, borderLeft: `2px solid ${r.dir === 'out' ? '#D6453B' : '#3A6FD1'}`,
                lineHeight: 1.5,
              }}>
                "{r.note}"
              </div>
            )}
            <div style={{ fontSize: 11, color: 'var(--text-3)', marginTop: 6 }}>{r.date}</div>
          </div>
          <div className="font-num" style={{
            fontSize: 16, fontWeight: 700, flexShrink: 0,
            color: r.dir === 'out' ? 'var(--text-1)' : '#3A6FD1',
          }}>
            {r.dir === 'out' ? '-' : '+'}¥{r.amt}
          </div>
        </div>
      ))}
      <div style={{ textAlign: 'center', padding: '12px 0', fontSize: 12, color: 'var(--text-3)' }}>— 没有更多了 —</div>
    </div>
    <HomeIndicator/>
  </Phone>
);

Object.assign(window, {
  ScreenProfile, ScreenAssets, ScreenBalanceLog, ScreenPointsLog,
  ScreenCoupons, ScreenSettings, ScreenMessages, ScreenCS,
  ScreenBeansLog, ScreenDFGift, ScreenDFGiftLog,
});
