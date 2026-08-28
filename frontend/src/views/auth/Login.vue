<template>
  <div class="login-page">
    <!-- 左侧品牌面板：要客星图 -->
    <section class="brand-panel">
      <header class="brand-top">
        <div class="brand-logo">C</div>
        <span class="brand-name">CMI 要客运营平台</span>
      </header>

      <div class="brand-center">
        <p class="eyebrow">CMI · KEY ACCOUNT OPERATION</p>
        <h1 class="headline">连接要客网络，<br />洞见决策链路</h1>
        <p class="sub">客户全景、决策关键人与集团协同，都在同一个工作台。</p>
      </div>

      <svg
        class="constellation"
        viewBox="0 0 640 600"
        fill="none"
        xmlns="http://www.w3.org/2000/svg"
        aria-hidden="true"
      >
        <!-- 连接线 -->
        <g stroke="rgba(129,140,248,0.30)" stroke-width="1.2">
          <path d="M200,430 L330,340" />
          <path d="M330,340 L330,210" />
          <path d="M330,340 L480,400" />
          <path d="M330,210 L180,180" />
          <path d="M330,210 L500,250" />
          <path d="M500,250 L560,140" />
          <path d="M500,250 L430,100" />
          <path d="M480,400 L590,330" />
          <path d="M330,210 L430,100" />
        </g>

        <!-- 枢纽信号环 -->
        <g class="motion-el">
          <circle cx="200" cy="430" r="16" stroke="rgba(129,140,248,0.4)" stroke-width="1">
            <animate attributeName="r" values="14;36" dur="3.4s" repeatCount="indefinite" />
            <animate attributeName="stroke-opacity" values="0.55;0" dur="3.4s" repeatCount="indefinite" />
          </circle>
          <circle cx="200" cy="430" r="16" stroke="rgba(129,140,248,0.4)" stroke-width="1">
            <animate attributeName="r" values="14;36" dur="3.4s" begin="1.7s" repeatCount="indefinite" />
            <animate attributeName="stroke-opacity" values="0.55;0" dur="3.4s" begin="1.7s" repeatCount="indefinite" />
          </circle>
        </g>

        <!-- 节点光晕 -->
        <g fill="#818cf8">
          <circle class="node-glow" cx="330" cy="340" r="15" opacity="0.14" />
          <circle class="node-glow" cx="480" cy="400" r="12" opacity="0.12" style="animation-delay: -1.3s" />
          <circle class="node-glow" cx="330" cy="210" r="16" opacity="0.15" style="animation-delay: -2.1s" />
          <circle class="node-glow" cx="500" cy="250" r="13" opacity="0.13" style="animation-delay: -0.7s" />
          <circle class="node-glow" cx="560" cy="140" r="10" opacity="0.12" style="animation-delay: -2.8s" />
          <circle class="node-glow" cx="180" cy="180" r="10" opacity="0.12" style="animation-delay: -1.8s" />
          <circle class="node-glow" cx="430" cy="100" r="11" opacity="0.12" style="animation-delay: -3.4s" />
          <circle class="node-glow" cx="590" cy="330" r="9" opacity="0.11" style="animation-delay: -0.4s" />
          <circle class="node-glow" cx="200" cy="430" r="24" opacity="0.20" />
        </g>

        <!-- 节点核心 -->
        <g>
          <circle cx="330" cy="340" r="5.5" fill="#c7d2fe" />
          <circle cx="480" cy="400" r="4.5" fill="#818cf8" />
          <circle cx="330" cy="210" r="6" fill="#c7d2fe" />
          <circle cx="500" cy="250" r="5" fill="#c7d2fe" />
          <circle cx="560" cy="140" r="3.8" fill="#818cf8" />
          <circle cx="180" cy="180" r="4" fill="#818cf8" />
          <circle cx="430" cy="100" r="4.2" fill="#c7d2fe" />
          <circle cx="590" cy="330" r="3.6" fill="#818cf8" />
          <!-- 枢纽：要客 -->
          <circle cx="200" cy="430" r="13" fill="rgba(199,210,254,0.30)" />
          <circle cx="200" cy="430" r="7.5" fill="#ffffff" />
        </g>

        <!-- 枢纽标注 -->
        <text class="node-label" x="222" y="452">要客</text>

        <!-- 沿线光脉冲 -->
        <g fill="#a5b4fc">
          <circle class="pulse motion-el" r="3.2" opacity="0.9">
            <animateMotion dur="7s" repeatCount="indefinite" path="M200,430 L330,340 L330,210 L500,250 L560,140" />
          </circle>
          <circle class="pulse motion-el" r="2.6" opacity="0.75">
            <animateMotion dur="9s" begin="1.6s" repeatCount="indefinite" path="M200,430 L330,340 L480,400 L590,330" />
          </circle>
          <circle class="pulse motion-el" r="2.6" opacity="0.75">
            <animateMotion dur="8s" begin="3.2s" repeatCount="indefinite" path="M180,180 L330,210 L430,100" />
          </circle>
        </g>
      </svg>

      <footer class="brand-foot">CMI 要客运营平台 · 内部系统，仅限授权人员使用</footer>
    </section>

    <!-- 右侧登录表单 -->
    <section class="form-panel">
      <div class="form-inner">
        <h2 class="form-title">登录工作台</h2>
        <p class="form-desc">使用您的账号进入要客运营工作台</p>

        <a-alert
          v-if="errorMsg"
          :message="errorMsg"
          type="error"
          show-icon
          class="form-alert"
        />

        <a-form class="login-form" layout="vertical" :model="form" @finish="handleSubmit">
          <a-form-item
            name="username"
            label="用户名"
            :rules="[{ required: true, message: '请输入用户名' }]"
          >
            <a-input
              v-model:value="form.username"
              size="large"
              placeholder="请输入用户名"
              autocomplete="username"
              allow-clear
            >
              <template #prefix><UserOutlined class="input-icon" /></template>
            </a-input>
          </a-form-item>

          <a-form-item
            name="password"
            label="密码"
            :rules="[{ required: true, message: '请输入密码' }]"
          >
            <a-input-password
              v-model:value="form.password"
              size="large"
              placeholder="请输入密码"
              autocomplete="current-password"
            >
              <template #prefix><LockOutlined class="input-icon" /></template>
            </a-input-password>
          </a-form-item>

          <a-form-item class="submit-item">
            <a-button
              type="primary"
              html-type="submit"
              block
              size="large"
              :loading="loading"
              class="login-btn"
            >
              {{ loading ? '正在登录…' : '登 录' }}
            </a-button>
          </a-form-item>
        </a-form>

        <p class="form-hint">忘记密码？请联系系统管理员</p>
      </div>
    </section>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import { getLastUsername, setLastUsername } from '../../utils/token'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const form = reactive({
  username: getLastUsername(),
  password: ''
})
const loading = ref(false)
const errorMsg = ref('')

