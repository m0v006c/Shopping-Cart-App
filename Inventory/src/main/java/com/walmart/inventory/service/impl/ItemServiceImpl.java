package com.walmart.inventory.service.impl;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.walmart.inventory.service.ItemService;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;

@Component
public class ItemServiceImpl implements ItemService{

	private final static Logger logger = LoggerFactory.getLogger(ItemServiceImpl.class);
	
	@Autowired
	private MongoClient mongoClient;
	
	public void addItem(Message<JsonObject> message) {
		// TODO Auto-generated method stub
		logger.debug("Request for adding item into inventory.");
		JsonObject itemJson = message.body();
		if(itemJson.getString("_id") ==  null) {
			itemJson.remove("_id");
		}
		
		mongoClient.insert("item", itemJson, result ->{
			if(result.succeeded()) {
				message.reply(new JsonObject().put("Status", "Success").put("uniqueId", result.result()));
			}
			else {
				message.reply(new JsonObject().put("Status", "Failed").put("Cause", result.cause()));
			}
		});
	}

	public void removeItem(Message<JsonObject> message) {
		// TODO Auto-generated method stub
		logger.debug("Request for removing item into inventory.");

		mongoClient.findOneAndDelete("item", new JsonObject()
				.put("_id", message.body().getString("_id")), result->{
					if(result.succeeded()) {
						message.reply(new JsonObject().put("Status", "Success").put("item", result.result()));
					}
					else {
						message.reply(new JsonObject().put("Status", "Failed").put("Cause", result.cause()));
					}
				});
	}

	public void getAvailableItem(Message<JsonObject> message) {
		// TODO Auto-generated method stub
		logger.debug("Request for fetch available items from inventory.");
		mongoClient.find("item", new JsonObject(), result ->{
			if(result.failed()) {
				message.reply(new JsonObject().put("Status", "Failed").put("Cause", result.cause()));
			}
			else {
				message.reply(new JsonObject().put("Status", "Success").put("Items", result.result()));
			}
		});
	}
	
	public void validateItems(Message<JsonObject> message) {
		logger.debug("Request for validating items in inventory.");
		
		Map<String, Object> itemMap = message.body().getMap();
		
		Set<String> items = itemMap.keySet();
		
		JsonArray shortItemJson = new JsonArray();
		JsonArray foundItemJson = new JsonArray();
		
		AtomicInteger countDown = new AtomicInteger(items.size());
		
		items.stream().forEach(item ->{
			logger.info("item "+item);
			
			mongoClient.find("item",new JsonObject().put("_id", item).put("quantity", new JsonObject()
					.put("$gt", (int)itemMap.get(item))), result ->{

						if(result.failed() || (result.succeeded() &&  result.result().size() != 1)) {
							JsonObject shortI = result.result().stream().findFirst().get();
							shortItemJson.add(shortI);
							countDown.decrementAndGet();
							synchronized (countDown) {
								countDown.notifyAll();
							}
						}
						else {
							JsonObject foundI = result.result().stream().findFirst().get();
							foundItemJson.add(foundI);
							countDown.decrementAndGet();
							synchronized (countDown) {
								countDown.notifyAll();
							}
						}
					
					});
		});
		
		new Thread(new CountDownRunnable(countDown, message, shortItemJson, foundItemJson)).start();
	}
	
	class CountDownRunnable implements Runnable{

		private AtomicInteger countDown;
		private Message<JsonObject> message;
		private JsonArray shortItemJson;
		private JsonArray foundItemJson;
		
		public CountDownRunnable(AtomicInteger countDown, Message<JsonObject> message,JsonArray shortItemJson, JsonArray foundItemJson) {
			// TODO Auto-generated constructor stub
			this.countDown = countDown;
			this.message = message;
			this.shortItemJson = shortItemJson;
			this.foundItemJson = foundItemJson;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			synchronized (countDown) {
				if(countDown.get() > 0) {
					while(countDown.get() > 0) {
						try {
							countDown.wait();
							countDown.notifyAll();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				
			}

			if(shortItemJson.size() == 0) {
				logger.debug("Found Items {} in inventory ",foundItemJson);
				message.reply(new JsonObject().put("Status", "Success").put("items", foundItemJson));
			}
			else {
				logger.debug("Short Items {} in inventory",shortItemJson);
				message.reply(new JsonObject().put("Status", "Failed").put("items", shortItemJson));
			}
			
		}
		
	}
}
