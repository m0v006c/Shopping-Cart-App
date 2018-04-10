package com.walmart.cart.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.walmart.cart.bean.Cart;
import com.walmart.cart.service.CartService;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;

@Component
public class CartServiceImpl implements CartService{

	private Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);
	
	@Autowired
	private MongoClient mongoClient;
	
	@Autowired
	private Vertx vertx;
	
	@Override
	public void addItemToCart(Message<JsonObject> message) {
		// TODO Auto-generated method stub
		logger.debug("Request for adding item into cart.");
		String cartId = message.body().getString("_id");
		
		mongoClient.find("cart", new JsonObject()
				.put("_id", cartId)
				, result ->{
					if(result.succeeded()) {
						JsonObject cartJson = result.result().stream().findFirst().get();
						Cart cart = Json.decodeValue(cartJson.toString(), Cart.class);
						
						if(Optional.ofNullable(cart).isPresent()) {
							Cart toBeAddedCart = Json.decodeValue(message.body().toString(), Cart.class);
							cart.addItem(toBeAddedCart);
							mongoClient.findOneAndUpdate("cart",
									new JsonObject().put("_id", cart.get_id()), 
									new JsonObject().put("$set", new JsonObject(Json.encodePrettily(cart))), updateResult ->{
										if(updateResult.succeeded()) {
											message.reply(new JsonObject().put("Status", "Success").put("uniqueId", updateResult.result().getString("_id")));
										}
										else {
											message.reply(new JsonObject().put("Status", "Failed").put("Cause", updateResult.cause()));
										}
									});
						}
						else {
							message.reply(new JsonObject().put("Status", "Failed").put("Cause", "Cart not found."));
						}
					}
					else {
						message.reply(new JsonObject().put("Status", "Failed").put("Cause", result.cause()));
					}
				});
		
	}

	@Override
	public void removeItemFromCart(Message<JsonObject> message) {
		// TODO Auto-generated method stub
		logger.debug("Request for removing item from cart.");
		String cartId = message.body().getString("_id");
		
		mongoClient.find("cart", new JsonObject()
				.put("_id", cartId)
				, result ->{
					if(result.succeeded()) {
						JsonObject cartJson = result.result().stream().findFirst().get();
						Cart cart = Json.decodeValue(cartJson.toString(), Cart.class);
						
						if(Optional.ofNullable(cart).isPresent()) {
							Cart toBeRemovedCart = Json.decodeValue(message.body().toString(), Cart.class);
							cart.removeItem(toBeRemovedCart);
							mongoClient.findOneAndUpdate("cart",
									new JsonObject().put("_id", cart.get_id()), 
									new JsonObject().put("$set", new JsonObject(Json.encodePrettily(cart))), updateResult ->{
										if(updateResult.succeeded()) {
											message.reply(new JsonObject().put("Status", "Success").put("uniqueId", updateResult.result().getString("_id")));
										}
										else {
											message.reply(new JsonObject().put("Status", "Failed").put("Cause", updateResult.cause()));
										}
									});
						}
						else {
							message.reply(new JsonObject().put("Status", "Failed").put("Cause", "Cart not found."));
						}
					}
					else {
						message.reply(new JsonObject().put("Status", "Failed").put("Cause", result.cause()));
					}
				});
		
	}

	@Override
	public void checkoutCart(Message<JsonObject> message) {
		// TODO Auto-generated method stub
		logger.debug("Request for checkout cart.");
		String cartId = message.body().getString("_id");
		String associatedUser = message.body().getString("associatedUser");
		
		if(Optional.ofNullable(cartId).isPresent()) {
			
			mongoClient.find("cart", new JsonObject().put("_id", cartId), result ->{
				
				if(result.succeeded()) {
					JsonObject cartJson = result.result().stream().findFirst().get();
					Cart cart = Json.decodeValue(cartJson.toString(), Cart.class);
					
					DeliveryOptions option = new DeliveryOptions();
					option.addHeader("action", "placeorder");

					JsonObject itemJson = new JsonObject();
					itemJson.put("associatedUser", associatedUser);
					itemJson.put("items", new JsonObject(Json.encodePrettily(cart.getItems())));
					
					vertx.eventBus().send("order-manager-bus", itemJson, option , reply ->{
						if(reply.succeeded()) {
							logger.debug("reply from cart "+reply.result().body());
							if("Success".equals(((JsonObject)reply.result().body()).getString("Status"))) {
								emptyCart(cart);
								message.reply(new JsonObject().put("Status", "Success").put("uniqueId", ((JsonObject)reply.result().body()).getString("uniqueId")));
							}
							else {
								if(((JsonObject)reply.result().body()).getString("Cause") == null) {
									message.reply(new JsonObject().put("Status", "Failed").put("Cause", "Few items are short.")
											.put("items", ((JsonObject)reply.result().body()).getJsonArray("items")));
								}
								else {
									message.reply(new JsonObject().put("Status", "Failed").put("Cause", reply.cause()));
								}
							}
						}
						else {
							message.reply(new JsonObject().put("Status", "Failed").put("Cause", reply.cause().getMessage()));
						}
					});
				}
				else {
					message.reply(new JsonObject().put("Status", "Failed").put("Cause", result.cause().getMessage()));
				}
			});
		}
	}
	
	private void emptyCart(Cart cart) {
		cart.setItems(null);
		mongoClient.findOneAndUpdate("cart", new JsonObject().put("_id", cart.get_id()),
				new JsonObject().put("items", new JsonObject().put("$set", cart.getItems())), result ->{
					if(result.succeeded()) {
						logger.debug("Cart has been empty. Result is {}",result.result());
					}
					else {
						logger.error("Cart has not been empty. Cause is {}",result.cause());
					}
				});
	}

	@Override
	public void createCart(Message<JsonObject> message) {
		// TODO Auto-generated method stub
		logger.debug("Request for create new cart.");
		String associatedUser = message.body().getString("associatedUser");
		
		if(Optional.ofNullable(associatedUser).isPresent()) {
			
			Cart cart = new Cart(associatedUser, null);
			JsonObject cartJson = new JsonObject(Json.encode(cart));
			if(cartJson.getString("_id") == null) {
				cartJson.remove("_id");
			}
			
			mongoClient.insert("cart", cartJson, result ->{
				if(result.succeeded()) {
					message.reply(new JsonObject().put("Status", "Success").put("uniqueId", result.result()));
				}
				else {
					message.reply(new JsonObject().put("Status", "Failed").put("Cause", result.cause()));
				}
			});
		}
		else {
			message.reply(new JsonObject().put("Status", "Failed").put("Cause", "Associated User can not be empty."));
		}
	}

	@Override
	public void getCartDetails(Message<JsonObject> message) {
		// TODO Auto-generated method stub
		logger.debug("Request to get cart details.");
		String associatedUser = message.body().getString("associatedUser");
		
		if(Optional.ofNullable(associatedUser).isPresent()) {
			
			mongoClient.find("cart", new JsonObject().put("associatedUser", associatedUser), 
					result->{
						
						if(result.succeeded()) {
							JsonObject cartJson = result.result().stream().findFirst().get();
							Cart cart = Json.decodeValue(cartJson.toString(), Cart.class);

							if(Optional.ofNullable(cart.getItems()).isPresent()) {
								message.reply(new JsonObject().put("Status", "Success").put("items", new JsonObject(Json.encodePrettily(cart.getItems()))));
							}
						}
						else {
							message.reply(new JsonObject().put("Status", "Failed").put("Cause", result.cause()));
						}
					});
		}
		else {
			message.reply(new JsonObject().put("Status", "Failed").put("Cause", "Associated User can not be empty."));
		}
	}
}
