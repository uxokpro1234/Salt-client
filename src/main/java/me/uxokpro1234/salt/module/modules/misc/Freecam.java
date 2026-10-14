package me.uxokpro1234.salt.module.modules.misc;

import me.uxokpro1234.salt.event.events.PacketEvent;
import me.uxokpro1234.salt.module.Module;
import me.uxokpro1234.salt.module.setting.Setting;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class Freecam extends Module {

    public Setting<Boolean> c03SpoofValue = register(new Setting<>("C03Spoof", true));
    public Setting<Boolean> flyValue = register(new Setting<>("Fly", true));
    public Setting<Boolean> noClipValue = register(new Setting<>("NoClip", true));
    public Setting<Float> speedValue = register(new Setting<>("Speed", 1.0F, 0.1F, 2.0F));

    private EntityOtherPlayerMP fakePlayer;
    private int fakePlayerId = -1;
    private int packetCount;

    private double motionX;
    private double motionY;
    private double motionZ;

    public Freecam() {
        super("Freecam", Category.MISC, Keyboard.KEY_NONE);
    }

    @Override
    public void onEnable() {
        if (mc.thePlayer == null || mc.theWorld == null) {
            return;
        }

        // Save the original motion so it can be restored later.
        motionX = mc.thePlayer.motionX;
        motionY = mc.thePlayer.motionY;
        motionZ = mc.thePlayer.motionZ;

        packetCount = 0;

        // Create a visual copy of the player at the starting position.
        fakePlayer = new EntityOtherPlayerMP(
                mc.theWorld,
                mc.thePlayer.getGameProfile()
        );

        fakePlayer.clonePlayer(mc.thePlayer, true);
        fakePlayer.copyLocationAndAnglesFrom(mc.thePlayer);
        fakePlayer.rotationYawHead = mc.thePlayer.rotationYawHead;

        // Find an unused entity ID for the fake player.
        for (int id = -1; id > -10000; id--) {
            if (mc.theWorld.getEntityByID(id) == null) {
                fakePlayerId = id;
                break;
            }
        }

        if (fakePlayerId == -1) {
            fakePlayer = null;
            return;
        }

        mc.theWorld.addEntityToWorld(fakePlayerId, fakePlayer);

        mc.thePlayer.noClip = noClipValue.getValue();
    }

    @Override
    public void onDisable() {
        if (mc.theWorld != null && fakePlayer != null) {
            mc.theWorld.removeEntityFromWorld(fakePlayerId);
        }

        fakePlayer = null;
        fakePlayerId = -1;

        if (mc.thePlayer != null) {
            // Restore the player's original movement.
            mc.thePlayer.motionX = motionX;
            mc.thePlayer.motionY = motionY;
            mc.thePlayer.motionZ = motionZ;

            mc.thePlayer.noClip = false;
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (!isEnabled() || event.phase != TickEvent.Phase.START) {
            return;
        }

        if (mc.thePlayer == null || mc.theWorld == null) {
            return;
        }

        mc.thePlayer.noClip = noClipValue.getValue();
        mc.thePlayer.fallDistance = 0.0F;

        if (!flyValue.getValue()) {
            return;
        }

        double speed = speedValue.getValue();

        // Reset velocity so keyboard input controls Freecam movement.
        mc.thePlayer.motionX = 0.0D;
        mc.thePlayer.motionY = 0.0D;
        mc.thePlayer.motionZ = 0.0D;

        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.thePlayer.motionY += speed;
        }

        if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.thePlayer.motionY -= speed;
        }

        // Calculate forward and sideways movement from the player's yaw.
        float forward = 0.0F;
        float strafe = 0.0F;

        if (mc.gameSettings.keyBindForward.isKeyDown()) forward++;
        if (mc.gameSettings.keyBindBack.isKeyDown()) forward--;
        if (mc.gameSettings.keyBindLeft.isKeyDown()) strafe++;
        if (mc.gameSettings.keyBindRight.isKeyDown()) strafe--;

        double yaw = Math.toRadians(mc.thePlayer.rotationYaw);
        double sin = Math.sin(yaw);
        double cos = Math.cos(yaw);

        double moveX = strafe * cos - forward * sin;
        double moveZ = forward * cos + strafe * sin;

        double length = Math.sqrt(moveX * moveX + moveZ * moveZ);

        if (length > 0.0D) {
            mc.thePlayer.motionX = moveX / length * speed;
            mc.thePlayer.motionZ = moveZ / length * speed;
        }
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        if (!isEnabled() || mc.thePlayer == null
                || mc.theWorld == null || fakePlayer == null) {
            return;
        }

        Object packet = event.getPacket();

        // Handle outgoing player movement packets.
        if (packet instanceof C03PacketPlayer) {
            if (!c03SpoofValue.getValue()) {
                event.setCanceled(true);
                return;
            }

            packetCount++;

            // Send a position update for the stationary fake player periodically.
            if (packetCount >= 20) {
                packetCount = 0;

                mc.getNetHandler().addToSendQueue(
                        new C03PacketPlayer.C06PacketPlayerPosLook(
                                fakePlayer.posX,
                                fakePlayer.posY,
                                fakePlayer.posZ,
                                fakePlayer.rotationYaw,
                                fakePlayer.rotationPitch,
                                fakePlayer.onGround
                        )
                );
            } else {
                mc.getNetHandler().addToSendQueue(
                        new C03PacketPlayer(fakePlayer.onGround)
                );
            }

            event.setCanceled(true);
            return;
        }

        // Handle server position corrections.
        if (packet instanceof S08PacketPlayerPosLook) {
            S08PacketPlayerPosLook correction =
                    (S08PacketPlayerPosLook) packet;

            fakePlayer.setPosition(
                    correction.getX(),
                    correction.getY(),
                    correction.getZ()
            );

            event.setCanceled(true);
        }
    }
}