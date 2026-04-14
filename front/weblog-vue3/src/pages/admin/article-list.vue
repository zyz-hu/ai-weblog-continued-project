<template>
    <div>
        <el-card shadow="never" class="mb-5">
            <div class="flex flex-wrap items-center gap-4">
                <el-text class="text-slate-600">文章标题</el-text>
                <el-input v-model="searchArticleTitle" placeholder="请输入（模糊查询）" class="w-60" />

                <el-text class="text-slate-600">创建日期</el-text>
                <el-date-picker v-model="pickDate" type="daterange" range-separator="至" start-placeholder="开始时间"
                        end-placeholder="结束时间" size="default" :shortcuts="shortcuts" @change="datepickerChange" class="w-80" />

                <div class="flex items-center gap-2 ml-auto">
                    <el-button type="primary" :icon="Search" @click="getTableData">查询</el-button>
                    <el-button :icon="RefreshRight" @click="reset">重置</el-button>
                </div>
            </div>
        </el-card>

        <el-card shadow="never">
            <div class="mb-5">
                <el-button type="primary" @click="isArticlePublishEditorShow = true">
                    <el-icon class="mr-1">
                        <EditPen />
                    </el-icon>
                    写文章</el-button>
            </div>

            <el-table :data="tableData" border stripe style="width: 100%" v-loading="tableLoading" class="admin-table">
                <el-table-column prop="id" label="ID" width="50" />
                <el-table-column prop="title" label="标题" width="380" />
                <el-table-column prop="cover" label="封面" width="180">
                    <template #default="scope">
                        <el-image style="width: 100px;" :src="scope.row.cover" />
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="发布时间" width="180" />
                <el-table-column label="操作">
                    <template #default="scope">
                        <el-button size="small" @click="showArticleUpdateEditor(scope.row)">
                            <el-icon class="mr-1">
                                <Edit />
                            </el-icon>
                            编辑</el-button>
                            <el-button size="small" @click="goArticleDetailPage(scope.row.id)">
                            <el-icon class="mr-1">
                                <View />
                            </el-icon>
                            预览</el-button>
                        <el-button type="danger" size="small" @click="deleteArticleSubmit(scope.row)">
                            <el-icon class="mr-1">
                                <Delete />
                            </el-icon>
                            删除
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>

            <div class="mt-10 flex justify-center">
                <el-pagination v-model:current-page="current" v-model:page-size="size" :page-sizes="[10, 20, 50]"
                    :small="false" :background="true" layout="total, sizes, prev, pager, next, jumper" :total="total"
                    @size-change="handleSizeChange" @current-change="getTableData" />
            </div>

        </el-card>

        <el-dialog v-model="isArticlePublishEditorShow" :fullscreen="true" :show-close="false"
            :close-on-press-escape="false" append-to-body>
            <template #header="{ close, titleId, titleClass }">
                <el-affix :offset="20" style="width: 100%;">
                    <div class="flex h-10 bg-white">
                        <h4 class="font-bold">写文章</h4>
                        <div class="ml-auto flex">
                            <el-button @click="isArticlePublishEditorShow = false">取消</el-button>
                            <el-button type="primary" @click="publishArticleSubmit">
                                <el-icon class="mr-1">
                                    <Promotion />
                                </el-icon>
                                发布
                            </el-button>
                        </div>
                    </div>
                </el-affix>
            </template>
            <el-form :model="form" ref="publishArticleFormRef" label-position="top" size="large" :rules="rules">
                <el-form-item label="标题" prop="title">
                    <el-input v-model="form.title" autocomplete="off" size="large" maxlength="40" show-word-limit
                        clearable />
                </el-form-item>
                <el-form-item label="内容" prop="content">
                    <div class="flex items-center justify-between mb-2 gap-2">
                        <el-text type="info" size="small">选中要润色的文字优先处理，未选中则润色全文（快捷键：Ctrl + Shift + P）。</el-text>
                        <div class="flex items-center gap-2">
                            <el-input v-model="polishPrompt" size="small" placeholder="可选：自定义润色提示词" clearable style="width: 260px" />
                            <el-select v-model="polishModelName" size="small" placeholder="选择模型" style="width: 180px">
                                <el-option v-for="item in polishModelOptions" :key="item.name" :label="item.description || item.name" :value="item.name" />
                            </el-select>
                            <el-button type="success" plain :loading="polishing" @click="triggerPolish('publish')">AI润色</el-button>
                        </div>
                    </div>
                    <div v-if="polishing" class="mb-2 text-xs text-gray-500 bg-gray-50 p-2 rounded border border-dashed border-gray-200">
                        正在润色中（流式）：{{ polishPreview || '...' }}
                    </div>
                    <MdEditor v-model="form.content" @onUploadImg="onUploadImg" editorId="publishArticleEditor" style="height: calc(100vh - 260px);" />
                </el-form-item>
                <el-form-item label="封面" prop="cover">
                    <el-upload class="avatar-uploader cover-drop" action="#" :on-change="handleCoverChange" :auto-upload="false"
                        :show-file-list="false">
                        <img v-if="form.cover" :src="form.cover" class="avatar" />
                        <el-icon v-else class="avatar-uploader-icon">
                            <Plus />
                        </el-icon>
                    </el-upload>
                </el-form-item>
                <el-form-item label="摘要" prop="summary">
                    <el-input v-model="form.summary" :rows="3" type="textarea" placeholder="请输入文章摘要" />
                </el-form-item>
                <el-form-item label="分类" prop="categoryId">
                    <el-select v-model="form.categoryId" clearable placeholder="---请选择---" size="large">
                        <el-option v-for="item in categories" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                </el-form-item>
                <el-form-item label="标签" prop="tags">
                    <span class="w-60">
                        <el-select v-model="form.tags" multiple filterable remote reserve-keyword placeholder="请输入文章标签"
                            remote-show-suffix allow-create default-first-option :remote-method="remoteMethod"
                            :loading="tagSelectLoading" size="large">
                            <el-option v-for="item in tags" :key="item.value" :label="item.label" :value="item.value" />
                        </el-select>
                    </span>
                </el-form-item>
            </el-form>
        </el-dialog>

        <el-dialog v-model="isArticleUpdateEditorShow" :fullscreen="true" :show-close="false"
            :close-on-press-escape="false" append-to-body>
            <template #header="{ close, titleId, titleClass }">
                <el-affix :offset="20" style="width: 100%;">
                    <div class="flex h-10 bg-white">
                        <h4 class="font-bold">编辑文章</h4>
                        <div class="ml-auto flex">
                            <el-button @click="isArticleUpdateEditorShow = false">取消</el-button>
                            <el-button type="primary" @click="updateSubmit">
                                <el-icon class="mr-1">
                                    <Promotion />
                                </el-icon>
                                保存
                            </el-button>
                        </div>
                    </div>
                </el-affix>
            </template>
            <el-form :model="updateArticleForm" ref="updateArticleFormRef" label-position="top" size="large" :rules="rules">
                <el-form-item label="标题" prop="title">
                    <el-input v-model="updateArticleForm.title" autocomplete="off" size="large" maxlength="40"
                        show-word-limit clearable />
                </el-form-item>
                <el-form-item label="内容" prop="content">
                    <div class="flex items-center justify-between mb-2 gap-2">
                        <el-text type="info" size="small">选中要润色的文字优先处理，未选中则润色全文（快捷键：Ctrl + Shift + P）。</el-text>
                        <div class="flex items-center gap-2">
                            <el-input v-model="polishPrompt" size="small" placeholder="可选：自定义润色提示词" clearable style="width: 260px" />
                            <el-select v-model="polishModelName" size="small" placeholder="选择模型" style="width: 180px">
                                <el-option v-for="item in polishModelOptions" :key="item.name" :label="item.description || item.name" :value="item.name" />
                            </el-select>
                            <el-button type="success" plain :loading="polishing" @click="triggerPolish('update')">AI润色</el-button>
                        </div>
                    </div>
                    <div v-if="polishing" class="mb-2 text-xs text-gray-500 bg-gray-50 p-2 rounded border border-dashed border-gray-200">
                        正在润色中（流式）：{{ polishPreview || '...' }}
                    </div>
                    <MdEditor v-model="updateArticleForm.content" @onUploadImg="onUploadImg"
                        editorId="updateArticleEditor" style="height: calc(100vh - 260px);" />
                </el-form-item>
                <el-form-item label="封面" prop="cover">
                    <el-upload class="avatar-uploader cover-drop" action="#" :on-change="handleUpdateCoverChange" :auto-upload="false"
                        :show-file-list="false">
                        <img v-if="updateArticleForm.cover" :src="updateArticleForm.cover" class="avatar" />
                        <el-icon v-else class="avatar-uploader-icon">
                            <Plus />
                        </el-icon>
                    </el-upload>
                </el-form-item>
                <el-form-item label="摘要" prop="summary">
                    <el-input v-model="updateArticleForm.summary" :rows="3" type="textarea" placeholder="请输入文章摘要" />
                </el-form-item>
                <el-form-item label="分类" prop="categoryId">
                    <el-select v-model="updateArticleForm.categoryId" clearable placeholder="---请选择---" size="large">
                        <el-option v-for="item in categories" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                </el-form-item>
                <el-form-item label="标签" prop="tags">
                    <span class="w-60">
                        <el-select v-model="updateArticleForm.tags" multiple filterable remote reserve-keyword
                            placeholder="请输入文章标签" remote-show-suffix allow-create default-first-option
                            :remote-method="remoteMethod" :loading="tagSelectLoading" size="large">
                            <el-option v-for="item in tags" :key="item.value" :label="item.label" :value="item.value" />
                        </el-select>
                    </span>
                </el-form-item>
            </el-form>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, computed, watch } from 'vue'
