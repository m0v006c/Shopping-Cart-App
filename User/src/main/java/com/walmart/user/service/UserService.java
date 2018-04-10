package com.walmart.user.service;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

public interface UserService {
	
	public void registerUser(Message<JsonObject> message);
	
	public void loginUser(Message<JsonObject> message);
	
	public void openCart(Message<JsonObject> message);
}
