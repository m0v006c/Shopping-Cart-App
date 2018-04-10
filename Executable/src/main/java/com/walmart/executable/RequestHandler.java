package com.walmart.executable;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.walmart.inventory.bean.Item;
import com.walmart.user.bean.User;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

@Component
public class RequestHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
	
	@Autowired
	private Vertx vertx;
	
	public void getHome(RoutingContext routingContext) {
		logger.info("Home page request.");
		routingContext.response()
		.putHeader("content-type", "text/html")
		.end("<h1>Welcome to Shopping-Cart-App !!</h1>");
	}
	
	public void userRegisteration(RoutingContext routingContext) {
		logger.info("User registeration request.");
		routingContext.request().bodyHandler( buff ->{
			JsonObject json = buff.toJsonObject();
			logger.debug("Request body content"+json.toString());
			String userId = json.getString("userId");
			String name = json.getString("name");
			String password = json.getString("password");
			String contact = json.getString("contact");
			
			if (!Optional.ofNullable(userId).isPresent() || !Optional.ofNullable(password).isPresent()
					|| !Optional.ofNullable(name).isPresent()) {
				routingContext.response().setStatusCode(400)
						.putHeader("content-type", "text/html")
						.end("<h1>User Id, Name and Password can not be empty. Try again!! </h1>");
			}
			else {
				User user = new User(userId, password, name, contact);
				
				JsonObject userJson = new JsonObject(Json.encodePrettily(user));
				
				DeliveryOptions options = new DeliveryOptions();
				options.addHeader("action", "registeruser");
				
				vertx.eventBus().send("user-bus", userJson,options, reply ->{
					if(reply.succeeded()) {
						JsonObject jsonReply = (JsonObject)reply.result().body();
						if("Success".equals(jsonReply.getString("Status"))) {
							routingContext.response().setStatusCode(201)
							.putHeader("content-type", "text/html")
							.end("<h1> User unique Id "+jsonReply.getString("uniqueId")+"</h1>");
						}
						else {
							routingContext.response().setStatusCode(400)
							.putHeader("content-type", "text/html")
							.end("<h1>"+jsonReply.getString("Cause")+"</h1>");
						}
					}
					else {
						routingContext.response().setStatusCode(400)
						.putHeader("content-type", "text/html")
						.end("<h1>"+reply.cause()+"</h1>");
					}
				});
			}
		});
	}

	public void userLogin(RoutingContext routingContext) {
		logger.info("User login request.");
		routingContext.request().bodyHandler( buff ->{
			JsonObject json = buff.toJsonObject();
			logger.debug("Request body content"+json.toString());
			String userId = json.getString("userId");
			String password = json.getString("password");
			
			if (!Optional.ofNullable(userId).isPresent() || !Optional.ofNullable(password).isPresent()) {
				routingContext.response().setStatusCode(400)
						.putHeader("content-type", "text/html")
						.end("<h1>UserId and Password can not be empty. Try again!! </h1>");
			}
			else {
				User user = new User(userId, password, null, null);
				
				JsonObject userJson = new JsonObject(Json.encodePrettily(user));
				
				DeliveryOptions options = new DeliveryOptions();
				options.addHeader("action", "loginuser");
				
				vertx.eventBus().send("user-bus", userJson,options, reply ->{
					if(reply.succeeded()) {
						JsonObject jsonReply = (JsonObject)reply.result().body();
						if("Success".equals(jsonReply.getString("Status"))) {
							routingContext.response().setStatusCode(200)
							.putHeader("content-type", "text/html")
							.end("<h1>"+jsonReply.getString("Message")+"</h1>");
						}
						else {
							routingContext.response().setStatusCode(400)
							.putHeader("content-type", "text/html")
							.end("<h1>"+jsonReply.getString("Cause")+"</h1>");
						}
					}
					else {
						routingContext.response().setStatusCode(400)
						.putHeader("content-type", "text/html")
						.end("<h1>"+reply.cause()+"</h1>");
					}
				});
			}
		});
	}

	public void addItem(RoutingContext routingContext) {
		logger.info("Add item in inventory request.");
		routingContext.request().bodyHandler( buff ->{
			JsonObject json = buff.toJsonObject();
			logger.debug("Request body content"+json.toString());
			String title = json.getString("title");
			String description = json.getString("description");
			int quantity = json.getInteger("quantity");
			double cost = json.getDouble("cost");
			
			if (!Optional.ofNullable(title).isPresent() || !Optional.ofNullable(quantity).isPresent()
					|| !Optional.ofNullable(description).isPresent() || !Optional.ofNullable(cost).isPresent()) {
				routingContext.response().setStatusCode(400)
						.putHeader("content-type", "text/html")
						.end("<h1>Item Title, Decription, Quantity, Cost can not be null. Try again!! </h1>");
			}
			else {
				
				Item item = new Item(title, description, quantity, cost);
				
				JsonObject itemJson = new JsonObject(Json.encodePrettily(item));
				
				DeliveryOptions options = new DeliveryOptions();
				options.addHeader("action", "additem");
				
				vertx.eventBus().send("inventory-bus", itemJson,options, reply ->{
					if(reply.succeeded()) {
						JsonObject jsonReply = (JsonObject)reply.result().body();
						if("Success".equals(jsonReply.getString("Status"))) {
							routingContext.response().setStatusCode(201)
							.putHeader("content-type", "text/html")
							.end("<h1> Item unique Id "+jsonReply.getString("uniqueId")+"</h1>");
						}
						else {
							routingContext.response().setStatusCode(400)
							.putHeader("content-type", "text/html")
							.end("<h1>"+jsonReply.getString("Cause")+"</h1>");
						}
					}
					else {
						routingContext.response().setStatusCode(400)
						.putHeader("content-type", "text/html")
						.end("<h1>"+reply.cause()+"</h1>");
					}
				});
			}
		});
	}

	public void removeItem(RoutingContext routingContext) {

		logger.info("Remove item from inventory request.");
		routingContext.request().bodyHandler( buff ->{
			JsonObject json = buff.toJsonObject();
			logger.debug("Request body content"+json.toString());
			String _id = json.getString("_id");
			
			if (!Optional.ofNullable(_id).isPresent()) {
				routingContext.response().setStatusCode(400)
						.putHeader("content-type", "text/html")
						.end("<h1>Item _id can not be null. Try again!! </h1>");
			}
			else {
				
				JsonObject itemJson = new JsonObject().put("_id", _id);
				
				DeliveryOptions options = new DeliveryOptions();
				options.addHeader("action", "removeitem");
				
				vertx.eventBus().send("inventory-bus", itemJson,options, reply ->{
					if(reply.succeeded()) {
						JsonObject jsonReply = (JsonObject)reply.result().body();
						if("Success".equals(jsonReply.getString("Status"))) {
							routingContext.response().setStatusCode(200)
							.putHeader("content-type", "text/html")
							.end("<h1> Item "+jsonReply.toString()+" removed.</h1>");
						}
						else {
							routingContext.response().setStatusCode(400)
							.putHeader("content-type", "text/html")
							.end("<h1>"+jsonReply.getString("Cause")+"</h1>");
						}
					}
					else {
						routingContext.response().setStatusCode(400)
						.putHeader("content-type", "text/html")
						.end("<h1>"+reply.cause()+"</h1>");
					}
				});
			}
		});
	
	}

	public void addItemToCart(RoutingContext routingContext) {
		logger.info("Add item into cart request.");
		routingContext.request().bodyHandler( buff ->{
			JsonObject json = buff.toJsonObject();
			logger.debug("Request body content"+json.toString());
			String _id = json.getString("_id");
			Map<String, Object> itemMap = json.getJsonObject("items").getMap();
			
			if (!Optional.ofNullable(_id).isPresent() || !Optional.ofNullable(itemMap).isPresent() ) {
				routingContext.response().setStatusCode(400)
						.putHeader("content-type", "text/html")
						.end("<h1>Cart _id, Items can not be null. Try again!! </h1>");
			}
			else {
				
				DeliveryOptions options = new DeliveryOptions();
				options.addHeader("action", "additemtocart");
				
				vertx.eventBus().send("cart-bus", json,options, reply ->{
					if(reply.succeeded()) {
						JsonObject jsonReply = (JsonObject)reply.result().body();
						logger.info("jsonReply ",jsonReply);
						if("Success".equals(jsonReply.getString("Status"))) {
							routingContext.response().setStatusCode(201)
							.putHeader("content-type", "text/html")
							.end("<h1> Item added  to cart "+jsonReply.getString("uniqueId")+"</h1>");
						}
						else {
							routingContext.response().setStatusCode(400)
							.putHeader("content-type", "text/html")
							.end("<h1>"+jsonReply.getString("Cause")+"</h1>");
						}
					}
					else {
						routingContext.response().setStatusCode(400)
						.putHeader("content-type", "text/html")
						.end("<h1>"+reply.cause()+"</h1>");
					}
				});
			}
		});
	}
	
	public void removeItemFromCart(RoutingContext routingContext) {
		logger.info("Remove item from cart request.");
		routingContext.request().bodyHandler( buff ->{
			JsonObject json = buff.toJsonObject();
			logger.debug("Request body content"+json.toString());
			String _id = json.getString("_id");
			Map<String, Object> itemMap = json.getJsonObject("items").getMap();
			
			if (!Optional.ofNullable(_id).isPresent() || !Optional.ofNullable(itemMap).isPresent() ) {
				routingContext.response().setStatusCode(400)
						.putHeader("content-type", "text/html")
						.end("<h1>Cart _id, Items can not be null. Try again!! </h1>");
			}
			else {
				
				DeliveryOptions options = new DeliveryOptions();
				options.addHeader("action", "removeitemfromcart");
				
				vertx.eventBus().send("cart-bus", json,options, reply ->{
					if(reply.succeeded()) {
						JsonObject jsonReply = (JsonObject)reply.result().body();
						if("Success".equals(jsonReply.getString("Status"))) {
							routingContext.response().setStatusCode(201)
							.putHeader("content-type", "text/html")
							.end("<h1> Item removed  from cart "+jsonReply.getString("uniqueId")+"</h1>");
						}
						else {
							routingContext.response().setStatusCode(400)
							.putHeader("content-type", "text/html")
							.end("<h1>"+jsonReply.getString("Cause")+"</h1>");
						}
					}
					else {
						routingContext.response().setStatusCode(400)
						.putHeader("content-type", "text/html")
						.end("<h1>"+reply.cause()+"</h1>");
					}
				});
			}
		});
	}

	public void checkoutCart(RoutingContext routingContext) {
		logger.info("Checkout cart request.");
		routingContext.request().bodyHandler( buff ->{
			JsonObject json = buff.toJsonObject();
			logger.debug("Request body content"+json.toString());
			String _id = json.getString("_id");
			String associatedUser = json.getString("associatedUser");
			
			if (!Optional.ofNullable(_id).isPresent() || !Optional.ofNullable(associatedUser).isPresent() ) {
				routingContext.response().setStatusCode(400)
						.putHeader("content-type", "text/html")
						.end("<h1>Cart _id, associatedUser can not be null. Try again!! </h1>");
			}
			else {
				
				DeliveryOptions options = new DeliveryOptions();
				options.addHeader("action", "checkoutcart");
				
				vertx.eventBus().send("cart-bus", json,options, reply ->{
					if(reply.succeeded()) {
						JsonObject jsonReply = (JsonObject)reply.result().body();
						if("Success".equals(jsonReply.getString("Status"))) {
							routingContext.response().setStatusCode(201)
							.putHeader("content-type", "text/html")
							.end("<h1> Order "+jsonReply.getString("uniqueId")+" has been placed.</h1>");
						}
						else {
							
							if(jsonReply.getString("Cause") == null) {
								
								routingContext.response().setStatusCode(400)
								.putHeader("content-type", "text/html")
								.end("<h1> Items "+jsonReply.getJsonArray("items")+" are short.</h1>");
							}
							else {
								routingContext.response().setStatusCode(400)
								.putHeader("content-type", "text/html")
								.end("<h1> "+jsonReply.getString("Cause")+"</h1>");
								
							}
						}
					}
					else {
						routingContext.response().setStatusCode(400)
						.putHeader("content-type", "text/html")
						.end("<h1>"+reply.cause()+"</h1>");
					}
				});
			}
		});
	}
}
