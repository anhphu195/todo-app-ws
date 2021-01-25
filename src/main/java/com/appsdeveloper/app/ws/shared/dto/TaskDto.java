package com.appsdeveloper.app.ws.shared.dto;

import lombok.Data;

import java.util.Date;

@Data
public class TaskDto {

    private long id;
    private String taskId;
    private Date date;
    private String taskContent;
    private UserDto userDetails;

}
