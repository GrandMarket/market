package com.github.grandmarket.market;

import java.io.Serializable;

import org.bukkit.inventory.ItemStack;

public class SellOrder extends Order implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public SellOrder(ItemStack item, int price) {
		super(item, price);
	}
	
	
}
