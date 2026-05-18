// section-a.jsx — Account & Login screens
// 1. 登录页 / 2. 微信授权手机号 / 3. 邀请码 / 4. 协议 / 5. 游客限制

// ============================================================
// A1. 登录页 (Login)
// ============================================================
const ScreenLogin = () => (
  <Phone bg="var(--bg-app)" dark>
    {/* Brown gradient top */}
    <div style={{
      position: 'absolute', top: 0, left: 0, right: 0, height: 410,
      background: 'var(--grad-brown)',
      backgroundImage: 'radial-gradient(ellipse 80% 60% at 50% 30%, rgba(228,165,22,0.18), transparent 60%), var(--grad-brown)',
    }}/>
    <StatusBar theme="dark"/>
    <div style={{ position: 'relative', textAlign: 'center', paddingTop: 60, color: '#F6E7C2' }}>
      <BrandMark size={92}/>
      <div style={{ marginTop: -92, paddingTop: 110 }}>
        <div style={{ fontSize: 28, fontWeight: 700, letterSpacing: 8, color: '#FFD685' }}>醇 品 汇</div>
        <div style={{ fontSize: 13, color: 'rgba(246,231,194,0.7)', letterSpacing: 1.5, marginTop: 8 }}>甄选美酒 · 共创财富</div>
      </div>
    </div>
    {/* White login panel */}
    <div style={{
      position: 'absolute', left: 0, right: 0, bottom: 0, height: 440,
      background: '#fff',
      borderRadius: '24px 24px 0 0',
      padding: '32px 24px 28px',
    }}>
      <div style={{ textAlign: 'center', fontSize: 22, fontWeight: 700 }}>欢迎登录</div>
      <div style={{ textAlign: 'center', fontSize: 13, color: 'var(--text-3)', marginTop: 6 }}>登录后享受完整购物与分销服务</div>

      <button className="btn-wechat" style={{ width: '100%', marginTop: 28 }}>
        <Icon name="wechat" size={22}/> 微信快捷登录
      </button>
      <button className="btn-gold-outline" style={{ width: '100%', marginTop: 14, background: 'var(--cream-100)', borderColor: '#F0E0B7' }}>
        游客浏览
      </button>

      <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginTop: 22, fontSize: 12, color: 'var(--text-2)' }}>
        <div style={{
          width: 16, height: 16, borderRadius: 4,
          background: 'var(--grad-gold)',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
        }}>
          <Icon name="check" size={12} color="#fff" strokeWidth={2.5}/>
        </div>
        已阅读并同意 <span className="text-gold">《用户协议》</span><span className="text-gold">《隐私协议》</span>
      </div>

      <div style={{
        marginTop: 18, background: 'var(--gold-50)',
        border: '1px solid #F2E0B0',
        borderRadius: 10, padding: '10px 12px',
        display: 'flex', alignItems: 'center', gap: 8,
        fontSize: 12, color: '#7A5610',
      }}>
        <Icon name="info" size={16} color="#D38A00"/>
        请通过好友分享码进入小程序完成注册
      </div>
    </div>
    <HomeIndicator/>
  </Phone>
);

