package com.webServices.pranav.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.webServices.pranav.exception.UserServiceException;
import com.webServices.pranav.io.entity.UserEntity;
import com.webServices.pranav.repository.UserRepo;
import com.webServices.pranav.service.UserService;
import com.webServices.pranav.shared.Utils;
import com.webServices.pranav.shared.dto.AddressDTO;
import com.webServices.pranav.shared.dto.UserDto;
import com.webServices.pranav.ui.model.response.ErrorMessages;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private Utils utils;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public UserDto createUser(UserDto user) {

		if (userRepo.findUserByEmail(user.getEmail()) != null) {
			throw new RuntimeException("Record already Exists!!");
		}

		for (int i = 0; i < user.getAddresses().size(); i++) {
			AddressDTO address = user.getAddresses().get(i);
			address.setUserDetails(user);
			address.setPublicAddId(utils.generateAddressId(15));
			user.getAddresses().set(i, address);

		}
		// BeanUtils.copyProperties(user, userEntity);
		ModelMapper modelMapper = new ModelMapper();
		UserEntity userEntity = modelMapper.map(user, UserEntity.class);

		String generateUserId = utils.generateUserId(15);

		userEntity.setUserId(generateUserId);
		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));

		UserEntity storedUserDetails = userRepo.save(userEntity);

		// BeanUtils.copyProperties(storedUserDetails, returnedValue);
		UserDto returnedValue = modelMapper.map(storedUserDetails, UserDto.class);
		return returnedValue;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		UserEntity userEntity = userRepo.findUserByEmail(email);

		if (userEntity == null) {
			throw new UsernameNotFoundException(email);
		}
		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
	}

	@Override
	public UserDto getUser(String mail) {

		UserEntity userEntity = userRepo.findUserByEmail(mail);

		if (userEntity == null) {
			throw new UsernameNotFoundException(mail);
		}

		UserDto returnedValue = new UserDto();
		BeanUtils.copyProperties(userEntity, returnedValue);
		return returnedValue;

	}

	@Override
	public UserDto getUserByUserId(String userId) {

		UserDto returnedValue = new UserDto();
		UserEntity userEntity = userRepo.findByUserId(userId);
		if (userEntity == null) {
			throw new UsernameNotFoundException(userId);
		}
		BeanUtils.copyProperties(userEntity, returnedValue);
		return returnedValue;
	}

	@Override
	public UserDto updateUser(String id, UserDto userDto) {

		UserDto returnedValue = new UserDto();
		UserEntity userEntity = userRepo.findByUserId(id);
		if (userEntity == null) {
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessages());
		}

		userEntity.setFirstName(userDto.getFirstName());
		userEntity.setLastName(userDto.getLastName());

		UserEntity updatedValue = userRepo.save(userEntity);
		BeanUtils.copyProperties(updatedValue, returnedValue);

		return returnedValue;
	}

	@Override
	public void deleteUser(String userId) {

		UserEntity userEntity = userRepo.findByUserId(userId);
		if (userEntity == null) {
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessages());
		}

		userRepo.delete(userEntity);
	}

	@Override
	public List<UserDto> getUsers(int page, int limit) {
		List<UserDto> returnedValue = new ArrayList<>();
		if (page > 0) {
			page = page - 1;
		}
		Pageable pageable = PageRequest.of(page, limit);
		Page<UserEntity> userPage = userRepo.findAll(pageable);
		List<UserEntity> users = userPage.getContent();
		for (UserEntity userEntity : users) {
			UserDto userDto = new UserDto();
			BeanUtils.copyProperties(userEntity, userDto);
			returnedValue.add(userDto);
		}

		return returnedValue;
	}
}
