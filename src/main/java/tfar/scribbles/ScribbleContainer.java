package tfar.scribbles;

import tfar.scribbles.Util.a;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.registries.ForgeRegistries;

public class ScribbleContainer extends Container {

  protected ItemStackHandler itemStackHandler = new ItemStackHandler(2);
  protected ItemStack scribble;
  protected PlayerEntity player;

  protected ScribbleContainer(int id,PlayerEntity player) {
    super(Scribbles.Obj.container, id);
    this.player = player;
    PlayerInventory inv = player.inventory;
    scribble = player.getHeldItemMainhand();
    int lock = inv.currentItem;
    addSlot(new SlotItemHandler(itemStackHandler,0,26,30));

    addSlot(new SlotItemHandler(itemStackHandler,1,134,30));

    int i1 = 163;

    for (int i = 0; i < 3; ++i) {
      for (int j = 0; j < 9; ++j) {
        this.addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, i1 + i * 18));
      }
    }

    for (int k = 0; k < 9; ++k) {
      Slot slot = lock == k ? new LockedSlot(inv, k, 8 + k * 18, i1 + 58) : new Slot(inv, k, 8 + k * 18, i1 + 58);
      this.addSlot(slot);
    }
  }

  public void deconstruct(){
    ItemStack stack = itemStackHandler.getStackInSlot(0);
    String name = stack.getItem().getRegistryName().getPath();
    int count = stack.getCount();
    CompoundNBT nbt = scribble.getOrCreateChildTag("scribbles");
    for (int i = 0; i < name.length();i++) {
      String c = Character.valueOf(name.charAt(i)).toString();
        int oldcount = nbt.getInt(c);
        nbt.putInt(c, oldcount + count);
    }
    itemStackHandler.extractItem(0,Integer.MAX_VALUE,false);
  }

  /**
   * Determines whether supplied player can use this container
   *
   * @param playerIn
   */
  @Override
  public boolean canInteractWith(PlayerEntity playerIn) {
    return true;
  }

  /**
   * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
   * inventory and the other inventory(s).
   */
  public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
    ItemStack itemstack = ItemStack.EMPTY;
    Slot slot = this.inventorySlots.get(index);
    if (slot != null && slot.getHasStack()) {
      ItemStack itemstack1 = slot.getStack();
      itemstack = itemstack1.copy();
      if (index == 1) {
        if (!this.mergeItemStack(itemstack1, 2, 38, true)) {
          return ItemStack.EMPTY;
        }

        slot.onSlotChange(itemstack1, itemstack);
      } else if (index != 0) {
        if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
          return ItemStack.EMPTY;
        }
      } else if (!this.mergeItemStack(itemstack1, 2, 38, false)) {
        return ItemStack.EMPTY;
      }

      if (itemstack1.isEmpty()) {
        slot.putStack(ItemStack.EMPTY);
      } else {
        slot.onSlotChanged();
      }

      if (itemstack1.getCount() == itemstack.getCount()) {
        return ItemStack.EMPTY;
      }

      slot.onTake(playerIn, itemstack1);
    }

    return itemstack;
  }


  public void construct(String rl) {
    rl = a.stringtoRL(rl);
    if (isValid(rl) && hasEnoughCharacters(rl)) {
      String name = rl.split(":")[1];
      Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(rl));
      if (hasRoom(item)) {
        CompoundNBT nbt = scribble.getOrCreateChildTag("scribbles");
        for (int i = 0; i < name.length(); i++) {
          String c = Character.valueOf(name.charAt(i)).toString();
            int oldcount = nbt.getInt(c);
            nbt.putInt(c, oldcount - 1);
        }
        ItemStack stack = new ItemStack(item);
        itemStackHandler.insertItem(1, stack,false);
      }
    }
  }

  public boolean isValid(String s) {
    try {
      boolean isValid = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s)) != Items.AIR;
      if (!isValid) this.player.sendMessage(new StringTextComponent("Invalid name!"),Util.DUMMY_UUID);
      return isValid;
    } catch (Exception e) {
      this.player.sendMessage(new StringTextComponent("Invalid name!"), Util.DUMMY_UUID);
      return false;
    }
  }

  public boolean hasEnoughCharacters(String name){
    String string = name.split(":")[1];
    boolean hasEnough = true;
    String missing = null;
    CompoundNBT nbtCopy = scribble.getOrCreateChildTag("scribbles").copy();
    for (int i = 0; i < string.length(); i++) {
      String c = Character.valueOf(string.charAt(i)).toString();
        int oldcount = nbtCopy.getInt(c);
        nbtCopy.putInt(c, oldcount - 1);
        if (nbtCopy.getInt(c) < 0){
          hasEnough = false;
          missing = c;
          break;
      }
    }
    if (!hasEnough){
      //player.sendMessage(new StringTextComponent("Insufficient letters "+ missing));
    }
    return hasEnough;
  }

  public boolean hasRoom(Item i){
    return itemStackHandler.getStackInSlot(1).isEmpty() || itemStackHandler.getStackInSlot(1).getItem() == i;
  }
}
