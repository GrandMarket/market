package com.github.grandmarket.market;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import lib.PatPeter.SQLibrary.*;

public class Market extends JavaPlugin implements Listener {
	public SQLite dbconn;
	public Logger logger;
	public void onEnable() {
		logger = Logger.getLogger("Minecraft");
		dbconn = new SQLite(logger, "", "GrandMarket", "./plugins/GrandMarket/");
		dbconn.open();
		if(!dbconn.checkTable("settings")) {
			logger.log(Level.INFO, "GrandMarket: Creating table \"settings\" in database \""+dbconn.name+"\"");
			dbconn.createTable("CREATE TABLE settings (id INTEGER NOT NULL PRIMARY KEY, setting TEXT, value BLOB)");
		}
		try {
			if(!dbconn.query("SELECT * FROM settings WHERE setting='setup' LIMIT 1").next()) {
				getLogger().log(Level.WARNING, "GrandMarket: The setup has not been completed.  We recomend that this be done ASAP.");	
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		getLogger().info("The market plugin has been enabled.");
	}
	public void onDisable() {
		getLogger().info("The market plugin has been disabled.");
	}
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(cmd.getName().equalsIgnoreCase("market")) {
			if(args.length < 1) {
				try {
					ResultSet query = dbconn.query("SELECT * FROM settings WHERE setting='mainText' LIMIT 1");
					if(query.next()) {
						sender.sendMessage(query.getString("value"));
					}
					else {
						sender.sendMessage("The market allows players to buy and sell items.");
						sender.sendMessage("/"+commandLabel+" help lists avalible commands.");
						if(sender.hasPermission("market.setup")) {
							sender.sendMessage("This message is the default help message.  Change the plugin settings (or go through the setup) to change this message.");
						}
					}
				}
				catch (SQLException e) {
					e.printStackTrace();
					getLogger().log(Level.SEVERE, "SQLException onCommand market.main");
				}
				return true;
			}
		}
		if(args[0] == "help") {
			sender.sendMessage("/"+commandLabel+" [commands...]");
			if(sender.hasPermission("market.main")) {
				sender.sendMessage("/"+commandLabel+"  Display basic information");
			}
			if(sender.hasPermission("market.help")) {
				sender.sendMessage("/"+commandLabel+" help  Display this information");
			}
			if(sender.hasPermission("market.updates")) {
				sender.sendMessage("/"+commandLabel+" updates Gives personalized updates on the market.");
				sender.sendMessage("/"+commandLabel+" updates clear  Removes personalized update messages.");
			}
			if(sender.hasPermission("market.mystuff")) {
				sender.sendMessage("/"+commandLabel+" mystuff Displays information about your items on the market.");
			}
			if(sender.hasPermission("market.pricecheck")) {
				sender.sendMessage("/"+commandLabel+" pricecheck <item name>  Checks the price of an item.");
			}
			if(sender.hasPermission("market.buy")) {
				sender.sendMessage("/"+commandLabel+" buy <ammount> <item> ([now [under price]] or [price]) Purchase an item (more information at /" + commandLabel + " buy)");
			}
			if(sender.hasPermission("market.sell")) {
				sender.sendMessage("/"+commandLabel+" sell <ammount> <item> ([now [under price]] or [price])  Sell an item (more info at /" + commandLabel + " sell");
			}
			if(sender.hasPermission("market.setup")) {
				sender.sendMessage("/"+commandLabel+" setup  Offers a series of setup options, mostly default values");
			}
			return true;
		}
		return false;
	}
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		
	}
}
