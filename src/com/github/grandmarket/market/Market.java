package com.github.grandmarket.market;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import static org.bukkit.ChatColor.*;

import lib.PatPeter.SQLibrary.*;

public class Market extends JavaPlugin implements Listener {
	public SQLite dbconn;
	public Logger logger;
	public File blockConfigFile;
	public FileConfiguration blocks;
	public void onEnable() {
		logger = Logger.getLogger("Minecraft");
		// Get block configuration from the YAML, create if it doesn't exist.
		blockConfigFile = new File(getDataFolder(), "blocks.yml");
		if(!blockConfigFile.exists()) {
			blockConfigFile.getParentFile().mkdirs();
			copy(getResource("src/github/grandmarket/market/sampleBlocks.yml"), blockConfigFile);
		}
		blocks = new YamlConfiguration();
		try {
			blocks.load(blockConfigFile);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		// Open the database, create settings table if it doesn't exist
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
						sender.sendMessage(DARK_AQUA+"/"+commandLabel+" "+DARK_BLUE+"help "+BLACK+"lists avalible commands.");
						if(sender.hasPermission("market.setup")) {
							sender.sendMessage("This message is the default help message.  The help message can be changed in config.yml");
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
				sender.sendMessage(DARK_AQUA+"/"+commandLabel+"  "+BLACK+"Display basic information");
			}
			if(sender.hasPermission("market.help")) {
				sender.sendMessage(DARK_AQUA+"/"+commandLabel+" "+DARK_BLUE+"help  "+BLACK+"Display this information");
			}
			if(sender.hasPermission("market.updates")) {
				sender.sendMessage(DARK_AQUA+"/"+commandLabel+" "+DARK_BLUE+"updates "+BLACK+"Gives personalized updates on the market.");
				sender.sendMessage(DARK_AQUA+"/"+commandLabel+" "+DARK_BLUE+"updates clear  "+BLACK+"Removes personalized update messages.");
			}
			if(sender.hasPermission("market.mystuff")) {
				sender.sendMessage(DARK_AQUA+"/"+commandLabel+" "+DARK_BLUE+"mystuff "+BLACK+"Displays information about your items on the market.");
			}
			if(sender.hasPermission("market.pricecheck")) {
				sender.sendMessage(DARK_AQUA+"/"+commandLabel+" "+DARK_BLUE+"pricecheck "+LIGHT_PURPLE+"<item name>  "+BLACK+"Checks the price of an item.");
			}
			if(sender.hasPermission("market.buy")) {
				sender.sendMessage(DARK_AQUA+"/"+commandLabel+" "+DARK_BLUE+"buy "+LIGHT_PURPLE+"<ammount> <item> "+BLACK+"("+DARK_GREEN+"[now [under price]] "+BLACK+"or "+DARK_GREEN+"[price]"+BLACK+") Purchase an item (more information at "+DARK_AQUA+"/" + commandLabel + " "+DARK_BLUE+"buy"+BLACK+")");
			}
			if(sender.hasPermission("market.sell")) {
				sender.sendMessage(DARK_AQUA+"/"+commandLabel+" "+DARK_BLUE+"sell "+LIGHT_PURPLE+"<ammount> <item> "+BLACK+"("+DARK_GREEN+"[now [under price]] "+BLACK+"or "+DARK_GREEN+"[price]"+BLACK+")  Sell an item (more info at "+DARK_AQUA+"/" + commandLabel + " "+DARK_BLUE+"sell"+BLACK+")");
			}
			if(sender.hasPermission("market.setup")) {
				sender.sendMessage(DARK_AQUA+"/"+commandLabel+" "+DARK_BLUE+"setup  "+BLACK+"Offers a series of setup options, mostly default values");
			}
			return true;
		}
		if(args[0] == "pricecheck") {
			if(args.length > 1) {
				// Implement text to id parser later, currently just takes an id
				/*String itemName = "";
				for(int nameArg = 1; nameArg < args.length - 1; nameArg++) {
					itemName += args[nameArg] + " ";
				}
				*/
				String itemId = args[1];
				if(blocks.contains(itemId)) {
					sender.sendMessage("Current market price for "+DARK_RED+blocks.getString(itemId+".name"));
				}
			}
		}
		return false;
	}
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		
	}
	private void copy(InputStream in, File file) {
	    try {
	        OutputStream out = new FileOutputStream(file);
	        byte[] buf = new byte[1024];
	        int len;
	        while((len=in.read(buf))>0){
	            out.write(buf,0,len);
	        }
	        out.close();
	        in.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
}
