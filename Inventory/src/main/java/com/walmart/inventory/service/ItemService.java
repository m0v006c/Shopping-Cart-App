package com.walmart.inventory.service;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

public interface ItemService {

	public void addItem(Message<JsonObject> message);
	
	public void removeItem(Message<JsonObject> message);
	
	public void getAvailableItem(Message<JsonObject> message);
	
	public void validateItems(Message<JsonObject> message);
}
