<template>
  <div class="outline-container">
    <el-page-header @back="$router.push('/dashboard')" title="返回">
      <template #content>
        <h2>小说大纲生成</h2>
        <span v-if="generated" class="page-subtitle">
          当前版本: {{ outline.version }} | 状态: {{ outline.status }} | 创建时间: {{ formatDate(outline.createdAt) }}
        </span>
      </template>
    </el-page-header>
    
    <el-card class="main-card">
      <template #header>
        <span>生成新大纲</span>
        <el-button v-if="generated" type="text" size="small" style="float: right" @click="generated = false">
          重新生成
        </el-button>
      </template>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="创意描述" prop="idea">
          <el-input
            v-model="form.idea"
            type="textarea"
            :rows="4"
            placeholder="请输入你的小说创意，例如：一个平凡的少年意外获得神秘力量，踏上了拯救世界的征程..."
          />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="小说风格" prop="style">
              <el-select v-model="form.style" placeholder="请选择风格" style="width: 100%">
                <el-option label="玄幻" value="玄幻" />
                <el-option label="都市" value="都市" />
                <el-option label="科幻" value="科幻" />
                <el-option label="武侠" value="武侠" />
                <el-option label="历史" value="历史" />
                <el-option label="奇幻" value="奇幻" />
                <el-option label="悬疑" value="悬疑" />
                <el-option label="言情" value="言情" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="目标字数" prop="targetWordCount">
              <el-input-number
                v-model="form.targetWordCount"
                :min="10000"
                :max="1000000"
                :step="10000"
                style="width: 100%"
              />
              <span class="unit">字</span>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="章节数量" prop="chapterCount">
              <el-input-number
                v-model="form.chapterCount"
                :min="10"
                :max="200"
                :step="5"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="异步生成">
          <el-switch v-model="form.async" active-text="异步" inactive-text="同步" />
          <span class="async-tip">异步生成适合长大纲，任务将在后台处理</span>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="generating" @click="generateOutline">
            <el-icon><MagicStick /></el-icon>
            {{ generated ? '重新生成' : '生成大纲' }}
          </el-button>
          <el-button v-if="generated" type="success" :loading="generating" @click="saveOutline">
            <el-icon><DocumentAdd /></el-icon>
            保存为新版本
          </el-button>
          <el-button v-if="generated" type="danger" @click="deleteOutline">
            <el-icon><Delete /></el-icon>
            删除大纲
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-row :gutter="20" v-if="generated">
      <el-col :span="18">
        <el-card class="result-card">
          <template #header>
            <span>大纲详情</span>
            <div style="float: right">
              <el-tag type="success">{{ outline.style }}风格</el-tag>
              <el-tag type="info" class="ml-2">{{ outline.targetWordCount.toLocaleString() }}字</el-tag>
              <el-tag type="warning" class="ml-2">{{ outline.chapterCount }}章</el-tag>
            </div>
          </template>
          <div class="outline-preview">
            <h3 class="outline-title">{{ outline.title }}</h3>
            <p class="idea">{{ outline.idea }}</p>
            
            <el-collapse v-model="activePanels" class="outline-collapse">
              <el-collapse-item title="世界观设定" name="world">
                <div class="content-box" v-html="outline.worldBuilding || '暂无世界观设定'"></div>
              </el-collapse-item>
              <el-collapse-item title="故事主线" name="story">
                <div class="content-box" v-html="outline.mainStory || '暂无故事主线'"></div>
              </el-collapse-item>
              <el-collapse-item title="主要人物" name="characters">
                <el-table :data="outline.mainCharacters" border v-if="outline.mainCharacters.length > 0">
                  <el-table-column prop="name" label="姓名" width="120" />
                  <el-table-column prop="gender" label="性别" width="80" />
                  <el-table-column prop="age" label="年龄" width="80" />
                  <el-table-column prop="identity" label="身份/职业" />
                  <el-table-column prop="personality" label="性格特点" />
                </el-table>
                <div v-else class="empty-content">暂无人物设定</div>
              </el-collapse-item>
              <el-collapse-item title="章节概要" name="chapters">
                <el-timeline v-if="outline.chapters.length > 0">
                  <el-timeline-item
                    v-for="chapter in outline.chapters"
                    :key="chapter.id"
                    :timestamp="`第${chapter.chapterNumber}章`"
                    placement="top"
                  >
                    <el-card class="chapter-card">
                      <h4>{{ chapter.title }}</h4>
                      <p>{{ chapter.summary }}</p>
                      <div class="chapter-meta">
                        <span>预计字数: {{ chapter.estimatedWordCount }}字</span>
                      </div>
                    </el-card>
                  </el-timeline-item>
                </el-timeline>
                <div v-else class="empty-content">暂无章节概要</div>
              </el-collapse-item>
            </el-collapse>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="version-card">
          <template #header>
            <span>版本历史</span>
            <el-button type="text" size="small" style="float: right" @click="currentOutlineId && loadOutlineVersions(currentOutlineId)">
              刷新
            </el-button>
          </template>
          <div class="version-list" v-if="outlineVersions.length > 0">
            <div 
              v-for="version in outlineVersions" 
              :key="version.id"
              class="version-item"
              :class="{ active: version.id === outline.id }"
              @click="switchVersion(version.id)"
            >
              <div class="version-header">
                <span class="version-tag">{{ version.version }}</span>
                <span class="version-date">{{ formatDate(version.createdAt) }}</span>
              </div>
              <div class="version-title">{{ version.title }}</div>
              <div class="version-meta">
                <span>{{ version.style }}</span>
                <span>{{ version.chapterCount }}章</span>
                <span>{{ version.targetWordCount.toLocaleString() }}字</span>
              </div>
            </div>
          </div>
          <div v-else class="empty-versions">
            <el-icon><Histogram /></el-icon>
            <p>暂无版本历史</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { 
  generateOutline as apiGenerateOutline,
  getOutlineDetail,
  deleteOutline as apiDeleteOutline,
  updateOutline as apiUpdateOutline,
  getOutlineVersions,
  type OutlineGenerateRequest,
  type OutlineGenerateResponse,
  type ChapterDTO,
  type MainCharacterDTO
} from '@/api/novel'
import { 
  MagicStick, 
  DocumentAdd, 
  Delete, 
  Histogram 
} from '@element-plus/icons-vue'

