<template>
  <div class="timeline-container">
    <el-page-header @back="$router.push('/dashboard')" title="返回">
      <template #content>
        <h2>时间线管理</h2>
      </template>
    </el-page-header>
    
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="filter-card">
          <template #header>
            <span>筛选条件</span>
          </template>
          <el-input placeholder="搜索时间线" v-model="searchKeyword" clearable @change="loadTimelines">
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-divider />
          <el-checkbox v-model="filter.isMain" @change="loadTimelines">仅显示主线时间线</el-checkbox>
          <el-divider />
          <el-button type="primary" @click="addTimeline" style="width: 100%">添加时间线</el-button>
          <el-button type="success" @click="loadTimelines" style="width: 100%; margin-top: 10px">刷新列表</el-button>
        </el-card>
      </el-col>
      <el-col :span="18">
        <el-card>
          <template #header>
            <span>时间线列表</span>
          </template>
          <el-table :data="timelines" border>
            <el-table-column prop="name" label="名称" />
            <el-table-column prop="description" label="描述" />
            <el-table-column prop="timeUnit" label="时间单位" width="120" />
            <el-table-column prop="startTime" label="开始时间" width="120" />
            <el-table-column prop="endTime" label="结束时间" width="120" />
            <el-table-column prop="isMain" label="主线" width="80">
              <template #default="{ row }">
                <el-tag v-if="row.isMain" type="success">是</el-tag>
                <el-tag v-else type="info">否</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200">
              <template #default="{ row }">
                <el-button type="primary" size="small" text @click="editTimeline(row)">编辑</el-button>
                <el-button type="info" size="small" text @click="viewTimeline(row)">详情</el-button>
                <el-button type="success" size="small" text @click="viewOnMap(row)">
                  <el-icon><MapLocation /></el-icon>
                </el-button>
                <el-button type="danger" size="small" text @click="deleteTimeline(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div class="pagination">
            <el-pagination
              v-model:current-page="pagination.current"
              v-model:page-size="pagination.size"
              :total="pagination.total"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </el-card>
        
        <el-card class="mt-3">
          <template #header>
            <span>时间点管理</span>
            <el-button type="primary" size="small" style="float: right" @click="addTimePoint">添加时间点</el-button>
          </template>
          <div v-if="selectedTimeline">
            <el-timeline>
              <el-timeline-item
                v-for="(point, index) in selectedTimeline.timePoints"
                :key="index"
                :timestamp="point.time"
                :type="index === 0 ? 'primary' : 'success'"
                placement="top"
              >
                <el-card>
                  <h4>{{ point.title }}</h4>
                  <p>{{ point.description }}</p>
                  <div v-if="point.relatedEntities && point.relatedEntities.length > 0">
                    <el-tag v-for="entity in point.relatedEntities" :key="entity.id" size="small" style="margin-right: 5px">
                      {{ entity.type }}: {{ entity.name }}
                    </el-tag>
                  </div>
                  <div style="margin-top: 10px">
                    <el-button type="primary" size="small" text @click="editTimePoint(point, index)">编辑</el-button>
                    <el-button type="danger" size="small" text @click="deleteTimePoint(index)">删除</el-button>
                  </div>
                </el-card>
              </el-timeline-item>
            </el-timeline>
          </div>
          <div v-else class="empty-timeline">
            <el-icon size="60"><Clock /></el-icon>
            <p>请先选择一个时间线</p>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 添加/编辑时间线对话框 -->
    <el-dialog
      v-model="showTimelineDialog"
      :title="editMode === 'add' ? '添加时间线' : '编辑时间线'"
      width="600px"
      @close="resetTimelineForm"
    >
      <el-form :model="timelineForm" label-width="100px" :rules="formRules" ref="timelineFormRef">
        <el-form-item label="名称" prop="name">
          <el-input v-model="timelineForm.name" placeholder="请输入时间线名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="timelineForm.description" type="textarea" placeholder="请输入时间线描述" />
        </el-form-item>
        <el-form-item label="时间单位">
          <el-select v-model="timelineForm.timeUnit" placeholder="请选择时间单位">
            <el-option label="年" value="年" />
            <el-option label="月" value="月" />
            <el-option label="日" value="日" />
            <el-option label="时辰" value="时辰" />
            <el-option label="自定义" value="自定义" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间">
          <el-input v-model="timelineForm.startTime" placeholder="例如：元年" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-input v-model="timelineForm.endTime" placeholder="例如：十年后" />
        </el-form-item>
        <el-form-item label="主线时间线">
          <el-switch v-model="timelineForm.isMain" />
        </el-form-item>
        <el-form-item label="标签">
          <el-select v-model="timelineForm.tags" multiple placeholder="选择标签" filterable allow-create>
            <el-option v-for="tag in availableTags" :key="tag" :label="tag" :value="tag" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showTimelineDialog = false">取消</el-button>
        <el-button type="primary" @click="submitTimelineForm">保存</el-button>
      </template>
    </el-dialog>

    <!-- 添加/编辑时间点对话框 -->
    <el-dialog
      v-model="showTimePointDialog"
      :title="timePointEditMode === 'add' ? '添加时间点' : '编辑时间点'"
      width="600px"
      @close="resetTimePointForm"
    >
      <el-form :model="timePointForm" label-width="100px" :rules="timePointRules" ref="timePointFormRef">
        <el-form-item label="时间" prop="time">
          <el-input v-model="timePointForm.time" placeholder="例如：元年春" />
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="timePointForm.title" placeholder="请输入时间点标题" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="timePointForm.description" type="textarea" placeholder="请输入时间点描述" />
        </el-form-item>
        <el-form-item label="相关实体">
          <div v-for="(entity, index) in timePointForm.relatedEntities" :key="index" class="entity-item">
            <el-select v-model="entity.type" placeholder="类型" style="width: 120px; margin-right: 10px">
              <el-option label="人物" value="CHARACTER" />
              <el-option label="地点" value="LOCATION" />
              <el-option label="事件" value="EVENT" />
              <el-option label="物品" value="ITEM" />
            </el-select>
            <el-input v-model="entity.name" placeholder="名称" style="width: 150px; margin-right: 10px" />
            <el-button type="danger" size="small" text @click="removeRelatedEntity(index)">删除</el-button>
          </div>
          <el-button type="primary" size="small" @click="addRelatedEntity">添加相关实体</el-button>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showTimePointDialog = false">取消</el-button>
        <el-button type="primary" @click="submitTimePointForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, Clock, MapLocation } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import * as resourceApi from '@/api/resource'

