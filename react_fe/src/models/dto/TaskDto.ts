import { TaskAssigneeDto } from './TaskAssigneeDto'

export interface TaskDto {
  id: number
  issueId: number
  statusId: number
  statusName: string
  code: string
  description: string
  assignees: TaskAssigneeDto[]
  createdAt: string
  updatedAt: string
}
