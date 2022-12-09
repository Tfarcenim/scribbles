package tfar.scribbles;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import tfar.scribbles.Util.a;
import tfar.scribbles.network.C2ConstructionPacket;
import tfar.scribbles.network.C2SDeconstructionPacket;
import tfar.scribbles.network.Message;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_TAB;

public class ScribbleScreen extends ContainerScreen<ScribbleContainer> implements IContainerListener {

  public static final ResourceLocation BACKGROUND = new ResourceLocation(Scribbles.MODID,"textures/gui/pencil_container.png");

  protected TextFieldWidget input;
  protected final List<String> suggestions = new ArrayList<>();
  public int slotcolor = 0;
  public List<String> missingletters = new ArrayList<>();

  public ScribbleScreen(ScribbleContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
    super(screenContainer, inv, titleIn);
    ySize+=80;
  }

  @Override
  protected void init() {
    super.init();

    this.minecraft.keyboardListener.enableRepeatEvents(true);
    int i = (this.width - this.xSize) / 2;
    int j = (this.height - this.ySize) / 2;
    this.input = new TextFieldWidget(this.font, i + 29, j + 50, 120, 12,new TranslationTextComponent("container.repair"));
    this.input.setCanLoseFocus(false);
    this.input.changeFocus(true);
    this.input.setTextColor(-1);
    this.input.setDisabledTextColour(-1);
    this.input.setEnableBackgroundDrawing(false);
    this.input.setMaxStringLength(35);
    this.input.setResponder(this::onEdited);
    this.children.add(this.input);
    this.container.addListener(this);
    this.setFocusedDefault(this.input);

    this.addButton(new Button(guiLeft + 12, guiTop + 4 ,46,20,new StringTextComponent("Destroy"),
            b -> {
      Message.INSTANCE.sendToServer(new C2SDeconstructionPacket());
      this.container.deconstruct();
            }));

    this.addButton(new Button(guiLeft + 126, guiTop + 4 ,30,20,new StringTextComponent("Build"),
            b -> {
      Message.INSTANCE.sendToServer(new C2ConstructionPacket(input.getText()));
      this.container.construct(input.getText());
    }));

  }

  @Override
  public void render(MatrixStack stack,int p_render_1_, int p_render_2_, float p_render_3_) {
    this.renderBackground(stack);
    super.render(stack,p_render_1_, p_render_2_, p_render_3_);
    RenderSystem.disableBlend();
    this.input.render(stack,p_render_1_, p_render_2_, p_render_3_);
    this.renderHoveredTooltip(stack,p_render_1_, p_render_2_);
  }

  private void onEdited(String text) {
    slotcolor = 0;
    if (!isValid(this.input.getText())){
      slotcolor = 0xffff0000;
    } else if (!hasEnoughCharacters(input.getText())){
      slotcolor =0xffffff00;
    }
  }

  /**
   * Draws the background layer of this container (behind the items).
   *
   * @param partialTicks
   * @param mouseX
   * @param mouseY
   */
  @Override
  protected void drawGuiContainerBackgroundLayer(MatrixStack stack,float partialTicks, int mouseX, int mouseY) {
    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    this.minecraft.getTextureManager().bindTexture(BACKGROUND);
    int i = (this.width - this.xSize) / 2;
    int j = (this.height - this.ySize) / 2;
    int x = 25;
    int y = 48;
    int x1 = 134;
    int y1 = 30;
    this.blit(stack,i, j, 0, 0, this.xSize, this.ySize);
    fill(stack,i+x,j+y,i+126+x,j+12+y,0xff000000);
    if (slotcolor != 0){
      fill(stack,i+x1,j+y1,i+16+x1,j+16+y1,slotcolor);
    }
  }

  public boolean hasEnoughCharacters(String name){
    String rl = a.stringtoRL(name);
    String string = rl.split(":")[1];
    boolean hasEnough = true;
    missingletters.clear();
    CompoundNBT nbtCopy = container.scribble.getOrCreateChildTag("scribbles").copy();
    for (int i = 0; i < string.length(); i++) {
      String c = Character.valueOf(string.charAt(i)).toString();
      int oldcount = nbtCopy.getInt(c);
      nbtCopy.putInt(c, oldcount - 1);
      if (nbtCopy.getInt(c) < 0){
        missingletters.add(c);
        hasEnough = false;
      }
    }
    return hasEnough;
  }

  public boolean isValid(String s) {
    try {
      return ForgeRegistries.ITEMS.getValue(new ResourceLocation(s)) != Items.AIR;
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  protected void drawGuiContainerForegroundLayer(MatrixStack stack,int mouseX, int mouseY) {
    int initaly = 63;
    int y = initaly;
    int x = 8;
    CompoundNBT nbt = this.container.scribble.getOrCreateChildTag("scribbles");
  for (String s : a.chars) {
    int color = missingletters.contains(s) ? 0xff0000 : 0x404040;
      this.font.drawString(stack,s + ": "+nbt.getInt(s), x, y, color);
      y += 10;
      if (y > 153){
        y = initaly ; x += 40;
      }
    }
  }

  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (keyCode == GLFW_KEY_ESCAPE) {
      this.minecraft.player.closeScreen();
    }

    if (keyCode == GLFW_KEY_TAB) {
      getSuggestions();
    }

    return this.input.keyPressed(keyCode, scanCode, modifiers) || this.input.canWrite() || super.keyPressed(keyCode, scanCode, modifiers);
  }
  public void getSuggestions(){
    String s = input.getText();

    String lookupname = s.contains(":") ? s : "minecraft:"+s;

    suggestions.clear();
    suggestions.addAll(StreamSupport.stream(ForgeRegistries.ITEMS.spliterator(),false)
            .map(ForgeRegistryEntry::getRegistryName)
            .map(ResourceLocation::toString)
    .filter(s1 -> s1.startsWith(lookupname)).collect(Collectors.toList()));



    if (suggestions.size() == 1){
      input.setText(suggestions.get(0));
    }
  }

  public void onClose() {
    super.onClose();
    this.minecraft.keyboardListener.enableRepeatEvents(false);
    this.container.removeListener(this);
  }


  /**
   * update the crafting window inventory with the items in the list
   */
  public void sendAllContents(Container containerToSend, NonNullList<ItemStack> itemsList) {
    this.sendSlotContents(containerToSend, 0, containerToSend.getSlot(0).getStack());
  }

  /**
   * Sends the contents of an inventory slot to the client-side Container. This doesn't have to match the actual
   * contents of that slot.
   */
  public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack) {
    if (slotInd == 0) {
      //this.input.setText(stack.isEmpty() ? "" : stack.getDisplayName().getString());
      this.input.setEnabled(true);
    }

  }

  /**
   * Sends two ints to the client-side Container. Used for furnace burning time, smelting progress, brewing progress,
   * and enchanting level. Normally the first int identifies which variable to update, and the second contains the new
   * value. Both are truncated to shorts in non-local SMP.
   */
  public void sendWindowProperty(Container containerIn, int varToUpdate, int newValue) {
  }

}
