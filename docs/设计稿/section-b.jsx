// section-b.jsx — Home & browsing

// Reusable: top brown header with brand + search
const BrandHeader = ({ withSearch = true, level = '县级联营商', onLight = false }) => (
  <div style={{
    background: onLight ? '#fff' : 'var(--grad-brown)',
    color: onLight ? 'var(--text-1)' : '#F6E7C2',
    padding: onLight ? '0 16px 12px' : '0 16px 14px',
  }}>
    <StatusBar theme={onLight ? 'light' : 'dark'}/>
    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '4px 0 12px' }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
        <div style={{ fontSize: 22, fontWeight: 700, color: onLight ? 'var(--text-1)' : '#FFE0A0', letterSpacing: 1.2 }}>醇品汇</div>
        <div style={{
          background: 'var(--grad-gold)',
          color: '#3A1A00', fontSize: 11, fontWeight: 600,
          padding: '3px 8px', borderRadius: 6,
        }}>{level}</div>
      </div>
      <div style={{ display: 'flex', gap: 14 }}>
        <Icon name="cart" size={22} color={onLight ? 'var(--text-1)' : '#FFD685'}/>
        <Icon name="bell" size={22} color={onLight ? 'var(--text-1)' : '#FFD685'}/>
      </div>
    </div>
    {withSearch && (
      <div className={`searchbar ${onLight ? 'light' : ''}`}>
        <Icon name="search" size={14}/> 搜索酒水商品…
      </div>
    )}
  </div>
);

// Reusable product card with image
const ProductCard = ({ name, price, kind, tag = '主产品', sold }) => (
  <div className="prod-card">
    <div className="pimg">
      <ProdImg kind={kind} text={name?.substring(0, 2)}/>
      <div className="pcart"><Icon name="plus" size={16} color="#3A1A00" strokeWidth={2.4}/></div>
      {tag && <div style={{ position: 'absolute', top: 8, left: 8 }}><div className="tag tag-gold-solid">{tag}</div></div>}
    </div>
    <div className="pmeta">
      <div className="pname">{name}</div>
      <div style={{ display: 'flex', alignItems: 'baseline', justifyContent: 'space-between', marginTop: 6 }}>
        <div className="price font-num" style={{ fontSize: 18 }}>
          <span className="sym">¥</span>{price}
        </div>
        {sold && <div style={{ fontSize: 11, color: 'var(--text-3)' }}>已售 {sold}</div>}
      </div>
    </div>
  </div>
);