import { Search, RefreshRight } from '@element-plus/icons-vue'
import { getArticlePageList, deleteArticle, publishArticle, getArticleDetail, updateArticle } from '@/api/admin/article'
import { uploadFile } from '@/api/admin/file'
import { getCategorySelectList } from '@/api/admin/category'
import { searchTags, getTagSelectList } from '@/api/admin/tag'
import moment from 'moment'
import { showMessage, showModel } from '@/composables/util'
import { MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import { useRouter } from 'vue-router'
import { fetchEventSource } from '@microsoft/fetch-event-source'
import { useAiChatStore } from '@/stores/ai-robot/chat'
import { getToken } from '@/composables/cookie'

const router = useRouter()

// 模糊搜索的文章标题
const searchArticleTitle = ref('')
// 日期
const pickDate = ref('')

// 查询条件：开始结束时间
const startDate = reactive({})
const endDate = reactive({})

// 监听日期组件改变事件，并将开始结束时间设置到变量中
const datepickerChange = (e) => {
    startDate.value = moment(e[0]).format('YYYY-MM-DD')
    endDate.value = moment(e[1]).format('YYYY-MM-DD')

    console.log('开始时间：' + startDate.value + ', 结束时间：' + endDate.value)
}

const shortcuts = [
    {
        text: '最近一周',
        value: () => {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
            return [start, end]
        },
    },
    {
        text: '最近一个月',
        value: () => {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
            return [start, end]
        },
    },
    {
        text: '最近三个月',
        value: () => {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
            return [start, end]
        },
    },
]

// 重置
const reset = () => {
    pickDate.value = ''
    startDate.value = null
    endDate.value = null
    searchArticleTitle.value = ''
}

// 表格加载 Loading
const tableLoading = ref(false)
// 表格数据
const tableData = ref([])
// 当前页码，给了一个默认值 1
const current = ref(1)
// 总数据量，给了个默认值 0
const total = ref(0)
// 每页显示的数据量，给了个默认值 10
const size = ref(10)


// 获取分页数据
function getTableData() {
    // 显示表格 loading
    tableLoading.value = true
    // 调用后台分页接口，并传入所需参数
    getArticlePageList({ current: current.value, size: size.value, startDate: startDate.value, endDate: endDate.value, title: searchArticleTitle.value })
        .then((res) => {
            if (res.success == true) {
                tableData.value = res.data
                current.value = res.current
                size.value = res.size
                total.value = res.total
            }
        })
        .finally(() => tableLoading.value = false) // 隐藏表格 loading
}
getTableData()

// 每页展示数量变更事件
const handleSizeChange = (chooseSize) => {
    console.log('选择的页码' + chooseSize)
    size.value = chooseSize
    getTableData()
}

// 删除文章
const deleteArticleSubmit = (row) => {
    console.log(row)
    showModel('是否确定要删除该文章？').then(() => {
        deleteArticle(row.id).then((res) => {
            if (res.success == false) {
                // 获取服务端返回的错误消息
                let message = res.message
                // 提示错误消息
                showMessage(message, 'error')
                return
            }

            showMessage('删除成功')
            // 重新请求分页接口，渲染数据
            getTableData()
        })
    }).catch(() => {
        console.log('取消了')
    })
}

// 是否显示文章发布对话框
const isArticlePublishEditorShow = ref(false)
// 发布文章表单引用
const publishArticleFormRef = ref(null)

// 表单对象
const form = reactive({
    id: null,
    title: '',
    content: '请输入内容',
    cover: '',
    categoryId: null,
    tags: [],
    summary: ""
})

// 修改文章表单对象
const updateArticleForm = reactive({
    id: null,
    title: '',
    content: '请输入内容',
    cover: '',
    categoryId: null,
    tags: [],
    summary: ""
})

// AI 润色配置
const polishing = ref(false)
const polishPreview = ref('')
const polishPrompt = ref('请润色以下内容，保持原意、逻辑清晰并提升可读性。')
const aiChatStore = useAiChatStore()
const polishModelName = ref(aiChatStore.selectedModel?.name || aiChatStore.models?.[0]?.name || 'deepseek-chat')
watch(() => aiChatStore.selectedModel?.name, (val) => {
    if (val) polishModelName.value = val
})
const polishModelOptions = computed(() => aiChatStore.models || [])
const currentModelConfig = computed(() => polishModelOptions.value.find((m) => m.name === polishModelName.value) || aiChatStore.selectedModel || polishModelOptions.value[0])
const polishTemperature = computed(() => currentModelConfig.value?.temperature ?? 0.7)

// 表单校验规则
const rules = {
    title: [
        { required: true, message: '请输入文章标题', trigger: 'blur' },
        { min: 1, max: 40, message: '文章标题要求大于1个字符，小于40个字符', trigger: 'blur' },
    ],
    content: [{ required: true }],
    cover: [{ required: true }],
    categoryId: [{ required: true, message: '请选择文章分类', trigger: 'blur' }],
    tags: [{ required: true, message: '请选择文章标签', trigger: 'blur' }],
}

// 上传文章封面图片
const handleCoverChange = (file) => {
    // 表单对象
    let formData = new FormData()
    // 添加 file 字段，并将文件传入 
    formData.append('file', file.raw)
    uploadFile(formData).then((e) => {
        // 响参失败，提示错误消息
        if (e.success == false) {
            let message = e.message
            showMessage(message, 'error')
            return
        }

        // 成功则设置表单对象中的封面链接，并提示上传成功
        form.cover = e.data.url
        showMessage('上传成功')
    })
}

// 编辑文章：上传文章封面图片
const handleUpdateCoverChange = (file) => {
    // 表单对象
    let formData = new FormData()
    // 添加 file 字段，并将文件传入 
    formData.append('file', file.raw)
    uploadFile(formData).then((e) => {
        // 响参失败，提示错误消息
        if (e.success == false) {
            let message = e.message
            showMessage(message, 'error')
            return
        }

        // 成功则设置表单对象中的封面链接，并提示上传成功
        updateArticleForm.cover = e.data.url
        showMessage('上传成功')
    })
}

// 编辑器图片上传
const onUploadImg = async (files, callback) => {
    const res = await Promise.all(
        files.map((file) => {
            return new Promise((rev, rej) => {
                console.log('==> 编辑器开始上传文件...')
                let formData = new FormData()
                formData.append("file", file);
                uploadFile(formData).then((res) => {
                    console.log(res)
                    console.log('访问路径：' + res.data.url)
                    // 调用 callback 函数，回显上传图片
                    callback([res.data.url]);
                })
            });
        })
    );
}

const getEditorContext = (target) => {
    if (target === 'update') {
        return {
            editorId: 'updateArticleEditor',
            content: updateArticleForm.content,
            setContent: (val) => updateArticleForm.content = val
        }
    }
    return {
        editorId: 'publishArticleEditor',
        content: form.content,
        setContent: (val) => form.content = val
    }
}

const getEditorSelection = (editorId) => {
    const editorEl = document.getElementById(editorId)
    const textarea = editorEl ? editorEl.querySelector('textarea') : null
    if (textarea && textarea.selectionStart !== textarea.selectionEnd) {
        const start = textarea.selectionStart
        const end = textarea.selectionEnd
        return { text: textarea.value.slice(start, end), start, end }
    }
    const selection = window.getSelection()
    if (selection && selection.toString() && editorEl && editorEl.contains(selection.anchorNode)) {
        return { text: selection.toString(), start: null, end: null }
    }
    return { text: '', start: null, end: null }
}

const applyPolishedText = (ctx, selection, polishedText) => {
    const source = ctx.content || ''
    let nextContent = polishedText
    if (selection?.text) {
        if (selection.start !== null && selection.end !== null) {
            nextContent = source.slice(0, selection.start) + polishedText + source.slice(selection.end)
        } else if (source.includes(selection.text)) {
            nextContent = source.replace(selection.text, polishedText)
        }
    }
    ctx.setContent(nextContent)
}

const runPolishStream = (article, prompt) => {
    return new Promise((resolve, reject) => {
        polishPreview.value = ''
        let result = ''
        let finished = false
        const token = getToken()
        fetchEventSource('/api/robot/chat/polish', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                ...(token ? { Authorization: `Bearer ${token}` } : {}),
            },
            body: JSON.stringify({
                article,
                prompt,
                modelName: polishModelName.value,
                temperature: polishTemperature.value,
            }),
            openWhenHidden: true,
            onmessage(ev) {
                if (!ev?.data) {
                    return
                }
                try {
                    const data = JSON.parse(ev.data)
                    if (data.type === 'ping') {
                        return
                    }
                    if (data.type === 'content') {
                        result += data.v || ''
                        polishPreview.value = result
                    }
                } catch (err) {
                    console.error('parse polish chunk error', err)
                }
            },
            onclose() {
                finished = true
                resolve(result)
            },
            onerror(err) {
                finished = true
                reject(err)
            },
        }).then(() => {
            if (!finished) {
                resolve(result)
            }
        }).catch((err) => {
            if (!finished) {
                reject(err)
            }
        })
    })
}

