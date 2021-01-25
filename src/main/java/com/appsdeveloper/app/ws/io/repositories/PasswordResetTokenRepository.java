package com.appsdeveloper.app.ws.io.repositories;

import com.appsdeveloper.app.ws.io.entity.PasswordResetTokenEntity;
import com.appsdeveloper.app.ws.io.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetTokenEntity,Long> {
    PasswordResetTokenEntity findByToken(String token);
    PasswordResetTokenEntity findByUserDetails(UserEntity userEntity);
}
