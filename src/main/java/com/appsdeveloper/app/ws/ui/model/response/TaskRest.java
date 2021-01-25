package com.appsdeveloper.app.ws.ui.model.response;

import lombok.Data;

import java.util.Date;

@Data
public class TaskRest {
    private String taskId;
    private Date date;
    private String taskContent;
}
