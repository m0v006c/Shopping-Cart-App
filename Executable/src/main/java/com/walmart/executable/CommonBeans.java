package com.walmart.executable;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

@Component
public class CommonBeans {
	
	@Bean(name= {"vertx"})
	public Vertx getVertx() {
		return Vertx.vertx();
	}
	
	@Bean(name= {"deploymentOptions"})
	public DeploymentOptions getDeploymentOptions() {
		return new DeploymentOptions().setConfig(new JsonObject()
	            .put("http.port", 8080)
	            .put("db_name", "Shopping-Cart-App")
	            .put("connection_string", "mongodb://localhost:" + 27017)
	        );
	}
	
	@Bean(name= {"mongoClient"})
	public MongoClient getMongoClient() {
		return MongoClient.createNonShared(getVertx(), getDeploymentOptions().getConfig());
	}
	
	/*public MongoClientService getMongoClientService() {
		return new MongoClientService();
	}*/
}