// ============================================================
// B1. 首页
// ============================================================
const ScreenHome = () => (
  <Phone dark>
    <BrandHeader/>
    <div style={{ padding: '12px 16px 96px', overflow: 'auto', height: 'calc(100% - 130px)' }}>
      {/* Banner */}
      <div style={{
        height: 150, borderRadius: 14, overflow: 'hidden',
        background: 'linear-gradient(135deg, #2A1408 0%, #4A2410 50%, #1F0E04 100%)',
        position: 'relative',
      }}>
        <div style={{ position: 'absolute', inset: 0, display: 'flex', alignItems: 'center', padding: '0 22px' }}>
          <div style={{ color: '#FFE0A0' }}>
            <div style={{ fontSize: 11, letterSpacing: 4, color: 'rgba(255,224,160,0.65)' }}>2026 · AUTUMN</div>
            <div style={{ fontSize: 22, fontWeight: 700, marginTop: 6 }}>秋季新品季</div>
            <div style={{ fontSize: 12, color: 'rgba(255,224,160,0.75)', marginTop: 2 }}>限时联营商福利 · 至高省 ¥600</div>
          </div>
          <div style={{ marginLeft: 'auto', display: 'flex', gap: 6, alignItems: 'flex-end' }}>
            {['redwine', 'redwine', 'redwine'].map((k, i) => (
              <div key={i} style={{ width: 38, height: 90, transform: `rotate(${i === 1 ? 0 : (i === 0 ? -6 : 6)}deg)`, opacity: 0.85 }}>
                <ProdImg kind={k}/>
              </div>
            ))}
          </div>
        </div>
        <div style={{ position: 'absolute', bottom: 10, left: '50%', transform: 'translateX(-50%)', display: 'flex', gap: 4 }}>
          <div style={{ width: 14, height: 4, background: '#FFD646', borderRadius: 2 }}/>
          <div style={{ width: 4, height: 4, background: 'rgba(255,255,255,0.35)', borderRadius: 2 }}/>
          <div style={{ width: 4, height: 4, background: 'rgba(255,255,255,0.35)', borderRadius: 2 }}/>
        </div>
      </div>

      {/* Notice */}
      <div style={{
        marginTop: 12, background: '#FFF6DA', borderRadius: 10,
        padding: '10px 12px', display: 'flex', alignItems: 'center', gap: 8,
      }}>
        <div className="tag tag-gold-solid" style={{ flexShrink: 0 }}>公告</div>
        <div style={{ fontSize: 12, color: '#7A5610', flex: 1, overflow: 'hidden', whiteSpace: 'nowrap', textOverflow: 'ellipsis' }}>
          关于秋季新品上市及联营商福利活动通知…
        </div>
        <Icon name="chevron" size={14} color="#B07000"/>
      </div>

      {/* Channels */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 8, marginTop: 18 }}>
        {[
          { label: '主产品区', icon: 'medal', bg: '#FBE9B6', color: '#D38A00' },
          { label: '招商专区', icon: 'shop',  bg: '#DCE5FA', color: '#3A6FD1' },
          { label: '零售专区', icon: 'gift',  bg: '#D9F0E0', color: '#2F9E5C' },
          { label: '礼包专区', icon: 'gift',  bg: '#FCDDE6', color: '#D1467A' },
        ].map(c => (
          <div key={c.label} style={{ textAlign: 'center' }}>
            <div style={{
              width: 52, height: 52, margin: '0 auto', borderRadius: '50%',
              background: c.bg, display: 'flex', alignItems: 'center', justifyContent: 'center',
              color: c.color,
            }}>
              <Icon name={c.icon} size={26} color={c.color} strokeWidth={1.8}/>
            </div>
            <div style={{ fontSize: 12, marginTop: 6, color: 'var(--text-1)' }}>{c.label}</div>
          </div>
        ))}
      </div>

      {/* Main area section */}
      <div className="sec-title" style={{ paddingLeft: 0, paddingRight: 0, marginTop: 8 }}>
        <div className="bar"/> 主产品专区
        <div className="more">更多 ›</div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
        <ProductCard name="茅台飞天53度酱香型白酒 500ml" kind="maotai" price="2,980" sold="3.2k"/>
        <ProductCard name="五粮液普五52度浓香型白酒 500ml" kind="wuliang" price="1,380" sold="2.8k"/>
        <ProductCard name="国窖1573经典装52度500ml" kind="luzhou" price="980" sold="1.5k" tag="联营专享"/>
        <ProductCard name="洋河梦之蓝M6+52度500ml" kind="yanghe" price="868" sold="980"/>
      </div>

      {/* Retail section */}
      <div className="sec-title" style={{ paddingLeft: 0, paddingRight: 0 }}>
        <div className="bar"/> 零售专区
        <div className="more">更多 ›</div>
      </div>
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
        <ProductCard name="法国波尔多红葡萄酒 750ml" kind="redwine" price="298" sold="6.5k" tag="零售"/>
        <ProductCard name="单一麦芽威士忌 700ml" kind="whisky" price="688" sold="1.1k" tag="零售"/>
      </div>
    </div>

    <div className="fab-cs"><Icon name="chat" size={24} color="#3A1A00"/></div>
    <TabBar active="home"/>
  </Phone>
);

