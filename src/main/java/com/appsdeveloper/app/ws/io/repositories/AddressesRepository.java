package com.appsdeveloper.app.ws.io.repositories;

import com.appsdeveloper.app.ws.io.entity.AddressEntity;
import com.appsdeveloper.app.ws.io.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressesRepository extends CrudRepository<AddressEntity,Long> {
    List<AddressEntity> findAddressEntitiesByUserDetails(UserEntity userEntity);
    AddressEntity findByAddressId(String addressId);
}
