<template>
  <div class="dashboard-container">
    <el-container class="layout">
      <!-- 侧边栏 -->
      <el-aside width="200px" class="sidebar">
        <div class="logo">
          <h2>小说AI创作平台</h2>
        </div>
        <el-menu
          :default-active="activeMenu"
          class="sidebar-menu"
          @select="handleMenuSelect"
          router
        >
          <el-menu-item index="/dashboard">
            <el-icon><House /></el-icon>
            <span>仪表盘</span>
          </el-menu-item>
          <el-sub-menu index="1">
            <template #title>
              <el-icon><Notebook /></el-icon>
              <span>小说创作</span>
            </template>
            <el-menu-item index="/outline">大纲生成</el-menu-item>
            <el-menu-item index="/characters">人物设定</el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="2">
            <template #title>
              <el-icon><Picture /></el-icon>
              <span>多媒体生成</span>
            </template>
            <el-menu-item index="/audio">语音生成</el-menu-item>
            <el-menu-item index="/images">图片生成</el-menu-item>
            <el-menu-item index="/videos">视频生成</el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="3">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>系统管理</span>
            </template>
            <el-menu-item index="/settings">系统设置</el-menu-item>
            <el-menu-item index="/tasks">任务管理</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-aside>

      <!-- 主内容区 -->
      <el-container>
        <el-header class="header">
          <div class="header-left">
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item v-if="$route.meta.title">{{ $route.meta.title }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <div class="header-right">
            <el-dropdown @command="handleCommand">
              <span class="user-info">
                <el-avatar :size="32" icon="User" />
                <span class="username">管理员</span>
                <el-icon class="arrow"><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>

        <el-main class="main">
          <router-view v-if="$route.path !== '/dashboard'" />
          <div v-else class="dashboard-content">
            <h2 class="welcome-title">欢迎使用小说AI创作平台</h2>
            <p class="welcome-desc">从这里开始你的小说创作之旅</p>
            
            <el-row :gutter="20" class="quick-actions">
              <el-col :span="6">
                <el-card class="action-card" @click="$router.push('/outline')">
                  <div class="card-icon">
                    <el-icon size="40"><Notebook /></el-icon>
                  </div>
                  <h3>大纲生成</h3>
                  <p>基于AI生成小说大纲</p>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card class="action-card" @click="$router.push('/characters')">
                  <div class="card-icon">
                    <el-icon size="40"><User /></el-icon>
                  </div>
                  <h3>人物设定</h3>
                  <p>创建和管理角色</p>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card class="action-card" @click="$router.push('/images')">
                  <div class="card-icon">
                    <el-icon size="40"><Picture /></el-icon>
                  </div>
                  <h3>图片生成</h3>
                  <p>为角色生成立绘</p>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card class="action-card" @click="$router.push('/videos')">
                  <div class="card-icon">
                    <el-icon size="40"><VideoCamera /></el-icon>
                  </div>
                  <h3>视频生成</h3>
                  <p>生成小说动画视频</p>
                </el-card>
              </el-col>
            </el-row>

            <el-row :gutter="20" class="stats-row">
              <el-col :span="8">
                <el-card>
                  <h3>创作统计</h3>
                  <div class="stat-item">
                    <span class="label">已完成小说</span>
                    <span class="value">3</span>
                  </div>
                  <div class="stat-item">
                    <span class="label">生成图片数</span>
                    <span class="value">128</span>
                  </div>
                  <div class="stat-item">
                    <span class="label">生成视频数</span>
                    <span class="value">12</span>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="8">
                <el-card>
                  <h3>任务状态</h3>
                  <el-progress :percentage="70" :format="() => '进行中'">进行中</el-progress>
                  <el-progress :percentage="30" status="success" :format="() => '已完成'">已完成</el-progress>
                  <el-progress :percentage="10" status="exception" :format="() => '失败'">失败</el-progress>
                </el-card>
              </el-col>
              <el-col :span="8">
                <el-card>
                  <h3>系统状态</h3>
                  <div class="system-status">
                    <el-tag type="success">AI服务正常</el-tag>
                    <el-tag type="success">存储服务正常</el-tag>
                    <el-tag type="warning">ComfyUI队列中</el-tag>
                  </div>
                </el-card>
              </el-col>
            </el-row>
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  House,
  Notebook,
  Picture,
  Setting,
  User,
  VideoCamera,
  ArrowDown
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const activeMenu = computed(() => route.path)

const handleMenuSelect = (index: string) => {
  if (index.startsWith('/')) {
    router.push(index)
  }
}

const handleCommand = async (command: string) => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      localStorage.removeItem('token')
      router.push('/login')
      ElMessage.success('已退出登录')
    } catch {
      // 取消退出
    }
  } else if (command === 'profile') {
    ElMessage.info('个人中心功能开发中')
  }
}
</script>

<style scoped lang="scss">
.dashboard-container {
  height: 100vh;

  .layout {
    height: 100%;
  }

  .sidebar {
    background-color: #001529;
    color: white;

    .logo {
      height: 60px;
      display: flex;
      align-items: center;
      justify-content: center;
      border-bottom: 1px solid #002140;

      h2 {
        color: white;
        font-size: 16px;
        margin: 0;
      }
    }

    .sidebar-menu {
      border-right: none;
      background-color: #001529;

      :deep(.el-menu-item),
      :deep(.el-sub-menu__title) {
        color: rgba(255, 255, 255, 0.65);

        &:hover {
          background-color: #000c17;
          color: white;
        }
      }

      :deep(.el-menu-item.is-active) {
        background-color: #1890ff;
        color: white;
      }
    }
  }

  .header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    background-color: white;
    border-bottom: 1px solid #e8e8e8;
    padding: 0 20px;

    .header-left {
      .el-breadcrumb {
        line-height: 60px;
      }
    }

    .header-right {
      .user-info {
        display: flex;
        align-items: center;
        cursor: pointer;

        .username {
          margin: 0 8px;
        }

        .arrow {
          margin-left: 4px;
        }
      }
    }
  }

  .main {
    background-color: #f0f2f5;
    padding: 20px;
    height: calc(100vh - 60px);
    overflow: auto;

    .dashboard-content {
      .welcome-title {
        font-size: 24px;
        margin-bottom: 10px;
        color: #333;
      }

      .welcome-desc {
        color: #666;
        margin-bottom: 30px;
      }

      .quick-actions {
        margin-bottom: 20px;

        .action-card {
          cursor: pointer;
          text-align: center;
          transition: transform 0.3s;

          &:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
          }

          .card-icon {
            margin-bottom: 15px;
            color: #1890ff;
          }

          h3 {
            margin-bottom: 10px;
            color: #333;
          }

          p {
            color: #666;
            font-size: 12px;
          }
        }
      }

      .stats-row {
        .stat-item {
          display: flex;
          justify-content: space-between;
          margin: 10px 0;

          .label {
            color: #666;
          }

          .value {
            font-weight: bold;
            color: #1890ff;
          }
        }

        .system-status {
          .el-tag {
            margin-right: 10px;
            margin-bottom: 10px;
          }
        }
      }
    }
  }
}
</style>