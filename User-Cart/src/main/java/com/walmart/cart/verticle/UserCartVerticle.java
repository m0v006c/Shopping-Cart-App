package com.walmart.cart.verticle;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.walmart.cart.service.CartService;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

@Component
public class UserCartVerticle extends AbstractVerticle{

	@Autowired
	private CartService cartService;
	
	private final static Logger logger = LoggerFactory.getLogger(UserCartVerticle.class);
	
	@Override
	public void start(Future<Void> startFuture) throws Exception {
		// TODO Auto-generated method stub
		super.start(startFuture);
		vertx.eventBus().consumer("cart-bus" , this:: onMessage);
		logger.info("{} deployed.",UserCartVerticle.class.getName());
	}
	
	private void onMessage(Message<JsonObject> message) {
		
		String action = message.headers().get("action");
		logger.debug("Action to be taken {} for {}",action,UserCartVerticle.class.getName());
		
		if(!Optional.ofNullable(action).isPresent()) {
			message.reply(new JsonObject().put("status", "Failed").put("Cause", "action is empty."));
		}
		
		switch (action) {
		case "additemtocart":
			cartService.addItemToCart(message);
			break;
		case "removeitemfromcart":
			cartService.removeItemFromCart(message);
			break;
		case "createcart":
			cartService.createCart(message);
			break;
		case "checkoutcart":
			cartService.checkoutCart(message);
			break;
		case "getcartdetails":
			cartService.getCartDetails(message);
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
