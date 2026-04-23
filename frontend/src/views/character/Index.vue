<template>
  <div class="character-container">
    <el-page-header @back="$router.push('/dashboard')" title="返回">
      <template #content>
        <h2>人物设定管理</h2>
      </template>
    </el-page-header>
    
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="filter-card">
          <template #header>
            <span>筛选条件</span>
          </template>
          <el-input placeholder="搜索人物" v-model="searchKeyword" clearable @change="loadCharacters">
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-divider />
          <div class="filter-section">
            <h4>性别</h4>
            <el-checkbox-group v-model="filter.gender">
              <el-checkbox label="男">男性</el-checkbox>
              <el-checkbox label="女">女性</el-checkbox>
            </el-checkbox-group>
          </div>
          <el-divider />
          <div class="filter-section">
            <h4>阵营</h4>
            <el-input placeholder="阵营筛选" v-model="filter.faction" clearable @change="loadCharacters" />
          </div>
          <el-divider />
          <div class="filter-section">
            <h4>标签</h4>
            <el-select v-model="filter.tags" multiple placeholder="选择标签" clearable @change="loadCharacters">
              <el-option v-for="tag in availableTags" :key="tag" :label="tag" :value="tag" />
            </el-select>
          </div>
          <el-divider />
          <el-button type="primary" @click="showAddDialog = true" style="width: 100%">添加人物</el-button>
          <el-button type="success" @click="loadCharacters" style="width: 100%; margin-top: 10px">刷新列表</el-button>
        </el-card>
      </el-col>
      <el-col :span="18">
        <el-card>
          <template #header>
            <span>人物列表</span>
            <div style="float: right">
              <el-button type="primary" size="small" @click="showBatchImport = true">批量导入</el-button>
              <el-button type="success" size="small" @click="enhanceSelectedCharacters" :disabled="selectedCharacters.length === 0">批量AI增强</el-button>
            </div>
          </template>
          <el-table :data="characters" border @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="55" />
            <el-table-column prop="name" label="姓名" />
            <el-table-column prop="gender" label="性别" width="80" />
            <el-table-column prop="age" label="年龄" width="80" />
            <el-table-column prop="faction" label="阵营" />
            <el-table-column prop="identity" label="身份" />
            <el-table-column prop="tags" label="标签">
              <template #default="{ row }">
                <el-tag v-for="tag in row.tags" :key="tag" size="small" style="margin-right: 5px">{{ tag }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="250">
              <template #default="{ row }">
                <el-button type="primary" size="small" text @click="editCharacter(row)">编辑</el-button>
                <el-button type="warning" size="small" text @click="enhanceCharacter(row)">AI增强</el-button>
                <el-button type="info" size="small" text @click="viewCharacter(row)">详情</el-button>
                <el-button type="success" size="small" text @click="viewOnMap(row)">
                  <el-icon><MapLocation /></el-icon>
                </el-button>
                <el-button type="danger" size="small" text @click="deleteCharacter(row)">删除</el-button>
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
            <span>人物关系图</span>
            <el-button type="primary" size="small" style="float: right" @click="generateRelationshipGraph">生成关系图</el-button>
          </template>
          <div class="relation-graph">
            <div v-if="relationshipGraph.nodes.length === 0" class="graph-placeholder">
              <el-icon size="60"><PieChart /></el-icon>
              <p>人物关系图可视化区域</p>
              <el-button type="primary" size="small" @click="generateRelationshipGraph">生成关系图</el-button>
            </div>
            <div v-else class="graph-container">
              <!-- 这里可以集成关系图可视化库，如ECharts -->
              <div class="graph-stats">
                <span>节点数: {{ relationshipGraph.nodes.length }}</span>
                <span style="margin-left: 20px">关系数: {{ relationshipGraph.edges.length }}</span>
              </div>
              <div class="graph-visualization">
                <p>关系图可视化组件占位符</p>
                <p>可集成ECharts、G6等可视化库</p>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 添加/编辑人物对话框 -->
    <el-dialog
      v-model="showEditDialog"
      :title="editMode === 'add' ? '添加人物' : '编辑人物'"
      width="800px"
      @close="resetForm"
    >
      <el-form :model="characterForm" label-width="100px" :rules="formRules" ref="formRef">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="characterForm.name" placeholder="请输入人物姓名" />
            </el-form-item>
            <el-form-item label="别名">
              <el-input v-model="characterForm.alias" placeholder="请输入人物别名" />
            </el-form-item>
            <el-form-item label="性别">
              <el-select v-model="characterForm.gender" placeholder="请选择性别">
                <el-option label="男" value="男" />
                <el-option label="女" value="女" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
            <el-form-item label="年龄">
              <el-input v-model="characterForm.age" placeholder="请输入年龄" />
            </el-form-item>
            <el-form-item label="种族">
              <el-input v-model="characterForm.race" placeholder="请输入种族" />
            </el-form-item>
            <el-form-item label="阵营">
              <el-input v-model="characterForm.faction" placeholder="请输入阵营" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身份">
              <el-input v-model="characterForm.identity" placeholder="请输入身份" />
            </el-form-item>
            <el-form-item label="性格">
              <el-input v-model="characterForm.personality" type="textarea" placeholder="请输入性格描述" />
            </el-form-item>
            <el-form-item label="外貌">
              <el-input v-model="characterForm.appearance" type="textarea" placeholder="请输入外貌描述" />
            </el-form-item>
            <el-form-item label="标签">
              <el-select v-model="characterForm.tags" multiple placeholder="选择标签" filterable allow-create>
                <el-option v-for="tag in availableTags" :key="tag" :label="tag" :value="tag" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="背景故事">
              <el-input v-model="characterForm.background" type="textarea" :rows="3" placeholder="请输入背景故事" />
            </el-form-item>
            <el-form-item label="能力">
              <el-input v-model="characterForm.abilities" type="textarea" :rows="2" placeholder="请输入能力描述" />
            </el-form-item>
            <el-form-item label="目标">
              <el-input v-model="characterForm.goals" type="textarea" :rows="2" placeholder="请输入人物目标" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="submitCharacterForm">保存</el-button>
      </template>
    </el-dialog>

    <!-- 批量导入对话框 -->
    <el-dialog v-model="showBatchImport" title="批量导入人物" width="600px">
      <el-alert type="info" title="支持JSON格式批量导入" show-icon style="margin-bottom: 20px" />
      <el-input
        v-model="batchImportData"
        type="textarea"
        :rows="10"
        placeholder='请输入JSON格式数据，例如：[{"name": "张三", "gender": "男", "age": "20"}]'
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
import { useRouter } from 'vue-router'
import { Search, PieChart, MapLocation } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import * as resourceApi from '@/api/resource'

// 当前小说ID（暂时硬编码，后续从路由或store获取）
const NOVEL_ID = 1

const router = useRouter()

// 搜索与筛选
const searchKeyword = ref('')
const filter = reactive({
  gender: [] as string[],
  faction: '',
  tags: [] as string[]
})

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 数据
const characters = ref<resourceApi.CharacterDTO[]>([])
const selectedCharacters = ref<resourceApi.CharacterDTO[]>([])
const availableTags = ref<string[]>(['主角', '反派', '配角', '重要人物', '神秘人物'])

// 对话框状态
const showEditDialog = ref(false)
const showBatchImport = ref(false)
const editMode = ref<'add' | 'edit'>('add')
const currentCharacterId = ref<number | null>(null)

// 表单
const characterForm = reactive<resourceApi.CreateCharacterRequest>({
  name: '',
  alias: '',
  gender: '',
  age: '',
  race: '',
  identity: '',
  faction: '',
  personality: '',
  appearance: '',
  background: '',
  abilities: '',
  weaknesses: '',
  goals: '',
  growthArc: '',
  relationships: [],
  quotes: '',
  novelId: NOVEL_ID,
  status: 'ACTIVE',
  tags: []
})

const formRef = ref<FormInstance>()
const formRules: FormRules = {
  name: [
    { required: true, message: '请输入人物姓名', trigger: 'blur' }
  ]
}

// 关系图
const relationshipGraph = reactive<resourceApi.RelationshipGraph>({
  nodes: [],
  edges: []
})

// 批量导入数据
const batchImportData = ref('')

// 加载人物列表
const loadCharacters = async () => {
  try {
    const response = await resourceApi.listCharacters(
      NOVEL_ID,
      searchKeyword.value || undefined,
      filter.tags.length > 0 ? filter.tags : undefined
    )
    characters.value = response
    // 更新标签列表
    updateTagList()
    // 更新分页总数
    pagination.total = characters.value.length
  } catch (error) {
    ElMessage.error('加载人物列表失败')
    console.error(error)
  }
}

// 更新标签列表（从现有数据中提取）
const updateTagList = () => {
  const tagSet = new Set<string>()
  characters.value.forEach(char => {
    if (char.tags) {
      char.tags.forEach(tag => tagSet.add(tag))
    }
  })
  availableTags.value = Array.from(tagSet)
}

// 表格选择变化
const handleSelectionChange = (selection: resourceApi.CharacterDTO[]) => {
  selectedCharacters.value = selection
}

// 分页大小变化
const handleSizeChange = (size: number) => {
  pagination.size = size
  loadCharacters()
}

// 当前页变化
const handleCurrentChange = (page: number) => {
  pagination.current = page
  loadCharacters()
}

// 添加人物
const addCharacter = () => {
  editMode.value = 'add'
  resetForm()
  showEditDialog.value = true
}

// 编辑人物
const editCharacter = (row: resourceApi.CharacterDTO) => {
  editMode.value = 'edit'
  currentCharacterId.value = row.id!
  Object.assign(characterForm, {
    name: row.name,
    alias: row.alias || '',
    gender: row.gender || '',
    age: row.age || '',
    race: row.race || '',
    identity: row.identity || '',
    faction: row.faction || '',
    personality: row.personality || '',
    appearance: row.appearance || '',
    background: row.background || '',
    abilities: row.abilities || '',
    weaknesses: row.weaknesses || '',
    goals: row.goals || '',
    growthArc: row.growthArc || '',
    relationships: row.relationships || [],
    quotes: row.quotes || '',
    novelId: row.novelId,
    status: row.status || 'ACTIVE',
    tags: row.tags || []
  })
  showEditDialog.value = true
}

// 查看人物详情
const viewCharacter = async (row: resourceApi.CharacterDTO) => {
  try {
    const character = await resourceApi.getCharacter(NOVEL_ID, row.id!)
    ElMessageBox.alert(
      `
      <h3>${character.name}</h3>
      <p><strong>身份：</strong>${character.identity || '无'}</p>
      <p><strong>阵营：</strong>${character.faction || '无'}</p>
      <p><strong>性格：</strong>${character.personality || '无'}</p>
      <p><strong>背景：</strong>${character.background || '无'}</p>
      <p><strong>能力：</strong>${character.abilities || '无'}</p>
      `,
      '人物详情',
      {
        dangerouslyUseHTMLString: true,
        confirmButtonText: '确定'
      }
    )
  } catch (error) {
    ElMessage.error('获取人物详情失败')
    console.error(error)
  }
}

// 在地图上查看人物
const viewOnMap = (row: resourceApi.CharacterDTO) => {
  router.push({
    path: '/world-map',
    query: {
      focus: 'character',
      characterId: row.id
    }
  })
}

// 删除人物
const deleteCharacter = (row: resourceApi.CharacterDTO) => {
  ElMessageBox.confirm(
    `确定要删除人物 "${row.name}" 吗？`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await resourceApi.deleteCharacter(NOVEL_ID, row.id!)
      ElMessage.success('删除成功')
      loadCharacters()
    } catch (error) {
      ElMessage.error('删除失败')
      console.error(error)
    }
  })
}