// ============================================================
// B2. 搜索页
// ============================================================
const ScreenSearch = () => (
  <Phone>
    <div style={{ background: '#fff' }}>
      <StatusBar theme="light"/>
      <div style={{ padding: '4px 16px 12px', display: 'flex', alignItems: 'center', gap: 12 }}>
        <Icon name="back" size={22} color="#1A1006"/>
        <div style={{
          flex: 1, height: 38, borderRadius: 999,
          background: 'var(--bg-app)', padding: '0 14px',
          display: 'flex', alignItems: 'center', gap: 8, fontSize: 13,
        }}>
          <Icon name="search" size={16} color="#9B8E7C"/>
          <span style={{ color: 'var(--text-1)' }}>茅台</span>
          <div style={{ width: 1, height: 14, background: '#9B8E7C', marginLeft: 1 }}/>
        </div>
        <div style={{ fontSize: 14, color: 'var(--gold-500)', fontWeight: 600 }}>搜索</div>
      </div>
    </div>

    <div style={{ padding: '16px 16px 0' }}>
      {/* History */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <div style={{ fontSize: 14, fontWeight: 600 }}>历史搜索</div>
        <Icon name="trash" size={16} color="#9B8E7C"/>
      </div>
      <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8, marginTop: 12 }}>
        {['茅台飞天', '五粮液', '红酒礼盒', '威士忌', '酱香型', '中秋礼包'].map(h => (
          <div key={h} style={{
            padding: '6px 14px', borderRadius: 999,
            background: 'var(--bg-app)',
            fontSize: 13, color: 'var(--text-2)',
          }}>{h}</div>
        ))}
      </div>

      {/* Hot */}
      <div style={{ fontSize: 14, fontWeight: 600, marginTop: 24 }}>热门搜索</div>
      <div style={{ marginTop: 12 }}>
        {[
          { rank: 1, txt: '茅台飞天 53度', hot: '6.8w' },
          { rank: 2, txt: '五粮液普五', hot: '4.2w' },
          { rank: 3, txt: '中秋商务礼盒', hot: '3.5w' },
          { rank: 4, txt: '国窖1573', hot: '2.1w' },
          { rank: 5, txt: '青花郎20', hot: '1.5w' },
        ].map(h => (
          <div key={h.rank} style={{ display: 'flex', alignItems: 'center', gap: 12, padding: '10px 0' }}>
            <div style={{
              width: 22, height: 22, borderRadius: 4,
              background: h.rank <= 3 ? 'var(--grad-gold)' : 'var(--cream-100)',
              color: h.rank <= 3 ? '#3A1A00' : 'var(--text-3)',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              fontSize: 12, fontWeight: 700,
            }}>{h.rank}</div>
            <div style={{ flex: 1, fontSize: 14 }}>{h.txt}</div>
            <div style={{ fontSize: 12, color: 'var(--text-3)' }}>{h.hot} 热度</div>
          </div>
        ))}
      </div>

      {/* Categories */}
      <div style={{ fontSize: 14, fontWeight: 600, marginTop: 18 }}>发现好酒</div>
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 8, marginTop: 12 }}>
        {['酱香型', '浓香型', '清香型', '红葡萄酒', '威士忌', '黄酒', '清酒', '香槟'].map(c => (
          <div key={c} style={{
            background: 'var(--cream-50)',
            borderRadius: 8, padding: '10px 0',
            textAlign: 'center', fontSize: 12, color: 'var(--text-2)',
          }}>{c}</div>
        ))}
      </div>
    </div>
    <HomeIndicator/>
  </Phone>
);