// 当前小说ID（暂时硬编码，后续从路由或store获取）
const NOVEL_ID = 1

const router = useRouter()

// 搜索与筛选
const searchKeyword = ref('')
const filter = reactive({
  isMain: false
})

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 数据
const timelines = ref<resourceApi.TimelineDTO[]>([])
const selectedTimeline = ref<resourceApi.TimelineDTO | null>(null)
const availableTags = ref<string[]>(['主线', '支线', '历史', '未来', '回忆'])

// 对话框状态
const showTimelineDialog = ref(false)
const showTimePointDialog = ref(false)
const editMode = ref<'add' | 'edit'>('add')
const timePointEditMode = ref<'add' | 'edit'>('add')
const currentTimelineId = ref<number | null>(null)
const currentTimePointIndex = ref<number | null>(null)

// 表单
const timelineForm = reactive<resourceApi.CreateTimelineRequest>({
  name: '',
  description: '',
  timeUnit: '年',
  startTime: '',
  endTime: '',
  timePoints: [],
  novelId: NOVEL_ID,
  status: 'ACTIVE',
  isMain: false,
  tags: []
})

const timePointForm = reactive({
  time: '',
  title: '',
  description: '',
  relatedEntities: [] as resourceApi.RelatedEntity[]
})

const timelineFormRef = ref<FormInstance>()
const timePointFormRef = ref<FormInstance>()
const formRules: FormRules = {
  name: [
    { required: true, message: '请输入时间线名称', trigger: 'blur' }
  ]
}
const timePointRules: FormRules = {
  time: [
    { required: true, message: '请输入时间', trigger: 'blur' }
  ],
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' }
  ]
}