// AI增强人物
const enhanceCharacter = async (row: resourceApi.CharacterDTO) => {
  try {
    ElMessage.info('AI增强中，请稍候...')
    const enhanced = await resourceApi.enhanceCharacterWithAI(NOVEL_ID, row.id!)
    // 更新列表中的该人物
    const index = characters.value.findIndex(c => c.id === row.id)
    if (index !== -1) {
      characters.value[index] = enhanced
    }
    ElMessage.success('AI增强完成')
  } catch (error) {
    ElMessage.error('AI增强失败')
    console.error(error)
  }
}

// 批量AI增强
const enhanceSelectedCharacters = async () => {
  if (selectedCharacters.value.length === 0) {
    ElMessage.warning('请先选择要增强的人物')
    return
  }
  ElMessageBox.confirm(
    `确定要对选中的 ${selectedCharacters.value.length} 个人物进行AI增强吗？`,
    '批量AI增强',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      ElMessage.info('批量AI增强中，请稍候...')
      for (const character of selectedCharacters.value) {
        await resourceApi.enhanceCharacterWithAI(NOVEL_ID, character.id!)
      }
      ElMessage.success('批量AI增强完成')
      loadCharacters()
    } catch (error) {
      ElMessage.error('批量AI增强失败')
      console.error(error)
    }
  })
}

