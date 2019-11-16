package com.webServices.pranav.ui.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.webServices.pranav.exception.UserServiceException;
import com.webServices.pranav.service.AddressService;
import com.webServices.pranav.service.UserService;
import com.webServices.pranav.shared.dto.AddressDTO;
import com.webServices.pranav.shared.dto.UserDto;
import com.webServices.pranav.ui.model.request.UserDetailsRequestModel;
import com.webServices.pranav.ui.model.response.AddressRest;
import com.webServices.pranav.ui.model.response.ErrorMessages;
import com.webServices.pranav.ui.model.response.OperationStatusModel;
import com.webServices.pranav.ui.model.response.RequestOperationStatus;
import com.webServices.pranav.ui.model.response.UserRest;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private AddressService addressService;

	@GetMapping(path = "/{id}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public UserRest getUser(@PathVariable String id) {
		UserRest returnValue = new UserRest();
		UserDto userDetails = userService.getUserByUserId(id);
		BeanUtils.copyProperties(userDetails, returnValue);

		return returnValue;
	}

	@PostMapping(consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {

		UserRest returnValue = new UserRest();

		if (userDetails.getFirstName().isEmpty())
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD);

		// UserDto userDto = new UserDto();
		// BeanUtils.copyProperties(userDetails, userDto);

		ModelMapper modelMapper = new ModelMapper();
		UserDto userDto = modelMapper.map(userDetails, UserDto.class);

		UserDto createdUser = userService.createUser(userDto);
		// BeanUtils.copyProperties(createdUser, returnValue);
		returnValue = modelMapper.map(createdUser, UserRest.class);
		return returnValue;
	}

	@PutMapping(path = "/{id}", consumes = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_XML_VALUE,
					MediaType.APPLICATION_JSON_VALUE })
	public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {
		UserRest returnValue = new UserRest();

		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userDetails, userDto);

		UserDto updatedUser = userService.updateUser(id, userDto);
		BeanUtils.copyProperties(updatedUser, returnValue);
		return returnValue;
	}

	@DeleteMapping(path = "/{id}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public OperationStatusModel deleteUser(@PathVariable String id) {

		OperationStatusModel returnValue = new OperationStatusModel();
		returnValue.setOperationName(RequestOperationName.DELETE.name());
		userService.deleteUser(id);
		returnValue.setOprationResult(RequestOperationStatus.SUCCESS.name());

		return returnValue;
	}

	@GetMapping(produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE,
			"application/hal+json" })
	public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "limit", defaultValue = "25") int limit) {
		List<UserRest> returnValue = new ArrayList<>();
		List<UserDto> users = userService.getUsers(page, limit);

		for (UserDto userDto : users) {
			UserRest userModel = new UserRest();
			BeanUtils.copyProperties(userDto, userModel);
			returnValue.add(userModel);
		}

		return returnValue;
	}

	@GetMapping(path = "/{id}/addresses", produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE, "application/hal+json" })
	public CollectionModel<AddressRest> getUserAddresses(@PathVariable String id) {
		List<AddressRest> addressesListRestModel = new ArrayList<>();
		List<AddressDTO> addressDto = addressService.getAddresses(id);
		if (addressDto != null && !addressDto.isEmpty()) {

			ModelMapper modelMapper = new ModelMapper();
			Type listType = new TypeToken<List<AddressRest>>() {
			}.getType();
			addressesListRestModel = modelMapper.map(addressDto, listType);

			for (AddressRest addressRest : addressesListRestModel) {
				Link addressLink = linkTo(
						methodOn(UserController.class).getUserAddress(id, addressRest.getPublicAddId())).withSelfRel();
				addressRest.add(addressLink);
				Link userLink = linkTo(methodOn(UserController.class).getUser(id)).withRel("user");
				addressRest.add(userLink);
			}
		}

		// return (CollectionModel<AddressRest>) addressesListRestModel;
		return new CollectionModel<>(addressesListRestModel);
	}

	@GetMapping(path = "/{userId}/addresses/{addressId}", produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public EntityModel<AddressRest> getUserAddress(@PathVariable String userId, @PathVariable String addressId) {

		AddressDTO addressDto = addressService.getAddress(addressId);
		ModelMapper modelMapper = new ModelMapper();
		Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(userId, addressId)).withSelfRel();
		Link userLink = linkTo(UserController.class).slash(userId).withRel("user");
		Link addressesLink = linkTo(methodOn(UserController.class).getUserAddresses(userId)).withRel("addresses");
		AddressRest addressRestModel = modelMapper.map(addressDto, AddressRest.class);
		addressRestModel.add(addressLink);
		addressRestModel.add(userLink);
		addressRestModel.add(addressesLink);
		return new EntityModel<>(addressRestModel);

	}
}
