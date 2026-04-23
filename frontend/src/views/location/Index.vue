<template>
  <div class="location-container">
    <el-page-header @back="$router.push('/dashboard')" title="返回">
      <template #content>
        <h2>地点设定管理</h2>
      </template>
    </el-page-header>
    
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="filter-card">
          <template #header>
            <span>筛选条件</span>
          </template>
          <el-input placeholder="搜索地点" v-model="searchKeyword" clearable @change="loadLocations">
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-divider />
          <div class="filter-section">
            <h4>类型</h4>
            <el-select v-model="filter.type" placeholder="选择类型" clearable @change="loadLocations">
              <el-option v-for="type in locationTypes" :key="type" :label="type" :value="type" />
            </el-select>
          </div>
          <el-divider />
          <div class="filter-section">
            <h4>标签</h4>
            <el-select v-model="filter.tags" multiple placeholder="选择标签" clearable @change="loadLocations">
              <el-option v-for="tag in availableTags" :key="tag" :label="tag" :value="tag" />
            </el-select>
          </div>
          <el-divider />
          <el-button type="primary" @click="addLocation" style="width: 100%">添加地点</el-button>
          <el-button type="success" @click="loadLocations" style="width: 100%; margin-top: 10px">刷新列表</el-button>
        </el-card>
      </el-col>
      <el-col :span="18">
        <el-card>
          <template #header>
            <span>地点列表</span>
            <el-button type="primary" size="small" style="float: right" @click="showBatchImport = true">批量导入</el-button>
          </template>
          <el-table :data="locations" border @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="55" />
            <el-table-column prop="name" label="名称" />
            <el-table-column prop="type" label="类型" width="120" />
            <el-table-column prop="geography" label="地理" />
            <el-table-column prop="climate" label="气候" width="100" />
            <el-table-column label="坐标" width="120">
              <template #default="{ row }">
                <el-tag v-if="row.coordinates && (row.coordinates.lat || row.coordinates.x)" size="small" type="success">
                  <el-icon><Location /></el-icon> 已设置
                </el-tag>
                <el-tag v-else size="small" type="info">未设置</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="tags" label="标签">
              <template #default="{ row }">
                <el-tag v-for="tag in row.tags" :key="tag" size="small" style="margin-right: 5px">{{ tag }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200">
              <template #default="{ row }">
                <el-button type="primary" size="small" text @click="editLocation(row)">编辑</el-button>
                <el-button type="info" size="small" text @click="viewLocation(row)">详情</el-button>
                <el-button type="success" size="small" text @click="viewOnMap(row)" v-if="row.coordinates && (row.coordinates.lat || row.coordinates.x)">
                  <el-icon><MapLocation /></el-icon>
                </el-button>
                <el-button type="danger" size="small" text @click="deleteLocation(row)">删除</el-button>
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
            <span>地点连接关系</span>
            <el-button type="primary" size="small" style="float: right" @click="generateConnectionGraph">生成连接图</el-button>
          </template>
          <div class="relation-graph">
            <div class="graph-placeholder">
              <el-icon size="60"><PieChart /></el-icon>
              <p>地点连接关系图可视化区域</p>
              <el-button type="primary" size="small" @click="generateConnectionGraph">生成连接图</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 添加/编辑地点对话框 -->
    <el-dialog
      v-model="showEditDialog"
      :title="editMode === 'add' ? '添加地点' : '编辑地点'"
      width="800px"
      @close="resetForm"
    >
      <el-form :model="locationForm" label-width="100px" :rules="formRules" ref="formRef">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="名称" prop="name">
              <el-input v-model="locationForm.name" placeholder="请输入地点名称" />
            </el-form-item>
            <el-form-item label="别名">
              <el-input v-model="locationForm.alias" placeholder="请输入地点别名" />
            </el-form-item>
            <el-form-item label="类型">
              <el-select v-model="locationForm.type" placeholder="请选择类型">
                <el-option v-for="type in locationTypes" :key="type" :label="type" :value="type" />
              </el-select>
            </el-form-item>
            <el-form-item label="地理">
              <el-input v-model="locationForm.geography" type="textarea" placeholder="请输入地理描述" />
            </el-form-item>
            <el-form-item label="气候">
              <el-input v-model="locationForm.climate" placeholder="请输入气候描述" />
            </el-form-item>
            <el-form-item label="建筑">
              <el-input v-model="locationForm.architecture" type="textarea" placeholder="请输入建筑风格描述" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="文化">
              <el-input v-model="locationForm.culture" type="textarea" placeholder="请输入文化描述" />
            </el-form-item>
            <el-form-item label="历史">
              <el-input v-model="locationForm.history" type="textarea" placeholder="请输入历史背景" />
            </el-form-item>
            <el-form-item label="重要场所">
              <el-input v-model="locationForm.importantPlaces" type="textarea" placeholder="请输入重要场所描述" />
            </el-form-item>
            <el-form-item label="政治">
              <el-input v-model="locationForm.politics" type="textarea" placeholder="请输入政治状况" />
            </el-form-item>
            <el-form-item label="经济">
              <el-input v-model="locationForm.economy" type="textarea" placeholder="请输入经济状况" />
            </el-form-item>
            <el-form-item label="坐标">
              <coordinate-picker v-model="locationForm.coordinates" />
            </el-form-item>
            <el-form-item label="标签">
              <el-select v-model="locationForm.tags" multiple placeholder="选择标签" filterable allow-create>
                <el-option v-for="tag in availableTags" :key="tag" :label="tag" :value="tag" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="submitLocationForm">保存</el-button>
      </template>
    </el-dialog>

    <!-- 批量导入对话框 -->
    <el-dialog v-model="showBatchImport" title="批量导入地点" width="600px">
      <el-alert type="info" title="支持JSON格式批量导入" show-icon style="margin-bottom: 20px" />
      <el-input
        v-model="batchImportData"
        type="textarea"
        :rows="10"
        placeholder='请输入JSON格式数据，例如：[{"name": "青龙山", "type": "山脉"}]'
      />
      <template #footer>
        <el-button @click="showBatchImport = false">取消</el-button>
        <el-button type="primary" @click="handleBatchImport">导入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Search, PieChart, Location, MapLocation } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import * as resourceApi from '@/api/resource'
