package com.appsdeveloper.app.ws.io.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity(name = "tasks")
public class TaskEntity implements Serializable {
    private static final long serialVersionUID = -752445937932343970L;

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false, length = 30)
    private String taskId;

    @Column(nullable = false, length = 1000)
    private Date date;

    @Column(nullable = false, length = 1000)
    private String taskContent;

    @ManyToOne
    @JoinColumn(name = "users_id")
    private UserEntity userDetails;
}