// ============================================================
// B3. 搜索结果
// ============================================================
const ScreenSearchResult = () => (
  <Phone>
    <div style={{ background: '#fff' }}>
      <StatusBar theme="light"/>
      <div style={{ padding: '4px 16px 12px', display: 'flex', alignItems: 'center', gap: 12 }}>
        <Icon name="back" size={22} color="#1A1006"/>
        <div style={{
          flex: 1, height: 38, borderRadius: 999,
          background: 'var(--bg-app)', padding: '0 14px',
          display: 'flex', alignItems: 'center', gap: 8, fontSize: 13,
        }}>
          <Icon name="search" size={16} color="#9B8E7C"/>
          <span>茅台</span>
        </div>
        <div style={{ fontSize: 14, color: 'var(--gold-500)', fontWeight: 600 }}>搜索</div>
      </div>
      {/* Sort tabs */}
      <div style={{ display: 'flex', padding: '0 16px 10px', gap: 18, fontSize: 13, borderBottom: '1px solid var(--line-soft)' }}>
        <div style={{ color: 'var(--gold-500)', fontWeight: 600, position: 'relative', paddingBottom: 6 }}>
          综合
          <div style={{ position: 'absolute', bottom: 0, left: '50%', transform: 'translateX(-50%)', width: 18, height: 2, background: 'var(--grad-gold)' }}/>
        </div>
        <div style={{ color: 'var(--text-2)' }}>销量</div>
        <div style={{ color: 'var(--text-2)' }}>价格 ↑↓</div>
        <div style={{ color: 'var(--text-2)' }}>新品</div>
        <div style={{ marginLeft: 'auto', color: 'var(--text-2)', display: 'flex', alignItems: 'center', gap: 4 }}>
          <Icon name="filter" size={14} color="#5E5142"/> 筛选
        </div>
      </div>
    </div>

    <div style={{ padding: '12px 16px 30px', display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10, overflow: 'auto', height: 'calc(100% - 110px - 40px)' }}>
      <ProductCard name="茅台飞天53度酱香型白酒 500ml" kind="maotai" price="2,980" sold="3.2k"/>
      <ProductCard name="贵州茅台王子酒 53度 500ml" kind="maotai" price="368" sold="8.5k"/>
      <ProductCard name="茅台迎宾酒 53度浓香型 500ml" kind="maotai" price="158" sold="12k"/>
      <ProductCard name="茅台醇 53度酱香 500ml" kind="maotai" price="298" sold="6.8k"/>
      <ProductCard name="茅台镇 30年陈酿 500ml" kind="luzhou" price="1,580" sold="1.2k"/>
      <ProductCard name="茅台 1935 53度 500ml" kind="maotai" price="1,188" sold="980"/>
    </div>
    <HomeIndicator/>
  </Phone>
);

