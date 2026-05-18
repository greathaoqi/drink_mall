// section-f.jsx — Distribution / 联营商

// ============================================================
// F1. 分销工作台
// ============================================================
const ScreenDist = () => (
  <Phone>
    {/* Brown header with stats */}
    <div style={{
      background: 'var(--grad-brown)',
      backgroundImage: 'radial-gradient(ellipse 80% 60% at 50% 30%, rgba(228,165,22,0.18), transparent 60%), var(--grad-brown)',
      color: '#F6E7C2', paddingBottom: 26,
    }}>
      <StatusBar theme="dark"/>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '4px 16px 16px' }}>
        <div style={{ fontSize: 20, fontWeight: 700, color: '#FFE0A0' }}>分销工作台</div>
        <Icon name="qr" size={22} color="#FFD685"/>
      </div>

      {/* Hero card */}
      <div style={{ margin: '0 16px', padding: '14px 16px', borderRadius: 14, background: 'rgba(0,0,0,0.22)', border: '1px solid rgba(228,165,22,0.18)' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
          <Avatar size={40} kind="boss"/>
          <div style={{ flex: 1 }}>
            <div style={{ fontSize: 14, fontWeight: 600, color: '#FFE0A0' }}>李明远</div>
            <div style={{ fontSize: 11, color: 'rgba(246,231,194,0.7)' }}>县级联营商 · 生态合伙人</div>
          </div>
          <div className="tag tag-gold-solid">已达成</div>
        </div>
        <div style={{ marginTop: 14 }}>
          <div style={{ fontSize: 11, color: 'rgba(246,231,194,0.6)' }}>累计业绩</div>
          <div className="font-num" style={{ fontSize: 30, fontWeight: 700, color: '#FFE0A0', marginTop: 2 }}>¥58,400</div>
        </div>
        <div className="bar-track" style={{ marginTop: 10 }}>
          <div className="fill" style={{ width: '38%' }}/>
        </div>
        <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 6, fontSize: 11, color: 'rgba(246,231,194,0.7)' }}>
          <span>已达成 ¥58,400</span>
          <span>市级目标 ¥150,000 (差 ¥91,600)</span>
        </div>
      </div>
    </div>

    <div style={{ overflow: 'auto', height: 'calc(100% - 220px - 76px)', background: 'var(--bg-app)', padding: '12px 16px 16px' }}>
      {/* Quick stats */}
      <div style={{ background: '#fff', borderRadius: 12, padding: '14px 0', display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)' }}>
        {[
          { l: '今日佣金', v: '¥386', c: 'var(--gold-500)' },
          { l: '本月佣金', v: '¥4,260' },
          { l: '团队人数', v: '23' },
          { l: '直推', v: '8' },
        ].map(s => (
          <div key={s.l} style={{ textAlign: 'center', position: 'relative' }}>
            <div className="font-num" style={{ fontSize: 18, fontWeight: 700, color: s.c || 'var(--text-1)' }}>{s.v}</div>
            <div style={{ fontSize: 11, color: 'var(--text-3)', marginTop: 4 }}>{s.l}</div>
          </div>
        ))}
      </div>

      {/* Wallet card */}
      <div style={{
        background: 'var(--grad-gold)', borderRadius: 14,
        padding: '16px', marginTop: 12, color: '#3A1A00',
        position: 'relative', overflow: 'hidden',
      }}>
        <div style={{ position: 'absolute', top: -30, right: -30, width: 140, height: 140, borderRadius: '50%', background: 'rgba(255,255,255,0.18)' }}/>
        <div style={{ position: 'relative' }}>
          <div style={{ fontSize: 12, color: '#7A4810' }}>可提现余额</div>
          <div className="font-num" style={{ fontSize: 30, fontWeight: 700, marginTop: 2 }}>¥24,860.00</div>
          <div style={{ display: 'flex', alignItems: 'center', marginTop: 12 }}>
            <div style={{ fontSize: 12 }}>冻结余额 <span className="font-num">¥3,200</span></div>
            <button style={{ marginLeft: 'auto', height: 32, padding: '0 18px', borderRadius: 999, background: '#3A1A00', color: '#FFD646', border: 'none', fontSize: 13, fontWeight: 600 }}>立即提现 ›</button>
          </div>
        </div>
      </div>

      {/* Menu grid */}
      <div style={{ background: '#fff', borderRadius: 12, padding: '14px 8px', marginTop: 12, display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 12 }}>
        {[
          { l: '联营商等级', i: 'medal',  c: '#D38A00' },
          { l: '合伙人等级', i: 'star',   c: '#D1467A' },
          { l: '团队管理',  i: 'people', c: '#3A6FD1' },
          { l: '邀请海报',  i: 'qr',     c: '#2F9E5C' },
          { l: '佣金明细',  i: 'coin',   c: '#D38A00' },
          { l: '奖励明细',  i: 'gift',   c: '#D1467A' },
          { l: '提现记录',  i: 'wallet', c: '#3A6FD1' },
          { l: '体验店',    i: 'shop',   c: '#2F9E5C' },
        ].map(m => (
          <div key={m.l} style={{ textAlign: 'center' }}>
            <div style={{ width: 46, height: 46, margin: '0 auto', borderRadius: 12, background: m.c + '18', display: 'flex', alignItems: 'center', justifyContent: 'center', color: m.c }}>
              <Icon name={m.i} size={22} color={m.c}/>
            </div>
            <div style={{ fontSize: 11, marginTop: 6, color: 'var(--text-1)' }}>{m.l}</div>
          </div>
        ))}
      </div>

      {/* Promotions */}
      <div className="sec-title" style={{ paddingLeft: 0, paddingRight: 0 }}>
        <div className="bar"/> 热推商品 · 高佣
        <div className="more">更多 ›</div>
      </div>
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
        <div className="prod-card" style={{ position: 'relative' }}>
          <div className="pimg"><ProdImg kind="maotai" text="茅"/></div>
          <div className="pmeta">
            <div className="pname">茅台飞天 53度 500ml</div>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 6 }}>
              <div className="price font-num" style={{ fontSize: 17 }}><span className="sym">¥</span>2,980</div>
              <div style={{ fontSize: 11, color: 'var(--gold-500)' }}>佣金¥596</div>
            </div>
            <button style={{ width: '100%', height: 28, marginTop: 8, borderRadius: 999, background: 'var(--grad-gold)', color: '#3A1A00', border: 'none', fontSize: 12, fontWeight: 600 }}>
              <Icon name="share" size={12} color="#3A1A00" style={{ marginRight: 4, verticalAlign: 'middle' }}/>分享赚
            </button>
          </div>
        </div>
        <div className="prod-card">
          <div className="pimg"><ProdImg kind="wuliang" text="五"/></div>
          <div className="pmeta">
            <div className="pname">五粮液普五 52度 500ml</div>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 6 }}>
              <div className="price font-num" style={{ fontSize: 17 }}><span className="sym">¥</span>1,380</div>
              <div style={{ fontSize: 11, color: 'var(--gold-500)' }}>佣金¥276</div>
            </div>
            <button style={{ width: '100%', height: 28, marginTop: 8, borderRadius: 999, background: 'var(--grad-gold)', color: '#3A1A00', border: 'none', fontSize: 12, fontWeight: 600 }}>
              <Icon name="share" size={12} color="#3A1A00" style={{ marginRight: 4, verticalAlign: 'middle' }}/>分享赚
            </button>
          </div>
        </div>
      </div>
    </div>
    <TabBar active="dist"/>
  </Phone>
);

