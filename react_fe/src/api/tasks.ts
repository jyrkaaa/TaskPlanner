import { TaskDto } from '../models/dto/TaskDto'
import { api } from './client'

export const tasksApi = {
  getAll: () => api.get<TaskDto[]>('/tasks'),
  getById: (id: number) => api.get<TaskDto>(`/tasks/${id}`),
  create: (task: Omit<TaskDto, 'id' | 'createdAt' | 'updatedAt'>) => api.post<TaskDto>('/tasks', task),
  update: (id: number, task: Partial<TaskDto>) => api.put<TaskDto>(`/tasks/${id}`, task),
  remove: (id: number) => api.delete<void>(`/tasks/${id}`),
}
