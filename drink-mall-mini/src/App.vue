<script lang="ts">
import { checkAuth } from '@/services/auth'
import { useUserStore } from '@/store/user'

export default {
  globalData: {
    productListCategoryId: null as number | null
  },
  onLaunch() {
    const userStore = useUserStore()
    if (userStore.token) {
      checkAuth().catch(() => {})
    }
  },
  onShow() {
    console.log('App Show')
  },
  onHide() {
    console.log('App Hide')
  }
}
</script>

<style lang="scss">
page {
  background-color: #F7F7F7;
}

/* Global Styles */
.text-primary {
  color: #333333;
}

.text-secondary {
  color: #999999;
}

.text-accent {
  color: #07C160;
}

.bg-white {
  background-color: #FFFFFF;
}

.bg-secondary {
  background-color: #F7F7F7;
}
</style>