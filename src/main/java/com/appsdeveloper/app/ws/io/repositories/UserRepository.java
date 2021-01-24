package com.appsdeveloper.app.ws.io.repositories;

import com.appsdeveloper.app.ws.io.entity.UserEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity,Long> {
    UserEntity findUserByEmail(String email);

    UserEntity findByEmail(String email);

    UserEntity findByUserId(String id);

    UserEntity findByEmailVerificationToken(String token);
}
