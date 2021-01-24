package com.appsdeveloper.app.ws.service;

import com.appsdeveloper.app.ws.shared.dto.AddressDto;

import java.util.List;

public interface AddressesService {
    List<AddressDto> getAddresses(String userId);
    AddressDto getAddress(String addressId);
}
