<template>
  <div class="video-container">
    <el-page-header @back="$router.push('/dashboard')" title="返回">
      <template #content>
        <h2>AI视频生成</h2>
      </template>
    </el-page-header>
    
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="generate-card">
          <template #header>
            <span>视频生成参数</span>
          </template>
          <el-form :model="form" label-width="100px">
            <el-form-item label="视频类型">
              <el-radio-group v-model="form.type">
                <el-radio label="storyboard">故事板动画</el-radio>
                <el-radio label="character">角色动画</el-radio>
                <el-radio label="scene">场景动画</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="输入图片">
              <div class="image-selection">
                <div v-for="(img, index) in selectedImages" :key="index" class="selected-image">
                  <img :src="img.url" :alt="img.name" />
                  <el-icon class="remove-btn" @click="removeImage(index)"><Close /></el-icon>
                </div>
                <el-upload
                  action="#"
                  :auto-upload="false"
                  :on-change="addImage"
                  :show-file-list="false"
                  multiple
                >
                  <div class="upload-area">
                    <el-icon size="40"><Plus /></el-icon>
                    <p>添加图片</p>
                  </div>
                </el-upload>
              </div>
              <p class="tip">支持添加多张图片，系统将根据图片生成连贯动画</p>
            </el-form-item>
            <el-form-item label="动画参数">
              <el-row :gutter="10">
                <el-col :span="12">
                  <el-form-item label="视频长度" label-width="80px">
                    <el-input-number v-model="form.duration" :min="1" :max="60" :step="0.5" style="width: 100%" />
                    <span class="unit">秒</span>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="帧率" label-width="80px">
                    <el-input-number v-model="form.fps" :min="12" :max="60" :step="1" style="width: 100%" />
                    <span class="unit">fps</span>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form-item>
            <el-form-item label="运动描述">
              <el-input
                v-model="form.motionPrompt"
                type="textarea"
                :rows="3"
                placeholder="描述你想要的效果，例如：镜头从左向右缓慢平移，角色轻微点头..."
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="generating" @click="generateVideo" style="width: 100%">
                <el-icon><VideoCamera /></el-icon>
                生成视频
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
        
        <el-card class="mt-3">
          <template #header>
            <span>生成任务队列</span>
          </template>
          <el-table :data="tasks" border>
            <el-table-column prop="id" label="任务ID" width="100" />
            <el-table-column prop="name" label="任务名称" />
            <el-table-column prop="progress" label="进度" width="120">
              <template #default="{ row }">
                <el-progress :percentage="row.progress" :status="row.status === 'failed' ? 'exception' : undefined" />
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="statusType(row.status)">{{ row.statusText }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button v-if="row.status === 'completed'" type="text" size="small">查看</el-button>
                <el-button v-if="row.status === 'failed'" type="text" size="small">重试</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="preview-card">
          <template #header>
            <span>视频预览</span>
            <div style="float: right">
              <el-button type="text" size="small" :disabled="!currentVideo">下载</el-button>
              <el-button type="text" size="small" :disabled="!currentVideo">分享</el-button>
            </div>
          </template>
          <div class="video-preview">
            <div v-if="currentVideo" class="video-player">
              <video :src="currentVideo" controls style="width: 100%; border-radius: 4px;"></video>
            </div>
            <div v-else class="empty-preview">
              <el-icon size="80"><VideoCamera /></el-icon>
              <p>暂无视频预览</p>
              <p class="sub">请先生成或选择视频</p>
            </div>
          </div>
          <div class="video-info" v-if="currentVideo">
            <h4>视频信息</h4>
            <el-row>
              <el-col :span="12">
                <div class="info-item">
                  <span class="label">分辨率:</span>
                  <span class="value">1920x1080</span>
                </div>
                <div class="info-item">
                  <span class="label">时长:</span>
                  <span class="value">15.6秒</span>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="info-item">
                  <span class="label">帧率:</span>
                  <span class="value">30 fps</span>
                </div>
                <div class="info-item">
                  <span class="label">文件大小:</span>
                  <span class="value">45.2 MB</span>
                </div>
              </el-col>
            </el-row>
          </div>
        </el-card>
        
        <el-card class="mt-3">
          <template #header>
            <span>视频库</span>
            <el-button type="text" size="small" style="float: right">管理</el-button>
          </template>
          <div class="video-library">
            <div class="video-list">
              <div v-for="video in videoLibrary" :key="video.id" class="video-item" @click="playVideo(video)">
                <div class="video-thumbnail">
                  <img :src="video.thumbnail" :alt="video.title" />
                  <div class="video-duration">{{ video.duration }}</div>
                </div>
                <div class="video-details">
                  <h5>{{ video.title }}</h5>
                  <p class="desc">{{ video.description }}</p>
                  <div class="video-meta">
                    <span>{{ video.createdAt }}</span>
                    <span>{{ video.size }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { VideoCamera, Plus, Close } from '@element-plus/icons-vue'

const generating = ref(false)
const currentVideo = ref('')
const selectedImages = ref([
  { url: 'https://via.placeholder.com/200x150', name: '场景1' },
  { url: 'https://via.placeholder.com/200x150', name: '场景2' },
  { url: 'https://via.placeholder.com/200x150', name: '场景3' }
])

const form = reactive({
  type: 'storyboard',
  duration: 10,
  fps: 30,
  motionPrompt: '镜头从左向右缓慢平移，画面中的云海缓缓流动，角色轻微点头'
})

const tasks = ref([
  { id: '001', name: '第一章故事板动画', progress: 100, status: 'completed', statusText: '已完成' },
  { id: '002', name: '角色介绍动画', progress: 65, status: 'running', statusText: '进行中' },
  { id: '003', name: '场景切换动画', progress: 30, status: 'running', statusText: '进行中' },
  { id: '004', name: '片头动画', progress: 0, status: 'pending', statusText: '等待中' },
  { id: '005', name: '打斗场景', progress: 0, status: 'failed', statusText: '失败' }
])

const videoLibrary = ref([
  { id: 1, title: '第一章故事板', description: '主角初入宗门的故事板动画', thumbnail: 'https://via.placeholder.com/120x80', duration: '0:15', createdAt: '2024-01-15', size: '25.3MB' },
  { id: 2, title: '角色介绍动画', description: '主要角色介绍动画', thumbnail: 'https://via.placeholder.com/120x80', duration: '0:22', createdAt: '2024-01-14', size: '38.7MB' },
  { id: 3, title: '宗门全景', description: '宗门全景展示动画', thumbnail: 'https://via.placeholder.com/120x80', duration: '0:18', createdAt: '2024-01-13', size: '32.1MB' }
])

const statusType = (status: string) => {
  const map: Record<string, string> = {
    completed: 'success',
    running: 'primary',
    pending: 'info',
    failed: 'danger'
  }
  return map[status] || 'info'
}

const addImage = (file: any) => {
  const reader = new FileReader()
  reader.onload = (e) => {
    selectedImages.value.push({
      url: e.target?.result as string,
      name: file.name
    })
  }
  reader.readAsDataURL(file.raw)
}

const removeImage = (index: number) => {
  selectedImages.value.splice(index, 1)
}

const generateVideo = () => {
  if (selectedImages.value.length === 0) {
    ElMessage.warning('请至少添加一张图片')
    return
  }
  
  generating.value = true
  setTimeout(() => {
    currentVideo.value = 'https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_1mb.mp4'
    tasks.value.unshift({
      id: '006',
      name: '新视频任务',
      progress: 100,
      status: 'completed',
      statusText: '已完成'
    })
    generating.value = false
    ElMessage.success('视频生成成功！')
  }, 5000)
}

const playVideo = (video: any) => {
  currentVideo.value = 'https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_1mb.mp4'
  ElMessage.info(`正在播放: ${video.title}`)
}
</script>

<style scoped lang="scss">
.video-container {
  padding: 20px;

  .generate-card {
    .image-selection {
      display: flex;
      flex-wrap: wrap;
      gap: 10px;

      .selected-image {
        position: relative;
        width: 80px;
        height: 60px;
        border: 1px solid #ddd;
        border-radius: 4px;
        overflow: hidden;

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }

        .remove-btn {
          position: absolute;
          top: 2px;
          right: 2px;
          background: rgba(0, 0, 0, 0.5);
          color: white;
          border-radius: 50%;
          cursor: pointer;
          padding: 2px;
          font-size: 12px;

          &:hover {
            background: rgba(0, 0, 0, 0.8);
          }
        }
      }

      .upload-area {
        width: 80px;
        height: 60px;
        border: 2px dashed #ddd;
        border-radius: 4px;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        color: #999;

        &:hover {
          border-color: #409eff;
          color: #409eff;
        }

        p {
          margin: 5px 0 0;
          font-size: 12px;
        }
      }
    }

    .tip {
      font-size: 12px;
      color: #999;
      margin-top: 5px;
    }

    .unit {
      margin-left: 5px;
      color: #666;
    }
  }

  .preview-card {
    .video-preview {
      .video-player {
        margin-bottom: 20px;
      }

      .empty-preview {
        text-align: center;
        padding: 60px 0;
        color: #999;

        .sub {
          font-size: 12px;
          margin-top: 5px;
        }
      }
    }

    .video-info {
      h4 {
        margin-bottom: 15px;
        color: #333;
      }

      .info-item {
        margin-bottom: 10px;

        .label {
          display: inline-block;
          width: 80px;
          color: #666;
        }

        .value {
          color: #333;
          font-weight: 500;
        }
      }
    }
  }

  .video-library {
    .video-list {
      .video-item {
        display: flex;
        padding: 10px 0;
        border-bottom: 1px solid #eee;
        cursor: pointer;

        &:last-child {
          border-bottom: none;
        }

        &:hover {
          background-color: #f9f9f9;
        }

        .video-thumbnail {
          position: relative;
          flex-shrink: 0;
          width: 120px;
          height: 80px;
          margin-right: 15px;
          border-radius: 4px;
          overflow: hidden;

          img {
            width: 100%;
            height: 100%;
            object-fit: cover;
          }

          .video-duration {
            position: absolute;
            bottom: 5px;
            right: 5px;
            background: rgba(0, 0, 0, 0.7);
            color: white;
            padding: 2px 6px;
            border-radius: 2px;
            font-size: 10px;
          }
        }

        .video-details {
          flex: 1;

          h5 {
            margin: 0 0 5px;
            font-size: 14px;
            color: #333;
          }

          .desc {
            margin: 0 0 5px;
            font-size: 12px;
            color: #666;
          }

          .video-meta {
            display: flex;
            justify-content: space-between;
            font-size: 12px;
            color: #999;
          }
        }
      }
    }
  }
}
</style>