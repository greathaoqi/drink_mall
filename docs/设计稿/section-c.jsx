// section-c.jsx — Product detail & purchase

// ============================================================
// C1. 商品详情页
// ============================================================
const ScreenProductDetail = () => (
  <Phone>
    {/* Image area with floating header */}
    <div style={{ position: 'relative', height: 390, background: '#0E0805' }}>
      <ProdImg kind="maotai" text="茅台"/>
      <div style={{ position: 'absolute', top: 0, left: 0, right: 0, color: '#fff' }}>
        <StatusBar theme="dark"/>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '4px 16px 0' }}>
          <div style={{ width: 36, height: 36, borderRadius: '50%', background: 'rgba(0,0,0,0.4)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <Icon name="back" size={20} color="#fff"/>
          </div>
          <div style={{ fontSize: 16, fontWeight: 600 }}>商品详情</div>
          <div style={{ width: 36, height: 36, borderRadius: '50%', background: 'rgba(0,0,0,0.4)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <Icon name="share" size={18} color="#fff"/>
          </div>
        </div>
      </div>
      {/* Image dots */}
      <div style={{ position: 'absolute', bottom: 14, left: 0, right: 0, display: 'flex', justifyContent: 'center', gap: 5 }}>
        <div style={{ width: 18, height: 4, background: '#fff', borderRadius: 2 }}/>
        <div style={{ width: 4, height: 4, background: 'rgba(255,255,255,0.5)', borderRadius: 2 }}/>
        <div style={{ width: 4, height: 4, background: 'rgba(255,255,255,0.5)', borderRadius: 2 }}/>
      </div>
    </div>

    {/* Scroll content */}
    <div style={{ background: '#fff', padding: '16px 16px 100px', overflow: 'auto', height: 'calc(100% - 390px - 80px)' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
        <div>
          <div className="price font-num" style={{ fontSize: 28, display: 'flex', alignItems: 'baseline', gap: 8 }}>
            <span><span className="sym">¥</span>2,980</span>
            <span style={{ fontSize: 12, color: 'var(--text-3)', fontWeight: 400 }}>联营商价</span>
          </div>
        </div>
        <div style={{ textAlign: 'right', fontSize: 12, color: 'var(--text-3)', lineHeight: 1.7 }}>
          已售 <span className="font-num" style={{ color: 'var(--text-1)' }}>3,280</span> 件<br/>
          库存 <span className="font-num" style={{ color: 'var(--text-1)' }}>500</span><br/>
          <span style={{ color: 'var(--gold-500)' }}>酱香型</span>
        </div>
      </div>
      <div style={{ fontSize: 17, fontWeight: 700, marginTop: 8, lineHeight: 1.4 }}>茅台飞天 53度酱香型白酒 500ml</div>
      <div style={{ display: 'flex', gap: 6, marginTop: 10, flexWrap: 'wrap' }}>
        <span className="tag tag-gold">主产品区</span>
        <span className="tag tag-gold">酱香型</span>
        <span className="tag tag-blue">首购享 20% 佣金</span>
      </div>

      {/* SKU */}
      <div style={{ marginTop: 22 }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 10 }}>
          <div style={{ fontSize: 14, fontWeight: 700 }}>规格选择</div>
          <div style={{ fontSize: 12, color: 'var(--text-3)' }}>选择规格、数量</div>
        </div>
        <div style={{ display: 'flex', gap: 10 }}>
          <div style={{
            padding: '10px 20px', borderRadius: 8,
            background: 'var(--gold-50)', border: '1.5px solid var(--gold-500)',
            color: 'var(--gold-600)', fontWeight: 600, fontSize: 13,
          }}>500ml 单瓶</div>
          <div style={{
            padding: '10px 20px', borderRadius: 8,
            background: '#fff', border: '1px solid var(--line)',
            color: 'var(--text-2)', fontSize: 13,
          }}>500ml 6瓶装</div>
        </div>
      </div>

      <div style={{ marginTop: 22, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <div style={{ fontSize: 14, fontWeight: 700 }}>购买数量</div>
        <div style={{ display: 'flex', alignItems: 'center', border: '1px solid var(--line)', borderRadius: 8 }}>
          <div style={{ width: 32, height: 32, display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--text-3)' }}>
            <Icon name="minus" size={14}/>
          </div>
          <div style={{ width: 40, textAlign: 'center', fontSize: 14, fontWeight: 600, borderLeft: '1px solid var(--line)', borderRight: '1px solid var(--line)', padding: '6px 0' }}>1</div>
          <div style={{ width: 32, height: 32, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <Icon name="plus" size={14}/>
          </div>
        </div>
      </div>

      <div style={{ marginTop: 26, borderTop: '1px solid var(--line-soft)', paddingTop: 18 }}>
        <div style={{ fontSize: 14, fontWeight: 700, marginBottom: 12 }}>商品参数</div>
        {[
          ['品牌', '贵州茅台'],
          ['香型', '酱香型'],
          ['度数', '53° vol'],
          ['容量', '500ml'],
          ['产地', '贵州 · 茅台镇'],
          ['保质期', '长期保存'],
        ].map(([k, v]) => (
          <div key={k} style={{ display: 'flex', padding: '6px 0', fontSize: 13 }}>
            <div style={{ width: 80, color: 'var(--text-3)' }}>{k}</div>
            <div>{v}</div>
          </div>
        ))}
      </div>

      <div style={{ marginTop: 22, borderTop: '1px solid var(--line-soft)', paddingTop: 18 }}>
        <div style={{ fontSize: 14, fontWeight: 700, marginBottom: 12 }}>商品详情</div>
        <div style={{ borderRadius: 10, overflow: 'hidden', aspectRatio: '4/3', marginBottom: 10 }}>
          <ProdImg kind="maotai"/>
        </div>
        <div style={{ fontSize: 13, color: 'var(--text-2)', lineHeight: 1.8 }}>
          1958年贵州茅台飞天牌正式诞生，以「酱香突出，幽雅细腻，酒体醇厚」名扬天下。本品选用茅台镇优质高粱、小麦及赤水河水，经传统大曲酱香工艺酿造而成，五年陈酿，回味悠长。
        </div>
      </div>
    </div>

    {/* Bottom bar */}
    <div style={{
      position: 'absolute', left: 0, right: 0, bottom: 0,
      background: '#fff', borderTop: '1px solid var(--line-soft)',
      padding: '10px 14px 22px',
      display: 'flex', alignItems: 'center', gap: 10,
    }}>
      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', position: 'relative' }}>
        <div style={{
          width: 48, height: 48, borderRadius: '50%',
          background: 'var(--gold-50)', border: '1.5px solid var(--gold-300)',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
        }}>
          <Icon name="cart" size={22} color="#D38A00"/>
        </div>
        <div style={{ fontSize: 10, color: 'var(--text-3)', marginTop: 2 }}>购物车</div>
        <div style={{ position: 'absolute', top: -2, right: -4, background: '#D6453B', color: '#fff', fontSize: 10, padding: '0 5px', height: 16, borderRadius: 8, display: 'flex', alignItems: 'center' }}>3</div>
      </div>
      <button className="btn-gold" style={{ flex: 1, height: 50, fontSize: 17 }}>立即购买</button>
    </div>
    <HomeIndicator/>
  </Phone>
);

// ============================================================
// C2. 规格选择弹窗
// ============================================================
const ScreenSku = () => (
  <Phone>
    {/* dim detail behind */}
    <div style={{ position: 'relative', height: 390, background: '#0E0805', opacity: 0.4 }}>
      <ProdImg kind="maotai"/>
    </div>
    <div style={{ position: 'absolute', inset: 0, background: 'rgba(0,0,0,0.55)' }}/>

    <div style={{
      position: 'absolute', left: 0, right: 0, bottom: 0,
      background: '#fff', borderRadius: '20px 20px 0 0',
      padding: '18px 16px 24px',
    }}>
      {/* Top with product */}
      <div style={{ display: 'flex', gap: 12, alignItems: 'flex-start', position: 'relative' }}>
        <div style={{
          width: 100, height: 100, borderRadius: 10, overflow: 'hidden',
          marginTop: -36, boxShadow: '0 4px 14px rgba(0,0,0,0.18)',
          border: '3px solid #fff',
        }}>
          <ProdImg kind="maotai"/>
        </div>
        <div style={{ flex: 1, paddingTop: 4 }}>
          <div className="price font-num" style={{ fontSize: 22 }}>
            <span className="sym">¥</span>2,980
          </div>
          <div style={{ fontSize: 11, color: 'var(--text-3)', marginTop: 2 }}>库存 500 · 已选: 500ml 单瓶</div>
        </div>
        <div style={{ position: 'absolute', top: -4, right: -4 }}>
          <Icon name="close" size={22} color="#999"/>
        </div>
      </div>

      <div style={{ marginTop: 20 }}>
        <div style={{ fontSize: 13, color: 'var(--text-2)', marginBottom: 10 }}>规格</div>
        <div style={{ display: 'flex', gap: 10, flexWrap: 'wrap' }}>
          <div style={{ padding: '8px 18px', borderRadius: 8, background: 'var(--gold-50)', border: '1.5px solid var(--gold-500)', color: 'var(--gold-600)', fontWeight: 600, fontSize: 13 }}>500ml 单瓶</div>
          <div style={{ padding: '8px 18px', borderRadius: 8, background: 'var(--bg-app)', color: 'var(--text-2)', fontSize: 13 }}>500ml 6瓶装</div>
          <div style={{ padding: '8px 18px', borderRadius: 8, background: 'var(--bg-app)', color: 'var(--text-3)', fontSize: 13, textDecoration: 'line-through' }}>500ml 12瓶 (缺货)</div>
        </div>
      </div>

      <div style={{ marginTop: 22, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <div style={{ fontSize: 13, color: 'var(--text-2)' }}>购买数量</div>
        <div style={{ display: 'flex', alignItems: 'center', border: '1px solid var(--line)', borderRadius: 8 }}>
          <div style={{ width: 32, height: 32, display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--text-3)' }}>
            <Icon name="minus" size={14}/>
          </div>
          <div style={{ width: 50, textAlign: 'center', fontSize: 14, fontWeight: 600, borderLeft: '1px solid var(--line)', borderRight: '1px solid var(--line)', padding: '6px 0' }}>2</div>
          <div style={{ width: 32, height: 32, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <Icon name="plus" size={14}/>
          </div>
        </div>
      </div>

      <div style={{ marginTop: 18, padding: '12px 14px', background: 'var(--gold-50)', borderRadius: 10, display: 'flex', alignItems: 'center', gap: 8, fontSize: 12, color: '#7A5610' }}>
        <Icon name="info" size={16} color="#D38A00"/>
        首购此商品可获 <span style={{ fontWeight: 700, color: '#D38A00' }}>¥596</span> 佣金返现
      </div>

      <div style={{ display: 'flex', gap: 10, marginTop: 18 }}>
        <button className="btn-gold-outline" style={{ flex: 1 }}>加入购物车</button>
        <button className="btn-gold" style={{ flex: 1 }}>立即购买</button>
      </div>
      <HomeIndicator/>
    </div>
  </Phone>
);

// ============================================================
// C3. 分享商品弹窗
// ============================================================
const ScreenShare = () => (
  <Phone>
    <div style={{ position: 'relative', height: 390, background: '#0E0805', opacity: 0.5 }}>
      <ProdImg kind="maotai"/>
    </div>
    <div style={{ position: 'absolute', inset: 0, background: 'rgba(0,0,0,0.6)' }}/>

    <div style={{
      position: 'absolute', left: 0, right: 0, bottom: 0,
      background: '#fff', borderRadius: '20px 20px 0 0',
      padding: '20px 16px 24px',
    }}>
      <div style={{ textAlign: 'center', fontSize: 15, fontWeight: 600 }}>分享此商品</div>
      <div style={{ textAlign: 'center', fontSize: 12, color: 'var(--text-3)', marginTop: 6 }}>
        好友通过你的分享下单，你将获得 <span style={{ color: 'var(--gold-500)', fontWeight: 700 }}>20%</span> 推广佣金
      </div>

      {/* Product preview */}
      <div style={{ background: 'var(--cream-50)', borderRadius: 12, padding: 12, marginTop: 16, display: 'flex', gap: 10 }}>
        <div style={{ width: 56, height: 56, borderRadius: 6, overflow: 'hidden' }}>
          <ProdImg kind="maotai"/>
        </div>
        <div style={{ flex: 1, minWidth: 0 }}>
          <div style={{ fontSize: 13, fontWeight: 500, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>茅台飞天 53度 500ml</div>
          <div className="price font-num" style={{ fontSize: 16, marginTop: 4 }}><span className="sym">¥</span>2,980</div>
        </div>
        <div style={{ textAlign: 'right' }}>
          <div style={{ fontSize: 11, color: 'var(--text-3)' }}>预估佣金</div>
          <div style={{ fontSize: 15, color: 'var(--gold-500)', fontWeight: 700 }}>¥596</div>
        </div>
      </div>

      {/* Share methods */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 12, marginTop: 22 }}>
        {[
          { l: '微信好友', i: 'wechat', c: '#12B95B' },
          { l: '朋友圈',  i: 'people', c: '#0FA34F' },
          { l: '生成海报', i: 'qr',     c: '#D38A00' },
          { l: '复制链接', i: 'edit',   c: '#3A6FD1' },
        ].map(m => (
          <div key={m.l} style={{ textAlign: 'center' }}>
            <div style={{ width: 56, height: 56, margin: '0 auto', borderRadius: '50%', background: m.c + '18', display: 'flex', alignItems: 'center', justifyContent: 'center', color: m.c }}>
              <Icon name={m.i} size={26} color={m.c}/>
            </div>
            <div style={{ fontSize: 12, marginTop: 6, color: 'var(--text-2)' }}>{m.l}</div>
          </div>
        ))}
      </div>

      <div style={{ height: 1, background: 'var(--line)', margin: '20px -16px 14px' }}/>
      <div style={{ textAlign: 'center', color: 'var(--text-3)', fontSize: 14 }}>取消</div>
      <HomeIndicator/>
    </div>
  </Phone>
);

// ============================================================
// C4. 商品海报
// ============================================================
const ScreenPoster = () => (
  <Phone dark>
    <div style={{ position: 'absolute', inset: 0, background: 'linear-gradient(180deg, #1A0A02 0%, #2A0E00 100%)' }}/>
    <StatusBar theme="dark"/>
    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '4px 16px 0', color: '#F6E7C2' }}>
      <Icon name="back" size={22} color="#FFD685"/>
      <div style={{ fontSize: 16, fontWeight: 600 }}>商品海报</div>
      <div style={{ width: 22 }}/>
    </div>

    {/* Poster */}
    <div style={{
      margin: '24px 30px 0', background: '#fff', borderRadius: 14,
      padding: 14, boxShadow: '0 10px 40px rgba(0,0,0,0.45)',
    }}>
      <div style={{
        background: 'linear-gradient(180deg, #2A0E00, #3F1A00)', borderRadius: 10, padding: '14px 12px 10px',
        color: '#FFD685', display: 'flex', alignItems: 'center', gap: 10,
      }}>
        <BrandMark size={36}/>
        <div>
          <div style={{ fontSize: 15, fontWeight: 700 }}>醇品汇</div>
          <div style={{ fontSize: 10, color: 'rgba(255,214,133,0.7)' }}>甄选美酒 · 共创财富</div>
        </div>
      </div>
      <div style={{ aspectRatio: '1/1', borderRadius: 10, overflow: 'hidden', margin: '12px 0 12px' }}>
        <ProdImg kind="maotai"/>
      </div>
      <div style={{ fontSize: 13, fontWeight: 600, lineHeight: 1.4, color: '#1A1006' }}>
        茅台飞天 53度酱香型白酒 500ml
      </div>
      <div style={{ display: 'flex', gap: 4, marginTop: 6 }}>
        <span className="tag tag-gold" style={{ fontSize: 10 }}>主产品区</span>
        <span className="tag tag-blue" style={{ fontSize: 10 }}>首购20%</span>
      </div>
      <div style={{ display: 'flex', alignItems: 'flex-end', justifyContent: 'space-between', marginTop: 14 }}>
        <div>
          <div style={{ fontSize: 10, color: 'var(--text-3)' }}>联营商价</div>
          <div className="price font-num" style={{ fontSize: 24 }}><span className="sym">¥</span>2,980</div>
        </div>
        <div style={{ width: 64, height: 64, background: '#fff', border: '1px solid var(--line)', borderRadius: 6, padding: 4 }}>
          {/* QR placeholder */}
          <div style={{ width: '100%', height: '100%', background: 
            `radial-gradient(circle at 20% 20%, #1A1006 25%, transparent 25%) 0 0/12px 12px,
             radial-gradient(circle at 80% 20%, #1A1006 25%, transparent 25%) 0 0/12px 12px,
             radial-gradient(circle at 20% 80%, #1A1006 25%, transparent 25%) 0 0/12px 12px,
             linear-gradient(#1A1006 0 0) 8px 8px/4px 4px,
             linear-gradient(#1A1006 0 0) 32px 14px/4px 4px,
             linear-gradient(#1A1006 0 0) 20px 28px/6px 6px,
             linear-gradient(#1A1006 0 0) 38px 36px/4px 4px`,
            backgroundRepeat: 'no-repeat',
          }}/>
        </div>
      </div>
      <div style={{ fontSize: 10, color: 'var(--text-3)', marginTop: 6, textAlign: 'right' }}>长按识别 · 立即购买</div>
    </div>

    {/* Bottom actions */}
    <div style={{ position: 'absolute', left: 16, right: 16, bottom: 26, display: 'flex', gap: 10 }}>
      <button className="btn-gold-outline" style={{ flex: 1, background: 'rgba(255,255,255,0.08)', borderColor: 'rgba(255,214,133,0.4)', color: '#FFD685' }}>
        <Icon name="edit" size={16} color="#FFD685" style={{ marginRight: 4 }}/>保存到相册
      </button>
      <button className="btn-gold" style={{ flex: 1 }}>
        <Icon name="wechat" size={18} color="#3A1A00" style={{ marginRight: 4 }}/>分享给好友
      </button>
    </div>
    <HomeIndicator dark/>
  </Phone>
);

// ============================================================
// C5. 购物车
// ============================================================
const ScreenCart = () => (
  <Phone>
    <div style={{ background: '#fff' }}>
      <StatusBar theme="light"/>
      <div style={{ padding: '4px 16px 12px', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <div style={{ fontSize: 17, fontWeight: 600 }}>购物车 <span style={{ color: 'var(--text-3)', fontSize: 13, fontWeight: 400 }}>(3)</span></div>
        <div style={{ fontSize: 13, color: 'var(--text-2)' }}>管理</div>
      </div>
    </div>

    <div style={{ padding: '12px 16px 130px', overflow: 'auto', height: 'calc(100% - 88px - 80px)' }}>
      {/* Section: 联营商品 */}
      <div style={{ display: 'flex', alignItems: 'center', gap: 8, padding: '4px 0 10px' }}>
        <div style={{ width: 18, height: 18, borderRadius: '50%', background: 'var(--grad-gold)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <Icon name="check" size={12} color="#fff" strokeWidth={3}/>
        </div>
        <div style={{ fontSize: 13, fontWeight: 600 }}>主产品专区</div>
        <div className="tag tag-gold">联营商专享</div>
      </div>

      {[
        { name: '茅台飞天 53度酱香型 500ml', spec: '500ml 单瓶', price: '2,980', qty: 2, kind: 'maotai', sel: true },
        { name: '五粮液普五52度浓香型 500ml', spec: '500ml 单瓶', price: '1,380', qty: 1, kind: 'wuliang', sel: true },
      ].map((p, i) => (
        <div key={i} style={{ background: '#fff', borderRadius: 12, padding: 12, marginBottom: 10, display: 'flex', gap: 10, alignItems: 'flex-start' }}>
          <div style={{ alignSelf: 'center' }}>
            <div style={{ width: 20, height: 20, borderRadius: '50%', background: p.sel ? 'var(--grad-gold)' : '#fff', border: p.sel ? 'none' : '1.5px solid var(--line)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
              {p.sel && <Icon name="check" size={13} color="#fff" strokeWidth={3}/>}
            </div>
          </div>
          <div style={{ width: 80, height: 80, borderRadius: 8, overflow: 'hidden' }}>
            <ProdImg kind={p.kind}/>
          </div>
          <div style={{ flex: 1, minWidth: 0 }}>
            <div style={{ fontSize: 13, fontWeight: 500, lineHeight: 1.4, display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical', overflow: 'hidden' }}>{p.name}</div>
            <div style={{ display: 'inline-block', marginTop: 6, fontSize: 11, color: 'var(--text-3)', background: 'var(--bg-app)', padding: '2px 8px', borderRadius: 4 }}>{p.spec} ›</div>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginTop: 10 }}>
              <div className="price font-num" style={{ fontSize: 17 }}><span className="sym">¥</span>{p.price}</div>
              <div style={{ display: 'flex', alignItems: 'center', border: '1px solid var(--line)', borderRadius: 6 }}>
                <div style={{ width: 26, height: 26, display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--text-3)' }}><Icon name="minus" size={12}/></div>
                <div style={{ width: 32, textAlign: 'center', fontSize: 13, fontWeight: 600, padding: '4px 0', borderLeft: '1px solid var(--line)', borderRight: '1px solid var(--line)' }}>{p.qty}</div>
                <div style={{ width: 26, height: 26, display: 'flex', alignItems: 'center', justifyContent: 'center' }}><Icon name="plus" size={12}/></div>
              </div>
            </div>
          </div>
        </div>
      ))}

      <div style={{ display: 'flex', alignItems: 'center', gap: 8, padding: '14px 0 10px' }}>
        <div style={{ width: 18, height: 18, borderRadius: '50%', background: '#fff', border: '1.5px solid var(--line)' }}/>
        <div style={{ fontSize: 13, fontWeight: 600 }}>零售专区</div>
      </div>

      <div style={{ background: '#fff', borderRadius: 12, padding: 12, marginBottom: 10, display: 'flex', gap: 10, alignItems: 'flex-start' }}>
        <div style={{ alignSelf: 'center' }}>
          <div style={{ width: 20, height: 20, borderRadius: '50%', background: '#fff', border: '1.5px solid var(--line)' }}/>
        </div>
        <div style={{ width: 80, height: 80, borderRadius: 8, overflow: 'hidden' }}><ProdImg kind="redwine"/></div>
        <div style={{ flex: 1 }}>
          <div style={{ fontSize: 13, fontWeight: 500 }}>法国波尔多干红葡萄酒 750ml</div>
          <div style={{ display: 'inline-block', marginTop: 6, fontSize: 11, color: 'var(--text-3)', background: 'var(--bg-app)', padding: '2px 8px', borderRadius: 4 }}>750ml 单瓶 ›</div>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginTop: 10 }}>
            <div className="price font-num" style={{ fontSize: 17 }}><span className="sym">¥</span>298</div>
            <div style={{ display: 'flex', alignItems: 'center', border: '1px solid var(--line)', borderRadius: 6 }}>
              <div style={{ width: 26, height: 26, display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--text-3)' }}><Icon name="minus" size={12}/></div>
              <div style={{ width: 32, textAlign: 'center', fontSize: 13, fontWeight: 600, padding: '4px 0', borderLeft: '1px solid var(--line)', borderRight: '1px solid var(--line)' }}>1</div>
              <div style={{ width: 26, height: 26, display: 'flex', alignItems: 'center', justifyContent: 'center' }}><Icon name="plus" size={12}/></div>
            </div>
          </div>
        </div>
      </div>
    </div>

    {/* Bottom checkout bar */}
    <div style={{
      position: 'absolute', left: 0, right: 0, bottom: 76,
      background: '#fff', borderTop: '1px solid var(--line-soft)',
      padding: '12px 14px', display: 'flex', alignItems: 'center', gap: 10,
    }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: 6, fontSize: 13 }}>
        <div style={{ width: 20, height: 20, borderRadius: '50%', background: 'var(--grad-gold)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <Icon name="check" size={12} color="#fff" strokeWidth={3}/>
        </div>
        全选
      </div>
      <div style={{ marginLeft: 'auto', textAlign: 'right' }}>
        <div style={{ fontSize: 12, color: 'var(--text-3)' }}>合计 <span className="price font-num" style={{ fontSize: 20 }}><span className="sym">¥</span>7,340</span></div>
        <div style={{ fontSize: 11, color: 'var(--gold-500)' }}>预估佣金 ¥1,468</div>
      </div>
      <button className="btn-gold" style={{ height: 44, padding: '0 22px' }}>结算 (2)</button>
    </div>
    <TabBar active="home"/>
  </Phone>
);

// ============================================================
// C6. 确认订单
// ============================================================
const ScreenCheckout = () => (
  <Phone>
    <div style={{ background: 'var(--bg-app)' }}>
      <StatusBar theme="light"/>
      <NavBar title="确认订单" theme="on-light"/>
    </div>
    <div style={{ padding: '8px 16px 100px', overflow: 'auto', height: 'calc(100% - 88px - 70px)' }}>
      {/* Address */}
      <div style={{ background: '#fff', borderRadius: 12, padding: '14px', display: 'flex', alignItems: 'flex-start', gap: 12 }}>
        <Icon name="pin" size={22} color="#D38A00"/>
        <div style={{ flex: 1 }}>
          <div style={{ fontSize: 14, fontWeight: 600 }}>李明远 <span style={{ fontWeight: 400, color: 'var(--text-2)', marginLeft: 6 }}>138 **** 8888</span></div>
          <div style={{ fontSize: 13, color: 'var(--text-2)', marginTop: 4, lineHeight: 1.5 }}>
            <span className="tag tag-gold" style={{ marginRight: 6 }}>默认</span>
            北京市朝阳区建国门外大街1号国贸大厦A座 28层2801室
          </div>
        </div>
        <Icon name="chevron" size={16} color="#C9BFA9" style={{ marginTop: 4 }}/>
      </div>

      {/* Items */}
      <div style={{ background: '#fff', borderRadius: 12, padding: '12px 14px', marginTop: 10 }}>
        <div style={{ fontSize: 13, fontWeight: 600, marginBottom: 10 }}>商品 · 共 3 件</div>
        {[
          { n: '茅台飞天 53度 500ml', p: '2,980', q: 2, k: 'maotai' },
          { n: '五粮液普五52度 500ml', p: '1,380', q: 1, k: 'wuliang' },
        ].map((p, i) => (
          <div key={i} style={{ display: 'flex', gap: 10, padding: '8px 0', borderTop: i > 0 ? '1px solid var(--line-soft)' : 'none' }}>
            <div style={{ width: 60, height: 60, borderRadius: 6, overflow: 'hidden' }}><ProdImg kind={p.k}/></div>
            <div style={{ flex: 1 }}>
              <div style={{ fontSize: 13, fontWeight: 500 }}>{p.n}</div>
              <div style={{ fontSize: 11, color: 'var(--text-3)', marginTop: 2 }}>500ml 单瓶</div>
            </div>
            <div style={{ textAlign: 'right' }}>
              <div className="price font-num" style={{ fontSize: 14 }}><span className="sym">¥</span>{p.p}</div>
              <div style={{ fontSize: 11, color: 'var(--text-3)', marginTop: 2 }}>×{p.q}</div>
            </div>
          </div>
        ))}
      </div>

      {/* Coupon & note */}
      <div style={{ background: '#fff', borderRadius: 12, marginTop: 10 }}>
        <div className="form-row" style={{ background: 'transparent' }}>
          <div className="label">优惠券</div>
          <div className="field" style={{ textAlign: 'right' }}>
            <span style={{ color: 'var(--gold-500)' }}>-¥300 满3000减300</span>
          </div>
          <Icon name="chevron" size={14} color="#C9BFA9" style={{ marginLeft: 6 }}/>
        </div>
        <div className="form-row" style={{ background: 'transparent' }}>
          <div className="label">积分抵扣</div>
          <div className="field" style={{ textAlign: 'right' }}>
            <span className="text-3">1,280 积分可抵 ¥12.80</span>
          </div>
          <div style={{ width: 36, height: 20, borderRadius: 999, background: 'var(--grad-gold)', position: 'relative', marginLeft: 6 }}>
            <div style={{ position: 'absolute', top: 2, right: 2, width: 16, height: 16, background: '#fff', borderRadius: '50%' }}/>
          </div>
        </div>
        <div className="form-row" style={{ background: 'transparent', borderBottom: 'none' }}>
          <div className="label">备注</div>
          <div className="field" style={{ color: 'var(--text-3)' }}>选填，请留言…</div>
        </div>
      </div>

      {/* Price breakdown */}
      <div style={{ background: '#fff', borderRadius: 12, padding: '14px', marginTop: 10, fontSize: 13 }}>
        {[
          ['商品总额', '¥7,340.00'],
          ['运费',     '免运费'],
          ['优惠券',   <span style={{ color: 'var(--gold-500)' }}>-¥300.00</span>],
          ['积分抵扣', <span style={{ color: 'var(--gold-500)' }}>-¥12.80</span>],
        ].map(([k, v], i) => (
          <div key={i} style={{ display: 'flex', justifyContent: 'space-between', padding: '4px 0' }}>
            <div className="text-2">{k}</div>
            <div className="font-num">{v}</div>
          </div>
        ))}
        <div style={{ borderTop: '1px solid var(--line-soft)', marginTop: 10, paddingTop: 10, display: 'flex', justifyContent: 'space-between', alignItems: 'baseline' }}>
          <div className="text-2" style={{ fontSize: 12 }}>共 3 件，应付</div>
          <div className="price font-num" style={{ fontSize: 22 }}><span className="sym">¥</span>7,027.20</div>
        </div>
      </div>

      <div style={{ marginTop: 10, padding: '10px 14px', background: 'var(--gold-50)', borderRadius: 10, fontSize: 12, color: '#7A5610' }}>
        ℹ︎ 此订单预计获得佣金 <span style={{ fontWeight: 700, color: '#D38A00' }}>¥1,405.44</span> · 确认收货后到账
      </div>
    </div>

    {/* Bottom pay bar */}
    <div style={{
      position: 'absolute', left: 0, right: 0, bottom: 0,
      background: '#fff', borderTop: '1px solid var(--line-soft)',
      padding: '12px 16px 26px', display: 'flex', alignItems: 'center',
    }}>
      <div>
        <div style={{ fontSize: 11, color: 'var(--text-3)' }}>合计应付</div>
        <div className="price font-num" style={{ fontSize: 22 }}><span className="sym">¥</span>7,027.20</div>
      </div>
      <button className="btn-gold" style={{ marginLeft: 'auto', height: 46, padding: '0 32px' }}>提交订单</button>
    </div>
  </Phone>
);

// ============================================================
// C7. 收货地址列表
// ============================================================
const ScreenAddressList = () => (
  <Phone>
    <div style={{ background: '#fff' }}>
      <StatusBar theme="light"/>
      <NavBar title="收货地址" theme="on-light" right={<Icon name="plus" size={22}/>}/>
    </div>
    <div style={{ padding: 16, overflow: 'auto', height: 'calc(100% - 88px - 80px)' }}>
      {[
        { name: '李明远', phone: '138 **** 8888', addr: '北京市朝阳区建国门外大街1号国贸大厦A座 28层2801室', def: true },
        { name: '李明远', phone: '138 **** 8888', addr: '北京市朝阳区望京SOHO T3 A座 1208室' },
        { name: '李太太', phone: '139 **** 6666', addr: '北京市朝阳区三里屯太古里南区 5号院 12-3-501' },
      ].map((a, i) => (
        <div key={i} style={{ background: '#fff', borderRadius: 12, padding: 14, marginBottom: 12, boxShadow: 'var(--shadow-card)' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
            <div style={{ fontSize: 15, fontWeight: 600 }}>{a.name}</div>
            <div style={{ fontSize: 13, color: 'var(--text-2)' }}>{a.phone}</div>
            {a.def && <div className="tag tag-gold-solid">默认</div>}
            <Icon name="edit" size={16} color="#C9BFA9" style={{ marginLeft: 'auto' }}/>
          </div>
          <div style={{ fontSize: 13, color: 'var(--text-2)', marginTop: 8, lineHeight: 1.5 }}>{a.addr}</div>
        </div>
      ))}
    </div>
    <div style={{ position: 'absolute', left: 16, right: 16, bottom: 30 }}>
      <button className="btn-gold" style={{ width: '100%' }}>
        <Icon name="plus" size={18} color="#3A1A00"/> 新增收货地址
      </button>
    </div>
    <HomeIndicator/>
  </Phone>
);

// ============================================================
// C8. 编辑地址
// ============================================================
const ScreenAddressEdit = () => (
  <Phone>
    <div style={{ background: '#fff' }}>
      <StatusBar theme="light"/>
      <NavBar title="编辑地址" theme="on-light"/>
    </div>
    <div style={{ background: 'var(--bg-app)', padding: '12px 0', height: 'calc(100% - 88px - 100px)', overflow: 'auto' }}>
      <div style={{ background: '#fff' }}>
        <div className="form-row">
          <div className="label">收货人</div>
          <input className="field" defaultValue="李明远"/>
        </div>
        <div className="form-row">
          <div className="label">手机号</div>
          <input className="field" defaultValue="138 8888 8888"/>
        </div>
        <div className="form-row">
          <div className="label">所在地区</div>
          <div className="field">北京市 · 朝阳区</div>
          <Icon name="chevron" size={14} color="#C9BFA9"/>
        </div>
        <div className="form-row" style={{ alignItems: 'flex-start', paddingTop: 14 }}>
          <div className="label" style={{ paddingTop: 2 }}>详细地址</div>
          <textarea className="field" rows="2" style={{ resize: 'none', lineHeight: 1.5 }} defaultValue="建国门外大街1号国贸大厦A座 28层2801室"/>
        </div>
        <div className="form-row">
          <div className="label">门牌号</div>
          <input className="field" placeholder="选填" defaultValue=""/>
        </div>
      </div>

      <div style={{ background: '#fff', marginTop: 12 }}>
        <div className="form-row" style={{ borderBottom: 'none' }}>
          <div style={{ flex: 1 }}>设为默认地址</div>
          <div style={{ width: 40, height: 22, borderRadius: 999, background: 'var(--grad-gold)', position: 'relative' }}>
            <div style={{ position: 'absolute', top: 2, right: 2, width: 18, height: 18, background: '#fff', borderRadius: '50%' }}/>
          </div>
        </div>
      </div>

      <div style={{ padding: '14px 16px 0' }}>
        <div style={{ fontSize: 12, color: 'var(--text-3)', marginBottom: 8 }}>地址标签 (选填)</div>
        <div style={{ display: 'flex', gap: 8 }}>
          {['家', '公司', '父母', '其他'].map((t, i) => (
            <div key={t} style={{
              padding: '6px 16px', borderRadius: 6,
              background: i === 1 ? 'var(--gold-50)' : '#fff',
              border: i === 1 ? '1px solid var(--gold-300)' : '1px solid var(--line)',
              color: i === 1 ? 'var(--gold-600)' : 'var(--text-2)',
              fontSize: 13,
            }}>{t}</div>
          ))}
        </div>
      </div>
    </div>
    <div style={{ position: 'absolute', left: 16, right: 16, bottom: 30 }}>
      <button className="btn-gold" style={{ width: '100%' }}>保存</button>
    </div>
    <HomeIndicator/>
  </Phone>
);

// ============================================================
// C9. 支付方式
// ============================================================
const ScreenPayMethod = () => (
  <Phone>
    <div style={{ background: '#fff' }}>
      <StatusBar theme="light"/>
      <NavBar title="选择支付方式" theme="on-light"/>
    </div>
    <div style={{ padding: '20px 16px', height: 'calc(100% - 88px)', display: 'flex', flexDirection: 'column' }}>
      <div style={{ textAlign: 'center' }}>
        <div style={{ fontSize: 12, color: 'var(--text-3)' }}>实付金额</div>
        <div className="price font-num" style={{ fontSize: 36, marginTop: 4 }}><span className="sym">¥</span>7,027.20</div>
        <div style={{ fontSize: 12, color: 'var(--text-3)', marginTop: 4 }}>订单将在 <span style={{ color: 'var(--red-500)', fontWeight: 600 }}>14:59</span> 后自动取消</div>
      </div>

      <div style={{ background: '#fff', borderRadius: 12, marginTop: 30, boxShadow: 'var(--shadow-card)' }}>
        {[
          { l: '微信支付', sub: '推荐 · 极速到账', i: 'wechat', c: '#12B95B', a: true },
          { l: '余额支付', sub: '可用余额 ¥24,860', i: 'wallet', c: '#D38A00' },
          { l: 'DF 余额支付', sub: '可用 ¥1,500', i: 'coin', c: '#3A6FD1' },
        ].map((m, i) => (
          <div key={i} style={{ display: 'flex', alignItems: 'center', gap: 12, padding: '16px 16px', borderBottom: i < 2 ? '1px solid var(--line-soft)' : 'none' }}>
            <div style={{ width: 38, height: 38, borderRadius: '50%', background: m.c + '18', display: 'flex', alignItems: 'center', justifyContent: 'center', color: m.c }}>
              <Icon name={m.i} size={22} color={m.c}/>
            </div>
            <div style={{ flex: 1 }}>
              <div style={{ fontSize: 14, fontWeight: 600 }}>{m.l}</div>
              <div style={{ fontSize: 11, color: 'var(--text-3)', marginTop: 2 }}>{m.sub}</div>
            </div>
            {m.a ? (
              <div style={{ width: 22, height: 22, borderRadius: '50%', background: 'var(--grad-gold)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <Icon name="check" size={14} color="#fff" strokeWidth={3}/>
              </div>
            ) : (
              <div style={{ width: 22, height: 22, borderRadius: '50%', border: '1.5px solid var(--line)' }}/>
            )}
          </div>
        ))}
      </div>

      <button className="btn-gold" style={{ width: '100%', marginTop: 'auto', marginBottom: 30 }}>立即支付 ¥7,027.20</button>
    </div>
  </Phone>
);

// ============================================================
// C10. 支付成功
// ============================================================
const ScreenPaySuccess = () => (
  <Phone>
    <div style={{ background: '#fff' }}>
      <StatusBar theme="light"/>
      <NavBar title="支付结果" theme="on-light" left={<Icon name="close" size={22}/>}/>
    </div>
    <div style={{ padding: '50px 16px 30px', display: 'flex', flexDirection: 'column', height: 'calc(100% - 88px)' }}>
      <div style={{ textAlign: 'center' }}>
        <div style={{
          width: 80, height: 80, borderRadius: '50%',
          background: 'var(--grad-gold)',
          margin: '0 auto', display: 'flex', alignItems: 'center', justifyContent: 'center',
          boxShadow: '0 10px 30px rgba(211,138,0,0.32)',
        }}>
          <Icon name="check" size={42} color="#fff" strokeWidth={2.6}/>
        </div>
        <div style={{ fontSize: 20, fontWeight: 700, marginTop: 18 }}>支付成功</div>
        <div className="price font-num" style={{ fontSize: 30, marginTop: 8 }}><span className="sym">¥</span>7,027.20</div>
        <div style={{ fontSize: 12, color: 'var(--text-3)', marginTop: 6 }}>订单号: CPH202605161641892</div>
      </div>

      <div style={{ background: 'var(--gold-50)', borderRadius: 12, padding: 16, marginTop: 30, border: '1px solid #F2E0B0' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 6 }}>
          <Icon name="gift" size={18} color="#D38A00"/>
          <div style={{ fontSize: 13, fontWeight: 600, color: '#7A5610' }}>本单预估佣金 <span className="font-num" style={{ fontSize: 18, color: '#D38A00' }}>¥1,405.44</span></div>
        </div>
        <div style={{ fontSize: 11, color: '#7A5610', lineHeight: 1.6 }}>
          佣金将在确认收货后 7 天内到账；分享给好友，每单还可获得 20% 推广奖励。
        </div>
      </div>

      <div style={{ display: 'flex', gap: 10, marginTop: 24 }}>
        <button className="btn-gold-outline" style={{ flex: 1 }}>查看订单</button>
        <button className="btn-gold" style={{ flex: 1 }}>
          <Icon name="share" size={16} color="#3A1A00"/> 分享获佣
        </button>
      </div>

      <div style={{ marginTop: 28 }}>
        <div style={{ fontSize: 13, fontWeight: 600, marginBottom: 10 }}>· 推荐好物 ·</div>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
          <ProductCard name="国窖1573经典装 500ml" kind="luzhou" price="980"/>
          <ProductCard name="洋河梦之蓝M6+ 500ml" kind="yanghe" price="868"/>
        </div>
      </div>
    </div>
    <HomeIndicator/>
  </Phone>
);

// ============================================================
// C11. 支付失败
// ============================================================
const ScreenPayFail = () => (
  <Phone>
    <div style={{ background: '#fff' }}>
      <StatusBar theme="light"/>
      <NavBar title="支付结果" theme="on-light" left={<Icon name="close" size={22}/>}/>
    </div>
    <div style={{ padding: '50px 16px 30px', display: 'flex', flexDirection: 'column', height: 'calc(100% - 88px)' }}>
      <div style={{ textAlign: 'center' }}>
        <div style={{
          width: 80, height: 80, borderRadius: '50%',
          background: '#FFE9E6', margin: '0 auto',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
        }}>
          <Icon name="close" size={42} color="#D6453B" strokeWidth={2.6}/>
        </div>
        <div style={{ fontSize: 20, fontWeight: 700, marginTop: 18 }}>支付未完成</div>
        <div style={{ fontSize: 13, color: 'var(--text-2)', marginTop: 10, lineHeight: 1.6, maxWidth: 280, margin: '10px auto 0' }}>
          您的银行账户余额不足，请更换支付方式或在 <span style={{ color: 'var(--red-500)', fontWeight: 600 }}>14:32</span> 内重试
        </div>
      </div>

      <div style={{ background: 'var(--cream-50)', borderRadius: 12, padding: 16, marginTop: 30, fontSize: 13 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', padding: '4px 0' }}>
          <div className="text-3">订单号</div>
          <div className="font-num">CPH202605161641892</div>
        </div>
        <div style={{ display: 'flex', justifyContent: 'space-between', padding: '4px 0' }}>
          <div className="text-3">订单金额</div>
          <div className="font-num">¥7,027.20</div>
        </div>
        <div style={{ display: 'flex', justifyContent: 'space-between', padding: '4px 0' }}>
          <div className="text-3">支付方式</div>
          <div>微信支付</div>
        </div>
      </div>

      <div style={{ display: 'flex', gap: 10, marginTop: 'auto', marginBottom: 30 }}>
        <button className="btn-gold-outline" style={{ flex: 1 }}>取消订单</button>
        <button className="btn-gold" style={{ flex: 1.4 }}>重新支付</button>
      </div>
    </div>
    <HomeIndicator/>
  </Phone>
);

Object.assign(window, {
  ScreenProductDetail, ScreenSku, ScreenShare, ScreenPoster,
  ScreenCart, ScreenCheckout, ScreenAddressList, ScreenAddressEdit,
  ScreenPayMethod, ScreenPaySuccess, ScreenPayFail,
});
