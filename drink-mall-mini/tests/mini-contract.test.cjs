const assert = require('node:assert/strict')
const { readFileSync } = require('node:fs')
const { join } = require('node:path')
const test = require('node:test')

const root = join(__dirname, '..')
const read = (path) => readFileSync(join(root, path), 'utf8')

test('mini program imports shared design tokens globally', () => {
  const app = read('src/App.vue')
  const tokens = read('src/styles/design-tokens.scss')

  assert.match(app, /@import ['"]@\/styles\/design-tokens\.scss['"]/)
  assert.match(tokens, /--dm-brown-900:\s*#1F0900/i)
  assert.match(tokens, /--dm-gold-500:\s*#D38A00/i)
  assert.match(tokens, /--dm-bg-app:\s*#F7F3EA/i)
  assert.match(tokens, /--dm-radius-md:\s*12rpx/i)
  assert.match(tokens, /--dm-shadow-card:/)
  assert.match(tokens, /\.dm-page/)
  assert.match(tokens, /\.dm-btn-primary/)
})

test('shared visual foundation components expose reusable app chrome contracts', () => {
  const nav = read('src/components/DesignNavBar/DesignNavBar.vue')
  const tabs = read('src/components/SectionTabs/SectionTabs.vue')
  const modal = read('src/components/DesignModal/DesignModal.vue')
  const sheet = read('src/components/DesignActionSheet/DesignActionSheet.vue')
  const toast = read('src/components/DesignToast/DesignToast.vue')

  assert.match(nav, /safeAreaInsetTop/)
  assert.match(nav, /back|close/)
  assert.match(tabs, /modelValue/)
  assert.match(tabs, /update:modelValue/)
  assert.match(modal, /cancelText/)
  assert.match(modal, /confirmText/)
  assert.match(sheet, /actions/)
  assert.match(sheet, /safe-area-inset-bottom/)
  assert.match(toast, /uni\.showToast/)
  assert.doesNotMatch(nav + tabs + modal + sheet + toast, /third|tree|children|descendants|level3|\u4e09\u7ea7/)
})

test('shared page states include loading empty error and auth gate contracts', () => {
  const pageState = read('src/components/PageState/PageState.vue')
  const authGate = read('src/components/AuthGate/AuthGate.vue')
  const loginPrompt = read('src/components/LoginPrompt/LoginPrompt.vue')

  assert.match(pageState, /loadingText/)
  assert.match(pageState, /errorText/)
  assert.match(pageState, /emptyText/)
  assert.match(pageState, /authRequired/)
  assert.match(authGate, /allowGuest|guest/)
  assert.match(loginPrompt, /login|Login|\u767b\u5f55/)
})

test('product purchase pages block disabled payment and expose share poster paths without mojibake', () => {
  const detail = read('src/pages/product/detail/index.vue')
  const checkout = read('src/pages/checkout/index.vue')
  const selector = read('src/components/PayMethodSelector/PayMethodSelector.vue')
  const result = read('src/pages/payment/result/index.vue')
  const touched = [
    detail,
    read('src/pages/cart/index.vue'),
    checkout,
    read('src/pages/address/list/index.vue'),
    read('src/pages/address/edit/index.vue'),
    result,
    selector
  ].join('\n')

  assert.match(detail, /showShareSheet/)
  assert.match(detail, /showPoster/)
  assert.match(detail, /buildProductSharePath/)
  assert.match(detail, /scene:\s*'share_product'/)
  assert.match(checkout, /selectedMethod\.value\.disabled/)
  assert.match(checkout, /canUseSinglePayMethod/)
  assert.match(checkout, /hasGiftAndNormal/)
  assert.match(checkout, /hasInvestment/)
  assert.match(selector, /item\.disabled/)
  assert.match(selector, /uni\.showToast/)
  assert.match(result, /\u652f\u4ed8\u6210\u529f/)
  assert.match(result, /\u652f\u4ed8\u672a\u5b8c\u6210/)
  assert.doesNotMatch(touched, /锟|閰|璐|鏀|绉|鍟|鐧|寰|榛|涓|浣|鍗曢|褰撳墠|璇烽|楼/)
})

test('account flow page exposes design states and mandatory invite binding contract', () => {
  const login = read('src/pages/login/index.vue')

  assert.match(login, /inviteCode/)
  assert.match(login, /showPhoneSheet/)
  assert.match(login, /showAgreementPanel/)
  assert.match(login, /confirmPhoneAndLogin/)
  assert.match(login, /parseReferralOptions/)
  assert.match(login, /agreed/)
})

test('real-name page validates ID card and uploads both ID images before submit', () => {
  const page = read('src/pages/auth/realname/index.vue')
  const service = read('src/services/user.ts')

  assert.match(page, /isValidChineseIdCard/)
  assert.match(page, /idCardNo/)
  assert.match(page, /frontImageUrl/)
  assert.match(page, /backImageUrl/)
  assert.match(page, /chooseIdImage\('front'\)/)
  assert.match(page, /chooseIdImage\('back'\)/)
  assert.match(page, /userApi\.uploadImage/)
  assert.match(page, /userApi\.submitRealName\(\{\s*realName:/)
  assert.doesNotMatch(page, /submitRealName\(form\.value\)/)

  assert.match(service, /uploadImage:\s*\(filePath: string\)/)
  assert.match(service, /uni\.uploadFile/)
  assert.match(service, /\/upload\/image/)
})