// ============================================================
// F2. 联营商等级
// ============================================================
const ScreenLevelLianYing = () => (
  <Phone>
    {/* Brown header */}
    <div style={{ background: 'var(--grad-brown)', color: '#F6E7C2', paddingBottom: 12 }}>
      <StatusBar theme="dark"/>
      <NavBar title="联营商等级体系" theme="on-brown"/>
      {/* Tabs */}
      <div style={{ display: 'flex', padding: '4px 16px 0', fontSize: 14 }}>
        {['联营商等级', '合伙人等级', '奖励明细'].map((t, i) => (
          <div key={t} style={{
            flex: 1, textAlign: 'center', paddingBottom: 8,
            fontWeight: i === 0 ? 700 : 400,
            color: i === 0 ? '#FFE0A0' : 'rgba(246,231,194,0.6)',
            position: 'relative',
          }}>
            {t}
            {i === 0 && <div style={{ position: 'absolute', bottom: 0, left: '50%', transform: 'translateX(-50%)', width: 22, height: 2, background: 'var(--grad-gold)' }}/>}
          </div>
        ))}
      </div>
    </div>

    {/* Current level card */}
    <div style={{ margin: '0 16px', marginTop: -22, position: 'relative', zIndex: 2,
      background: 'linear-gradient(135deg, #2A0E00 0%, #4A1E08 100%)',
      borderRadius: 14, padding: '16px 18px', border: '1px solid rgba(228,165,22,0.25)',
      boxShadow: '0 10px 30px rgba(0,0,0,0.18)',
    }}>
      <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between' }}>
        <div style={{ fontSize: 12, color: 'rgba(246,231,194,0.6)' }}>当前等级</div>
        <div className="tag tag-gold-solid">已达成</div>
      </div>
      <div className="font-num" style={{ fontSize: 28, fontWeight: 700, color: '#FFE0A0', marginTop: 6 }}>累计业绩 ¥58,400</div>
      <div className="bar-track" style={{ marginTop: 14 }}>
        <div className="fill" style={{ width: '38%' }}/>
      </div>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 8, fontSize: 11, color: 'rgba(246,231,194,0.65)' }}>
        <span>已达成 ¥58,400</span>
        <span>市级目标 ¥150,000</span>
      </div>
    </div>

    <div style={{ overflow: 'auto', height: 'calc(100% - 120px - 130px - 76px)', padding: '20px 16px 14px' }}>
      <div style={{ fontSize: 16, fontWeight: 700, marginBottom: 12 }}>等级晋升路径</div>

      {/* Active level */}
      <div style={{
        background: '#fff', borderRadius: 14, padding: '16px',
        border: '2px solid var(--gold-500)', marginBottom: 12,
        boxShadow: '0 4px 14px rgba(228,165,22,0.18)',
      }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <div style={{ width: 12, height: 12, borderRadius: '50%', background: 'var(--gold-500)' }}/>
            <div style={{ fontSize: 16, fontWeight: 700 }}>县级联营商</div>
          </div>
          <div style={{ background: '#D9F0E0', color: '#2F9E5C', fontSize: 11, fontWeight: 600, padding: '3px 10px', borderRadius: 6 }}>当前等级</div>
        </div>
        <div style={{ fontSize: 13, color: 'var(--text-2)', marginTop: 8 }}>入门金额：<span className="font-num text-1">¥50,000</span></div>

        <div style={{ background: '#FFF6DA', borderRadius: 10, padding: '12px 14px', marginTop: 12, border: '1px solid #F2E0B0' }}>
          <div style={{ fontSize: 12, fontWeight: 700, color: '#7A5610', marginBottom: 6 }}>核心权益</div>
          <div style={{ fontSize: 12, color: '#7A5610', lineHeight: 1.9 }}>
            • 直推首购 <span style={{ fontWeight: 700, color: '#D38A00' }}>20%</span> 佣金　• 招商奖励 <span style={{ fontWeight: 700, color: '#D38A00' }}>20%</span><br/>
            • 扶商奖励 <span style={{ fontWeight: 700, color: '#D38A00' }}>10%</span>　• 自购 <span style={{ fontWeight: 700, color: '#D38A00' }}>5</span> 折优惠<br/>
            • 可提交体验店申请
          </div>
        </div>
      </div>

      {/* Next level */}
      <div style={{ background: '#fff', borderRadius: 14, padding: '16px', border: '1px solid var(--line)', marginBottom: 12 }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <div style={{ width: 12, height: 12, borderRadius: '50%', background: '#C9BFA9' }}/>
            <div style={{ fontSize: 16, fontWeight: 700 }}>市级联营商</div>
          </div>
          <div className="tag tag-gold-solid">差价升级 ¥91,600</div>
        </div>
        <div style={{ fontSize: 13, color: 'var(--text-2)', marginTop: 8 }}>入门金额：<span className="font-num text-1">¥150,000</span></div>

        <div style={{ background: '#F4EEDF', borderRadius: 10, padding: '12px 14px', marginTop: 12 }}>
          <div style={{ fontSize: 12, fontWeight: 700, color: 'var(--text-2)', marginBottom: 6 }}>核心权益</div>
          <div style={{ fontSize: 12, color: 'var(--text-2)', lineHeight: 1.9 }}>
            • 直推首购 <span style={{ fontWeight: 700 }}>25%</span> 佣金　• 招商奖励 <span style={{ fontWeight: 700 }}>25%</span><br/>
            • 扶商奖励 <span style={{ fontWeight: 700 }}>12%</span>　• 自购 <span style={{ fontWeight: 700 }}>4.5</span> 折优惠<br/>
            • 优先体验店申请　• 团队管理权限
          </div>
        </div>
      </div>

      {/* Future levels */}
      {[
        { l: '省级联营商', amt: '500,000', diff: '441,600' },
        { l: '全国联营商', amt: '2,000,000', diff: '1,941,600' },
      ].map((lv, i) => (
        <div key={i} style={{ background: '#fff', borderRadius: 14, padding: '14px 16px', border: '1px solid var(--line)', marginBottom: 12 }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
              <div style={{ width: 12, height: 12, borderRadius: '50%', background: '#E4D4B0' }}/>
              <div style={{ fontSize: 15, fontWeight: 600, color: 'var(--text-2)' }}>{lv.l}</div>
            </div>
            <div style={{ fontSize: 11, color: 'var(--text-3)' }}>差 ¥{lv.diff}</div>
          </div>
          <div style={{ fontSize: 12, color: 'var(--text-3)', marginTop: 6 }}>入门金额：¥{lv.amt}</div>
        </div>
      ))}
    </div>

    <div style={{ position: 'absolute', left: 16, right: 16, bottom: 86 }}>
      <button className="btn-gold" style={{ width: '100%' }}>立即升级市级联营商</button>
    </div>
    <TabBar active="dist"/>
  </Phone>
);

// ============================================================
// F3. 合伙人等级
// ============================================================
const ScreenLevelPartner = () => (
  <Phone>
    <div style={{ background: 'var(--grad-brown)', color: '#F6E7C2', paddingBottom: 12 }}>
      <StatusBar theme="dark"/>
      <NavBar title="联营商等级体系" theme="on-brown"/>
      <div style={{ display: 'flex', padding: '4px 16px 0', fontSize: 14 }}>
        {['联营商等级', '合伙人等级', '奖励明细'].map((t, i) => (
          <div key={t} style={{
            flex: 1, textAlign: 'center', paddingBottom: 8,
            fontWeight: i === 1 ? 700 : 400,
            color: i === 1 ? '#FFE0A0' : 'rgba(246,231,194,0.6)',
            position: 'relative',
          }}>
            {t}
            {i === 1 && <div style={{ position: 'absolute', bottom: 0, left: '50%', transform: 'translateX(-50%)', width: 22, height: 2, background: 'var(--grad-gold)' }}/>}
          </div>
        ))}
      </div>
    </div>

    <div style={{ margin: '0 16px', marginTop: -22, zIndex: 2, position: 'relative',
      background: 'linear-gradient(135deg, #2A0E00 0%, #4A1E08 100%)',
      borderRadius: 14, padding: '16px 18px', border: '1px solid rgba(228,165,22,0.25)',
    }}>
      <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between' }}>
        <div style={{ fontSize: 12, color: 'rgba(246,231,194,0.6)' }}>当前身份</div>
        <div className="tag tag-gold-solid">生态合伙人</div>
      </div>
      <div className="font-num" style={{ fontSize: 28, fontWeight: 700, color: '#FFE0A0', marginTop: 6 }}>团队总业绩 ¥186,520</div>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 12, fontSize: 11, color: 'rgba(246,231,194,0.65)' }}>
        <div>直推 8 人 · 团队 23 人</div>
        <div>本月分润 ¥4,260</div>
      </div>
    </div>

    <div style={{ overflow: 'auto', height: 'calc(100% - 120px - 110px - 76px)', padding: '20px 16px 14px' }}>
      <div style={{ fontSize: 16, fontWeight: 700, marginBottom: 12 }}>合伙人成长路径</div>

      {[
        { l: '生态合伙人', amt: '县级联营商身份', cur: true,
          benefits: ['团队分润 5%', '招商奖励 10%', '扶商奖励 5%', '专属社群'] },
        { l: '区域合伙人', amt: '团队业绩 ¥500,000',
          benefits: ['团队分润 8%', '招商奖励 12%', '扶商奖励 7%', '区域培训权益'] },
        { l: '战略合伙人', amt: '团队业绩 ¥3,000,000',
          benefits: ['团队分润 10%', '招商奖励 15%', '股权激励池', '年度旅游福利'] },
        { l: '联合创始人', amt: '团队业绩 ¥10,000,000',
          benefits: ['团队分润 12%', '平台股权', '战略决策权', '年度分红'] },
      ].map((lv, i) => (
        <div key={i} style={{
          background: '#fff', borderRadius: 14, padding: 16,
          border: lv.cur ? '2px solid var(--gold-500)' : '1px solid var(--line)',
          marginBottom: 12,
          boxShadow: lv.cur ? '0 4px 14px rgba(228,165,22,0.18)' : 'none',
        }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
              <div style={{
                width: 32, height: 32, borderRadius: '50%',
                background: lv.cur ? 'var(--grad-gold)' : '#F4EEDF',
                display: 'flex', alignItems: 'center', justifyContent: 'center',
              }}>
                <Icon name="star" size={18} color={lv.cur ? '#3A1A00' : '#C9BFA9'}/>
              </div>
              <div>
                <div style={{ fontSize: 15, fontWeight: 700 }}>{lv.l}</div>
                <div style={{ fontSize: 12, color: 'var(--text-3)' }}>{lv.amt}</div>
              </div>
            </div>
            {lv.cur && <div className="tag tag-gold-solid">当前</div>}
          </div>
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: 6, marginTop: 12 }}>
            {lv.benefits.map(b => (
              <div key={b} style={{
                padding: '3px 10px', borderRadius: 6,
                background: lv.cur ? 'var(--gold-50)' : 'var(--cream-100)',
                color: lv.cur ? 'var(--gold-600)' : 'var(--text-2)',
                fontSize: 11, fontWeight: 500,
              }}>{b}</div>
            ))}
          </div>
        </div>
      ))}
    </div>
    <TabBar active="dist"/>
  </Phone>
);

