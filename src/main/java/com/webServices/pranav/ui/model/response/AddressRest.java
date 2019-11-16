package com.webServices.pranav.ui.model.response;

import org.springframework.hateoas.RepresentationModel;

public class AddressRest extends RepresentationModel<AddressRest> {

	private String publicAddId;
	private String city;
	private String country;
	private String streetName;
	private String postalCode;
	private String type;

	public String getPublicAddId() {
		return publicAddId;
	}

	public void setPublicAddId(String publicAddId) {
		this.publicAddId = publicAddId;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getStreetName() {
		return streetName;
	}

	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
