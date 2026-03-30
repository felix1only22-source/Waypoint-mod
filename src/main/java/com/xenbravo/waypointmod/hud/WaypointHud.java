package com.xenbravo.waypointmod.hud;

import com.xenbravo.waypointmod.WaypointMod;
import com.xenbravo.waypointmod.waypoint.Waypoint;
import com.xenbravo.waypointmod.waypoint.WaypointManager;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.util.List;

public class WaypointHud {

    public static void register() {
        HudRenderCallback.EVENT.register((ctx, tickDelta) -> render(ctx));
    }

    private static void render(DrawContext ctx) {
        if (!WaypointMod.waypointsVisible) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;
        if (mc.options.debugEnabled) return;

        List<Waypoint> wps = WaypointManager.getInstance().getWaypoints(mc);
        if (wps.isEmpty()) return;

        TextRenderer tr  = mc.textRenderer;
        int W            = mc.getWindow().getScaledWidth();
        int H            = mc.getWindow().getScaledHeight();
        double px        = mc.player.getX();
        double py        = mc.player.getY();
        double pz        = mc.player.getZ();

        final int ENTRY_H  = 36;
        final int INNER_X  = 5;
        final int INNER_Y  = 4;
        final int ACCENT   = 3;
        final int MARGIN   = 6;
        final int GAP      = 3;

        int count  = wps.size();
        int totalH = count * ENTRY_H + (count - 1) * GAP;
        int startY = (H - totalH) / 2;

        for (int i = 0; i < count; i++) {
            Waypoint wp = wps.get(i);
            double dist = wp.distanceTo(px, py, pz);

            String nameLine  = "§e" + wp.getName();
            String coordLine = "§7" + (int)wp.getX() + ", " + (int)wp.getY() + ", " + (int)wp.getZ();
            String distLine  = "§f" + fmt(dist);

            int textW = Math.max(tr.getWidth(nameLine),
                        Math.max(tr.getWidth(coordLine), tr.getWidth(distLine)));

            int boxW     = ACCENT + INNER_X + textW + INNER_X;
            int boxRight = W - MARGIN;
            int boxLeft  = boxRight - boxW;
            int boxTop   = startY + i * (ENTRY_H + GAP);
            int boxBot   = boxTop + ENTRY_H;

            // Background
            ctx.fill(boxLeft, boxTop, boxRight, boxBot, 0xAA000000);
            // Orange accent bar
            ctx.fill(boxLeft, boxTop, boxLeft + ACCENT, boxBot, 0xFFFF9900);

            int textX = boxLeft + ACCENT + INNER_X;
            ctx.drawTextWithShadow(tr, nameLine,  textX, boxTop + INNER_Y,      0xFFFFFF);
            ctx.drawTextWithShadow(tr, coordLine, textX, boxTop + INNER_Y + 11, 0xFFFFFF);
            ctx.drawTextWithShadow(tr, distLine,  textX, boxTop + INNER_Y + 22, 0xFFFFFF);
        }

        // Bottom hint
        String hint = "§8[N] Add  [B] Toggle";
        ctx.drawTextWithShadow(tr, hint,
            W - tr.getWidth(hint) - MARGIN, H - 12, 0xFFFFFF);
    }

    private static String fmt(double d) {
        if (d >= 1000) return String.format("%.1fkm", d / 1000.0);
        return String.format("%.0fm", d);
    }
}
