package com.appsdeveloper.app.ws.service;

import com.appsdeveloper.app.ws.shared.dto.TaskDto;

import java.util.List;

public interface TaskService {
    TaskDto addTask(String userId, String taskContent);

    List<TaskDto> getTasks(String userId, int page, int limit);

    TaskDto updateTask(String userId, String taskId, TaskDto taskDto);

    void deleteTask(String userId, String taskId);
}
