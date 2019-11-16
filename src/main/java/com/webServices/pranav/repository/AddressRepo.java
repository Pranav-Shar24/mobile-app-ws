package com.webServices.pranav.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.webServices.pranav.io.entity.AddressEntity;
import com.webServices.pranav.io.entity.UserEntity;

@Repository
public interface AddressRepo extends CrudRepository<AddressEntity, Long> {

	List<AddressEntity> findAllByUserDetails(UserEntity userEntity);
	
	AddressEntity findByPublicAddId(String  addressId);
	
}
