<template>
  <div class="item-container">
    <el-page-header @back="$router.push('/dashboard')" title="返回">
      <template #content>
        <h2>物品管理</h2>
      </template>
    </el-page-header>
    
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="filter-card">
          <template #header>
            <span>筛选条件</span>
          </template>
          <el-input placeholder="搜索物品" v-model="searchKeyword" clearable @change="loadItems">
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-divider />
          <div class="filter-section">
            <h4>稀有度</h4>
            <el-select v-model="filter.rarity" placeholder="选择稀有度" clearable @change="loadItems">
              <el-option v-for="rarity in rarityOptions" :key="rarity" :label="rarity" :value="rarity" />
            </el-select>
          </div>
          <el-divider />
          <div class="filter-section">
            <h4>标签</h4>
            <el-select v-model="filter.tags" multiple placeholder="选择标签" clearable @change="loadItems">
              <el-option v-for="tag in availableTags" :key="tag" :label="tag" :value="tag" />
            </el-select>
          </div>
          <el-divider />
          <el-button type="primary" @click="addItem" style="width: 100%">添加物品</el-button>
          <el-button type="success" @click="loadItems" style="width: 100%; margin-top: 10px">刷新列表</el-button>
        </el-card>
      </el-col>
      <el-col :span="18">
        <el-card>
          <template #header>
            <span>物品列表</span>
          </template>
          <el-table :data="items" border>
            <el-table-column prop="name" label="名称" />
            <el-table-column prop="type" label="类型" width="120" />
            <el-table-column prop="rarity" label="稀有度" width="100" />
            <el-table-column prop="currentHolderName" label="当前持有者" />
            <el-table-column prop="locationName" label="所在地点" />
            <el-table-column prop="tags" label="标签">
              <template #default="{ row }">
                <el-tag v-for="tag in row.tags" :key="tag" size="small" style="margin-right: 5px">{{ tag }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200">
              <template #default="{ row }">
                <el-button type="primary" size="small" text @click="editItem(row)">编辑</el-button>
                <el-button type="info" size="small" text @click="viewItem(row)">详情</el-button>
                <el-button type="danger" size="small" text @click="deleteItem(row)">删除</el-button>
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
            <span>物品流转历史</span>
          </template>
          <div v-if="selectedItem">
            <el-timeline>
              <el-timeline-item
                v-for="(holder, index) in selectedItem.previousHolders"
                :key="index"
                :timestamp="holder.period"
                placement="top"
              >
                <el-card>
                  <h4>{{ holder.characterName }}</h4>
                  <p>持有时期: {{ holder.period }}</p>
                </el-card>
              </el-timeline-item>
            </el-timeline>
          </div>
          <div v-else class="empty-history">
            <el-icon size="60"><Clock /></el-icon>
            <p>请先选择一个物品查看流转历史</p>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 添加/编辑物品对话框 -->
    <el-dialog
      v-model="showEditDialog"
      :title="editMode === 'add' ? '添加物品' : '编辑物品'"
      width="800px"
      @close="resetForm"
    >
      <el-form :model="itemForm" label-width="100px" :rules="formRules" ref="formRef">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="名称" prop="name">
              <el-input v-model="itemForm.name" placeholder="请输入物品名称" />
            </el-form-item>
            <el-form-item label="别名">
              <el-input v-model="itemForm.alias" placeholder="请输入物品别名" />
            </el-form-item>
            <el-form-item label="类型">
              <el-select v-model="itemForm.type" placeholder="请选择类型">
                <el-option v-for="type in itemTypes" :key="type" :label="type" :value="type" />
              </el-select>
            </el-form-item>
            <el-form-item label="稀有度">
              <el-select v-model="itemForm.rarity" placeholder="请选择稀有度">
                <el-option v-for="rarity in rarityOptions" :key="rarity" :label="rarity" :value="rarity" />
              </el-select>
            </el-form-item>
            <el-form-item label="外观">
              <el-input v-model="itemForm.appearance" type="textarea" placeholder="请输入物品外观描述" />
            </el-form-item>
            <el-form-item label="能力">
              <el-input v-model="itemForm.abilities" type="textarea" placeholder="请输入物品能力描述" />
            </el-form-item>
            <el-form-item label="用法">
              <el-input v-model="itemForm.usage" type="textarea" placeholder="请输入物品使用方法" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="获取方式">
              <el-input v-model="itemForm.acquisition" type="textarea" placeholder="请输入物品获取方式" />
            </el-form-item>
            <el-form-item label="历史">
              <el-input v-model="itemForm.history" type="textarea" placeholder="请输入物品历史背景" />
            </el-form-item>
            <el-form-item label="当前持有者">
              <el-select v-model="itemForm.currentHolderId" placeholder="选择持有者" filterable>
                <el-option v-for="character in characters" :key="character.id" :label="character.name" :value="character.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="所在地点">
              <el-select v-model="itemForm.locationId" placeholder="选择地点" filterable>
                <el-option v-for="location in locations" :key="location.id" :label="location.name" :value="location.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="相关事件">
              <el-select v-model="itemForm.eventId" placeholder="选择相关事件" filterable>
                <el-option v-for="event in events" :key="event.id" :label="event.title" :value="event.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="标签">
              <el-select v-model="itemForm.tags" multiple placeholder="选择标签" filterable allow-create>
                <el-option v-for="tag in availableTags" :key="tag" :label="tag" :value="tag" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="描述">
              <el-input v-model="itemForm.description" type="textarea" :rows="3" placeholder="请输入物品详细描述" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="submitItemForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Search, Clock } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import * as resourceApi from '@/api/resource'

// 当前小说ID（暂时硬编码，后续从路由或store获取）
const NOVEL_ID = 1

// 搜索与筛选
const searchKeyword = ref('')
const filter = reactive({
  rarity: '',
  tags: [] as string[]
})

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 数据
const items = ref<resourceApi.ItemResourceDTO[]>([])
const selectedItem = ref<resourceApi.ItemResourceDTO | null>(null)
const characters = ref<resourceApi.CharacterDTO[]>([])
const locations = ref<resourceApi.LocationDTO[]>([])
const events = ref<resourceApi.EventDTO[]>([])
const itemTypes = ref(['武器', '防具', '饰品', '法器', '药材', '书籍', '宝物', '工具', '其他'])
const rarityOptions = ref(['普通', '稀有', '史诗', '传说', '神器'])
const availableTags = ref<string[]>(['重要物品', '剧情关键', '可装备', '消耗品', '任务物品'])

// 对话框状态
const showEditDialog = ref(false)
const editMode = ref<'add' | 'edit'>('add')
const currentItemId = ref<number | null>(null)

// 表单
const itemForm = reactive<resourceApi.CreateItemResourceRequest>({
  name: '',
  alias: '',
  type: '',
  rarity: '',
  description: '',
  appearance: '',
  abilities: '',
  usage: '',
  acquisition: '',
  history: '',
  currentHolderId: undefined,
  currentHolderName: '',
  previousHolders: [],
  locationId: undefined,
  locationName: '',
  eventId: undefined,
  eventTitle: '',
  novelId: NOVEL_ID,
  status: 'ACTIVE',
  tags: []
})

const formRef = ref<FormInstance>()
const formRules: FormRules = {
  name: [
    { required: true, message: '请输入物品名称', trigger: 'blur' }
  ]
}

// 加载物品列表
const loadItems = async () => {
  try {
    const response = await resourceApi.listItemResources(
      NOVEL_ID,
      searchKeyword.value || undefined,
      filter.tags.length > 0 ? filter.tags : undefined
    )
    items.value = response
    pagination.total = items.value.length
  } catch (error) {
    ElMessage.error('加载物品列表失败')
    console.error(error)
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

// 加载地点列表
const loadLocations = async () => {
  try {
    const response = await resourceApi.listLocations(NOVEL_ID)
    locations.value = response
  } catch (error) {
    console.error('加载地点列表失败', error)
  }
}

// 加载事件列表
const loadEvents = async () => {
  try {
    const response = await resourceApi.listEvents(NOVEL_ID)
    events.value = response
  } catch (error) {
    console.error('加载事件列表失败', error)
  }
}

// 分页大小变化
const handleSizeChange = (size: number) => {
  pagination.size = size
  loadItems()
}

// 当前页变化
const handleCurrentChange = (page: number) => {
  pagination.current = page
  loadItems()
}

// 添加物品
const addItem = () => {
  editMode.value = 'add'
  resetForm()
  showEditDialog.value = true
}

// 编辑物品
const editItem = (row: resourceApi.ItemResourceDTO) => {
  editMode.value = 'edit'
  currentItemId.value = row.id!
  selectedItem.value = row
  Object.assign(itemForm, {
    name: row.name,
    alias: row.alias || '',
    type: row.type || '',
    rarity: row.rarity || '',
    description: row.description || '',
    appearance: row.appearance || '',
    abilities: row.abilities || '',
    usage: row.usage || '',
    acquisition: row.acquisition || '',
    history: row.history || '',
    currentHolderId: row.currentHolderId || undefined,
    currentHolderName: row.currentHolderName || '',
    previousHolders: row.previousHolders || [],
    locationId: row.locationId || undefined,
    locationName: row.locationName || '',
    eventId: row.eventId || undefined,
    eventTitle: row.eventTitle || '',
    novelId: row.novelId,
    status: row.status || 'ACTIVE',
    tags: row.tags || []
  })
  showEditDialog.value = true
}

// 查看物品详情
const viewItem = async (row: resourceApi.ItemResourceDTO) => {
  try {
    const item = await resourceApi.getItemResource(NOVEL_ID, row.id!)
    selectedItem.value = item
    ElMessageBox.alert(
      `
      <h3>${item.name}</h3>
      <p><strong>类型：</strong>${item.type || '无'}</p>
      <p><strong>稀有度：</strong>${item.rarity || '无'}</p>
      <p><strong>描述：</strong>${item.description || '无'}</p>
      <p><strong>能力：</strong>${item.abilities || '无'}</p>
      <p><strong>当前持有者：</strong>${item.currentHolderName || '无'}</p>
      <p><strong>所在地点：</strong>${item.locationName || '无'}</p>
      `,
      '物品详情',
      {
        dangerouslyUseHTMLString: true,
        confirmButtonText: '确定'
      }
    )
  } catch (error) {
    ElMessage.error('获取物品详情失败')
    console.error(error)
  }
}

// 删除物品
const deleteItem = (row: resourceApi.ItemResourceDTO) => {
  ElMessageBox.confirm(
    `确定要删除物品 "${row.name}" 吗？`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await resourceApi.deleteItemResource(NOVEL_ID, row.id!)
      ElMessage.success('删除成功')
      loadItems()
      if (selectedItem.value?.id === row.id) {
        selectedItem.value = null
      }
    } catch (error) {
      ElMessage.error('删除失败')
      console.error(error)
    }
  })
}

// 提交物品表单
const submitItemForm = async () => {
  if (!formRef.value) return
  const valid = await formRef.value.validate()
  if (!valid) return

  try {
    if (editMode.value === 'add') {
      await resourceApi.createItemResource(NOVEL_ID, itemForm)
      ElMessage.success('添加成功')
    } else {
      await resourceApi.updateItemResource(NOVEL_ID, currentItemId.value!, itemForm)
      ElMessage.success('更新成功')
    }
    showEditDialog.value = false
    loadItems()
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
  Object.assign(itemForm, {
    name: '',
    alias: '',
    type: '',
    rarity: '',
    description: '',
    appearance: '',
    abilities: '',
    usage: '',
    acquisition: '',
    history: '',
    currentHolderId: undefined,
    currentHolderName: '',
    previousHolders: [],
    locationId: undefined,
    locationName: '',
    eventId: undefined,
    eventTitle: '',
    novelId: NOVEL_ID,
    status: 'ACTIVE',
    tags: []
  })
  currentItemId.value = null
}

// 初始化
onMounted(() => {
  loadItems()
  loadCharacters()
  loadLocations()
  loadEvents()
})
</script>

<style scoped lang="scss">
.item-container {
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

  .mt-3 {
    margin-top: 20px;
  }

  .empty-history {
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
}
</style>