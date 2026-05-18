// section-responsive.jsx — Home screen at 4 device sizes

// Responsive home — slimmer paddings on small, max content on large
const ScreenHomeResp = ({ w = 390, di = false }) => {
  // Edge margin per spec: 16, small-screen可压缩到12
  const edge = w <= 375 ? 12 : 16;
  // For 430 keep grid card scale but slightly wider gap
  const banner = w <= 375 ? 130 : w >= 430 ? 168 : 150;
  const channelGap = w <= 375 ? 6 : 8;

  return (
    <Phone width={w} height={w === 375 ? 667 : w === 393 ? 852 : w === 430 ? 932 : 844} dark>
      <div style={{
        background: 'var(--grad-brown)',
        backgroundImage: 'radial-gradient(ellipse 80% 60% at 50% 30%, rgba(228,165,22,0.18), transparent 60%), var(--grad-brown)',
        color: '#F6E7C2',
        padding: `0 ${edge}px 12px`,
      }}>
        <StatusBar theme="dark" dynamicIsland={di}/>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '4px 0 12px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
            <div style={{ fontSize: w <= 375 ? 20 : 22, fontWeight: 700, color: '#FFE0A0', letterSpacing: 1.2 }}>醇品汇</div>
            <div style={{ background: 'var(--grad-gold)', color: '#3A1A00', fontSize: 11, fontWeight: 600, padding: '3px 8px', borderRadius: 6 }}>县级联营商</div>
          </div>
          <div style={{ display: 'flex', gap: 14 }}>
            <Icon name="cart" size={22} color="#FFD685"/>
            <Icon name="bell" size={22} color="#FFD685"/>
          </div>
        </div>
        <div className="searchbar"><Icon name="search" size={14}/> 搜索酒水商品…</div>
      </div>

      <div style={{ padding: `12px ${edge}px 96px`, overflow: 'auto', height: w === 375 ? 'calc(100% - 100px - 76px)' : 'calc(100% - 130px - 76px)' }}>
        <div style={{
          height: banner, borderRadius: 14, overflow: 'hidden',
          background: 'linear-gradient(135deg, #2A1408, #1F0E04)',
          position: 'relative',
        }}>
          <div style={{ position: 'absolute', inset: 0, display: 'flex', alignItems: 'center', padding: `0 ${edge + 4}px` }}>
            <div style={{ color: '#FFE0A0' }}>
              <div style={{ fontSize: 10, letterSpacing: 4, color: 'rgba(255,224,160,0.65)' }}>2026 · AUTUMN</div>
              <div style={{ fontSize: w <= 375 ? 18 : 22, fontWeight: 700, marginTop: 4 }}>秋季新品季</div>
              <div style={{ fontSize: w <= 375 ? 11 : 12, color: 'rgba(255,224,160,0.75)', marginTop: 2 }}>联营商福利 · 至高省 ¥600</div>
            </div>
            <div style={{ marginLeft: 'auto', display: 'flex', gap: 4, alignItems: 'flex-end' }}>
              {[0, 1, 2].map(i => (
                <div key={i} style={{ width: w <= 375 ? 32 : 38, height: w <= 375 ? 76 : 90, transform: `rotate(${i === 1 ? 0 : (i === 0 ? -6 : 6)}deg)`, opacity: 0.85 }}>
                  <ProdImg kind="redwine"/>
                </div>
              ))}
            </div>
          </div>
        </div>

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

        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: channelGap, marginTop: 18 }}>
          {[
            { label: '主产品区', icon: 'medal', bg: '#FBE9B6', color: '#D38A00' },
            { label: '招商专区', icon: 'shop',  bg: '#DCE5FA', color: '#3A6FD1' },
            { label: '零售专区', icon: 'gift',  bg: '#D9F0E0', color: '#2F9E5C' },
            { label: '礼包专区', icon: 'gift',  bg: '#FCDDE6', color: '#D1467A' },
          ].map(c => (
            <div key={c.label} style={{ textAlign: 'center' }}>
              <div style={{
                width: w <= 375 ? 46 : 52, height: w <= 375 ? 46 : 52, margin: '0 auto', borderRadius: '50%',
                background: c.bg, display: 'flex', alignItems: 'center', justifyContent: 'center',
              }}>
                <Icon name={c.icon} size={w <= 375 ? 22 : 26} color={c.color} strokeWidth={1.8}/>
              </div>
              <div style={{ fontSize: w <= 375 ? 11 : 12, marginTop: 6, color: 'var(--text-1)' }}>{c.label}</div>
            </div>
          ))}
        </div>

        <div className="sec-title" style={{ paddingLeft: 0, paddingRight: 0, marginTop: 8 }}>
          <div className="bar"/> 主产品专区
          <div className="more">更多 ›</div>
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: w <= 375 ? 8 : 10 }}>
          <ProductCard name="茅台飞天 53度 500ml" kind="maotai" price="2,980" sold="3.2k"/>
          <ProductCard name="五粮液普五52度 500ml" kind="wuliang" price="1,380" sold="2.8k"/>
          {w >= 393 && <ProductCard name="国窖1573经典装" kind="luzhou" price="980" sold="1.5k" tag="联营专享"/>}
          {w >= 393 && <ProductCard name="洋河梦之蓝M6+" kind="yanghe" price="868" sold="980"/>}
        </div>

        {w >= 430 && (
          <>
            <div className="sec-title" style={{ paddingLeft: 0, paddingRight: 0 }}>
              <div className="bar"/> 零售专区
              <div className="more">更多 ›</div>
            </div>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
              <ProductCard name="法国波尔多干红 750ml" kind="redwine" price="298" sold="6.5k" tag="零售"/>
              <ProductCard name="单一麦芽威士忌 700ml" kind="whisky" price="688" sold="1.1k" tag="零售"/>
            </div>
          </>
        )}
      </div>

      <div className="fab-cs"><Icon name="chat" size={24} color="#3A1A00"/></div>
      <TabBar active="home"/>
    </Phone>
  );
};

Object.assign(window, { ScreenHomeResp });
