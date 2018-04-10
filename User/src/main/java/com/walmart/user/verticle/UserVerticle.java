package com.walmart.user.verticle;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.walmart.user.service.UserService;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

@Component
public class UserVerticle extends AbstractVerticle{

	private final static Logger logger = LoggerFactory.getLogger(UserVerticle.class);
	
	@Autowired
	private UserService userService;
	
	@Override
	public void start(Future<Void> startFuture) throws Exception {
		// TODO Auto-generated method stub
		super.start(startFuture);
		
		vertx.eventBus().consumer("user-bus", this::onMessage);
		logger.info("{} deployed.",UserVerticle.class.getName());
	}
	
	private void onMessage(Message<JsonObject> message) {
		
		String action = message.headers().get("action");
		logger.debug("Action to be taken {} for {}",action,UserVerticle.class.getName());
		
		if(!Optional.ofNullable(action).isPresent()) {
			message.reply(new JsonObject().put("status", "Failed").put("Cause", "action is empty."));
		}
		
		switch (action) {
		case "registeruser":
			userService.registerUser(message);
			break;
		case "loginuser":
			userService.loginUser(message);
			break;
		case "opencart":
			userService.openCart(message);
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