import CoordinatePicker from '@/components/CoordinatePicker.vue'

// 当前小说ID（暂时硬编码，后续从路由或store获取）
const NOVEL_ID = 1

const route = useRoute()
const router = useRouter()

// 搜索与筛选
const searchKeyword = ref('')
const filter = reactive({
  type: '',
  tags: [] as string[]
})

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 数据
const locations = ref<resourceApi.LocationDTO[]>([])
const selectedLocations = ref<resourceApi.LocationDTO[]>([])
const locationTypes = ref(['城市', '山脉', '森林', '河流', '海洋', '城堡', '村庄', '神殿', '迷宫', '其他'])
const availableTags = ref<string[]>(['重要地点', '安全区', '危险区', '隐藏地点', '剧情关键'])

// 对话框状态
const showEditDialog = ref(false)
const showBatchImport = ref(false)
const editMode = ref<'add' | 'edit'>('add')
const currentLocationId = ref<number | null>(null)

// 表单
const locationForm = reactive<resourceApi.CreateLocationRequest>({
  name: '',
  alias: '',
  type: '',
  geography: '',
  climate: '',
  architecture: '',
  culture: '',
  history: '',
  importantPlaces: '',
  politics: '',
  economy: '',
  connections: [],
  coordinates: {},
  novelId: NOVEL_ID,
  status: 'ACTIVE',
  tags: []
})

const formRef = ref<FormInstance>()
const formRules: FormRules = {
  name: [
    { required: true, message: '请输入地点名称', trigger: 'blur' }
  ]
}

// 批量导入数据
const batchImportData = ref('')

// 加载地点列表
const loadLocations = async () => {
  try {
    const response = await resourceApi.listLocations(
      NOVEL_ID,
      searchKeyword.value || undefined,
      filter.tags.length > 0 ? filter.tags : undefined
    )
    locations.value = response
    // 更新标签列表
    updateTagList()
    // 更新分页总数
    pagination.total = locations.value.length
  } catch (error) {
    ElMessage.error('加载地点列表失败')
    console.error(error)
  }
}

// 更新标签列表（从现有数据中提取）
const updateTagList = () => {
  const tagSet = new Set<string>()
  locations.value.forEach(loc => {
    if (loc.tags) {
      loc.tags.forEach(tag => tagSet.add(tag))
    }
  })
  availableTags.value = Array.from(tagSet)
}

// 表格选择变化
const handleSelectionChange = (selection: resourceApi.LocationDTO[]) => {
  selectedLocations.value = selection
}

// 分页大小变化
const handleSizeChange = (size: number) => {
  pagination.size = size
  loadLocations()
}

// 当前页变化
const handleCurrentChange = (page: number) => {
  pagination.current = page
  loadLocations()
}

// 添加地点
const addLocation = () => {
  editMode.value = 'add'
  resetForm()
  showEditDialog.value = true
}

