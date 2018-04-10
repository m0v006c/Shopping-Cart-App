package com.walmart.inventory.verticle;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.walmart.inventory.service.ItemService;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

@Component
public class InventoryVerticle extends AbstractVerticle{
	
	@Autowired
	private ItemService itemService;
	
	private final static Logger logger = LoggerFactory.getLogger(InventoryVerticle.class);
	
	@Override
	public void start(Future<Void> startFuture) throws Exception {
		// TODO Auto-generated method stub
		super.start(startFuture);
		vertx.eventBus().consumer("inventory-bus", this:: onMessage);
		logger.info("{} deployed.",InventoryVerticle.class.getName());
	}
	
	private void onMessage(Message<JsonObject> message) {
		
		String action = message.headers().get("action");
		logger.debug("Action to be taken {} for {}",action,InventoryVerticle.class.getName());
		
		if(!Optional.ofNullable(action).isPresent()) {
			message.reply(new JsonObject().put("status", "Failed").put("Cause", "action is empty."));
		}
		
		switch (action) {
		case "additem":
			itemService.addItem(message);
			break;
		case "removeitem":
			itemService.removeItem(message);
			break;
		case "getavailableitems":
			itemService.getAvailableItem(message);
			break;
		case "validateitems":
			itemService.validateItems(message);
			break;

		default:
			message.reply(new JsonObject().put("status", "Failed").put("Cause", "Invalid action."));
			break;
		}
	}
	
	@Override
	public void stop(Future<Void> stopFuture) throws Exception {
		// TODO Auto-generated method stub
		super.stop(stopFuture);
	}
}
