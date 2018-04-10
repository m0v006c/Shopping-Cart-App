package com.walmart.ordermanger.bean;

import java.util.Date;
import java.util.List;

public class Order {

	private String _id;
	private String status;
	private Date orderdate;
	private String associateduser;
	private List<OrderItem> items;
	
	public Order() {
		// TODO Auto-generated constructor stub
	}
	
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getOrderdate() {
		return orderdate;
	}
	public void setOrderdate(Date orderdate) {
		this.orderdate = orderdate;
	}
	
	public String getAssociateduser() {
		return associateduser;
	}

	public void setAssociateduser(String associateduser) {
		this.associateduser = associateduser;
	}

	public List<OrderItem> getItems() {
		return items;
	}
	public void setItems(List<OrderItem> items) {
		this.items = items;
	}
	
	
}
