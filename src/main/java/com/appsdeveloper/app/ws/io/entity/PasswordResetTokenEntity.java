package com.appsdeveloper.app.ws.io.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity(name = "passwordResetToken")
public class PasswordResetTokenEntity implements Serializable {
    private static final long serialVersionUID = 213379715629884294L;

    @Id
    @GeneratedValue
    private Long id;

    private String token;

    @OneToOne
    @JoinColumn(name = "users_id")
    private UserEntity userDetails;


}
