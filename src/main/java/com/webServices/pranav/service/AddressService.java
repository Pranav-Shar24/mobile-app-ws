package com.webServices.pranav.service;

import java.util.List;

import com.webServices.pranav.shared.dto.AddressDTO;

public interface AddressService {

	List<AddressDTO> getAddresses(String userId);

	AddressDTO getAddress(String publicAddId);
}
