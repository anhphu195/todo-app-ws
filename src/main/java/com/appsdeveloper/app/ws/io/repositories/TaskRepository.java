package com.appsdeveloper.app.ws.io.repositories;

import com.appsdeveloper.app.ws.io.entity.TaskEntity;
import com.appsdeveloper.app.ws.io.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends PagingAndSortingRepository<TaskEntity,Long> {
    Page<TaskEntity> findByUserDetails(Pageable pageable, UserEntity userEntity);
}
