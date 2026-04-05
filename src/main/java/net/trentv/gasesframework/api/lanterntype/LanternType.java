package net.trentv.gasesframework.api.lanterntype;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class LanternType
{
	/**
	 * A name for this lantern type. Must be unique.
	 */
	public final String name;
	/**
	 * The level of light emitted by this lantern in an interval from 0.0f to
	 * 1.0f.
	 */
	public final float lightLevel;
	/**
	 * The name of the texture that will be used inside the lantern.
	 */
	public final String textureName;
	/**
	 * The item given from this lantern.
	 */
	public Item itemOut;
	/**
	 * The lantern type this lantern will transform into if it expires. Can be
	 * null.
	 */
	public final LanternType expirationLanternType;
	/**
	 * The rate at which this lantern will expire. Smaller numbers mean quicker
	 * expiration. If <= 0, this lantern type will never expire.
	 */
	public final int expirationRate;

	/**
	 * The creative tab this lantern type is bound to.
	 */
	public CreativeTabs creativeTab;

	/**
	 * Creates a new lantern type. Lantern types must be registered.
	 *
	 * @param name                  A unique name for this lantern type
	 * @param lightLevel            The level of light emitted by this lantern type in an interval
	 *                              from 0.0f to 1.0f
	 * @param textureName           The name of the texture displayed inside the lantern
	 * @param itemOut               The item given from this lantern. NOTE: Not the necessarily
	 *                              same as the item placed in the lantern
	 * @param expirationLanternType The lantern type this lantern will transform into if it
	 *                              expires. Can be null
	 * @param expirationRate        The rate at which this lantern will expire. Smaller numbers
	 *                              mean quicker expiration. If <= 0, this lantern type will never
	 *                              expire
	 */
	public LanternType(String name, float lightLevel, String textureName, Item itemOut, LanternType expirationLanternType, int expirationRate)
	{
		this.name = name;
		this.lightLevel = lightLevel;
		this.textureName = textureName;
		this.itemOut = itemOut;
		this.expirationLanternType = expirationLanternType;
		this.expirationRate = expirationRate;
	}

	/**
	 * Set the creative tab this lantern type is bound to.
	 *
	 * @param creativeTab The creative tab
	 * @return this
	 */
	public LanternType setCreativeTab(CreativeTabs creativeTab)
	{
		this.creativeTab = creativeTab;
		return this;
	}

	/**
	 * Does this lantern type expire?
	 *
	 * @return True if this lantern type expires
	 */
	public boolean expires()
	{
		return expirationRate > 0;
	}

	/**
	 * Get the unlocalized name of the lantern.
	 *
	 * @return "gf_lantern." + {@link #name};
	 */
	public String getUnlocalizedName()
	{
		return "gf_lantern." + name;
	}
}
