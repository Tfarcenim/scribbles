package com.tfar.examplemod.network;

import com.tfar.examplemod.Scribbles;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;


public class Message {

  public static SimpleChannel INSTANCE;

  public static void registerMessages(String channelName) {
    INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(Scribbles.MODID, channelName), () -> "1.0", s -> true, s -> true);
    INSTANCE.registerMessage(0, C2SDeconstructionPacket.class,
            (a, b) -> {},
            p -> new C2SDeconstructionPacket(),
            C2SDeconstructionPacket::handle);

    INSTANCE.registerMessage(1, C2ConstructionPacket.class,
            C2ConstructionPacket::encode,
            C2ConstructionPacket::new,
            C2ConstructionPacket::handle);
  }
}