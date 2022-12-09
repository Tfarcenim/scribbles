package com.tfar.examplemod.network;

import com.tfar.examplemod.ScribbleContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class C2ConstructionPacket {

  private String name;
  private int length;

  public C2ConstructionPacket() {}

  public C2ConstructionPacket(String newName) {
    this.name = newName;
  }

  public C2ConstructionPacket(PacketBuffer buf) {
    length = buf.readInt();
    name = buf.readString(length);
  }

  public void encode(PacketBuffer buf) {
    buf.writeInt(name.length());
    buf.writeString(name);
  }

  public void handle(Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      if (ctx.get() == null || ctx.get().getSender() == null)return;
      Container anvil = ctx.get().getSender().openContainer;
      if (anvil instanceof ScribbleContainer){
        ((ScribbleContainer)anvil).construct(name);}
    });
    ctx.get().setPacketHandled(true);
  }
}
