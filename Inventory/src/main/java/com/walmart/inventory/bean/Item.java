package com.walmart.inventory.bean;

public class Item {

	private String _id;
	private String title;
	private String description;
	private int quantity;
	private double cost;
	
	public Item() {
		// TODO Auto-generated constructor stub
	}
	
	public Item(String title, String description, int quantity, double cost) {
		this.title = title;
		this.description = description;
		this.quantity = quantity;
		this.cost = cost;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}
	
	
}
