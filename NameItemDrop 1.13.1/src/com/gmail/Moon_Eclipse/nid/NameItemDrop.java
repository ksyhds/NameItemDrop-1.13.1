package com.gmail.Moon_Eclipse.nid;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.Moon_Eclipse.MCgive.ItemDeliver.ItemDeliver;
import com.Moon_eclipse.EclipseLib.LibMain;
import com.Moon_eclipse.EclipseLib.ItemCreater.ItemCreator;

public class NameItemDrop extends JavaPlugin implements Listener
{
	
	private File DropItem;
	private FileConfiguration items;
	private File MobList;
	private FileConfiguration mobs;
	private Configuration c;
	String prefix;
	Random rn = new Random();

	public void onEnable()
	{
		Bukkit.getPluginManager().registerEvents(this, this);
		this.saveDefaultmobs();
		this.saveDefaultItems();
		this.saveDefaultConfig();
		c = this.getConfig();
		DropItem = new File(getDataFolder(), "items.yml");
		items = YamlConfiguration.loadConfiguration(DropItem);
		MobList = new File(getDataFolder(), "mobs.yml");
		mobs = YamlConfiguration.loadConfiguration(MobList);
		prefix = c.getString("config.prefix");
		
	}
	public void onDisable(){}
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if(sender.isOp())
		{
			if(command.getName().equalsIgnoreCase("nid"))
			{
				// nid reload/give nickname itemname
				if(args[0].equalsIgnoreCase("reload"))
				{
					prefix = c.getString("config.prefix");
					this.reloadConfig();
					c = this.getConfig();
					DropItem = new File(getDataFolder(), "items.yml");
					items = YamlConfiguration.loadConfiguration(DropItem);
					MobList = new File(getDataFolder(), "mobs.yml");
					mobs = YamlConfiguration.loadConfiguration(MobList);
					sender.sendMessage("[NameItemDrop] 문제없이 리로드.");	
				}
				if(args[0].equalsIgnoreCase("list"))
				{
					Set<String> list = items.getConfigurationSection("items").getKeys(false);
					for(String itemname : list)
					{
						sender.sendMessage(itemname);
					}
				}
			}
		}
		return true;
	}
	@EventHandler
	public void onEntityDeath(EntityDeathEvent e)
	{
		//Bukkit.broadcastMessage("이벤트 FIRE - NID");
		LivingEntity entity = e.getEntity();
		
		String PlayerName = "";
		if(entity.getKiller() instanceof Player)
		{
			Player p = (Player) entity.getKiller(); 
			//Bukkit.broadcastMessage("p.getname" + p.getName());
			PlayerName = p.getName();
		}
		
		if(entity instanceof Creature || entity instanceof Slime)
		{
			//Bukkit.broadcastMessage("엔티티는 크리쳐에 속함.");
		    LivingEntity creature = (LivingEntity) entity;
		    String getname = creature.getCustomName();
		    if(!((getname + "").equals("null")))
		    {
		    	//Bukkit.broadcastMessage("엔티티가 이름을 갖음. 이름: " + getname);
		    	String name = getname.replace(" ", "_");
			    String pluginname = name.replace("§", "&");
			    
			    List<ItemStack> newdrops = e.getDrops();
			    //Bukkit.broadcastMessage(pluginname);
			    Set<String> keys = mobs.getConfigurationSection("mobs").getKeys(false);
			    for(String key : keys)
			    {
			    	if(pluginname.contains(key))
			   		{
			    		//Bukkit.broadcastMessage("이름을 콘픽에서 찾음. 콘픽 이름: " + key);
			   			String path = "mobs." + key;
			   			int exp = mobs.getInt(path + ".exp");
					    e.setDroppedExp(exp);
		   				int	chanceint = c.getInt("config.ChanceInt");
			   			List<String> itemlist = mobs.getStringList(path + ".items");
			   			for(String item : itemlist)
			   			{
			   				// ("1, 10")
			   				String perstr = item.substring(item.indexOf(",") + 1, item.length());
			   				double perint = Double.parseDouble(perstr);
			   				//Bukkit.broadcastMessage(perstr + " + perstr");
			   				int random = rn.nextInt(chanceint -1) + 1;
			   				double percent = ((double)random / (double)chanceint) * 100;
			   				//Bukkit.broadcastMessage(perint + " + perint");
			   				//Bukkit.broadcastMessage(percent + " + percent");
			   				if(percent <= perint)
			   				{
			   					String itemname = item.substring(0 , item.indexOf(","));
			   					
			   					ItemStack dropitem = ItemDeliver.ItemCreat_From_Config(itemname, 0);
			   					dropitem = ItemCreator.getPlaceHoldered_ItemStack(dropitem, "PLAYER", PlayerName);			   					
				   				dropitem = LibMain.hideFlags_Unbreak(dropitem);
				   				newdrops.add(dropitem);
			   				}
			   			}
			   			break;
			   		}
			    }
		    }
		}
	}
	public void saveDefaultItems()
	{
		   if (DropItem == null)
		   {
			   DropItem = new File(getDataFolder(), "items.yml");
		   }
		   if (!DropItem.exists())
		   {            
			   this.saveResource("items.yml", true);
		   }
	}
	public void saveDefaultmobs()
	{
		   if (MobList == null)
		   {
			   MobList = new File(getDataFolder(), "mobs.yml");
		   }
		   if (!MobList.exists())
		   {            
			   this.saveResource("mobs.yml", true);
		   }
	}
}
