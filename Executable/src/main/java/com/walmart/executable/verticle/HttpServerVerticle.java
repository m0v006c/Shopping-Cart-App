package com.walmart.executable.verticle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.walmart.executable.RequestHandler;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;

@Component
public class HttpServerVerticle extends AbstractVerticle{

	private static final Logger logger = LoggerFactory.getLogger(HttpServerVerticle.class);
	
	@Autowired
	private RequestHandler requestHandler;
	
	@Autowired
	private DeploymentOptions deploymentOptions; 
	
	@Override
	public void start(Future<Void> startFuture) throws Exception {
		// TODO Auto-generated method stub
		super.start(startFuture);
		initHttpServer();
	}
	
	private void initHttpServer() {

		
		HttpServer server = vertx.createHttpServer();
		Router router = Router.router(vertx);
		router.route("/api/home").handler(requestHandler::getHome);
		router.post("/api/registeruser").handler(requestHandler::userRegisteration);
		router.post("/api/loginuser").handler(requestHandler::userLogin);
		router.post("/api/additem").handler(requestHandler::addItem);
		router.post("/api/removeitem").handler(requestHandler::removeItem);
		router.post("/api/additemtocart").handler(requestHandler::addItemToCart);
		router.post("/api/removeitemfromcart").handler(requestHandler::removeItemFromCart);
		router.post("/api/checkoutcart").handler(requestHandler::checkoutCart);
		server.requestHandler(router::accept).listen(deploymentOptions.getConfig().getInteger("http.port"), ar -> {
			if(ar.succeeded()) {
				logger.info("Http Server started on port "+deploymentOptions.getConfig().getInteger("http.port"));
			}else {
				logger.info("Http Server failed to start on port "+deploymentOptions.getConfig().getInteger("http.port"));
			}
		});

	}
	
	@Override
	public void stop(Future<Void> stopFuture) throws Exception {
		// TODO Auto-generated method stub
		super.stop(stopFuture);
	}
}