// 编辑地点
const editLocation = (row: resourceApi.LocationDTO) => {
  editMode.value = 'edit'
  currentLocationId.value = row.id!
  Object.assign(locationForm, {
    name: row.name,
    alias: row.alias || '',
    type: row.type || '',
    geography: row.geography || '',
    climate: row.climate || '',
    architecture: row.architecture || '',
    culture: row.culture || '',
    history: row.history || '',
    importantPlaces: row.importantPlaces || '',
    politics: row.politics || '',
    economy: row.economy || '',
    connections: row.connections || [],
    coordinates: row.coordinates || {},
    novelId: row.novelId,
    status: row.status || 'ACTIVE',
    tags: row.tags || []
  })
  showEditDialog.value = true
}

// 查看地点详情
const viewLocation = async (row: resourceApi.LocationDTO) => {
  try {
    const location = await resourceApi.getLocation(NOVEL_ID, row.id!)
    ElMessageBox.alert(
      `
      <h3>${location.name}</h3>
      <p><strong>类型：</strong>${location.type || '无'}</p>
      <p><strong>地理：</strong>${location.geography || '无'}</p>
      <p><strong>气候：</strong>${location.climate || '无'}</p>
      <p><strong>文化：</strong>${location.culture || '无'}</p>
      <p><strong>历史：</strong>${location.history || '无'}</p>
      `,
      '地点详情',
      {
        dangerouslyUseHTMLString: true,
        confirmButtonText: '确定'
      }
    )
  } catch (error) {
    ElMessage.error('获取地点详情失败')
    console.error(error)
  }
}

// 在地图上查看地点
const viewOnMap = (row: resourceApi.LocationDTO) => {
  router.push({
    path: '/world-map',
    query: {
      focus: 'location',
      locationId: row.id,
      lat: row.coordinates?.lat || row.coordinates?.y,
      lng: row.coordinates?.lng || row.coordinates?.x
    }
  })
}

// 删除地点
const deleteLocation = (row: resourceApi.LocationDTO) => {
  ElMessageBox.confirm(
    `确定要删除地点 "${row.name}" 吗？`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await resourceApi.deleteLocation(NOVEL_ID, row.id!)
      ElMessage.success('删除成功')
      loadLocations()
    } catch (error) {
      ElMessage.error('删除失败')
      console.error(error)
    }
  })
}

// 提交地点表单
const submitLocationForm = async () => {
  if (!formRef.value) return
  const valid = await formRef.value.validate()
  if (!valid) return

  try {
    if (editMode.value === 'add') {
      await resourceApi.createLocation(NOVEL_ID, locationForm)
      ElMessage.success('添加成功')
    } else {
      await resourceApi.updateLocation(NOVEL_ID, currentLocationId.value!, locationForm)
      ElMessage.success('更新成功')
    }
    showEditDialog.value = false
    loadLocations()
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
  Object.assign(locationForm, {
    name: '',
    alias: '',
    type: '',
    geography: '',
    climate: '',
    architecture: '',
    culture: '',
    history: '',
    importantPlaces: '',
    politics: '',
    economy: '',
    connections: [],
    coordinates: {},
    novelId: NOVEL_ID,
    status: 'ACTIVE',
    tags: []
  })
  currentLocationId.value = null
  
  // 清除URL中的edit查询参数
  if (route.query.edit) {
    router.replace({ query: {} })
  }
}

// 批量导入
const handleBatchImport = async () => {
  try {
    const data = JSON.parse(batchImportData.value)
    if (!Array.isArray(data)) {
      ElMessage.error('数据格式错误，请输入JSON数组')
      return
    }
    await resourceApi.batchCreateLocations(NOVEL_ID, data)
    ElMessage.success('批量导入成功')
    showBatchImport.value = false
    batchImportData.value = ''
    loadLocations()
  } catch (error) {
    ElMessage.error('批量导入失败，请检查数据格式')
    console.error(error)
  }
}

// 生成连接关系图
const generateConnectionGraph = () => {
  ElMessage.info('连接关系图功能开发中')
}

// 初始化
onMounted(async () => {
  await loadLocations()
  
  // 检查URL查询参数，如果有edit参数则打开编辑对话框
  const editId = route.query.edit
  if (editId) {
    const id = parseInt(editId as string)
    if (!isNaN(id)) {
      const location = locations.value.find(loc => loc.id === id)
      if (location) {
        // 等待下一帧确保DOM已更新
        setTimeout(() => {
          editLocation(location)
        }, 100)
      } else {
        ElMessage.warning(`未找到ID为${id}的地点`)
      }
    }
  }
})
</script>

<style scoped lang="scss">
.location-container {
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
}
</style>