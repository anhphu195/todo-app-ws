package com.appsdeveloper.app.ws.ui.model.request;

import lombok.Data;

@Data
public class PasswordResetModel {
    private String token;
    private String password;
}