const triggerPolish = async (target = 'publish', presetSelection = null) => {
    if (polishing.value) {
        showMessage('正在润色中，请稍候', 'info')
        return
    }
    const ctx = getEditorContext(target)
    const selection = presetSelection || getEditorSelection(ctx.editorId)
    const targetText = selection.text || ctx.content || ''
    if (!targetText || targetText.trim() === '') {
        showMessage('没有可润色的内容', 'warning')
        return
    }
    polishing.value = true
    try {
        const polished = await runPolishStream(targetText, polishPrompt.value)
        if (!polished) {
            showMessage('AI 未返回结果', 'warning')
            return
        }
        applyPolishedText(ctx, selection, polished)
        showMessage('润色完成，已替换内容')
    } catch (err) {
        console.error(err)
        showMessage('润色失败，请稍后重试', 'error')
    } finally {
        polishing.value = false
        polishPreview.value = ''
    }
}

const handlePolishShortcut = (event) => {
    if (!(event.ctrlKey && event.shiftKey && event.key.toLowerCase() === 'p')) {
        return
    }
    const target = isArticleUpdateEditorShow.value ? 'update' : (isArticlePublishEditorShow.value ? 'publish' : null)
    if (!target) {
        return
    }
    const ctx = getEditorContext(target)
    const selection = getEditorSelection(ctx.editorId)
    const candidate = selection.text || ctx.content || ''
    if (!candidate || candidate.trim() === '') {
        showMessage('没有可润色的内容', 'warning')
        return
    }
    event.preventDefault()
    showModel('是否润色选中的内容？', 'info', 'AI润色').then(() => {
        triggerPolish(target, selection)
    })
}

