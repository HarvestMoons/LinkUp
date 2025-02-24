package com.example.linkup.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class GroupMemberId implements Serializable {

    private Long taskGroup; // 群组ID
    private Long user;      // 用户ID

    // 默认构造函数
    public GroupMemberId() {}

    // 带参数构造函数
    public GroupMemberId(Long taskGroup, Long user) {
        this.taskGroup = taskGroup;
        this.user = user;
    }

    // equals 和 hashCode 方法必须重写，以确保复合主键正确识别
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GroupMemberId that = (GroupMemberId) o;
        return Objects.equals(taskGroup, that.taskGroup) &&
                Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskGroup, user);
    }
}
