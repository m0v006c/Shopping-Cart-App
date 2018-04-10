package com.walmart.executable;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages= {"com.walmart.executable","com.walmart.inventory", "com.walmart.ordermanger",
		"com.walmart.user","com.walmart.cart"})
public class ParentBeans {

}