function safeRedirect(target) {
  if (typeof target === 'string' && target.startsWith('/') && !target.startsWith('//')) {
    return target
  }
  return '/'
}

async function handleSubmit() {
  loading.value = true
  errorMsg.value = ''
  try {
    await authStore.login(form.username.trim(), form.password)
    setLastUsername(form.username.trim())
    // 用户信息获取失败不阻塞进入，Layout 会重试
    await authStore.fetchUserInfo().catch(() => null)
    router.push(safeRedirect(route.query.redirect))
  } catch (error) {
    errorMsg.value = error?.message || '登录失败，请重试'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  display: flex;
  min-height: 100vh;
}

/* ==================== 左侧品牌面板 ==================== */

.brand-panel {
  position: relative;
  flex: 1.18;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 44px 52px;
  background: linear-gradient(160deg, #0c1430 0%, #101a3f 48%, #1c2a5e 100%);
  color: #f4f6ff;
}

.brand-panel::before {
  content: '';
  position: absolute;
  right: -160px;
  bottom: -140px;
  width: 720px;
  height: 720px;
  background: radial-gradient(circle, rgba(99, 102, 241, 0.30) 0%, rgba(99, 102, 241, 0) 66%);
  pointer-events: none;
}

.brand-top {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  gap: 12px;
}

.brand-logo {
  width: 34px;
  height: 34px;
  border-radius: 9px;
  background: linear-gradient(135deg, #6366f1, #818cf8);
  color: #fff;
  font-weight: 700;
  font-size: 17px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 14px rgba(99, 102, 241, 0.45);
}

.brand-name {
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 0.02em;
  color: #e7ebff;
}

.brand-center {
  position: relative;
  z-index: 1;
  padding-right: 12%;
}

.eyebrow {
  font-family: ui-monospace, 'SF Mono', 'Cascadia Mono', Consolas, monospace;
  font-size: 11px;
  letter-spacing: 0.32em;
  color: #8b97d4;
  margin: 0 0 22px;
}

.headline {
  font-size: 44px;
  line-height: 1.3;
  font-weight: 700;
  letter-spacing: 0.01em;
  color: #f4f6ff;
  margin: 0 0 20px;
}

.sub {
  font-size: 15px;
  line-height: 1.9;
  color: #9aa6d6;
  max-width: 30em;
  margin: 0;
}

.constellation {
  position: absolute;
  right: -30px;
  bottom: 26px;
  width: min(560px, 62%);
  height: auto;
  z-index: 0;
}

.node-label {
  font-family: ui-monospace, 'SF Mono', 'Cascadia Mono', Consolas, monospace;
  font-size: 11px;
  letter-spacing: 0.2em;
  fill: rgba(165, 180, 252, 0.85);
}

.node-glow {
  animation: breathe 4.5s ease-in-out infinite;
}

@keyframes breathe {
  0%, 100% { opacity: 0.08; }
  50% { opacity: 0.22; }
}

.brand-foot {
  position: relative;
  z-index: 1;
  font-size: 12px;
  letter-spacing: 0.06em;
  color: #5d6a9e;
}

/* ==================== 右侧表单面板 ==================== */

.form-panel {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px 24px;
  background: #f7f8fc;
}

.form-inner {
  width: 100%;
  max-width: 380px;
}

.form-title {
  font-size: 26px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 10px;
}

.form-desc {
  font-size: 14px;
  color: #6b7280;
  margin: 0 0 30px;
}

.form-alert {
  border-radius: 10px;
  margin-bottom: 20px;
}

.input-icon {
  color: #9ca3af;
}

.login-form :deep(.ant-input-affix-wrapper) {
  border-radius: 10px;
  padding: 9px 14px;
}

.login-form :deep(.ant-input-affix-wrapper-focused),
.login-form :deep(.ant-input-affix-wrapper:focus) {
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.12);
}

.login-form :deep(.ant-form-item-label > label) {
  font-size: 13px;
  color: #374151;
}

.submit-item {
  margin-top: 8px;
  margin-bottom: 0;
}

.login-btn {
  height: 46px;
  border-radius: 10px;
  border: none;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 0.1em;
  background: linear-gradient(135deg, #6366f1 0%, #7c6cf0 100%);
  box-shadow: 0 8px 20px rgba(99, 102, 241, 0.28);
  transition: filter 0.2s ease, transform 0.2s ease, box-shadow 0.2s ease;
}

.login-btn:hover {
  filter: brightness(1.07);
  transform: translateY(-1px);
  box-shadow: 0 10px 24px rgba(99, 102, 241, 0.34);
}

.login-btn:active {
  transform: translateY(0);
}

.login-btn:focus-visible {
  outline: 2px solid #6366f1;
  outline-offset: 2px;
}

.form-hint {
  margin-top: 22px;
  text-align: center;
  font-size: 12.5px;
  color: #9ca3af;
}

/* ==================== 响应式 ==================== */

@media (max-width: 960px) {
  .login-page {
    flex-direction: column;
  }

  .brand-panel {
    flex: none;
    min-height: 250px;
    padding: 26px 24px 30px;
  }

  .brand-center {
    padding-right: 0;
  }

  .headline {
    font-size: 27px;
    margin-bottom: 10px;
  }

  .sub {
    font-size: 13px;
  }

  .constellation {
    width: 300px;
    right: -70px;
    bottom: -30px;
    opacity: 0.4;
  }

  .brand-foot {
    display: none;
  }

  .form-panel {
    flex: 1;
    padding: 40px 24px;
  }
}

/* 减少动态效果偏好：隐藏运动元素，仅保留静态星图 */
@media (prefers-reduced-motion: reduce) {
  .node-glow {
    animation: none;
  }

  .motion-el {
    display: none;
  }

  .login-btn {
    transition: none;
  }
}
</style>
