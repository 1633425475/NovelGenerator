<template>
  <div class="image-container">
    <el-page-header @back="$router.push('/dashboard')" title="返回">
      <template #content>
        <h2>AI图片生成</h2>
      </template>
    </el-page-header>
    
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="generate-card">
          <template #header>
            <span>图片生成参数</span>
          </template>
          <el-form :model="form" label-width="80px">
            <el-form-item label="提示词">
              <el-input
                v-model="form.prompt"
                type="textarea"
                :rows="4"
                placeholder="详细描述你想要生成的图片内容..."
              />
            </el-form-item>
            <el-form-item label="负面词">
              <el-input
                v-model="form.negativePrompt"
                type="textarea"
                :rows="2"
                placeholder="不希望出现在图片中的内容..."
              />
            </el-form-item>
            <el-row :gutter="10">
              <el-col :span="12">
                <el-form-item label="宽度">
                  <el-input-number v-model="form.width" :min="256" :max="1024" :step="64" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="高度">
                  <el-input-number v-model="form.height" :min="256" :max="1024" :step="64" style="width: 100%" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="采样步数">
              <el-slider v-model="form.steps" :min="10" :max="50" :step="5" show-input />
            </el-form-item>
            <el-form-item label="参考图">
              <el-upload
                action="#"
                :auto-upload="false"
                :on-change="handleImageUpload"
                :show-file-list="false"
              >
                <el-button type="primary" plain>
                  <el-icon><Upload /></el-icon>
                  上传参考图
                </el-button>
              </el-upload>
              <div v-if="referenceImage" class="reference-preview">
                <img :src="referenceImage" alt="参考图" />
                <el-button type="text" size="small" @click="referenceImage = ''">清除</el-button>
              </div>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="generating" @click="generateImage" style="width: 100%">
                <el-icon><MagicStick /></el-icon>
                生成图片
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card>
          <template #header>
            <span>生成结果</span>
            <div style="float: right">
              <el-button type="text" size="small">保存图片</el-button>
              <el-button type="text" size="small">批量下载</el-button>
            </div>
          </template>
          <div class="result-area">
            <div v-if="generatedImages.length > 0" class="image-grid">
              <div v-for="(image, index) in generatedImages" :key="index" class="image-item">
                <div class="image-wrapper">
                  <img :src="image.url" :alt="image.prompt" />
                  <div class="image-overlay">
                    <el-button type="primary" size="small" circle>
                      <el-icon><Download /></el-icon>
                    </el-button>
                    <el-button type="warning" size="small" circle>
                      <el-icon><Edit /></el-icon>
                    </el-button>
                    <el-button type="danger" size="small" circle>
                      <el-icon><Delete /></el-icon>
                    </el-button>
                  </div>
                </div>
                <div class="image-info">
                  <p class="prompt-preview">{{ image.prompt.substring(0, 50) }}...</p>
                  <div class="image-meta">
                    <span>{{ image.size }}</span>
                    <span>{{ image.model }}</span>
                  </div>
                </div>
              </div>
            </div>
            <div v-else class="empty-result">
              <el-icon size="80"><Picture /></el-icon>
              <p>暂无生成的图片，请先填写参数并生成</p>
            </div>
          </div>
        </el-card>
        
        <el-card class="mt-3">
          <template #header>
            <span>图片库</span>
            <el-button type="text" size="small" style="float: right">管理</el-button>
          </template>
          <div class="library-tabs">
            <el-tabs v-model="activeTab">
              <el-tab-pane label="角色立绘" name="character">
                <div class="category-images">
                  <div class="category-item">
                    <img src="https://via.placeholder.com/150x200" alt="角色立绘" />
                    <p>林风</p>
                  </div>
                  <div class="category-item">
                    <img src="https://via.placeholder.com/150x200" alt="角色立绘" />
                    <p>苏灵儿</p>
                  </div>
                  <div class="category-item">
                    <img src="https://via.placeholder.com/150x200" alt="角色立绘" />
                    <p>黑暗君主</p>
                  </div>
                </div>
              </el-tab-pane>
              <el-tab-pane label="场景图" name="scene">
                <div class="category-images">
                  <div class="category-item">
                    <img src="https://via.placeholder.com/200x150" alt="场景图" />
                    <p>宗门大殿</p>
                  </div>
                  <div class="category-item">
                    <img src="https://via.placeholder.com/200x150" alt="场景图" />
                    <p>山林小道</p>
                  </div>
                </div>
              </el-tab-pane>
              <el-tab-pane label="物品图" name="item">
                <div class="category-images">
                  <div class="category-item">
                    <img src="https://via.placeholder.com/120x120" alt="物品图" />
                    <p>神秘玉佩</p>
                  </div>
                </div>
              </el-tab-pane>
            </el-tabs>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { MagicStick, Upload, Download, Edit, Delete, Picture } from '@element-plus/icons-vue'

