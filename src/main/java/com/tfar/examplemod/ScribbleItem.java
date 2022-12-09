package com.tfar.examplemod;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ScribbleItem extends Item implements INamedContainerProvider {
  public ScribbleItem(Properties properties) {
    super(properties);
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity player, Hand handIn) {
    if (!worldIn.isRemote) {
      player.openContainer(this);

    }
    return super.onItemRightClick(worldIn, player, handIn);
  }

  @Override
  public ITextComponent getDisplayName() {
    return new StringTextComponent("scribbles");
  }

  @Nullable
  @Override
  public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
    return new ScribbleContainer(id,player);
  }
}
