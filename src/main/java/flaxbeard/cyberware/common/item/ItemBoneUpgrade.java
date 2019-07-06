package flaxbeard.cyberware.common.item;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.google.common.collect.HashMultimap;

import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.common.lib.LibConstants;

public class ItemBoneUpgrade extends ItemCyberware
{

	public static final int META_LACING                 = 0;
	public static final int META_FLEX                   = 1;
	public static final int META_BATTERY                = 2;
	
	public ItemBoneUpgrade(String name, EnumSlot slot, String[] subnames)
	{
		super(name, slot, subnames);
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	private static final UUID healthId = UUID.fromString("8bce997a-4c3a-11e6-beb8-9e71128cae77");

	@Override
	public void onAdded(EntityLivingBase entityLivingBase, ItemStack stack)
	{
		if (stack.getItemDamage() == META_LACING)
		{
			HashMultimap<String, AttributeModifier> multimap = HashMultimap.create();
			
			multimap.put(SharedMonsterAttributes.MAX_HEALTH.getName(), new AttributeModifier(healthId, "Bone hp upgrade", 4F * stack.getCount(), 0));
			entityLivingBase.getAttributeMap().applyAttributeModifiers(multimap);
		}
	}
	
	@Override
	public void onRemoved(EntityLivingBase entityLivingBase, ItemStack stack)
	{
		if (stack.getItemDamage() == META_LACING)
		{
			HashMultimap<String, AttributeModifier> multimap = HashMultimap.create();
			
			multimap.put(SharedMonsterAttributes.MAX_HEALTH.getName(), new AttributeModifier(healthId, "Bone hp upgrade", 4F * stack.getCount(), 0));
			entityLivingBase.getAttributeMap().removeAttributeModifiers(multimap);
		}
	}
	
	@SubscribeEvent
	public void handleJoinWorld(EntityJoinWorldEvent event)
	{
		Entity entity = event.getEntity();
		
		ItemStack test = new ItemStack(this, 1, META_LACING);
		if (entity instanceof EntityLivingBase && CyberwareAPI.isCyberwareInstalled(entity, test))
		{
			this.onAdded((EntityLivingBase) entity, CyberwareAPI.getCyberware(entity, test));
		}
		else if (CyberwareAPI.hasCapability(entity) && entity instanceof EntityLivingBase)
		{
			this.onRemoved((EntityLivingBase) entity, test);
		}
	}
	
	@SubscribeEvent
	public void handleFallDamage(LivingHurtEvent event)
	{
		EntityLivingBase entityLivingBase = event.getEntityLiving();
		
		if (CyberwareAPI.isCyberwareInstalled(entityLivingBase, new ItemStack(this, 1, META_FLEX)))
		{
			if (event.getSource() == DamageSource.FALL)
			{
				event.setAmount(event.getAmount() * .3333F);
			}
		}
	}
	
	@Override
	public boolean isIncompatible(ItemStack stack, ItemStack other)
	{
		return other.getItem() == this;
	}	@Override
	
	public int getCapacity(ItemStack wareStack)
	{
		return wareStack.getItemDamage() == META_BATTERY ? LibConstants.BONE_BATTERY_CAPACITY * wareStack.getCount() : 0;
	}
	
	@Override
	public int installedStackSize(ItemStack stack)
	{
		return stack.getItemDamage() == META_LACING ? 5
		     : stack.getItemDamage() == META_BATTERY ? 4 : 1;
	}
	
	@Override
	protected int getUnmodifiedEssenceCost(ItemStack stack)
	{
		if (stack.getItemDamage() == META_LACING)
		{
			switch (stack.getCount())
			{
				case 1:
					return 3;
				case 2:
					return 6;
				case 3:
					return 9;
				case 4:
					return 12;
				case 5:
					return 15;
			}
		}
		if (stack.getItemDamage() == META_BATTERY)
		{
			switch (stack.getCount())
			{
				case 1:
					return 2;
				case 2:
					return 3;
				case 3:
					return 4;
				case 4:
					return 5;
			}
		}
		return super.getUnmodifiedEssenceCost(stack);
	}
	
}
