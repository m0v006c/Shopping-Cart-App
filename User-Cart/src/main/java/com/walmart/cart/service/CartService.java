package com.walmart.cart.service;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

public interface CartService {
	
	public void createCart(Message<JsonObject> message);

	public void addItemToCart(Message<JsonObject> message);
	
	public void removeItemFromCart(Message<JsonObject> message);
	
	public void checkoutCart(Message<JsonObject> message);
	
	public void getCartDetails(Message<JsonObject> message);
}
