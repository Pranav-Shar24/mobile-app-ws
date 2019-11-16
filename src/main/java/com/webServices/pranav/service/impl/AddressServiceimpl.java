package com.webServices.pranav.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webServices.pranav.io.entity.AddressEntity;
import com.webServices.pranav.io.entity.UserEntity;
import com.webServices.pranav.repository.AddressRepo;
import com.webServices.pranav.repository.UserRepo;
import com.webServices.pranav.service.AddressService;
import com.webServices.pranav.shared.dto.AddressDTO;

@Service
public class AddressServiceimpl implements AddressService {

	@Autowired
	private UserRepo repo;

	@Autowired
	private AddressRepo addRepo;

	@Override
	public List<AddressDTO> getAddresses(String userId) {

		List<AddressDTO> returnValue = new ArrayList<>();
		UserEntity userEntity = repo.findByUserId(userId);
		ModelMapper modelMapper = new ModelMapper();
		if (userEntity == null) {
			return returnValue;
		}
		Iterable<AddressEntity> addresses = addRepo.findAllByUserDetails(userEntity);
		for (AddressEntity addressEntity : addresses) {
			returnValue.add(modelMapper.map(addressEntity, AddressDTO.class));
		}
		return returnValue;
	}

	@Override
	public AddressDTO getAddress(String addressId) {

		AddressDTO returnValue = null;
		AddressEntity addressEntity = addRepo.findByPublicAddId(addressId);

		if (addressEntity != null) {
			returnValue = new ModelMapper().map(addressEntity, AddressDTO.class);
		}

		return returnValue;
	}

}
