<template>
  <div class="error-page">
    <div class="error-content">
      <div class="error-code">404</div>
      <h1 class="error-title">页面不存在</h1>
      <p class="error-desc">抱歉，您访问的页面不存在或已被移动</p>
      <div class="error-actions">
        <el-button type="primary" @click="$router.push('/dashboard')">返回首页</el-button>
        <el-button @click="$router.back()">返回上一页</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped lang="scss">
.error-page {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

  .error-content {
    text-align: center;
    background: white;
    padding: 60px 40px;
    border-radius: 10px;
    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);

    .error-code {
      font-size: 120px;
      font-weight: bold;
      color: #f56c6c;
      line-height: 1;
      margin-bottom: 20px;
    }

    .error-title {
      font-size: 24px;
      color: #333;
      margin-bottom: 15px;
    }

    .error-desc {
      font-size: 16px;
      color: #666;
      margin-bottom: 30px;
    }

    .error-actions {
      display: flex;
      gap: 15px;
      justify-content: center;
    }
  }
}
</style>