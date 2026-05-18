// section-extras-2.jsx — Profile edit, real-name auth, invite code, address mgmt

// ============================================================
// 个人设置 / 资料编辑
// ============================================================
const ScreenProfileEdit = () => (
  <Phone>
    <div style={{ background: '#fff' }}>
      <StatusBar theme="light"/>
      <NavBar title="个人资料" theme="on-light" right={<span style={{ fontSize: 13, color: 'var(--gold-500)' }}>保存</span>}/>
    </div>
    <div style={{ background: 'var(--bg-app)', overflow: 'auto', height: 'calc(100% - 88px)', padding: '0 0 24px' }}>
      {/* Avatar */}
      <div style={{ background: '#fff', padding: '20px 16px', display: 'flex', alignItems: 'center', borderBottom: '1px solid var(--line-soft)' }}>
        <div style={{ flex: 1 }}>
          <div style={{ fontSize: 13, color: 'var(--text-2)' }}>头像</div>
          <div style={{ fontSize: 11, color: 'var(--text-3)', marginTop: 4 }}>建议尺寸 200×200 · 不超过 2MB</div>
        </div>
        <div style={{ position: 'relative' }}>
          <Avatar size={64} kind="boss" seed="李"/>
          <div style={{
            position: 'absolute', bottom: 0, right: 0,
            width: 22, height: 22, borderRadius: '50%',
            background: 'var(--grad-gold)', display: 'flex', alignItems: 'center', justifyContent: 'center',
            border: '2px solid #fff',
          }}>
            <Icon name="edit" size={12} color="#3A1A00" strokeWidth={2.5}/>
          </div>
        </div>
      </div>

      {/* Basic */}
      <div style={{ background: '#fff' }}>
        <div className="form-row"><div className="label">昵称</div><input className="field" defaultValue="李明远"/><Icon name="chevron" size={14} color="#C9BFA9"/></div>
        <div className="form-row"><div className="label">性别</div><div className="field">先生</div><Icon name="chevron" size={14} color="#C9BFA9"/></div>
        <div className="form-row"><div className="label">生日</div><div className="field">1988-08-21</div><Icon name="chevron" size={14} color="#C9BFA9"/></div>
        <div className="form-row" style={{ borderBottom: 'none' }}><div className="label">地区</div><div className="field">北京市 朝阳区</div><Icon name="chevron" size={14} color="#C9BFA9"/></div>
      </div>

      {/* Account */}
      <div style={{ fontSize: 12, color: 'var(--text-3)', padding: '16px 16px 8px' }}>账户信息</div>
      <div style={{ background: '#fff' }}>
        <div className="form-row">
          <div className="label">手机号</div>
          <div className="field font-num">138 **** 8888</div>
          <span style={{ color: 'var(--gold-500)', fontSize: 12 }}>更换</span>
        </div>
        <div className="form-row">
          <div className="label">实名认证</div>
          <div className="field" style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
            <Icon name="check-circle" size={16} color="#2F9E5C"/>
            <span style={{ color: 'var(--text-1)' }}>李 ** 远</span>
          </div>
          <Icon name="chevron" size={14} color="#C9BFA9"/>
        </div>
        <div className="form-row">
          <div className="label">微信号</div>
          <div className="field text-3">已绑定</div>
          <Icon name="chevron" size={14} color="#C9BFA9"/>
        </div>
        <div className="form-row" style={{ borderBottom: 'none' }}>
          <div className="label">登录密码</div>
          <div className="field text-3">点击修改</div>
          <Icon name="chevron" size={14} color="#C9BFA9"/>
        </div>
      </div>

      {/* Identity */}
      <div style={{ fontSize: 12, color: 'var(--text-3)', padding: '16px 16px 8px' }}>身份信息</div>
      <div style={{ background: '#fff' }}>
        <div className="form-row">
          <div className="label">用户身份</div>
          <div className="field" style={{ display: 'flex', gap: 6 }}>
            <div className="tag tag-gold-solid">县级联营商</div>
            <div className="tag tag-gold">生态合伙人</div>
          </div>
        </div>
        <div className="form-row">
          <div className="label">邀请人</div>
          <div className="field">张敬山</div>
        </div>
        <div className="form-row" style={{ borderBottom: 'none' }}>
          <div className="label">入驻日期</div>
          <div className="field font-num">2025-12-08</div>
        </div>
      </div>

      <div style={{ padding: '16px' }}>
        <div style={{ fontSize: 12, color: 'var(--text-3)' }}>个性签名</div>
        <textarea rows="3" placeholder="写一段介绍自己的话…" defaultValue="甄选好酒 · 与好友共创财富，欢迎品鉴。" style={{
          width: '100%', resize: 'none', border: 'none', outline: 'none',
          marginTop: 10, padding: 12, background: '#fff', borderRadius: 10,
          fontSize: 13, fontFamily: 'inherit',
        }}/>
      </div>
    </div>
    <HomeIndicator/>
  </Phone>
);