const formRef = ref<FormInstance>()
const generating = ref(false)
const generated = ref(false)
const activePanels = ref(['world', 'characters', 'story', 'chapters'])
const currentOutlineId = ref<number | null>(null)
const outlineVersions = ref<any[]>([])

// 表单数据 - 注意字段名与API保持一致
const form = reactive<OutlineGenerateRequest>({
  idea: '',
  style: '玄幻',
  targetWordCount: 50000,
  chapterCount: 30,
  async: false
})

const rules: FormRules = {
  idea: [
    { required: true, message: '请输入创意描述', trigger: 'blur' },
    { min: 10, message: '创意描述至少10个字符', trigger: 'blur' }
  ],
  style: [
    { required: true, message: '请选择小说风格', trigger: 'change' }
  ],
  targetWordCount: [
    { required: true, message: '请输入目标字数', trigger: 'blur' },
    { type: 'number', min: 10000, max: 1000000, message: '字数应在10000-1000000之间', trigger: 'blur' }
  ],
  chapterCount: [
    { required: true, message: '请输入章节数量', trigger: 'blur' },
    { type: 'number', min: 10, max: 200, message: '章节数应在10-200之间', trigger: 'blur' }
  ]
}

// 大纲数据
const outline = reactive<{
  id?: number
  title: string
  idea: string
  style: string
  targetWordCount: number
  chapterCount: number
  worldBuilding: string
  mainStory: string
  status: string
  version: string
  createdAt: string
  chapters: ChapterDTO[]
  mainCharacters: MainCharacterDTO[]
}>({
  title: '',
  idea: '',
  style: '',
  targetWordCount: 0,
  chapterCount: 0,
  worldBuilding: '',
  mainStory: '',
  status: '',
  version: '',
  createdAt: '',
  chapters: [],
  mainCharacters: []
})

// 生成大纲
const generateOutline = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    generating.value = true
    
    // 调用API生成大纲
    const response = await apiGenerateOutline(form)
    
    // 更新大纲数据
    updateOutlineData(response)
    currentOutlineId.value = response.id
    generated.value = true
    
    ElMessage.success('大纲生成成功！')
    
    // 加载版本历史
    if (response.id) {
      loadOutlineVersions(response.id)
    }
  } catch (error: any) {
    ElMessage.error(`生成失败: ${error.message || '未知错误'}`)
  } finally {
    generating.value = false
  }
}

// 保存大纲（实际上是更新大纲，创建新版本）
const saveOutline = async () => {
  if (!currentOutlineId.value) return
  
  try {
    await ElMessageBox.confirm('保存大纲将创建新版本，是否继续？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    generating.value = true
    const response = await apiUpdateOutline(currentOutlineId.value, form)
    
    // 更新大纲数据
    updateOutlineData(response)
    currentOutlineId.value = response.id
    
    ElMessage.success('大纲保存成功（新版本已创建）！')
    
    // 重新加载版本历史
    loadOutlineVersions(response.id)
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(`保存失败: ${error.message || '未知错误'}`)
    }
  } finally {
    generating.value = false
  }
}

// 删除大纲
const deleteOutline = async () => {
  if (!currentOutlineId.value) return
  
  try {
    await ElMessageBox.confirm('确定要删除这个大纲吗？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'error'
    })
    
    await apiDeleteOutline(currentOutlineId.value)
    ElMessage.success('大纲删除成功')
    
    // 重置状态
    generated.value = false
    currentOutlineId.value = null
    outlineVersions.value = []
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(`删除失败: ${error.message || '未知错误'}`)
    }
  }
}

