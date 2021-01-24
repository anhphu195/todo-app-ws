package com.appsdeveloper.app.ws.service.impl;

import com.appsdeveloper.app.ws.io.entity.AddressEntity;
import com.appsdeveloper.app.ws.io.entity.UserEntity;
import com.appsdeveloper.app.ws.io.repositories.AddressesRepository;
import com.appsdeveloper.app.ws.io.repositories.UserRepository;
import com.appsdeveloper.app.ws.service.AddressesService;
import com.appsdeveloper.app.ws.shared.dto.AddressDto;
import org.modelmapper.ModelMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;

@Service
public class AddressesServiceImpl implements AddressesService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AddressesRepository addressesRepository;

    @Override
    public List<AddressDto> getAddresses(String userId) {
//        List<AddressDto> addressDtos = new ArrayList<>();
//        List<AddressEntity> addressEntity = addressesRepository.findByUserId(userId);
//
//        if(addressEntity != null && !addressEntity.isEmpty()){
//            Type listType = new TypeToken<List<String>>() {}.getType();
//            addressDtos = new ModelMapper().map(addressEntity,listType);
//        }
        List<AddressDto> returnValue = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        UserEntity userEntity = userRepository.findByUserId(userId);
        List<AddressEntity> addressEntities = addressesRepository.findAddressEntitiesByUserDetails(userEntity);
        for (AddressEntity addressEntity : addressEntities){
            returnValue.add(modelMapper.map(addressEntity,AddressDto.class));
        }
        return returnValue;
    }

    @Override
    public AddressDto getAddress(String addressId) {
        AddressEntity addressEntity = addressesRepository.findByAddressId(addressId);
        ModelMapper modelMapper = new ModelMapper();
        AddressDto returnValue = null;
        if(addressEntity != null){
            returnValue = modelMapper.map(addressEntity,AddressDto.class);
        }
        return returnValue;
    }
}