onMounted(() => {
    window.addEventListener('keydown', handlePolishShortcut)
})

onBeforeUnmount(() => {
    window.removeEventListener('keydown', handlePolishShortcut)
})

// 文章分类
const categories = ref([])
getCategorySelectList().then((e) => {
    console.log('获取分类数据')
    categories.value = e.data
})

// 标签 select Loading 状态，默认不显示
const tagSelectLoading = ref(false)
// 文章标签
const tags = ref([])
// 渲染标签数据
getTagSelectList().then(res => {
    tags.value = res.data
})


// 根据用户输入的标签名称，远程模糊查询
const remoteMethod = (query) => {
    console.log('远程搜索：' + tags.value)
    // 如果用户的查询关键词不为空
    if (query) {
        // 显示 loading
        tagSelectLoading.value = true
        // 调用标签模糊查询接口
        searchTags(query).then((e) => {
            if (e.success) {
                // 设置到 tags 变量中
                tags.value = e.data
            }
        }).finally(() => tagSelectLoading.value = false) // 隐藏 loading
    }
}

// 发布文章
const publishArticleSubmit = () => {
    // isArticlePublishEditorShow.value = true
    console.log('提交 md 内容：' + form.content)
    // 校验表单
    publishArticleFormRef.value.validate((valid) => {
        if (!valid) {
            return false
        }

        publishArticle(form).then((res) => {
            if (res.success == false) {
                // 获取服务端返回的错误消息
                let message = res.message
                // 提示错误消息
                showMessage(message, 'error')
                return
            }

            showMessage('发布成功')
            // 隐藏发布文章对话框
            isArticlePublishEditorShow.value = false
            // 将 form 表单字段置空
            form.title = ''
            form.content = ''
            form.cover = ''
            form.summary = ''
            form.categoryId = null
            form.tags = []
            // 重新请求分页接口，渲染列表数据
            getTableData()
        })
    })
}


