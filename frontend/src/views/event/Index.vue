<template>
  <div class="event-container">
    <el-page-header @back="$router.push('/dashboard')" title="返回">
      <template #content>
        <h2>事件管理</h2>
      </template>
    </el-page-header>
    
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="filter-card">
          <template #header>
            <span>筛选条件</span>
          </template>
          <el-input placeholder="搜索事件" v-model="searchKeyword" clearable @change="loadEvents">
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-divider />
          <div class="filter-section">
            <h4>时间线</h4>
            <el-select v-model="filter.timelineId" placeholder="选择时间线" clearable @change="loadEvents">
              <el-option v-for="timeline in timelines" :key="timeline.id" :label="timeline.name" :value="timeline.id" />
            </el-select>
          </div>
          <el-divider />
          <div class="filter-section">
            <h4>事件类型</h4>
            <el-select v-model="filter.type" placeholder="选择类型" clearable @change="loadEvents">
              <el-option v-for="type in eventTypes" :key="type" :label="type" :value="type" />
            </el-select>
          </div>
          <el-divider />
          <el-button type="primary" @click="addEvent" style="width: 100%">添加事件</el-button>
          <el-button type="success" @click="loadEvents" style="width: 100%; margin-top: 10px">刷新列表</el-button>
        </el-card>
      </el-col>
      <el-col :span="18">
        <el-card>
          <template #header>
            <span>事件列表</span>
          </template>
          <el-table :data="events" border>
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="type" label="类型" width="120" />
            <el-table-column prop="timePoint" label="时间点" width="120" />
            <el-table-column prop="locationName" label="地点" />
            <el-table-column prop="isTurningPoint" label="转折点" width="80">
              <template #default="{ row }">
                <el-tag v-if="row.isTurningPoint" type="success">是</el-tag>
                <el-tag v-else type="info">否</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200">
              <template #default="{ row }">
                <el-button type="primary" size="small" text @click="editEvent(row)">编辑</el-button>
                <el-button type="info" size="small" text @click="viewEvent(row)">详情</el-button>
                <el-button type="danger" size="small" text @click="deleteEvent(row)">删除</el-button>
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
            <span>事件关系图</span>
            <el-button type="primary" size="small" style="float: right" @click="generateEventGraph">生成关系图</el-button>
          </template>
          <div class="relation-graph">
            <div class="graph-placeholder">
              <el-icon size="60"><PieChart /></el-icon>
              <p>事件关系图可视化区域</p>
              <el-button type="primary" size="small" @click="generateEventGraph">生成关系图</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 添加/编辑事件对话框 -->
    <el-dialog
      v-model="showEditDialog"
      :title="editMode === 'add' ? '添加事件' : '编辑事件'"
      width="800px"
      @close="resetForm"
    >
      <el-form :model="eventForm" label-width="100px" :rules="formRules" ref="formRef">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="标题" prop="title">
              <el-input v-model="eventForm.title" placeholder="请输入事件标题" />
            </el-form-item>
            <el-form-item label="类型">
              <el-select v-model="eventForm.type" placeholder="请选择类型">
                <el-option v-for="type in eventTypes" :key="type" :label="type" :value="type" />
              </el-select>
            </el-form-item>
            <el-form-item label="时间点">
              <el-input v-model="eventForm.timePoint" placeholder="例如：元年春" />
            </el-form-item>
            <el-form-item label="持续时间">
              <el-input v-model="eventForm.duration" placeholder="例如：三个月" />
            </el-form-item>
            <el-form-item label="时间线">
              <el-select v-model="eventForm.timelineId" placeholder="选择时间线">
                <el-option v-for="timeline in timelines" :key="timeline.id" :label="timeline.name" :value="timeline.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="地点">
              <el-select v-model="eventForm.locationId" placeholder="选择地点" filterable>
                <el-option v-for="location in locations" :key="location.id" :label="location.name" :value="location.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="转折点">
              <el-switch v-model="eventForm.isTurningPoint" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="参与者">
              <div v-for="(participant, index) in eventForm.participants" :key="index" class="participant-item">
                <el-select v-model="participant.characterId" placeholder="选择人物" style="width: 150px; margin-right: 10px" filterable>
                  <el-option v-for="character in characters" :key="character.id" :label="character.name" :value="character.id" />
                </el-select>
                <el-input v-model="participant.role" placeholder="角色" style="width: 120px; margin-right: 10px" />
                <el-button type="danger" size="small" text @click="removeParticipant(index)">删除</el-button>
              </div>
              <el-button type="primary" size="small" @click="addParticipant">添加参与者</el-button>
            </el-form-item>
            <el-form-item label="标签">
              <el-select v-model="eventForm.tags" multiple placeholder="选择标签" filterable allow-create>
                <el-option v-for="tag in availableTags" :key="tag" :label="tag" :value="tag" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="描述">
              <el-input v-model="eventForm.description" type="textarea" :rows="3" placeholder="请输入事件描述" />
            </el-form-item>
            <el-form-item label="起因">
              <el-input v-model="eventForm.cause" type="textarea" :rows="2" placeholder="请输入事件起因" />
            </el-form-item>
            <el-form-item label="过程">
              <el-input v-model="eventForm.process" type="textarea" :rows="2" placeholder="请输入事件过程" />
            </el-form-item>
            <el-form-item label="结果">
              <el-input v-model="eventForm.result" type="textarea" :rows="2" placeholder="请输入事件结果" />
            </el-form-item>
            <el-form-item label="影响">
              <el-input v-model="eventForm.impact" type="textarea" :rows="2" placeholder="请输入事件影响" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="submitEventForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Search, PieChart } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import * as resourceApi from '@/api/resource'