// ============================================================
// A2. 微信授权手机号弹窗
// ============================================================
const ScreenWxAuth = () => (
  <Phone dark>
    {/* Dim background — show login page */}
    <div style={{ position: 'absolute', inset: 0, background: 'var(--grad-brown)', opacity: 1 }}/>
    <div style={{ position: 'absolute', inset: 0, background: 'rgba(0,0,0,0.55)' }}/>
    <StatusBar theme="dark"/>
    {/* dim faint preview */}
    <div style={{ position: 'absolute', inset: 0, opacity: 0.18, pointerEvents: 'none' }}>
      <div style={{ textAlign: 'center', paddingTop: 100 }}>
        <BrandMark size={80}/>
      </div>
    </div>

    {/* Bottom sheet */}
    <div style={{
      position: 'absolute', left: 0, right: 0, bottom: 0,
      background: '#fff', borderRadius: '20px 20px 0 0',
      padding: '20px 20px 32px',
    }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 12 }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
          <div style={{ width: 36, height: 36, borderRadius: 8, background: 'var(--grad-gold)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <svg viewBox="0 0 24 24" width="22" height="22" fill="#fff"><path d="M7 3 H17 L16 11 a4 4 0 0 1 -8 0 Z M12 15 V21 M9 21 H15" stroke="#fff" strokeWidth="1.6" fill="none" strokeLinecap="round"/></svg>
          </div>
          <div>
            <div style={{ fontSize: 13, fontWeight: 600 }}>醇品汇</div>
            <div style={{ fontSize: 11, color: 'var(--text-3)' }}>申请获取你的手机号</div>
          </div>
        </div>
        <Icon name="close" size={20} color="#999"/>
      </div>

      <div style={{ background: 'var(--bg-app)', borderRadius: 10, padding: '14px 14px', marginTop: 8 }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <div style={{ fontSize: 13, fontWeight: 600 }}>使用本机号码</div>
          <div style={{ width: 18, height: 18, borderRadius: '50%', background: 'var(--grad-gold)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <Icon name="check" size={12} color="#fff" strokeWidth={3}/>
          </div>
        </div>
        <div className="font-num" style={{ fontSize: 18, fontWeight: 700, marginTop: 6 }}>138 **** 8888</div>
      </div>

      <div style={{ background: 'var(--bg-app)', borderRadius: 10, padding: '14px 14px', marginTop: 10, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <div style={{ fontSize: 14 }}>使用其他号码</div>
        <Icon name="chevron" size={16} color="#999"/>
      </div>

      <button className="btn-gold" style={{ width: '100%', marginTop: 22 }}>允许</button>
      <div style={{ textAlign: 'center', marginTop: 10, color: 'var(--text-3)', fontSize: 13 }}>拒绝</div>
    </div>
    <HomeIndicator dark/>
  </Phone>
);

// ============================================================
// A3. 邀请码填写
// ============================================================
const ScreenInvite = () => (
  <Phone>
    <div style={{ background: 'var(--grad-brown)', color: '#F6E7C2', paddingBottom: 36 }}>
      <StatusBar theme="dark"/>
      <NavBar title="完善邀请信息" theme="on-brown"/>
      <div style={{ textAlign: 'center', padding: '12px 24px 32px' }}>
        <BrandMark size={64}/>
        <div style={{ fontSize: 18, fontWeight: 700, marginTop: 14, color: '#FFE0A0' }}>欢迎加入醇品汇</div>
        <div style={{ fontSize: 12, color: 'rgba(246,231,194,0.6)', marginTop: 4 }}>请填写好友邀请码完成注册</div>
      </div>
    </div>
    <div style={{ background: '#fff', borderRadius: '20px 20px 0 0', marginTop: -16, padding: '24px 20px', minHeight: 480 }}>
      <div style={{ fontSize: 13, color: 'var(--text-2)' }}>邀请码</div>
      <div style={{
        marginTop: 10, height: 56, borderRadius: 12,
        background: 'var(--cream-100)',
        border: '1.5px solid var(--gold-300)',
        display: 'flex', alignItems: 'center',
        padding: '0 16px', fontSize: 22, fontWeight: 700,
        letterSpacing: 8, color: 'var(--gold-500)',
        fontFamily: '"SF Pro Display", monospace',
      }}>
        CPH8821
      </div>

      <div style={{ marginTop: 18, fontSize: 13, color: 'var(--text-2)' }}>邀请人信息</div>
      <div style={{
        marginTop: 10, padding: '14px',
        background: 'var(--cream-50)', borderRadius: 12,
        border: '1px solid var(--line-soft)',
        display: 'flex', alignItems: 'center', gap: 12,
      }}>
        <Avatar size={44} kind="boss"/>
        <div style={{ flex: 1 }}>
          <div style={{ fontSize: 15, fontWeight: 600 }}>张敬山</div>
          <div style={{ fontSize: 12, color: 'var(--text-3)' }}>市级联营商</div>
        </div>
        <div className="tag tag-gold-solid">已绑定</div>
      </div>

      <div style={{
        marginTop: 18, padding: '12px 14px',
        background: 'rgba(255, 246, 218, 0.6)',
        border: '1px dashed var(--gold-300)',
        borderRadius: 10,
        fontSize: 12, color: '#7A5610', lineHeight: 1.6,
      }}>
        ⓘ 邀请人绑定后将不可更改。绑定后您和邀请人将形成长期分销关系，邀请人可获得对应推广佣金。
      </div>

      <button className="btn-gold" style={{ width: '100%', marginTop: 30 }}>确认绑定并完成注册</button>
      <div style={{ textAlign: 'center', marginTop: 14, fontSize: 13, color: 'var(--text-3)' }}>暂时跳过</div>
    </div>
    <HomeIndicator/>
  </Phone>
);

// ============================================================
// A4. 协议页
// ============================================================
const ScreenAgreement = () => (
  <Phone>
    <div style={{ background: '#fff' }}>
      <StatusBar theme="light"/>
      <NavBar title="用户服务协议" theme="on-light"/>
    </div>
    <div style={{ padding: '12px 20px 40px', background: '#fff', height: 'calc(100% - 88px)', overflow: 'hidden' }}>
      <div style={{ fontSize: 22, fontWeight: 700, marginBottom: 6 }}>醇品汇用户服务协议</div>
      <div style={{ fontSize: 12, color: 'var(--text-3)' }}>更新于 2026 年 4 月 15 日</div>

      <div style={{ fontSize: 13, lineHeight: 1.8, color: 'var(--text-2)', marginTop: 18 }}>
        <p style={{ fontWeight: 600, color: 'var(--text-1)' }}>一、服务条款的接受</p>
        <p>欢迎您使用醇品汇微信小程序所提供的酒类商品在线选购、分销、邀请等服务。在使用本平台服务前，请您仔细阅读并充分理解本协议各项内容，特别是免除或限制责任的条款。</p>

        <p style={{ fontWeight: 600, color: 'var(--text-1)', marginTop: 14 }}>二、账户注册与使用</p>
        <p>1. 您通过微信授权注册账户，并通过好友邀请码完成绑定后即可使用完整功能。<br/>
        2. 您应当对所提供的注册资料的真实性、合法性、有效性承担相应法律责任。<br/>
        3. 您应当妥善保管账户信息，不得将账户出借、转让给他人。</p>

        <p style={{ fontWeight: 600, color: 'var(--text-1)', marginTop: 14 }}>三、商品与订单</p>
        <p>1. 平台所展示商品包括白酒、红酒、黄酒等酒类商品；<br/>
        2. 商品价格、库存、规格以下单时页面信息为准；<br/>
        3. 您下单即视为发出要约，平台确认订单并发货后合同生效。</p>

        <p style={{ fontWeight: 600, color: 'var(--text-1)', marginTop: 14 }}>四、分销与佣金</p>
        <p>1. 联营商身份需满足相应入门金额并通过审核；<br/>
        2. 佣金、奖励将根据等级权益按规则结算至账户余额；<br/>
        3. 您不得通过虚假交易、刷单等方式套取佣金。</p>

        <p style={{ fontWeight: 600, color: 'var(--text-1)', marginTop: 14 }}>五、知识产权</p>
        <p>本平台所展示的所有内容包括但不限于文字、图片、品牌标识，其所有权归醇品汇平台或权利人所有。</p>
      </div>
    </div>
    <HomeIndicator/>
  </Phone>
);

// ============================================================
// A5. 游客限制弹窗
// ============================================================
const ScreenGuestLimit = () => (
  <Phone>
    {/* Faint home in background */}
    <div style={{ position: 'absolute', inset: 0, background: 'var(--bg-app)' }}/>
    <div style={{ position: 'absolute', top: 0, left: 0, right: 0, height: 280, background: 'var(--grad-brown)', opacity: 1 }}/>
    <StatusBar theme="dark"/>
    <div style={{ padding: '0 16px', position: 'relative', zIndex: 1, color: '#F6E7C2' }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '8px 0 14px' }}>
        <div style={{ fontSize: 22, fontWeight: 700, color: '#FFE0A0' }}>醇品汇</div>
        <div style={{ display: 'flex', gap: 10 }}>
          <Icon name="cart" size={22} color="#FFD685"/>
          <Icon name="bell" size={22} color="#FFD685"/>
        </div>
      </div>
    </div>

    {/* Mask */}
    <div className="modal-mask">
      <div className="modal" style={{ padding: '28px 20px 0' }}>
        <div style={{
          width: 64, height: 64, borderRadius: '50%',
          background: 'var(--gold-50)',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          margin: '0 auto 14px',
        }}>
          <Icon name="warn" size={32} color="#D38A00"/>
        </div>
        <div style={{ fontSize: 17, fontWeight: 700 }}>请先登录</div>
        <div style={{ fontSize: 13, color: 'var(--text-2)', marginTop: 8, lineHeight: 1.6 }}>
          购买、加入购物车、分享获佣等功能需要登录后才可使用，登录后享受完整购物与分销服务
        </div>
        <button className="btn-wechat" style={{ width: '100%', marginTop: 18, marginBottom: 4 }}>
          <Icon name="wechat" size={20}/> 微信一键登录
        </button>
        <div style={{ padding: '14px 0 18px', color: 'var(--text-3)', fontSize: 14 }}>继续游客浏览</div>
      </div>
    </div>
    <HomeIndicator dark/>
  </Phone>
);

Object.assign(window, { ScreenLogin, ScreenWxAuth, ScreenInvite, ScreenAgreement, ScreenGuestLimit });
