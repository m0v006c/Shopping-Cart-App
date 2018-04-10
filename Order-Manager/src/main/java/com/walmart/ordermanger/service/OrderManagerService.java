package com.walmart.ordermanger.service;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

public interface OrderManagerService {

	public void initiateOrder(Message<JsonObject> message);
	
}