// ============================================================
// B4. 商品分类
// ============================================================
const ScreenCategory = () => (
  <Phone>
    <div style={{ background: '#fff' }}>
      <StatusBar theme="light"/>
      <NavBar title="商品分类" theme="on-light"/>
    </div>
    <div style={{ background: '#fff', padding: '8px 16px 0', borderBottom: '1px solid var(--line-soft)' }}>
      <div className="searchbar light" style={{ background: 'var(--bg-app)', border: 'none' }}>
        <Icon name="search" size={14}/> 搜索酒水商品…
      </div>
    </div>
    <div style={{ display: 'flex', height: 'calc(100% - 132px)' }}>
      {/* Side categories */}
      <div style={{ width: 90, background: 'var(--bg-app)', overflow: 'auto', paddingTop: 6 }}>
        {[
          { label: '酱香型', active: true },
          { label: '浓香型' },
          { label: '清香型' },
          { label: '兼香型' },
          { label: '米香型' },
          { label: '红葡萄酒' },
          { label: '白葡萄酒' },
          { label: '威士忌' },
          { label: '白兰地' },
          { label: '伏特加' },
          { label: '黄酒' },
          { label: '清酒' },
          { label: '香槟起泡' },
        ].map(c => (
          <div key={c.label} style={{
            height: 48, padding: '0 12px',
            display: 'flex', alignItems: 'center',
            fontSize: 13, fontWeight: c.active ? 600 : 400,
            color: c.active ? 'var(--gold-500)' : 'var(--text-2)',
            background: c.active ? '#fff' : 'transparent',
            position: 'relative',
          }}>
            {c.active && <div style={{ position: 'absolute', left: 0, top: '50%', transform: 'translateY(-50%)', width: 3, height: 18, background: 'var(--grad-gold)', borderRadius: 2 }}/>}
            {c.label}
          </div>
        ))}
      </div>
      {/* Right content */}
      <div style={{ flex: 1, padding: '12px 14px', overflow: 'auto' }}>
        <div style={{ fontSize: 14, fontWeight: 700, marginBottom: 10 }}>酱香型白酒</div>
        {/* Sub-categories */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 8, marginBottom: 14 }}>
          {[
            { l: '茅台系', k: 'maotai' },
            { l: '郎酒系', k: 'redwine' },
            { l: '习酒系', k: 'luzhou' },
            { l: '国台系', k: 'fenjiu' },
            { l: '钓鱼台', k: 'wuliang' },
            { l: '金沙系', k: 'whisky' },
          ].map(s => (
            <div key={s.l} style={{ textAlign: 'center' }}>
              <div style={{ aspectRatio: '1/1', borderRadius: 8, overflow: 'hidden' }}>
                <ProdImg kind={s.k}/>
              </div>
              <div style={{ fontSize: 11, marginTop: 4 }}>{s.l}</div>
            </div>
          ))}
        </div>
        <div style={{ fontSize: 13, fontWeight: 600, marginBottom: 8 }}>热门商品</div>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr', gap: 8 }}>
          {[
            { n: '茅台飞天 53度 500ml', p: '2,980', k: 'maotai' },
            { n: '茅台王子酒 53度 500ml', p: '368', k: 'maotai' },
            { n: '青花郎20 53度 500ml', p: '1,188', k: 'redwine' },
          ].map(p => (
            <div key={p.n} style={{ display: 'flex', gap: 10, background: 'var(--cream-50)', borderRadius: 8, padding: 8 }}>
              <div style={{ width: 60, height: 60, borderRadius: 6, overflow: 'hidden', flexShrink: 0 }}>
                <ProdImg kind={p.k}/>
              </div>
              <div style={{ flex: 1, minWidth: 0 }}>
                <div style={{ fontSize: 12, color: 'var(--text-1)', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{p.n}</div>
                <div className="price font-num" style={{ fontSize: 15, marginTop: 4 }}><span className="sym">¥</span>{p.p}</div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
    <TabBar active="home"/>
  </Phone>
);

// ============================================================
// B5. 主产品专区
// ============================================================
const ZoneScreen = ({ title, subtitle, accent, themeColor, products, tag }) => (
  <Phone>
    {/* Top brown banner */}
    <div style={{
      background: 'var(--grad-brown)',
      backgroundImage: `radial-gradient(ellipse 80% 60% at 50% 30%, ${accent}33, transparent 60%), var(--grad-brown)`,
      color: '#F6E7C2',
    }}>
      <StatusBar theme="dark"/>
      <NavBar title={title} theme="on-brown"/>
      <div style={{ padding: '8px 20px 28px' }}>
        <div style={{ fontSize: 11, letterSpacing: 4, color: 'rgba(246,231,194,0.6)' }}>EXCLUSIVE ZONE</div>
        <div style={{ fontSize: 26, fontWeight: 700, color: themeColor, marginTop: 4 }}>{title}</div>
        <div style={{ fontSize: 13, color: 'rgba(246,231,194,0.75)', marginTop: 6 }}>{subtitle}</div>
      </div>
    </div>
    <div style={{
      background: 'var(--bg-app)', borderRadius: '20px 20px 0 0',
      marginTop: -16, padding: '14px 16px 30px',
      overflow: 'auto', height: 'calc(100% - 200px)',
    }}>
      {/* Sub-filter */}
      <div style={{ display: 'flex', gap: 8, marginBottom: 12, overflow: 'auto' }}>
        {['全部', '酱香型', '浓香型', '清香型', '兼香型'].map((f, i) => (
          <div key={f} style={{
            padding: '6px 14px', borderRadius: 999,
            background: i === 0 ? themeColor : '#fff',
            color: i === 0 ? '#3A1A00' : 'var(--text-2)',
            fontSize: 12, fontWeight: 600, flexShrink: 0,
            border: i === 0 ? 'none' : '1px solid var(--line)',
          }}>{f}</div>
        ))}
      </div>
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
        {products.map((p, i) => <ProductCard key={i} {...p} tag={tag}/>)}
      </div>
    </div>
    <TabBar active="home"/>
  </Phone>
);

const ScreenMainArea = () => (
  <ZoneScreen
    title="主产品专区"
    subtitle="平台甄选名酒 · 联营商专享渠道价"
    accent="#FFD646"
    themeColor="#FFE0A0"
    tag="主产品"
    products={[
      { name: '茅台飞天 53度 500ml', price: '2,980', kind: 'maotai', sold: '3.2k' },
      { name: '五粮液普五52度 500ml', price: '1,380', kind: 'wuliang', sold: '2.8k' },
      { name: '国窖1573经典装 500ml', price: '980', kind: 'luzhou', sold: '1.5k' },
      { name: '洋河梦之蓝M6+ 500ml', price: '868', kind: 'yanghe', sold: '980' },
      { name: '青花汾酒 30年 500ml', price: '1,288', kind: 'fenjiu', sold: '760' },
      { name: '青花郎 20年 53度 500ml', price: '1,188', kind: 'redwine', sold: '1.1k' },
    ]}
  />
);

const ScreenInvestArea = () => (
  <ZoneScreen
    title="招商专区"
    subtitle="低门槛起步 · 高额招商奖励 20%"
    accent="#3A6FD1"
    themeColor="#BFD2F4"
    tag="招商"
    products={[
      { name: '醇品汇 私享 53度浓香型 500ml', price: '598', kind: 'luzhou', sold: '420' },
      { name: '醇品汇 经典款 52度浓香 500ml', price: '498', kind: 'wuliang', sold: '380' },
      { name: '小酒馆 浓香型口粮酒 500ml', price: '128', kind: 'fenjiu', sold: '5.2k' },
      { name: '九粮春 兼香型 500ml', price: '218', kind: 'yanghe', sold: '1.6k' },
    ]}
  />
);

const ScreenRetailArea = () => (
  <ZoneScreen
    title="零售专区"
    subtitle="日常自饮 · 高频补货 · 性价比之选"
    accent="#2F9E5C"
    themeColor="#BDE5C9"
    tag="零售"
    products={[
      { name: '法国波尔多干红 750ml', price: '298', kind: 'redwine', sold: '6.5k' },
      { name: '单一麦芽威士忌 12年 700ml', price: '688', kind: 'whisky', sold: '1.1k' },
      { name: '日本清酒 大吟酿 720ml', price: '388', kind: 'sake', sold: '2.3k' },
      { name: '法国香槟 750ml', price: '588', kind: 'champagne', sold: '1.5k' },
      { name: '澳洲西拉干红 750ml', price: '258', kind: 'redwine', sold: '3.8k' },
      { name: '苏格兰调和威士忌 700ml', price: '358', kind: 'whisky', sold: '2.1k' },
    ]}
  />
);

const ScreenGiftArea = () => (
  <ZoneScreen
    title="礼包专区"
    subtitle="商务礼赠 · 中秋国庆双节限定"
    accent="#D1467A"
    themeColor="#F4BFD0"
    tag="礼包"
    products={[
      { name: '中秋商务礼盒 双瓶装 茅五', price: '4,580', kind: 'gift', sold: '680' },
      { name: '尊享礼盒 飞天500ml × 2', price: '6,180', kind: 'gift', sold: '320' },
      { name: '红酒商务礼盒 双瓶装', price: '888', kind: 'gift', sold: '1.2k' },
      { name: '威士忌伴手礼盒 配杯具', price: '988', kind: 'gift', sold: '560' },
    ]}
  />
);

// ============================================================
// B9. 筛选/排序 (with filter drawer overlay)
// ============================================================
const ScreenFilter = () => (
  <Phone>
    {/* Dim search result behind */}
    <div style={{ filter: 'blur(0px)', opacity: 0.45 }}>
      <div style={{ background: '#fff' }}>
        <StatusBar theme="light"/>
        <div style={{ padding: '4px 16px 12px', display: 'flex', alignItems: 'center', gap: 12 }}>
          <Icon name="back" size={22} color="#1A1006"/>
          <div style={{ flex: 1, height: 38, borderRadius: 999, background: 'var(--bg-app)', padding: '0 14px', display: 'flex', alignItems: 'center', gap: 8, fontSize: 13 }}>
            <Icon name="search" size={16}/> 茅台
          </div>
          <div style={{ fontSize: 14, color: 'var(--gold-500)', fontWeight: 600 }}>搜索</div>
        </div>
      </div>
      <div style={{ padding: '12px 16px', display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
        <ProductCard name="茅台飞天53度" kind="maotai" price="2,980"/>
        <ProductCard name="茅台王子酒 53度" kind="maotai" price="368"/>
      </div>
    </div>

    <div style={{ position: 'absolute', inset: 0, background: 'rgba(0,0,0,0.4)' }}/>

    {/* Right drawer */}
    <div style={{
      position: 'absolute', right: 0, top: 0, bottom: 0,
      width: 320, background: '#fff',
      padding: '14px 18px 30px',
      display: 'flex', flexDirection: 'column',
    }}>
      <StatusBar theme="light"/>
      <div style={{ fontSize: 18, fontWeight: 700, marginTop: 4 }}>筛选条件</div>

      <div style={{ flex: 1, overflow: 'auto', marginTop: 16 }}>
        <div style={{ fontSize: 13, fontWeight: 600 }}>专区</div>
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8, marginTop: 10 }}>
          {[
            { l: '主产品区', a: true }, { l: '招商专区' }, { l: '零售专区' }, { l: '礼包专区' }
          ].map(o => (
            <div key={o.l} style={{
              padding: '6px 14px', borderRadius: 6,
              background: o.a ? 'var(--gold-50)' : 'var(--bg-app)',
              border: o.a ? '1px solid var(--gold-300)' : '1px solid transparent',
              color: o.a ? 'var(--gold-600)' : 'var(--text-2)',
              fontSize: 12, fontWeight: o.a ? 600 : 400,
            }}>{o.l}</div>
          ))}
        </div>

        <div style={{ fontSize: 13, fontWeight: 600, marginTop: 22 }}>香型</div>
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8, marginTop: 10 }}>
          {[
            { l: '酱香型', a: true }, { l: '浓香型', a: true }, { l: '清香型' }, { l: '兼香型' }, { l: '红葡萄酒' }, { l: '威士忌' }
          ].map(o => (
            <div key={o.l} style={{
              padding: '6px 14px', borderRadius: 6,
              background: o.a ? 'var(--gold-50)' : 'var(--bg-app)',
              border: o.a ? '1px solid var(--gold-300)' : '1px solid transparent',
              color: o.a ? 'var(--gold-600)' : 'var(--text-2)',
              fontSize: 12, fontWeight: o.a ? 600 : 400,
            }}>{o.l}</div>
          ))}
        </div>

        <div style={{ fontSize: 13, fontWeight: 600, marginTop: 22 }}>价格区间</div>
        <div style={{ display: 'flex', gap: 8, marginTop: 10 }}>
          <div style={{ flex: 1, padding: '8px 12px', background: 'var(--bg-app)', borderRadius: 6, fontSize: 12, color: 'var(--text-3)' }}>¥ 最低价</div>
          <div style={{ alignSelf: 'center', color: 'var(--text-3)' }}>—</div>
          <div style={{ flex: 1, padding: '8px 12px', background: 'var(--bg-app)', borderRadius: 6, fontSize: 12, color: 'var(--text-3)' }}>¥ 最高价</div>
        </div>
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8, marginTop: 10 }}>
          {['100以下', '100-500', '500-1000', '1000-3000', '3000以上'].map((p, i) => (
            <div key={p} style={{
              padding: '5px 12px', borderRadius: 6,
              background: i === 2 ? 'var(--gold-50)' : 'var(--bg-app)',
              border: i === 2 ? '1px solid var(--gold-300)' : '1px solid transparent',
              color: i === 2 ? 'var(--gold-600)' : 'var(--text-2)',
              fontSize: 12,
            }}>{p}</div>
          ))}
        </div>

        <div style={{ fontSize: 13, fontWeight: 600, marginTop: 22 }}>商品标签</div>
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8, marginTop: 10 }}>
          {['首购享20%佣金', '联营商专享', '新人特价', '限时秒杀'].map(o => (
            <div key={o} style={{
              padding: '6px 12px', borderRadius: 6,
              background: 'var(--bg-app)', color: 'var(--text-2)', fontSize: 12,
            }}>{o}</div>
          ))}
        </div>
      </div>

      <div style={{ display: 'flex', gap: 10, marginTop: 16 }}>
        <button className="btn-gold-outline" style={{ flex: 1 }}>重置</button>
        <button className="btn-gold" style={{ flex: 1.4 }}>确认 (24)</button>
      </div>
    </div>
  </Phone>
);