// ============================================================
// F4. 奖励明细
// ============================================================
const ScreenRewardDetail = () => (
  <Phone>
    <div style={{ background: 'var(--grad-brown)', color: '#F6E7C2', paddingBottom: 12 }}>
      <StatusBar theme="dark"/>
      <NavBar title="联营商等级体系" theme="on-brown"/>
      <div style={{ display: 'flex', padding: '4px 16px 0', fontSize: 14 }}>
        {['联营商等级', '合伙人等级', '奖励明细'].map((t, i) => (
          <div key={t} style={{
            flex: 1, textAlign: 'center', paddingBottom: 8,
            fontWeight: i === 2 ? 700 : 400,
            color: i === 2 ? '#FFE0A0' : 'rgba(246,231,194,0.6)',
            position: 'relative',
          }}>
            {t}
            {i === 2 && <div style={{ position: 'absolute', bottom: 0, left: '50%', transform: 'translateX(-50%)', width: 22, height: 2, background: 'var(--grad-gold)' }}/>}
          </div>
        ))}
      </div>
    </div>

    <div style={{ margin: '0 16px', marginTop: -22, zIndex: 2, position: 'relative',
      background: 'var(--grad-gold)', borderRadius: 14, padding: '16px 18px',
      color: '#3A1A00', boxShadow: '0 10px 30px rgba(0,0,0,0.12)',
    }}>
      <div style={{ fontSize: 12, color: '#7A4810' }}>本月奖励合计</div>
      <div className="font-num" style={{ fontSize: 32, fontWeight: 700, marginTop: 4 }}>¥4,260.80</div>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 14, fontSize: 12 }}>
        <div>
          <div style={{ fontWeight: 700 }} className="font-num">¥1,892</div>
          <div style={{ opacity: 0.7 }}>佣金</div>
        </div>
        <div>
          <div style={{ fontWeight: 700 }} className="font-num">¥1,560</div>
          <div style={{ opacity: 0.7 }}>招商</div>
        </div>
        <div>
          <div style={{ fontWeight: 700 }} className="font-num">¥608</div>
          <div style={{ opacity: 0.7 }}>扶商</div>
        </div>
        <div>
          <div style={{ fontWeight: 700 }} className="font-num">¥200</div>
          <div style={{ opacity: 0.7 }}>自购优惠</div>
        </div>
      </div>
    </div>

    <div style={{ overflow: 'auto', height: 'calc(100% - 120px - 130px - 76px)', padding: '14px 16px' }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 10 }}>
        <div style={{ fontSize: 15, fontWeight: 700 }}>奖励明细</div>
        <div style={{ fontSize: 12, color: 'var(--text-2)', display: 'flex', alignItems: 'center', gap: 4 }}>
          2026 年 5 月 <Icon name="chevron-down" size={14} color="#5E5142"/>
        </div>
      </div>

      <div style={{ background: '#fff', borderRadius: 12 }}>
        {[
          { t: '直推佣金 · 茅台飞天 500ml', amt: '+596.00', date: '今日 14:32', cat: '佣金', cc: '#D38A00' },
          { t: '招商奖励 · 王女士入驻', amt: '+1,200.00', date: '今日 11:08', cat: '招商', cc: '#D1467A' },
          { t: '扶商奖励 · 团队成员业绩', amt: '+208.40', date: '昨日 17:45', cat: '扶商', cc: '#3A6FD1' },
          { t: '自购优惠 · 国窖1573 ×1', amt: '+98.00', date: '昨日 09:12', cat: '自购', cc: '#2F9E5C' },
          { t: '直推佣金 · 五粮液普五 500ml', amt: '+276.00', date: '5月14日', cat: '佣金', cc: '#D38A00' },
          { t: '招商奖励 · 张先生入驻', amt: '+360.00', date: '5月12日', cat: '招商', cc: '#D1467A' },
          { t: '直推佣金 · 红酒礼盒 ×2', amt: '+178.80', date: '5月10日', cat: '佣金', cc: '#D38A00' },
        ].map((r, i) => (
          <div key={i} style={{ display: 'flex', alignItems: 'center', gap: 12, padding: '14px 16px', borderBottom: '1px solid var(--line-soft)' }}>
            <div style={{ width: 34, height: 34, borderRadius: '50%', background: r.cc + '18', color: r.cc, fontSize: 10, fontWeight: 700, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>{r.cat}</div>
            <div style={{ flex: 1, minWidth: 0 }}>
              <div style={{ fontSize: 13, fontWeight: 500, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{r.t}</div>
              <div style={{ fontSize: 11, color: 'var(--text-3)', marginTop: 2 }}>{r.date}</div>
            </div>
            <div className="font-num" style={{ fontSize: 15, fontWeight: 700, color: 'var(--gold-500)' }}>{r.amt}</div>
          </div>
        ))}
      </div>
    </div>
    <TabBar active="dist"/>
  </Phone>
);

// ============================================================
// F5. 佣金明细
// ============================================================
const ScreenCommissionDetail = () => (
  <Phone>
    <div style={{ background: '#fff' }}>
      <StatusBar theme="light"/>
      <NavBar title="佣金明细" theme="on-light" right={<Icon name="filter" size={20}/>}/>
    </div>
    {/* Stats */}
    <div style={{ background: 'var(--grad-gold)', margin: '0 16px', borderRadius: 12, padding: '14px 18px', color: '#3A1A00' }}>
      <div style={{ fontSize: 12, color: '#7A4810' }}>累计佣金收入</div>
      <div className="font-num" style={{ fontSize: 26, fontWeight: 700, marginTop: 2 }}>¥18,624.00</div>
      <div style={{ display: 'flex', gap: 18, marginTop: 12, fontSize: 12 }}>
        <div><span className="font-num" style={{ fontWeight: 700 }}>¥1,892</span> · 本月</div>
        <div><span className="font-num" style={{ fontWeight: 700 }}>¥386</span> · 今日</div>
        <div style={{ marginLeft: 'auto', opacity: 0.7 }}>待结算 ¥820</div>
      </div>
    </div>

    {/* Filter chips */}
    <div style={{ display: 'flex', gap: 8, padding: '14px 16px 4px', overflow: 'auto' }}>
      {['全部', '直推佣金', '次级佣金', '分享佣金', '推广佣金'].map((c, i) => (
        <div key={c} style={{
          padding: '5px 14px', borderRadius: 999,
          background: i === 0 ? 'var(--gold-50)' : 'var(--bg-app)',
          border: i === 0 ? '1px solid var(--gold-300)' : 'none',
          color: i === 0 ? 'var(--gold-600)' : 'var(--text-2)',
          fontSize: 12, fontWeight: i === 0 ? 600 : 400, flexShrink: 0,
        }}>{c}</div>
      ))}
    </div>

    <div style={{ overflow: 'auto', height: 'calc(100% - 88px - 110px - 50px)', padding: '0 16px 24px' }}>
      <div style={{ fontSize: 12, color: 'var(--text-3)', margin: '10px 0 8px' }}>2026 年 5 月</div>
      <div style={{ background: '#fff', borderRadius: 12 }}>
        {[
          { p: '茅台飞天 53度 500ml', n: '王晓明', t: '直推佣金 · 20%', amt: '+596.00', date: '今日 14:32', k: 'maotai', s: '已到账' },
          { p: '五粮液普五 52度 500ml', n: '陈丽华', t: '直推佣金 · 20%', amt: '+276.00', date: '今日 11:18', k: 'wuliang', s: '已到账' },
          { p: '法国波尔多干红 750ml', n: '李志强', t: '分享佣金 · 15%', amt: '+44.70', date: '昨日 19:42', k: 'redwine', s: '已到账' },
          { p: '威士忌 12年 700ml', n: '赵小婷', t: '次级佣金 · 10%', amt: '+68.80', date: '5月14日', k: 'whisky', s: '待结算' },
          { p: '国窖1573 经典装', n: '周文君', t: '直推佣金 · 20%', amt: '+196.00', date: '5月12日', k: 'luzhou', s: '已到账' },
        ].map((r, i) => (
          <div key={i} style={{ display: 'flex', gap: 10, padding: '12px 14px', borderBottom: '1px solid var(--line-soft)' }}>
            <div style={{ width: 44, height: 44, borderRadius: 8, overflow: 'hidden', flexShrink: 0 }}>
              <ProdImg kind={r.k}/>
            </div>
            <div style={{ flex: 1, minWidth: 0 }}>
              <div style={{ fontSize: 13, fontWeight: 500, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{r.p}</div>
              <div style={{ fontSize: 11, color: 'var(--text-3)', marginTop: 2 }}>{r.t} · {r.n}</div>
              <div style={{ fontSize: 11, color: 'var(--text-3)', marginTop: 2 }}>{r.date}</div>
            </div>
            <div style={{ textAlign: 'right' }}>
              <div className="font-num" style={{ fontSize: 15, fontWeight: 700, color: 'var(--gold-500)' }}>{r.amt}</div>
              <div style={{ fontSize: 10, color: r.s === '已到账' ? 'var(--green-500)' : 'var(--text-3)', marginTop: 4 }}>{r.s}</div>
            </div>
          </div>
        ))}
      </div>
    </div>
    <HomeIndicator/>
  </Phone>
);

// ============================================================
// F6. 提现
// ============================================================
const ScreenWithdraw = () => (
  <Phone>
    <div style={{ background: '#fff' }}>
      <StatusBar theme="light"/>
      <NavBar title="提现" theme="on-light" right={<span style={{ fontSize: 13, color: 'var(--gold-500)' }}>提现记录</span>}/>
    </div>
    <div style={{ padding: '14px 16px', background: 'var(--bg-app)', height: 'calc(100% - 88px)' }}>
      <div style={{ background: 'var(--grad-gold)', borderRadius: 14, padding: '18px 18px 16px', color: '#3A1A00' }}>
        <div style={{ fontSize: 12, color: '#7A4810' }}>可提现余额</div>
        <div className="font-num" style={{ fontSize: 30, fontWeight: 700, marginTop: 4 }}>¥24,860.00</div>
        <div style={{ fontSize: 11, marginTop: 4 }}>冻结余额 <span className="font-num">¥3,200.00</span></div>
      </div>

      <div style={{ background: '#fff', borderRadius: 12, padding: '16px', marginTop: 14 }}>
        <div style={{ fontSize: 13, color: 'var(--text-2)' }}>提现到</div>
        <div style={{ display: 'flex', alignItems: 'center', gap: 12, marginTop: 10, padding: '12px 12px', background: 'var(--bg-app)', borderRadius: 10 }}>
          <div style={{ width: 36, height: 36, borderRadius: '50%', background: '#12B95B', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <Icon name="wechat" size={22} color="#fff"/>
          </div>
          <div style={{ flex: 1 }}>
            <div style={{ fontSize: 14, fontWeight: 600 }}>微信钱包</div>
            <div style={{ fontSize: 11, color: 'var(--text-3)' }}>已实名认证 · 李明远</div>
          </div>
          <Icon name="chevron" size={14} color="#C9BFA9"/>
        </div>

        <div style={{ marginTop: 18, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <div style={{ fontSize: 13, color: 'var(--text-2)' }}>提现金额</div>
          <div style={{ fontSize: 12, color: 'var(--gold-500)' }}>全部提现</div>
        </div>
        <div style={{ display: 'flex', alignItems: 'baseline', marginTop: 10, paddingBottom: 12, borderBottom: '1px solid var(--line-soft)' }}>
          <div style={{ fontSize: 28, fontWeight: 700, color: 'var(--text-1)' }}>¥</div>
          <div className="font-num" style={{ fontSize: 36, fontWeight: 700, marginLeft: 4 }}>10,000.00</div>
        </div>
        <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 8, fontSize: 11, color: 'var(--text-3)' }}>
          <div>手续费 ¥0 · 实际到账 <span className="font-num text-1">¥10,000.00</span></div>
        </div>

        <div style={{ display: 'flex', gap: 8, marginTop: 16 }}>
          {['100', '500', '1000', '5000', '全部'].map((a, i) => (
            <div key={a} style={{
              flex: 1, padding: '8px 0', textAlign: 'center',
              borderRadius: 8, fontSize: 12,
              background: i === 4 ? 'var(--gold-50)' : 'var(--bg-app)',
              color: i === 4 ? 'var(--gold-600)' : 'var(--text-2)',
              fontWeight: i === 4 ? 600 : 400,
            }}>{a}</div>
          ))}
        </div>
      </div>

      <div style={{ marginTop: 14, padding: '12px 14px', background: 'var(--gold-50)', borderRadius: 10, fontSize: 12, color: '#7A5610', lineHeight: 1.6 }}>
        <div style={{ fontWeight: 600, marginBottom: 4 }}>提现说明</div>
        · 单笔最低 ¥100，最高 ¥50,000<br/>
        · 提现到账时间 1-3 个工作日<br/>
        · 当月累计提现 5 次内免手续费
      </div>

      <button className="btn-gold" style={{ width: '100%', marginTop: 22 }}>确认提现</button>
    </div>
    <HomeIndicator/>
  </Phone>
);

// ============================================================
// F7. 提现记录
// ============================================================
const ScreenWithdrawHistory = () => (
  <Phone>
    <div style={{ background: '#fff' }}>
      <StatusBar theme="light"/>
      <NavBar title="提现记录" theme="on-light"/>
    </div>
    <div style={{ background: 'var(--bg-app)', padding: '12px 16px 24px', overflow: 'auto', height: 'calc(100% - 88px)' }}>
      {[
        { d: '2026-05-15 09:32', amt: '10,000.00', s: '到账成功', sc: '#2F9E5C', to: '微信钱包' },
        { d: '2026-05-08 14:18', amt: '5,000.00', s: '到账成功', sc: '#2F9E5C', to: '微信钱包' },
        { d: '2026-05-02 10:05', amt: '8,500.00', s: '处理中', sc: '#D38A00', to: '微信钱包' },
        { d: '2026-04-22 16:42', amt: '12,000.00', s: '到账成功', sc: '#2F9E5C', to: '微信钱包' },
        { d: '2026-04-15 09:18', amt: '2,000.00', s: '已驳回', sc: '#D6453B', to: '微信钱包', reason: '银行账户异常' },
        { d: '2026-04-08 11:30', amt: '6,800.00', s: '到账成功', sc: '#2F9E5C', to: '微信钱包' },
      ].map((r, i) => (
        <div key={i} style={{ background: '#fff', borderRadius: 12, padding: '14px 16px', marginBottom: 10 }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <div style={{ fontSize: 13, fontWeight: 600 }}>提现到{r.to}</div>
            <div style={{ fontSize: 13, color: r.sc, fontWeight: 600 }}>{r.s}</div>
          </div>
          <div className="font-num" style={{ fontSize: 24, fontWeight: 700, marginTop: 8 }}>¥{r.amt}</div>
          <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 8, fontSize: 11, color: 'var(--text-3)' }}>
            <div>{r.d}</div>
            <div>手续费 ¥0</div>
          </div>
          {r.reason && (
            <div style={{ marginTop: 8, padding: '8px 10px', background: '#FFE9E6', borderRadius: 6, fontSize: 11, color: '#D6453B' }}>
              驳回原因：{r.reason}
            </div>
          )}
        </div>
      ))}
    </div>
    <HomeIndicator/>
  </Phone>
);

// ============================================================
// F8. 团队管理
// ============================================================
const ScreenTeam = () => (
  <Phone>
    <div style={{ background: 'var(--grad-brown)', color: '#F6E7C2', paddingBottom: 18 }}>
      <StatusBar theme="dark"/>
      <NavBar title="团队管理" theme="on-brown" right={<Icon name="search" size={20} color="#FFD685"/>}/>
      <div style={{ padding: '4px 16px' }}>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 4 }}>
          {[
            { l: '团队总人数', v: '23' },
            { l: '直推', v: '8' },
            { l: '本月新增', v: '4' },
            { l: '团队业绩', v: '¥186k' },
          ].map(s => (
            <div key={s.l} style={{ textAlign: 'center' }}>
              <div className="font-num" style={{ fontSize: 22, fontWeight: 700, color: '#FFE0A0' }}>{s.v}</div>
              <div style={{ fontSize: 10, color: 'rgba(246,231,194,0.6)', marginTop: 4 }}>{s.l}</div>
            </div>
          ))}
        </div>
      </div>
    </div>

    {/* Tabs */}
    <div style={{ background: '#fff', display: 'flex', borderBottom: '1px solid var(--line-soft)' }}>
      {[
        { l: '直推团队', n: 8, a: true },
        { l: '次级团队', n: 15 },
        { l: '体验店', n: 2 },
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

    <div style={{ overflow: 'auto', height: 'calc(100% - 124px - 44px)', padding: '8px 16px 24px' }}>
      {[
        { n: '王晓明', t: '县级联营商', d: '5月15日入驻', a: '52,800', new: true, k: 'man1' },
        { n: '陈丽华', t: '县级联营商', d: '5月10日入驻', a: '48,200', k: 'woman1' },
        { n: '李志强', t: '县级联营商', d: '4月28日入驻', a: '83,500', k: 'man2' },
        { n: '赵小婷', t: '县级联营商', d: '4月15日入驻', a: '36,800', k: 'woman2' },
        { n: '周文君', t: '县级联营商', d: '4月02日入驻', a: '129,400', k: 'woman1' },
        { n: '吴建国', t: '县级联营商', d: '3月22日入驻', a: '67,300', k: 'man1' },
        { n: '林梅芳', t: '县级联营商', d: '3月10日入驻', a: '54,200', k: 'woman2' },
        { n: '黄家辉', t: '县级联营商', d: '2月18日入驻', a: '92,100', k: 'man2' },
      ].map((m, i) => (
        <div key={i} style={{ background: '#fff', borderRadius: 12, padding: '12px 14px', marginBottom: 8, display: 'flex', alignItems: 'center', gap: 12 }}>
          <Avatar size={42} seed={m.n[0]} kind={m.k}/>
          <div style={{ flex: 1, minWidth: 0 }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
              <div style={{ fontSize: 14, fontWeight: 600 }}>{m.n}</div>
              <span className="tag tag-gold" style={{ fontSize: 10 }}>{m.t}</span>
              {m.new && <span className="tag" style={{ background: '#FFE5E2', color: '#D6453B', fontSize: 10 }}>NEW</span>}
            </div>
            <div style={{ fontSize: 11, color: 'var(--text-3)', marginTop: 4 }}>{m.d}</div>
          </div>
          <div style={{ textAlign: 'right' }}>
            <div className="font-num" style={{ fontSize: 15, fontWeight: 700 }}>¥{m.a}</div>
            <div style={{ fontSize: 10, color: 'var(--text-3)', marginTop: 2 }}>累计业绩</div>
          </div>
        </div>
      ))}
    </div>
    <HomeIndicator/>
  </Phone>
);

// ============================================================
// F9. 团队成员详情
// ============================================================
const ScreenTeamMember = () => (
  <Phone>
    <div style={{ background: 'var(--grad-brown)', color: '#F6E7C2', paddingBottom: 20 }}>
      <StatusBar theme="dark"/>
      <NavBar title="成员详情" theme="on-brown" right={<Icon name="chat" size={20} color="#FFD685"/>}/>
      <div style={{ padding: '8px 20px 0', display: 'flex', alignItems: 'center', gap: 14 }}>
        <Avatar size={64} kind="woman1" seed="周"/>
        <div>
          <div style={{ fontSize: 18, fontWeight: 700, color: '#FFE0A0' }}>周文君</div>
          <div style={{ fontSize: 12, color: 'rgba(246,231,194,0.7)', marginTop: 4 }}>139 **** 2046 · 4月02日入驻</div>
          <div style={{ display: 'flex', gap: 6, marginTop: 6 }}>
            <span className="tag tag-gold-solid">县级联营商</span>
            <span style={{ background: 'rgba(255,224,160,0.18)', color: '#FFD685', fontSize: 10, padding: '3px 8px', borderRadius: 4 }}>直推</span>
          </div>
        </div>
      </div>
    </div>

    <div style={{ padding: '14px 16px 20px', overflow: 'auto', height: 'calc(100% - 165px)' }}>
      {/* Stats */}
      <div style={{ background: '#fff', borderRadius: 12, padding: '14px 0', display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)' }}>
        {[
          { l: '累计业绩', v: '¥129,400' },
          { l: '本月业绩', v: '¥18,200' },
          { l: '直推团队', v: '5 人' },
        ].map(s => (
          <div key={s.l} style={{ textAlign: 'center' }}>
            <div className="font-num" style={{ fontSize: 17, fontWeight: 700 }}>{s.v}</div>
            <div style={{ fontSize: 11, color: 'var(--text-3)', marginTop: 4 }}>{s.l}</div>
          </div>
        ))}
      </div>

      {/* Growth chart placeholder */}
      <div style={{ background: '#fff', borderRadius: 12, padding: 14, marginTop: 12 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 12 }}>
          <div style={{ fontSize: 14, fontWeight: 700 }}>业绩走势</div>
          <div style={{ fontSize: 12, color: 'var(--text-2)' }}>近 6 月 ›</div>
        </div>
        <svg width="100%" height="120" viewBox="0 0 320 120">
          <defs>
            <linearGradient id="growth-grad" x1="0" x2="0" y1="0" y2="1">
              <stop offset="0" stopColor="#E4A516" stopOpacity="0.3"/>
              <stop offset="1" stopColor="#E4A516" stopOpacity="0"/>
            </linearGradient>
          </defs>
          <path d="M0 90 L60 70 L120 80 L180 50 L240 30 L320 20 L320 120 L0 120 Z" fill="url(#growth-grad)"/>
          <path d="M0 90 L60 70 L120 80 L180 50 L240 30 L320 20" fill="none" stroke="#D38A00" strokeWidth="2"/>
          {[[0, 90], [60, 70], [120, 80], [180, 50], [240, 30], [320, 20]].map(([x, y], i) => (
            <circle key={i} cx={x} cy={y} r="3.5" fill="#fff" stroke="#D38A00" strokeWidth="2"/>
          ))}
        </svg>
        <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 10, color: 'var(--text-3)', marginTop: 4 }}>
          <span>12月</span><span>1月</span><span>2月</span><span>3月</span><span>4月</span><span>5月</span>
        </div>
      </div>

      {/* Recent orders */}
      <div style={{ fontSize: 14, fontWeight: 700, margin: '14px 0 10px' }}>近期订单</div>
      <div style={{ background: '#fff', borderRadius: 12 }}>
        {[
          { p: '茅台飞天 53度 500ml', amt: '5,960', date: '5/16 14:23', k: 'maotai' },
          { p: '五粮液普五 52度 500ml', amt: '2,760', date: '5/14 10:08', k: 'wuliang' },
          { p: '国窖1573 经典装 500ml', amt: '980', date: '5/10 16:42', k: 'luzhou' },
        ].map((o, i) => (
          <div key={i} style={{ display: 'flex', gap: 10, padding: 12, borderBottom: i < 2 ? '1px solid var(--line-soft)' : 'none' }}>
            <div style={{ width: 44, height: 44, borderRadius: 8, overflow: 'hidden' }}><ProdImg kind={o.k}/></div>
            <div style={{ flex: 1, minWidth: 0 }}>
              <div style={{ fontSize: 13 }}>{o.p}</div>
              <div style={{ fontSize: 11, color: 'var(--text-3)', marginTop: 4 }}>{o.date}</div>
            </div>
            <div className="price font-num" style={{ fontSize: 14 }}><span className="sym">¥</span>{o.amt}</div>
          </div>
        ))}
      </div>
    </div>
  </Phone>
);

// ============================================================
// F10. 分享邀请
// ============================================================
const ScreenInviteShare = () => (
  <Phone>
    <div style={{ background: 'var(--grad-brown)', color: '#F6E7C2', minHeight: '100%' }}>
      <StatusBar theme="dark"/>
      <NavBar title="分享邀请" theme="on-brown" right={<span style={{ fontSize: 13, color: '#FFD685' }}>邀请记录</span>}/>

      <div style={{ padding: '12px 24px 32px', textAlign: 'center' }}>
        <div style={{ fontSize: 11, letterSpacing: 4, color: 'rgba(246,231,194,0.55)', marginTop: 8 }}>INVITE · EARN · GROW</div>
        <div style={{ fontSize: 26, fontWeight: 700, color: '#FFE0A0', marginTop: 6 }}>邀好友 · 共财富</div>
        <div style={{ fontSize: 13, color: 'rgba(246,231,194,0.7)', marginTop: 8, lineHeight: 1.6 }}>
          好友通过你的邀请码加入<br/>
          首单消费可获 <span style={{ color: '#FFD646', fontWeight: 700, fontSize: 18 }}>20%</span> 推广佣金
        </div>
      </div>

      {/* Invite code card */}
      <div style={{ margin: '0 16px', background: 'rgba(255,255,255,0.06)', border: '1px solid rgba(228,165,22,0.25)', borderRadius: 14, padding: 18 }}>
        <div style={{ fontSize: 11, color: 'rgba(246,231,194,0.6)' }}>专属邀请码</div>
        <div style={{ display: 'flex', alignItems: 'center', marginTop: 10 }}>
          <div className="font-num" style={{ fontSize: 30, fontWeight: 700, color: '#FFE0A0', letterSpacing: 6 }}>CPH8821</div>
          <button style={{ marginLeft: 'auto', height: 30, padding: '0 14px', borderRadius: 999, border: '1px solid #FFD685', background: 'transparent', color: '#FFD685', fontSize: 12, fontWeight: 600 }}>复制</button>
        </div>
      </div>

      {/* CTA */}
      <div style={{ padding: '16px 16px 0', display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', gap: 10 }}>
        <button className="btn-wechat" style={{ width: '100%', height: 50 }}>
          <Icon name="wechat" size={20}/> 微信邀请
        </button>
        <button className="btn-gold" style={{ width: '100%', height: 50 }}>
          <Icon name="qr" size={18} color="#3A1A00"/> 生成海报
        </button>
      </div>

      {/* Steps */}
      <div style={{ margin: '20px 16px 0', background: 'rgba(255,255,255,0.05)', borderRadius: 14, padding: 18 }}>
        <div style={{ fontSize: 14, fontWeight: 700, color: '#FFE0A0', marginBottom: 14 }}>邀请奖励 · 三步到账</div>
        {[
          { i: 1, l: '分享邀请码', d: '微信、海报、链接三种方式' },
          { i: 2, l: '好友注册并完成首单', d: '好友首单消费立即触发奖励' },
          { i: 3, l: '佣金到账', d: '好友确认收货 7 天后，自动结算' },
        ].map(s => (
          <div key={s.i} style={{ display: 'flex', alignItems: 'flex-start', gap: 12, paddingBottom: 12 }}>
            <div style={{
              width: 28, height: 28, borderRadius: '50%',
              background: 'var(--grad-gold)', color: '#3A1A00',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              fontSize: 13, fontWeight: 700, flexShrink: 0,
            }}>{s.i}</div>
            <div>
              <div style={{ fontSize: 14, fontWeight: 600, color: '#FFE0A0' }}>{s.l}</div>
              <div style={{ fontSize: 12, color: 'rgba(246,231,194,0.6)', marginTop: 2 }}>{s.d}</div>
            </div>
          </div>
        ))}
      </div>

      {/* Stats */}
      <div style={{ margin: '16px 16px 30px', display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 10 }}>
        {[
          { l: '已邀请', v: '23' },
          { l: '本月新增', v: '4' },
          { l: '累计佣金', v: '¥18,624' },
        ].map(s => (
          <div key={s.l} style={{ background: 'rgba(255,255,255,0.05)', borderRadius: 10, padding: 12, textAlign: 'center', border: '1px solid rgba(228,165,22,0.18)' }}>
            <div className="font-num" style={{ fontSize: 18, fontWeight: 700, color: '#FFE0A0' }}>{s.v}</div>
            <div style={{ fontSize: 10, color: 'rgba(246,231,194,0.6)', marginTop: 4 }}>{s.l}</div>
          </div>
        ))}
      </div>
      <HomeIndicator dark/>
    </div>
  </Phone>
);

// ============================================================
// F11. 邀请海报
// ============================================================
const ScreenInvitePoster = () => (
  <Phone dark>
    <div style={{ position: 'absolute', inset: 0, background: 'linear-gradient(180deg, #1A0A02 0%, #2A0E00 100%)' }}/>
    <StatusBar theme="dark"/>
    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '4px 16px 0', color: '#F6E7C2' }}>
      <Icon name="back" size={22} color="#FFD685"/>
      <div style={{ fontSize: 16, fontWeight: 600 }}>邀请海报</div>
      <Icon name="reload" size={22} color="#FFD685"/>
    </div>

    {/* Poster */}
    <div style={{
      margin: '20px 30px 0', background: 'linear-gradient(180deg, #2A0E00, #4A1E08)',
      borderRadius: 16, padding: 16, position: 'relative', overflow: 'hidden',
      boxShadow: '0 14px 40px rgba(0,0,0,0.5)',
    }}>
      <div style={{ position: 'absolute', top: -30, right: -40, width: 160, height: 160, borderRadius: '50%', background: 'radial-gradient(circle, rgba(228,165,22,0.25), transparent 60%)' }}/>

      <div style={{ display: 'flex', alignItems: 'center', gap: 10, position: 'relative' }}>
        <Avatar size={42} kind="boss"/>
        <div>
          <div style={{ fontSize: 14, fontWeight: 600, color: '#FFE0A0' }}>李明远 邀请你加入</div>
          <div style={{ fontSize: 11, color: 'rgba(246,231,194,0.6)' }}>县级联营商 · 生态合伙人</div>
        </div>
      </div>

      <div style={{ textAlign: 'center', marginTop: 24, position: 'relative' }}>
        <BrandMark size={56}/>
        <div style={{ fontSize: 26, fontWeight: 700, color: '#FFE0A0', letterSpacing: 4, marginTop: 12 }}>醇 品 汇</div>
        <div style={{ fontSize: 12, color: 'rgba(246,231,194,0.7)', letterSpacing: 2, marginTop: 4 }}>甄选美酒 · 共创财富</div>
        <div style={{ fontSize: 20, fontWeight: 700, color: '#fff', marginTop: 22 }}>注册即享</div>
        <div className="font-num" style={{ fontSize: 42, fontWeight: 800, color: '#FFD646', marginTop: 4 }}>20%</div>
        <div style={{ fontSize: 12, color: 'rgba(246,231,194,0.65)' }}>首单推广佣金</div>
      </div>

      <div style={{
        background: '#fff', borderRadius: 10, padding: '12px 14px', marginTop: 22,
        display: 'flex', alignItems: 'center', gap: 12,
      }}>
        <div style={{ flex: 1 }}>
          <div style={{ fontSize: 11, color: 'var(--text-3)' }}>专属邀请码</div>
          <div className="font-num" style={{ fontSize: 22, fontWeight: 700, color: '#1A1006', letterSpacing: 4, marginTop: 2 }}>CPH8821</div>
        </div>
        <div style={{ width: 70, height: 70, borderRadius: 6, background: '#fff', border: '1px solid var(--line)', padding: 4 }}>
          <div style={{ width: '100%', height: '100%', background:
            `radial-gradient(circle at 20% 20%, #1A1006 25%, transparent 25%) 0 0/14px 14px,
             radial-gradient(circle at 80% 20%, #1A1006 25%, transparent 25%) 0 0/14px 14px,
             radial-gradient(circle at 20% 80%, #1A1006 25%, transparent 25%) 0 0/14px 14px,
             linear-gradient(#1A1006 0 0) 8px 8px/4px 4px,
             linear-gradient(#1A1006 0 0) 32px 14px/4px 4px,
             linear-gradient(#1A1006 0 0) 20px 28px/6px 6px,
             linear-gradient(#1A1006 0 0) 38px 36px/4px 4px`,
            backgroundRepeat: 'no-repeat',
          }}/>
        </div>
      </div>
      <div style={{ fontSize: 10, color: 'rgba(246,231,194,0.5)', textAlign: 'center', marginTop: 10, letterSpacing: 2 }}>
        长按识别二维码 · 立即加入醇品汇
      </div>
    </div>

    {/* Actions */}
    <div style={{ position: 'absolute', left: 16, right: 16, bottom: 26, display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: 8 }}>
      <button style={{ height: 44, borderRadius: 10, background: 'rgba(255,255,255,0.08)', color: '#FFD685', border: '1px solid rgba(255,214,133,0.3)', fontSize: 12, fontWeight: 600 }}>
        <Icon name="edit" size={16} color="#FFD685" style={{ verticalAlign: 'middle', marginRight: 4 }}/>保存
      </button>
      <button style={{ height: 44, borderRadius: 10, background: 'var(--green-wechat)', color: '#fff', border: 'none', fontSize: 12, fontWeight: 600 }}>
        <Icon name="wechat" size={16} style={{ verticalAlign: 'middle', marginRight: 4 }}/>微信
      </button>
      <button style={{ height: 44, borderRadius: 10, background: 'var(--grad-gold)', color: '#3A1A00', border: 'none', fontSize: 12, fontWeight: 600 }}>
        <Icon name="people" size={16} color="#3A1A00" style={{ verticalAlign: 'middle', marginRight: 4 }}/>朋友圈
      </button>
    </div>
    <HomeIndicator dark/>
  </Phone>
);

// ============================================================
// F12. 体验店申请
// ============================================================
const ScreenStoreApply = () => (
  <Phone>
    <div style={{ background: '#fff' }}>
      <StatusBar theme="light"/>
      <NavBar title="体验店申请" theme="on-light"/>
    </div>
    <div style={{ background: 'var(--bg-app)', overflow: 'auto', height: 'calc(100% - 88px - 80px)', paddingBottom: 16 }}>
      {/* Banner */}
      <div style={{ margin: '12px 16px 0', background: 'var(--grad-gold)', borderRadius: 14, padding: '18px', color: '#3A1A00', position: 'relative', overflow: 'hidden' }}>
        <div style={{ fontSize: 11, letterSpacing: 3, color: '#7A4810' }}>EXPERIENCE STORE</div>
        <div style={{ fontSize: 20, fontWeight: 700, marginTop: 4 }}>申请醇品汇体验店</div>
        <div style={{ fontSize: 12, color: '#7A4810', marginTop: 6, lineHeight: 1.6 }}>
          开店 = 线下品鉴会 + 高端商务礼赠 + 同城配送一体
        </div>
        <Icon name="shop" size={64} color="rgba(58,26,0,0.18)" style={{ position: 'absolute', right: -10, bottom: -16 }}/>
      </div>

      {/* Form */}
      <div style={{ background: '#fff', marginTop: 12 }}>
        <div style={{ fontSize: 13, fontWeight: 700, padding: '12px 16px 8px' }}>店主信息</div>
        <div className="form-row"><div className="label">姓名</div><div className="field">李明远</div></div>
        <div className="form-row"><div className="label">手机号</div><div className="field">138 **** 8888</div></div>
        <div className="form-row"><div className="label">身份证</div><div className="field" style={{ color: 'var(--text-3)' }}>请输入</div></div>
      </div>

      <div style={{ background: '#fff', marginTop: 12 }}>
        <div style={{ fontSize: 13, fontWeight: 700, padding: '12px 16px 8px' }}>店铺信息</div>
        <div className="form-row"><div className="label">店铺名称</div><input className="field" placeholder="请输入"/></div>
        <div className="form-row"><div className="label">所在地区</div><div className="field" style={{ color: 'var(--text-3)' }}>请选择</div><Icon name="chevron" size={14} color="#C9BFA9"/></div>
        <div className="form-row"><div className="label">详细地址</div><input className="field" placeholder="街道、门牌号"/></div>
        <div className="form-row"><div className="label">营业面积</div><div className="field" style={{ color: 'var(--text-3)' }}>50-100㎡</div><Icon name="chevron" size={14} color="#C9BFA9"/></div>
        <div className="form-row" style={{ alignItems: 'flex-start', paddingTop: 12 }}>
          <div className="label" style={{ paddingTop: 2 }}>店铺照片</div>
          <div className="field" style={{ display: 'flex', gap: 8 }}>
            <div style={{ width: 64, height: 64, borderRadius: 8, overflow: 'hidden' }}><ProdImg kind="redwine"/></div>
            <div style={{ width: 64, height: 64, borderRadius: 8, border: '1.5px dashed var(--line)', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#9B8E7C' }}>
              <Icon name="plus" size={22} color="#9B8E7C"/>
            </div>
          </div>
        </div>
      </div>

      <div style={{ background: '#fff', marginTop: 12, padding: '12px 16px' }}>
        <div style={{ fontSize: 13, fontWeight: 700, marginBottom: 10 }}>店铺类型</div>
        <div style={{ display: 'flex', gap: 8 }}>
          {[
            { l: '体验店', d: '50-100㎡', a: true },
            { l: '形象店', d: '100-300㎡' },
            { l: '旗舰店', d: '300㎡+' },
          ].map(t => (
            <div key={t.l} style={{
              flex: 1, padding: '10px 6px', borderRadius: 10,
              background: t.a ? 'var(--gold-50)' : 'var(--bg-app)',
              border: t.a ? '1.5px solid var(--gold-500)' : '1px solid transparent',
              textAlign: 'center',
            }}>
              <div style={{ fontSize: 13, fontWeight: 600, color: t.a ? 'var(--gold-600)' : 'var(--text-1)' }}>{t.l}</div>
              <div style={{ fontSize: 10, color: 'var(--text-3)', marginTop: 2 }}>{t.d}</div>
            </div>
          ))}
        </div>
      </div>

      <div style={{ margin: '12px 16px 0', padding: '12px 14px', background: 'var(--gold-50)', borderRadius: 10, fontSize: 12, color: '#7A5610' }}>
        ⓘ 县级联营商可申请体验店，平台将在 5 个工作日内完成审核
      </div>
    </div>
    <div style={{ position: 'absolute', left: 16, right: 16, bottom: 30 }}>
      <button className="btn-gold" style={{ width: '100%' }}>提交申请</button>
    </div>
    <HomeIndicator/>
  </Phone>
);

// ============================================================
// F13. 体验店申请状态
// ============================================================
const ScreenStoreStatus = () => (
  <Phone>
    <div style={{ background: 'var(--grad-brown)', color: '#F6E7C2', paddingBottom: 36 }}>
      <StatusBar theme="dark"/>
      <NavBar title="体验店申请" theme="on-brown"/>
      <div style={{ padding: '8px 20px', textAlign: 'center' }}>
        <div style={{
          width: 72, height: 72, borderRadius: '50%',
          background: 'var(--grad-gold)', margin: '0 auto',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          boxShadow: '0 8px 24px rgba(228,165,22,0.35)',
        }}>
          <Icon name="check" size={36} color="#fff" strokeWidth={3}/>
        </div>
        <div style={{ fontSize: 22, fontWeight: 700, color: '#FFE0A0', marginTop: 16 }}>申请已提交</div>
        <div style={{ fontSize: 12, color: 'rgba(246,231,194,0.7)', marginTop: 6 }}>平台正在审核中，预计 5 个工作日完成</div>
      </div>
    </div>

    <div style={{ overflow: 'auto', height: 'calc(100% - 230px)', padding: '12px 16px' }}>
      {/* Progress */}
      <div style={{ background: '#fff', borderRadius: 12, padding: '16px', marginTop: -16, boxShadow: 'var(--shadow-card)' }}>
        {[
          { l: '提交申请', t: '5/16 14:20', a: true },
          { l: '资料初审', t: '5/17 10:08', a: true },
          { l: '实地考察', t: '审核中…', current: true },
          { l: '签约授权', t: '待安排' },
          { l: '开业准备', t: '待开始' },
        ].map((s, i, arr) => (
          <div key={i} style={{ display: 'flex', alignItems: 'center', gap: 14, padding: '4px 0' }}>
            <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
              <div style={{
                width: s.current ? 22 : 18, height: s.current ? 22 : 18, borderRadius: '50%',
                background: s.current ? 'var(--grad-gold)' : s.a ? 'var(--gold-500)' : 'var(--line)',
                display: 'flex', alignItems: 'center', justifyContent: 'center',
                boxShadow: s.current ? '0 0 0 5px rgba(228,165,22,0.18)' : 'none',
              }}>
                {s.a && <Icon name="check" size={12} color="#fff" strokeWidth={3}/>}
              </div>
              {i < arr.length - 1 && <div style={{ width: 2, height: 20, background: s.a ? 'var(--gold-500)' : 'var(--line)' }}/>}
            </div>
            <div style={{ flex: 1, paddingBottom: 6 }}>
              <div style={{ fontSize: 14, fontWeight: s.a || s.current ? 600 : 400, color: s.a || s.current ? 'var(--text-1)' : 'var(--text-3)' }}>{s.l}</div>
              <div style={{ fontSize: 11, color: 'var(--text-3)', marginTop: 2 }}>{s.t}</div>
            </div>
          </div>
        ))}
      </div>

      {/* Info */}
      <div style={{ background: '#fff', borderRadius: 12, padding: '14px 16px', marginTop: 12, fontSize: 13 }}>
        <div style={{ fontWeight: 700, marginBottom: 10 }}>申请信息</div>
        {[
          ['服务单号', 'STA202605161420029'],
          ['申请人', '李明远'],
          ['店铺名称', '醇品汇 · 朝阳国贸店'],
          ['店铺地址', '北京市朝阳区建国门外大街…'],
          ['店铺类型', '体验店 (50-100㎡)'],
        ].map(([k, v]) => (
          <div key={k} style={{ display: 'flex', padding: '4px 0' }}>
            <div style={{ width: 80, color: 'var(--text-3)' }}>{k}</div>
            <div style={{ flex: 1 }} className="font-num">{v}</div>
          </div>
        ))}
      </div>

      <div style={{ display: 'flex', gap: 10, marginTop: 16 }}>
        <button className="btn-gold-outline" style={{ flex: 1 }}>修改资料</button>
        <button className="btn-gold" style={{ flex: 1 }}>
          <Icon name="headset" size={16} color="#3A1A00" style={{ marginRight: 4 }}/>联系客服
        </button>
      </div>
    </div>
    <HomeIndicator/>
  </Phone>
);

Object.assign(window, {
  ScreenDist, ScreenLevelLianYing, ScreenLevelPartner, ScreenRewardDetail, ScreenCommissionDetail,
  ScreenWithdraw, ScreenWithdrawHistory, ScreenTeam, ScreenTeamMember,
  ScreenInviteShare, ScreenInvitePoster, ScreenStoreApply, ScreenStoreStatus,
});
