// section-e.jsx — Dynamic (content/announcements)

// ============================================================
// E1. 动态首页
// ============================================================
const ScreenDynamic = () => (
  <Phone>
    {/* Brown header */}
    <div style={{ background: 'var(--grad-brown)', color: '#F6E7C2', paddingBottom: 14 }}>
      <StatusBar theme="dark"/>
      <div style={{ padding: '8px 16px 12px' }}>
        <div style={{ fontSize: 22, fontWeight: 700, color: '#FFE0A0' }}>动态</div>
        <div style={{ fontSize: 12, color: 'rgba(246,231,194,0.6)', marginTop: 2 }}>关注平台公告、品酒知识与新品上市</div>
      </div>
      {/* Sub-tabs */}
      <div style={{ display: 'flex', gap: 18, padding: '0 16px', fontSize: 14 }}>
        {[
          { l: '推荐', a: true },
          { l: '公告' },
          { l: '品酒知识' },
          { l: '联营商动态' },
        ].map(t => (
          <div key={t.l} style={{
            paddingBottom: 8, fontWeight: t.a ? 600 : 400,
            color: t.a ? '#FFE0A0' : 'rgba(246,231,194,0.55)',
            position: 'relative',
          }}>
            {t.l}
            {t.a && <div style={{ position: 'absolute', bottom: 0, left: '50%', transform: 'translateX(-50%)', width: 18, height: 2, background: 'var(--grad-gold)' }}/>}
          </div>
        ))}
      </div>
    </div>

    <div style={{ background: 'var(--bg-app)', overflow: 'auto', height: 'calc(100% - 110px - 30px - 76px)', padding: '12px 16px 24px' }}>
      {/* Pinned notice */}
      <div style={{ background: '#fff', borderRadius: 12, padding: '14px 16px', display: 'flex', alignItems: 'center', gap: 10, marginBottom: 12, borderLeft: '3px solid var(--gold-500)' }}>
        <div className="tag tag-gold-solid" style={{ flexShrink: 0 }}>置顶</div>
        <div style={{ flex: 1, minWidth: 0, fontSize: 13, color: 'var(--text-1)' }}>
          <div style={{ overflow: 'hidden', whiteSpace: 'nowrap', textOverflow: 'ellipsis', fontWeight: 600 }}>2026 秋季新品上市 · 联营商招商福利启动</div>
          <div style={{ fontSize: 11, color: 'var(--text-3)', marginTop: 4 }}>2 小时前 · 阅读 1.2w</div>
        </div>
      </div>

      {/* Feed card 1 - large image */}
      <div style={{ background: '#fff', borderRadius: 12, overflow: 'hidden', marginBottom: 12, boxShadow: 'var(--shadow-card)' }}>
        <div style={{ aspectRatio: '16/9', position: 'relative' }}>
          <ProdImg kind="redwine"/>
          <div style={{ position: 'absolute', top: 10, left: 10 }} className="tag tag-gold-solid">活动</div>
        </div>
        <div style={{ padding: 14 }}>
          <div style={{ fontSize: 15, fontWeight: 600 }}>中秋商务季 · 联营商专享至高省 ¥600</div>
          <div style={{ fontSize: 12, color: 'var(--text-3)', marginTop: 6, lineHeight: 1.6 }}>
            即日起至 9 月 30 日，联营商分享主产品专区商品，最高享 30% 推广佣金…
          </div>
          <div style={{ display: 'flex', alignItems: 'center', marginTop: 10, fontSize: 11, color: 'var(--text-3)' }}>
            <Icon name="eye" size={13} color="#9B8E7C" style={{ marginRight: 4 }}/> 8,632 阅读
            <Icon name="heart" size={13} color="#9B8E7C" style={{ marginLeft: 12, marginRight: 4 }}/> 1,205
            <div style={{ marginLeft: 'auto' }}>5 月 14 日</div>
          </div>
        </div>
      </div>

      {/* Feed card 2 - small image */}
      {[
        { tag: '品酒知识', title: '一文读懂酱香、浓香、清香——三大白酒香型品鉴指南', sub: '酱香突出、幽雅细腻、回味悠长——三大主流香型的口感差异…', k: 'maotai', read: '5.4k', like: '623' },
        { tag: '联营商', title: '从县级到市级：3 个月业绩翻倍的真实复盘', sub: '本期分享：北京张姐如何用 3 个月将业绩从 ¥5w 做到 ¥18w…', k: 'wuliang', read: '3.1k', like: '418' },
        { tag: '新品上市', title: '法国波尔多名庄 2022 年份红酒到货预告', sub: '5 月 25 日 10:00 限量首发，仅限联营商专享通道…', k: 'redwine', read: '1.8k', like: '256' },
      ].map((c, i) => (
        <div key={i} style={{ background: '#fff', borderRadius: 12, padding: 14, marginBottom: 12, display: 'flex', gap: 12, boxShadow: 'var(--shadow-card)' }}>
          <div style={{ flex: 1, minWidth: 0 }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
              <span className="tag tag-gold">{c.tag}</span>
            </div>
            <div style={{ fontSize: 14, fontWeight: 600, marginTop: 8, lineHeight: 1.4 }}>{c.title}</div>
            <div style={{ fontSize: 12, color: 'var(--text-3)', marginTop: 6, lineHeight: 1.5, display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical', overflow: 'hidden' }}>{c.sub}</div>
            <div style={{ display: 'flex', alignItems: 'center', marginTop: 10, fontSize: 11, color: 'var(--text-3)' }}>
              <Icon name="eye" size={12} color="#9B8E7C" style={{ marginRight: 3 }}/> {c.read}
              <Icon name="heart" size={12} color="#9B8E7C" style={{ marginLeft: 10, marginRight: 3 }}/> {c.like}
            </div>
          </div>
          <div style={{ width: 90, height: 90, borderRadius: 8, overflow: 'hidden', flexShrink: 0 }}>
            <ProdImg kind={c.k}/>
          </div>
        </div>
      ))}
    </div>
    <TabBar active="dynamic"/>
  </Phone>
);

// ============================================================
// E2. 公告列表
// ============================================================
const ScreenNoticeList = () => (
  <Phone>
    <div style={{ background: '#fff' }}>
      <StatusBar theme="light"/>
      <NavBar title="平台公告" theme="on-light"/>
    </div>
    <div style={{ overflow: 'auto', height: 'calc(100% - 88px)', background: 'var(--bg-app)' }}>
      {[
        { d: '2026-05-16', cat: '系统', title: '关于秋季新品上市及联营商福利活动的通知', sub: '各位联营商伙伴：为响应秋季消费旺季……', new: true },
        { d: '2026-05-12', cat: '规则', title: '关于「招商奖励」结算规则升级的公告', sub: '自 2026 年 6 月 1 日起，招商奖励结算……' },
        { d: '2026-05-08', cat: '系统', title: '关于「DF 余额」上线说明与使用规则' },
        { d: '2026-05-02', cat: '活动', title: '五一假期联营商专享团购活动战报' },
        { d: '2026-04-28', cat: '规则', title: '关于「县级 → 市级」升级流程的优化通知' },
        { d: '2026-04-20', cat: '系统', title: '关于体验店申请审核周期调整的通知' },
        { d: '2026-04-12', cat: '活动', title: '春季招商大会 · 联营商培训回顾' },
      ].map((n, i) => (
        <div key={i} style={{ background: '#fff', padding: '14px 16px', borderBottom: '1px solid var(--line-soft)', display: 'flex', gap: 12 }}>
          <div style={{ width: 44, height: 44, borderRadius: 10, background: n.cat === '系统' ? '#FBE9B6' : n.cat === '活动' ? '#FCDDE6' : '#DCE5FA', display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0, color: n.cat === '系统' ? '#D38A00' : n.cat === '活动' ? '#D1467A' : '#3A6FD1' }}>
            <Icon name={n.cat === '系统' ? 'bell' : n.cat === '活动' ? 'gift' : 'info'} size={22}/>
          </div>
          <div style={{ flex: 1, minWidth: 0 }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
              <span className="tag" style={{ background: n.cat === '系统' ? '#FBE9B6' : n.cat === '活动' ? '#FCDDE6' : '#DCE5FA', color: n.cat === '系统' ? '#D38A00' : n.cat === '活动' ? '#D1467A' : '#3A6FD1' }}>{n.cat}</span>
              <div style={{ fontSize: 11, color: 'var(--text-3)' }}>{n.d}</div>
              {n.new && <div style={{ width: 6, height: 6, borderRadius: '50%', background: '#D6453B', marginLeft: 'auto' }}/>}
            </div>
            <div style={{ fontSize: 14, fontWeight: 600, marginTop: 6, lineHeight: 1.4 }}>{n.title}</div>
            {n.sub && <div style={{ fontSize: 12, color: 'var(--text-3)', marginTop: 4, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{n.sub}</div>}
          </div>
        </div>
      ))}
    </div>
    <HomeIndicator/>
  </Phone>
);

// ============================================================
// E3. 公告详情
// ============================================================
const ScreenNoticeDetail = () => (
  <Phone>
    <div style={{ background: '#fff' }}>
      <StatusBar theme="light"/>
      <NavBar title="公告详情" theme="on-light" right={<Icon name="share" size={20}/>}/>
    </div>
    <div style={{ padding: '16px 20px 30px', overflow: 'auto', height: 'calc(100% - 88px)', background: '#fff' }}>
      <div className="tag tag-gold">系统公告</div>
      <div style={{ fontSize: 20, fontWeight: 700, marginTop: 10, lineHeight: 1.4 }}>关于秋季新品上市及联营商福利活动的通知</div>
      <div style={{ display: 'flex', alignItems: 'center', gap: 12, marginTop: 12, fontSize: 12, color: 'var(--text-3)' }}>
        <span>2026-05-16 09:32</span>
        <span>· 醇品汇官方</span>
        <span style={{ marginLeft: 'auto' }}>阅读 1.2w</span>
      </div>

      <div style={{ marginTop: 20, fontSize: 14, color: 'var(--text-1)', lineHeight: 1.9 }}>
        <p>尊敬的联营商伙伴：</p>
        <p>值此 2026 秋季消费旺季来临之际，醇品汇平台将于 2026 年 5 月 18 日起正式上线「秋季新品季 · 联营商专享福利」活动，特此公告。</p>

        <p style={{ fontWeight: 600, marginTop: 14 }}>一、活动时间</p>
        <p>2026 年 5 月 18 日 10:00 — 2026 年 9 月 30 日 23:59</p>

        <p style={{ fontWeight: 600, marginTop: 14 }}>二、活动福利</p>
        <p>1. 主产品专区分享，享至高 <span style={{ color: 'var(--gold-500)', fontWeight: 700 }}>30%</span> 推广佣金；<br/>
        2. 招商专区新签约联营商，享 <span style={{ color: 'var(--gold-500)', fontWeight: 700 }}>20%</span> 招商奖励；<br/>
        3. 业绩达成 ¥150,000 即可升级市级联营商，享更高权益；<br/>
        4. 体验店申请通道开放，可优先获得线下展示权益。</p>

        <p style={{ fontWeight: 600, marginTop: 14 }}>三、特别提醒</p>
        <p>1. 所有活动佣金均在消费者确认收货 7 日后结算至账户余额；<br/>
        2. 招商奖励将在新联营商首次缴纳入门金额后 24 小时内到账；<br/>
        3. 如有疑问请咨询您的所属市级联营商或平台客服。</p>

        <p style={{ marginTop: 20 }}>祝大家秋季业绩长虹！</p>
        <p style={{ color: 'var(--text-3)', marginTop: 10 }}>醇品汇运营中心<br/>2026 年 5 月 16 日</p>
      </div>

      <div style={{ marginTop: 20, padding: '12px 14px', background: 'var(--gold-50)', borderRadius: 10, fontSize: 12, color: '#7A5610', display: 'flex', alignItems: 'center', gap: 8 }}>
        <Icon name="info" size={16} color="#D38A00"/>
        本公告为官方解释，最终解释权归醇品汇平台所有
      </div>
    </div>
    <HomeIndicator/>
  </Phone>
);

// ============================================================
// E4. 知识 / 活动列表
// ============================================================
const ScreenKnowledgeList = () => (
  <Phone>
    <div style={{ background: '#fff' }}>
      <StatusBar theme="light"/>
      <NavBar title="品酒知识" theme="on-light" right={<Icon name="search" size={20}/>}/>
      <div style={{ display: 'flex', gap: 10, padding: '0 16px 12px', overflow: 'auto' }}>
        {['全部', '酱香型', '浓香型', '红酒入门', '威士忌', '搭配技巧', '品鉴方法'].map((c, i) => (
          <div key={c} style={{
            padding: '5px 14px', borderRadius: 999,
            background: i === 0 ? 'var(--gold-50)' : 'var(--bg-app)',
            border: i === 0 ? '1px solid var(--gold-300)' : 'none',
            color: i === 0 ? 'var(--gold-600)' : 'var(--text-2)',
            fontSize: 12, fontWeight: i === 0 ? 600 : 400, flexShrink: 0,
          }}>{c}</div>
        ))}
      </div>
    </div>

    <div style={{ overflow: 'auto', height: 'calc(100% - 88px - 50px)', background: 'var(--bg-app)', padding: '12px 16px 24px' }}>
      {/* Feature card */}
      <div style={{ background: '#fff', borderRadius: 14, overflow: 'hidden', marginBottom: 14, boxShadow: 'var(--shadow-card)' }}>
        <div style={{ aspectRatio: '5/3', position: 'relative' }}>
          <ProdImg kind="maotai"/>
          <div style={{ position: 'absolute', inset: 0, background: 'linear-gradient(180deg, transparent 60%, rgba(0,0,0,0.7))' }}/>
          <div style={{ position: 'absolute', bottom: 14, left: 16, right: 16, color: '#fff' }}>
            <div className="tag tag-gold-solid" style={{ marginBottom: 8 }}>精选</div>
            <div style={{ fontSize: 17, fontWeight: 700, lineHeight: 1.4 }}>三大主流白酒香型 · 品鉴入门完全指南</div>
          </div>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
        {[
          { t: '红酒醒酒 · 时间与器皿', k: 'redwine', tag: '红酒入门' },
          { t: '威士忌 · 单一麦芽与调和的差异', k: 'whisky', tag: '威士忌' },
          { t: '商务宴请 · 白酒搭配 5 法则', k: 'wuliang', tag: '搭配' },
          { t: '酒杯选择对口感的影响有多大？', k: 'champagne', tag: '品鉴' },
          { t: '清酒 · 大吟酿与本酿造', k: 'sake', tag: '清酒' },
          { t: '茅台镇 · 大曲酱香 12 道工序', k: 'maotai', tag: '工艺' },
        ].map((c, i) => (
          <div key={i} style={{ background: '#fff', borderRadius: 10, overflow: 'hidden', boxShadow: 'var(--shadow-card)' }}>
            <div style={{ aspectRatio: '4/3' }}><ProdImg kind={c.k}/></div>
            <div style={{ padding: '10px 10px 12px' }}>
              <div style={{ fontSize: 12, color: 'var(--text-1)', lineHeight: 1.4, display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical', overflow: 'hidden', minHeight: 34 }}>{c.t}</div>
              <div style={{ display: 'flex', alignItems: 'center', marginTop: 6, fontSize: 10, color: 'var(--text-3)' }}>
                <span className="tag tag-gold" style={{ fontSize: 10, padding: '1px 6px' }}>{c.tag}</span>
                <div style={{ marginLeft: 'auto' }}>{(1 + i * 0.7).toFixed(1)}k 阅读</div>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
    <HomeIndicator/>
  </Phone>
);

// ============================================================
// E5. 内容详情
// ============================================================
const ScreenArticle = () => (
  <Phone>
    <div style={{ background: '#fff' }}>
      <StatusBar theme="light"/>
      <NavBar title="品酒知识" theme="on-light" right={<Icon name="share" size={20}/>}/>
    </div>
    <div style={{ padding: '12px 20px 100px', overflow: 'auto', height: 'calc(100% - 88px)', background: '#fff' }}>
      <div className="tag tag-gold">品酒知识</div>
      <div style={{ fontSize: 22, fontWeight: 700, marginTop: 10, lineHeight: 1.4 }}>
        三大主流白酒香型 · 品鉴入门完全指南
      </div>
      <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginTop: 12 }}>
        <Avatar size={32} kind="boss"/>
        <div>
          <div style={{ fontSize: 12, fontWeight: 600 }}>醇品汇 · 品酒师</div>
          <div style={{ fontSize: 11, color: 'var(--text-3)' }}>2026-05-14 · 阅读 5.4k</div>
        </div>
        <button style={{ marginLeft: 'auto', height: 28, padding: '0 14px', borderRadius: 999, border: '1px solid var(--gold-500)', background: '#fff', color: 'var(--gold-500)', fontSize: 12, fontWeight: 600 }}>关注</button>
      </div>

      <div style={{ aspectRatio: '5/3', borderRadius: 10, overflow: 'hidden', marginTop: 16 }}>
        <ProdImg kind="maotai"/>
      </div>

      <div style={{ marginTop: 18, fontSize: 15, lineHeight: 1.9, color: 'var(--text-1)' }}>
        <p>白酒之于中国人，是宴席间的醇香、是商务间的礼数，更是岁月沉淀的味道。今天，我们就从最易上手的「香型」入门，带你读懂中国白酒。</p>

        <p style={{ fontSize: 17, fontWeight: 700, marginTop: 22, marginBottom: 4 }}>一、酱香型</p>
        <p>以 <span style={{ color: 'var(--gold-500)', fontWeight: 600 }}>茅台</span> 为代表，香气幽雅细腻、空杯留香持久。酿造工艺需「12987」：1 年生产周期、2 次投料、9 次蒸煮、8 次发酵、7 次取酒。</p>

        <p style={{ fontSize: 17, fontWeight: 700, marginTop: 18, marginBottom: 4 }}>二、浓香型</p>
        <p>以 <span style={{ color: 'var(--gold-500)', fontWeight: 600 }}>五粮液、国窖 1573</span> 为代表，香气浓郁、绵甜净爽，是中国白酒消费量最大的香型。</p>

        <p style={{ fontSize: 17, fontWeight: 700, marginTop: 18, marginBottom: 4 }}>三、清香型</p>
        <p>以 <span style={{ color: 'var(--gold-500)', fontWeight: 600 }}>汾酒</span> 为代表，「清字当头、净字到底」，入口柔和、余味爽净。</p>
      </div>

      <div style={{ marginTop: 24, padding: '14px 16px', background: 'var(--cream-50)', borderRadius: 12, border: '1px solid var(--line)' }}>
        <div style={{ fontSize: 13, fontWeight: 600, marginBottom: 10, color: 'var(--text-1)' }}>📖 推荐选品</div>
        <div style={{ display: 'flex', gap: 10, alignItems: 'center' }}>
          <div style={{ width: 60, height: 60, borderRadius: 6, overflow: 'hidden' }}><ProdImg kind="maotai"/></div>
          <div style={{ flex: 1 }}>
            <div style={{ fontSize: 13, fontWeight: 500 }}>茅台飞天 53度 500ml</div>
            <div className="price font-num" style={{ fontSize: 16, marginTop: 4 }}><span className="sym">¥</span>2,980</div>
          </div>
          <button className="btn-gold" style={{ height: 32, fontSize: 12, padding: '0 14px' }}>立即购买</button>
        </div>
      </div>
    </div>

    <div style={{ position: 'absolute', left: 0, right: 0, bottom: 0, background: '#fff', borderTop: '1px solid var(--line-soft)', padding: '10px 16px 22px', display: 'flex', alignItems: 'center', gap: 10 }}>
      <div style={{ flex: 1, height: 36, borderRadius: 999, background: 'var(--bg-app)', padding: '0 14px', display: 'flex', alignItems: 'center', fontSize: 12, color: 'var(--text-3)' }}>写下你的看法…</div>
      <Icon name="heart" size={22} color="#9B8E7C"/>
      <Icon name="share" size={22} color="#9B8E7C"/>
    </div>
  </Phone>
);

Object.assign(window, { ScreenDynamic, ScreenNoticeList, ScreenNoticeDetail, ScreenKnowledgeList, ScreenArticle });
