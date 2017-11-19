package ocdiary.twitchy.registry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import ocdiary.twitchy.Twitchy;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


@Mod.EventBusSubscriber(Side.CLIENT)
public class TCDrawScreen {

    private static final ResourceLocation twitch = new ResourceLocation(Twitchy.MODID, "textures/gui/twitch.png");
    private static Minecraft mc = Minecraft.getMinecraft();

    private static Rectangle twitchRect = new Rectangle(Twitchy.posX , Twitchy.posY, 23, 23);
    private static String CHANNEL = Twitchy.twitchChannelId;
    private static int tsize = Twitchy.tIconSize;

    private static int textU;
    private static int textV;

    private static int textLU;
    private static int textLV;


    private static void drawTwitch()
    {
        if(tsize == 3){
            textU = 0;
            textV = 0;
        }else if(tsize == 2){
            textU = 0;
            textV = 38;
        }else if(tsize == 1){
            textU = -1;
            textV = 87;
        }else{
            textU = 0;
            textV = 0;
        }
        GlStateManager.color(1f, 1f, 1f, 1f);
        GlStateManager.enableBlend();
        mc.getTextureManager().bindTexture(twitch);
        GuiUtils.drawTexturedModalRect(twitchRect.x, twitchRect.y, textU, textV, twitchRect.width, twitchRect.height, 0);
    }

    private static void drawLive()
    {
        if(tsize == 3){
            textLU = 24;
            textLV = 0;
        }else if(tsize == 2){
            textLU = 25;
            textLV = 38;
        }else if(tsize == 1){
            textLU = 26;
            textLV = 87;
        }else{
            textU = 0;
            textV = 0;
        }
        GlStateManager.color(1f, 1f, 1f, 1f);
        GlStateManager.enableBlend();
        mc.getTextureManager().bindTexture(twitch);
        GuiUtils.drawTexturedModalRect(twitchRect.x, twitchRect.y, textLU, textLV, twitchRect.width, twitchRect.height, 0);
    }

    private static void drawTooltip(GuiScreen gui, int mouseX, int mouseY)
    {
        List<String> tooltip = new ArrayList<String>();
        if(Twitchy.isLive) {
            if (GuiScreen.isShiftKeyDown()) {
                tooltip.add("Stream Title:" + " " + "\u00A79" + Twitchy.streamTitle);
                tooltip.add("Current Game:" + " " + "\u00A72" + Twitchy.streamGame);
                tooltip.add("Current Viewers:" + " " + "\u00A74" + Twitchy.streamViewers);


            }else
                tooltip.add("Click to watch " + "\u00A76" + CHANNEL + " " + "\u00A7flive on Twitch!");

            if (!GuiScreen.isShiftKeyDown())
                tooltip.add(TextFormatting.AQUA + I18n.format("press.for.info.name", "SHIFT"));
        }else
            tooltip.add("\u00A76" + CHANNEL +"'s" + "\u00A7f offline. Click to go to their channel.");


        GuiUtils.drawHoveringText(tooltip, mouseX, mouseY + 20, gui.width, gui.height, -1, mc.fontRenderer);
    }

    @SubscribeEvent
    public static void drawScreen(GuiScreenEvent.DrawScreenEvent.Post event)
    {
        if(mc.player != null && Twitchy.persistantIcon) {
            RenderHelper.disableStandardItemLighting();
            if (Twitchy.isLive)
                drawLive();
                if (twitchRect.contains(event.getMouseX(), event.getMouseY()))
                    drawTooltip(event.getGui(), event.getMouseX(), event.getMouseY());
                drawTwitch();
                RenderHelper.enableStandardItemLighting();
            }else if(mc.player != null && !Twitchy.persistantIcon && Twitchy.isLive) {
                drawLive();
            if (twitchRect.contains(event.getMouseX(), event.getMouseY()))
                drawTooltip(event.getGui(), event.getMouseX(), event.getMouseY());
            drawTwitch();
            RenderHelper.enableStandardItemLighting();
        }
    }

    @SubscribeEvent
    public static void drawOverlay(RenderGameOverlayEvent.Post event)
    {
        if(event.getType() == RenderGameOverlayEvent.ElementType.ALL && mc.currentScreen == null && Twitchy.persistantIcon) {
            if (Twitchy.isLive)
                drawLive();
            drawTwitch();
        }else if(event.getType() == RenderGameOverlayEvent.ElementType.ALL && mc.currentScreen == null && !Twitchy.persistantIcon && Twitchy.isLive){
            drawLive();
            drawTwitch();
        }
    }

    @SubscribeEvent
    public static void mouseClick(GuiScreenEvent.MouseInputEvent.Pre event)
    {
        if(mc.player != null && Mouse.getEventButton() == 0 && Mouse.getEventButtonState()) {
            //Get mouse position
            ScaledResolution sr = new ScaledResolution(mc);
            int srHeight = sr.getScaledHeight();
            int mouseX = Mouse.getX() * sr.getScaledWidth() / mc.displayWidth;
            int mouseY = srHeight - Mouse.getY() * srHeight / mc.displayHeight - 1;

            if (twitchRect.contains(mouseX, mouseY)) {
                openMCForumsThread();
            }
        }
    }

    public static String openMCForumsThread() {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop()
                        .browse(new URI(
                                "https://www.twitch.tv/" + Twitchy.twitchChannelId));
            } catch (IOException e) {
                e.printStackTrace();
                return "Can't open browser for some reason";
            } catch (URISyntaxException e) {
                e.printStackTrace();
                return "Bug in Mod: URL is invalid.";
            }
        } else {
            return "Can't open browser for some reason";
        }
        return null;
    }
}
