package com.walmart.user.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.walmart.user.bean.User;
import com.walmart.user.service.UserService;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;

@Component
public class UserServiceImpl implements UserService{

	@Autowired
	private MongoClient mongoClient;
	
	private final static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	
	@Autowired
	private Vertx vertx;
	
	@Override
	public void registerUser(Message<JsonObject> message) {
		// TODO Auto-generated method stub
		logger.debug("Request for user registeration.");
		String userId = message.body().getString("userId");
		
		mongoClient.count("user", new JsonObject()
				.put("userId", userId), result ->{
					if(result.succeeded()) {
						if(result.result() > 0) {
							message.reply(new JsonObject().put("Status", "Failed").put("Cause", "UserId already exist."));
						}
						else {
							User user = Json.decodeValue(message.body().toString(), User.class);
							
							if(!Optional.ofNullable(user).isPresent()) {
								message.reply(new JsonObject().put("Status", "Failed").put("Cause", "Invalid User details."));
							}

							JsonObject ascUserJson = new JsonObject().put("associatedUser", userId);
							
							DeliveryOptions options = new DeliveryOptions();
							options.addHeader("action", "createcart");
							
							vertx.eventBus().send("cart-bus",ascUserJson, options, reply ->{
								if(reply.succeeded()) {
									user.setCartId(((JsonObject)reply.result().body()).getString("uniqueId"));
									
									JsonObject userJson = new JsonObject(Json.encodePrettily(user));
									
									if(userJson.getString("_id") == null) {
										userJson.remove("_id");
									}
									
									mongoClient.insert("user", userJson, userResult ->{
										if(userResult.failed()) {
											message.reply(new JsonObject().put("Status", "Failed").put("Cause", userResult.cause()));
										}
										else {
											message.reply(new JsonObject().put("Status", "Success").put("uniqueId", userResult.result()));
										}
									});
								}
								else {
									message.reply(new JsonObject().put("Status", "Failed").put("Cause", reply.cause()));
								}
							});
						}
					}
					else {
						message.reply(new JsonObject().put("Status", "Failed").put("Cause", result.cause()));
					}
				});
		
	}
	
	@Override
	public void loginUser(Message<JsonObject> message) {
		// TODO Auto-generated method stub
		logger.debug("Request for user login.");
		String userId = message.body().getString("userId");
		String password = message.body().getString("password");

		mongoClient.count("user", new JsonObject()
				.put("userId", userId)
				.put("password", password), result ->{
					if(result.succeeded()) {
						if(result.result() > 0) {
							message.reply(new JsonObject().put("Status", "Success").put("Message", "User logged in."));
						}
						else {
							message.reply(new JsonObject().put("Status", "Failed").put("Cause", "UserId or Password might be incorrect."));
						}
					}
					else {
						message.reply(new JsonObject().put("Status", "Failed").put("Cause", result.cause()));
					}
				});
	}

	@Override
	public void openCart(Message<JsonObject> message) {
		// TODO Auto-generated method stub
		logger.debug("Request for open cart.");
		JsonObject ascUserJson = new JsonObject();
		ascUserJson.put("associatedUser", message.body().getString("associatedUser"));
		
		DeliveryOptions option = new DeliveryOptions();
		option.addHeader("action", "getcartdetails");
		
		vertx.eventBus().send("cart-bus", ascUserJson, option, reply ->{
			
			if(reply.succeeded()) {
				JsonObject itemsJson = (JsonObject)reply.result().body();
				
				message.reply(new JsonObject().put("Status", "Success").put("items", itemsJson.getJsonArray("items")));
			}
			else {
				message.reply(new JsonObject().put("Status", "Failed").put("Cause", reply.cause()));
			}
		});
	}

}
