package com.appsdeveloper.app.ws.service.impl;

import com.appsdeveloper.app.ws.io.entity.TaskEntity;
import com.appsdeveloper.app.ws.io.entity.UserEntity;
import com.appsdeveloper.app.ws.io.repositories.TaskRepository;
import com.appsdeveloper.app.ws.io.repositories.UserRepository;
import com.appsdeveloper.app.ws.service.TaskService;
import com.appsdeveloper.app.ws.service.UserService;
import com.appsdeveloper.app.ws.shared.Utils;
import com.appsdeveloper.app.ws.shared.dto.TaskDto;
import com.appsdeveloper.app.ws.shared.dto.UserDto;
import com.appsdeveloper.app.ws.ui.model.response.ErrorMessage;
import com.appsdeveloper.app.ws.ui.model.response.ErrorMessages;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    Utils utils;

    @Override
    public TaskDto addTask(String userId, String taskContent) {
        UserEntity userEntity = userRepository.findByUserId(userId);
        if (userEntity == null) throw new RuntimeException(ErrorMessages.NO_FOUND_USER.getErrorMessage());

        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setTaskId(utils.generateTaskId(30));
        taskEntity.setDate(new Date());
        taskEntity.setTaskContent(taskContent);
        taskEntity.setUserDetails(userEntity);
        userEntity.getTasks().add(taskEntity);
        TaskEntity savedTask = taskRepository.save(taskEntity);
        TaskDto returnValue = new TaskDto();
        BeanUtils.copyProperties(savedTask,returnValue);
        return returnValue;
    }

    @Override
    public List<TaskDto> getTasks(String userId, int page, int limit) {
        UserEntity userEntity = userRepository.findByUserId(userId);
        if (userEntity == null) throw new RuntimeException(ErrorMessages.NO_FOUND_USER.getErrorMessage());

        List<TaskDto> returnValue = new ArrayList<>();
        if (page > 0) page -= 1;
        Pageable pageableRest = PageRequest.of(page,limit);
        Page<TaskEntity> taskEntityPage = taskRepository.findByUserDetails(pageableRest,userEntity);
        List<TaskEntity> taskEntities = taskEntityPage.getContent();
        for (TaskEntity taskEntity : taskEntities){
            TaskDto taskDto = new TaskDto();
            BeanUtils.copyProperties(taskEntity,taskDto);
            returnValue.add(taskDto);
        }
        return returnValue;
    }

    @Override
    public TaskDto updateTask(String userId, String taskId, TaskDto taskDto) {
        UserEntity userEntity = userRepository.findByUserId(userId);
        if (userEntity == null) throw new RuntimeException(ErrorMessages.NO_FOUND_USER.getErrorMessage());

        TaskEntity taskEntity = taskRepository.findByTaskId(taskId);
        if (userEntity == null) throw new RuntimeException(ErrorMessages.NO_FOUND_TASK.getErrorMessage());

        if(taskEntity.getUserDetails() != userEntity) throw new RuntimeException(ErrorMessages.USER_DONT_HAVE_THIS_TASK.getErrorMessage());

        taskEntity.setTaskContent(taskDto.getTaskContent());
        taskEntity.setDate(new Date());

        TaskEntity savedTask = taskRepository.save(taskEntity);

        TaskDto returnValue = new TaskDto();
        BeanUtils.copyProperties(savedTask,returnValue);

        return returnValue;
    }

    @Override
    public void deleteTask(String userId, String taskId) {
        UserEntity userEntity = userRepository.findByUserId(userId);
        if (userEntity == null) throw new RuntimeException(ErrorMessages.NO_FOUND_USER.getErrorMessage());

        TaskEntity taskEntity = taskRepository.findByTaskId(taskId);
        if (userEntity == null) throw new RuntimeException(ErrorMessages.NO_FOUND_TASK.getErrorMessage());

        if(taskEntity.getUserDetails() != userEntity) throw new RuntimeException(ErrorMessages.USER_DONT_HAVE_THIS_TASK.getErrorMessage());
        taskRepository.delete(taskEntity);
    }
}
