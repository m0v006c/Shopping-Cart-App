
package com.walmart.executable;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.walmart.cart.verticle.UserCartVerticle;
import com.walmart.executable.verticle.HttpServerVerticle;
import com.walmart.inventory.verticle.InventoryVerticle;
import com.walmart.ordermanger.verticle.OrderManagerVerticle;
import com.walmart.user.verticle.UserVerticle;

import io.vertx.core.Vertx;

public class Executor {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		ApplicationContext context = new AnnotationConfigApplicationContext(CommonBeans.class, ParentBeans.class);
		
		Vertx vertx = context.getBean("vertx",Vertx.class);

		vertx.deployVerticle(context.getBean(HttpServerVerticle.class));
		//vertx.deployVerticle(context.getBean(MongoClientService.class),options);
		vertx.deployVerticle(context.getBean(UserCartVerticle.class));
		vertx.deployVerticle(context.getBean(UserVerticle.class));
		vertx.deployVerticle(context.getBean(OrderManagerVerticle.class));
		vertx.deployVerticle(context.getBean(InventoryVerticle.class));
	}

}