// 当前小说ID（暂时硬编码，后续从路由或store获取）
const NOVEL_ID = 1

// 搜索与筛选
const searchKeyword = ref('')
const filter = reactive({
  timelineId: null as number | null,
  type: ''
})

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 数据
const events = ref<resourceApi.EventDTO[]>([])
const timelines = ref<resourceApi.TimelineDTO[]>([])
const locations = ref<resourceApi.LocationDTO[]>([])
const characters = ref<resourceApi.CharacterDTO[]>([])
const eventTypes = ref(['战斗', '对话', '探索', '发现', '背叛', '联合', '死亡', '诞生', '其他'])
const availableTags = ref<string[]>(['重要事件', '转折点', '伏笔', '高潮', '结局'])

// 对话框状态
const showEditDialog = ref(false)
const editMode = ref<'add' | 'edit'>('add')
const currentEventId = ref<number | null>(null)

// 表单
const eventForm = reactive<resourceApi.CreateEventRequest>({
  title: '',
  type: '',
  description: '',
  timePoint: '',
  duration: '',
  locationId: undefined,
  locationName: '',
  participants: [],
  cause: '',
  process: '',
  result: '',
  impact: '',
  isTurningPoint: false,
  timelineId: undefined,
  novelId: NOVEL_ID,
  status: 'ACTIVE',
  tags: []
})

const formRef = ref<FormInstance>()
const formRules: FormRules = {
  title: [
    { required: true, message: '请输入事件标题', trigger: 'blur' }
  ]
}

// 加载事件列表
const loadEvents = async () => {
  try {
    const response = await resourceApi.listEvents(
      NOVEL_ID,
      filter.timelineId || undefined,
      searchKeyword.value || undefined
    )
    events.value = response
    pagination.total = events.value.length
  } catch (error) {
    ElMessage.error('加载事件列表失败')
    console.error(error)
  }
}

// 加载时间线列表
const loadTimelines = async () => {
  try {
    const response = await resourceApi.listTimelines(NOVEL_ID)
    timelines.value = response
  } catch (error) {
    console.error('加载时间线列表失败', error)
  }
}

// 加载地点列表
const loadLocations = async () => {
  try {
    const response = await resourceApi.listLocations(NOVEL_ID)
    locations.value = response
  } catch (error) {
    console.error('加载地点列表失败', error)
  }
}

// 加载人物列表
const loadCharacters = async () => {
  try {
    const response = await resourceApi.listCharacters(NOVEL_ID)
    characters.value = response
  } catch (error) {
    console.error('加载人物列表失败', error)
  }
}

// 分页大小变化
const handleSizeChange = (size: number) => {
  pagination.size = size
  loadEvents()
}

// 当前页变化
const handleCurrentChange = (page: number) => {
  pagination.current = page
  loadEvents()
}

// 添加事件
const addEvent = () => {
  editMode.value = 'add'
  resetForm()
  showEditDialog.value = true
}

