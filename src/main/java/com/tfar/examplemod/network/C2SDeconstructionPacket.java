package com.tfar.examplemod.network;

import com.tfar.examplemod.ScribbleContainer;
import net.minecraft.inventory.container.Container;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SDeconstructionPacket {

  public C2SDeconstructionPacket() {}

  public void handle(Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      if (ctx.get() == null || ctx.get().getSender() == null)return;
      Container anvil = ctx.get().getSender().openContainer;
      if (anvil instanceof ScribbleContainer){
        ((ScribbleContainer)anvil).deconstruct();}
    });
    ctx.get().setPacketHandled(true);
  }
}
