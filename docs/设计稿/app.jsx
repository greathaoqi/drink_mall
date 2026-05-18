// app.jsx — Wires all screens into the design canvas
const { createRoot } = ReactDOM;

const PHONE_W = 390;
const PHONE_H = 844;


const App = () => (
  <DesignCanvas>
    <DCSection id="intro" title="醇品汇 · 微信小程序" subtitle="高保真设计稿 · 共 60+ 屏，覆盖游客→分销全链路">
      <DCArtboard id="cover" label="封面 · 设计说明" width={520} height={PHONE_H}>
        <CoverCard/>
      </DCArtboard>
    </DCSection>

    <DCSection id="A" title="A · 账户与登录" subtitle="进入流程：游客 / 微信授权 / 邀请码绑定">
      <DCArtboard id="a1" label="A1 · 登录页" width={PHONE_W} height={PHONE_H}><ScreenLogin/></DCArtboard>
      <DCArtboard id="a2" label="A2 · 微信授权手机号" width={PHONE_W} height={PHONE_H}><ScreenWxAuth/></DCArtboard>
      <DCArtboard id="a3" label="A3 · 邀请码绑定" width={PHONE_W} height={PHONE_H}><ScreenInvite/></DCArtboard>
      <DCArtboard id="a4" label="A4 · 用户协议" width={PHONE_W} height={PHONE_H}><ScreenAgreement/></DCArtboard>
      <DCArtboard id="a5" label="A5 · 游客限制弹窗" width={PHONE_W} height={PHONE_H}><ScreenGuestLimit/></DCArtboard>
    </DCSection>

    <DCSection id="B" title="B · 首页与商品浏览" subtitle="首页 · 搜索 · 四大专区 · 分类 · 筛选 · 空状态">
      <DCArtboard id="b1" label="B1 · 首页" width={PHONE_W} height={PHONE_H}><ScreenHome/></DCArtboard>
      <DCArtboard id="b2" label="B2 · 搜索页" width={PHONE_W} height={PHONE_H}><ScreenSearch/></DCArtboard>
      <DCArtboard id="b3" label="B3 · 搜索结果" width={PHONE_W} height={PHONE_H}><ScreenSearchResult/></DCArtboard>
      <DCArtboard id="b4" label="B4 · 商品分类" width={PHONE_W} height={PHONE_H}><ScreenCategory/></DCArtboard>
      <DCArtboard id="b5" label="B5 · 主产品专区" width={PHONE_W} height={PHONE_H}><ScreenMainArea/></DCArtboard>
      <DCArtboard id="b6" label="B6 · 招商专区" width={PHONE_W} height={PHONE_H}><ScreenInvestArea/></DCArtboard>
      <DCArtboard id="b7" label="B7 · 零售专区" width={PHONE_W} height={PHONE_H}><ScreenRetailArea/></DCArtboard>
      <DCArtboard id="b8" label="B8 · 礼包专区" width={PHONE_W} height={PHONE_H}><ScreenGiftArea/></DCArtboard>
      <DCArtboard id="b9" label="B9 · 筛选/排序" width={PHONE_W} height={PHONE_H}><ScreenFilter/></DCArtboard>
      <DCArtboard id="b10" label="B10 · 商品空状态" width={PHONE_W} height={PHONE_H}><ScreenEmptyList/></DCArtboard>
    </DCSection>

    <DCSection id="C" title="C · 商品详情与购买" subtitle="详情 · 规格 · 分享 · 海报 · 购物车 · 下单 · 支付">
      <DCArtboard id="c1" label="C1 · 商品详情" width={PHONE_W} height={PHONE_H}><ScreenProductDetail/></DCArtboard>
      <DCArtboard id="c2" label="C2 · 规格选择弹窗" width={PHONE_W} height={PHONE_H}><ScreenSku/></DCArtboard>
      <DCArtboard id="c3" label="C3 · 分享商品弹窗" width={PHONE_W} height={PHONE_H}><ScreenShare/></DCArtboard>
      <DCArtboard id="c4" label="C4 · 商品海报" width={PHONE_W} height={PHONE_H}><ScreenPoster/></DCArtboard>
      <DCArtboard id="c5" label="C5 · 购物车" width={PHONE_W} height={PHONE_H}><ScreenCart/></DCArtboard>
      <DCArtboard id="c6" label="C6 · 确认订单" width={PHONE_W} height={PHONE_H}><ScreenCheckout/></DCArtboard>
      <DCArtboard id="c7" label="C7 · 收货地址列表" width={PHONE_W} height={PHONE_H}><ScreenAddressList/></DCArtboard>
      <DCArtboard id="c8" label="C8 · 编辑地址" width={PHONE_W} height={PHONE_H}><ScreenAddressEdit/></DCArtboard>
      <DCArtboard id="c9" label="C9 · 支付方式" width={PHONE_W} height={PHONE_H}><ScreenPayMethod/></DCArtboard>
      <DCArtboard id="c10" label="C10 · 支付成功" width={PHONE_W} height={PHONE_H}><ScreenPaySuccess/></DCArtboard>
      <DCArtboard id="c11" label="C11 · 支付失败" width={PHONE_W} height={PHONE_H}><ScreenPayFail/></DCArtboard>
    </DCSection>

    <DCSection id="D" title="D · 订单" subtitle="订单列表 · 详情 · 物流 · 售后">
      <DCArtboard id="d1" label="D1 · 我的订单" width={PHONE_W} height={PHONE_H}><ScreenOrders/></DCArtboard>
      <DCArtboard id="d2" label="D2 · 订单详情" width={PHONE_W} height={PHONE_H}><ScreenOrderDetail/></DCArtboard>
      <DCArtboard id="d3" label="D3 · 物流详情" width={PHONE_W} height={PHONE_H}><ScreenLogistics/></DCArtboard>
      <DCArtboard id="d4" label="D4 · 申请售后" width={PHONE_W} height={PHONE_H}><ScreenAfterSale/></DCArtboard>
      <DCArtboard id="d5" label="D5 · 售后详情" width={PHONE_W} height={PHONE_H}><ScreenAfterSaleDetail/></DCArtboard>
    </DCSection>

    <DCSection id="E" title="E · 动态" subtitle="公告 · 酒类知识 · 活动内容">
      <DCArtboard id="e1" label="E1 · 动态首页" width={PHONE_W} height={PHONE_H}><ScreenDynamic/></DCArtboard>
      <DCArtboard id="e2" label="E2 · 公告列表" width={PHONE_W} height={PHONE_H}><ScreenNoticeList/></DCArtboard>
      <DCArtboard id="e3" label="E3 · 公告详情" width={PHONE_W} height={PHONE_H}><ScreenNoticeDetail/></DCArtboard>
      <DCArtboard id="e4" label="E4 · 知识/活动列表" width={PHONE_W} height={PHONE_H}><ScreenKnowledgeList/></DCArtboard>
      <DCArtboard id="e5" label="E5 · 内容详情" width={PHONE_W} height={PHONE_H}><ScreenArticle/></DCArtboard>
    </DCSection>

    <DCSection id="F" title="F · 分销 / 联营商" subtitle="工作台 · 等级 · 奖励 · 提现 · 团队 · 邀请 · 体验店">
      <DCArtboard id="f1" label="F1 · 分销工作台" width={PHONE_W} height={PHONE_H}><ScreenDist/></DCArtboard>
      <DCArtboard id="f2" label="F2 · 联营商等级" width={PHONE_W} height={PHONE_H}><ScreenLevelLianYing/></DCArtboard>
      <DCArtboard id="f3" label="F3 · 合伙人等级" width={PHONE_W} height={PHONE_H}><ScreenLevelPartner/></DCArtboard>
      <DCArtboard id="f4" label="F4 · 奖励明细" width={PHONE_W} height={PHONE_H}><ScreenRewardDetail/></DCArtboard>
      <DCArtboard id="f5" label="F5 · 佣金明细" width={PHONE_W} height={PHONE_H}><ScreenCommissionDetail/></DCArtboard>
      <DCArtboard id="f6" label="F6 · 提现" width={PHONE_W} height={PHONE_H}><ScreenWithdraw/></DCArtboard>
      <DCArtboard id="f7" label="F7 · 提现记录" width={PHONE_W} height={PHONE_H}><ScreenWithdrawHistory/></DCArtboard>
      <DCArtboard id="f8" label="F8 · 团队管理" width={PHONE_W} height={PHONE_H}><ScreenTeam/></DCArtboard>
      <DCArtboard id="f9" label="F9 · 团队成员详情" width={PHONE_W} height={PHONE_H}><ScreenTeamMember/></DCArtboard>
      <DCArtboard id="f10" label="F10 · 分享邀请" width={PHONE_W} height={PHONE_H}><ScreenInviteShare/></DCArtboard>
      <DCArtboard id="f11" label="F11 · 邀请海报" width={PHONE_W} height={PHONE_H}><ScreenInvitePoster/></DCArtboard>
      <DCArtboard id="f12" label="F12 · 体验店申请" width={PHONE_W} height={PHONE_H}><ScreenStoreApply/></DCArtboard>
      <DCArtboard id="f13" label="F13 · 体验店状态" width={PHONE_W} height={PHONE_H}><ScreenStoreStatus/></DCArtboard>
    </DCSection>

    <DCSection id="G" title="G · 我的" subtitle="个人中心 · 资产 · 余额 · 酒豆 · 积分 · 优惠券 · DF 赠送 · 设置">
      <DCArtboard id="g1" label="G1 · 个人中心" width={PHONE_W} height={PHONE_H}><ScreenProfile/></DCArtboard>
      <DCArtboard id="g2" label="G2 · 我的资产" width={PHONE_W} height={PHONE_H}><ScreenAssets/></DCArtboard>
      <DCArtboard id="g3" label="G3 · 余额明细" width={PHONE_W} height={PHONE_H}><ScreenBalanceLog/></DCArtboard>
      <DCArtboard id="g4" label="G4 · 积分明细" width={PHONE_W} height={PHONE_H}><ScreenPointsLog/></DCArtboard>
      <DCArtboard id="g5" label="G5 · 我的优惠券" width={PHONE_W} height={PHONE_H}><ScreenCoupons/></DCArtboard>
      <DCArtboard id="g6" label="G6 · 设置" width={PHONE_W} height={PHONE_H}><ScreenSettings/></DCArtboard>
      <DCArtboard id="g7" label="G7 · 消息中心" width={PHONE_W} height={PHONE_H}><ScreenMessages/></DCArtboard>
      <DCArtboard id="g8" label="G8 · 联系客服" width={PHONE_W} height={PHONE_H}><ScreenCS/></DCArtboard>
      <DCArtboard id="g9" label="G9 · 酒豆明细" width={PHONE_W} height={PHONE_H}><ScreenBeansLog/></DCArtboard>
      <DCArtboard id="g10" label="G10 · DF 赠送" width={PHONE_W} height={PHONE_H}><ScreenDFGift/></DCArtboard>
      <DCArtboard id="g11" label="G11 · DF 赠送记录" width={PHONE_W} height={PHONE_H}><ScreenDFGiftLog/></DCArtboard>
    </DCSection>

    <DCSection id="H" title="H · 通用状态与弹窗" subtitle="加载 · 空 · 错误 · 拦截 · Toast · 确认 · 操作面板">
      <DCArtboard id="h1" label="H1 · 加载中" width={PHONE_W} height={PHONE_H}><ScreenLoading/></DCArtboard>
      <DCArtboard id="h2" label="H2 · 空状态" width={PHONE_W} height={PHONE_H}><ScreenEmpty/></DCArtboard>
      <DCArtboard id="h3" label="H3 · 网络错误" width={PHONE_W} height={PHONE_H}><ScreenNetError/></DCArtboard>
      <DCArtboard id="h4" label="H4 · 未登录拦截" width={PHONE_W} height={PHONE_H}><ScreenAuthGate/></DCArtboard>
      <DCArtboard id="h5" label="H5 · Toast" width={PHONE_W} height={PHONE_H}><ScreenToast/></DCArtboard>
      <DCArtboard id="h6" label="H6 · 确认弹窗" width={PHONE_W} height={PHONE_H}><ScreenConfirm/></DCArtboard>
      <DCArtboard id="h7" label="H7 · 操作面板" width={PHONE_W} height={PHONE_H}><ScreenActionSheet/></DCArtboard>
    </DCSection>

    <DCSection id="X" title="X · 补充页面 · v1.2" subtitle="订单状态分屏 · 取消原因 · 系统更新 · 实名认证 · 资料 · 邀请码 · 地址管理">
      <DCArtboard id="x1" label="X1 · 订单 待发货" width={PHONE_W} height={PHONE_H}><ScreenOrdersWaitShip/></DCArtboard>
      <DCArtboard id="x2" label="X2 · 订单 待收货" width={PHONE_W} height={PHONE_H}><ScreenOrdersWaitReceive/></DCArtboard>
      <DCArtboard id="x3" label="X3 · 订单 已完成" width={PHONE_W} height={PHONE_H}><ScreenOrdersDone/></DCArtboard>
      <DCArtboard id="x4" label="X4 · 取消订单原因" width={PHONE_W} height={PHONE_H}><ScreenCancelOrder/></DCArtboard>
      <DCArtboard id="x5" label="X5 · 系统更新弹窗" width={PHONE_W} height={PHONE_H}><ScreenSystemUpdate/></DCArtboard>
      <DCArtboard id="x6" label="X6 · 个人资料编辑" width={PHONE_W} height={PHONE_H}><ScreenProfileEdit/></DCArtboard>
      <DCArtboard id="x7" label="X7 · 实名认证" width={PHONE_W} height={PHONE_H}><ScreenRealName/></DCArtboard>
      <DCArtboard id="x8" label="X8 · 实名强制弹窗" width={PHONE_W} height={PHONE_H}><ScreenRealNameForced/></DCArtboard>
      <DCArtboard id="x9" label="X9 · 我的邀请码" width={PHONE_W} height={PHONE_H}><ScreenInviteCode/></DCArtboard>
      <DCArtboard id="x10" label="X10 · 地址管理" width={PHONE_W} height={PHONE_H}><ScreenAddressManage/></DCArtboard>
    </DCSection>

    <DCSection id="R" title="R · 多机型响应式适配" subtitle="iPhone SE 375 · 标准 390 · 动态岛 393 · Pro Max 430">
      <DCArtboard id="r1" label="375 × 667 · iPhone SE" width={375} height={667}><ScreenHomeResp w={375}/></DCArtboard>
      <DCArtboard id="r2" label="390 × 844 · iPhone 14" width={390} height={844}><ScreenHomeResp w={390}/></DCArtboard>
      <DCArtboard id="r3" label="393 × 852 · 15 Pro" width={393} height={852}><ScreenHomeResp w={393} di/></DCArtboard>
      <DCArtboard id="r4" label="430 × 932 · 15 Pro Max" width={430} height={932}><ScreenHomeResp w={430} di/></DCArtboard>
    </DCSection>
  </DesignCanvas>
);