// 编辑事件
const editEvent = (row: resourceApi.EventDTO) => {
  editMode.value = 'edit'
  currentEventId.value = row.id!
  Object.assign(eventForm, {
    title: row.title,
    type: row.type || '',
    description: row.description || '',
    timePoint: row.timePoint || '',
    duration: row.duration || '',
    locationId: row.locationId || undefined,
    locationName: row.locationName || '',
    participants: row.participants || [],
    cause: row.cause || '',
    process: row.process || '',
    result: row.result || '',
    impact: row.impact || '',
    isTurningPoint: row.isTurningPoint || false,
    timelineId: row.timelineId || undefined,
    novelId: row.novelId,
    status: row.status || 'ACTIVE',
    tags: row.tags || []
  })
  showEditDialog.value = true
}

// 查看事件详情
const viewEvent = async (row: resourceApi.EventDTO) => {
  try {
    const event = await resourceApi.getEvent(NOVEL_ID, row.id!)
    ElMessageBox.alert(
      `
      <h3>${event.title}</h3>
      <p><strong>类型：</strong>${event.type || '无'}</p>
      <p><strong>时间点：</strong>${event.timePoint || '无'}</p>
      <p><strong>地点：</strong>${event.locationName || '无'}</p>
      <p><strong>描述：</strong>${event.description || '无'}</p>
      <p><strong>参与者：</strong>${event.participants?.map(p => p.characterName).join(', ') || '无'}</p>
      `,
      '事件详情',
      {
        dangerouslyUseHTMLString: true,
        confirmButtonText: '确定'
      }
    )
  } catch (error) {
    ElMessage.error('获取事件详情失败')
    console.error(error)
  }
}

// 删除事件
const deleteEvent = (row: resourceApi.EventDTO) => {
  ElMessageBox.confirm(
    `确定要删除事件 "${row.title}" 吗？`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await resourceApi.deleteEvent(NOVEL_ID, row.id!)
      ElMessage.success('删除成功')
      loadEvents()
    } catch (error) {
      ElMessage.error('删除失败')
      console.error(error)
    }
  })
}

// 添加参与者
const addParticipant = () => {
  eventForm.participants.push({
    characterId: 0,
    characterName: '',
    role: ''
  })
}

// 移除参与者
const removeParticipant = (index: number) => {
  eventForm.participants.splice(index, 1)
}

// 提交事件表单
const submitEventForm = async () => {
  if (!formRef.value) return
  const valid = await formRef.value.validate()
  if (!valid) return

  try {
    if (editMode.value === 'add') {
      await resourceApi.createEvent(NOVEL_ID, eventForm)
      ElMessage.success('添加成功')
    } else {
      await resourceApi.updateEvent(NOVEL_ID, currentEventId.value!, eventForm)
      ElMessage.success('更新成功')
    }
    showEditDialog.value = false
    loadEvents()
  } catch (error) {
    ElMessage.error(editMode.value === 'add' ? '添加失败' : '更新失败')
    console.error(error)
  }
}

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  Object.assign(eventForm, {
    title: '',
    type: '',
    description: '',
    timePoint: '',
    duration: '',
    locationId: undefined,
    locationName: '',
    participants: [],
    cause: '',
    process: '',
    result: '',
    impact: '',
    isTurningPoint: false,
    timelineId: undefined,
    novelId: NOVEL_ID,
    status: 'ACTIVE',
    tags: []
  })
  currentEventId.value = null
}

// 生成事件关系图
const generateEventGraph = () => {
  ElMessage.info('事件关系图功能开发中')
}

// 初始化
onMounted(() => {
  loadEvents()
  loadTimelines()
  loadLocations()
  loadCharacters()
})
</script>

<style scoped lang="scss">
.event-container {
  padding: 20px;

  .filter-card {
    .el-divider {
      margin: 15px 0;
    }

    .filter-section {
      margin-bottom: 15px;

      h4 {
        margin-bottom: 10px;
        font-size: 14px;
        color: #666;
      }
    }
  }

  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: center;
  }

  .relation-graph {
    height: 400px;
    background-color: #f9f9f9;
    border-radius: 4px;
    display: flex;
    align-items: center;
    justify-content: center;

    .graph-placeholder {
      text-align: center;
      color: #999;

      p {
        margin: 10px 0;
      }
    }
  }

  .mt-3 {
    margin-top: 20px;
  }

  .participant-item {
    display: flex;
    align-items: center;
    margin-bottom: 10px;
  }
}
</style>