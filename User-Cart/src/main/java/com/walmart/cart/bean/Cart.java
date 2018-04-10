package com.walmart.cart.bean;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

public class Cart {

	private String _id;
	private String associatedUser;
	private Map<String , Integer> items;
	
	public Cart() {
		// TODO Auto-generated constructor stub
	}
	
	public Cart(String associatedUser, Map<String, Integer> items) {
		this.associatedUser = associatedUser;
		this.items = items;
	}
	
	public void removeItem(Cart cart) {
		
		if(Optional.ofNullable(this.items).isPresent()) {
			Map<String , Integer> items = cart.getItems();
			
			Set<Entry<String, Integer>> set = items.entrySet();
			
			set.stream().forEach(entry ->{
				
				if(this.items.containsKey(entry.getKey())) {
					if(this.items.get(entry.getKey()) <= entry.getValue()) {
						this.items.remove(entry.getKey());
					}
					else {
						this.items.put(entry.getKey(), this.items.get(entry.getKey()) - entry.getValue());
					}
				}
			});
		}
	}
	
	public void addItem(Cart cart) {
		
		if(Optional.ofNullable(this.items).isPresent()) {
			Map<String , Integer> items = cart.getItems();
			
			Set<Entry<String, Integer>> set = items.entrySet();
			
			set.stream().forEach(entry ->{
				
				if(this.items.containsKey(entry.getKey())) {
					
					this.items.put(entry.getKey(), this.items.get(entry.getKey()) + entry.getValue());
				}
				else {
					this.items.put(entry.getKey(), entry.getValue());
				}
			});
		}
		else {
			this.items = cart.getItems();
		}
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getAssociatedUser() {
		return associatedUser;
	}

	public void setAssociatedUser(String associatedUser) {
		this.associatedUser = associatedUser;
	}

	public Map<String, Integer> getItems() {
		return items;
	}

	public void setItems(Map<String, Integer> items) {
		this.items = items;
	}
	
	
}