// 是否显示编辑文章对话框
const isArticleUpdateEditorShow = ref(false)
// 编辑文章表单引用
const updateArticleFormRef = ref(null)
// 编辑文章按钮点击事件
const showArticleUpdateEditor = (row) => {
    // 显示编辑文章对话框
    isArticleUpdateEditorShow.value = true
    // 拿到文章 ID
    let articleId = row.id
    getArticleDetail(articleId).then((res) => {
        if (res.success) {
            // 设置表单数据
            updateArticleForm.id = res.data.id
            updateArticleForm.title = res.data.title
            updateArticleForm.cover = res.data.cover
            updateArticleForm.content = res.data.content
            updateArticleForm.categoryId = res.data.categoryId
            updateArticleForm.tags = res.data.tagIds
            updateArticleForm.summary = res.data.summary
        }
    })
}

// 保存文章
const updateSubmit = () => {
    console.log('tijiao')
    updateArticleFormRef.value.validate((valid) => {
        // 校验表单
        if (!valid) {
            return false
        }

        // 请求更新文章接口
        updateArticle(updateArticleForm).then((res) => {
            if (res.success == false) {
                // 获取服务端返回的错误消息
                let message = res.message
                // 提示错误消息
                showMessage(message, 'error')
                return
            }

            showMessage('保存成功')
            // 隐藏编辑框
            isArticleUpdateEditorShow.value = false
            // 重新请求分页接口，渲染列表数据
            getTableData()
        })
    })
}


