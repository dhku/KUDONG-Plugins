package kr.kudong.entity.data;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import lombok.Data;

@Data
public class SteerableEntity
{
	private Player player;
	private Entity entity;
	private SteerablePreset preset;
	
	private Vector inertiaDirection;
	private Vector currentDirection;
	private Vector currentVelocity;
	
	private double velocity;
	
	private boolean isInit = false;
	private boolean isCasualMode = false;
	private boolean isPositionVisible = false;
	
	private List<Location> entityOffset;
	private Location entityCenter;
	
	private Entity _entity;
	
	public SteerableEntity(Player player, Entity entity, SteerablePreset preset)
	{
		this.player = player;
		this.entity = entity;
		this.preset = preset;
		this.velocity = 0.0;
		this.entityOffset = new ArrayList<>();
		
		ArmorStand armor = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
		ItemStack item = new ItemStack(Material.DIAMOND_HOE);
		ItemMeta meta = item.getItemMeta();
		meta.setCustomModelData(preset.CustomModelDataID);
		item.setItemMeta(meta);
		
		armor.setVisible(false);
		armor.getEquipment().setHelmet(item);
		armor.setCollidable(false);
		
		this._entity = armor;
		
	}
	
	public void initializeInertiaDir(Vector inertiaDirection)
	{
		this.inertiaDirection = inertiaDirection;
		this.isInit = true;
	}
	
	public void updatePhysics(Player player ,KeyInputState state)
	{
		double height = this.checkGravity(state);
		Entity e = entity;
		float forward = state.getForward();
		float sidewalks = state.getSidewalks();
		boolean isShift = state.isShift();
		boolean isSpaceBar = state.isSpacebar();
		
		if(isSpaceBar)
		{
			this.velocity += -this.preset.ACCELERATION_RATE * 1.2;
		}
	
		if(forward > 0 && sidewalks == 0)
		{
			if(isSpaceBar == false)
			{

				if(isShift)
				{
					this.velocity += this.preset.ACCELERATION_RATE;
					
					if(this.velocity > this.preset.FORWARD_BOOST_MAXSPEED)
						this.velocity = this.preset.FORWARD_BOOST_MAXSPEED;
				}
				else
				{
					if(this.velocity > this.preset.FORWARD_DEFAULT_MAXSPEED)
					{
						this.velocity += -this.preset.DECELERATION_RATE;
						
						if(this.velocity <= this.preset.FORWARD_DEFAULT_MAXSPEED)
							this.velocity = this.preset.FORWARD_DEFAULT_MAXSPEED;
					}
					else
					{
						this.velocity += this.preset.ACCELERATION_RATE;
						
						if(this.velocity > this.preset.FORWARD_DEFAULT_MAXSPEED)
							this.velocity = this.preset.FORWARD_DEFAULT_MAXSPEED;
					}	
				}
			}
			
			if(this.velocity < 0.0f)
				this.velocity = 0.0f;
		}
		else if(forward < 0)
		{
			if(this.velocity > 0.0f)
			{
				this.velocity += -0.05f;
			}
			else
			{
				this.velocity += -this.preset.DECELERATION_RATE;
			}
			if(this.velocity < -0.4f)
				this.velocity = -0.3f;
			
		}else if(sidewalks != 0)
		{
			this.velocity += -0.02f;
			
			if(this.velocity < 0.0f)
				this.velocity = 0.0f;
		}
		else
		{
			this.velocity += -0.05f;
			
			if(this.velocity < 0.0f)
				this.velocity = 0.0f;
		}
		
		if(isCasualMode == false)
		{
			if(sidewalks < 0)
			{
				if(forward >= 0)
					e.setRotation(e.getLocation().getYaw() + this.preset.SIDE_STEER_SENSIVITY , 0.0f);
				else 
					e.setRotation(e.getLocation().getYaw() - this.preset.SIDE_STEER_SENSIVITY , 0.0f);
				
			}
			else if(sidewalks > 0)
			{
				if(forward >= 0)
					e.setRotation(e.getLocation().getYaw() - this.preset.SIDE_STEER_SENSIVITY , 0.0f);
				else
					e.setRotation(e.getLocation().getYaw() + this.preset.SIDE_STEER_SENSIVITY , 0.0f);
			}

			Vector v2 = e.getLocation().getDirection().clone();
			this.currentDirection = inertiaDirection.add(v2.subtract(inertiaDirection).multiply(this.preset.getTRACTION())).normalize();
		}
		else
		{
			this.currentDirection = player.getLocation().getDirection().setY(0).normalize();
			e.setRotation(player.getLocation().getYaw(), 0);
		}
		
		this.updateGUI(player);
		
		this.currentVelocity = this.currentDirection.clone().multiply(this.velocity);
		this.currentVelocity.setY(height);
		
		this.entity.setVelocity(this.currentVelocity);

		Location l = entity.getLocation();
		this._entity.teleport(l);

	}
	
	public void destroyEntity()
	{
		this.entity.eject();
		this._entity.remove();
		this.entity.remove();
	}
	
	private double checkGravity(KeyInputState state)
	{
		Entity e = this.entity;
		
		this.entityOffset= this.getClimbChecker(e);

		for(Location check : this.entityOffset)
		{
			Block checkBlock=check.getBlock();

			if(state.getForward()!=0)
			{
				if(!checkBlock.isPassable()&&checkBlock.getRelative(BlockFace.UP).isPassable())
				{
					return e.getLocation().getBlock().getRelative(BlockFace.UP).getY()
							-e.getLocation().getY();
				}
				else if(!(e.isOnGround()))
				{
					return e.getLocation().getBlock().getRelative(BlockFace.DOWN).getY()
							-e.getLocation().getY();
				}
			}

		}
		return e.getVelocity().getY();
	}
	
	private List<Location> getClimbChecker(Entity entity)
	{
		this.entityCenter=entity.getBoundingBox().getCenter().toLocation(entity.getWorld());
		this.entityOffset.clear();
		Location loc = entity.getLocation();
		
		for(int x = (loc.getBlockX()-1); (x <= loc.getBlockX()+1); x++)
		{
			for(int z=(loc.getBlockZ()-1); (z<=loc.getBlockZ()+1); z++)
			{
				if(loc.getBlockX()==x && loc.getBlockZ()==z) continue;

				Location location= new Location(entity.getWorld(), x, loc.getY(), z);
				
				this.entityOffset.add(location);

				if(this.entityOffset.size()==8)
				{
					return this.entityOffset;
				}

			}
		}
		return null;
	}
	
	public void updateGUI(Player player)
	{
		if(isPositionVisible)
		{
			player.sendActionBar("§6탈것 §f"+this.preset.getDISPLAY_NAME()+" §7| §6속도 §f"
					+(Math.round(this.velocity*100)/10.0)*10+" §7| §6위치 §f"
					+(int)this.entity.getLocation().getX()+"§6 , §f"
					+(int)this.entity.getLocation().getY()+"§6 , §f"
					+(int)this.entity.getLocation().getZ());
			
		}
		else
		{
			player.sendActionBar("§6탈것 §f"+this.preset.getDISPLAY_NAME()+" §7| §6속도 §f"
					+(Math.round(this.velocity*100)/10.0)*10);
		}
	}

	public void clear()
	{
		this.entityOffset.clear();
	}

}
