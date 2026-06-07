package com.jurgen.task_planner.models.entities;

import java.io.Serializable;

import com.jurgen.task_planner.models.entities.auth.UserEntity;

import jakarta.persistence.*;

@Entity
@Table(name = "task_users")
public class TaskUsersEntity {

    @EmbeddedId
    private TaskUsersId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("taskId")
    @JoinColumn(name = "task_id")
    private TaskEntity task;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsibility_id", nullable = false)
    private TaskResponsibilityEntity responsibility;

    public static TaskUsersEntity create(TaskEntity task, UserEntity user, TaskResponsibilityEntity responsibility) {
        TaskUsersEntity e = new TaskUsersEntity();
        e.task = task;
        e.user = user;
        e.responsibility = responsibility;
        e.id = new TaskUsersId(task.getId(), user.getId());
        return e;
    }

    public TaskEntity getTask()                         { return task; }
    public UserEntity getUser()                         { return user; }
    public TaskResponsibilityEntity getResponsibility() { return responsibility; }

    @Embeddable
    public static class TaskUsersId implements Serializable {
        private int taskId;
        private int userId;

        protected TaskUsersId() {}

        public TaskUsersId(int taskId, int userId) {
            this.taskId = taskId;
            this.userId = userId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TaskUsersId that = (TaskUsersId) o;
            return taskId == that.taskId && userId == that.userId;
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(taskId, userId);
        }
    }
}
