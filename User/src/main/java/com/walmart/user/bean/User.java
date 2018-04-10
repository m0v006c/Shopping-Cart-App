package com.walmart.user.bean;

public class User {
	
	private String _id;
	private String userId;
	private String password;
	private String name;
	private String contact;
	private String cartId;
	
	public User() {
		// TODO Auto-generated constructor stub
	}
	
	public User(String userId, String password, String name, String contact) {
		this.userId = userId;
		this.password = password;
		this.name = name;
		this.contact = contact;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getCartId() {
		return cartId;
	}

	public void setCartId(String cartId) {
		this.cartId = cartId;
	}
	
	

}