// 加载时间线列表
const loadTimelines = async () => {
  try {
    const response = await resourceApi.listTimelines(NOVEL_ID)
    // 过滤主线时间线
    let filtered = response
    if (filter.isMain) {
      filtered = response.filter(t => t.isMain)
    }
    timelines.value = filtered
    pagination.total = timelines.value.length
  } catch (error) {
    ElMessage.error('加载时间线列表失败')
    console.error(error)
  }
}

// 分页大小变化
const handleSizeChange = (size: number) => {
  pagination.size = size
  loadTimelines()
}

// 当前页变化
const handleCurrentChange = (page: number) => {
  pagination.current = page
  loadTimelines()
}

// 添加时间线
const addTimeline = () => {
  editMode.value = 'add'
  resetTimelineForm()
  showTimelineDialog.value = true
}

// 编辑时间线
const editTimeline = (row: resourceApi.TimelineDTO) => {
  editMode.value = 'edit'
  currentTimelineId.value = row.id!
  selectedTimeline.value = row
  Object.assign(timelineForm, {
    name: row.name,
    description: row.description || '',
    timeUnit: row.timeUnit || '年',
    startTime: row.startTime || '',
    endTime: row.endTime || '',
    timePoints: row.timePoints || [],
    novelId: row.novelId,
    status: row.status || 'ACTIVE',
    isMain: row.isMain || false,
    tags: row.tags || []
  })
  showTimelineDialog.value = true
}

// 查看时间线详情
const viewTimeline = async (row: resourceApi.TimelineDTO) => {
  try {
    const timeline = await resourceApi.getTimeline(NOVEL_ID, row.id!)
    selectedTimeline.value = timeline
    ElMessageBox.alert(
      `
      <h3>${timeline.name}</h3>
      <p><strong>描述：</strong>${timeline.description || '无'}</p>
      <p><strong>时间单位：</strong>${timeline.timeUnit || '无'}</p>
      <p><strong>时间范围：</strong>${timeline.startTime || ''} - ${timeline.endTime || ''}</p>
      <p><strong>时间点数量：</strong>${timeline.timePoints?.length || 0}</p>
      `,
      '时间线详情',
      {
        dangerouslyUseHTMLString: true,
        confirmButtonText: '确定'
      }
    )
  } catch (error) {
    ElMessage.error('获取时间线详情失败')
    console.error(error)
  }
}

// 删除时间线
const deleteTimeline = (row: resourceApi.TimelineDTO) => {
  ElMessageBox.confirm(
    `确定要删除时间线 "${row.name}" 吗？`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await resourceApi.deleteTimeline(NOVEL_ID, row.id!)
      ElMessage.success('删除成功')
      loadTimelines()
      if (selectedTimeline.value?.id === row.id) {
        selectedTimeline.value = null
      }
    } catch (error) {
      ElMessage.error('删除失败')
      console.error(error)
    }
  })
}

// 提交时间线表单
const submitTimelineForm = async () => {
  if (!timelineFormRef.value) return
  const valid = await timelineFormRef.value.validate()
  if (!valid) return

  try {
    if (editMode.value === 'add') {
      const created = await resourceApi.createTimeline(NOVEL_ID, timelineForm)
      timelines.value.push(created)
      ElMessage.success('添加成功')
    } else {
      const updated = await resourceApi.updateTimeline(NOVEL_ID, currentTimelineId.value!, timelineForm)
      const index = timelines.value.findIndex(t => t.id === currentTimelineId.value)
      if (index !== -1) {
        timelines.value[index] = updated
      }
      if (selectedTimeline.value?.id === currentTimelineId.value) {
        selectedTimeline.value = updated
      }
      ElMessage.success('更新成功')
    }
    showTimelineDialog.value = false
  } catch (error) {
    ElMessage.error(editMode.value === 'add' ? '添加失败' : '更新失败')
    console.error(error)
  }
}

