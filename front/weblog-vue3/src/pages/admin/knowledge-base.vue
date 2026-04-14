<template>
  <div class="space-y-5">
    <el-card shadow="never">
      <div class="flex flex-wrap items-center gap-4">
        <el-input v-model="query.keyword" placeholder="按原始文件名模糊搜索" class="w-72" clearable />
        <el-select v-model="query.status" placeholder="处理状态" class="w-56" clearable>
          <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <div class="ml-auto flex gap-2">
          <el-button type="primary" :icon="Search" @click="loadData">查询</el-button>
          <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
          <el-button type="success" :icon="UploadFilled" @click="openUploadDialog">上传 Markdown</el-button>
        </div>
      </div>
    </el-card>

    <el-card shadow="never">
      <el-table :data="list" border stripe v-loading="loading" class="admin-table">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="originalFileName" label="原始文件名" min-width="200" />
        <el-table-column prop="remark" label="备注" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status)" effect="plain">
              {{ statusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="fileSize" label="大小" width="120">
          <template #default="{ row }">
            {{ formatSize(row.fileSize) }}
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="180" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text @click="openEditDialog(row)">编辑</el-button>
            <el-button type="danger" text @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="mt-6 flex justify-center">
        <el-pagination
          v-model:current-page="query.current"
          v-model:page-size="query.size"
          layout="total, sizes, prev, pager, next, jumper"
          :page-sizes="[10, 20, 50]"
          :total="total"
          @current-change="loadData"
          @size-change="loadData"
        />
      </div>
    </el-card>

    <el-dialog v-model="showUpload" title="上传知识库 Markdown" width="520px" destroy-on-close>
      <el-form :model="uploadForm" label-width="80px">
        <el-form-item label="备注">
          <el-input v-model="uploadForm.remark" placeholder="可选" />
        </el-form-item>
        <el-form-item label="文件">
          <el-upload
            drag
            action="#"
            :auto-upload="false"
            :limit="1"
            :on-change="handleUploadFileChange"
            :file-list="uploadFileList"
          >
            <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
            <div class="el-upload__text">拖拽或点击选择 .md 文件</div>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showUpload = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitUpload">上传</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showEdit" title="更新知识库文件" width="520px" destroy-on-close>
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="文件ID">
          <el-input v-model="editForm.id" disabled />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="editForm.remark" placeholder="可选" />
        </el-form-item>
        <el-form-item label="替换文件">
          <el-upload
            drag
            action="#"
            :auto-upload="false"
            :limit="1"
            :on-change="handleEditFileChange"
            :file-list="editFileList"
          >
            <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
            <div class="el-upload__text">可选：上传新 .md 文件替换</div>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEdit = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import { Refresh, Search, UploadFilled } from '@element-plus/icons-vue';
import { deleteKnowledgeFile, fetchKnowledgeFiles, updateKnowledgeFile, uploadKnowledgeFile } from '@/api/ai-robot/knowledge';
import { showMessage, showModel } from '@/composables/util';

const query = reactive({
  keyword: '',
  status: null,
  current: 1,
  size: 10,
});

const statusOptions = [
  { label: '全部状态', value: null },
  { label: '待处理', value: 0 },
  { label: '向量化中', value: 1 },
  { label: '已完成', value: 2 },
  { label: '失败', value: 3 },
];

const loading = ref(false);
const list = ref([]);
const total = ref(0);

const showUpload = ref(false);
const uploadForm = reactive({ remark: '', file: null });
const uploadFileList = ref([]);

const showEdit = ref(false);
const editForm = reactive({ id: null, remark: '', file: null });
const editFileList = ref([]);

const submitLoading = ref(false);

const statusLabel = (status) => {
  const map = {
    0: '待处理',
    1: '向量化中',
    2: '已完成',
    3: '失败',
  };
  return map[status] || '-';
};

const statusTag = (status) => {
  if (status === 2) return 'success';
  if (status === 1) return 'warning';
  if (status === 3) return 'danger';
  return 'info';
};

const formatSize = (size) => {
  if (!size && size !== 0) return '-';
  if (size < 1024) return `${size} B`;
  if (size < 1024 * 1024) return `${(size / 1024).toFixed(1)} KB`;
  return `${(size / 1024 / 1024).toFixed(2)} MB`;
};

const loadData = () => {
  loading.value = true;
  fetchKnowledgeFiles({
    current: query.current,
    size: query.size,
    keyword: query.keyword,
    status: query.status,
  })
    .then((res) => {
      if (res.success) {
        list.value = res.data || [];
        query.current = res.current || query.current;
        query.size = res.size || query.size;
        total.value = res.total || 0;
      }
    })
    .finally(() => {
      loading.value = false;
    });
};

const resetQuery = () => {
  query.keyword = '';
  query.status = null;
  query.current = 1;
  loadData();
};

const openUploadDialog = () => {
  uploadForm.remark = '';
  uploadForm.file = null;
  uploadFileList.value = [];
  showUpload.value = true;
};

const handleUploadFileChange = (file, fileList) => {
  uploadForm.file = file.raw;
  uploadFileList.value = fileList.slice(-1);
};

const submitUpload = () => {
  if (!uploadForm.file) {
    showMessage('请先选择 Markdown 文件', 'warning');
    return;
  }
  const formData = new FormData();
  formData.append('file', uploadForm.file);
  if (uploadForm.remark) formData.append('remark', uploadForm.remark);
  submitLoading.value = true;
  uploadKnowledgeFile(formData)
    .then((res) => {
      if (res.success) {
        showMessage('上传成功，正在向量化');
        showUpload.value = false;
        loadData();
      }
    })
    .finally(() => {
      submitLoading.value = false;
    });
};

const openEditDialog = (row) => {
  editForm.id = row.id;
  editForm.remark = row.remark || '';
  editForm.file = null;
  editFileList.value = [];
  showEdit.value = true;
};

const handleEditFileChange = (file, fileList) => {
  editForm.file = file.raw;
  editFileList.value = fileList.slice(-1);
};

const submitEdit = () => {
  if (!editForm.id) return;
  const formData = new FormData();
  formData.append('id', editForm.id);
  if (editForm.remark) formData.append('remark', editForm.remark);
  if (editForm.file) formData.append('file', editForm.file);

  submitLoading.value = true;
  updateKnowledgeFile(formData)
    .then((res) => {
      if (res.success) {
        showMessage(editForm.file ? '更新成功，重新向量化中' : '备注已更新');
        showEdit.value = false;
        loadData();
      }
    })
    .finally(() => {
      submitLoading.value = false;
    });
};

const handleDelete = (row) => {
  showModel(`确认删除「${row.originalFileName}」?`).then(() => {
    deleteKnowledgeFile(row.id).then((res) => {
      if (res.success) {
        showMessage('已删除');
        loadData();
      }
    });
  });
};

onMounted(() => {
  loadData();
});
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
</style>
