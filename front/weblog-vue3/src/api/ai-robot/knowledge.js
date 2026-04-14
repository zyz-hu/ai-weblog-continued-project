import axios from '@/axios';

// 知识库文件分页
export function fetchKnowledgeFiles(params) {
  return axios.get('/api/robot/customer-service/md/page', { params });
}

// 上传 Markdown
export function uploadKnowledgeFile(formData) {
  return axios.post('/api/robot/customer-service/md/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
}

// 更新 Markdown（可带新文件或仅备注）
export function updateKnowledgeFile(formData) {
  return axios.put('/api/robot/customer-service/md/update', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
}

// 删除 Markdown
export function deleteKnowledgeFile(id) {
  return axios.delete(`/api/robot/customer-service/md/${id}`);
}
