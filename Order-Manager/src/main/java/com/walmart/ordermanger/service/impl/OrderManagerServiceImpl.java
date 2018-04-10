package com.walmart.ordermanger.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.walmart.inventory.bean.Item;
import com.walmart.ordermanger.bean.Order;
import com.walmart.ordermanger.bean.OrderItem;
import com.walmart.ordermanger.service.OrderManagerService;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;

@Component
public class OrderManagerServiceImpl implements OrderManagerService{
	
	private final static Logger logger  = LoggerFactory.getLogger(OrderManagerServiceImpl.class);
	
	@Autowired
	private MongoClient mongoClient;
	
	@Autowired
	private Vertx vertx;
	
	public void initiateOrder(Message<JsonObject> message) {
		// TODO Auto-generated method stub
		logger.debug("Request for initiating order.");
		String associatedUser = message.body().getString("associatedUser");
		Map<String, Object> itemMap = message.body().getJsonObject("items").getMap();
		
		DeliveryOptions option  = new DeliveryOptions();
		option.addHeader("action", "validateitems");
		
		vertx.eventBus().send("inventory-bus", message.body().getJsonObject("items"), option , reply ->{
			
			if(reply.succeeded()) {
				JsonObject jsonReply = (JsonObject)reply.result().body();
				
				if("Success".equals(jsonReply.getString("Status"))) {
					List<OrderItem> orderItems = new ArrayList<>();
					JsonArray itemsAr = jsonReply.getJsonArray("items");
					itemsAr.stream().forEach(itemJson ->{
						Item item = Json.decodeValue(itemJson.toString(), Item.class);
						orderItems.add(new OrderItem(item, (Integer)itemMap.get(item.get_id())));
						
					});
					
					Order order = new Order();
					order.setAssociateduser(associatedUser);
					order.setOrderdate(new Date());
					order.setStatus("PLACED");
					order.setItems(orderItems);
					
					JsonObject orderJson = new JsonObject(Json.encodePrettily(order));
					if(orderJson.getString("_id") == null) {
						orderJson.remove("_id");
					}
					
					mongoClient.insert("order", orderJson, result ->{
						if(result.succeeded()) {
							logger.debug("Order created with orderId {}.",result.result().toString());
							JsonObject output = new JsonObject().put("Status", "Success").put("uniqueId", result.result().toString());
							message.reply(output);
						}
						else {
							message.reply(new JsonObject().put("Status", "Failed").put("Cause", result.cause().toString()));
						}
					});
				}
				else {
					message.reply(new JsonObject().put("Status", "Failed").put("items", jsonReply.getJsonArray("items")));
				}
			}
			else {
				message.reply(new JsonObject().put("Status", "Failed"));
			}
		});
	}
}
