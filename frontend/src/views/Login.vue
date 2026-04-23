<template>
  <div class="login-container">
    <div class="login-form">
      <h2 class="title">基于 AI 的小说生成工具</h2>
      <el-form :model="form" :rules="rules" ref="formRef" @submit.prevent="handleLogin">
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleLogin"
            class="login-btn"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
      <div class="tips">
        <span>测试账号: admin / admin123</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { loginApi, type LoginRequest, type LoginResponse } from '@/api/auth'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive<LoginRequest>({
  username: 'admin',
  password: 'admin123'
})

const rules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        console.log('登录请求数据:', form)
        console.log('登录请求URL:', import.meta.env.VITE_API_BASE_URL + '/auth/login')
        const response = await loginApi(form) as any
        console.log('登录响应:', response)
        const loginData = response as LoginResponse
        
        localStorage.setItem('token', loginData.token)
        localStorage.setItem('userInfo', JSON.stringify({
          userId: loginData.userId,
          username: loginData.username,
          nickname: loginData.nickname,
          avatarUrl: loginData.avatarUrl,
          roles: loginData.roles,
          permissions: loginData.permissions
        }))
        
        ElMessage.success('登录成功')
        router.push('/dashboard')
      } catch (error: any) {
        console.error('登录失败:', error)
        console.error('错误响应:', error.response)
        console.error('错误状态:', error.response?.status)
        console.error('错误数据:', error.response?.data)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped lang="scss">
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-form {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);

  .title {
    text-align: center;
    margin-bottom: 30px;
    color: #333;
    font-size: 24px;
  }

  .login-btn {
    width: 100%;
    margin-top: 10px;
  }

  .tips {
    text-align: center;
    margin-top: 20px;
    color: #999;
    font-size: 12px;
  }
}
</style>