// 跳转文章详情页
const goArticleDetailPage = (articleId) => {
    router.push('/article/' + articleId)
}
</script>

<style scoped>
:deep(.el-card) {
    border-radius: 18px;
    border: 1px solid #e2e8f0;
    background: linear-gradient(135deg, rgba(255, 255, 255, 0.96), rgba(244, 247, 255, 0.92));
    box-shadow: 0 18px 45px rgba(15, 23, 42, 0.06);
}

:deep(.el-card__body) {
    padding: 18px;
}

.admin-table :deep(.el-table) {
    border-radius: 14px;
    overflow: hidden;
}

.admin-table :deep(.el-table__header-wrapper) {
    background: #f8fafc;
}

/* 封面图片样式 */
.avatar-uploader .avatar {
    width: 200px;
    height: 100px;
    display: block;
}

.cover-drop {
    border: 1px dashed rgba(99, 102, 241, 0.35);
    background: linear-gradient(135deg, rgba(99, 102, 241, 0.08), rgba(14, 165, 233, 0.08));
    border-radius: 18px;
    padding: 8px;
    transition: all 0.2s ease;
}

.cover-drop:hover {
    border-color: rgba(99, 102, 241, 0.6);
    box-shadow: 0 12px 30px rgba(99, 102, 241, 0.12);
    transform: translateY(-2px);
}

.el-icon.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 200px;
    height: 100px;
    text-align: center;
}

/* 指定 select 下拉框宽度 */
.el-select--large {
    width: 600px;
}
</style>

<style>
.md-editor-footer {
    height: 40px;
}
</style>
