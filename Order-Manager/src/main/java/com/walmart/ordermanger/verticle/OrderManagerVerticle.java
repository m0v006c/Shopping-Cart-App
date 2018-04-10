package com.walmart.ordermanger.verticle;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.walmart.ordermanger.service.OrderManagerService;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

@Component
public class OrderManagerVerticle extends AbstractVerticle{

	@Autowired
	private OrderManagerService orderMangerService;
	
	private final static Logger logger = LoggerFactory.getLogger(OrderManagerVerticle.class);
	
	@Override
	public void start(Future<Void> startFuture) throws Exception {
		// TODO Auto-generated method stub
		super.start(startFuture);
		vertx.eventBus().consumer("order-manager-bus", this::onMessage);
		logger.info("{} deployed.",OrderManagerVerticle.class.getName());
	}
	
	private void onMessage(Message<JsonObject> message) {
		
		String action = message.headers().get("action");
		logger.debug("Action to be taken {} for {}",action,OrderManagerVerticle.class.getName());
		
		if(!Optional.ofNullable(action).isPresent()) {
			message.reply(new JsonObject().put("status", "Failed").put("Cause", "action is empty."));
		}
		
		switch (action) {
		case "placeorder":
			orderMangerService.initiateOrder(message);
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