// Cover card
const CoverCard = () => (
  <div style={{
    width: '100%', height: '100%',
    background: 'linear-gradient(180deg, #2A0E00 0%, #3B1900 50%, #421E04 100%)',
    color: '#F6E7C2',
    padding: '60px 38px',
    display: 'flex', flexDirection: 'column',
    fontFamily: '-apple-system, "PingFang SC", sans-serif',
    position: 'relative', overflow: 'hidden',
  }}>
    <div style={{ position: 'absolute', top: '-15%', right: '-25%', width: '70%', height: '70%', borderRadius: '50%', background: 'radial-gradient(circle, rgba(228,165,22,0.18), transparent 60%)' }}/>
    <BrandMark size={72}/>
    <div style={{ marginTop: 28, fontSize: 36, fontWeight: 700, letterSpacing: 6, color: '#FFE0A0' }}>醇品汇</div>
    <div style={{ marginTop: 8, fontSize: 14, color: 'rgba(246,231,194,0.7)', letterSpacing: 2 }}>甄选美酒 · 共创财富</div>
    <div style={{ marginTop: 36, fontSize: 22, fontWeight: 700, color: '#fff', lineHeight: 1.4 }}>
      微信小程序<br/>高保真设计稿
    </div>
    <div style={{ marginTop: 20, fontSize: 13, color: 'rgba(246,231,194,0.75)', lineHeight: 1.8 }}>
      · 60+ 高保真页面<br/>
      · 8 大模块完整覆盖<br/>
      · 严格遵循参考稿视觉语言<br/>
      · 4 档机型响应式适配规则
    </div>
    <div style={{ marginTop: 'auto', borderTop: '1px solid rgba(246,231,194,0.18)', paddingTop: 16, fontSize: 11, color: 'rgba(246,231,194,0.45)', letterSpacing: 1.5 }}>
      DESIGN BASE · 390 × 844<br/>
      v1.0 · 2026.05
    </div>
  </div>
);

const mount = () => createRoot(document.getElementById('root')).render(<App/>);
if (document.readyState === 'loading') {
  window.addEventListener('DOMContentLoaded', mount);
} else {
  mount();
}