// ============================================================
// 实名认证
// ============================================================
const ScreenRealName = () => (
  <Phone>
    <div style={{ background: 'var(--grad-brown)', color: '#F6E7C2', paddingBottom: 30 }}>
      <StatusBar theme="dark"/>
      <NavBar title="实名认证" theme="on-brown" right={<span style={{ fontSize: 13, color: '#FFD685' }}>帮助</span>}/>
      <div style={{ padding: '6px 24px 0', textAlign: 'center' }}>
        <div style={{
          width: 72, height: 72, borderRadius: '50%',
          background: 'var(--grad-gold)', margin: '0 auto',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          boxShadow: '0 8px 24px rgba(228,165,22,0.35)',
        }}>
          <Icon name="card" size={36} color="#3A1A00"/>
        </div>
        <div style={{ fontSize: 18, fontWeight: 700, color: '#FFE0A0', marginTop: 14 }}>请完成实名认证</div>
        <div style={{ fontSize: 12, color: 'rgba(246,231,194,0.7)', marginTop: 6 }}>用于身份核验 · 信息全程加密保护</div>
      </div>
    </div>

    <div style={{ background: 'var(--bg-app)', overflow: 'auto', height: 'calc(100% - 230px - 80px)', padding: '0 0 16px' }}>
      <div style={{ background: '#fff', padding: '8px 0', marginTop: -16, borderRadius: '16px 16px 0 0' }}>
        <div className="form-row">
          <div className="label">真实姓名</div>
          <input className="field" placeholder="请输入身份证上的姓名" defaultValue="李明远"/>
        </div>
        <div className="form-row">
          <div className="label">身份证号</div>
          <input className="field font-num" placeholder="请输入 18 位身份证号码" defaultValue="110105 ******** 2812"/>
        </div>
      </div>

      <div style={{ background: '#fff', marginTop: 10, padding: '14px 16px' }}>
        <div style={{ fontSize: 13, fontWeight: 600, marginBottom: 12 }}>身份证照片</div>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
          {[
            { l: '人像面', sub: '上传身份证人像面' },
            { l: '国徽面', sub: '上传身份证国徽面' },
          ].map((c, i) => (
            <div key={c.l} style={{
              aspectRatio: '5/3', borderRadius: 10,
              background: 'linear-gradient(135deg, #FBF2DC, #F6E5BC)',
              border: '1.5px dashed var(--gold-300)',
              display: 'flex', flexDirection: 'column',
              alignItems: 'center', justifyContent: 'center',
              gap: 6, position: 'relative',
            }}>
              <Icon name={i === 0 ? 'user' : 'medal'} size={28} color="#D38A00"/>
              <div style={{ fontSize: 12, fontWeight: 600, color: '#7A5610' }}>{c.l}</div>
              <div style={{ fontSize: 10, color: '#A0846A' }}>{c.sub}</div>
            </div>
          ))}
        </div>
      </div>

      <div style={{ margin: '12px 16px 0', padding: '12px 14px', background: '#E8F4FC', borderRadius: 10, fontSize: 12, color: '#1F4B9A', lineHeight: 1.7 }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 6, fontWeight: 600, marginBottom: 4 }}>
          <Icon name="info" size={16} color="#3A6FD1"/>认证须知
        </div>
        · 信息仅用于身份核验，平台严格保密<br/>
        · 一个身份证仅可绑定一个账户，请确认信息无误<br/>
        · 认证通过后联营商提现、佣金结算才会正常生效
      </div>

      <div style={{ margin: '14px 16px 0', display: 'flex', alignItems: 'center', gap: 8, fontSize: 12, color: 'var(--text-2)' }}>
        <div style={{ width: 16, height: 16, borderRadius: 4, background: 'var(--grad-gold)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <Icon name="check" size={12} color="#fff" strokeWidth={3}/>
        </div>
        我已阅读并同意 <span style={{ color: 'var(--gold-500)' }}>《实名认证服务协议》</span>
      </div>
    </div>

    <div style={{ position: 'absolute', left: 16, right: 16, bottom: 30 }}>
      <button className="btn-gold" style={{ width: '100%' }}>提交认证</button>
    </div>
    <HomeIndicator/>
  </Phone>
);

// ============================================================
// 实名强制弹窗
// ============================================================
const ScreenRealNameForced = () => (
  <Phone>
    {/* faint profile behind */}
    <div style={{ background: 'var(--grad-brown)', height: 280 }}>
      <StatusBar theme="dark"/>
    </div>
    <div style={{ position: 'absolute', inset: 0, background: 'rgba(0,0,0,0.65)' }}/>

    <div className="modal-mask">
      <div style={{ width: 320, background: '#fff', borderRadius: 16, padding: '28px 22px 0', textAlign: 'center' }}>
        <div style={{
          width: 72, height: 72, borderRadius: '50%',
          background: 'var(--gold-50)',
          margin: '0 auto 14px',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
        }}>
          <Icon name="warn" size={36} color="#D38A00"/>
        </div>
        <div style={{ fontSize: 18, fontWeight: 700 }}>请先完成实名认证</div>
        <div style={{ fontSize: 13, color: 'var(--text-2)', marginTop: 12, lineHeight: 1.7 }}>
          根据国家法规要求，参与<span style={{ color: 'var(--gold-500)', fontWeight: 600 }}>提现、分销、邀请奖励</span>等需先完成实名认证
        </div>

        <div style={{ background: 'var(--cream-50)', borderRadius: 10, padding: '12px 14px', marginTop: 16, textAlign: 'left' }}>
          <div style={{ fontSize: 11, color: 'var(--text-3)', marginBottom: 6 }}>认证后即可：</div>
          {[
            '✓ 享有联营商身份与佣金分润',
            '✓ 解锁余额提现至微信钱包',
            '✓ 体验店申请与团队管理权限',
          ].map(l => (
            <div key={l} style={{ fontSize: 12, color: 'var(--text-1)', padding: '3px 0', lineHeight: 1.6 }}>{l}</div>
          ))}
        </div>

        <button className="btn-gold" style={{ width: '100%', marginTop: 18 }}>
          <Icon name="card" size={18} color="#3A1A00" style={{ marginRight: 6 }}/>立即去认证
        </button>
        <div style={{ padding: '16px 0 18px', color: 'var(--text-3)', fontSize: 13 }}>暂不认证 · 仅浏览</div>
      </div>
    </div>
    <HomeIndicator dark/>
  </Phone>
);

// ============================================================
// 分享邀请码 (简版独立页)
// ============================================================
const ScreenInviteCode = () => (
  <Phone>
    <div style={{ background: 'linear-gradient(180deg, #2A0E00 0%, #4A1E08 50%, #6B2D1A 100%)', color: '#F6E7C2', minHeight: '100%', position: 'relative', overflow: 'hidden' }}>
      <div style={{ position: 'absolute', top: '20%', left: '-20%', width: '60%', height: '40%', borderRadius: '50%', background: 'radial-gradient(circle, rgba(228,165,22,0.25), transparent 60%)' }}/>
      <div style={{ position: 'absolute', top: '50%', right: '-30%', width: '70%', height: '50%', borderRadius: '50%', background: 'radial-gradient(circle, rgba(228,165,22,0.18), transparent 60%)' }}/>

      <StatusBar theme="dark"/>
      <div style={{ position: 'relative', zIndex: 1 }}>
        <NavBar title="我的邀请码" theme="on-brown" right={<Icon name="bell" size={20} color="#FFD685"/>}/>

        {/* Brand */}
        <div style={{ padding: '20px 24px 0', textAlign: 'center' }}>
          <BrandMark size={64}/>
          <div style={{ fontSize: 11, letterSpacing: 4, color: 'rgba(246,231,194,0.55)', marginTop: 18 }}>EXCLUSIVE INVITE CODE</div>
          <div style={{ fontSize: 22, fontWeight: 700, color: '#FFE0A0', marginTop: 6 }}>李明远 邀请你加入醇品汇</div>
          <div style={{ fontSize: 12, color: 'rgba(246,231,194,0.65)', marginTop: 6, lineHeight: 1.6 }}>
            首单消费 · 即得 <span style={{ color: '#FFD646', fontWeight: 700 }}>20% 推广佣金</span>
          </div>
        </div>

        {/* Big code card */}
        <div style={{ margin: '26px 30px 0', background: '#fff', borderRadius: 16, padding: '22px 18px', boxShadow: '0 16px 50px rgba(0,0,0,0.45)' }}>
          <div style={{ fontSize: 11, color: 'var(--text-3)', textAlign: 'center', letterSpacing: 3 }}>专属邀请码</div>
          <div className="font-num" style={{
            fontSize: 44, fontWeight: 800, color: 'var(--text-1)',
            letterSpacing: 10, textAlign: 'center', marginTop: 6,
            background: 'linear-gradient(135deg, #2A0E00, #B07000 100%)',
            WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent',
          }}>CPH8821</div>

          <div style={{ display: 'flex', gap: 10, marginTop: 18 }}>
            <button style={{ flex: 1, height: 38, borderRadius: 999, background: 'var(--cream-100)', color: 'var(--gold-600)', border: 'none', fontSize: 13, fontWeight: 600 }}>
              <Icon name="edit" size={14} color="#B07000" style={{ verticalAlign: 'middle', marginRight: 4 }}/>复制
            </button>
            <button style={{ flex: 1, height: 38, borderRadius: 999, background: 'var(--grad-gold)', color: '#3A1A00', border: 'none', fontSize: 13, fontWeight: 700 }}>
              <Icon name="qr" size={14} color="#3A1A00" style={{ verticalAlign: 'middle', marginRight: 4 }}/>生成海报
            </button>
          </div>
        </div>

        {/* Reward summary */}
        <div style={{ margin: '16px 16px 0', display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 8 }}>
          {[
            { l: '已邀请', v: '23', s: '人' },
            { l: '本月新增', v: '4', s: '人' },
            { l: '累计佣金', v: '18,624', s: '元' },
          ].map(s => (
            <div key={s.l} style={{
              background: 'rgba(255,255,255,0.06)', borderRadius: 10,
              padding: '12px 0', textAlign: 'center',
              border: '1px solid rgba(228,165,22,0.18)',
            }}>
              <div style={{ fontSize: 11, color: 'rgba(246,231,194,0.65)' }}>{s.l}</div>
              <div className="font-num" style={{ fontSize: 20, fontWeight: 700, color: '#FFE0A0', marginTop: 4 }}>{s.v}<span style={{ fontSize: 11, marginLeft: 2, fontWeight: 400 }}>{s.s}</span></div>
            </div>
          ))}
        </div>

        {/* Share methods */}
        <div style={{ margin: '20px 16px 0' }}>
          <div style={{ fontSize: 13, fontWeight: 700, color: '#FFE0A0', marginBottom: 12 }}>选择邀请方式</div>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 8 }}>
            {[
              { l: '微信好友', i: 'wechat', c: '#12B95B' },
              { l: '朋友圈',  i: 'people', c: '#0FA34F' },
              { l: '邀请海报', i: 'qr',     c: '#D38A00' },
              { l: '复制链接', i: 'edit',   c: '#3A6FD1' },
            ].map(m => (
              <div key={m.l} style={{
                background: 'rgba(255,255,255,0.06)', borderRadius: 10,
                padding: '14px 0', textAlign: 'center',
                border: '1px solid rgba(228,165,22,0.18)',
              }}>
                <div style={{ width: 44, height: 44, margin: '0 auto', borderRadius: '50%', background: m.c, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                  <Icon name={m.i} size={22} color="#fff"/>
                </div>
                <div style={{ fontSize: 11, marginTop: 8, color: 'rgba(246,231,194,0.85)' }}>{m.l}</div>
              </div>
            ))}
          </div>
        </div>

        <div style={{ padding: '20px 16px 26px', textAlign: 'center', fontSize: 11, color: 'rgba(246,231,194,0.45)' }}>
          已邀请 23 位好友 · <span style={{ color: '#FFD685' }}>查看邀请记录 ›</span>
        </div>
      </div>
      <HomeIndicator dark/>
    </div>
  </Phone>
);

// ============================================================
// 地址管理 (完整管理版本)
// ============================================================
const ScreenAddressManage = () => (
  <Phone>
    <div style={{ background: '#fff' }}>
      <StatusBar theme="light"/>
      <NavBar title="地址管理" theme="on-light" right={<span style={{ fontSize: 13, color: 'var(--gold-500)' }}>完成</span>}/>
    </div>

    <div style={{ background: 'var(--bg-app)', padding: '12px 16px', overflow: 'auto', height: 'calc(100% - 88px - 80px)' }}>
      {/* Default */}
      <div style={{ fontSize: 12, color: 'var(--text-3)', padding: '4px 0 8px' }}>默认地址</div>
      <div style={{ background: '#fff', borderRadius: 12, padding: 14, marginBottom: 14, border: '1.5px solid var(--gold-300)' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
          <div style={{ fontSize: 15, fontWeight: 600 }}>李明远</div>
          <div className="font-num" style={{ fontSize: 13, color: 'var(--text-2)' }}>138 **** 8888</div>
          <div className="tag tag-gold-solid">默认</div>
          <div className="tag tag-gold" style={{ marginLeft: 'auto' }}>家</div>
        </div>
        <div style={{ fontSize: 13, color: 'var(--text-2)', marginTop: 8, lineHeight: 1.5 }}>
          北京市朝阳区建国门外大街1号国贸大厦A座 28层2801室
        </div>
        <div style={{ display: 'flex', gap: 8, marginTop: 12 }}>
          <button style={{ flex: 1, height: 32, borderRadius: 6, background: 'var(--cream-50)', color: 'var(--gold-600)', border: '1px solid var(--gold-300)', fontSize: 12, fontWeight: 600 }}>
            <Icon name="edit" size={14} color="#B07000" style={{ verticalAlign: 'middle', marginRight: 4 }}/>编辑
          </button>
          <button style={{ flex: 1, height: 32, borderRadius: 6, background: '#fff', color: 'var(--text-2)', border: '1px solid var(--line)', fontSize: 12 }}>
            <Icon name="pin" size={14} color="#5E5142" style={{ verticalAlign: 'middle', marginRight: 4 }}/>导航
          </button>
        </div>
      </div>

      {/* Other addresses */}
      <div style={{ fontSize: 12, color: 'var(--text-3)', padding: '0 0 8px' }}>其他地址</div>
      {[
        { n: '李明远', p: '138 **** 8888', a: '北京市朝阳区望京SOHO T3 A座 1208室', tag: '公司' },
        { n: '李太太', p: '139 **** 6666', a: '北京市朝阳区三里屯太古里南区 5号院 12-3-501', tag: '家人' },
        { n: '李父',   p: '136 **** 7777', a: '上海市浦东新区陆家嘴金融中心 32层 3208室', tag: '父母' },
      ].map((addr, i) => (
        <div key={i} style={{ background: '#fff', borderRadius: 12, padding: 14, marginBottom: 10 }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
            <div style={{ fontSize: 15, fontWeight: 600 }}>{addr.n}</div>
            <div className="font-num" style={{ fontSize: 13, color: 'var(--text-2)' }}>{addr.p}</div>
            <div className="tag tag-gold" style={{ marginLeft: 'auto' }}>{addr.tag}</div>
          </div>
          <div style={{ fontSize: 13, color: 'var(--text-2)', marginTop: 8, lineHeight: 1.5 }}>{addr.a}</div>
          <div style={{ display: 'flex', alignItems: 'center', marginTop: 10, paddingTop: 10, borderTop: '1px solid var(--line-soft)', fontSize: 12 }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 4, color: 'var(--text-2)' }}>
              <div style={{ width: 14, height: 14, borderRadius: '50%', border: '1.5px solid var(--line)' }}/>
              设为默认
            </div>
            <button style={{ marginLeft: 'auto', height: 26, padding: '0 12px', borderRadius: 999, background: '#fff', color: 'var(--gold-600)', border: '1px solid var(--line)', fontSize: 12 }}>
              <Icon name="edit" size={12} color="#B07000" style={{ verticalAlign: 'middle', marginRight: 3 }}/>编辑
            </button>
            <button style={{ marginLeft: 8, height: 26, padding: '0 12px', borderRadius: 999, background: '#fff', color: 'var(--red-500)', border: '1px solid var(--line)', fontSize: 12 }}>
              <Icon name="trash" size={12} color="#D6453B" style={{ verticalAlign: 'middle', marginRight: 3 }}/>删除
            </button>
          </div>
        </div>
      ))}

      <div style={{ textAlign: 'center', padding: '12px 0', fontSize: 11, color: 'var(--text-3)' }}>
        共 4 个地址 · 最多可保存 20 个
      </div>
    </div>

    <div style={{ position: 'absolute', left: 16, right: 16, bottom: 30 }}>
      <button className="btn-gold" style={{ width: '100%' }}>
        <Icon name="plus" size={18} color="#3A1A00" style={{ marginRight: 6 }}/>新增收货地址
      </button>
    </div>
    <HomeIndicator/>
  </Phone>
);

Object.assign(window, {
  ScreenProfileEdit, ScreenRealName, ScreenRealNameForced,
  ScreenInviteCode, ScreenAddressManage,
});