// 提交人物表单
const submitCharacterForm = async () => {
  if (!formRef.value) return
  const valid = await formRef.value.validate()
  if (!valid) return

  try {
    if (editMode.value === 'add') {
      await resourceApi.createCharacter(NOVEL_ID, characterForm)
      ElMessage.success('添加成功')
    } else {
      await resourceApi.updateCharacter(NOVEL_ID, currentCharacterId.value!, characterForm)
      ElMessage.success('更新成功')
    }
    showEditDialog.value = false
    loadCharacters()
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
  Object.assign(characterForm, {
    name: '',
    alias: '',
    gender: '',
    age: '',
    race: '',
    identity: '',
    faction: '',
    personality: '',
    appearance: '',
    background: '',
    abilities: '',
    weaknesses: '',
    goals: '',
    growthArc: '',
    relationships: [],
    quotes: '',
    novelId: NOVEL_ID,
    status: 'ACTIVE',
    tags: []
  })
  currentCharacterId.value = null
}

// 批量导入
const handleBatchImport = async () => {
  try {
    const data = JSON.parse(batchImportData.value)
    if (!Array.isArray(data)) {
      ElMessage.error('数据格式错误，请输入JSON数组')
      return
    }
    await resourceApi.batchCreateCharacters(NOVEL_ID, data)
    ElMessage.success('批量导入成功')
    showBatchImport.value = false
    batchImportData.value = ''
    loadCharacters()
  } catch (error) {
    ElMessage.error('批量导入失败，请检查数据格式')
    console.error(error)
  }
}

// 生成关系图
const generateRelationshipGraph = async () => {
  try {
    const graph = await resourceApi.getRelationshipGraph(NOVEL_ID)
    relationshipGraph.nodes = graph.nodes
    relationshipGraph.edges = graph.edges
    ElMessage.success('关系图生成成功')
  } catch (error) {
    ElMessage.error('生成关系图失败')
    console.error(error)
  }
}

// 初始化
onMounted(() => {
  loadCharacters()
})
</script>

<style scoped lang="scss">
.character-container {
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

    .graph-container {
      width: 100%;
      height: 100%;
      padding: 20px;

      .graph-stats {
        margin-bottom: 20px;
        font-size: 14px;
        color: #666;
      }

      .graph-visualization {
        height: 300px;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        color: #999;
      }
    }
  }

  .mt-3 {
    margin-top: 20px;
  }
}
</style>