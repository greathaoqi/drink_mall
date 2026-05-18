<script lang="ts">
import { checkAuth } from '@/services/auth'
import { useUserStore } from '@/store/user'
import { captureReferral, parseReferralOptions } from '@/utils/referral'

export default {
  globalData: {
    productListCategoryId: null as number | null
  },
  onLaunch(options: any) {
    const userStore = useUserStore()
    captureReferral(options?.query || options || {}, userStore)
    if (userStore.token) {
      checkAuth().catch(() => {})
    }
  },
  onShow(options: any) {
    const userStore = useUserStore()
    const referral = parseReferralOptions(options?.query || options || {})
    if (referral.inviteCode) {
      userStore.setReferral(referral.inviteCode)
    }
    console.log('App Show')
  },
  onHide() {
    console.log('App Hide')
  }
}
</script>

<style lang="scss">
@import '@/styles/design-tokens.scss';

page {
  background-color: var(--dm-bg-app);
  color: var(--dm-text-1);
  font-family: -apple-system, BlinkMacSystemFont, "Helvetica Neue", Helvetica, sans-serif;
  font-size: 28rpx;
  line-height: 1.45;
}

button {
  margin: 0;
}

button:after {
  border: 0;
}

.mall-page {
  min-height: 100vh;
  background: var(--dm-bg-app);
  color: var(--dm-text-1);
  padding-bottom: calc(24rpx + env(safe-area-inset-bottom));
}

.mall-card {
  background: var(--dm-bg-card);
  border-radius: var(--dm-radius-md);
  box-shadow: var(--dm-shadow-card);
}

.mall-price {
  color: var(--dm-gold-500);
  font-weight: 800;
  font-variant-numeric: tabular-nums;
}

.mall-muted {
  color: var(--dm-text-3);
}

.mall-press {
  transition: transform .14s ease, opacity .14s ease;
}

.mall-press:active {
  opacity: .72;
  transform: scale(.985);
}

/* Global Styles */
.text-primary {
  color: var(--dm-text-1);
}

.text-secondary {
  color: var(--dm-text-3);
}

.text-accent {
  color: var(--dm-gold-500);
}

.bg-white {
  background-color: var(--dm-bg-card);
}

.bg-secondary {
  background-color: var(--dm-bg-app);
}
</style>
