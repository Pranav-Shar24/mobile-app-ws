package com.webServices.pranav.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.webServices.pranav.io.entity.UserEntity;

@Repository
public interface UserRepo extends PagingAndSortingRepository<UserEntity, Long> {

	UserEntity findUserByEmail(String mail);

	UserEntity findByUserId(String userId);


}
