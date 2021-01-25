package com.appsdeveloper.app.ws.io.entity;


import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity(name="users") // name von table
@Data
public class UserEntity implements Serializable {
    public static final long serialVersionUID = 5330919304646776609L;

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false, length = 50)
    private String userId;

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false, length = 120, unique = true) // unique not dupplicate for email
    private String email;

    @Column(nullable = false)
    private String encrytedPassword;

    private String emailVerificationToken;

    @Column(nullable = false)
    private Boolean emailVerificationStatus = false;

    @OneToMany(mappedBy = "userDetails", cascade = CascadeType.ALL)
    private List<AddressEntity> addresses;

    @OneToMany(mappedBy = "userDetails", cascade = CascadeType.ALL)
    private List<TaskEntity> tasks;
}