// 重置时间线表单
const resetTimelineForm = () => {
  if (timelineFormRef.value) {
    timelineFormRef.value.resetFields()
  }
  Object.assign(timelineForm, {
    name: '',
    description: '',
    timeUnit: '年',
    startTime: '',
    endTime: '',
    timePoints: [],
    novelId: NOVEL_ID,
    status: 'ACTIVE',
    isMain: false,
    tags: []
  })
  currentTimelineId.value = null
}

// 添加时间点
const addTimePoint = () => {
  if (!selectedTimeline.value) {
    ElMessage.warning('请先选择一个时间线')
    return
  }
  timePointEditMode.value = 'add'
  resetTimePointForm()
  showTimePointDialog.value = true
}

// 编辑时间点
const editTimePoint = (point: resourceApi.TimePoint, index: number) => {
  if (!selectedTimeline.value) return
  timePointEditMode.value = 'edit'
  currentTimePointIndex.value = index
  Object.assign(timePointForm, {
    time: point.time,
    title: point.title,
    description: point.description || '',
    relatedEntities: point.relatedEntities ? [...point.relatedEntities] : []
  })
  showTimePointDialog.value = true
}

// 删除时间点
const deleteTimePoint = (index: number) => {
  if (!selectedTimeline.value) return
  ElMessageBox.confirm(
    '确定要删除这个时间点吗？',
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    if (selectedTimeline.value?.timePoints) {
      selectedTimeline.value.timePoints.splice(index, 1)
      // 更新到后端
      updateTimelineTimePoints()
    }
  })
}

// 添加相关实体
const addRelatedEntity = () => {
  timePointForm.relatedEntities.push({
    type: 'CHARACTER',
    id: 0,
    name: ''
  })
}

// 移除相关实体
const removeRelatedEntity = (index: number) => {
  timePointForm.relatedEntities.splice(index, 1)
}

// 提交时间点表单
const submitTimePointForm = async () => {
  if (!timePointFormRef.value) return
  const valid = await timePointFormRef.value.validate()
  if (!valid) return

  if (!selectedTimeline.value) {
    ElMessage.error('请先选择一个时间线')
    return
  }

  const timePoint: resourceApi.TimePoint = {
    time: timePointForm.time,
    title: timePointForm.title,
    description: timePointForm.description,
    relatedEntities: timePointForm.relatedEntities.filter(e => e.name.trim() !== '')
  }

  if (!selectedTimeline.value.timePoints) {
    selectedTimeline.value.timePoints = []
  }

  if (timePointEditMode.value === 'add') {
    selectedTimeline.value.timePoints.push(timePoint)
  } else if (currentTimePointIndex.value !== null) {
    selectedTimeline.value.timePoints[currentTimePointIndex.value] = timePoint
  }

  // 更新到后端
  await updateTimelineTimePoints()
  showTimePointDialog.value = false
}

// 更新时间线的时间点
const updateTimelineTimePoints = async () => {
  if (!selectedTimeline.value) return
  try {
    const updated = await resourceApi.updateTimeline(NOVEL_ID, selectedTimeline.value.id!, {
      ...selectedTimeline.value,
      novelId: NOVEL_ID
    })
    selectedTimeline.value.timePoints = updated.timePoints
    ElMessage.success('时间点更新成功')
  } catch (error) {
    ElMessage.error('更新时间点失败')
    console.error(error)
  }
}

// 重置时间点表单
const resetTimePointForm = () => {
  if (timePointFormRef.value) {
    timePointFormRef.value.resetFields()
  }
  Object.assign(timePointForm, {
    time: '',
    title: '',
    description: '',
    relatedEntities: []
  })
  currentTimePointIndex.value = null
}

// 初始化
onMounted(() => {
  loadTimelines()
})
</script>

<style scoped lang="scss">
.timeline-container {
  padding: 20px;

  .filter-card {
    .el-divider {
      margin: 15px 0;
    }
  }

  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: center;
  }

  .mt-3 {
    margin-top: 20px;
  }

  .empty-timeline {
    height: 300px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: #999;

    p {
      margin-top: 10px;
    }
  }

  .entity-item {
    display: flex;
    align-items: center;
    margin-bottom: 10px;
  }
}
</style>