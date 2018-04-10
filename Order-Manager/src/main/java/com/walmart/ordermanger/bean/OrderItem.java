package com.walmart.ordermanger.bean;

import com.walmart.inventory.bean.Item;

public class OrderItem {
	
	private Item item;
	private int quantity;
	
	public OrderItem() {
		// TODO Auto-generated constructor stub
	}
	
	public OrderItem(Item item , int quantity) {
		this.item = item;
		this.quantity = quantity;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	

}