// ============================================================
// B10. 空状态
// ============================================================
const ScreenEmptyList = () => (
  <Phone>
    <div style={{ background: '#fff' }}>
      <StatusBar theme="light"/>
      <div style={{ padding: '4px 16px 12px', display: 'flex', alignItems: 'center', gap: 12 }}>
        <Icon name="back" size={22}/>
        <div style={{ flex: 1, height: 38, borderRadius: 999, background: 'var(--bg-app)', padding: '0 14px', display: 'flex', alignItems: 'center', gap: 8, fontSize: 13 }}>
          <Icon name="search" size={16} color="#9B8E7C"/> 茅台冰激凌
        </div>
        <div style={{ fontSize: 14, color: 'var(--gold-500)', fontWeight: 600 }}>搜索</div>
      </div>
    </div>
    <div className="empty" style={{ paddingTop: 110 }}>
      <div className="empty-icon" style={{ width: 140, height: 140 }}>
        <svg viewBox="0 0 100 100" width="80" height="80" fill="none" stroke="#E4A516" strokeWidth="2.4" strokeLinecap="round" strokeLinejoin="round">
          <path d="M35 18 H65 V32 L72 50 V82 a4 4 0 0 1 -4 4 H32 a4 4 0 0 1 -4 -4 V50 Z"/>
          <path d="M28 60 H72"/>
          <circle cx="50" cy="68" r="4" fill="#E4A516"/>
          <path d="M40 78 H60" strokeDasharray="2 3"/>
        </svg>
      </div>
      <div style={{ fontSize: 15, fontWeight: 600, color: 'var(--text-1)', marginBottom: 6 }}>没有找到相关商品</div>
      <div style={{ fontSize: 13, color: 'var(--text-3)', textAlign: 'center', marginBottom: 22, maxWidth: 240 }}>
        换个关键词试试？也可以浏览下方为你推荐的好酒
      </div>
      <button className="btn-gold-outline">浏览主产品专区</button>

      <div style={{ width: '100%', marginTop: 36, padding: '0 16px' }}>
        <div style={{ fontSize: 13, color: 'var(--text-2)', marginBottom: 10 }}>· 猜你喜欢 ·</div>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
          <ProductCard name="茅台王子酒 53度 500ml" kind="maotai" price="368"/>
          <ProductCard name="国窖1573经典装" kind="luzhou" price="980"/>
        </div>
      </div>
    </div>
    <HomeIndicator/>
  </Phone>
);

Object.assign(window, {
  BrandHeader, ProductCard,
  ScreenHome, ScreenSearch, ScreenSearchResult, ScreenCategory,
  ScreenMainArea, ScreenInvestArea, ScreenRetailArea, ScreenGiftArea,
  ScreenFilter, ScreenEmptyList,
});