const generating = ref(false)
const referenceImage = ref('')
const activeTab = ref('character')

const form = reactive({
  prompt: '一位英俊的年轻剑客，身穿白色长袍，手持长剑，站在山巅，身后是云海和朝阳，玄幻风格，细节精致',
  negativePrompt: '低质量，模糊，变形',
  width: 512,
  height: 768,
  steps: 30
})

const generatedImages = ref([
  { url: 'https://via.placeholder.com/512x768', prompt: '年轻剑客山巅站姿', size: '512x768', model: 'SDXL' },
  { url: 'https://via.placeholder.com/512x768', prompt: '女修士御剑飞行', size: '512x768', model: 'SDXL' },
  { url: 'https://via.placeholder.com/512x768', prompt: '神秘宗门大殿', size: '512x768', model: 'SDXL' },
  { url: 'https://via.placeholder.com/512x768', prompt: '古风园林场景', size: '512x768', model: 'SDXL' }
])

const handleImageUpload = (file: any) => {
  const reader = new FileReader()
  reader.onload = (e) => {
    referenceImage.value = e.target?.result as string
  }
  reader.readAsDataURL(file.raw)
}

const generateImage = () => {
  generating.value = true
  setTimeout(() => {
    generatedImages.value.unshift({
      url: 'https://via.placeholder.com/512x768',
      prompt: form.prompt,
      size: `${form.width}x${form.height}`,
      model: 'SDXL'
    })
    generating.value = false
    ElMessage.success('图片生成成功！')
  }, 3000)
}
</script>

<style scoped lang="scss">
.image-container {
  padding: 20px;

  .generate-card {
    .reference-preview {
      margin-top: 10px;

      img {
        max-width: 100%;
        max-height: 150px;
        border-radius: 4px;
      }
    }
  }

  .result-area {
    .image-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 20px;

      @media (min-width: 1400px) {
        grid-template-columns: repeat(3, 1fr);
      }

      .image-item {
        border: 1px solid #eee;
        border-radius: 4px;
        overflow: hidden;

        .image-wrapper {
          position: relative;
          aspect-ratio: 2/3;
          overflow: hidden;

          img {
            width: 100%;
            height: 100%;
            object-fit: cover;
          }

          .image-overlay {
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: rgba(0, 0, 0, 0.5);
            display: flex;
            align-items: center;
            justify-content: center;
            opacity: 0;
            transition: opacity 0.3s;

            &:hover {
              opacity: 1;
            }
          }
        }

        .image-info {
          padding: 10px;

          .prompt-preview {
            margin: 0;
            font-size: 12px;
            color: #666;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }

          .image-meta {
            display: flex;
            justify-content: space-between;
            font-size: 12px;
            color: #999;
            margin-top: 5px;
          }
        }
      }
    }

    .empty-result {
      text-align: center;
      padding: 60px 0;
      color: #999;

      p {
        margin-top: 10px;
      }
    }
  }

  .library-tabs {
    .category-images {
      display: flex;
      gap: 20px;
      flex-wrap: wrap;

      .category-item {
        text-align: center;

        img {
          width: 100%;
          max-width: 150px;
          border-radius: 4px;
        }

        p {
          margin-top: 5px;
          font-size: 12px;
        }
      }
    }
  }
}
</style>