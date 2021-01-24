package com.appsdeveloper.app.ws.shared.dto;

import lombok.Data;

@Data
public class TaskDto {

    private long id;
    private String taskId;
    private String taskContent;
    private UserDto userDetails;

}