// 加载版本历史
const loadOutlineVersions = async (outlineId: number) => {
  try {
    const versions = await getOutlineVersions(outlineId)
    outlineVersions.value = versions
  } catch (error) {
    console.error('加载版本历史失败:', error)
  }
}

// 切换版本
const switchVersion = async (versionId: number) => {
  try {
    const response = await getOutlineDetail(versionId)
    updateOutlineData(response)
    currentOutlineId.value = response.id
    
    // 更新表单数据
    form.idea = response.idea
    form.style = response.style
    form.targetWordCount = response.targetWordCount
    form.chapterCount = response.chapterCount
    
    ElMessage.success(`已切换到版本 ${response.version}`)
  } catch (error: any) {
    ElMessage.error(`切换版本失败: ${error.message || '未知错误'}`)
  }
}

// 更新大纲数据
const updateOutlineData = (response: OutlineGenerateResponse) => {
  outline.id = response.id
  outline.title = response.title
  outline.idea = response.idea
  outline.style = response.style
  outline.targetWordCount = response.targetWordCount
  outline.chapterCount = response.chapterCount
  outline.worldBuilding = response.worldBuilding || ''
  outline.mainStory = response.mainStory || ''
  outline.status = response.status
  outline.version = response.version
  outline.createdAt = response.createdAt
  outline.chapters = response.chapters || []
  outline.mainCharacters = response.mainCharacters || []
}

// 格式化日期
const formatDate = (dateStr: string) => {
  return new Date(dateStr).toLocaleString('zh-CN')
}

// 在组件挂载时，如果有URL参数，可以加载已有大纲
onMounted(() => {
  // 这里可以添加从URL参数加载大纲的逻辑
})
</script>

<style scoped lang="scss">
.outline-container {
  padding: 20px;
  height: 100%;
  overflow: auto;

  .page-subtitle {
    font-size: 14px;
    color: #666;
    margin-top: 5px;
    display: block;
  }

  .main-card {
    margin-top: 20px;
    margin-bottom: 20px;

    .unit {
      margin-left: 10px;
      color: #666;
    }

    .async-tip {
      margin-left: 10px;
      font-size: 12px;
      color: #999;
    }
  }

  .result-card {
    .outline-preview {
      .outline-title {
        font-size: 20px;
        margin-bottom: 10px;
        color: #333;
        border-bottom: 1px solid #eee;
        padding-bottom: 10px;
      }

      .idea {
        color: #666;
        margin: 10px 0 20px;
        font-size: 16px;
        line-height: 1.6;
        padding: 15px;
        background-color: #f9f9f9;
        border-radius: 4px;
      }

      .outline-collapse {
        margin-top: 20px;

        .content-box {
          padding: 15px;
          background-color: #f9f9f9;
          border-radius: 4px;
          line-height: 1.6;
          min-height: 100px;

          :deep(p) {
            margin-bottom: 10px;
          }

          :deep(ul) {
            padding-left: 20px;
          }

          :deep(li) {
            margin-bottom: 5px;
          }
        }

        .empty-content {
          text-align: center;
          padding: 40px 20px;
          color: #999;
          font-size: 14px;
        }

        .chapter-card {
          margin-bottom: 10px;

          h4 {
            margin-bottom: 10px;
            color: #333;
          }

          p {
            color: #666;
            line-height: 1.5;
            margin-bottom: 10px;
          }

          .chapter-meta {
            font-size: 12px;
            color: #999;
            border-top: 1px solid #eee;
            padding-top: 8px;
            margin-top: 8px;
          }
        }
      }
    }
  }

  .version-card {
    height: 100%;

    .version-list {
      .version-item {
        padding: 12px;
        border: 1px solid #eee;
        border-radius: 4px;
        margin-bottom: 10px;
        cursor: pointer;
        transition: all 0.3s;

        &:hover {
          border-color: #409eff;
          background-color: #f0f7ff;
        }

        &.active {
          border-color: #409eff;
          background-color: #ecf5ff;
        }

        .version-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 8px;

          .version-tag {
            background-color: #409eff;
            color: white;
            padding: 2px 8px;
            border-radius: 10px;
            font-size: 12px;
          }

          .version-date {
            font-size: 12px;
            color: #999;
          }
        }

        .version-title {
          font-weight: 500;
          margin-bottom: 6px;
          color: #333;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .version-meta {
          display: flex;
          justify-content: space-between;
          font-size: 12px;
          color: #666;

          span {
            background-color: #f5f5f5;
            padding: 2px 6px;
            border-radius: 2px;
          }
        }
      }
    }

    .empty-versions {
      text-align: center;
      padding: 40px 20px;
      color: #999;

      .el-icon {
        font-size: 40px;
        margin-bottom: 10px;
      }

      p {
        font-size: 14px;
      }
    }
  }
}

// 响应式调整
@media (max-width: 1200px) {
  .outline-container {
    .result-card,
    .version-card {
      margin-top: 20px;
    }
  }
}
</style>